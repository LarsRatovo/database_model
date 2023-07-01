package org.lars.commons.queries;
import org.lars.commons.queries.builders.SelectBuilder;
import org.lars.commons.queries.creator.Creator;
import org.lars.commons.queries.creator.CreatorException;
import org.lars.commons.queries.executor.QueryExecutor;
import org.lars.commons.queries.executor.QueryResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Select<M> extends Query<M> {
    private String orderBy;
    private String limit;
    ArrayList<String> columns;
    private ArrayList<Where> wheres;
    ArrayList<Stretch> stretches;
    private String offset;
    protected Creator<M> creator;

    boolean deep;
    private boolean or=false;

    private void initSelect(boolean deep,String[] columns) throws CreatorException {
        this.deep=deep;
        if(columns!=null&&columns.length>0){
            this.columns=new ArrayList<>();
            Collections.addAll(this.columns, columns);
        }else {
            this.columns=getColumns(classModel);
        }
        if(deep){
            this.stretches=getStretches(classModel);
        }
    }
    public M select(boolean deep,String... columns) throws CreatorException,DatabaseModelException{
        init();
        initSelect(deep,columns);
        return (M)this;
    }
    public void select(String tablename,boolean deep,Class<M> classModel,String... columns) throws CreatorException {
        this.tablename=tablename;
        this.classModel=classModel;
        initSelect(deep,columns);
    }
    public Select<M> limit(int limit){
        this.limit="LIMIT "+limit;
        return this;
    }
    public Select<M> paginate(int rows,int page){
        this.limit="LIMIT "+rows;
        this.offset="OFFSET "+(rows*page);
        return this;
    }
    public Select<M> equalsTo(String column,Object value){
        checkWhere();
        Where w=new Where();
        w.key=column;
        w.value=value;
        w.operator=Where.equals;
        wheres.add(w);
        return this;
    }
    public Select<M> notEquals(String column,Object value){
        checkWhere();
        Where w=new Where();
        w.key=column;
        w.value=value;
        w.operator=Where.notEquals;
        wheres.add(w);
        return this;
    }
    public Select<M> like(String column,Object value){
        checkWhere();
        Where w=new Where();
        w.key=column;
        w.value=value;
        w.operator=Where.like;
        wheres.add(w);
        return this;
    }
    public Select<M> ilike(String column,Object value){
        checkWhere();
        Where w=new Where();
        w.key=column;
        w.value=value;
        w.operator=Where.ilike;
        wheres.add(w);
        return this;
    }
    public Select<M> greater(String column,Object value){
        checkWhere();
        Where w=new Where();
        w.key=column;
        w.value=value;
        w.operator=Where.grt;
        wheres.add(w);
        return this;
    }
    public Select<M> greaterOrEquals(String column,Object value){
        checkWhere();
        Where w=new Where();
        w.key=column;
        w.value=value;
        w.operator=Where.grtOrEquals;
        wheres.add(w);
        return this;
    }
    public Select<M> less(String column,Object value){
        checkWhere();
        Where w=new Where();
        w.key=column;
        w.value=value;
        w.operator=Where.less;
        wheres.add(w);
        return this;
    }
    public Select<M> lessOrEquals(String column,Object value){
        checkWhere();
        Where w=new Where();
        w.key=column;
        w.value=value;
        w.operator=Where.lessOrEquals;
        wheres.add(w);
        return this;
    }
    public Select<M> setToOr(){
        this.or=true;
        return this;
    }
    public Select<M> orderBy(String column,int order){
        if(order==Query.desc){
            this.orderBy="ORDER BY "+column+" DESC";
        }else{
            this.orderBy="ORDER BY "+column+" ASC";
        }
        return this;
    }
    private void checkWhere(){
        if(wheres==null){
            wheres=new ArrayList<>();
        }
    }
    private QueryResult execute() throws DatabaseModelException{
        SelectBuilder builder=new SelectBuilder();
        String sql=builder.buildSelect(columns,deep,stretches,tablename,wheres,or,orderBy,limit,offset);
        QueryExecutor executor=new QueryExecutor();
        return executor.executeSelect(sql,this.wheres,stretches);
    }
    public M executeOne() throws CreatorException {
        checkCreator();
        try {
            return creator.createOne(this.execute(), this.getClass());
        }catch (DatabaseModelException e){
            throw new CreatorException(e.getMessage(),e);
        }
    }
    public List<M> executeMany() throws CreatorException {
        checkCreator();
        try{
            return creator.createMany(this.execute(),classModel);
        }catch (DatabaseModelException dbe){
            throw new CreatorException(dbe.getMessage(),dbe);
        }
    }
    private void checkCreator(){
        if(creator==null){
            creator=new Creator<>();
        }
    }
}