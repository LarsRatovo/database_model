package org.lars;

import org.lars.commons.queries.Entity;
import org.lars.commons.queries.Query;
import org.lars.commons.queries.creator.annotations.Column;
import org.lars.commons.queries.creator.annotations.Linked;

import java.sql.Date;

@Linked("persons")
public class Person extends Entity<Person> {
    @Column(autogen = true,autogenMode = Query.generator)
    Long id;
    @Column(value = "name")
    String name;
    @Column(value = "age")
    int age;
    @Column()
    Date birth;
    @Column()
    double weight;
    public Person(){
        super(Person.class);
    }
}
