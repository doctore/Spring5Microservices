package com.spring5microservices.common.util;

import com.spring5microservices.common.collection.tuple.Tuple2;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static com.spring5microservices.common.util.ObjectUtil.getOrElse;
import static java.lang.Math.abs;
import static java.util.Objects.isNull;

@UtilityClass
public class DateTimeUtil {


    /**
     * Compares provided {@link Date}s taking into account the given {@code epsilon} and {@code timeUnit}.
     *
     * @param one
     *    {@link Date} of the "left side" of compare method
     * @param two
     *    {@link Date} of the "right side" of compare method
     * @param epsilon
     *    Timeframe used to consider equals two {@link Date} values. If less than zero then 0 will be used
     * @param timeUnit
     *    {@link ChronoUnit} of the given {@code epsilon}. If {@code null} then {@link ChronoUnit#MINUTES} will be used
     *
     * @return using {@code epsilon} as value in {@link ChronoUnit} format:
     *          -1 if {@code one} is before than {@code two}
     *           0 if both are equals
     *           1 if {@code one} is after than {@code two}
     */
    public static int compare(final Date one,
                              final Date two,
                              final long epsilon,
                              final ChronoUnit timeUnit) {
        if (isNull(one)) {
            return isNull(two)
                    ? 0
                    : -1;
        }
        if (isNull(two)) {
            return 1;
        }
        final long finalEpsilon = 0 < epsilon
                ? epsilon
                : 0L;
        if (0 == finalEpsilon) {
            return one.compareTo(two);
        }
        return compare(
                DateTimeUtil.fromDateToLocalDateTime(one),
                DateTimeUtil.fromDateToLocalDateTime(two),
                finalEpsilon,
                timeUnit
        );
    }


    /**
     * Compares provided {@link LocalDateTime}s taking into account the given {@code epsilon} and {@code timeUnit}.
     *
     * @param one
     *    {@link LocalDateTime} of the "left side" of compare method
     * @param two
     *    {@link LocalDateTime} of the "right side" of compare method
     * @param epsilon
     *    Timeframe used to consider equals two {@link LocalDateTime} values. If less than zero then 0 will be used
     * @param timeUnit
     *    {@link ChronoUnit} of the given {@code epsilon}. If {@code null} then {@link ChronoUnit#MINUTES} will be used
     *
     * @return using {@code epsilon} as value in {@link ChronoUnit} format:
     *          -1 if {@code one} is before than {@code two}
     *           0 if both are equals
     *           1 if {@code one} is after than {@code two}
     */
    public static int compare(final LocalDateTime one,
                              final LocalDateTime two,
                              final long epsilon,
                              final ChronoUnit timeUnit) {
        if (isNull(one)) {
            return isNull(two)
                    ? 0
                    : -1;
        }
        if (isNull(two)) {
            return 1;
        }
        final long finalEpsilon = 0 < epsilon
                ? epsilon
                : 0L;
        if (0 == finalEpsilon) {
            return one.compareTo(two);
        }
        final ChronoUnit finalTimeUnit = getOrElse(
                timeUnit,
                ChronoUnit.MINUTES
        );
        long difference = finalTimeUnit.between(one, two);
        return abs(difference) <= finalEpsilon
                ? 0
                : 0 < difference ? -1 : 1;
    }


    /**
     * Converts to an instance of {@link Date} the given {@link LocalDateTime} using {@link ZoneId#systemDefault()}
     *
     * @param sourceLocalDateTime
     *    {@link LocalDateTime} value to convert. If {@code null} then {@link LocalDateTime#now()} will be used
     *
     * @return {@link Date}
     */
    public static Date fromLocalDateTimeToDate(final LocalDateTime sourceLocalDateTime) {
        return fromLocalDateTimeToDate(
                sourceLocalDateTime,
                ZoneId.systemDefault()
        );
    }


    /**
     * Converts to an instance of {@link Date} the given {@link LocalDateTime} using the provided {@link ZoneId}
     *
     * @param sourceLocalDateTime
     *    {@link LocalDateTime} value to convert. If {@code null} then {@link LocalDateTime#now()} with {@code zoneId} will be used
     * @param zoneId
     *    {@link ZoneId} used in the conversion. If {@code null} then new {@link ZoneId#systemDefault()} will be used
     *
     * @return {@link Date}
     */
    public static Date fromLocalDateTimeToDate(final LocalDateTime sourceLocalDateTime,
                                               final ZoneId zoneId) {
        final ZoneId finalZoneId = getOrElse(
                zoneId,
                ZoneId.systemDefault()
        );
        final LocalDateTime finalSourceLocalDateTime = getOrElse(
                sourceLocalDateTime,
                LocalDateTime.now(finalZoneId)
        );
        return Date.from(
                finalSourceLocalDateTime
                        .atZone(finalZoneId)
                        .toInstant()
        );
    }


