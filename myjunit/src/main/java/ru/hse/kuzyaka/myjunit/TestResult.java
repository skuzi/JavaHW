package ru.hse.kuzyaka.myjunit;

/** Class for providing test result, contains test name, message of test, its status, and time this test lasted **/
public class TestResult {
    private long timePassed;
    private String message;
    private TestStatus status;
    private String testName;

    public TestResult(String testName, long timePassed, String message, TestStatus status) {
        this.timePassed = timePassed;
        this.message = message;
        this.status = status;
        this.testName = testName;
    }

    public long getTimePassed() {
        return timePassed;
    }

    public String getMessage() {
        return message;
    }

    public TestStatus getStatus() {
        return status;
    }

    public String getTestName() {
        return testName;
    }

}
