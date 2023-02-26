package com.spring5microservices.common.util;

import lombok.experimental.UtilityClass;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

import static com.spring5microservices.common.util.ObjectUtil.getOrElse;
import static java.lang.Math.abs;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;

@UtilityClass
public class DateTimeUtil {

    /**
     * Compares provided {@link LocalDateTime}s taking into account the given {@code epsilon} and {@code timeUnit}.
     *
     * @param one
     *    {@link LocalDateTime} of the "left side" of compare method
     * @param two
     *    {@link LocalDateTime} of the "right side" of compare method
     * @param epsilon
     *    Timeframe used to consider equals two {@link LocalDateTime} values
     * @param timeUnit
     *    {@link ChronoUnit} of the given {@code epsilon}. If {@code null} then {@link ChronoUnit#MINUTES} will be used.
     *
     * @return using {@code epsilon} as value in {@link ChronoUnit} format:
     *          -1 if {@code one} is before than {@code two}
     *           0 if both are equals
     *           1 if {@code one} is after than {@code two}
     *
     * @throws IllegalArgumentException if {@code epsilon} is less than {@code zero}
     */
    public static int compare(final LocalDateTime one,
                              final LocalDateTime two,
                              final long epsilon,
                              final ChronoUnit timeUnit) {
        Assert.isTrue(0 <= epsilon, "epsilon must be equals or greater than 0");
        if (isNull(one)) {
            return null == two
                    ? 0
                    : -1;
        }
        if (isNull(two)) {
            return 1;
        }
        if (0 == epsilon) {
            return one.compareTo(two);
        }
        final ChronoUnit finalTimeUnit = getOrElse(
                timeUnit,
                ChronoUnit.MINUTES
        );
        long difference = finalTimeUnit.between(one, two);
        return abs(difference) <= epsilon
                ? 0
                : 0 < difference ? -1 : 1;
    }


    /**
     * Converts to an instance of {@link Date} the given {@link LocalDateTime} using {@link ZoneId#systemDefault()}
     *
     * @param localDateTime
     *    {@link LocalDateTime} value to convert
     *
     * @return {@link Optional} of {@link Date}
     */
    public static Optional<Date> fromLocalDateTimeToDate(final LocalDateTime localDateTime) {
        return fromLocalDateTimeToDate(
                localDateTime,
                ZoneId.systemDefault()
        );
    }


    /**
     * Converts to an instance of {@link Date} the given {@link LocalDateTime} using the provided {@link ZoneId}
     *
     * @param localDateTime
     *    {@link LocalDateTime} value to convert
     * @param zoneId
     *    {@link ZoneId} used in the conversion
     *
     * @return {@link Optional} of {@link Date}
     */
    public static Optional<Date> fromLocalDateTimeToDate(final LocalDateTime localDateTime,
                                                         final ZoneId zoneId) {
        return ofNullable(localDateTime)
                .map(lcd -> {
                    final ZoneId finalZoneId = getOrElse(
                            zoneId,
                            ZoneId.systemDefault()
                    );
                    return Date.from(
                            lcd.atZone(finalZoneId).toInstant()
                    );
                });
    }


    /**
     * Converts to an instance of {@link LocalDateTime} the given {@link Date} using {@link ZoneId#systemDefault()}
     *
     * @param date
     *    {@link Date} value to convert
     *
     * @return {@link Optional} of {@link LocalDateTime}
     */
    public static Optional<LocalDateTime> fromDateToLocalDateTime(final Date date) {
        return fromDateToLocalDateTime(
                date,
                ZoneId.systemDefault()
        );
    }


    /**
     * Converts to an instance of {@link LocalDateTime} the given {@link Date} using the provided {@link ZoneId}
     *
     * @param date
     *    {@link Date} value to convert
     * @param zoneId
     *    {@link ZoneId} used in the conversion
     *
     * @return {@link Optional} of {@link LocalDateTime}
     */
    public static Optional<LocalDateTime> fromDateToLocalDateTime(final Date date,
                                                                  final ZoneId zoneId) {
        return ofNullable(date)
                .map(d -> {
                    final ZoneId finalZoneId = getOrElse(
                            zoneId,
                            ZoneId.systemDefault()
                    );
                    return d.toInstant()
                            .atZone(finalZoneId)
                            .toLocalDateTime();
                });
    }

}
