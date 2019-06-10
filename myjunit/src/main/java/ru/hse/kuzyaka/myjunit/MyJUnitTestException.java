package ru.hse.kuzyaka.myjunit;

/** Class containing exceptions thrown during working with a test class, and not during running tests **/
public class MyJUnitTestException extends Exception {
    /**
     * Constructs an instance with given string as a message
     *
     * @param message message of the exception
     */
    public MyJUnitTestException(String message) {
        super(message);
    }
}
