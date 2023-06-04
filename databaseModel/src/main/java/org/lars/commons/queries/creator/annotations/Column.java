package org.lars.commons.queries.creator.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Column {
    String value() default "";
    boolean autogen() default false;
    int autogenMode() default 0;
    String generator() default "";
}
