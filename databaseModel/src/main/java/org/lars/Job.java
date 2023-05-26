package org.lars;

import org.lars.commons.queries.Entity;
import org.lars.commons.queries.Query;
import org.lars.commons.queries.creator.annotations.Column;
import org.lars.commons.queries.creator.annotations.Join;
import org.lars.commons.queries.creator.annotations.Linked;

import java.util.ArrayList;

@Linked("jobs")
public class Job extends Entity<Job> {
    @Column
    Integer id;
    @Column
    String name;
    @Join(value = Query.oneToMany, table = "persons", localKey = "id", foreignKey = "job", classModel = Person.class)
    ArrayList<Person> employes;
}
