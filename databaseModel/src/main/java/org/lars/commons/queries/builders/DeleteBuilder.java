package org.lars.commons.queries.builders;

import org.lars.commons.queries.DatabaseModelException;
import org.lars.commons.queries.KeyValue;
import org.lars.commons.queries.creator.annotations.Column;
import org.lars.commons.queries.creator.annotations.Linked;

import java.lang.reflect.Field;
import java.util.List;

public class DeleteBuilder {
    public String build(Object o, List<Field> fields, List<KeyValue> keyValues) throws DatabaseModelException{
        Class<?> model=o.getClass();
        String tableName=model.getAnnotation(Linked.class).value();
        StringBuilder whereBuilder=new StringBuilder();
        StringBuilder sqlBuilder=new StringBuilder();
        int id=1;
        for (Field field:fields){
            Column column=field.getAnnotation(Column.class);
            field.setAccessible(true);
            try {
                if(field.get(o)!=null){
                    whereBuilder.append(" AND ");
                    KeyValue keyValue=new KeyValue();
                    if(column.value().isBlank()){
                        whereBuilder.append(field.getName().toLowerCase());
                    }else {
                        whereBuilder.append(column.value().toLowerCase().replace(" ",""));
                    }
                    whereBuilder.append(" = ? ");
                    keyValue.setId(id);
                    keyValue.setValue(field.get(o));
                    keyValues.add(keyValue);
                    id++;
                }
            }catch (IllegalAccessException e){
                throw new DatabaseModelException(e.getMessage());
            }
        }
        whereBuilder.delete(0,4);
        sqlBuilder.append("DELETE FROM ").append(tableName).append(" WHERE ").append(whereBuilder);
        return sqlBuilder.toString();
    }
    public String buildDeleteChildren(Object o,List<Field> fields,String localKey,String foreignKey,String childTable,KeyValue keyValue) throws DatabaseModelException {
        StringBuilder whereBuilder=new StringBuilder();
        StringBuilder sqlBuilder=new StringBuilder();
        for (Field field:fields){
            Column column=field.getAnnotation(Column.class);
            field.setAccessible(true);
            try {
                if(field.get(o)!=null){
                    String match;
                    if(column.value().isBlank()){
                        match=field.getName().toLowerCase().toLowerCase();
                    }else {
                        match=column.value().toLowerCase().replace(" ","");
                    }
                    if(match.equalsIgnoreCase(localKey)){
                        keyValue.setId(1);
                        keyValue.setValue(field.get(o));
                        whereBuilder.append(foreignKey)
                        .append(" = ? ");
                        break;
                    }
                }
            }catch (IllegalAccessException e){
                throw new DatabaseModelException(e.getMessage());
            }
        }
        sqlBuilder.append("DELETE FROM ").append(childTable).append(" WHERE ").append(whereBuilder);
        return sqlBuilder.toString();
    }
}
