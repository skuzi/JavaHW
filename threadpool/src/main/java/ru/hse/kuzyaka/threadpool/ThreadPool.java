package ru.hse.kuzyaka.threadpool;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Supplier;

/** Class for a simple thread pool **/
public class ThreadPool {
    private final SynchronizedQueue<ThreadPoolTask<?>> taskQueue;
    private Thread[] threads;
    private boolean isShutDown = false;

    /**
     * Constructs a fixed thread pool with the specified number of threads
     *
     * @param numberOfThreads number of threads which this thread pool will operate with
     */
    public ThreadPool(int numberOfThreads) {
        threads = new Thread[numberOfThreads];
        taskQueue = new SynchronizedQueue<>();
        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(this::taskWork);
            threads[i].start();
        }
    }

    /**
     * Submit a task for execution
     *
     * @return {@code LightFuture} representing a task
     * @throws IllegalStateException if thread was shut down at the moment a task was submitted
     */
    public <T> LightFuture<T> submit(Supplier<? extends T> supplier) {
        if (isShutDown) {
            throw new IllegalStateException("ThreadPool is already shut down");
        }
        var lightFuture = new ThreadPoolTask<T>(supplier);
        taskQueue.push(lightFuture);
        return lightFuture;
    }

    /**
     * Shuts the thread pool down (tries to interrupt all threads and joins them)
     */
    public void shutdown() throws InterruptedException {
        isShutDown = true;
        for (Thread thread : threads) {
            thread.interrupt();
        }
        for (Thread thread : threads) {
            thread.join();
        }
    }

    private void taskWork() {
        try {
            while (!Thread.interrupted()) {
                ThreadPoolTask<?> task = taskQueue.pop();
                task.run();
            }
        } catch (InterruptedException ignored) {

        }
    }

    private class ThreadPoolTask<T> implements LightFuture<T>, Runnable {
        private final List<ThreadPoolTask<?>> thenApplyTaskQueue;
        private volatile boolean isReady = false;
        private Supplier<? extends T> supplier;
        private T result = null;
        private Exception exception;

        ThreadPoolTask(Supplier<? extends T> supplier) {
            this.supplier = supplier;
            thenApplyTaskQueue = new ArrayList<>();
        }

        @Override
        public boolean isReady() {
            return isReady;
        }

        @Override
        public T get() throws LightExecutionException, InterruptedException {
            synchronized (thenApplyTaskQueue) {
                while (!isReady) {
                    thenApplyTaskQueue.wait();
                }
            }
            if (exception != null) {
                throw new LightExecutionException(exception);
            } else {
                return result;
            }
        }

        @Override
        public <E> LightFuture<E> thenApply(Function<? super T, ? extends E> f) {
            if (isShutDown) {
                throw new IllegalStateException("Thread is already shut down");
            }

            var task = new ThreadPoolTask<E>(() -> {
                if (exception != null) {
                    throw new RuntimeException(exception);
                }
                return f.apply(result);
            });

            synchronized (thenApplyTaskQueue) {
                if (isReady) {
                    taskQueue.push(task);
                } else {
                    thenApplyTaskQueue.add(task);
                }
            }
            return task;
        }

        @Override
        public void run() {
            try {
                result = supplier.get();
            } catch (Exception e) {
                exception = e;
            }
            isReady = true;
            supplier = null;

            synchronized (thenApplyTaskQueue) {
                thenApplyTaskQueue.notifyAll();
                thenApplyTaskQueue.forEach(taskQueue::push);
            }
        }
    }

    private class SynchronizedQueue<T> {
        private Queue<T> queue = new LinkedList<>();

        public synchronized void push(T t) {
            queue.offer(t);
            notifyAll();
        }

        public synchronized T pop() throws InterruptedException {
            while (queue.size() == 0) {
                wait();
            }
            return queue.poll();
        }
    }
}
