package org.lars.commons.queries;

import org.lars.commons.queries.creator.annotations.Extension;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class Stretch {
    Field field;
    int tableAliasId;
    ArrayList<String> columns;
    private Extension extension;

    public void setField(Field field) {
        this.field = field;
        this.extension=field.getAnnotation(Extension.class);
    }
    public int getExtensionType() {
        return extension.value();
    }

    public ArrayList<String> getColumns() {
        return columns;
    }

    public int getTableAliasId() {
        return tableAliasId;
    }

    public String getTableName() {
        return extension.table();
    }

    public String getLocalKey() {
        return extension.localKey();
    }

    public String getForeignKey() {
        return extension.foreignKey();
    }

    public Field getField() {
        return field;
    }
    public boolean isDeep(){
        return extension.deep();
    }
}
