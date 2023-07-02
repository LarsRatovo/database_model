package org.lars.commons.queries;

import org.lars.commons.queries.builders.InsertBuilder;
import org.lars.commons.queries.creator.CreatorException;
import org.lars.commons.queries.creator.annotations.Column;
import org.lars.commons.queries.creator.annotations.Extension;
import org.lars.commons.queries.executor.QueryExecutor;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Insert<M> extends Select<M> {
    public void insert() throws DatabaseModelException, SQLException {
        init();
        InsertBuilder builder=new InsertBuilder();
        QueryExecutor queryExecutor=new QueryExecutor();
        Connection connection=queryExecutor.getConnection();
        try {
            connection.setAutoCommit(false);
            for (Field field:initFieldsof(classModel)){
                Column column=field.getAnnotation(Column.class);
                if(column.autogenMode()==Query.generatedValue){
                    field.setAccessible(true);
                    Object object;
                    if(column.generator().isBlank()){
                        String generator=tablename.toLowerCase()+"_";
                        if(column.value().isBlank()){
                            generator+=field.getName().toLowerCase();
                        }else {
                            generator+=column.value().toLowerCase().replace(" ","");
                        }
                        generator+="_seq";
                        object=queryExecutor.getGeneratedValue(generator,field.getType(),connection);
                    }else{
                        object=queryExecutor.getGeneratedValue(column.generator(),field.getType(),connection);
                    }
                    field.set(this,object);
                }
            }
            List<KeyValue> keyValues=new ArrayList<>();
            queryExecutor.executeUpdate(builder.build(initFieldsof(getClass()),tablename,keyValues,this),keyValues,connection);
            connection.commit();
            connection= queryExecutor.getConnection();
            connection.setAutoCommit(false);
            for(Stretch stretch:getStretches(classModel)){
                if(stretch.isCascade()){
                    if(stretch.getExtensionType()==Query.one){
                        Object child=stretch.field.get(this);
                        if(child!=null){
                            setFor(stretch.getForeignKey(),child,getFor(stretch.getLocalKey(),this,classModel),stretch.getExtensionObjectType());
                            ((Insert<?>)child).insert();
                        }
                    }else {
                        saveListChildren(stretch);
                    }
                }
            }
            connection.commit();
        }catch (SQLException e){
            connection.rollback();
            throw e;
        } catch (IllegalAccessException e) {
            throw new DatabaseModelException("Can't set generated value");
        } finally {
            connection.close();
        }
    }
    private void setFor(String column,Object object,Object value,Class<?> type) throws IllegalAccessException {
        for (Field field:initFieldsof(type)){
            Column col=field.getAnnotation(Column.class);
            String match="";
            if(col.value().isBlank()){
                match=field.getName();
            }else {
                match=col.value();
            }
            if(match.equalsIgnoreCase(column)){
                field.setAccessible(true);
                field.set(object,value);
                break;
            }
        }
    }
    private Object getFor(String column,Object object,Class<?> type) throws IllegalAccessException{
        for (Field field:initFieldsof(type)){
            Column col=field.getAnnotation(Column.class);
            String match="";
            if(col.value().isBlank()){
                match=field.getName();
            }else {
                match=col.value();
            }
            if(match.equalsIgnoreCase(column)){
                field.setAccessible(true);
                return field.get(object);
            }
        }
        return null;
    }
    protected void saveListChildren(Stretch stretch) throws IllegalAccessException, SQLException, DatabaseModelException {
        List<?> children=(List<?>)stretch.field.get(this);
        if(children!=null){
            for(Object child:children){
                setFor(stretch.getForeignKey(),child,getFor(stretch.getLocalKey(),this,classModel),stretch.getExtensionObjectType());
                ((Insert<?>)child).insert();
            }
        }
    }
}
