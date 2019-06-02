package ru.hse.kuzyaka.myjunit;

import org.junit.jupiter.api.Test;
import ru.hse.kuzyaka.myjunit.testclasses.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class MyJUnitTest {
    @Test
    void testThrowing() {
        List<TestResult> results = assertDoesNotThrow(() -> MyJUnit.run(ThrowingTestsClass.class));
        assertEquals(3, results.size());
        assertAllPassed(results);
    }

    @Test
    void testNotThrowing() {
        List<TestResult> results =
                assertDoesNotThrow(() -> MyJUnit.run(NotThrowingTestsClass.class));
        assertEquals(2, results.size());
        assertAllPassed(results);
    }

    @Test
    void testTestsWithParameters() {
        assertThrows(MyJUnitTestException.class, () -> MyJUnit.run(BadTests.class));
        assertThrows(MyJUnitTestException.class, () -> MyJUnit.run(BadAfter.class));
        assertThrows(MyJUnitTestException.class, () -> MyJUnit.run(BadBefore.class));
    }

    @Test
    void testIgnored() {
        List<TestResult> results = assertDoesNotThrow(() -> MyJUnit.run(IgnoredTests.class));
        results.sort(Comparator.comparing(TestResult::getMessage));
        for (int i = 0; i < 2; i++) {
            assertEquals(String.valueOf(i + 1), results.get(i).getMessage());
        }
    }

    @Test
    void testMixed() {
        List<TestResult> results = assertDoesNotThrow(() -> MyJUnit.run(MixedTest.class));
        List<TestResult> failed = results.stream().filter(result -> result.getStatus() == TestStatus.FAILED).
                collect(Collectors.toList());
        List<TestResult> passed = results.stream().filter(result -> result.getStatus() == TestStatus.PASSED).
                collect(Collectors.toList());
        assertEquals(2, failed.size());
        assertEquals(1, passed.size());
    }

    @Test
    void testSingleFailed() {
        TestResult result = assertDoesNotThrow(() -> MyJUnit.run(Fail.class)).get(0);
        assertEquals(TestStatus.FAILED, result.getStatus());
        assertEquals("Expected java.io.IOException;Found java.lang.IndexOutOfBoundsException",
                result.getMessage());
    }

    private void assertAllPassed(List<TestResult> results) {
        for (TestResult result : results) {
            assertEquals(TestStatus.PASSED, result.getStatus());
        }
    }
}