    /**
     * Converts to an instance of {@link LocalDateTime} the given {@link Date} using {@link ZoneId#systemDefault()}
     *
     * @param sourceDate
     *    {@link Date} value to convert. If {@code null} then new {@link Date} will be used
     *
     * @return {@link LocalDateTime}
     */
    public static LocalDateTime fromDateToLocalDateTime(final Date sourceDate) {
        return fromDateToLocalDateTime(
                sourceDate,
                ZoneId.systemDefault()
        );
    }


    /**
     * Converts to an instance of {@link LocalDateTime} the given {@link Date} using the provided {@link ZoneId}
     *
     * @param sourceDate
     *    {@link Date} value to convert. If {@code null} then new {@link Date} will be used
     * @param zoneId
     *    {@link ZoneId} used in the conversion. If {@code null} then new {@link ZoneId#systemDefault()} will be used
     *
     * @return {@link LocalDateTime}
     */
    public static LocalDateTime fromDateToLocalDateTime(final Date sourceDate,
                                                        final ZoneId zoneId) {
        final ZoneId finalZoneId = getOrElse(
                zoneId,
                ZoneId.systemDefault()
        );
        final Date finalSourceDate = getOrElse(
                sourceDate,
                new Date()
        );
        return finalSourceDate.toInstant()
                .atZone(finalZoneId)
                .toLocalDateTime();
    }


    /**
     * Returns a {@link Tuple2} with the interval:
     * <p>
     *   1. [ sourceDate - difference, sourceDate ]                if difference is lower than 0.
     *   2. [ sourceDate,              sourceDate + difference ]   if difference is greater than 0
     *
     * @param sourceDate
     *    {@link Date} value from which to add/subtract the specified {@code difference}. If {@code null} then new {@link Date} will be used
     * @param difference
     *    How much time we need to add/subtract to the provided {@code sourceDate}.
     * @param timeUnit
     *    {@link ChronoUnit} of the given {@code valueToSubtract}. If {@code null} then {@link ChronoUnit#MINUTES} will be used.
     *
     * @return {@link Tuple2} with the interval
     */
    public static Tuple2<Date, Date> getDateIntervalFromGiven(final Date sourceDate,
                                                              final long difference,
                                                              final ChronoUnit timeUnit) {
        return getDateIntervalFromGiven(
                sourceDate,
                difference,
                timeUnit,
                ZoneId.systemDefault()
        );
    }


    /**
     * Returns a {@link Tuple2} with the interval:
     * <p>
     *   1. [ sourceDate - difference, sourceDate ]                if difference is lower than 0.
     *   2. [ sourceDate,              sourceDate + difference ]   if difference is greater than 0
     *
     * @param sourceDate
     *    {@link Date} value from which to add/subtract the specified {@code difference}. If {@code null} then new {@link Date} will be used
     * @param difference
     *    How much time we need to add/subtract to the provided {@code sourceDate}.
     * @param timeUnit
     *    {@link ChronoUnit} of the given {@code valueToSubtract}. If {@code null} then {@link ChronoUnit#MINUTES} will be used.
     * @param zoneId
     *    {@link ZoneId} used in the conversion. If {@code null} then new {@link ZoneId#systemDefault()} will be used
     *
     * @return {@link Tuple2} with the interval
     */
    public static Tuple2<Date, Date> getDateIntervalFromGiven(final Date sourceDate,
                                                              final long difference,
                                                              final ChronoUnit timeUnit,
                                                              final ZoneId zoneId) {
        Tuple2<LocalDateTime, LocalDateTime> localDateTimeInterval = getLocalDateTimeIntervalFromGiven(
                fromDateToLocalDateTime(
                        sourceDate,
                        zoneId
                ),
                difference,
                timeUnit,
                zoneId
        );
        return Tuple2.of(
                fromLocalDateTimeToDate(
                        localDateTimeInterval._1,
                        zoneId
                ),
                fromLocalDateTimeToDate(
                        localDateTimeInterval._2,
                        zoneId
                )
        );
    }


