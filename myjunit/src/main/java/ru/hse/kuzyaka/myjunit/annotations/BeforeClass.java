package ru.hse.kuzyaka.myjunit.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for method in test class. Method annotated by it should be run before all methods in the class.
 * If annotated method is not static, {@code MyJUnitTestException} is thrown
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BeforeClass {
}
