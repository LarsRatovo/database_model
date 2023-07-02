package org.lars;
import org.lars.commons.queries.DatabaseModelException;
import org.lars.commons.queries.creator.CreatorException;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
public class Main {
    public static void main(String[] args) throws Exception {
//        for(Person person:new Person().select(false).executeMany()){
//            System.out.println("Id : "+person.id);
//            System.out.println("Name : "+person.name);
//        }
        Job j=new Job();
        j.id=25;
        j.name="CHARGER";
        Person person=new Person();
        person.name="Test2";
        person.birth= Date.valueOf(LocalDate.now());
        person.age=228;
        person.job=1;
        person.weight= BigDecimal.valueOf(0.69);
        j.p=person;
////        person.insert();
        person.transfers=new ArrayList<>();
        Transfer t=new Transfer();
//        t.job=1;
        t.transfer_date=Date.valueOf(LocalDate.now());
        Transfer t1=new Transfer();
//        t1.job=2;
        t1.transfer_date=Date.valueOf(LocalDate.now());
        Transfer t2=new Transfer();
//        t2.job=3;
        t2.transfer_date=Date.valueOf(LocalDate.now());
        Transfer t3=new Transfer();
//        t3.job=1;
        t3.transfer_date=Date.valueOf(LocalDate.now());
        person.transfers.add(t);
        person.transfers.add(t1);
        person.transfers.add(t2);
        person.transfers.add(t3);
        j.delete();
//        j.insert();
        //        person.insert();
//        for (Person p:person.select(true).executeMany()){
//            System.out.println("Id : "+p.id);
//            System.out.println("Name : "+p.name);
//            System.out.println("Birth : "+p.birth);
//            System.out.println("Age : "+p.age);
//            System.out.println("Weight : "+p.weight);
//            System.out.println("Actual job id : "+p.actual_job.id+" actual job name : "+p.actual_job.name);
//            for(Transfer t:p.transfers){
//                System.out.println("Person : "+t.person);
//                System.out.println("Job : "+t.job);
//                System.out.println("History job id : "+t.history_job.id+" History job name "+t.history_job.name);
//                System.out.println("Transfert Date : "+t.transfer_date);
//            }
//            System.out.println("-----------------");
//        }
//        for (Person p:person.select(true).executeMany()){
//            p.delete();
//        }
    }
}