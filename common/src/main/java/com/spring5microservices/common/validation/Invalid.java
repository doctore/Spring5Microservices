package com.spring5microservices.common.validation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * An invalid {@link Validation}.
 *
 * @param <T>
 *    Data type of the instance to validate.
 */
public class Invalid<T> extends Validation<T> implements Serializable {

    private final List<String> errors;

    /**
     * Construct an {@code Invalid}.
     *
     * @param errors
     *    The value of this error.
     */
    private Invalid(List<String> errors) {
        super();
        this.errors = errors;
    }


    /**
     * Returns an empty {@code Invalid} instance. No errors will be stored.
     *
     * @return an empty {@code Invalid}
     */
    public static <T> Invalid<T> empty() {
        return new Invalid(new ArrayList<>());
    }


    /**
     * Returns an {@code Invalid} adding the given {@link List} of errors.
     *
     * @param errors
     *    {@link List} of errors to include in the returned {@code Invalid}
     *
     * @return {@code Invalid}
     *
     * @throws NullPointerException if errors is {@code null}
     */
    public static <T> Invalid<T> of(List<String> errors) {
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
    public List<String> getErrors() {
        return errors;
    }


    @Override
    public boolean equals(Object obj) {
        return (obj == this) || (obj instanceof Invalid && Objects.equals(errors, ((Invalid<?>) obj).errors));
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
