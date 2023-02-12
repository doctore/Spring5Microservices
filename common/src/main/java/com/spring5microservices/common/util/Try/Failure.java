package com.spring5microservices.common.util.Try;

import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Objects;

/**
 * The unsuccessful computation of a {@link Try} operation.
 *
 * @param <T>
 *    Value type in the case of {@link Success}
 */
public final class Failure<T> extends Try<T> implements Serializable {

    private static final long serialVersionUID = -7025692225589255405L;

    private final Throwable exception;

    /**
     * Constructs a {@link Failure}.
     *
     * @param exception
     *    {@link Throwable} with the cause of the error
     *
     * @throws Throwable if the given {@code exception} is fatal, i.e. non-recoverable
     */
    private Failure(final Throwable exception) {
        if (isFatal(exception)) {
            sneakyThrow(exception);
        }
        this.exception = exception;
    }


    /**
     * Returns a {@link Failure} describing the given non-{@code null} exception.
     *
     * @param exception
     *    {@link Throwable} to store, which must be non-{@code null}
     *
     * @return {@link Failure}
     *
     * @throws IllegalArgumentException if {@code exception} is {@code null}
     * @throws Throwable if the given {@code exception} is fatal, i.e. non-recoverable
     */
    public static <T> Failure<T> of(final Throwable exception) {
        Assert.notNull(exception, "exception must be not null");
        return new Failure<>(exception);
    }


    @Override
    public boolean isSuccess() {
        return false;
    }


    @Override
    public T get() {
        return sneakyThrow(exception);
    }


    @Override
    public Throwable getException() {
        return exception;
    }


    @Override
    public boolean equals(Object obj) {
        return obj == this ||
                (obj instanceof Failure &&
                        Objects.equals(exception, ((Failure<?>) obj).exception)
                );
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(exception);
    }


    @Override
    public String toString() {
        return "Failure (" + exception + ")";
    }


    /**
     *    Returns {@code true} if given {@link Throwable} is a fatal exception should not be hidden by the {@link Try}
     * functionality.
     *
     * @param throwable
     *    {@link Throwable} to verify
     *
     * @return {@code true} if {@code throwable} is a fatal {@link Throwable},
     *         {@code false} otherwise.
     */
    private boolean isFatal(final Throwable throwable) {
        return throwable instanceof InterruptedException
                || throwable instanceof LinkageError
                || throwable instanceof ThreadDeath
                || throwable instanceof VirtualMachineError;
    }


    @SuppressWarnings("unchecked")
    private <E extends Throwable, R> R sneakyThrow(Throwable t) throws E {
        throw (E) t;
    }

}
