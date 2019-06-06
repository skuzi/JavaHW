package ru.hse.kuzyaka.threadpool;

import java.util.function.Function;

/** Provides interface for tasks accepted by thread pool **/
public interface LightFuture<T> {
    /**
     * Tells if the processed task is done
     *
     * @return {@code true} if task is done; {@code false} otherwise
     */
    boolean isReady();

    /**
     * Returns the result of the task
     *
     * @return result of the task
     * @throws LightExecutionException if computation of task throws some exception
     * @throws InterruptedException    if thread was interrupted
     */
    T get() throws LightExecutionException, InterruptedException;

    /**
     * Returns the new task that is application of some function to the result of this task
     *
     * @param f   function to apply
     * @param <E> result type of the application
     * @return new task
     */
    <E> LightFuture<E> thenApply(Function<? super T, ? extends E> f);
}
