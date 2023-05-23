package org.lars;

import org.lars.commons.queries.Entity;
import org.lars.commons.queries.Query;
import org.lars.commons.queries.creator.annotations.Column;
import org.lars.commons.queries.creator.annotations.Join;
import org.lars.commons.queries.creator.annotations.Linked;

import java.sql.Date;

@Linked("persons")
public class Person extends Entity<Person> {
    @Column(autogen = true,autogenMode = Query.generator)
    Integer id;
    @Column(value = "name")
    String name;
    @Column(value = "age")
    Integer age;
    @Column
    Date birth;
    @Column
    double weight;
    @Column
    Integer job;
    @Join(table = "jobs",classModel = Job.class,localKey = "job",foreignKey = "id")
    Job jobDetails;
    public Person(){
        super(Person.class);
    }
}
