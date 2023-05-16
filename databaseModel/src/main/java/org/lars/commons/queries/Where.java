package org.lars.commons.queries;

class Where {
    static String equals="=";
    static String notEquals="=";
    static String like="LIKE";
    static String ilike="ILIKE";
    static String grtOrEquals=">=";
    static String grt=">";
    static String less="<";
    static String lessOrEquals="<=";
    String columnName;
    Object value;
    String operator;
}
