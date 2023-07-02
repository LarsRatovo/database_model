package org.lars.commons.queries;
import org.lars.commons.queries.creator.CreatorException;
import org.lars.commons.queries.creator.annotations.Column;
import org.lars.commons.queries.creator.annotations.Extension;
import org.lars.commons.queries.creator.annotations.Linked;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class Query<M> {
    public static final int desc=1;
    public static final int asc=0;
    public static final int one=0;
    public static final int many=1;
    public static final int generator=1;
    public static final int self=2;
    public static final int generatedValue=3;

    protected Class<M> classModel;

    protected String tablename;
    ArrayList<String> getColumns(Class<?> checkingClass){
        ArrayList<String> cols=new ArrayList<>();
        for(Field f:this.initFieldsof(checkingClass)){
            Column column=f.getAnnotation(Column.class);
            if(column.value().isBlank()){
                cols.add(f.getName());
            }else{
                cols.add(column.value());
            }
        }
        return cols;
    }
    protected ArrayList<Stretch> getStretches(Class<?> checkingClass){
        Class<?> checking=checkingClass;
        ArrayList<Stretch> stretches=new ArrayList<>();
        int id=0;
        while (checking!=null&&checking!= Entity.class&&checking!= View.class){
            for(Field field:checking.getDeclaredFields()){
                if(field.isAnnotationPresent(Extension.class)){
                    Stretch stretch=new Stretch();
                    Extension extension=field.getAnnotation(Extension.class);
                    field.setAccessible(true);
                    stretch.setField(field);
                    stretch.columns=this.getColumns(extension.classModel());
                    stretch.tableAliasId=id;
                    id++;
                    stretches.add(stretch);
                }
            }
            checking=checking.getSuperclass();
        }
        return stretches;
    }
    protected void init() throws DatabaseModelException{
        this.classModel= (Class<M>) this.getClass();
        Linked linked=classModel.getAnnotation(Linked.class);
        if(linked.value()!=null){
            this.tablename=linked.value();
        }else{
            throw new DatabaseModelException("Linked annotation is needed but unfortunatly not found");
        }
    }
    protected ArrayList<Field> initFieldsof(Class<?> checkingClass){
        ArrayList<Field> myfields=new ArrayList<>();
        Class<?> checking=checkingClass;
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
}