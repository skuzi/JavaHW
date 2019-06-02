package ru.hse.kuzyaka.myjunit;

import ru.hse.kuzyaka.myjunit.annotations.*;
import ru.hse.kuzyaka.myjunit.TestStatus;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MyJUnit {
    public static List<TestResult> run(Class<?> testClass) throws MyJUnitTestException {
        Method[] methods = testClass.getMethods();
        List<Method> beforeClassMethods = getAnnotatedMethods(methods, BeforeClass.class);
        List<Method> afterClassMethods = getAnnotatedMethods(methods, AfterClass.class);
        List<Method> beforeMethods = getAnnotatedMethods(methods, Before.class);
        List<Method> afterMethods = getAnnotatedMethods(methods, After.class);
        List<Method> testMethods = getAnnotatedMethods(methods, Test.class);

        if (!allStatic(beforeClassMethods) || !allStatic(afterClassMethods)) {
            throw new MyJUnitTestException("all BeforeClass and AfterClass methods must be static");
        }

        if (!allWithNoParameters(beforeClassMethods) ||
                !allWithNoParameters(afterClassMethods) ||
        !allWithNoParameters(beforeMethods) ||
        !allWithNoParameters(afterMethods) ||
        !allWithNoParameters(testMethods)) {
            throw new MyJUnitTestException("all methods must have zero parameters");
        }

        Constructor<?> testConstructor = null;
        if (!allStatic(testMethods)) {
            try {
                testConstructor = testClass.getConstructor();
            } catch (NoSuchMethodException e) {
                throw new MyJUnitTestException
                        ("test class has non-static test methods and default public constructor wasn't found");
            }
        }

        List<TestResult> testResults = new ArrayList<>();
        runMethods(beforeClassMethods, null);
        for (var method : testMethods) {
            Test annotation = method.getAnnotation(Test.class);
            if (!annotation.ignore().equals("")) {
                testResults.
                        add(new TestResult(method.getName(), -1, annotation.ignore(), TestStatus.IGNORED));
                continue;
            }
            Object instance = null;
            if (testConstructor != null) {
                try {
                    instance = testConstructor.newInstance();
                } catch (IllegalAccessException ignored) {
                } catch (InstantiationException e) {
                    throw new MyJUnitTestException("class should not be abstract");
                } catch (InvocationTargetException e) {
                    var exception = new MyJUnitTestException("Constructor threw an exception");
                    exception.addSuppressed(e);
                    throw exception;
                }
            }
            runMethods(beforeMethods, instance);
            testResults.add(runTest(method, instance));
            runMethods(afterMethods, instance);
        }
        runMethods(afterClassMethods, null);
        return testResults;
    }

    private static boolean allWithNoParameters(List<Method> methods) {
        return methods.stream().allMatch(method -> method.getParameterCount() == 0);
    }

    private static TestResult runTest(Method method, Object instance) {
        Test annotation = method.getAnnotation(Test.class);
        String expected;
        String actual = "";
        if (annotation.expected() == Test.None.class) {
            expected = "No exception expected;";
        } else {
            expected = "Expected " + annotation.expected().getName() + ";";
        }
        long startTime = System.currentTimeMillis();
        try {
            method.invoke(instance);
            actual = "None was thrown";
        } catch (IllegalAccessException ignored) {
        } catch (InvocationTargetException e) {
            actual = "Found " + e.getTargetException().getClass().getName();
            if (!annotation.expected().isInstance(e.getTargetException())) {
                return new TestResult(method.getName(), -1, expected + actual, TestStatus.FAILED);
            }
        }
        long timePassed = System.currentTimeMillis() - startTime;
        return new TestResult(method.getName(), timePassed, expected + actual, TestStatus.PASSED);
    }

    private static void runMethods(List<Method> methods, Object instance) throws MyJUnitTestException {
        for (var method : methods) {
            try {
                method.invoke(instance);
            } catch (IllegalAccessException ignored) {
            } catch (InvocationTargetException e) {
                var exception = new MyJUnitTestException("exception was thrown in non-test method");
                exception.addSuppressed(e);
                throw exception;
            }
        }
    }

    private static boolean allStatic(List<Method> methods) {
        return methods.stream().allMatch(method -> Modifier.isStatic(method.getModifiers()));
    }

    private static List<Method> getAnnotatedMethods(Method[] methods, Class<? extends Annotation> annotation) {
        return Arrays.stream(methods).filter(method -> method.isAnnotationPresent(annotation)).
                collect(Collectors.toList());
    }
}
