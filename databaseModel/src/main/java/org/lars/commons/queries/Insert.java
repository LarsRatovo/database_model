package org.lars.commons.queries;

import org.lars.commons.queries.creator.CreatorException;
import org.lars.commons.queries.creator.annotations.Column;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class Insert<M> extends Select<M> {
    public void insert() throws SQLException, IOException, ClassNotFoundException, IllegalAccessException {
        try (Connection connection=getConnection()){
            this.insert(connection);
        }
    }
    private void insert(Connection connection) throws SQLException, IllegalAccessException, IOException, ClassNotFoundException {
        try (PreparedStatement statement=connection.prepareStatement(generateStringSql(false))){
            int variable=1;
            for (Field field : fields) {
                Column column = field.getAnnotation(Column.class);
                if (column.autogen()) {
                    if (column.autogenMode() == Query.generator) {
                        String seqname = column.generator();
                        if (seqname.isBlank()) {
                            if (column.value().isBlank()) {
                                seqname = this.tablename + "_" + field.getName() + "_seq";
                            } else {
                                seqname = this.tablename + "_" + column.value() + "_seq";
                            }
                        }
                        statement.setString(variable,seqname);
                        variable++;
                    }
                } else {
                    field.setAccessible(true);
                    statement.setObject(variable, field.get(this));
                    field.setAccessible(false);
                    variable++;
                }
            }
            statement.execute();
        }
    }
    public void insertReturning(boolean deep) throws SQLException, IllegalAccessException, IOException, ClassNotFoundException, CreatorException {
        try (Connection connection=getConnection()){
            try (PreparedStatement statement=connection.prepareStatement(generateStringSql(true))){
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
                if(deep){
                    deepInsert(connection);
                }
            }
        }
    }
    private String getSeqName(Field field,Column column){
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
    private String generateStringSql(boolean returning){
        init();
        initFields();
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
            if(column.autogen()){
                if(!returning){
                    columnsBuilder.append(",NEXTVAL(?)");
                }else {
                    columnsBuilder.append(",?");
                }
            }else{
                columnsBuilder.append(",?");
            }
        }
        columnsBuilder.deleteCharAt(0);
        sqlBuilder.append(columnsBuilder);
        sqlBuilder.append(")");
        return sqlBuilder.toString();
    }
    private void deepInsert(Connection connection) throws CreatorException, IllegalAccessException, SQLException, IOException, ClassNotFoundException {
        for(Joinnable j:this.joins(this.getClass())){
            if(j.type==Query.manyToMany){
                List<?> deeps= (List<?>) j.f.get(this);
                for(Object o:deeps){
                    List<Field> miniFields=this.initFieldsof(j.foreignType);
                    for(Field f:miniFields){
                        Column col=f.getAnnotation(Column.class);
                        if(col!=null){
                            String name=col.value();
                            if(name.isBlank()){
                                name=f.getName();
                            }
                            if(name.equalsIgnoreCase(j.foreignkey)){
                                f.setAccessible(true);
                                for(Field fm:this.fields){
                                    Column colm=fm.getAnnotation(Column.class);
                                    String namem=colm.value();
                                    if(namem.isBlank()){
                                        namem=fm.getName();
                                    }
                                    if(namem.equalsIgnoreCase(j.localkey)){
                                        fm.setAccessible(true);
                                        f.set(o,fm.get(this));
                                        break;
                                    }
                                }
                                f.setAccessible(false);
                                ((Insert)o).insert(connection);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}
