package org.lars;

import org.lars.commons.queries.creator.CreatorException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) throws CreatorException, SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
//        for(Person person:new Person().select(false).executeMany()){
//            System.out.println("Id : "+person.id);
//            System.out.println("Name : "+person.name);
//        }
        Person person=new Person();
        person.name="John Doe";
        person.weight=19.;
        person.age=69;
        person.job=1;
        person.birth= Date.valueOf(LocalDate.now());
        System.out.println("Generated id : "+person.id);
    }
}