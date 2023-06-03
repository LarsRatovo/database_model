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
    public M createOne(QueryResult rs,Class model,Connection connection) throws CreatorException {
        try {
            rs.getRs().next();
        }catch (SQLException e){
            throw new CreatorException("ResultSet is closed or no row in the resultSet",e);
        }
        return create(rs,model,connection);
    }
    public List<M> createMany(QueryResult rs,Class model,Connection connection) throws CreatorException {
        ArrayList<M> results=new ArrayList<>();
        try {
            while (rs.getRs().next()){
                results.add(create(rs,model,connection));
            }
            return results;
        }catch (SQLException sqlException){
            throw new CreatorException("Cannot access the resultset, maybe closed",sqlException);
        }
    }
    private M create(QueryResult queryResult,Class model, Connection connection) throws  CreatorException {
        M result= (M) modulize(model,queryResult.getRs(),"r");
        try {
            if(queryResult.isDeep()){
                for(Joinnable joinnable:queryResult.getJoinnables()){
                    Join join=joinnable.getF().getAnnotation(Join.class);
                    if(joinnable.getType()==Query.oneToOne){
                        joinnable.getF().set(result,modulize(join.classModel(),queryResult.getRs(), joinnable.getAlias()));
                    }else{
                        Select select=new Select();
                        select.select(join.table(),join.deep(),join.classModel());
                        try {
                            select.equals(join.foreignKey(),queryResult.getRs().getObject("r_"+join.localKey()));
                        }catch (SQLException e){
                            throw new CreatorException("There is no "+join.localKey()+" in the resultset",e);
                        }
                        try {
                            joinnable.getF().set(result,this.createMany(select.execute(connection),join.classModel(),connection));
                        }catch (SQLException e){
                            throw new CreatorException("Cannot execute the query check the database's server ",e);
                        }
                    }
                }
            }
            return result;
        }catch (IllegalAccessException exception){
            throw new CreatorException("Cannot access the field , maybe setAccessible is false",exception);
        }
    }
    private Object modulize(Class classModel, ResultSet rs, String alias) throws CreatorException {
        try {
            Object result=classModel.getConstructor().newInstance();
            ArrayList<Field> myFields=this.getFieldsOf(classModel);
            for(Field field:myFields){
                if(field.isAnnotationPresent(Column.class)){
                    Column column=field.getAnnotation(Column.class);
                    try {
                        Object value;
                        if(!column.value().isBlank()){
                            value=rs.getObject(alias+"_"+column.value());
                        }else{
                            value=rs.getObject(alias+"_"+field.getName());
                        }
                        if(value!=null){
                            field.setAccessible(true);
                            field.set(result,value);
                        }
                    }catch (SQLException sqlException){
                        if(column.value().isBlank()){
                            System.err.println("The name "+field.getName()+" is not found in the resultSet");
                        }else {
                            System.err.println("The name "+column.value()+" is not found in the resultSet");
                        }
                    }
                }
            }
            return result;
        }catch (InvocationTargetException| InstantiationException|IllegalAccessException|NoSuchMethodException exception){
            throw new CreatorException("Cannot invoke the constructor or cannot instantiate the object",exception);
        }
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