    /**
     * Returns a {@link Tuple2} with the interval:
     * <p>
     *   1. [ sourceLocalDateTime - difference, sourceLocalDateTime ]                if difference is lower than 0.
     *   2. [ sourceLocalDateTime,              sourceLocalDateTime + difference ]   if difference is greater than 0
     *
     * @param sourceLocalDateTime
     *    {@link LocalDateTime} value from which to add/subtract the specified {@code difference}. If {@code null} then {@link LocalDateTime#now()} will be used
     * @param difference
     *    How much time we need to add/subtract to the provided {@code sourceDate}.
     * @param timeUnit
     *    {@link ChronoUnit} of the given {@code valueToSubtract}. If {@code null} then {@link ChronoUnit#MINUTES} will be used.
     *
     * @return {@link Tuple2} with the interval
     */
    public static Tuple2<LocalDateTime, LocalDateTime> getLocalDateTimeIntervalFromGiven(final LocalDateTime sourceLocalDateTime,
                                                                                         final long difference,
                                                                                         final ChronoUnit timeUnit) {
        return getLocalDateTimeIntervalFromGiven(
                sourceLocalDateTime,
                difference,
                timeUnit,
                ZoneId.systemDefault()
        );
    }


    /**
     * Returns a {@link Tuple2} with the interval:
     * <p>
     *   1. [ sourceLocalDateTime - difference, sourceLocalDateTime ]                if difference is lower than 0.
     *   2. [ sourceLocalDateTime,              sourceLocalDateTime + difference ]   if difference is greater than 0
     *
     * @param sourceLocalDateTime
     *    {@link LocalDateTime} value from which to add/subtract the specified {@code difference}. If {@code null} then {@link LocalDateTime#now()} will be used
     * @param difference
     *    How much time we need to add/subtract to the provided {@code sourceDate}.
     * @param timeUnit
     *    {@link ChronoUnit} of the given {@code valueToSubtract}. If {@code null} then {@link ChronoUnit#MINUTES} will be used.
     * @param zoneId
     *    {@link ZoneId} used in the conversion. If {@code null} then new {@link ZoneId#systemDefault()} will be used
     *
     * @return {@link Tuple2} with the interval
     */
    public static Tuple2<LocalDateTime, LocalDateTime> getLocalDateTimeIntervalFromGiven(final LocalDateTime sourceLocalDateTime,
                                                                                         final long difference,
                                                                                         final ChronoUnit timeUnit,
                                                                                         final ZoneId zoneId) {
        final ZoneId finalZoneId = getOrElse(
                zoneId,
                ZoneId.systemDefault()
        );
        final LocalDateTime finalSourceLocalDateTime = getOrElse(
                sourceLocalDateTime,
                LocalDateTime.now(finalZoneId)
        )
        .atZone(finalZoneId)
        .toLocalDateTime();

        return 0 > difference
                ? Tuple2.of(
                        minus(
                                finalSourceLocalDateTime,
                                Math.abs(difference),
                                timeUnit,
                                finalZoneId
                        ),
                        finalSourceLocalDateTime
                  )
                : Tuple2.of(
                        finalSourceLocalDateTime,
                        plus(
                                finalSourceLocalDateTime,
                                difference,
                                timeUnit,
                                finalZoneId
                        )
                  );
    }


    /**
     *    Returns a {@link Date} based on {@code sourceDate} with the specified {@code amountToSubtract} subtracted,
     * in terms of {@code timeUnit}.
     *
     * @param sourceDate
     *    {@link Date} value from which to subtract the specified {@code amountToSubtract}. If {@code null} then new {@link Date} will be used
     * @param amountToSubtract
     *    The amount of the {@code timeUnit} to subtract from {@code sourceDate}. If less than zero then 1 will be used
     * @param timeUnit
     *    {@link ChronoUnit} of the given {@code amountToSubtract}. If {@code null} then {@link ChronoUnit#MINUTES} will be used
     *
     * @return {@link Date}
     */
    public static Date minus(final Date sourceDate,
                             final long amountToSubtract,
                             final ChronoUnit timeUnit) {
        return minus(
                sourceDate,
                amountToSubtract,
                timeUnit,
                ZoneId.systemDefault()
        );
    }


