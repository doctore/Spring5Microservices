package com.spring5microservices.common.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static java.lang.Math.abs;

@UtilityClass
public class DateTimeUtil {

    /**
     * Compare provided {@link LocalDateTime}s taking into account the given {@code epsilon} and {@code timeUnit}.
     *
     * @param one
     *    {@link LocalDateTime} of the "left side" of compare method
     * @param two
     *    {@link LocalDateTime} of the "right side" of compare method
     * @param epsilon
     *    Timeframe used to consider equals two {@link LocalDateTime} values
     * @param timeUnit
     *    {@link ChronoUnit} of the given {@code epsilon}. If no value is given, {@link ChronoUnit#MINUTES} will be used.
     *
     * @return using {@code epsilon} as value in {@link ChronoUnit} format:
     *          -1 if {@code one} is before than {@code two}
     *           0 if both are equals
     *           1 if {@code one} is after than {@code two}
     *
     * @throws IllegalArgumentException if {@code epsilon} is less than {@code zero}
     */
    public static int compare(LocalDateTime one, LocalDateTime two, long epsilon, ChronoUnit timeUnit) {
        if (0 > epsilon) {
            throw new IllegalArgumentException("epsilon must be equals or greater than 0");
        }
        if (Objects.isNull(one)) {
            return null == two ? 0 : -1;
        }
        if (Objects.isNull(two)) {
            return 1;
        }
        if (0 == epsilon) {
            return one.compareTo(two);
        }
        ChronoUnit finalTimeUnit = Objects.nonNull(timeUnit)
                ? timeUnit
                : ChronoUnit.MINUTES;
        long difference = finalTimeUnit.between(one, two);
        return abs(difference) <= epsilon
                ? 0
                : 0 < difference ? -1 : 1;
    }

}
