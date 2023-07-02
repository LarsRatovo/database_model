package org.lars.commons.queries;

public class KeyValue {
    String key;
    Object value;
    int id;

    public Object getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
