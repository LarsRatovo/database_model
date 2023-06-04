package org.lars.commons.queries;

import org.lars.commons.queries.creator.CreatorException;
import org.lars.commons.queries.creator.annotations.Column;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Delete<M> extends Update<M> {
    public void delete() throws CreatorException{
        try {
            Connection connection=getConnection();
            connection.setAutoCommit(false);
            try (connection){
                deleteWithChildren(this,connection);
                connection.commit();
            }catch (CreatorException e){
                connection.rollback();
                throw e;
            }
        }catch (SQLException | IOException | ClassNotFoundException e){
            throw new CreatorException("Can't open the connection : "+e.getMessage(),e);
        }
    }
    void delete(Connection connection) throws CreatorException{
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
            Object object= null;
            try {
                object = field.get(this);
            } catch (IllegalAccessException e) {
                throw new CreatorException("Can't access the field :"+e.getMessage(),e);
            }
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
        try{
            PreparedStatement statement=connection.prepareStatement(builder.toString());
            for (int i = 0; i < values.size(); i++) {
                statement.setObject(i+1,values.get(i));
            }
            statement.executeUpdate();
        }catch (SQLException e){
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new CreatorException("Error occurred but can't rollback ",e);
            }
            throw new CreatorException("Can't delete the row",e);
        }
    }
    private void deleteWithChildren(Delete local,Connection connection) throws CreatorException{
        ArrayList<Joinnable> joinnables=joins(local.getClass());
        if(joinnables.size()>0){
            for(Joinnable joinnable:joinnables){
                if(joinnable.dropsOnDelete){
                    try {
                        if(joinnable.type==Query.oneToOne){
                            deleteWithChildren((Delete) joinnable.f.get(local),connection);
                        }else{
                            for(Object delete:(List)joinnable.f.get(local)){
                                deleteWithChildren((Delete) delete,connection);
                            }
                        }
                    } catch (IllegalAccessException e) {
                        throw new CreatorException("Can't access to the field: "+e.getMessage(),e);
                    }
                }
            }
        }
        local.delete(connection);
    }
}
