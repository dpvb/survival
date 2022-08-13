package dev.dpvb.survival.util.messages;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Mark an element which contains counted elements.
 */
@Documented
@Retention(SOURCE)
@Target({FIELD, LOCAL_VARIABLE})
public @interface Counting {
    /**
     * A description of each counted element.
     *
     * @return an array of descriptions
     */
    String[] value() default {};
}
