package ru.hse.kuzyaka.myjunit.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Annotation for method in test class.
 *  Each annotated method is marked as a test method. To run the method, MyJUnit construct a fresh class instance,
 *  and invokes the annotated method. If no exceptions are thrown method is assumed as success.
 *  One can optionally specify two parameters -- {@code expected} and {@code ignore}.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Test {
    /** Optionally specify some {@code Throwable} to cause a method to succeed iff an exception of a specified class is thrown */
    Class<? extends Throwable> expected() default None.class;
    /** Optionally specify some not empty {@code String} to tell MyJUnit to ignore this test with a given string as a reason */
    String ignore() default "";

    class None extends Throwable {}
}
