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
    public Connection getConnection() throws DatabaseModelException {
        try {
            Properties properties=new Properties();
            properties.load(this.getClass().getClassLoader().getResourceAsStream("database.properties"));
            Class.forName(properties.getProperty("db.classname"));
            return DriverManager.getConnection(properties.getProperty("db.url"),properties.getProperty("db.user"),properties.getProperty("db.password"));

        }catch (IOException | ClassNotFoundException | SQLException e){
            throw new DatabaseModelException(e.getMessage());
        }
    }
    public QueryResult executeSelect(String sql, List<Where> wheres, List<Stretch> stretches) throws DatabaseModelException {
            return new QueryResult(executeQuery(sql,wheres),stretches);
    }
    ResultSet executeQuery(String sql, List< ? extends KeyValue> keyValues) throws DatabaseModelException {
        Connection connection=null;
        PreparedStatement statement=null;
        try {
            connection=getConnection();
            statement=connection.prepareStatement(sql);
            if(keyValues!=null){
                for (KeyValue keyValue:keyValues) {
                    statement.setObject(keyValue.getId(),keyValue.getValue());
                }
            }
            System.out.println(statement.toString());
            return statement.executeQuery();
        }catch (SQLException connectionException){
            throw new DatabaseModelException(connectionException.getMessage());
        }finally {
            try {
                if(statement!=null){
                    statement.close();
                }
                if(connection!=null){
                    connection.close();
                }
            }catch (SQLException e){
                throw new DatabaseModelException(e.getMessage());
            }
        }
    }
    public void executeUpdate(String sql, List<KeyValue> keyValues,Connection connection) throws DatabaseModelException {
        try (PreparedStatement preparedStatement=connection.prepareStatement(sql)){
            for(KeyValue keyValue:keyValues){
                preparedStatement.setObject(keyValue.getId(),keyValue.getValue());
            }
            System.out.println(preparedStatement.toString());
            preparedStatement.executeUpdate();
        }catch (SQLException e){
            throw new DatabaseModelException(e.getMessage());
        }
    }
    public Object getGeneratedValue(String generator,Class<?> type,Connection connection) throws DatabaseModelException, SQLException {
        String sqlBuilder = "SELECT nextval('" +generator.replace(" ", "")+"')::" + type.getName().replace("java.lang.", "");
        ResultSet rs=connection.prepareStatement(sqlBuilder.toString()).executeQuery();
        try {
            rs.next();
            Object genered=rs.getObject("nextval",type);
            return genered;
        }catch (SQLException e){
            throw new DatabaseModelException(e.getMessage());
        }
    }
}
