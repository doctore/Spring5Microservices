package com.spring5microservices.common.collection.tuple;

import com.spring5microservices.common.interfaces.functional.TriFunction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Tuple3Test {

    static Stream<Arguments> ofTestCases() {
        String stringValue = "ABC";
        Integer integerValue = 25;
        Long longValue = 33L;
        return Stream.of(
                //@formatter:off
                //            t1,             t2,             t3,             expectedResult
                Arguments.of( null,           null,           null,           Tuple3.of(null, null, null) ),
                Arguments.of( stringValue,    null,           null,           Tuple3.of(stringValue, null, null) ),
                Arguments.of( null,           stringValue,    null,           Tuple3.of(null, stringValue, null) ),
                Arguments.of( null,           null,           stringValue,    Tuple3.of(null, null, stringValue) ),
                Arguments.of( null,           stringValue,    integerValue,   Tuple3.of(null, stringValue, integerValue) ),
                Arguments.of( stringValue,    integerValue,   null,           Tuple3.of(stringValue, integerValue, null) ),
                Arguments.of( stringValue,    null,           integerValue,   Tuple3.of(stringValue, null, integerValue) ),
                Arguments.of( stringValue,    integerValue,   longValue,      Tuple3.of(stringValue, integerValue, longValue) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("ofTestCases")
    @DisplayName("of: test cases")
    public <T1, T2, T3> void of_testCases(T1 t1,
                                          T2 t2,
                                          T3 t3,
                                          Tuple3<T1, T2, T3> expectedResult) {
        assertEquals(expectedResult, Tuple3.of(t1, t2, t3));
    }


    static Stream<Arguments> comparatorTestCases() {
        Tuple3<String, Integer, Long> stringIntegerLong1Tuple = Tuple3.of("A", 1, 3L);
        Tuple3<String, Integer, Long> stringIntegerLong2Tuple = Tuple3.of("B", 2, 2L);
        Comparator<String> defaultStringComparator = Comparator.naturalOrder();
        Comparator<String> reverseStringComparator = Comparator.reverseOrder();
        Comparator<Integer> defaultIntegerComparator = Comparator.naturalOrder();
        Comparator<Integer> reverseIntegerComparator = Comparator.reverseOrder();
        Comparator<Long> defaultLongComparator = Comparator.naturalOrder();
        Comparator<Long> reverseLongComparator = Comparator.reverseOrder();
        return Stream.of(
                //@formatter:off
                //            t1,                        t2,                        comparatorT1,              comparatorT2,               comparatorT3,            expectedResult
                Arguments.of( stringIntegerLong1Tuple,   stringIntegerLong1Tuple,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   0 ),
                Arguments.of( stringIntegerLong1Tuple,   stringIntegerLong1Tuple,   reverseStringComparator,   reverseIntegerComparator,   reverseLongComparator,   0 ),
                Arguments.of( stringIntegerLong1Tuple,   stringIntegerLong2Tuple,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,  -1 ),
                Arguments.of( stringIntegerLong1Tuple,   stringIntegerLong2Tuple,   reverseStringComparator,   reverseIntegerComparator,   reverseLongComparator,   1 ),
                Arguments.of( stringIntegerLong2Tuple,   stringIntegerLong1Tuple,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   1 ),
                Arguments.of( stringIntegerLong2Tuple,   stringIntegerLong1Tuple,   reverseStringComparator,   reverseIntegerComparator,   reverseLongComparator,  -1 ),
                Arguments.of( stringIntegerLong1Tuple,   stringIntegerLong2Tuple,   defaultStringComparator,   reverseIntegerComparator,   reverseLongComparator,  -1 ),
                Arguments.of( stringIntegerLong2Tuple,   stringIntegerLong1Tuple,   defaultStringComparator,   reverseIntegerComparator,   reverseLongComparator,   1 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("comparatorTestCases")
    @DisplayName("comparator: test cases")
    public <T1, T2, T3> void comparator_testCases(Tuple3<T1, T2, T3> t1,
                                                  Tuple3<T1, T2, T3> t2,
                                                  Comparator<T1> comparatorT1,
                                                  Comparator<T2> comparatorT2,
                                                  Comparator<T3> comparatorT3,
                                                  int expectedResult) {
        assertEquals(expectedResult, Tuple3.comparator(comparatorT1, comparatorT2, comparatorT3).compare(t1, t2));
    }


    static Stream<Arguments> compareToTestCases() {
        Tuple3<String, Integer, Long> stringIntegerLong1Tuple = Tuple3.of("A", 1, 3L);
        Tuple3<String, Integer, Long> stringIntegerLong2Tuple = Tuple3.of("B", 2, 2L);
        return Stream.of(
                //@formatter:off
                //            t1,                        t2,                        expectedResult
                Arguments.of( stringIntegerLong1Tuple,   stringIntegerLong1Tuple,   0 ),
                Arguments.of( stringIntegerLong1Tuple,   stringIntegerLong2Tuple,  -1 ),
                Arguments.of( stringIntegerLong2Tuple,   stringIntegerLong1Tuple,   1 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("compareToTestCases")
    @DisplayName("compareTo: test cases")
    public <T1, T2, T3> void compareTo_testCases(Tuple3<T1, T2, T3> t1,
                                                 Tuple3<T1, T2, T3> t2,
                                                 int expectedResult) {
        assertEquals(expectedResult, Tuple3.compareTo(t1, t2));
    }


    @Test
    @DisplayName("arity: when is invoked then 0 returned")
    public void arity_whenIsInvoked_then0IsReturned() {
        int result = Tuple3.of(1, "A", 3L).arity();
        assertEquals(3, result);
    }


    static Stream<Arguments> update1TestCases() {
        Tuple3<String, Integer, Long> stringIntegerLongTuple = Tuple3.of("A", 1, 33L);
        Tuple3<String, Integer, Long> updatedStringIntegerLongTuple = Tuple3.of("B", 1, 33L);
        return Stream.of(
                //@formatter:off
                //            tuple,                    value,                              expectedResult
                Arguments.of( stringIntegerLongTuple,   null,                               Tuple3.of(null, stringIntegerLongTuple._2, stringIntegerLongTuple._3) ),
                Arguments.of( stringIntegerLongTuple,   updatedStringIntegerLongTuple._1,   updatedStringIntegerLongTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("update1TestCases")
    @DisplayName("update1: test cases")
    public <T1, T2, T3> void update1_testCases(Tuple3<T1, T2, T3> tuple,
                                               T1 value,
                                               Tuple3<T1, T2, T3> expectedResult) {
        assertEquals(expectedResult, tuple.update1(value));
    }


    @Test
    @DisplayName("remove1: when is invoked then Tuple2 is returned")
    public void remove1_whenIsInvoked_thenTuple2IsReturned() {
        Tuple2<Integer, Long> result = Tuple3.of("A", 1, 3L).remove1();
        assertEquals(Tuple2.of(1, 3L), result);
    }


    static Stream<Arguments> update2TestCases() {
        Tuple3<String, Integer, Long> stringIntegerLongTuple = Tuple3.of("A", 1, 33L);
        Tuple3<String, Integer, Long> updatedStringIntegerLongTuple = Tuple3.of("A", 3, 33L);
        return Stream.of(
                //@formatter:off
                //            tuple,                    value,                              expectedResult
                Arguments.of( stringIntegerLongTuple,   null,                               Tuple3.of(stringIntegerLongTuple._1, null, stringIntegerLongTuple._3) ),
                Arguments.of( stringIntegerLongTuple,   updatedStringIntegerLongTuple._2,   updatedStringIntegerLongTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("update2TestCases")
    @DisplayName("update2: test cases")
    public <T1, T2, T3> void update2_testCases(Tuple3<T1, T2, T3> tuple,
                                               T2 value,
                                               Tuple3<T1, T2, T3> expectedResult) {
        assertEquals(expectedResult, tuple.update2(value));
    }


    @Test
    @DisplayName("remove2: when is invoked then Tuple2 is returned")
    public void remove2_whenIsInvoked_thenTuple2IsReturned() {
        Tuple2<String, Long> result = Tuple3.of("A", 1, 3L).remove2();
        assertEquals(Tuple2.of("A", 3L), result);
    }


    static Stream<Arguments> update3TestCases() {
        Tuple3<String, Integer, Long> stringIntegerLongTuple = Tuple3.of("A", 1, 33L);
        Tuple3<String, Integer, Long> updatedStringIntegerLongTuple = Tuple3.of("A", 1, 20L);
        return Stream.of(
                //@formatter:off
                //            tuple,                    value,                              expectedResult
                Arguments.of( stringIntegerLongTuple,   null,                               Tuple3.of(stringIntegerLongTuple._1, stringIntegerLongTuple._2, null) ),
                Arguments.of( stringIntegerLongTuple,   updatedStringIntegerLongTuple._3,   updatedStringIntegerLongTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("update3TestCases")
    @DisplayName("update3: test cases")
    public <T1, T2, T3> void update3_testCases(Tuple3<T1, T2, T3> tuple,
                                               T3 value,
                                               Tuple3<T1, T2, T3> expectedResult) {
        assertEquals(expectedResult, tuple.update3(value));
    }


    @Test
    @DisplayName("remove3: when is invoked then Tuple2 is returned")
    public void remove3_whenIsInvoked_thenTuple2IsReturned() {
        Tuple2<String, Integer> result = Tuple3.of("A", 1, 3L).remove3();
        assertEquals(Tuple2.of("A", 1), result);
    }


    static Stream<Arguments> mapTriFunctionTestCases() {
        Tuple3<String, Integer, Long> stringIntegerLongTuple = Tuple3.of("BC", 9, 12L);

        TriFunction<String, Integer, Long, Tuple3<String, Integer, Long>> identity = Tuple3::of;
        TriFunction<String, Integer, Long, Tuple3<Long, String, Integer>> fromStringIntegerLongToTuple =
                (s, i, l) -> Tuple3.of((long) s.length(), String.valueOf(l + 1), i * 3);

        Tuple3<Long, String, Integer> mappedStringIntegerLongTuple = Tuple3.of(2L, "13", 27);
        return Stream.of(
                //@formatter:off
                //            tuple,                    mapper,                         expectedException,                expectedResult
                Arguments.of( stringIntegerLongTuple,   null,                           IllegalArgumentException.class,   null ),
                Arguments.of( stringIntegerLongTuple,   identity,                       null,                             stringIntegerLongTuple ),
                Arguments.of( stringIntegerLongTuple,   fromStringIntegerLongToTuple,   null,                         mappedStringIntegerLongTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapTriFunctionTestCases")
    @DisplayName("map: using TriFunction test cases")
    public <T1, T2, T3, U1, U2, U3> void mapTriFunction_testCases(Tuple3<T1, T2, T3> tuple,
                                                                  TriFunction<? super T1, ? super T2, ? super T3, Tuple3<U1, U2, U3>> mapper,
                                                                  Class<? extends Exception> expectedException,
                                                                  Tuple3<U1, U2, U3> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> tuple.map(mapper));
        }
        else {
            assertEquals(expectedResult, tuple.map(mapper));
        }
    }


    static Stream<Arguments> mapFunctionTestCases() {
        Tuple3<String, Integer, Long> stringIntegerLongTuple = Tuple3.of("CFD", 92, 45L);
        Function<String, Long> fromStringToLong = s -> (long) (3 + s.length());
        Function<Integer, String> fromIntegerToString = i -> String.valueOf(i - 2);
        Function<Long, String> fromLongToString = l -> String.valueOf(l + 10);
        Tuple3<Long, String, String> mappedStringIntegerLongTuple = Tuple3.of(6L, "90", "55");
        return Stream.of(
                //@formatter:off
                //            tuple,                    f1,                 f2,                    f3,                 expectedException,                expectedResult
                Arguments.of( stringIntegerLongTuple,   null,               null,                  null,               IllegalArgumentException.class,   null ),
                Arguments.of( stringIntegerLongTuple,   fromStringToLong,   null,                  null,               IllegalArgumentException.class,   null ),
                Arguments.of( stringIntegerLongTuple,   fromStringToLong,   fromIntegerToString,   null,               IllegalArgumentException.class,   null ),
                Arguments.of( stringIntegerLongTuple,   fromStringToLong,   null,                  fromLongToString,   IllegalArgumentException.class,   null ),
                Arguments.of( stringIntegerLongTuple,   null,               fromIntegerToString,   fromLongToString,   IllegalArgumentException.class,   null ),
                Arguments.of( stringIntegerLongTuple,   fromStringToLong,   fromIntegerToString,   fromLongToString,   null,                             mappedStringIntegerLongTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapFunctionTestCases")
    @DisplayName("map: using Function test cases")
    public <T1, T2, T3, U1, U2, U3> void mapFunction_testCases(Tuple3<T1, T2, T3> tuple,
                                                               Function<? super T1, ? extends U1> f1,
                                                               Function<? super T2, ? extends U2> f2,
                                                               Function<? super T3, ? extends U3> f3,
                                                               Class<? extends Exception> expectedException,
                                                               Tuple3<U1, U2, U3> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> tuple.map(f1, f2, f3));
        }
        else {
            assertEquals(expectedResult, tuple.map(f1, f2, f3));
        }
    }

}
