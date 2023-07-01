package org.lars.commons.queries.executor;

import org.lars.commons.queries.DatabaseModelException;
import org.lars.commons.queries.KeyValue;
import org.lars.commons.queries.Stretch;
import org.lars.commons.queries.Where;

import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.Properties;

public class QueryExecutor {
    protected Connection getConnection() throws IOException, ClassNotFoundException, SQLException {
        Properties properties=new Properties();
        properties.load(this.getClass().getClassLoader().getResourceAsStream("database.properties"));
        Class.forName(properties.getProperty("db.classname"));
        return DriverManager.getConnection(properties.getProperty("db.url"),properties.getProperty("db.user"),properties.getProperty("db.password"));
    }
    public QueryResult executeSelect(String sql, List<Where> wheres, List<Stretch> stretches) throws DatabaseModelException {
            return new QueryResult(executeQuery(sql,wheres),stretches);
    }
    ResultSet executeQuery(String sql, List< ? extends KeyValue> keyValues) throws DatabaseModelException {
        try {
            Connection connection=getConnection();
            PreparedStatement statement=connection.prepareStatement(sql);
            if(keyValues!=null){
                for (int i = 1; i <=keyValues.size() ; i++) {
                    statement.setObject(i,keyValues.get(i-1).getValue());
                }
            }
            System.out.println(statement.toString());
            return statement.executeQuery();
        }catch (IOException|ClassNotFoundException|SQLException connectionException){
            throw new DatabaseModelException(connectionException.getMessage());
        }
    }
    Object getGeneratedValue(String generator,Class<?> type) throws DatabaseModelException {
        String sqlBuilder = "SELECT nextval(" +generator.replace(" ", "")+")::" + type.getName().replace("java.lang.", "");
        ResultSet rs=executeQuery(sqlBuilder,null);
        try {
            Object genered=rs.getObject("nextval",type);
            rs.close();
            return genered;
        }catch (SQLException e){
            throw new DatabaseModelException(e.getMessage());
        }
    }
}
