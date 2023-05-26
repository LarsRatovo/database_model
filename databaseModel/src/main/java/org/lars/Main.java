package org.lars;

import org.lars.commons.queries.creator.CreatorException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws CreatorException, SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        for(Person person:new Person().select(false).executeMany()){
            person.name="Test";
            person.age=null;
            person.weight=10.;
            person.executeUpdate();
        }
    }
}