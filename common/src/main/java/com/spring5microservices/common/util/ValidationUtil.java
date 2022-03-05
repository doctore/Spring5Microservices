package com.spring5microservices.common.util;

import com.spring5microservices.common.validation.Invalid;
import com.spring5microservices.common.validation.Valid;
import com.spring5microservices.common.validation.Validation;
import lombok.experimental.UtilityClass;
import org.springframework.util.ObjectUtils;

import java.util.function.Supplier;

@UtilityClass
public class ValidationUtil {

    /**
     * Merges the given {@link Validation} in a result one that will be:
     *
     *   1. {@link Valid} instance with all given {@code validations} are {@link Valid} ones or such parameters is {@code null} or empty.
     *   2. {@link Invalid} instance if there is at least one {@link Invalid} in the given {@code validations}. In this case, errors of
     *      all provided {@link Invalid}s will be included in the result.
     *
     * @param validations
     *    {@link Validation} instances to combine
     *
     * @return {@link Validation}
     */
    @SafeVarargs
    public static <E, T> Validation<E, T> combine(final Validation<E, T>... validations) {
        Validation<E, T> result = Valid.empty();
        if (!ObjectUtils.isEmpty(validations)) {
            for (Validation<E, T> validation : validations) {
                result = result.ap(validation);
            }
        }
        return result;
    }


    /**
     *    Checks the given {@link Supplier} of {@link Validation}, returning a {@link Valid} instance if no {@link Invalid}
     * {@link Supplier} was given or the first {@link Invalid} one.
     *
     * @param suppliers
     *    {@link Supplier} of {@link Validation} instances to verify
     *
     * @return {@link Validation}
     */
    @SafeVarargs
    public static <E, T> Validation<E, T> getFirstInvalid(final Supplier<Validation<E, T>>... suppliers) {
        Validation<E, T> result = Valid.empty();
        if (!ObjectUtils.isEmpty(suppliers)) {
            for (Supplier<Validation<E, T>> supplier : suppliers) {
                result = result.ap(supplier.get());
                if (!result.isValid()) {
                    return result;
                }
            }
        }
        return result;
    }

}