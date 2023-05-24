package org.lars.commons.queries;

import org.lars.commons.queries.creator.CreatorException;
import org.lars.commons.queries.creator.annotations.Column;
import org.lars.commons.queries.creator.annotations.Join;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class Query<M> {
    public static final int desc=1;
    public static final int asc=0;
    public static final int oneToOne=0;
    public static final int oneToMany=1;
    public static final int generator=1;
    public static final int self=2;
    static final int none=0;
    protected Class<M> model;
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
}
