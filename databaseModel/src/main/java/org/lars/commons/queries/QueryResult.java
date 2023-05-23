package org.lars.commons.queries;

import java.sql.ResultSet;
import java.util.ArrayList;

public class QueryResult {
    ArrayList<String> aliases;
    ResultSet rs;
    ArrayList<Joinnable> joinnables;
    boolean deep;

    public ArrayList<String> getAliases() {
        return aliases;
    }

    public ResultSet getRs() {
        return rs;
    }

    public boolean isDeep() {
        return deep;
    }

    public ArrayList<Joinnable> getJoinnables() {
        return joinnables;
    }
}
