package org.lars;

import org.lars.commons.queries.Entity;
import org.lars.commons.queries.Query;
import org.lars.commons.queries.creator.annotations.Column;
import org.lars.commons.queries.creator.annotations.Join;
import org.lars.commons.queries.creator.annotations.Key;
import org.lars.commons.queries.creator.annotations.Linked;

import java.sql.Date;
import java.util.ArrayList;

@Linked("persons")
public class Person extends Entity<Person> {
    @Column(autogen = true,autogenMode = Query.generator)
    @Key
    Integer id;
    @Column
    String name;
    @Column
    Integer age;
    @Column
    Integer job;
    @Column
    Date birth;
    @Column
    Double weight;
    @Join(table = "jobs",localKey = "job",foreignKey = "id",classModel = Job.class)
    Job actual_job;
    @Join(value = Query.manyToMany,table = "transfer",localKey = "id",foreignKey = "person",classModel = Transfer.class,deep = true,dropsOnDelete = true)
    ArrayList<Transfer> transfers;

    public String getName() {
        return name;
    }

}
