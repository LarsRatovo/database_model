package org.lars;

import org.lars.commons.queries.Insert;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        Person p=new Person();
        p.select(false,"age","birth");
        List<Person> ps=p.executeMany();
        for(Person tmp:ps){
            System.out.println(tmp.name+" age : "+tmp.age+" weight : "+tmp.weight+" date : "+tmp.birth+" jobname : "+tmp.jobDetails);
        }
//        p.name="Test";
//        p.age=69;
//        p.weight=0.21;
//        p.birth=Date.valueOf(LocalDate.now());
//        p.executeReturning();
//        System.out.println("id: "+p.id+" name:"+p.name+" age : "+p.age+" weight : "+p.weight+" date : "+p.birth);
    }
}
