package org.lars.commons.queries;

import org.lars.commons.queries.creator.CreatorException;
import org.lars.commons.queries.creator.annotations.Column;
import org.lars.commons.queries.creator.annotations.Key;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class Update<M> extends Insert<M> {
    public void update(String tablename,Class<M> model) throws CreatorException {
        insert(tablename,model);
    }
    public void executeUpdate(Connection connection) throws IllegalAccessException, SQLException {
        ArrayList objects=new ArrayList();
        ArrayList keys=new ArrayList();
        StringBuilder builder=new StringBuilder();
        builder.append("UPDATE ")
                .append(this.tablename)
                .append(" SET ");
        StringBuilder columnsBuilder=new StringBuilder();
        StringBuilder keysBuilder=new StringBuilder();
        for(Field f:this.fields){
            f.setAccessible(true);
            Column column=f.getAnnotation(Column.class);
            if(f.isAnnotationPresent(Key.class)){
                if(f.get(this)!=null){
                    keys.add(f.get(this));
                    keysBuilder.append(" AND ");
                    if(column.value().isBlank()){
                        keysBuilder.append(f.getName());
                    }else{
                        keysBuilder.append(column.value());
                    }
                    keysBuilder.append("=?");
                }
            }else{
                if(f.get(this)!=null){
                    objects.add(f.get(this));
                    columnsBuilder.append(",");
                    if(column.value().isBlank()){
                        columnsBuilder.append(f.getName());
                    }else{
                        columnsBuilder.append(column.value());
                    }
                    columnsBuilder.append("=?");
                }
            }
            f.setAccessible(false);
        }
        columnsBuilder.deleteCharAt(0);
        keysBuilder.delete(0,4);
        builder.append(columnsBuilder);
        if(keys.size()>0){
            builder.append(" WHERE ")
                    .append(keysBuilder);
        }
        System.out.println(builder);
        PreparedStatement statement=connection.prepareStatement(builder.toString());
        int index=1;
        for (Object value:objects) {
            statement.setObject(index,value);
            index++;
        }
        for(Object key:keys){
            statement.setObject(index,key);
        }
        statement.executeUpdate();
    }
}
