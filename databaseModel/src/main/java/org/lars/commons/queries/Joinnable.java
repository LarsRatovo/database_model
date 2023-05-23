package org.lars.commons.queries;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class Joinnable {
    String tablename;
    String localkey;
    String foreignkey;
    String alias;
    Field f;
    ArrayList<String> columns;

    public Field getF() {
        return f;
    }

    public String getAlias() {
        return alias;
    }
}
