package org.lars.commons.queries.builders;

import org.lars.commons.queries.KeyValue;
import org.lars.commons.queries.creator.annotations.Column;
import org.lars.commons.queries.creator.annotations.Key;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class UpdateBuilder {
    public String build(Object o, Class classModel, List<Field> fields, String tableName, ArrayList<KeyValue> keyValues) throws IllegalAccessException {
        StringBuilder columnBuilder=new StringBuilder();
        StringBuilder keyBuilder=new StringBuilder();
        StringBuilder sqlBuilder=new StringBuilder();
        int id=1;
        KeyValue key=null;
        for(Field field:fields){
            Column column=field.getAnnotation(Column.class);
            field.setAccessible(true);
            Object value=field.get(o);
            if(value!=null){
                String columName=field.getName();
                if(!column.value().isBlank()){
                    columName=column.value().toLowerCase().replace(" ","");
                }
                if(field.isAnnotationPresent(Key.class)){
                    key=new KeyValue();
                    key.setValue(value);
                    keyBuilder.append(" WHERE ").append(columName).append("=?");
                }else {
                    columnBuilder.append(",").append(columName).append("=?");
                    KeyValue keyValue=new KeyValue();
                    keyValue.setId(id);
                    keyValue.setValue(value);
                    keyValues.add(keyValue);
                    id++;
                }
            }
        }
        columnBuilder.deleteCharAt(0);
        if(key!=null){
            key.setId(id);
            keyValues.add(key);
        }
        sqlBuilder.append("UPDATE ").append(tableName).append(" SET ").append(columnBuilder).append(keyBuilder);
        return sqlBuilder.toString();
    }
}
