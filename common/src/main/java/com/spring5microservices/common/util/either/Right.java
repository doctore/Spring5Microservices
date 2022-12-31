package com.spring5microservices.common.util.either;

import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.Objects;

import static java.util.Objects.isNull;

/**
 * The right side of the disjoint union, as opposed to the {@link Left} side.
 *
 * @param <L>
 *    Type of the {@link Left} value of an {@link Either}
 * @param <R>
 *    Type of the {@link Right} value of an {@link Either}
 */
public final class Right<L, R> extends Either<L, R> implements Serializable {

    private static final long serialVersionUID = -8035623396412649449L;

    private final R value;

    /**
     * Construct a {@code Right}
     *
     * @param value
     *    The value of this success.
     */
    private Right(R value) {
        super();
        this.value = value;
    }


    /**
     * Returns an empty {@link Right} instance. No value is present for this {@link Right}.
     *
     * @return an empty {@link Right}
     */
    public static <E, T> Right<E, T> empty() {
        return new Right<>(null);
    }


    /**
     * Returns an {@link Right} adding the given non-{@code null} value.
     *
     * @param value
     *    The value to store, which must be non-{@code null}
     *
     * @return {@link Right}
     *
     * @throws IllegalArgumentException if {@code value} is {@code null}
     */
    public static <L, R> Right<L, R> of(final R value) {
        Assert.notNull(value, "value must be not null");
        return new Right<>(value);
    }


    /**
     *    Returns an {@link Right} describing the given {@code value}, if non-null, otherwise returns an empty
     * {@link Right}
     *
     * @param value
     *    The value to store
     *
     * @return {@link Right}
     */
    public static <L, R> Right<L, R> ofNullable(final R value) {
        return isNull(value)
                ? empty()
                : of(value);
    }


    @Override
    public boolean isRight() {
        return true;
    }


    @Override
    public R get() {
        return value;
    }


    @Override
    public L getLeft() {
        throw new NoSuchElementException("Is not possible to get left value of a 'right' Either");
    }


    @Override
    public boolean equals(Object obj) {
        return obj == this ||
                (obj instanceof Right &&
                        Objects.equals(value, ((Right<?, ?>) obj).value)
                );
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }


    @Override
    public String toString() {
        return "Right (" + value + ")";
    }

}
