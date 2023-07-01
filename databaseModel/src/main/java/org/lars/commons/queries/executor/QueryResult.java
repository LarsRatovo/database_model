package org.lars.commons.queries.executor;

import org.lars.commons.queries.Stretch;
import java.sql.ResultSet;
import java.util.List;

public class QueryResult {
    ResultSet rs;
    List<Stretch> stretches;
    public QueryResult(ResultSet rs,List<Stretch> stretches){
        this.rs=rs;
        this.stretches=stretches;
    }
    public ResultSet getRs() {
        return rs;
    }

    public List<Stretch> getStretches() {
        return stretches;
    }
}