    /**
     *    Returns a {@link Date} based on {@code sourceDate} with the specified {@code amountToSubtract} subtracted,
     * in terms of {@code timeUnit}.
     *
     * @param sourceDate
     *    {@link Date} value from which to subtract the specified {@code amountToSubtract}. If {@code null} then new {@link Date} will be used
     * @param amountToSubtract
     *    The amount of the {@code timeUnit} to subtract from {@code sourceDate}. If less than zero then 1 will be used
     * @param timeUnit
     *    {@link ChronoUnit} of the given {@code amountToSubtract}. If {@code null} then {@link ChronoUnit#MINUTES} will be used
     * @param zoneId
     *    {@link ZoneId} used in the conversion. If {@code null} then new {@link ZoneId#systemDefault()} will be used
     *
     * @return {@link Date}
     */
    public static Date minus(final Date sourceDate,
                             final long amountToSubtract,
                             final ChronoUnit timeUnit,
                             final ZoneId zoneId) {
        return fromLocalDateTimeToDate(
                minus(
                        fromDateToLocalDateTime(sourceDate, zoneId),
                        amountToSubtract,
                        timeUnit,
                        zoneId
                ),
                zoneId
        );
    }


    /**
     *    Returns a {@link LocalDateTime} based on {@code sourceLocalDateTime} with the specified {@code amountToSubtract}
     * subtracted, in terms of {@code timeUnit}.
     *
     * @param sourceLocalDateTime
     *    {@link LocalDateTime} value from which to subtract the specified {@code amountToSubtract}. If {@code null} then {@link LocalDateTime#now()} will be used
     * @param amountToSubtract
     *    The amount of the {@code timeUnit} to subtract from {@code sourceLocalDateTime}. If less than zero then 1 will be used
     * @param timeUnit
     *    {@link ChronoUnit} of the given {@code amountToSubtract}. If {@code null} then {@link ChronoUnit#MINUTES} will be used
     *
     * @return {@link LocalDateTime}
     */
    public static LocalDateTime minus(final LocalDateTime sourceLocalDateTime,
                                      final long amountToSubtract,
                                      final ChronoUnit timeUnit) {
        return minus(
                sourceLocalDateTime,
                amountToSubtract,
                timeUnit,
                ZoneId.systemDefault()
        );
    }


    /**
     *    Returns a {@link LocalDateTime} based on {@code sourceLocalDateTime} with the specified {@code amountToSubtract}
     * subtracted, in terms of {@code timeUnit}.
     *
     * @param sourceLocalDateTime
     *    {@link LocalDateTime} value from which to subtract the specified {@code amountToSubtract}. If {@code null} then {@link LocalDateTime#now()} will be used
     * @param amountToSubtract
     *    The amount of the {@code timeUnit} to subtract from {@code sourceLocalDateTime}. If less than zero then 1 will be used
     * @param timeUnit
     *    {@link ChronoUnit} of the given {@code amountToSubtract}. If {@code null} then {@link ChronoUnit#MINUTES} will be used
     * @param zoneId
     *    {@link ZoneId} used in the conversion. If {@code null} then new {@link ZoneId#systemDefault()} will be used
     *
     * @return {@link LocalDateTime}
     */
    public static LocalDateTime minus(final LocalDateTime sourceLocalDateTime,
                                      final long amountToSubtract,
                                      final ChronoUnit timeUnit,
                                      final ZoneId zoneId) {
        final ZoneId finalZoneId = getOrElse(
                zoneId,
                ZoneId.systemDefault()
        );
        final LocalDateTime finalSourceLocalDateTime = getOrElse(
                sourceLocalDateTime,
                LocalDateTime.now()
        );
        final ChronoUnit finalTimeUnit = getOrElse(
                timeUnit,
                ChronoUnit.MINUTES
        );
        final long finalAmountToSubtract = 0 <= amountToSubtract
                ? amountToSubtract
                : 1L;

        return finalSourceLocalDateTime
                .atZone(finalZoneId)
                .minus(
                        finalAmountToSubtract,
                        finalTimeUnit
                )
                .toLocalDateTime();
    }


    /**
     *    Returns a {@link Date} based on {@code sourceLocalDateTime} with the specified {@code amountToAdd} added,
     * in terms of {@code timeUnit}.
     *
     * @param sourceDate
     *    {@link Date} value from which to add the specified {@code amountToAdd}. If {@code null} then new {@link Date} will be used
     * @param amountToAdd
     *    The amount of the {@code timeUnit} to add from {@code sourceDate}. If less than zero then 1 will be used
     * @param timeUnit
     *    {@link ChronoUnit} of the given {@code amountToAdd}. If {@code null} then {@link ChronoUnit#MINUTES} will be used
     *
     * @return {@link Date}
     */
    public static Date plus(final Date sourceDate,
                            final long amountToAdd,
                            final ChronoUnit timeUnit) {
        return plus(
                sourceDate,
                amountToAdd,
                timeUnit,
                ZoneId.systemDefault()
        );
    }


