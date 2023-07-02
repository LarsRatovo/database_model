package org.lars.commons.queries.builders;
import org.lars.commons.queries.KeyValue;
import org.lars.commons.queries.Query;
import org.lars.commons.queries.creator.annotations.Column;

import java.lang.reflect.Field;
import java.util.List;

public class InsertBuilder {
    public String build(List<Field> fields, String tableName, List<KeyValue> keyValues,Object object) throws IllegalAccessException {
        StringBuilder columnBuilder=new StringBuilder();
        StringBuilder sqlBuilder=new StringBuilder();
        StringBuilder valueBuilder=new StringBuilder();
        int id=1;
        for (Field field:fields){
            field.setAccessible(true);
            Column column=field.getAnnotation(Column.class);
            if(column.autogenMode()!=Query.self){
                columnBuilder.append(",");
                if(column.value().isBlank()){
                    columnBuilder.append(field.getName().toLowerCase());
                }else{
                    columnBuilder.append(column.value().toLowerCase().replace(" ",""));
                }
                KeyValue keyValue=new KeyValue();
                keyValue.setId(id);
                if(column.autogenMode()==Query.generator){
                    if(column.generator().isBlank()){
                        if(column.value().isBlank()){
                            keyValue.setValue(tableName.toLowerCase()+"_"+field.getName().toLowerCase()+"_seq");
                        }else {
                            keyValue.setValue(tableName.toLowerCase()+"_"+column.value()+"_seq");
                        }
                    }else {
                        keyValue.setValue(column.generator());
                    }
                    valueBuilder.append(",").append("nextval(?)");
                }else {
                    keyValue.setValue(field.get(object));
                    valueBuilder.append(",").append("?");
                }
                keyValues.add(keyValue);
                id++;
            }
        }
        columnBuilder.deleteCharAt(0);
        valueBuilder.deleteCharAt(0);
        sqlBuilder.append("INSERT INTO ")
                .append(tableName)
                .append("(")
                .append(columnBuilder)
                .append(") VALUES (")
                .append(valueBuilder)
                .append(")");
        return sqlBuilder.toString();
    }
}
