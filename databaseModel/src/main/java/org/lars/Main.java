package org.lars;

import org.lars.commons.queries.creator.CreatorException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws CreatorException, SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Job j=new Job();
        List<Job> jobs=j.select(true)
                .executeMany();
        for(Job job:jobs){
            System.out.println("id job : "+job.id);
            System.out.println("name job : "+job.name);
            for (Person person:job.employes) {
                System.out.println(
                        "id: "+person.id+
                        " name:"+person.name+
                        " birth:"+person.birth+
                        " age: "+person.age+
                        "weight: "+person.weight+
                        "job: "+person.job);
            }
            System.out.println("-----------------");
        }
    }
}