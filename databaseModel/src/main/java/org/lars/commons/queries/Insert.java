package org.lars.commons.queries;

import org.lars.commons.queries.creator.annotations.Column;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class Insert<M> extends Select<M> {
    private ArrayList<Field> fields;
    public void insert(String tablename,Class<M> model){
        super.select(tablename,model,null);
        Class checking=model;
        fields=new ArrayList<>();
        while (checking!=null&&checking!= Select.class&&checking!= Insert.class){
            for(Field field:checking.getDeclaredFields()){
                if(field.isAnnotationPresent(Column.class)){
                    fields.add(field);
                }
            }
            checking=checking.getSuperclass();
        }
    }
    protected void executeInsert(Connection connection) throws SQLException, IllegalAccessException {
        StringBuilder sqlBuilder=new StringBuilder();
        sqlBuilder.append("INSERT INTO ");
        sqlBuilder.append(this.tablename);
        sqlBuilder.append("(");
        StringBuilder columnsBuilder=new StringBuilder();
        for(Field f:fields){
            Column column=f.getAnnotation(Column.class);
            if(!column.autogen()||column.autogenMode()==Query.generator){
                String columnName=null;
                if(column.value().isBlank()){
                    columnName=f.getName();
                }else{
                    columnName=column.value();
                }
                columnsBuilder.append(",");
                columnsBuilder.append(columnName);
            }
        }
        columnsBuilder.deleteCharAt(0);
        sqlBuilder.append(columnsBuilder);
        sqlBuilder.append(") VALUES (");
        columnsBuilder=new StringBuilder();
        for(Field f:fields){
            Column column=f.getAnnotation(Column.class);
            if(!column.autogen()||column.autogenMode()==Query.generator){
                columnsBuilder.append(",?");
            }
        }
        columnsBuilder.deleteCharAt(0);
        sqlBuilder.append(columnsBuilder);
        sqlBuilder.append(")");
        try (PreparedStatement statement=connection.prepareStatement(sqlBuilder.toString())){
            int variable=1;
            for (int i = 0; i < fields.size(); i++) {
                Column column=fields.get(i).getAnnotation(Column.class);
                if(column.autogen()){
                    if(column.autogenMode()==Query.generator){
                        String seqname=column.generator();
                        if(seqname.isBlank()){
                            if(column.value().isBlank()){
                                seqname=this.tablename+"_"+fields.get(i).getName()+"_seq";
                            }else {
                                seqname=this.tablename+"_"+column.value()+"_seq";
                            }
                        }
                        statement.setString(i+1,"NEXTVAL('"+seqname+"')");
                    }
                }else{
                    fields.get(i).setAccessible(true);
                    statement.setObject(variable,fields.get(i).get(this));
                    fields.get(i).setAccessible(false);
                    variable++;
                }
            }
            statement.execute();
        }
    }
    protected void executeInsertReturning(Connection connection) throws SQLException, IllegalAccessException{
        StringBuilder sqlBuilder=new StringBuilder();
        sqlBuilder.append("INSERT INTO ");
        sqlBuilder.append(this.tablename);
        sqlBuilder.append("(");
        StringBuilder columnsBuilder=new StringBuilder();
        for(Field f:fields){
            Column column=f.getAnnotation(Column.class);
            if(column.autogenMode()!=Query.self){
                columnsBuilder.append(getSeqName(f,column));
            }
        }
        columnsBuilder.deleteCharAt(0);
        sqlBuilder.append(columnsBuilder);
        sqlBuilder.append(") VALUES (");
        columnsBuilder=new StringBuilder();
        for(Field f:fields){
            Column column=f.getAnnotation(Column.class);
            if(column.autogenMode()!=Query.self){
                columnsBuilder.append(",?");
            }
        }
        columnsBuilder.deleteCharAt(0);
        sqlBuilder.append(columnsBuilder);
        sqlBuilder.append(")");
        try (PreparedStatement statement=connection.prepareStatement(sqlBuilder.toString())){
            int variable=1;
            for (Field field : fields) {
                field.setAccessible(true);
                Column column = field.getAnnotation(Column.class);
                if (column.autogen()) {
                    if (column.autogenMode() == Query.generator) {
                        Object generated =this.getGeneratedValue(getSeqName(field,column),connection,field.getType());
                        field.set(this, generated);
                        statement.setObject(variable, generated);
                        variable++;
                    }
                } else {
                    statement.setObject(variable, field.get(this));
                    variable++;
                }
                field.setAccessible(false);
            }
            statement.execute();
        }
    }
    private String getSeqName(Field field,Column column) throws SQLException {
        String seqname = column.generator();
        if (seqname.isBlank()) {
            if (column.value().isBlank()) {
                seqname = this.tablename + "_" + field.getName() + "_seq";
            } else {
                seqname = this.tablename + "_" + column.value() + "_seq";
            }
        }
        return seqname;
    }
}
