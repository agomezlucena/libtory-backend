package io.github.agomezlucena.libtory.shared;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface FakerIsbn {
    enum IsbnType {
        ISBN_13,
        ISBN_10
    }

    /// if you define this value as not blank value will generate ISBN that aren't in the
    /// declared isbn should be separated by a semicolon character ';' null values are treated
    /// like an empty string
    String avoidIsbn() default "";
    IsbnType value() default IsbnType.ISBN_13;
    boolean withHyphens() default false;
    boolean dontRepeat() default false;
}
