package org.lars.commons.queries;

import org.lars.commons.queries.creator.annotations.Column;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class Delete<M> extends Update<M> {
    public void delete() throws IllegalAccessException, SQLException, IOException, ClassNotFoundException {
        init();
        initFields();
        StringBuilder builder=new StringBuilder();
        builder.append("DELETE FROM ")
                .append(this.tablename);
        ArrayList<Object> values=new ArrayList<>();
        StringBuilder columnsBuilder=new StringBuilder();
        for(Field field:fields){
            Column column=field.getAnnotation(Column.class);
            field.setAccessible(true);
            Object object=field.get(this);
            if(object!=null){
                values.add(object);
                if(column.value().isBlank()){
                    columnsBuilder.append(" AND ")
                            .append(field.getName())
                            .append("=?");
                }else {
                    columnsBuilder.append(" AND ")
                            .append(column.value())
                            .append("=?");
                }
            }
            field.setAccessible(false);
        }
        if(columnsBuilder.length()>0){
            columnsBuilder.delete(0,4);
            builder.append(" WHERE ");
            builder.append(columnsBuilder);
        }
        try (Connection connection=getConnection()){
            PreparedStatement statement=connection.prepareStatement(builder.toString());
            for (int i = 0; i < values.size(); i++) {
                statement.setObject(i+1,values.get(i));
            }
            statement.executeUpdate();
        }
        System.out.println(builder);
    }
}
