package ru.hse.kuzyaka.myjunit;

import java.util.List;

/** Body for console application **/
public class Main {
    /**
     * Method for running test class via application
     *
     * @param args arguments of application. To work correctly, must be single string, containing class name.
     *             If args size is not 1, exits with code 1.
     *             If class cannot be found, exits with code 2.
     *             If class contains errors critical for testing, exits with code 3.
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Argument must be a single string, containing class name");
            System.exit(1);
        }

        String className = args[0];
        try {
            List<TestResult> results = MyJUnit.run(Class.forName(className));
            int total = 0;
            int passed = 0;
            int failed = 0;
            for (var result : results) {
                System.out.println("=========");
                System.out.print("Test " + result.getTestName());
                switch (result.getStatus()) {
                    case IGNORED:
                        System.out.println(" was ignored, reason: " + result.getMessage());
                        break;
                    case PASSED:
                        System.out.println(" passed, execution time: " + result.getTimePassed());
                        System.out.println(result.getMessage());
                        total++;
                        passed++;
                        break;
                    case FAILED:
                        System.out.println(" failed, reason: " + result.getMessage());
                        total++;
                        failed++;
                        break;
                }
            }
            System.out.printf("Executed %d tests, passed %d tests, failed %d tests", total, passed, failed);
            System.out.println();
        } catch (MyJUnitTestException e) {
            System.out.println(e.getMessage());
            System.exit(2);
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found");
            System.exit(3);
        }
    }
}
