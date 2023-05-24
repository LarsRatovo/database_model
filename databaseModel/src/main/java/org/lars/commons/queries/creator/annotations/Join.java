package org.lars.commons.queries.creator.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Join {
    int value() default 0;
    String table();
    String localKey();
    String foreignKey();
    Class classModel();
    boolean deep() default false;
}
