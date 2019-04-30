package ru.hse.kuzyaka.threadpool;

import org.junit.jupiter.api.RepeatedTest;

import java.util.ArrayList;
import java.util.function.Supplier;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;

public class ThreadPoolTest {

    private Supplier<Task> supplier = () -> {
        var task = new Task();
        task.doWork();
        return task;
    };

    private void runTasks(int numberOfThreads, int numberOfTasks) {
        ThreadPool pool = new ThreadPool(numberOfThreads);
        var tasks = new ArrayList<LightFuture<Task>>();
        for (int i = 0; i < numberOfTasks; i++) {
            tasks.add(pool.submit(supplier));
        }

        try {
            sleep(50);
        } catch (InterruptedException ignored) {

        }

        boolean flag = false;
        for (int i = 0; i < 10; i++) {
            if (tasks.get(i).isReady()) {
                flag = true;
                final int j = i;
                final int[] counter = new int[1];
                assertDoesNotThrow(() -> counter[0] = tasks.get(j).get().getCounter());
                assertEquals(100, counter[0]);
            }
        }
        assertTrue(flag);
    }

    @RepeatedTest(20)
    void testFewTasksFewThreads() {
        runTasks(10, 10);
    }

    @RepeatedTest(20)
    void testManyTasksFewThreads() {
        runTasks(10, 500);
    }

    @RepeatedTest(20)
    void testFewTasksManyThreads() {
        runTasks(100, 50);
    }

    @RepeatedTest(20)
    void testManyTasksManyThreads() {
        runTasks(100, 500);
    }

    @RepeatedTest(20)
    void testThrowsException() {
        var pool = new ThreadPool(5);
        var lightFuture = pool.submit(() -> 5 / 0);
        assertThrows(LightExecutionException.class, lightFuture::get);
    }

    @RepeatedTest(20)
    void testShutDownDoesNotTakeSubmissions() {
        var pool = new ThreadPool(5);
        pool.shutdown();
        assertThrows(IllegalStateException.class, () -> pool.submit(() -> 1));
    }

    @RepeatedTest(20)
    void testShutDownDoesNotTakeThenApply() {
        var pool = new ThreadPool(5);
        var future = pool.submit(() -> 1);
        pool.shutdown();
        assertThrows(IllegalStateException.class, () -> future.thenApply(x -> x * 2));
    }

    @RepeatedTest(20)
    void testThenApply() throws InterruptedException {
        var pool = new ThreadPool(5);
        var future1 = pool.submit(() -> 1);
        var future2 = future1.thenApply(x -> x * 2);
        var future3 = future2.thenApply(x -> x * 2);
        assertEquals(1, future1.get());
        assertEquals(2, future2.get());
        assertEquals(4, future3.get());
    }

    @RepeatedTest(20)
    void testAllThreadsCreated() {
        int threadStart = Thread.activeCount();
        var pool = new ThreadPool(100);
        assertEquals(threadStart + 100, Thread.activeCount());
    }

    private static class Task {
        private int counter;

        private synchronized void doWork() {
            for (int i = 0; i < 100; i++) {
                counter++;
            }
        }

        private int getCounter() {
            return counter;
        }
    }
}