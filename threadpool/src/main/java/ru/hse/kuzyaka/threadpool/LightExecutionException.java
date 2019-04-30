package ru.hse.kuzyaka.threadpool;

/** Exception showing that while computing some task, another exception occurred. **/
public class LightExecutionException extends RuntimeException {
    /**
     * Constructs this exception with the given exception as a cause
     *
     * @param cause the cause of this exception
     */
    public LightExecutionException(Exception cause) {
        super(cause);
    }
}
