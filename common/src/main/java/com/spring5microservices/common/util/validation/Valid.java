package com.spring5microservices.common.util.validation;

import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Objects;

import static java.util.Objects.isNull;

/**
 * An valid {@link Validation}.
 *
 * @param <T>
 *    Type of the {@link Valid} value of an {@link Validation}
 * @param <E>
 *    Type of the {@link Invalid} value of an {@link Validation}
 */
public final class Valid<E, T> extends Validation<E, T> implements Serializable {

    private static final long serialVersionUID = 8596367511219466842L;

    private final T value;

    /**
     * Construct a {@link Valid}
     *
     * @param value
     *    The value of this success.
     */
    private Valid(T value) {
        super();
        this.value = value;
    }


    /**
     * Returns an empty {@link Valid} instance. No value is present for this {@link Valid}.
     *
     * @return an empty {@link Valid}
     */
    public static <E, T> Valid<E, T> empty() {
        return new Valid<>(null);
    }


    /**
     * Returns a {@link Valid} describing the given non-{@code null} value.
     *
     * @param value
     *    The value to store, which must be non-{@code null}
     *
     * @return {@link Valid}
     *
     * @throws IllegalArgumentException if {@code value} is {@code null}
     */
    public static <E, T> Valid<E, T> of(final T value) {
        Assert.notNull(value, "value must be not null");
        return new Valid<>(value);
    }


    /**
     *    Returns a {@link Valid} describing the given {@code value}, if non-null, otherwise returns an empty
     * {@link Valid}
     *
     * @param value
     *    The value to store,
     *
     * @return {@link Valid}
     */
    public static <E, T> Valid<E, T> ofNullable(final T value) {
        return isNull(value)
                ? empty()
                : of(value);
    }


    @Override
    public boolean isValid() {
        return true;
    }


    @Override
    public T get() {
        return value;
    }


    @Override
    public Collection<E> getErrors() {
        throw new NoSuchElementException("Is not possible to get error of a 'valid' Validation");
    }


    @Override
    public boolean equals(Object obj) {
        return obj == this ||
                (obj instanceof Valid &&
                        Objects.equals(value, ((Valid<?, ?>) obj).value)
                );
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }


    @Override
    public String toString() {
        return "Valid (" + value + ")";
    }

}
