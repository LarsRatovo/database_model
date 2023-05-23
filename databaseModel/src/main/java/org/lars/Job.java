package org.lars;

import org.lars.commons.queries.Entity;
import org.lars.commons.queries.creator.annotations.Column;
import org.lars.commons.queries.creator.annotations.Linked;

@Linked("jobs")
public class Job extends Entity<Job> {
    @Column
    Integer id;
    @Column
    String name;

    public Job() {
        super(Job.class);
    }
}
