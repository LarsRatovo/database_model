package org.lars;

import org.lars.commons.queries.Entity;
import org.lars.commons.queries.Query;
import org.lars.commons.queries.creator.annotations.Column;
import org.lars.commons.queries.creator.annotations.Extension;
import org.lars.commons.queries.creator.annotations.Linked;

import java.util.ArrayList;

@Linked("jobs")
public class Job extends Entity<Job> {
    @Column(autogenMode = Query.generatedValue)
    Integer id;
    @Column
    String name;
    @Extension(cascade = true,table = "persons",localKey = "id",foreignKey = "job",classModel = Person.class)
    Person p;
}
