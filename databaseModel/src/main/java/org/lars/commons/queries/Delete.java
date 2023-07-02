package org.lars.commons.queries;

import org.lars.commons.queries.builders.DeleteBuilder;
import org.lars.commons.queries.executor.QueryExecutor;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;

public class Delete<M> extends Insert<M> {
    public void delete() throws DatabaseModelException {
        init();
        DeleteBuilder builder=new DeleteBuilder();
        QueryExecutor executor=new QueryExecutor();
        this.stretches=getStretches(classModel);
        ArrayList<KeyValue> keyValues=new ArrayList<>();
        Connection connection=executor.getConnection();
        if(stretches!=null){
            for (Stretch stretch:stretches) {
                if(stretch.isCascade()){
                    KeyValue keyValue=new KeyValue();
                    String sql=builder.buildDeleteChildren(this,initFieldsof(classModel),stretch.getLocalKey(),stretch.getForeignKey(),stretch.getTableName(),keyValue);
                    executor.executeUpdate(sql, Collections.singletonList(keyValue),connection);
                }
            }
        }
        String sql=builder.build(this,initFieldsof(classModel),keyValues);
        executor.executeUpdate(sql,keyValues,connection);
    }
}
