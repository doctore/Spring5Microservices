package com.spring5microservices.common.util.either;

import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * The left side of the disjoint union, as opposed to the {@link Right} side.
 *
 * @param <L>
 *    Type of the {@link Left} value of an {@link Either}
 * @param <R>
 *    Type of the {@link Right} value of an {@link Either}
 */
public final class Left<L, R> extends Either<L, R> implements Serializable {

    private static final long serialVersionUID = -7188719336108620477L;

    private final L value;


    /**
     * Construct a {@code Left}
     *
     * @param value
     *    The value of this failure.
     */
    private Left(L value) {
        super();
        this.value = value;
    }


    /**
     * Returns an empty {@link Left} instance. No value is present for this {@link Left}.
     *
     * @return an empty {@link Left}
     */
    public static <E, T> Left<E, T> empty() {
        return new Left<>(null);
    }


    /**
     * Returns an {@link Left} describing the given non-{@code null} value.
     *
     * @param value
     *    The value to store, which must be non-{@code null}
     *
     * @return {@link Left}
     *
     * @throws IllegalArgumentException if {@code value} is {@code null}
     */
    public static <L, R> Left<L, R> of(final L value) {
        Assert.notNull(value, "value must be not null");
        return new Left<>(value);
    }


    /**
     *    Returns an {@link Left} describing the given {@code value}, if non-null, otherwise returns an empty
     * {@link Left}
     *
     * @param value
     *    The value to store
     *
     * @return {@link Left}
     */
    public static <L, R> Left<L, R> ofNullable(final L value) {
        return Objects.isNull(value)
                ? empty()
                : of(value);
    }


    @Override
    public boolean isRight() {
        return false;
    }


    @Override
    public R get() {
        throw new NoSuchElementException("Is not possible to get right value of a 'left' Either");
    }


    @Override
    public L getLeft() {
        return value;
    }


    @Override
    public boolean equals(Object obj) {
        return obj == this ||
                (obj instanceof Left &&
                        Objects.equals(value, ((Left<?, ?>) obj).value)
                );
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }


    @Override
    public String toString() {
        return "Left (" + value + ")";
    }

}
