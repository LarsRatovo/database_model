package org.lars.commons.queries;

public class Where extends KeyValue{
    public static final String equals="=";
    public static final String notEquals="!=";
    public static final String like="LIKE";
    public static final String ilike="ILIKE";
    public static final String grtOrEquals=">=";
    public static final String grt=">";
    public static final String less="<";
    public static final String lessOrEquals="<=";
    String operator;

    public String getOperator() {
        return operator;
    }
}
