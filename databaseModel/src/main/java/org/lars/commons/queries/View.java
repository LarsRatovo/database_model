package org.lars.commons.queries;

import org.lars.commons.queries.creator.Creator;
import org.lars.commons.queries.creator.CreatorException;
import org.lars.commons.queries.creator.annotations.Linked;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class View<M> extends Select<M>{
    Class<M> classModel;
    Creator<M> creator;
    public View(Class<M> classModel){
        this.classModel=classModel;
        Linked linked=classModel.getAnnotation(Linked.class);
        if(linked.value()!=null){
            this.tablename=linked.value();
        }else{
            this.tablename=classModel.getSimpleName();
        }
    }
    public M executeOne() throws NoSuchMethodException, SQLException, IOException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        checkCreator();
        Connection connection=null;
        try {
            connection=getConnection();
            M result=creator.createOne(this.execute(connection));
            connection.close();
            return result;
        }finally {
            if(connection!=null){
                connection.close();
            }
        }
    }
    public List<M> executeMany() throws SQLException, IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        checkCreator();
        try (Connection connection = getConnection()) {
            return creator.createMany(this.execute(connection));
        }
    }
    public View<M> select(boolean deep,String... columns) throws CreatorException {
        super.select(tablename,deep, classModel, columns);
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
