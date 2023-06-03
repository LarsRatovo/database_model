package org.lars.commons.queries;

import org.lars.commons.queries.creator.CreatorException;
import org.lars.commons.queries.creator.annotations.Column;
import org.lars.commons.queries.creator.annotations.Key;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Update<M> extends Insert<M> {
    public void update(boolean deep) throws IllegalAccessException, IOException, ClassNotFoundException, SQLException, CreatorException {
        init();
        initFields();
            try(Connection connection=getConnection()){
                connection.setAutoCommit(false);
                connection.beginRequest();
                try {
                    ArrayList<Object> objects=new ArrayList<>();
                    ArrayList<Object> keys=new ArrayList<>();
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
                    try (PreparedStatement statement=connection.prepareStatement(builder.toString())){
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
                    if(deep){
                        updateChildren(connection);
                    }
                    connection.commit();
                }catch (SQLException sqlException){
                    connection.rollback();
                    throw sqlException;
                }
            }
    }
    private void updateChildren(Connection connection) throws CreatorException, SQLException, IllegalAccessException, IOException, ClassNotFoundException {
        ArrayList<Joinnable> joinnables=this.joins(this.getClass());
        for(Joinnable joinnable:joinnables){
            if(joinnable.type==Query.manyToMany){
                String deleteBuilder = "DELETE FROM " +
                        joinnable.tablename +
                        " WHERE " +
                        joinnable.foreignkey +
                        "=?";
                try (PreparedStatement preparedStatement= connection.prepareStatement(deleteBuilder)){
                    for(Field field:this.fields){
                        Column column=field.getAnnotation(Column.class);
                        String name=column.value();
                        if(name.isBlank()){
                            name=field.getName();
                        }
                        if(name.equals(joinnable.localkey)){
                            field.setAccessible(true);
                            preparedStatement.setObject(1,field.get(this));
                            field.setAccessible(false);
                            break;
                        }
                    }
                    preparedStatement.executeUpdate();
                }
                for(Object data:((List<?>)joinnable.f.get(this))){
                    ((Insert)data).insert();
                }
            }
        }
    }
}
