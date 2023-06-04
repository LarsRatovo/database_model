package org.lars.commons.queries;

import org.lars.commons.queries.creator.CreatorException;
import org.lars.commons.queries.creator.annotations.Column;
import org.lars.commons.queries.creator.annotations.Join;
import org.lars.commons.queries.creator.annotations.Linked;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

public class Query<M> {
    public static final int desc=1;
    public static final int asc=0;
    public static final int oneToOne=0;
    public static final int oneToMany=1;
    public static final int manyToMany=2;
    public static final int generator=1;
    public static final int self=2;
    protected Class<M> classModel;
    static final int none=0;
    protected ArrayList<Field> fields;

    protected String tablename;
    protected ArrayList<String> getColumns(Class checkingClass){
        Class checking=checkingClass;
        ArrayList<String> cols=new ArrayList<>();
        while (checking!=null&&checking!= Entity.class&&checking!=View.class){
            for(Field field:checking.getDeclaredFields()){
                if(field.isAnnotationPresent(Column.class)){
                    Column columnAnnotation=field.getAnnotation(Column.class);
                    if(columnAnnotation.value().isBlank()){
                        cols.add(field.getName());
                    }else{
                        cols.add(columnAnnotation.value());
                    }
                }
            }
            checking=checking.getSuperclass();
        }
        return cols;
    }
    protected ArrayList<Joinnable> joins(Class checkingClass) throws CreatorException {
        Class checking=checkingClass;
        ArrayList<Joinnable> joinnables=new ArrayList<>();
        while (checking!=null&&checking!= Entity.class&&checking!= View.class){
            for(Field field:checking.getDeclaredFields()){
                if(field.isAnnotationPresent(Join.class)){
                    Join join=field.getAnnotation(Join.class);
                    Joinnable joinnable=new Joinnable();
                    if(join.table().isBlank()){
                        throw new CreatorException("Missing tablename for the join");
                    }
                    if(join.localKey().isBlank()){
                        throw new CreatorException("Missing localkeyException for the join");
                    }
                    if(join.foreignKey().isBlank()){
                        throw new CreatorException("Missing foreignkeyException for the join");
                    }
                    joinnable.tablename=join.table();
                    joinnable.foreignkey=join.foreignKey();
                    joinnable.localkey=join.localKey();
                    joinnable.type=join.value();
                    joinnable.foreignType=join.classModel();
                    joinnable.dropsOnDelete=join.dropsOnDelete();
                    field.setAccessible(true);
                    joinnable.f=field;
                    if(join.value()==Query.oneToOne){
                        joinnable.columns=getColumns(join.classModel());
                    }
                    joinnables.add(joinnable);
                }
            }
            checking=checking.getSuperclass();
        }
        return joinnables;
    }
    protected void init(){
        this.classModel= (Class<M>) this.getClass();
        Linked linked=classModel.getAnnotation(Linked.class);
        if(linked.value()!=null){
            this.tablename=linked.value();
        }else{
            this.tablename=classModel.getSimpleName();
        }
    }
    protected void initFields(){
        fields=new ArrayList<>();
        Class checking=this.classModel;
        while (checking!=null&&checking!= Entity.class&&checking!=View.class){
            for(Field field:checking.getDeclaredFields()){
                if(field.isAnnotationPresent(Column.class)){
                    fields.add(field);
                }
            }
            checking=checking.getSuperclass();
        }
    }
    protected ArrayList<Field> initFieldsof(Class checkingClass){
        ArrayList<Field> myfields=new ArrayList<>();
        Class checking=checkingClass;
        while (checking!=null&&checking!= Entity.class&&checking!=View.class){
            for(Field field:checking.getDeclaredFields()){
                if(field.isAnnotationPresent(Column.class)){
                    myfields.add(field);
                }
            }
            checking=checking.getSuperclass();
        }
        return  myfields;
    }
    protected Connection getConnection() throws IOException, ClassNotFoundException, SQLException {
        Properties properties=new Properties();
        properties.load(this.getClass().getClassLoader().getResourceAsStream("database.properties"));
        Class.forName(properties.getProperty("db.classname"));
        return DriverManager.getConnection(properties.getProperty("db.url"),properties.getProperty("db.user"),properties.getProperty("db.password"));
    }
}
