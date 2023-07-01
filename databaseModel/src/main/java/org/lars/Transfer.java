package org.lars;

import org.lars.commons.queries.Entity;
import org.lars.commons.queries.creator.annotations.Column;
import org.lars.commons.queries.creator.annotations.Extension;
import org.lars.commons.queries.creator.annotations.Linked;

import java.sql.Date;

@Linked("transfer")
public class Transfer extends Entity<Transfer> {
    @Column
    Integer person;
    @Column
    Integer job;
    @Column
    Date transfer_date;
    @Extension(table = "jobs",localKey = "job",foreignKey = "id",classModel = Job.class)
    Job history_job;
}
