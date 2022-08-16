package dev.dpvb.survive.util.messages;

import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Mark an element which requires special count handling.
 */
@Documented
@Retention(RUNTIME)
@Target({FIELD, LOCAL_VARIABLE})
@Repeatable(CountedElements.class)
public @interface Counting {
    /**
     * The placeholder to replace with the count.
     *
     * @return the placeholder
     * @implSpec The placeholder must not match the string {@code "count"}.
     */
    @Pattern("^(?!count).*$")
    @NotNull String value() default "s";

    /**
     * The specific index from which to source the number.
     * <p>
     * This corresponds directly to the index of the element in the array,
     * or in the case of varargs, the argument index.
     *
     * @return a specific index
     * @implSpec -1 indicates the index should be sourced from this
     * annotation's index in the container annotation array.
     * @implNote -1 is the default value.
     */
    @Range(from = -1, to = Integer.MAX_VALUE)
    int arg() default -1;

    /**
     * The replacement to use when count is zero.
     *
     * @return the text to use when count is zero
     */
    @NotNull String zero() default "s";

    /**
     * The replacement to use when count is one.
     *
     * @return the text to use when count is one
     */
    @NotNull String one() default "";

    /**
     * The replacement to use when count is two or more.
     *
     * @return the text to use when count is two or more
     */
    @NotNull String many() default "s";

    record Counted(String placeholder, int arg, String zero, String one, String many) {
        Counted(Counting counting) {
            this(counting.value(), counting.arg(), counting.zero(), counting.one(), counting.many());
        }

        String process(Number count) {
            final var s = switch (count.intValue()) {
                case 0 -> zero;
                case 1 -> one;
                default -> many;
            };
            return s.replace("{count}", count.toString());
        }
    }
}
