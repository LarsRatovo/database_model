package org.lars.commons.queries.creator;

import org.lars.commons.queries.Select;
import org.lars.commons.queries.creator.annotations.Column;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Creator<M> {
    Class<M> model;
    ArrayList<Field> fields;
    Constructor<M> constructor;
    public Creator(Class<M> model) throws NoSuchMethodException {
        this.model=model;
        Class checking=model;
        fields=new ArrayList<>();
        while (checking!=null&&checking!= Select.class){
            for(Field field:checking.getDeclaredFields()){
                if(field.isAnnotationPresent(Column.class)){
                    fields.add(field);
                }
            }
            checking=checking.getSuperclass();
        }
        constructor=model.getConstructor(null);
    }
    public M createOne(ResultSet rs) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        rs.next();
        return create(rs);
    }
    public List<M> createMany(ResultSet rs) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        ArrayList<M> results=new ArrayList<>();
        while (rs.next()){
            results.add(create(rs));
        }
        return results;
    }
    private M create(ResultSet rs) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        M result=constructor.newInstance(null);
        for(Field field:fields){
            Column column=field.getAnnotation(Column.class);
            try {
                Object value=null;
                if(!column.value().isBlank()){
                    value=rs.getObject(column.value());
                }else{
                    value=rs.getObject(field.getName());
                }
                if(value!=null){
                    field.setAccessible(true);
                    field.set(result,value);
                }
            }catch (SQLException sqlException){
                /*nothing to do*/
            }
        }
        return result;
    }
}