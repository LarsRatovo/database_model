package org.lars.commons.queries;

public class Query<M> {
    public static final int desc=1;
    public static final int asc=0;
    public static final int generator=1;
    public static final int self=2;
    static final int none=0;
    protected Class<M> model;
    protected String tablename;
}
