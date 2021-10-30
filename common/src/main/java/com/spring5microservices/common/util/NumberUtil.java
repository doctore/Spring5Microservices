package com.spring5microservices.common.util;

import lombok.experimental.UtilityClass;
import org.springframework.util.Assert;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@UtilityClass
public class NumberUtil {

    /**
     *    Compare provided {@link BigDecimal}s taking into account the number of decimals included in the parameter
     * {@code numberOfDecimals}.
     *
     * @param one
     *    {@link BigDecimal} of the "left side" of compare method
     * @param two
     *    {@link BigDecimal} of the "right side" of compare method
     * @param numberOfDecimals
     *    Number of decimals used for comparison
     *
     * @return {@code one#compareTo(two)} using {@code numberOfDecimals} as precision.
     *
     * @throws IllegalArgumentException if {@code numberOfDecimals} is less than {@code zero}
     */
    public static int compareTo(final BigDecimal one, BigDecimal two, int numberOfDecimals) {
        Assert.isTrue(0 <= numberOfDecimals, "numberOfDecimals must be equals or greater than 0");
        if (null == one) {
            return null == two ? 0 : -1;
        }
        if (null == two) {
            return 1;
        }
        BigDecimal oneWithProvidedPrecision = one.setScale(numberOfDecimals, RoundingMode.HALF_UP);
        BigDecimal twoWithProvidedPrecision = two.setScale(numberOfDecimals, RoundingMode.HALF_UP);
        return oneWithProvidedPrecision.compareTo(twoWithProvidedPrecision);
    }


    /**
     * Return an instance of the provided {@link Class} if is possible to do the conversion of given {@code potentialNumber}.
     *
     * @param potentialNumber
     *    {@link String} to convert into a {@link Number} instance.
     *
     * @param clazzReturnedInstance
     *    {@link Number} subclass of the returned instance.
     *
     * @return {@link Optional} with a value if provided {@code potentialNumber} could be converted,
     *         {@link Optional#empty()} otherwise.
     *
     * @throws IllegalArgumentException if {@code clazzReturnedInstance} is {@code null}
     */
    public static <T extends Number> Optional<T> fromString(final String potentialNumber, final Class<T> clazzReturnedInstance) {
        if (null == clazzReturnedInstance) {
            throw new IllegalArgumentException("clazzReturnedInstance must be not null");
        }
        return ofNullable(potentialNumber)
                .map(s -> {
                    try {
                        Constructor<T> ctor = clazzReturnedInstance.getConstructor(String.class);
                        return ctor.newInstance(new Object[] { s });
                    }
                    catch (Exception e) {
                        return null;
                    }
                });
    }


    /**
     * Return an {@link Integer} instance if is possible to do the conversion of given {@code potentialNumber}.
     *
     * @param potentialNumber
     *    {@link String} to convert into a {@link Integer} instance.
     *
     * @return {@link Optional} of {@link Integer} if provided {@code potentialNumber} could be converted,
     *         {@link Optional#empty()} otherwise.
     */
    public static Optional<Integer> fromString(final String potentialNumber) {
        return ofNullable(potentialNumber)
                .map(s -> {
                    try {
                        return Integer.valueOf(potentialNumber);
                    }
                    catch (Exception e) {
                        return null;
                    }
                });
    }

}
