package org.lars;

import org.lars.commons.queries.Entity;
import org.lars.commons.queries.creator.annotations.Column;
import org.lars.commons.queries.creator.annotations.Key;
import org.lars.commons.queries.creator.annotations.Linked;

import java.sql.Date;

@Linked("persons")
public class Person extends Entity<Person> {
    @Column
    @Key
    Integer id;
    @Column
    String name;
    @Column
    Integer age;
    @Column
    Date birth;
    @Column
    Integer job;
    @Column
    Double weight;

    public Person() {
        super(Person.class);
    }

    public String getName() {
        return name;
    }

    public Integer getJob() {
        return job;
    }
}
