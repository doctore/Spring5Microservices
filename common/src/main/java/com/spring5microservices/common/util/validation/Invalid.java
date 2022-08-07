package com.spring5microservices.common.util.validation;

import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * An invalid {@link Validation}.
 *
 * @param <T>
 *    Type of the {@link Valid} value of an {@link Validation}
 * @param <E>
 *    Type of the {@link Invalid} value of an {@link Validation}
 */
public class Invalid<E, T> extends Validation<E, T> implements Serializable {

    private static final long serialVersionUID = 7753855546224405561L;

    private final Collection<E> errors;

    /**
     * Construct an {@link Invalid}.
     *
     * @param errors
     *    The value of this error.
     */
    private Invalid(Collection<E> errors) {
        super();
        this.errors = errors;
    }


    /**
     * Returns an empty {@link Invalid} instance. No {@code error} will be stored.
     *
     * @return an empty {@link Invalid}
     */
    public static <E, T> Invalid<E, T> empty() {
        return new Invalid<>(new ArrayList<>());
    }


    /**
     * Returns an {@link Invalid} describing the given {@link Collection} of errors.
     *
     * @param errors
     *    {@link Collection} of errors to include in the returned {@link Invalid}
     *
     * @return {@link Invalid}
     *
     * @throws IllegalArgumentException if {@code errors} is {@code null}
     */
    public static <E, T> Invalid<E, T> of(final Collection<E> errors) {
        Assert.notNull(errors, "errors must be not null");
        return new Invalid<>(errors);
    }


    /**
     *    Returns an {@link Invalid} describing the given {@link Collection} of errors, if non-null, otherwise
     * returns an empty {@link Invalid}
     *
     * @param errors
     *    {@link Collection} of errors to include in the returned {@link Invalid}
     *
     * @return {@link Invalid}
     */
    public static <E, T> Invalid<E, T> ofNullable(final Collection<E> errors) {
        return Objects.isNull(errors)
                ? empty()
                : of(errors);
    }


    @Override
    public boolean isValid() {
        return false;
    }


    @Override
    public T get() {
        throw new NoSuchElementException("Is not possible to get a value of an 'invalid' Validation");
    }


    @Override
    public Collection<E> getErrors() {
        return errors;
    }


    @Override
    public boolean equals(Object obj) {
        return obj == this ||
                (obj instanceof Invalid &&
                        Objects.equals(errors, ((Invalid<?, ?>) obj).errors)
                );
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(errors);
    }


    @Override
    public String toString() {
        return "Invalid (" + errors + ")";
    }

}
