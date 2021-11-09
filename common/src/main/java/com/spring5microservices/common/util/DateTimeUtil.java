package com.spring5microservices.common.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import static java.lang.Math.abs;
import static java.util.Optional.ofNullable;

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
    public static int compare(final LocalDateTime one, final LocalDateTime two, long epsilon, final ChronoUnit timeUnit) {
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


    /**
     * Convert to an instance of {@link Date} the given {@link LocalDateTime} using the provided {@link ZoneId}
     *
     * @param localDateTime
     *    {@link LocalDateTime} value to convert
     * @param zoneId
     *    {@link ZoneId} used in the conversion
     *
     * @return {@link Optional} of {@link Date}
     */
    public static Optional<Date> fromLocalDateTimeToDate(final LocalDateTime localDateTime, final ZoneId zoneId) {
        return ofNullable(localDateTime)
                .map(lcd -> {
                    ZoneId finalZoneId = Objects.nonNull(zoneId)
                            ? zoneId
                            : ZoneId.systemDefault();
                    return Date.from(
                            lcd.atZone(finalZoneId).toInstant()
                    );
                });
    }


    /**
     * Convert to an instance of {@link Date} the given {@link LocalDateTime} using {@link ZoneId#systemDefault()}
     *
     * @param localDateTime
     *    {@link LocalDateTime} value to convert
     *
     * @return {@link Optional} of {@link Date}
     */
    public static Optional<Date> fromLocalDateTimeToDate(LocalDateTime localDateTime) {
        return fromLocalDateTimeToDate(localDateTime, ZoneId.systemDefault());
    }


    /**
     * Convert to an instance of {@link LocalDateTime} the given {@link Date} using the provided {@link ZoneId}
     *
     * @param date
     *    {@link Date} value to convert
     * @param zoneId
     *    {@link ZoneId} used in the conversion
     *
     * @return {@link Optional} of {@link LocalDateTime}
     */
    public static Optional<LocalDateTime> fromDateToLocalDateTime(final Date date, final ZoneId zoneId) {
        return ofNullable(date)
                .map(d -> {
                    ZoneId finalZoneId = Objects.nonNull(zoneId)
                            ? zoneId
                            : ZoneId.systemDefault();
                    return d.toInstant()
                            .atZone(finalZoneId)
                            .toLocalDateTime();
                });
    }


    /**
     * Convert to an instance of {@link LocalDateTime} the given {@link Date} using {@link ZoneId#systemDefault()}
     *
     * @param date
     *    {@link Date} value to convert
     *
     * @return {@link Optional} of {@link LocalDateTime}
     */
    public static Optional<LocalDateTime> fromDateToLocalDateTime(final Date date) {
        return fromDateToLocalDateTime(date, ZoneId.systemDefault());
    }

}