    /**
     *    Returns a {@link Date} based on {@code sourceLocalDateTime} with the specified {@code amountToAdd} added,
     * in terms of {@code timeUnit}.
     *
     * @param sourceDate
     *    {@link Date} value from which to add the specified {@code amountToAdd}. If {@code null} then new {@link Date} will be used
     * @param amountToAdd
     *    The amount of the {@code timeUnit} to add from {@code sourceDate}. If less than zero then 1 will be used
     * @param timeUnit
     *    {@link ChronoUnit} of the given {@code amountToAdd}. If {@code null} then {@link ChronoUnit#MINUTES} will be used
     * @param zoneId
     *    {@link ZoneId} used in the conversion. If {@code null} then new {@link ZoneId#systemDefault()} will be used
     *
     * @return {@link Date}
     */
    public static Date plus(final Date sourceDate,
                            final long amountToAdd,
                            final ChronoUnit timeUnit,
                            final ZoneId zoneId) {
        return fromLocalDateTimeToDate(
                plus(
                        fromDateToLocalDateTime(sourceDate, zoneId),
                        amountToAdd,
                        timeUnit,
                        zoneId
                ),
                zoneId
        );
    }


    /**
     *    Returns a {@link LocalDateTime} based on {@code sourceLocalDateTime} with the specified {@code amountToAdd}
     * added, in terms of {@code timeUnit}.
     *
     * @param sourceLocalDateTime
     *    {@link LocalDateTime} value from which to add the specified {@code amountToAdd}. If {@code null} then {@link LocalDateTime#now()} will be used
     * @param amountToAdd
     *    The amount of the {@code timeUnit} to add from {@code sourceLocalDateTime}. If less than zero then 1 will be used
     * @param timeUnit
     *    {@link ChronoUnit} of the given {@code amountToAdd}. If {@code null} then {@link ChronoUnit#MINUTES} will be used
     *
     * @return {@link LocalDateTime}
     */
    public static LocalDateTime plus(final LocalDateTime sourceLocalDateTime,
                                     final long amountToAdd,
                                     final ChronoUnit timeUnit) {
        return plus(
                sourceLocalDateTime,
                amountToAdd,
                timeUnit,
                ZoneId.systemDefault()
        );
    }


    /**
     *    Returns a {@link LocalDateTime} based on {@code sourceLocalDateTime} with the specified {@code amountToAdd}
     * added, in terms of {@code timeUnit}.
     *
     * @param sourceLocalDateTime
     *    {@link LocalDateTime} value from which to add the specified {@code amountToAdd}. If {@code null} then {@link LocalDateTime#now()} will be used
     * @param amountToAdd
     *    The amount of the {@code timeUnit} to add from {@code sourceLocalDateTime}. If less than zero then 1 will be used
     * @param timeUnit
     *    {@link ChronoUnit} of the given {@code amountToAdd}. If {@code null} then {@link ChronoUnit#MINUTES} will be used
     * @param zoneId
     *    {@link ZoneId} used in the conversion. If {@code null} then new {@link ZoneId#systemDefault()} will be used
     *
     * @return {@link LocalDateTime}
     */
    public static LocalDateTime plus(final LocalDateTime sourceLocalDateTime,
                                     final long amountToAdd,
                                     final ChronoUnit timeUnit,
                                     final ZoneId zoneId) {
        final ZoneId finalZoneId = getOrElse(
                zoneId,
                ZoneId.systemDefault()
        );
        final LocalDateTime finalSourceLocalDateTime = getOrElse(
                sourceLocalDateTime,
                LocalDateTime.now()
        );
        final ChronoUnit finalTimeUnit = getOrElse(
                timeUnit,
                ChronoUnit.MINUTES
        );
        final long finalAmountToAdd = 0 <= amountToAdd
                ? amountToAdd
                : 1L;

        return finalSourceLocalDateTime
                .atZone(finalZoneId)
                .plus(
                        finalAmountToAdd,
                        finalTimeUnit
                )
                .toLocalDateTime();
    }

}
