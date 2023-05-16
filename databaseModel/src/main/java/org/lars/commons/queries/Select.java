package org.lars.commons.queries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Select<M> extends Query<M> {
    private String orderBy;
    private String limit;
    protected String[] columns;
    private ArrayList<Where> wheres;
    private String offset;
    private boolean or=false;
    protected void select(String tablename,Class<M> classModel,String... columns){
        this.tablename=tablename;
        this.model=classModel;
        this.columns=columns;
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
    public Select<M> equals(String column,Object value){
        checkWhere();
        Where w=new Where();
        w.columnName=column;
        w.value=value;
        w.operator=Where.equals;
        wheres.add(w);
        return this;
    }
    public Select<M> notEquals(String column,Object value){
        checkWhere();
        Where w=new Where();
        w.columnName=column;
        w.value=value;
        w.operator=Where.notEquals;
        wheres.add(w);
        return this;
    }
    public Select<M> like(String column,Object value){
        checkWhere();
        Where w=new Where();
        w.columnName=column;
        w.value=value;
        w.operator=Where.like;
        wheres.add(w);
        return this;
    }
    public Select<M> ilike(String column,Object value){
        checkWhere();
        Where w=new Where();
        w.columnName=column;
        w.value=value;
        w.operator=Where.ilike;
        wheres.add(w);
        return this;
    }
    public Select<M> greater(String column,Object value){
        checkWhere();
        Where w=new Where();
        w.columnName=column;
        w.value=value;
        w.operator=Where.grt;
        wheres.add(w);
        return this;
    }
    public Select<M> greaterOrEquals(String column,Object value){
        checkWhere();
        Where w=new Where();
        w.columnName=column;
        w.value=value;
        w.operator=Where.grtOrEquals;
        wheres.add(w);
        return this;
    }
    public Select<M> less(String column,Object value){
        checkWhere();
        Where w=new Where();
        w.columnName=column;
        w.value=value;
        w.operator=Where.less;
        wheres.add(w);
        return this;
    }
    public Select<M> lessOrEquals(String column,Object value){
        checkWhere();
        Where w=new Where();
        w.columnName=column;
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
    public ResultSet execute(Connection connection) throws SQLException {
        StringBuilder sqlBuilder=new StringBuilder();
        StringBuilder columnsBuilder=new StringBuilder();
        sqlBuilder.append("SELECT ");
        if(columns!=null){
            for(String column:columns){
                columnsBuilder.append(",");
                columnsBuilder.append(column.replace(" ",""));
            }
            columnsBuilder.deleteCharAt(0);
        }else{
            columnsBuilder.append("*");
        }
        sqlBuilder.append(columnsBuilder);
        sqlBuilder.append(" FROM ");
        sqlBuilder.append(tablename);
        if(wheres!=null){
            String separator;
            if(or){
                separator=" OR ";
            }else{
                separator=" AND ";
            }
            sqlBuilder.append(" WHERE ");
            for(int i=0;i<wheres.size();i++){
                Where w=wheres.get(i);
                if(i>0){
                    sqlBuilder.append(separator);
                }
                sqlBuilder.append(w.columnName.replace(" ",""));
                sqlBuilder.append(" ");
                sqlBuilder.append(w.operator);
                sqlBuilder.append(" ?");
            }
        }
        if(orderBy!=null){
            sqlBuilder.append(" ");
            sqlBuilder.append(orderBy);
        }
        if(limit!=null){
            sqlBuilder.append(" ");
            sqlBuilder.append(limit);
        }
        if(offset!=null){
            sqlBuilder.append(" ");
            sqlBuilder.append(offset);
        }
        PreparedStatement statement=connection.prepareStatement(sqlBuilder.toString());
        if(wheres!=null){
            for (int i=1;i<=wheres.size();i++){
                Where w=wheres.get(i-1);
                statement.setObject(i,w.value);
            }
        }
        return statement.executeQuery();
    }
    Object getGeneratedValue(String generator,Connection connection,Class type) throws SQLException {
        try(PreparedStatement statement=connection.prepareStatement("SELECT nextval(?)")){
            statement.setString(1,generator);
            ResultSet rs=statement.executeQuery();
            rs.next();
            Object generated= rs.getObject("nextval",type);
            rs.close();
            return type.cast(generated);
        }
    }
    public ResultSet executeRaw(String raw, Connection connection) throws Exception {
        PreparedStatement statement = connection.prepareStatement(raw);
        if (wheres != null) {
            for (int i = 1; i <= wheres.size(); i++) {
                Where w = wheres.get(i - 1);
                statement.setObject(i, w.value);
            }
        }
        return statement.executeQuery();
    }
}
