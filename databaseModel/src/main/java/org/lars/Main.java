package org.lars;
import org.lars.commons.queries.creator.CreatorException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.sql.Date;

public class Main {
    public static void main(String[] args) throws CreatorException, SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
//        for(Person person:new Person().select(false).executeMany()){
//            System.out.println("Id : "+person.id);
//            System.out.println("Name : "+person.name);
//        }
       Person person=new Person();
    //    person.name="Test";
    //    person.job=1;
    //    person.birth=Date.valueOf(LocalDate.now());
    //    person.age=69;
    //    person.weight=1.25;
    //    person.transfers=new ArrayList<>();
    //    Transfer t=new Transfer();
    //    t.job=1;
    //    t.transfer_date=Date.valueOf(LocalDate.now());
    //    Transfer t1=new Transfer();
    //    t1.job=2;
    //    t1.transfer_date=Date.valueOf(LocalDate.now());
    //    Transfer t2=new Transfer();
    //    t2.job=3;
    //    t2.transfer_date=Date.valueOf(LocalDate.now());
    //    Transfer t3=new Transfer();
    //    t3.job=1;
    //    t3.transfer_date=Date.valueOf(LocalDate.now());
    //    person.transfers.add(t);
    //    person.transfers.add(t1);
    //    person.transfers.add(t2);
    //    person.transfers.add(t3);
    //    person.insertReturning(true);
       for (Person p:person.select(true).executeMany()){
           System.out.println("Id : "+p.id);
           System.out.println("Name : "+p.name);
           System.out.println("Birth : "+p.birth);
           System.out.println("Age : "+p.age);
           System.out.println("Weight : "+p.weight);
           System.out.println("Actual job id : "+p.actual_job.id+" actual job name : "+p.actual_job.name);
           for(Transfer t:p.transfers){
               System.out.println("Person : "+t.person);
               System.out.println("Job : "+t.job);
               System.out.println("History job id : "+t.history_job.id+" History job name "+t.history_job.name);
               System.out.println("Transfert Date : "+t.transfer_date);
           }
           System.out.println("-----------------");
       }
        // for (Person p:person.select(true).executeMany()){
        //     p.delete();
        // }
    }
}