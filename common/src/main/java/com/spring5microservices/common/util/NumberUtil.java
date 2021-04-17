package com.spring5microservices.common.util;

import lombok.experimental.UtilityClass;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
    public static int compareTo(BigDecimal one, BigDecimal two, int numberOfDecimals) {
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

}
