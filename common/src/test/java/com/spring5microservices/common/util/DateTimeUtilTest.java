package com.spring5microservices.common.util;

import com.spring5microservices.common.collection.tuple.Tuple2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.stream.Stream;

import static com.spring5microservices.common.util.DateTimeUtil.fromDateToLocalDateTime;
import static com.spring5microservices.common.util.DateTimeUtil.fromLocalDateTimeToDate;
import static com.spring5microservices.common.util.DateTimeUtil.getDateIntervalFromGiven;
import static com.spring5microservices.common.util.DateTimeUtil.getLocalDateTimeIntervalFromGiven;
import static com.spring5microservices.common.util.DateTimeUtil.minus;
import static com.spring5microservices.common.util.DateTimeUtil.plus;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DateTimeUtilTest {

    static Stream<Arguments> compareDateTestCases() {
        Date d1 = new GregorianCalendar(2020, Calendar.NOVEMBER, 11, 12, 31, 0).getTime();
        Date d2 = new GregorianCalendar(2020, Calendar.NOVEMBER, 11, 12, 31, 30).getTime();
        Date d3 = new GregorianCalendar(2020, Calendar.NOVEMBER, 11, 12, 33, 0).getTime();
        return Stream.of(
                //@formatter:off
                //            one,    two,    epsilon,   timeUnit,             expectedResult
                Arguments.of( null,   null,   -1,        null,                 CompareToResult.ZERO ),
                Arguments.of( null,   null,   1,         null,                 CompareToResult.ZERO ),
                Arguments.of( d1,     null,   1,         null,                 CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( null,   d1,     1,         null,                 CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( d1,     d2,     0,         null,                 CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( d2,     d1,     0,         null,                 CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( d1,     d1,     -1,        ChronoUnit.SECONDS,   CompareToResult.ZERO ),
                Arguments.of( d1,     d2,     -1,        ChronoUnit.SECONDS,   CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( d2,     d1,     -1,        ChronoUnit.SECONDS,   CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( d1,     d2,     29,        ChronoUnit.SECONDS,   CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( d1,     d2,     30,        ChronoUnit.SECONDS,   CompareToResult.ZERO ),
                Arguments.of( d1,     d2,     31,        ChronoUnit.SECONDS,   CompareToResult.ZERO ),
                Arguments.of( d2,     d1,     29,        ChronoUnit.SECONDS,   CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( d2,     d1,     30,        ChronoUnit.SECONDS,   CompareToResult.ZERO ),
                Arguments.of( d2,     d1,     31,        ChronoUnit.SECONDS,   CompareToResult.ZERO ),
                Arguments.of( d1,     d3,     1,         ChronoUnit.MINUTES,   CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( d1,     d3,     2,         ChronoUnit.MINUTES,   CompareToResult.ZERO ),
                Arguments.of( d3,     d1,     1,         ChronoUnit.MINUTES,   CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( d3,     d1,     2,         ChronoUnit.MINUTES,   CompareToResult.ZERO )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("compareDateTestCases")
    @DisplayName("compare: Date test cases")
    public void compareDate_testCases(Date one,
                                      Date two,
                                      long epsilon,
                                      ChronoUnit timeUnit,
                                      CompareToResult expectedResult) {
        int result = DateTimeUtil.compare(one, two, epsilon, timeUnit);
        verifyCompareToResult(result, expectedResult);
    }


    static Stream<Arguments> compareLocalDateTimeTestCases() {
        LocalDateTime ldt1 = LocalDateTime.of(2020, 11, 11, 12, 31, 00);
        LocalDateTime ldt2 = LocalDateTime.of(2020, 11, 11, 12, 31, 30);
        LocalDateTime ldt3 = LocalDateTime.of(2020, 11, 11, 12, 33, 00);
        return Stream.of(
                //@formatter:off
                //            one,    two,    epsilon,   timeUnit,             expectedResult
                Arguments.of( null,   null,   -1,        null,                 CompareToResult.ZERO ),
                Arguments.of( null,   null,   1,         null,                 CompareToResult.ZERO ),
                Arguments.of( ldt1,   null,   1,         null,                 CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( null,   ldt1,   1,         null,                 CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( ldt1,   ldt2,   0,         null,                 CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( ldt2,   ldt1,   0,         null,                 CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( ldt1,   ldt1,   -1,        ChronoUnit.SECONDS,   CompareToResult.ZERO ),
                Arguments.of( ldt1,   ldt2,   -1,        ChronoUnit.SECONDS,   CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( ldt2,   ldt1,   -1,        ChronoUnit.SECONDS,   CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( ldt1,   ldt2,   29,        ChronoUnit.SECONDS,   CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( ldt1,   ldt2,   30,        ChronoUnit.SECONDS,   CompareToResult.ZERO ),
                Arguments.of( ldt1,   ldt2,   31,        ChronoUnit.SECONDS,   CompareToResult.ZERO ),
                Arguments.of( ldt2,   ldt1,   29,        ChronoUnit.SECONDS,   CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( ldt2,   ldt1,   30,        ChronoUnit.SECONDS,   CompareToResult.ZERO ),
                Arguments.of( ldt2,   ldt1,   31,        ChronoUnit.SECONDS,   CompareToResult.ZERO ),
                Arguments.of( ldt1,   ldt3,   1,         ChronoUnit.MINUTES,   CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( ldt1,   ldt3,   2,         ChronoUnit.MINUTES,   CompareToResult.ZERO ),
                Arguments.of( ldt3,   ldt1,   1,         ChronoUnit.MINUTES,   CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( ldt3,   ldt1,   2,         ChronoUnit.MINUTES,   CompareToResult.ZERO )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("compareLocalDateTimeTestCases")
    @DisplayName("compare: LocalDateTime test cases")
    public void compareLocalDateTime_testCases(LocalDateTime one,
                                               LocalDateTime two,
                                               long epsilon,
                                               ChronoUnit timeUnit,
                                               CompareToResult expectedResult) {
        int result = DateTimeUtil.compare(one, two, epsilon, timeUnit);
        verifyCompareToResult(result, expectedResult);
    }


    static Stream<Arguments> fromLocalDateTimeToDateWithLocalDateTimeTestCases() {
        LocalDateTime ldt1 = LocalDateTime.of(2020, 10, 10, 12, 0, 0);
        LocalDateTime ldt2 = LocalDateTime.of(2022, 11, 12, 23, 0, 0);

        GregorianCalendar gc1 = new GregorianCalendar(2020, Calendar.OCTOBER, 10, 12, 0, 0);
        gc1.setTimeZone(TimeZone.getDefault());
        GregorianCalendar gc2 = new GregorianCalendar(2022, Calendar.NOVEMBER, 12, 23, 0, 0);
        gc2.setTimeZone(TimeZone.getDefault());
        return Stream.of(
                //@formatter:off
                //            sourceLocalDateTime,   expectedResult
                Arguments.of( null,                  null ),
                Arguments.of( ldt1,                  gc1.getTime() ),
                Arguments.of( ldt2,                  gc2.getTime() )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromLocalDateTimeToDateWithLocalDateTimeTestCases")
    @DisplayName("fromLocalDateTimeToDate: with LocalDateTime test cases")
    public void fromLocalDateTimeToDateWithLocalDateTime_testCases(LocalDateTime sourceLocalDateTime,
                                                                   Date expectedResult) {
        if (null == sourceLocalDateTime) {
            int compareResult = DateTimeUtil.compare(
                    fromLocalDateTimeToDate(sourceLocalDateTime),
                    new Date(),
                    5,
                    ChronoUnit.SECONDS
            );
            verifyCompareToResult(compareResult, CompareToResult.ZERO);
        } else {
            assertEquals(expectedResult, fromLocalDateTimeToDate(sourceLocalDateTime));
        }
    }


    static Stream<Arguments> fromLocalDateTimeToDateWithLocalDateTimeAndZoneIdTestCases() {
        ZoneId gmtZoneId = ZoneId.of("GMT");
        LocalDateTime ldt1 = LocalDateTime.of(2020, 10, 10, 12, 0, 0);
        LocalDateTime ldt2 = LocalDateTime.of(2022, 11, 12, 23, 0, 0);

        GregorianCalendar gc1 = new GregorianCalendar(2020, Calendar.OCTOBER, 10, 13, 0, 0);
        gc1.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        GregorianCalendar gc2 = new GregorianCalendar(2022, Calendar.NOVEMBER, 13, 1, 0, 0);
        gc2.setTimeZone(TimeZone.getTimeZone("GMT+2"));
        return Stream.of(
                //@formatter:off
                //            sourceLocalDateTime,   zoneId,      expectedResult
                Arguments.of( null,                  null,        null ),
                Arguments.of( null,                  gmtZoneId,   null ),
                Arguments.of( ldt1,                  gmtZoneId,   gc1.getTime() ),
                Arguments.of( ldt2,                  gmtZoneId,   gc2.getTime() )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromLocalDateTimeToDateWithLocalDateTimeAndZoneIdTestCases")
    @DisplayName("fromLocalDateTimeToDate: with LocalDateTime and ZoneId test cases")
    public void fromLocalDateTimeToDateWithLocalDateTimeAndZoneId_testCases(LocalDateTime sourceLocalDateTime,
                                                                            ZoneId zoneId,
                                                                            Date expectedResult) {
        if (null == sourceLocalDateTime) {
            int compareResult = DateTimeUtil.compare(
                    fromLocalDateTimeToDate(sourceLocalDateTime),
                    new Date(),
                    5,
                    ChronoUnit.SECONDS
            );
            verifyCompareToResult(compareResult, CompareToResult.ZERO);
        } else {
            assertEquals(expectedResult, fromLocalDateTimeToDate(sourceLocalDateTime, zoneId));
        }
    }


    static Stream<Arguments> fromDateToLocalDateTimeWithDateTestCases() {
        LocalDateTime ldt1 = LocalDateTime.of(2020, 10, 10, 12, 0, 0);
        LocalDateTime ldt2 = LocalDateTime.of(2022, 11, 12, 23, 0, 0);

        GregorianCalendar gc1 = new GregorianCalendar(2020, Calendar.OCTOBER, 10, 12, 0, 0);
        gc1.setTimeZone(TimeZone.getDefault());
        GregorianCalendar gc2 = new GregorianCalendar(2022, Calendar.NOVEMBER, 12, 23, 0, 0);
        gc2.setTimeZone(TimeZone.getDefault());
        return Stream.of(
                //@formatter:off
                //            sourceDate,      expectedResult
                Arguments.of( null,            null ),
                Arguments.of( gc1.getTime(),   ldt1 ),
                Arguments.of( gc2.getTime(),   ldt2 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromDateToLocalDateTimeWithDateTestCases")
    @DisplayName("fromDateToLocalDateTime: with Date test cases")
    public void fromDateToLocalDateTimeWithDate_testCases(Date sourceDate,
                                                          LocalDateTime expectedResult) {
        if (null == sourceDate) {
            int compareResult = DateTimeUtil.compare(
                    fromDateToLocalDateTime(sourceDate),
                    LocalDateTime.now(),
                    5,
                    ChronoUnit.SECONDS
            );
            verifyCompareToResult(compareResult, CompareToResult.ZERO);
        } else {
            assertEquals(expectedResult, fromDateToLocalDateTime(sourceDate));
        }
    }


    static Stream<Arguments> fromDateToLocalDateTimeWithDateAndZoneIdTestCases() {
        ZoneId gmtZoneId = ZoneId.of("GMT");
        LocalDateTime ldt1 = LocalDateTime.of(2020, 10, 10, 12, 0, 0);
        LocalDateTime ldt2 = LocalDateTime.of(2022, 11, 12, 23, 0, 0);

        GregorianCalendar gc1 = new GregorianCalendar(2020, Calendar.OCTOBER, 10, 13, 0, 0);
        gc1.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        GregorianCalendar gc2 = new GregorianCalendar(2022, Calendar.NOVEMBER, 13, 1, 0, 0);
        gc2.setTimeZone(TimeZone.getTimeZone("GMT+2"));

        LocalDateTime expectedResultNullSourceAndZoneId = LocalDateTime.now(ZoneId.systemDefault());
        LocalDateTime expectedResultNullSourceAndGmt = LocalDateTime.now(gmtZoneId);
        return Stream.of(
                //@formatter:off
                //            sourceDate,      zoneId,      expectedResult
                Arguments.of( null,            null,        expectedResultNullSourceAndZoneId ),
                Arguments.of( null,            gmtZoneId,   expectedResultNullSourceAndGmt ),
                Arguments.of( gc1.getTime(),   gmtZoneId,   ldt1 ),
                Arguments.of( gc2.getTime(),   gmtZoneId,   ldt2 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromDateToLocalDateTimeWithDateAndZoneIdTestCases")
    @DisplayName("fromDateToLocalDateTime: with Date and ZoneId test cases")
    public void fromDateToLocalDateTimeWithDateAndZoneId_testCases(Date sourceDate,
                                                                   ZoneId zoneId,
                                                                   LocalDateTime expectedResult) {
        if (null == sourceDate) {
            int compareResult = DateTimeUtil.compare(
                    fromDateToLocalDateTime(sourceDate, zoneId),
                    expectedResult,
                    5,
                    ChronoUnit.SECONDS
            );
            verifyCompareToResult(compareResult, CompareToResult.ZERO);
        } else {
            assertEquals(expectedResult, fromDateToLocalDateTime(sourceDate, zoneId));
        }
    }


    static Stream<Arguments> getDateIntervalFromGivenTestCases() {
        ZoneId utcZoneId = ZoneId.of("UTC");
        ZoneId gmtZoneId = ZoneId.of("GMT");

        Date d1 = new GregorianCalendar(2022, Calendar.NOVEMBER, 11, 12, 10, 0).getTime();
        Date d2 = new GregorianCalendar(2022, Calendar.SEPTEMBER, 11, 12, 10, 0).getTime();
        Date d3 = new GregorianCalendar(2022, Calendar.SEPTEMBER, 11, 11, 10, 0).getTime();
        Date d4 = new GregorianCalendar(2022, Calendar.SEPTEMBER, 11, 11, 5, 0).getTime();

        GregorianCalendar gc1 = new GregorianCalendar(2022, Calendar.NOVEMBER, 11, 13, 10, 0);
        gc1.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        Date gc1Date = gc1.getTime();

        GregorianCalendar gc2 = new GregorianCalendar(2022, Calendar.SEPTEMBER, 11, 12, 10, 0);
        gc2.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date gc2Date = gc2.getTime();

        Date nullSourceAndZoneId = DateTimeUtil.fromLocalDateTimeToDate(LocalDateTime.now(ZoneId.systemDefault()), ZoneId.systemDefault());
        Date nullSourceAndUtc = DateTimeUtil.fromLocalDateTimeToDate(LocalDateTime.now(utcZoneId), utcZoneId);
        Date nullSourceNegativeAmountNullUnitZoneId = DateTimeUtil.fromLocalDateTimeToDate(LocalDateTime.now(ZoneId.systemDefault()).minus(1, ChronoUnit.MINUTES), ZoneId.systemDefault());
        Date nullSourceNegativeAmountNullUnitAndUtc = DateTimeUtil.fromLocalDateTimeToDate(LocalDateTime.now(utcZoneId).minus(1, ChronoUnit.MINUTES), utcZoneId);
        Date nullSourceNegativeAmountAndSecondsAndNullZoneId = DateTimeUtil.fromLocalDateTimeToDate(LocalDateTime.now(ZoneId.systemDefault()).minus(1, ChronoUnit.SECONDS), ZoneId.systemDefault());
        Date nullSourceNegativeAmountAndSecondsAndUtc = DateTimeUtil.fromLocalDateTimeToDate(LocalDateTime.now(utcZoneId).minus(1, ChronoUnit.SECONDS), utcZoneId);
        Date nullSourcePositiveAmountAndMinutesAndNullZoneId = DateTimeUtil.fromLocalDateTimeToDate(LocalDateTime.now(ZoneId.systemDefault()).plus(1, ChronoUnit.MINUTES), ZoneId.systemDefault());
        Date nullSourcePositiveAmountAndMinutesAndUtc = DateTimeUtil.fromLocalDateTimeToDate(LocalDateTime.now(utcZoneId).plus(1, ChronoUnit.MINUTES), utcZoneId);

        Tuple2<Date, Date> expectedResultNullSourceNegativeAmountNullUnitZoneId = Tuple2.of(
                nullSourceNegativeAmountNullUnitZoneId,
                nullSourceAndZoneId
        );
        Tuple2<Date, Date> expectedResultNullSourceNegativeAmountNullUnitAndUtc = Tuple2.of(
                nullSourceNegativeAmountNullUnitAndUtc,
                nullSourceAndUtc
        );
        Tuple2<Date, Date> expectedResultNullSourceNegativeAmountAndSecondsAndNullZoneId = Tuple2.of(
                nullSourceNegativeAmountAndSecondsAndNullZoneId,
                nullSourceAndZoneId
        );
        Tuple2<Date, Date> expectedResultNullSourceNegativeAmountAndSecondsAndUtc = Tuple2.of(
                nullSourceNegativeAmountAndSecondsAndUtc,
                nullSourceAndUtc
        );
        Tuple2<Date, Date> expectedResultNullSourcePositiveAmountAndMinutesAndNullZoneId = Tuple2.of(
                nullSourceAndZoneId,
                nullSourcePositiveAmountAndMinutesAndNullZoneId
        );
        Tuple2<Date, Date> expectedResultNullSourcePositiveAmountAndMinutesAndUtc = Tuple2.of(
                nullSourceAndUtc,
                nullSourcePositiveAmountAndMinutesAndUtc
        );
        return Stream.of(
                //@formatter:off
                //            sourceDate,   difference,   timeUnit,             zoneId,      expectedResult
                Arguments.of( null,         -1,           null,                 null,        expectedResultNullSourceNegativeAmountNullUnitZoneId ),
                Arguments.of( null,         -1,           null,                 utcZoneId,   expectedResultNullSourceNegativeAmountNullUnitAndUtc ),
                Arguments.of( null,         -1,           ChronoUnit.SECONDS,   null,        expectedResultNullSourceNegativeAmountAndSecondsAndNullZoneId ),
                Arguments.of( null,         -1,           ChronoUnit.SECONDS,   utcZoneId,   expectedResultNullSourceNegativeAmountAndSecondsAndUtc ),
                Arguments.of( null,         1,            ChronoUnit.MINUTES,   null,        expectedResultNullSourcePositiveAmountAndMinutesAndNullZoneId ),
                Arguments.of( null,         1,            ChronoUnit.MINUTES,   utcZoneId,   expectedResultNullSourcePositiveAmountAndMinutesAndUtc ),
                Arguments.of( d1,           0,            ChronoUnit.HOURS,     null,        Tuple2.of(d1, d1) ),
                Arguments.of( d1,           0,            ChronoUnit.HOURS,     utcZoneId,   Tuple2.of(d1, d1) ),
                Arguments.of( d1,           0,            ChronoUnit.DAYS,      null,        Tuple2.of(d1, d1) ),
                Arguments.of( d1,           0,            ChronoUnit.DAYS,      utcZoneId,   Tuple2.of(d1, d1) ),
                Arguments.of( d2,           2,            ChronoUnit.MONTHS,    null,        Tuple2.of(d2, d1) ),
                Arguments.of( d3,           1,            ChronoUnit.HOURS,     null,        Tuple2.of(d3, d2) ),
                Arguments.of( d4,           5,            ChronoUnit.MINUTES,   null,        Tuple2.of(d4, d3) ),
                Arguments.of( d1,          -2,            ChronoUnit.MONTHS,    null,        Tuple2.of(d2, d1) ),
                Arguments.of( d2,          -1,            ChronoUnit.HOURS,     null,        Tuple2.of(d3, d2) ),
                Arguments.of( d3,          -5,            ChronoUnit.MINUTES,   null,        Tuple2.of(d4, d3) ),
                Arguments.of( gc2Date,      2,            ChronoUnit.MONTHS,    gmtZoneId,   Tuple2.of(gc2Date, gc1Date) ),
                Arguments.of( gc1Date,     -2,            ChronoUnit.MONTHS,    gmtZoneId,   Tuple2.of(gc2Date, gc1Date) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getDateIntervalFromGivenTestCases")
    @DisplayName("getDateIntervalFromGiven: test cases")
    public void getDateIntervalFromGiven_testCases(Date sourceDate,
                                                   long difference,
                                                   ChronoUnit timeUnit,
                                                   ZoneId zoneId,
                                                   Tuple2<Date, Date> expectedResult) {
        if (null == sourceDate) {
            Tuple2<Date, Date> result = getDateIntervalFromGiven(sourceDate, difference, timeUnit, zoneId);
            int compareResultLeft = DateTimeUtil.compare(
                    result._1,
                    expectedResult._1,
                    5,
                    ChronoUnit.SECONDS
            );
            int compareResultRight = DateTimeUtil.compare(
                    result._2,
                    expectedResult._2,
                    5,
                    ChronoUnit.SECONDS
            );
            verifyCompareToResult(compareResultLeft, CompareToResult.ZERO);
            verifyCompareToResult(compareResultRight, CompareToResult.ZERO);
        } else {
            Tuple2<Date, Date> result = getDateIntervalFromGiven(sourceDate, difference, timeUnit, zoneId);

            assertEquals(expectedResult._1, result._1);
            assertEquals(expectedResult._2, result._2);
        }
    }


    static Stream<Arguments> getLocalDateTimeIntervalFromGivenTestCases() {
        ZoneId utcZoneId = ZoneId.of("UTC");
        ZoneId utcPlus1ZoneId = ZoneId.of("UTC+1");

        LocalDateTime ldt1 = LocalDateTime.of(2022, 11, 11, 12, 10, 0);
        LocalDateTime ldt2 = LocalDateTime.of(2022, 9, 11, 12, 10, 0);
        LocalDateTime ldt3 = LocalDateTime.of(2022, 9, 11, 11, 10, 0);
        LocalDateTime ldt4 = LocalDateTime.of(2022, 9, 11, 11, 5, 0);

        LocalDateTime ldtUtc = LocalDateTime.ofInstant(Instant.parse("2022-11-11T12:10:00Z"), utcZoneId);
        LocalDateTime ldtUtcPlus1 = LocalDateTime.ofInstant(Instant.parse("2022-09-11T11:10:00Z"), utcPlus1ZoneId);

        LocalDateTime nullSourceAndZoneId = LocalDateTime.now(ZoneId.systemDefault());
        LocalDateTime nullSourceAndUtc = LocalDateTime.now(utcZoneId);
        LocalDateTime nullSourceNegativeAmountNullUnitZoneId = LocalDateTime.now(ZoneId.systemDefault()).minus(1, ChronoUnit.MINUTES);
        LocalDateTime nullSourceNegativeAmountNullUnitAndUtc = LocalDateTime.now(utcZoneId).minus(1, ChronoUnit.MINUTES);
        LocalDateTime nullSourceNegativeAmountAndSecondsAndNullZoneId = LocalDateTime.now(ZoneId.systemDefault()).minus(1, ChronoUnit.SECONDS);
        LocalDateTime nullSourceNegativeAmountAndSecondsAndUtc = LocalDateTime.now(utcZoneId).minus(1, ChronoUnit.SECONDS);
        LocalDateTime nullSourcePositiveAmountAndMinutesAndNullZoneId = LocalDateTime.now(ZoneId.systemDefault()).plus(1, ChronoUnit.MINUTES);
        LocalDateTime nullSourcePositiveAmountAndMinutesAndUtc = LocalDateTime.now(utcZoneId).plus(1, ChronoUnit.MINUTES);

        Tuple2<LocalDateTime, LocalDateTime> expectedResultNullSourceNegativeAmountNullUnitZoneId = Tuple2.of(
                nullSourceNegativeAmountNullUnitZoneId,
                nullSourceAndZoneId
        );
        Tuple2<LocalDateTime, LocalDateTime> expectedResultNullSourceNegativeAmountNullUnitAndUtc = Tuple2.of(
                nullSourceNegativeAmountNullUnitAndUtc,
                nullSourceAndUtc
        );
        Tuple2<LocalDateTime, LocalDateTime> expectedResultNullSourceNegativeAmountAndSecondsAndNullZoneId = Tuple2.of(
                nullSourceNegativeAmountAndSecondsAndNullZoneId,
                nullSourceAndZoneId
        );
        Tuple2<LocalDateTime, LocalDateTime> expectedResultNullSourceNegativeAmountAndSecondsAndUtc = Tuple2.of(
                nullSourceNegativeAmountAndSecondsAndUtc,
                nullSourceAndUtc
        );
        Tuple2<LocalDateTime, LocalDateTime> expectedResultNullSourcePositiveAmountAndMinutesAndNullZoneId = Tuple2.of(
                nullSourceAndZoneId,
                nullSourcePositiveAmountAndMinutesAndNullZoneId
        );
        Tuple2<LocalDateTime, LocalDateTime> expectedResultNullSourcePositiveAmountAndMinutesAndUtc = Tuple2.of(
                nullSourceAndUtc,
                nullSourcePositiveAmountAndMinutesAndUtc
        );
        return Stream.of(
                //@formatter:off
                //            sourceLocalDateTime,   difference,   timeUnit,             zoneId,      expectedResult
                Arguments.of( null,                  -1,           null,                 null,        expectedResultNullSourceNegativeAmountNullUnitZoneId ),
                Arguments.of( null,                  -1,           null,                 utcZoneId,   expectedResultNullSourceNegativeAmountNullUnitAndUtc ),
                Arguments.of( null,                  -1,           ChronoUnit.SECONDS,   null,        expectedResultNullSourceNegativeAmountAndSecondsAndNullZoneId ),
                Arguments.of( null,                  -1,           ChronoUnit.SECONDS,   utcZoneId,   expectedResultNullSourceNegativeAmountAndSecondsAndUtc ),
                Arguments.of( null,                  1,            ChronoUnit.MINUTES,   null,        expectedResultNullSourcePositiveAmountAndMinutesAndNullZoneId ),
                Arguments.of( null,                  1,            ChronoUnit.MINUTES,   utcZoneId,   expectedResultNullSourcePositiveAmountAndMinutesAndUtc ),
                Arguments.of( ldt1,                  0,            ChronoUnit.HOURS,     null,        Tuple2.of(ldt1, ldt1) ),
                Arguments.of( ldt1,                  0,            ChronoUnit.HOURS,     utcZoneId,   Tuple2.of(ldt1, ldt1) ),
                Arguments.of( ldt1,                  0,            ChronoUnit.DAYS,      null,        Tuple2.of(ldt1, ldt1) ),
                Arguments.of( ldt1,                  0,            ChronoUnit.DAYS,      utcZoneId,   Tuple2.of(ldt1, ldt1) ),
                Arguments.of( ldt2,                  2,            ChronoUnit.MONTHS,    null,        Tuple2.of(ldt2, ldt1) ),
                Arguments.of( ldt3,                  1,            ChronoUnit.HOURS,     null,        Tuple2.of(ldt3, ldt2) ),
                Arguments.of( ldt4,                  5,            ChronoUnit.MINUTES,   null,        Tuple2.of(ldt4, ldt3) ),
                Arguments.of( ldt1,                  -2,           ChronoUnit.MONTHS,    null,        Tuple2.of(ldt2, ldt1) ),
                Arguments.of( ldt2,                  -1,           ChronoUnit.HOURS,     null,        Tuple2.of(ldt3, ldt2) ),
                Arguments.of( ldt3,                  -5,           ChronoUnit.MINUTES,   null,        Tuple2.of(ldt4, ldt3) ),
                Arguments.of( ldtUtcPlus1,           2,            ChronoUnit.MONTHS,    utcZoneId,   Tuple2.of(ldtUtcPlus1, ldtUtc) ),
                Arguments.of( ldtUtc,                -2,           ChronoUnit.MONTHS,    utcZoneId,   Tuple2.of(ldtUtcPlus1, ldtUtc) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getLocalDateTimeIntervalFromGivenTestCases")
    @DisplayName("getLocalDateTimeIntervalFromGiven: test cases")
    public void getLocalDateTimeIntervalFromGiven_testCases(LocalDateTime sourceLocalDateTime,
                                                            long difference,
                                                            ChronoUnit timeUnit,
                                                            ZoneId zoneId,
                                                            Tuple2<LocalDateTime, LocalDateTime> expectedResult) {
        if (null == sourceLocalDateTime) {
            Tuple2<LocalDateTime, LocalDateTime> result = getLocalDateTimeIntervalFromGiven(sourceLocalDateTime, difference, timeUnit, zoneId);
            int compareResultLeft = DateTimeUtil.compare(
                    result._1,
                    expectedResult._1,
                    5,
                    ChronoUnit.SECONDS
            );
            int compareResultRight = DateTimeUtil.compare(
                    result._2,
                    expectedResult._2,
                    5,
                    ChronoUnit.SECONDS
            );
            verifyCompareToResult(compareResultLeft, CompareToResult.ZERO);
            verifyCompareToResult(compareResultRight, CompareToResult.ZERO);
        } else {
            Tuple2<LocalDateTime, LocalDateTime> result = getLocalDateTimeIntervalFromGiven(sourceLocalDateTime, difference, timeUnit, zoneId);

            assertEquals(expectedResult._1, result._1);
            assertEquals(expectedResult._2, result._2);
        }
    }


    static Stream<Arguments> minusDateWithAmountToSubtractAndTimeUnitTestCases() {
        Date d1 = new GregorianCalendar(2022, Calendar.NOVEMBER, 11, 12, 10, 0).getTime();
        Date d2 = new GregorianCalendar(2022, Calendar.SEPTEMBER, 11, 12, 10, 0).getTime();
        Date d3 = new GregorianCalendar(2022, Calendar.SEPTEMBER, 11, 11, 10, 0).getTime();
        Date d4 = new GregorianCalendar(2022, Calendar.SEPTEMBER, 11, 11, 5, 0).getTime();

        Date expectedResultNullSourceNegativeAmountNullUnit = DateTimeUtil.fromLocalDateTimeToDate(LocalDateTime.now().minus(1, ChronoUnit.MINUTES));
        Date expectedResultNullSourceNegativeAmountAndSeconds = DateTimeUtil.fromLocalDateTimeToDate(LocalDateTime.now().minus(1, ChronoUnit.SECONDS));
        Date expectedResultNullSourcePositiveAmountAndMinutes = DateTimeUtil.fromLocalDateTimeToDate(LocalDateTime.now().minus(1, ChronoUnit.MINUTES));
        return Stream.of(
                //@formatter:off
                //            sourceDate,   amountToSubtract,   timeUnit,             expectedResult
                Arguments.of( null,         -1,                 null,                 expectedResultNullSourceNegativeAmountNullUnit ),
                Arguments.of( null,         -1,                 ChronoUnit.SECONDS,   expectedResultNullSourceNegativeAmountAndSeconds ),
                Arguments.of( null,         1,                  ChronoUnit.MINUTES,   expectedResultNullSourcePositiveAmountAndMinutes ),
                Arguments.of( d1,           0,                  ChronoUnit.HOURS,     d1 ),
                Arguments.of( d1,           0,                  ChronoUnit.DAYS,      d1 ),
                Arguments.of( d1,           2,                  ChronoUnit.MONTHS,    d2 ),
                Arguments.of( d2,           1,                  ChronoUnit.HOURS,     d3 ),
                Arguments.of( d3,           5,                  ChronoUnit.MINUTES,   d4 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("minusDateWithAmountToSubtractAndTimeUnitTestCases")
    @DisplayName("minus: Date with amountToSubtract and TimeUnit test cases")
    public void minusDateWithAmountToSubtractAndTimeUnit_testCases(Date sourceDate,
                                                                   long amountToSubtract,
                                                                   ChronoUnit timeUnit,
                                                                   Date expectedResult) {
        if (null == sourceDate) {
            int compareResult = DateTimeUtil.compare(
                    minus(sourceDate, amountToSubtract, timeUnit),
                    expectedResult,
                    5,
                    ChronoUnit.SECONDS
            );
            verifyCompareToResult(compareResult, CompareToResult.ZERO);
        } else {
            assertEquals(expectedResult, minus(sourceDate, amountToSubtract, timeUnit));
        }
    }


    static Stream<Arguments> minusLocalDateTimeWithAmountToSubtractAndTimeUnitTestCases() {
        LocalDateTime ldt1 = LocalDateTime.of(2022, 11, 11, 12, 10, 0);
        LocalDateTime ldt2 = LocalDateTime.of(2022, 9, 11, 12, 10, 0);
        LocalDateTime ldt3 = LocalDateTime.of(2022, 9, 11, 11, 10, 0);
        LocalDateTime ldt4 = LocalDateTime.of(2022, 9, 11, 11, 5, 0);

        LocalDateTime expectedResultNullSourceNegativeAmountNullUnit = LocalDateTime.now().minus(1, ChronoUnit.MINUTES);
        LocalDateTime expectedResultNullSourceNegativeAmountAndSeconds = LocalDateTime.now().minus(1, ChronoUnit.SECONDS);
        LocalDateTime expectedResultNullSourcePositiveAmountAndMinutes = LocalDateTime.now().minus(1, ChronoUnit.MINUTES);
        return Stream.of(
                //@formatter:off
                //            sourceLocalDateTime,   amountToSubtract,   timeUnit,             expectedResult
                Arguments.of( null,                  -1,                 null,                 expectedResultNullSourceNegativeAmountNullUnit ),
                Arguments.of( null,                  -1,                 ChronoUnit.SECONDS,   expectedResultNullSourceNegativeAmountAndSeconds ),
                Arguments.of( null,                  1,                  ChronoUnit.MINUTES,   expectedResultNullSourcePositiveAmountAndMinutes ),
                Arguments.of( ldt1,                  0,                  ChronoUnit.HOURS,     ldt1 ),
                Arguments.of( ldt1,                  0,                  ChronoUnit.DAYS,      ldt1 ),
                Arguments.of( ldt1,                  2,                  ChronoUnit.MONTHS,    ldt2 ),
                Arguments.of( ldt2,                  1,                  ChronoUnit.HOURS,     ldt3 ),
                Arguments.of( ldt3,                  5,                  ChronoUnit.MINUTES,   ldt4 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("minusLocalDateTimeWithAmountToSubtractAndTimeUnitTestCases")
    @DisplayName("minus: LocalDateTime with amountToSubtract and TimeUnit test cases")
    public void minusLocalDateTimeWithAmountToSubtractAndTimeUnit_testCases(LocalDateTime sourceLocalDateTime,
                                                                            long amountToSubtract,
                                                                            ChronoUnit timeUnit,
                                                                            LocalDateTime expectedResult) {
        if (null == sourceLocalDateTime) {
            int compareResult = DateTimeUtil.compare(
                    minus(sourceLocalDateTime, amountToSubtract, timeUnit),
                    expectedResult,
                    5,
                    ChronoUnit.SECONDS
            );
            verifyCompareToResult(compareResult, CompareToResult.ZERO);
        } else {
            assertEquals(expectedResult, minus(sourceLocalDateTime, amountToSubtract, timeUnit));
        }
    }


    static Stream<Arguments> minusDateWithAmountToSubtractTimeUnitAndZoneIdTestCases() {
        ZoneId utcZoneId = ZoneId.of("UTC");
        ZoneId gmtZoneId = ZoneId.of("GMT");

        Date d1 = new GregorianCalendar(2022, Calendar.NOVEMBER, 11, 12, 10, 0).getTime();
        Date d2 = new GregorianCalendar(2022, Calendar.SEPTEMBER, 11, 12, 10, 0).getTime();
        Date d3 = new GregorianCalendar(2022, Calendar.SEPTEMBER, 11, 11, 10, 0).getTime();
        Date d4 = new GregorianCalendar(2022, Calendar.SEPTEMBER, 11, 11, 5, 0).getTime();

        GregorianCalendar gc1 = new GregorianCalendar(2022, Calendar.NOVEMBER, 11, 13, 10, 0);
        gc1.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        Date gc1Date = gc1.getTime();

        GregorianCalendar gc2 = new GregorianCalendar(2022, Calendar.SEPTEMBER, 11, 12, 10, 0);
        gc2.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date gc2Date = gc2.getTime();

        Date expectedResultNullSourceNegativeAmountNullUnitZoneId = DateTimeUtil.fromLocalDateTimeToDate(LocalDateTime.now(ZoneId.systemDefault()).minus(1, ChronoUnit.MINUTES), ZoneId.systemDefault());
        Date expectedResultNullSourceNegativeAmountNullUnitAndUtc = DateTimeUtil.fromLocalDateTimeToDate(LocalDateTime.now(utcZoneId).minus(1, ChronoUnit.MINUTES), utcZoneId);
        Date expectedResultNullSourceNegativeAmountAndSecondsAndNullZoneId = DateTimeUtil.fromLocalDateTimeToDate(LocalDateTime.now(ZoneId.systemDefault()).minus(1, ChronoUnit.SECONDS), ZoneId.systemDefault());
        Date expectedResultNullSourceNegativeAmountAndSecondsAndUtc = DateTimeUtil.fromLocalDateTimeToDate(LocalDateTime.now(utcZoneId).minus(1, ChronoUnit.SECONDS), utcZoneId);
        Date expectedResultNullSourcePositiveAmountAndMinutesAndNullZoneId = DateTimeUtil.fromLocalDateTimeToDate(LocalDateTime.now(ZoneId.systemDefault()).minus(1, ChronoUnit.MINUTES), ZoneId.systemDefault());
        Date expectedResultNullSourcePositiveAmountAndMinutesAndUtc = DateTimeUtil.fromLocalDateTimeToDate(LocalDateTime.now(utcZoneId).minus(1, ChronoUnit.MINUTES), utcZoneId);
        return Stream.of(
                //@formatter:off
                //            sourceDate,   amountToSubtract,   timeUnit,             zoneId,      expectedResult
                Arguments.of( null,         -1,                 null,                 null,        expectedResultNullSourceNegativeAmountNullUnitZoneId ),
                Arguments.of( null,         -1,                 null,                 utcZoneId,   expectedResultNullSourceNegativeAmountNullUnitAndUtc ),
                Arguments.of( null,         -1,                 ChronoUnit.SECONDS,   null,        expectedResultNullSourceNegativeAmountAndSecondsAndNullZoneId ),
                Arguments.of( null,         -1,                 ChronoUnit.SECONDS,   utcZoneId,   expectedResultNullSourceNegativeAmountAndSecondsAndUtc ),
                Arguments.of( null,         1,                  ChronoUnit.MINUTES,   null,        expectedResultNullSourcePositiveAmountAndMinutesAndNullZoneId ),
                Arguments.of( null,         1,                  ChronoUnit.MINUTES,   utcZoneId,   expectedResultNullSourcePositiveAmountAndMinutesAndUtc ),
                Arguments.of( d1,           0,                  ChronoUnit.HOURS,     null,        d1 ),
                Arguments.of( d1,           0,                  ChronoUnit.HOURS,     utcZoneId,   d1 ),
                Arguments.of( d1,           0,                  ChronoUnit.DAYS,      null,        d1 ),
                Arguments.of( d1,           0,                  ChronoUnit.DAYS,      utcZoneId,   d1 ),
                Arguments.of( d1,           2,                  ChronoUnit.MONTHS,    null,        d2 ),
                Arguments.of( d2,           1,                  ChronoUnit.HOURS,     null,        d3 ),
                Arguments.of( d3,           5,                  ChronoUnit.MINUTES,   null,        d4 ),
                Arguments.of( gc1Date,      2,                  ChronoUnit.MONTHS,    gmtZoneId,   gc2Date )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("minusDateWithAmountToSubtractTimeUnitAndZoneIdTestCases")
    @DisplayName("minus: Date with amountToSubtract, TimeUnit and ZoneId test cases")
    public void minusDateWithAmountToSubtractTimeUnitAndZoneId_testCases(Date sourceDate,
                                                                         long amountToSubtract,
                                                                         ChronoUnit timeUnit,
                                                                         ZoneId zoneId,
                                                                         Date expectedResult) {
        if (null == sourceDate) {
            int compareResult = DateTimeUtil.compare(
                    minus(sourceDate, amountToSubtract, timeUnit, zoneId),
                    expectedResult,
                    5,
                    ChronoUnit.SECONDS
            );
            verifyCompareToResult(compareResult, CompareToResult.ZERO);
        } else {
            assertEquals(expectedResult, minus(sourceDate, amountToSubtract, timeUnit, zoneId));
        }
    }


    static Stream<Arguments> minusLocalDateTimeWithAmountToSubtractTimeUnitAndZoneIdTestCases() {
        ZoneId utcZoneId = ZoneId.of("UTC");
        ZoneId utcPlus1ZoneId = ZoneId.of("UTC+1");

        LocalDateTime ldt1 = LocalDateTime.of(2022, 11, 11, 12, 10, 0);
        LocalDateTime ldt2 = LocalDateTime.of(2022, 9, 11, 12, 10, 0);
        LocalDateTime ldt3 = LocalDateTime.of(2022, 9, 11, 11, 10, 0);
        LocalDateTime ldt4 = LocalDateTime.of(2022, 9, 11, 11, 5, 0);

        LocalDateTime ldtUtc = LocalDateTime.ofInstant(Instant.parse("2022-11-11T12:10:00Z"), utcZoneId);
        LocalDateTime ldtUtcPlus1 = LocalDateTime.ofInstant(Instant.parse("2022-09-11T11:10:00Z"), utcPlus1ZoneId);

        LocalDateTime expectedResultNullSourceNegativeAmountNullUnitZoneId = LocalDateTime.now().minus(1, ChronoUnit.MINUTES);
        LocalDateTime expectedResultNullSourceNegativeAmountNullUnitAndUtc = LocalDateTime.now().minus(1, ChronoUnit.MINUTES);
        LocalDateTime expectedResultNullSourceNegativeAmountAndSecondsAndNullZoneId = LocalDateTime.now().minus(1, ChronoUnit.SECONDS);
        LocalDateTime expectedResultNullSourceNegativeAmountAndSecondsAndUtc = LocalDateTime.now().minus(1, ChronoUnit.SECONDS);
        LocalDateTime expectedResultNullSourcePositiveAmountAndMinutesAndNullZoneId = LocalDateTime.now().minus(1, ChronoUnit.MINUTES);
        LocalDateTime expectedResultNullSourcePositiveAmountAndMinutesAndUtc = LocalDateTime.now().minus(1, ChronoUnit.MINUTES);
        return Stream.of(
                //@formatter:off
                //            sourceLocalDateTime,   amountToSubtract,   timeUnit,   zoneId,                expectedResult
                Arguments.of( null,                  -1,                 null,                 null,        expectedResultNullSourceNegativeAmountNullUnitZoneId ),
                Arguments.of( null,                  -1,                 null,                 utcZoneId,   expectedResultNullSourceNegativeAmountNullUnitAndUtc ),
                Arguments.of( null,                  -1,                 ChronoUnit.SECONDS,   null,        expectedResultNullSourceNegativeAmountAndSecondsAndNullZoneId ),
                Arguments.of( null,                  -1,                 ChronoUnit.SECONDS,   utcZoneId,   expectedResultNullSourceNegativeAmountAndSecondsAndUtc ),
                Arguments.of( null,                  1,                  ChronoUnit.MINUTES,   null,        expectedResultNullSourcePositiveAmountAndMinutesAndNullZoneId ),
                Arguments.of( null,                  1,                  ChronoUnit.MINUTES,   utcZoneId,   expectedResultNullSourcePositiveAmountAndMinutesAndUtc ),
                Arguments.of( ldt1,                  0,                  ChronoUnit.HOURS,     null,        ldt1 ),
                Arguments.of( ldt1,                  0,                  ChronoUnit.HOURS,     utcZoneId,   ldt1 ),
                Arguments.of( ldt1,                  0,                  ChronoUnit.DAYS,      null,        ldt1 ),
                Arguments.of( ldt1,                  0,                  ChronoUnit.DAYS,      utcZoneId,   ldt1 ),
                Arguments.of( ldt1,                  2,                  ChronoUnit.MONTHS,    null,        ldt2 ),
                Arguments.of( ldt2,                  1,                  ChronoUnit.HOURS,     null,        ldt3 ),
                Arguments.of( ldt3,                  5,                  ChronoUnit.MINUTES,   null,        ldt4 ),
                Arguments.of( ldtUtc,                2,                  ChronoUnit.MONTHS,    null,        ldtUtcPlus1 ),
                Arguments.of( ldtUtc,                2,                  ChronoUnit.MONTHS,    utcZoneId,   ldtUtcPlus1 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("minusLocalDateTimeWithAmountToSubtractTimeUnitAndZoneIdTestCases")
    @DisplayName("minus: LocalDateTime with amountToSubtract, TimeUnit and ZoneId test cases")
    public void minusLocalDatetimeWithAmountToSubtractTimeUnitAndZoneId_testCases(LocalDateTime sourceLocalDateTime,
                                                                                  long amountToSubtract,
                                                                                  ChronoUnit timeUnit,
                                                                                  ZoneId zoneId,
                                                                                  LocalDateTime expectedResult) {
        if (null == sourceLocalDateTime) {
           int compareResult = DateTimeUtil.compare(
                    minus(sourceLocalDateTime, amountToSubtract, timeUnit, zoneId),
                    expectedResult,
                    5,
                    ChronoUnit.SECONDS
            );
            verifyCompareToResult(compareResult, CompareToResult.ZERO);
        } else {
            assertEquals(expectedResult, minus(sourceLocalDateTime, amountToSubtract, timeUnit, zoneId));
        }
    }


    static Stream<Arguments> plusDateWithAmountToSubtractAndTimeUnitTestCases() {
        Date d1 = new GregorianCalendar(2022, Calendar.NOVEMBER, 11, 12, 10, 0).getTime();
        Date d2 = new GregorianCalendar(2022, Calendar.SEPTEMBER, 11, 12, 10, 0).getTime();
        Date d3 = new GregorianCalendar(2022, Calendar.SEPTEMBER, 11, 11, 10, 0).getTime();
        Date d4 = new GregorianCalendar(2022, Calendar.SEPTEMBER, 11, 11, 5, 0).getTime();

        Date expectedResultNullSourceNegativeAmountNullUnit = DateTimeUtil.fromLocalDateTimeToDate(LocalDateTime.now().plus(1, ChronoUnit.MINUTES));
        Date expectedResultNullSourceNegativeAmountAndSeconds = DateTimeUtil.fromLocalDateTimeToDate(LocalDateTime.now().plus(1, ChronoUnit.SECONDS));
        Date expectedResultNullSourcePositiveAmountAndMinutes = DateTimeUtil.fromLocalDateTimeToDate(LocalDateTime.now().plus(1, ChronoUnit.MINUTES));
        return Stream.of(
                //@formatter:off
                //            sourceDate,   amountToAdd,   timeUnit,             expectedResult
                Arguments.of( null,         -1,            null,                 expectedResultNullSourceNegativeAmountNullUnit ),
                Arguments.of( null,         -1,            ChronoUnit.SECONDS,   expectedResultNullSourceNegativeAmountAndSeconds ),
                Arguments.of( null,         1,             ChronoUnit.MINUTES,   expectedResultNullSourcePositiveAmountAndMinutes ),
                Arguments.of( d1,           0,             ChronoUnit.HOURS,     d1 ),
                Arguments.of( d1,           0,             ChronoUnit.DAYS,      d1 ),
                Arguments.of( d2,           2,             ChronoUnit.MONTHS,    d1 ),
                Arguments.of( d3,           1,             ChronoUnit.HOURS,     d2 ),
                Arguments.of( d4,           5,             ChronoUnit.MINUTES,   d3 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("plusDateWithAmountToSubtractAndTimeUnitTestCases")
    @DisplayName("plus: Date with amountToSubtract and TimeUnit test cases")
    public void plusDateWithAmountToAddAndTimeUnit_testCases(Date sourceDate,
                                                             long amountToAdd,
                                                             ChronoUnit timeUnit,
                                                             Date expectedResult) {
        if (null == sourceDate) {
            int compareResult = DateTimeUtil.compare(
                    plus(sourceDate, amountToAdd, timeUnit),
                    expectedResult,
                    5,
                    ChronoUnit.SECONDS
            );
            verifyCompareToResult(compareResult, CompareToResult.ZERO);
        } else {
            assertEquals(expectedResult, plus(sourceDate, amountToAdd, timeUnit));
        }
    }


    static Stream<Arguments> plusLocalDateTimeWithAmountToSubtractAndTimeUnitTestCases() {
        LocalDateTime ldt1 = LocalDateTime.of(2022, 11, 11, 12, 10, 0);
        LocalDateTime ldt2 = LocalDateTime.of(2022, 9, 11, 12, 10, 0);
        LocalDateTime ldt3 = LocalDateTime.of(2022, 9, 11, 11, 10, 0);
        LocalDateTime ldt4 = LocalDateTime.of(2022, 9, 11, 11, 5, 0);

        LocalDateTime expectedResultNullSourceNegativeAmountNullUnit = LocalDateTime.now().plus(1, ChronoUnit.MINUTES);
        LocalDateTime expectedResultNullSourceNegativeAmountAndSeconds = LocalDateTime.now().plus(1, ChronoUnit.SECONDS);
        LocalDateTime expectedResultNullSourcePositiveAmountAndMinutes = LocalDateTime.now().plus(1, ChronoUnit.MINUTES);
        return Stream.of(
                //@formatter:off
                //            sourceLocalDateTime,   amountToAdd,   timeUnit,             expectedResult
                Arguments.of( null,                  -1,            null,                 expectedResultNullSourceNegativeAmountNullUnit ),
                Arguments.of( null,                  -1,            ChronoUnit.SECONDS,   expectedResultNullSourceNegativeAmountAndSeconds ),
                Arguments.of( null,                  1,             ChronoUnit.MINUTES,   expectedResultNullSourcePositiveAmountAndMinutes ),
                Arguments.of( ldt1,                  0,             ChronoUnit.HOURS,     ldt1 ),
                Arguments.of( ldt1,                  0,             ChronoUnit.DAYS,      ldt1 ),
                Arguments.of( ldt2,                  2,             ChronoUnit.MONTHS,    ldt1 ),
                Arguments.of( ldt3,                  1,             ChronoUnit.HOURS,     ldt2 ),
                Arguments.of( ldt4,                  5,             ChronoUnit.MINUTES,   ldt3 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("plusLocalDateTimeWithAmountToSubtractAndTimeUnitTestCases")
    @DisplayName("plus: LocalDateTime with amountToSubtract and TimeUnit test cases")
    public void plusLocalDateTimeWithAmountToAddAndTimeUnit_testCases(LocalDateTime sourceLocalDateTime,
                                                                      long amountToAdd,
                                                                      ChronoUnit timeUnit,
                                                                      LocalDateTime expectedResult) {
        if (null == sourceLocalDateTime) {
            int compareResult = DateTimeUtil.compare(
                    plus(sourceLocalDateTime, amountToAdd, timeUnit),
                    expectedResult,
                    5,
                    ChronoUnit.SECONDS
            );
            verifyCompareToResult(compareResult, CompareToResult.ZERO);
        } else {
            assertEquals(expectedResult, plus(sourceLocalDateTime, amountToAdd, timeUnit));
        }
    }


    static Stream<Arguments> plusDateWithAmountToSubtractTimeUnitAndZoneIdTestCases() {
        ZoneId utcZoneId = ZoneId.of("UTC");
        ZoneId gmtZoneId = ZoneId.of("GMT");

        Date d1 = new GregorianCalendar(2022, Calendar.NOVEMBER, 11, 12, 10, 0).getTime();
        Date d2 = new GregorianCalendar(2022, Calendar.SEPTEMBER, 11, 12, 10, 0).getTime();
        Date d3 = new GregorianCalendar(2022, Calendar.SEPTEMBER, 11, 11, 10, 0).getTime();
        Date d4 = new GregorianCalendar(2022, Calendar.SEPTEMBER, 11, 11, 5, 0).getTime();

        GregorianCalendar gc1 = new GregorianCalendar(2022, Calendar.NOVEMBER, 11, 13, 10, 0);
        gc1.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        Date gc1Date = gc1.getTime();

        GregorianCalendar gc2 = new GregorianCalendar(2022, Calendar.SEPTEMBER, 11, 12, 10, 0);
        gc2.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date gc2Date = gc2.getTime();

        Date expectedResultNullSourceNegativeAmountNullUnitZoneId = DateTimeUtil.fromLocalDateTimeToDate(LocalDateTime.now(ZoneId.systemDefault()).plus(1, ChronoUnit.MINUTES), ZoneId.systemDefault());
        Date expectedResultNullSourceNegativeAmountNullUnitAndUtc = DateTimeUtil.fromLocalDateTimeToDate(LocalDateTime.now(utcZoneId).plus(1, ChronoUnit.MINUTES), utcZoneId);
        Date expectedResultNullSourceNegativeAmountAndSecondsAndNullZoneId = DateTimeUtil.fromLocalDateTimeToDate(LocalDateTime.now(ZoneId.systemDefault()).plus(1, ChronoUnit.SECONDS), ZoneId.systemDefault());
        Date expectedResultNullSourceNegativeAmountAndSecondsAndUtc = DateTimeUtil.fromLocalDateTimeToDate(LocalDateTime.now(utcZoneId).plus(1, ChronoUnit.SECONDS), utcZoneId);
        Date expectedResultNullSourcePositiveAmountAndMinutesAndNullZoneId = DateTimeUtil.fromLocalDateTimeToDate(LocalDateTime.now(ZoneId.systemDefault()).plus(1, ChronoUnit.MINUTES), ZoneId.systemDefault());
        Date expectedResultNullSourcePositiveAmountAndMinutesAndUtc = DateTimeUtil.fromLocalDateTimeToDate(LocalDateTime.now(utcZoneId).plus(1, ChronoUnit.MINUTES), utcZoneId);
        return Stream.of(
                //@formatter:off
                //            sourceDate,   amountToAdd,   timeUnit,             zoneId,      expectedResult
                Arguments.of( null,         -1,            null,                 null,        expectedResultNullSourceNegativeAmountNullUnitZoneId ),
                Arguments.of( null,         -1,            null,                 utcZoneId,   expectedResultNullSourceNegativeAmountNullUnitAndUtc ),
                Arguments.of( null,         -1,            ChronoUnit.SECONDS,   null,        expectedResultNullSourceNegativeAmountAndSecondsAndNullZoneId ),
                Arguments.of( null,         -1,            ChronoUnit.SECONDS,   utcZoneId,   expectedResultNullSourceNegativeAmountAndSecondsAndUtc ),
                Arguments.of( null,         1,             ChronoUnit.MINUTES,   null,        expectedResultNullSourcePositiveAmountAndMinutesAndNullZoneId ),
                Arguments.of( null,         1,             ChronoUnit.MINUTES,   utcZoneId,   expectedResultNullSourcePositiveAmountAndMinutesAndUtc ),
                Arguments.of( d1,           0,             ChronoUnit.HOURS,     null,        d1 ),
                Arguments.of( d1,           0,             ChronoUnit.HOURS,     utcZoneId,   d1 ),
                Arguments.of( d1,           0,             ChronoUnit.DAYS,      null,        d1 ),
                Arguments.of( d1,           0,             ChronoUnit.DAYS,      utcZoneId,   d1 ),
                Arguments.of( d2,           2,             ChronoUnit.MONTHS,    null,        d1 ),
                Arguments.of( d3,           1,             ChronoUnit.HOURS,     null,        d2 ),
                Arguments.of( d4,           5,             ChronoUnit.MINUTES,   null,        d3 ),
                Arguments.of( gc2Date,      2,             ChronoUnit.MONTHS,    gmtZoneId,   gc1Date )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("plusDateWithAmountToSubtractTimeUnitAndZoneIdTestCases")
    @DisplayName("plus: Date with amountToAdd, TimeUnit and ZoneId test cases")
    public void plusDateWithAmountToAddTimeUnitAndZoneId_testCases(Date sourceDate,
                                                                   long amountToAdd,
                                                                   ChronoUnit timeUnit,
                                                                   ZoneId zoneId,
                                                                   Date expectedResult) {
        if (null == sourceDate) {
            int compareResult = DateTimeUtil.compare(
                    plus(sourceDate, amountToAdd, timeUnit, zoneId),
                    expectedResult,
                    5,
                    ChronoUnit.SECONDS
            );
            verifyCompareToResult(compareResult, CompareToResult.ZERO);
        } else {
            assertEquals(expectedResult, plus(sourceDate, amountToAdd, timeUnit, zoneId));
        }
    }


    static Stream<Arguments> plusLocalDateTimeWithAmountToSubtractTimeUnitAndZoneIdTestCases() {
        ZoneId utcZoneId = ZoneId.of("UTC");
        ZoneId utcPlus1ZoneId = ZoneId.of("UTC+1");

        LocalDateTime ldt1 = LocalDateTime.of(2022, 11, 11, 12, 10, 0);
        LocalDateTime ldt2 = LocalDateTime.of(2022, 9, 11, 12, 10, 0);
        LocalDateTime ldt3 = LocalDateTime.of(2022, 9, 11, 11, 10, 0);
        LocalDateTime ldt4 = LocalDateTime.of(2022, 9, 11, 11, 5, 0);

        LocalDateTime ldtUtc = LocalDateTime.ofInstant(Instant.parse("2022-11-11T12:10:00Z"), utcZoneId);
        LocalDateTime ldtUtcPlus1 = LocalDateTime.ofInstant(Instant.parse("2022-09-11T11:10:00Z"), utcPlus1ZoneId);

        LocalDateTime expectedResultNullSourceNegativeAmountNullUnitZoneId = LocalDateTime.now().plus(1, ChronoUnit.MINUTES);
        LocalDateTime expectedResultNullSourceNegativeAmountNullUnitAndUtc = LocalDateTime.now().plus(1, ChronoUnit.MINUTES);
        LocalDateTime expectedResultNullSourceNegativeAmountAndSecondsAndNullZoneId = LocalDateTime.now().plus(1, ChronoUnit.SECONDS);
        LocalDateTime expectedResultNullSourceNegativeAmountAndSecondsAndUtc = LocalDateTime.now().plus(1, ChronoUnit.SECONDS);
        LocalDateTime expectedResultNullSourcePositiveAmountAndMinutesAndNullZoneId = LocalDateTime.now().plus(1, ChronoUnit.MINUTES);
        LocalDateTime expectedResultNullSourcePositiveAmountAndMinutesAndUtc = LocalDateTime.now().plus(1, ChronoUnit.MINUTES);
        return Stream.of(
                //@formatter:off
                //            sourceLocalDateTime,   amountToAdd,   timeUnit,   zoneId,                expectedResult
                Arguments.of( null,                  -1,            null,                 null,        expectedResultNullSourceNegativeAmountNullUnitZoneId ),
                Arguments.of( null,                  -1,            null,                 utcZoneId,   expectedResultNullSourceNegativeAmountNullUnitAndUtc ),
                Arguments.of( null,                  -1,            ChronoUnit.SECONDS,   null,        expectedResultNullSourceNegativeAmountAndSecondsAndNullZoneId ),
                Arguments.of( null,                  -1,            ChronoUnit.SECONDS,   utcZoneId,   expectedResultNullSourceNegativeAmountAndSecondsAndUtc ),
                Arguments.of( null,                  1,             ChronoUnit.MINUTES,   null,        expectedResultNullSourcePositiveAmountAndMinutesAndNullZoneId ),
                Arguments.of( null,                  1,             ChronoUnit.MINUTES,   utcZoneId,   expectedResultNullSourcePositiveAmountAndMinutesAndUtc ),
                Arguments.of( ldt1,                  0,             ChronoUnit.HOURS,     null,        ldt1 ),
                Arguments.of( ldt1,                  0,             ChronoUnit.HOURS,     utcZoneId,   ldt1 ),
                Arguments.of( ldt1,                  0,             ChronoUnit.DAYS,      null,        ldt1 ),
                Arguments.of( ldt1,                  0,             ChronoUnit.DAYS,      utcZoneId,   ldt1 ),
                Arguments.of( ldt2,                  2,             ChronoUnit.MONTHS,    null,        ldt1 ),
                Arguments.of( ldt3,                  1,             ChronoUnit.HOURS,     null,        ldt2 ),
                Arguments.of( ldt4,                  5,             ChronoUnit.MINUTES,   null,        ldt3 ),
                Arguments.of( ldtUtcPlus1,           2,             ChronoUnit.MONTHS,    null,        ldtUtc ),
                Arguments.of( ldtUtcPlus1,           2,             ChronoUnit.MONTHS,    utcZoneId,   ldtUtc )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("plusLocalDateTimeWithAmountToSubtractTimeUnitAndZoneIdTestCases")
    @DisplayName("plus: LocalDateTime with amountToAdd, TimeUnit and ZoneId test cases")
    public void plusLocalDateTimeWithAmountToAddTimeUnitAndZoneId_testCases(LocalDateTime sourceLocalDateTime,
                                                                            long amountToAdd,
                                                                            ChronoUnit timeUnit,
                                                                            ZoneId zoneId,
                                                                            LocalDateTime expectedResult) {
        if (null == sourceLocalDateTime) {
            int compareResult = DateTimeUtil.compare(
                    plus(sourceLocalDateTime, amountToAdd, timeUnit, zoneId),
                    expectedResult,
                    5,
                    ChronoUnit.SECONDS
            );
            verifyCompareToResult(compareResult, CompareToResult.ZERO);
        } else {
            assertEquals(expectedResult, plus(sourceLocalDateTime, amountToAdd, timeUnit, zoneId));
        }
    }



    private void verifyCompareToResult(int actualResult,
                                       CompareToResult expectedResult) {
        switch (expectedResult) {
            case LESS_THAN_ZERO -> assertTrue(0 > actualResult);
            case ZERO -> assertEquals(0, actualResult);
            case GREATER_THAN_ZERO -> assertTrue(0 < actualResult);
        }
    }


    private enum CompareToResult {
        LESS_THAN_ZERO,
        ZERO,
        GREATER_THAN_ZERO
    }

}
