package ru.hse.kuzyaka.myjunit;

public class TestResult {
    private long timePassed;
    private String message;
    private TestStatus status;

    public String getTestName() {
        return testName;
    }

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
}
