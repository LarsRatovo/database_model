package org.lars;

import org.lars.commons.queries.Entity;
import org.lars.commons.queries.Query;
import org.lars.commons.queries.creator.annotations.Column;
import org.lars.commons.queries.creator.annotations.Extension;
import org.lars.commons.queries.creator.annotations.Key;
import org.lars.commons.queries.creator.annotations.Linked;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;

@Linked("persons")
public class Person extends Entity<Person> {
    @Column(autogenMode = Query.generatedValue)
    @Key
    Integer id;
    @Column
    String name;
    @Column
    Integer age;
    @Column
    Date birth;
    @Column
    BigDecimal weight;
    @Column
    Integer job;
    @Extension(table = "jobs",localKey = "job",foreignKey = "id",classModel = Job.class)
    Job actual_job;
    @Extension(value = Query.many,table = "transfer",localKey = "id",foreignKey = "person",classModel = Transfer.class,deep = true,cascade = true)
    ArrayList<Transfer> transfers;
}
