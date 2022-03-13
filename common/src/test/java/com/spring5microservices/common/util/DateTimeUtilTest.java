package com.spring5microservices.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Stream;

import static com.spring5microservices.common.util.DateTimeUtil.fromDateToLocalDateTime;
import static com.spring5microservices.common.util.DateTimeUtil.fromLocalDateTimeToDate;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DateTimeUtilTest {

    static Stream<Arguments> compareLocalDateTimeTestCases() {
        LocalDateTime ldt1 = LocalDateTime.of(2020, 11, 11, 12, 31, 00);
        LocalDateTime ldt2 = LocalDateTime.of(2020, 11, 11, 12, 31, 30);
        LocalDateTime ldt3 = LocalDateTime.of(2020, 11, 11, 12, 33, 00);

        return Stream.of(
                //@formatter:off
                //            one,    two,    epsilon,   timeUnit,             expectedException,                expectedResult
                Arguments.of( null,   null,   -1,        null,                 IllegalArgumentException.class,   null ),
                Arguments.of( null,   null,   1,         null,                 null,                             CompareToResult.ZERO ),
                Arguments.of( ldt1,   null,   1,         null,                 null,                             CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( null,   ldt1,   1,         null,                 null,                             CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( ldt1,   ldt2,   0,         null,                 null,                             CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( ldt2,   ldt1,   0,         null,                 null,                             CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( ldt1,   ldt2,   29,        ChronoUnit.SECONDS,   null,                             CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( ldt1,   ldt2,   30,        ChronoUnit.SECONDS,   null,                             CompareToResult.ZERO ),
                Arguments.of( ldt1,   ldt2,   31,        ChronoUnit.SECONDS,   null,                             CompareToResult.ZERO ),
                Arguments.of( ldt2,   ldt1,   29,        ChronoUnit.SECONDS,   null,                             CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( ldt2,   ldt1,   30,        ChronoUnit.SECONDS,   null,                             CompareToResult.ZERO ),
                Arguments.of( ldt2,   ldt1,   31,        ChronoUnit.SECONDS,   null,                             CompareToResult.ZERO ),
                Arguments.of( ldt1,   ldt3,   1,         ChronoUnit.MINUTES,   null,                             CompareToResult.LESS_THAN_ZERO ),
                Arguments.of( ldt1,   ldt3,   2,         ChronoUnit.MINUTES,   null,                             CompareToResult.ZERO ),
                Arguments.of( ldt3,   ldt1,   1,         ChronoUnit.MINUTES,   null,                             CompareToResult.GREATER_THAN_ZERO ),
                Arguments.of( ldt3,   ldt1,   2,         ChronoUnit.MINUTES,   null,                             CompareToResult.ZERO )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("compareLocalDateTimeTestCases")
    @DisplayName("compare: LocalDateTime test cases")
    public void compareLocalDateTime_testCases(LocalDateTime one, LocalDateTime two, long epsilon, ChronoUnit timeUnit,
                                               Class<? extends Exception> expectedException, CompareToResult expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> DateTimeUtil.compare(one, two, epsilon, timeUnit));
        }
        else {
            int result = DateTimeUtil.compare(one, two, epsilon, timeUnit);
            switch (expectedResult) {
                case LESS_THAN_ZERO: assertTrue(0 > result); break;
                case ZERO: assertEquals(0, result); break;
                case GREATER_THAN_ZERO: assertTrue(0 < result); break;
            }
        }
    }


    static Stream<Arguments> fromLocalDateTimeToDateTestCases() {
        ZoneId gmtZoneId = ZoneId.of("GMT");
        LocalDateTime ldt1 = LocalDateTime.of(2020, 10, 10, 12, 0, 0);
        LocalDateTime ldt2 = LocalDateTime.of(2022, 11, 12, 23, 0, 0);

        GregorianCalendar gc1 = new GregorianCalendar(2020, Calendar.OCTOBER, 10, 13, 0, 0);
        gc1.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        GregorianCalendar gc2 = new GregorianCalendar(2022, Calendar.NOVEMBER, 13, 1, 0, 0);
        gc2.setTimeZone(TimeZone.getTimeZone("GMT+2"));
        return Stream.of(
                //@formatter:off
                //            localDateTime,   zoneId,      expectedResult
                Arguments.of( null,            null,        empty() ),
                Arguments.of( ldt1,            gmtZoneId,   of(gc1.getTime()) ),
                Arguments.of( ldt2,            gmtZoneId,   of(gc2.getTime()) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromLocalDateTimeToDateTestCases")
    @DisplayName("fromLocalDateTimeToDate: test cases")
    public void fromLocalDateTimeToDate_testCases(LocalDateTime localDateTime, ZoneId zoneId, Optional<Date> expectedResult) {
        assertEquals(expectedResult, fromLocalDateTimeToDate(localDateTime, zoneId));
    }


    static Stream<Arguments> fromDateToLocalDateTimeTestCases() {
        ZoneId gmtZoneId = ZoneId.of("GMT");
        LocalDateTime ldt1 = LocalDateTime.of(2020, 10, 10, 12, 0, 0);
        LocalDateTime ldt2 = LocalDateTime.of(2022, 11, 12, 23, 0, 0);

        GregorianCalendar gc1 = new GregorianCalendar(2020, Calendar.OCTOBER, 10, 13, 0, 0);
        gc1.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        GregorianCalendar gc2 = new GregorianCalendar(2022, Calendar.NOVEMBER, 13, 1, 0, 0);
        gc2.setTimeZone(TimeZone.getTimeZone("GMT+2"));
        return Stream.of(
                //@formatter:off
                //            date,            zoneId,      expectedResult
                Arguments.of( null,            null,        empty() ),
                Arguments.of( gc1.getTime(),   gmtZoneId,   of(ldt1) ),
                Arguments.of( gc2.getTime(),   gmtZoneId,   of(ldt2) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromDateToLocalDateTimeTestCases")
    @DisplayName("fromDateToLocalDateTime: test cases")
    public void fromDateToLocalDateTime_testCases(Date date, ZoneId zoneId, Optional<LocalDateTime> expectedResult) {
        assertEquals(expectedResult, fromDateToLocalDateTime(date, zoneId));
    }


    private enum CompareToResult {
        LESS_THAN_ZERO,
        ZERO,
        GREATER_THAN_ZERO
    }

}
