package com.spring5microservices.common.util.Try;

import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * The successful result of a {@link Try} operation.
 *
 * @param <T>
 *    Value type in the case of {@link Success}
 */
public final class Success<T> extends Try<T> implements Serializable {

    private static final long serialVersionUID = -1068886781251104798L;

    private final T value;

    /**
     * Construct a {@code Success}
     *
     * @param value
     *    The value of this success.
     */
    private Success(T value) {
        super();
        this.value = value;
    }


    /**
     * Returns an empty {@link Success} instance. No value is present for this {@link Success}.
     *
     * @return an empty {@link Success}
     */
    public static <T> Success<T> empty() {
        return new Success<>(null);
    }


    /**
     * Returns an {@link Success} adding the given non-{@code null} value.
     *
     * @param value
     *    The value to store, which must be non-{@code null}
     *
     * @return {@link Success}
     *
     * @throws IllegalArgumentException if {@code value} is {@code null}
     */
    public static <T> Success<T> of(final T value) {
        Assert.notNull(value, "value must be not null");
        return new Success<>(value);
    }


    /**
     *    Returns an {@link Success} describing the given {@code value}, if non-null, otherwise returns an empty
     * {@link Success}
     *
     * @param value
     *    The value to store
     *
     * @return {@link Success}
     */
    public static <T> Success<T> ofNullable(final T value) {
        return Objects.isNull(value)
                ? empty()
                : of(value);
    }


    @Override
    public boolean isSuccess() {
        return true;
    }


    @Override
    public T get() {
        return value;
    }


    @Override
    public Throwable getException() {
        throw new NoSuchElementException("Is not possible to get exception value of a 'success' Try");
    }


    @Override
    public boolean equals(Object obj) {
        return obj == this ||
                (obj instanceof Success &&
                        Objects.equals(value, ((Success<?>) obj).value)
                );
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }


    @Override
    public String toString() {
        return "Success (" + value + ")";
    }

}
