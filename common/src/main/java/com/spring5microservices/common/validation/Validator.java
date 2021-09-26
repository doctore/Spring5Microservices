package com.spring5microservices.common.validation;

/**
 * Manages the validation of provided instances, through the functionality provided by {@link Validation} class.
 */
public interface Validator<T> {

    /**
     *    Validates the provided {@code toValidate}, returning an instance of {@link Valid} if there are no errors in
     * the given parameter. Otherwise returns an {@link Invalid} one.
     *
     * @param toValidate
     *    Object to validate.
     *
     * @return {@link Validation}
     */
    Validation<T> validate(T toValidate);

}
