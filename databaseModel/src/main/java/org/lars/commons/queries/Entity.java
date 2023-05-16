package org.lars.commons.queries;

import org.lars.commons.queries.creator.Creator;
import org.lars.commons.queries.creator.annotations.Linked;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class Entity<M> extends Insert<M> {
    Class<M> classModel;
    Creator<M> creator;
    public Entity(Class<M> classModel){
        this.classModel=classModel;
        Linked linked=classModel.getAnnotation(Linked.class);
        if(linked.value()!=null){
            this.tablename=linked.value();
        }else{
            this.tablename=classModel.getSimpleName();
        }
    }
    public void executeInsert() throws SQLException, IOException, ClassNotFoundException, IllegalAccessException {
        insert(this.tablename,classModel);
        try (Connection connection = getConnection()) {
            this.executeInsert(connection);
        }
    }
    public void executeInsert(List<M> lists) throws SQLException, IOException, ClassNotFoundException, IllegalAccessException {
        try (Connection connection=getConnection()){
            for(M m:lists){
                ((Entity<?>)m).executeInsert(connection);
            }
        }
    }
    public void executeInsertReturning(List<M> lists) throws SQLException, IOException, ClassNotFoundException, IllegalAccessException {
        try (Connection connection=getConnection()){
            for(M m:lists){
                ((Entity<?>)m).executeInsertReturning(connection);
            }
        }
    }
    public void executeReturning() throws SQLException, IOException, ClassNotFoundException, IllegalAccessException {
        insert(this.tablename,classModel);
        try (Connection connection=getConnection()){
            this.executeInsertReturning(connection);
        }
    }
    public M executeOne() throws NoSuchMethodException, SQLException, IOException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        checkCreator();
        Connection connection=null;
        try {
            connection=getConnection();
            M result=creator.createOne(this.execute(connection));
            return result;
        }finally {
            if(connection!=null)connection.close();
        }
    }
    public List<M> executeMany() throws SQLException, IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        checkCreator();
        Connection connection=null;
        try {
            connection=getConnection();
            List<M> results=creator.createMany(this.execute(connection));
            return results;
        }finally {
            if(connection!=null)connection.close();
        }
    }
    public Entity<M> select(String... columns) {
        super.select(tablename, classModel, columns);
        return this;
    }
    private void checkCreator() throws NoSuchMethodException {
        if(creator==null){
            creator=new Creator<>(this.classModel);
        }
    }
    protected Connection getConnection() throws IOException, ClassNotFoundException, SQLException {
        Properties properties=new Properties();
        properties.load(classModel.getClassLoader().getResourceAsStream("database.properties"));
        Class.forName(properties.getProperty("db.classname"));
        return DriverManager.getConnection(properties.getProperty("db.url"),properties.getProperty("db.user"),properties.getProperty("db.password"));
    }
}
