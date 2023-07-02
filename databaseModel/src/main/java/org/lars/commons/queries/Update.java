package org.lars.commons.queries;

import org.lars.commons.queries.builders.UpdateBuilder;
import org.lars.commons.queries.executor.QueryExecutor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Update<M> extends Delete<M> {
    public void update() throws DatabaseModelException, IllegalAccessException, SQLException {
        init();
        UpdateBuilder builder=new UpdateBuilder();
        ArrayList<KeyValue> keyValues=new ArrayList<>();
        String sql=builder.build(this,classModel,initFieldsof(classModel),tablename,keyValues);
        QueryExecutor executor=new QueryExecutor();
        stretches=getStretches(classModel);
        for (Stretch stretch:stretches) {
            if(stretch.isCascade()){
                if(stretch.getExtensionType()==Query.one){
                    stretch.field.setAccessible(true);
                    Update<?> thing=(Update<?>) stretch.field.get(this);
                    thing.update();
                }else {
                    deleteChildren();
                    saveListChildren(stretch);
                }
            }
        }
        executor.executeUpdate(sql,keyValues,null);
    }
}
