package org.lars.commons.queries.creator;

import org.lars.Person;
import org.lars.commons.queries.*;
import org.lars.commons.queries.creator.annotations.Column;
import org.lars.commons.queries.creator.annotations.Join;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Creator<M> {
    public M createOne(QueryResult rs,Class model,Connection connection) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, CreatorException {
        rs.getRs().next();
        return create(rs,model,connection);
    }
    public List<M> createMany(QueryResult rs,Class model,Connection connection) throws SQLException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, CreatorException {
        ArrayList<M> results=new ArrayList<>();
        while (rs.getRs().next()){
            results.add(create(rs,model,connection));
        }
        return results;
    }
    private M create(QueryResult queryResult,Class model, Connection connection) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, CreatorException, SQLException {
        M result= (M) modulize(model,queryResult.getRs(),"r");
        if(queryResult.isDeep()){
            for(Joinnable joinnable:queryResult.getJoinnables()){
                Join join=joinnable.getF().getAnnotation(Join.class);
                if(joinnable.getType()==Query.oneToOne){
                    joinnable.getF().set(result,modulize(join.classModel(),queryResult.getRs(), joinnable.getAlias()));
                }else if(joinnable.getType()==Query.oneToMany){
                    Select select=new Select();
                    select.select(join.table(),join.deep(),join.classModel());
                    select.equals(join.foreignKey(),queryResult.getRs().getObject("r_"+join.localKey()));
                    joinnable.getF().set(result,this.createMany(select.execute(connection),join.classModel(),connection));
                }
            }
        }
        return result;
    }
    private Object modulize(Class classModel, ResultSet rs, String alias) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        Constructor constructor=classModel.getConstructor(null);
        Object result=constructor.newInstance(null);
        ArrayList<Field> myFields=this.getFieldsOf(classModel);
        for(Field field:myFields){
            if(field.isAnnotationPresent(Column.class)){
                Column column=field.getAnnotation(Column.class);
                try {
                    Object value=null;
                    if(!column.value().isBlank()){
                        value=rs.getObject(alias+"_"+column.value());
                    }else{
                        value=rs.getObject(alias+"_"+field.getName());
                    }
                    if(value!=null){
                        field.setAccessible(true);
                        field.set(result,value);
                    }
                }catch (Exception sqlException){
//                    sqlException.printStackTrace();
                }
            }
        }
        return result;
    }
    protected ArrayList<Field> getFieldsOf(Class checkingClass){
        Class checking=checkingClass;
        ArrayList<Field> myFields=new ArrayList<>();
        while (checking!=null&&checking!= View.class&&checking!= Entity.class){
            myFields.addAll(Arrays.asList(checking.getDeclaredFields()));
            checking=checking.getSuperclass();
        }
        return myFields;
    }
}