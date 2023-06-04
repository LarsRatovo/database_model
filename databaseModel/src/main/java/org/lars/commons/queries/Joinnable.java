package org.lars.commons.queries;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class Joinnable {
    String tablename;
    String localkey;
    String foreignkey;
    Integer type;
    String alias;
    Field f;
    ArrayList<String> columns;
    Class foreignType;
    boolean dropsOnDelete;

    public Field getF() {
        return f;
    }

    public String getAlias() {
        return alias;
    }

    public Integer getType() {
        return type;
    }

}
