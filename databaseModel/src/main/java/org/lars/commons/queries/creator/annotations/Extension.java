package org.lars.commons.queries.creator.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Extension {
    int value() default 0;
    String table() default "";
    String localKey();
    String foreignKey();
    Class<?> classModel();
    boolean cascade() default false;
    boolean deep() default false;
}
