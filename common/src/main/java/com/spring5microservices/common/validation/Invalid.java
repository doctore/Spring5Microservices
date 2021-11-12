package com.spring5microservices.common.validation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * An invalid {@link Validation}.
 *
 * @param <T>
 *    Data type of the instance to validate.
 * @param <E>
 *    Value type of error
 */
public class Invalid<E, T> extends Validation<E, T> implements Serializable {

    private final Collection<E> errors;

    /**
     * Construct an {@code Invalid}.
     *
     * @param errors
     *    The value of this error.
     */
    private Invalid(Collection<E> errors) {
        super();
        this.errors = errors;
    }


    /**
     * Returns an empty {@code Invalid} instance. No {@code error} will be stored.
     *
     * @return an empty {@code Invalid}
     */
    public static <E, T> Invalid<E, T> empty() {
        return new Invalid(new ArrayList());
    }


    /**
     * Returns an {@code Invalid} adding the given {@link Collection} of errors.
     *
     * @param errors
     *    {@link Collection} of errors to include in the returned {@code Invalid}
     *
     * @return {@code Invalid}
     *
     * @throws NullPointerException if {@code error} is {@code null}
     */
    public static <E, T> Invalid<E, T> of(Collection<E> errors) {
        return new Invalid(Objects.requireNonNull(errors));
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
        return (obj == this) || (obj instanceof Invalid && Objects.equals(errors, ((Invalid<?, ?>) obj).errors));
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
