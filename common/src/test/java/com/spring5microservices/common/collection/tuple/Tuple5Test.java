package com.spring5microservices.common.collection.tuple;

import com.spring5microservices.common.interfaces.functional.PentaFunction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Tuple5Test {

    static Stream<Arguments> ofTestCases() {
        String stringValue = "ABC";
        Integer integerValue = 25;
        Long longValue = 33L;
        Boolean booleanValue = TRUE;
        Double doubleValue = 11.3d;
        return Stream.of(
                //@formatter:off
                //            t1,             t2,             t3,             t4,             t5,            expectedResult
                Arguments.of( null,           null,           null,           null,           null,          Tuple5.of(null, null, null, null, null) ),
                Arguments.of( stringValue,    null,           null,           null,           null,          Tuple5.of(stringValue, null, null, null, null ) ),
                Arguments.of( null,           stringValue,    null,           null,           null,          Tuple5.of(null, stringValue, null, null, null) ),
                Arguments.of( null,           null,           stringValue,    null,           null,          Tuple5.of(null, null, stringValue, null, null) ),
                Arguments.of( null,           null,           null,           stringValue,    null,          Tuple5.of(null, null, null, stringValue, null) ),
                Arguments.of( null,           null,           null,           null,           stringValue,   Tuple5.of(null, null, null, null, stringValue) ),
                Arguments.of( null,           stringValue,    integerValue,   null,           null,          Tuple5.of(null, stringValue, integerValue, null, null) ),
                Arguments.of( stringValue,    integerValue,   null,           null,           null,          Tuple5.of(stringValue, integerValue, null, null, null) ),
                Arguments.of( stringValue,    null,           integerValue,   null,           doubleValue,   Tuple5.of(stringValue, null, integerValue, null, doubleValue) ),
                Arguments.of( stringValue,    integerValue,   longValue,      booleanValue,   doubleValue,   Tuple5.of(stringValue, integerValue, longValue, booleanValue, doubleValue) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("ofTestCases")
    @DisplayName("of: test cases")
    public <T1, T2, T3, T4, T5> void of_testCases(T1 t1,
                                                  T2 t2,
                                                  T3 t3,
                                                  T4 t4,
                                                  T5 t5,
                                                  Tuple5<T1, T2, T3, T4, T5> expectedResult) {
        assertEquals(expectedResult, Tuple5.of(t1, t2, t3, t4, t5));
    }


    @Test
    @DisplayName("empty: when is invoked then a tuple with all values a null is returned")
    public void empty_whenIsInvoked_thenTupleWithAllValuesEqualsNullIsReturned() {
        Tuple5<?, ?, ?, ?, ?> result = Tuple5.empty();
        assertNotNull(result);
        assertNull(result._1);
        assertNull(result._2);
        assertNull(result._3);
        assertNull(result._4);
        assertNull(result._5);
    }


    static Stream<Arguments> comparatorTestCases() {
        Tuple5<String, Integer, Long, Boolean, Double> t1 = Tuple5.of("A", 1, 3L, TRUE, 31.1d);
        Tuple5<String, Integer, Long, Boolean, Double> t2 = Tuple5.of("B", 2, 2L, FALSE, 11.9d);
        Comparator<String> defaultStringComparator = Comparator.naturalOrder();
        Comparator<String> reverseStringComparator = Comparator.reverseOrder();
        Comparator<Integer> defaultIntegerComparator = Comparator.naturalOrder();
        Comparator<Integer> reverseIntegerComparator = Comparator.reverseOrder();
        Comparator<Long> defaultLongComparator = Comparator.naturalOrder();
        Comparator<Long> reverseLongComparator = Comparator.reverseOrder();
        Comparator<Boolean> defaultBooleanComparator = Comparator.naturalOrder();
        Comparator<Boolean> reverseBooleanComparator = Comparator.reverseOrder();
        Comparator<Double> defaultDoubleComparator = Comparator.naturalOrder();
        Comparator<Double> reverseDoubleComparator = Comparator.reverseOrder();
        return Stream.of(
                //@formatter:off
                //            t1,   t2,   comparatorT1,              comparatorT2,               comparatorT3,            comparatorT4,               comparatorT5,              expectedResult
                Arguments.of( t1,   t1,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   defaultBooleanComparator,   defaultDoubleComparator,   0 ),
                Arguments.of( t1,   t1,   reverseStringComparator,   reverseIntegerComparator,   reverseLongComparator,   reverseBooleanComparator,   reverseDoubleComparator,   0 ),
                Arguments.of( t1,   t2,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   defaultBooleanComparator,   defaultDoubleComparator,  -1 ),
                Arguments.of( t1,   t2,   reverseStringComparator,   reverseIntegerComparator,   reverseLongComparator,   reverseBooleanComparator,   reverseDoubleComparator,   1 ),
                Arguments.of( t2,   t1,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   defaultBooleanComparator,   defaultDoubleComparator,   1 ),
                Arguments.of( t2,   t1,   reverseStringComparator,   reverseIntegerComparator,   reverseLongComparator,   reverseBooleanComparator,   reverseDoubleComparator,  -1 ),
                Arguments.of( t1,   t2,   defaultStringComparator,   reverseIntegerComparator,   reverseLongComparator,   reverseBooleanComparator,   reverseDoubleComparator,  -1 ),
                Arguments.of( t2,   t1,   defaultStringComparator,   reverseIntegerComparator,   reverseLongComparator,   reverseBooleanComparator,   reverseDoubleComparator,   1 ),
                Arguments.of( t1,   t2,   defaultStringComparator,   defaultIntegerComparator,   reverseLongComparator,   reverseBooleanComparator,   reverseDoubleComparator,  -1 ),
                Arguments.of( t2,   t1,   defaultStringComparator,   defaultIntegerComparator,   reverseLongComparator,   reverseBooleanComparator,   reverseDoubleComparator,   1 ),
                Arguments.of( t1,   t2,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   reverseBooleanComparator,   reverseDoubleComparator,  -1 ),
                Arguments.of( t2,   t1,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   reverseBooleanComparator,   reverseDoubleComparator,   1 ),
                Arguments.of( t1,   t2,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   defaultBooleanComparator,   reverseDoubleComparator,  -1 ),
                Arguments.of( t2,   t1,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   defaultBooleanComparator,   reverseDoubleComparator,   1 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("comparatorTestCases")
    @DisplayName("comparator: test cases")
    public <T1, T2, T3, T4, T5> void comparator_testCases(Tuple5<T1, T2, T3, T4, T5> t1,
                                                          Tuple5<T1, T2, T3, T4, T5> t2,
                                                          Comparator<T1> comparatorT1,
                                                          Comparator<T2> comparatorT2,
                                                          Comparator<T3> comparatorT3,
                                                          Comparator<T4> comparatorT4,
                                                          Comparator<T5> comparatorT5,
                                                          int expectedResult) {
        assertEquals(
                expectedResult,
                Tuple5.comparator(comparatorT1, comparatorT2, comparatorT3, comparatorT4, comparatorT5)
                        .compare(t1, t2)
        );
    }


    static Stream<Arguments> compareToTestCases() {
        Tuple5<String, Integer, Long, Boolean, Double> t1 = Tuple5.of("A", 1, 3L, TRUE, 53.1d);
        Tuple5<String, Integer, Long, Boolean, Double> t2 = Tuple5.of("B", 2, 2L, FALSE, 77.5d);
        return Stream.of(
                //@formatter:off
                //            t1,   t2,   expectedResult
                Arguments.of( t1,   t1,   0 ),
                Arguments.of( t1,   t2,  -1 ),
                Arguments.of( t2,   t1,   1 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("compareToTestCases")
    @DisplayName("compareTo: test cases")
    public <T1, T2, T3, T4, T5> void compareTo_testCases(Tuple5<T1, T2, T3, T4, T5> t1,
                                                         Tuple5<T1, T2, T3, T4, T5> t2,
                                                         int expectedResult) {
        assertEquals(expectedResult, Tuple5.compareTo(t1, t2));
    }


    @Test
    @DisplayName("arity: when is invoked then 0 returned")
    public void arity_whenIsInvoked_then0IsReturned() {
        int result = Tuple5.of(1, "A", 3L, TRUE, 44.0d).arity();
        assertEquals(5, result);
    }


    static Stream<Arguments> equalsTestCases() {
        Tuple5<String, Long, Integer, Boolean, Double> t1 = Tuple5.of("TYHG", 21L, 16, TRUE, 11.1d);
        Tuple5<Long, String, Integer, Boolean, Double> t2 = Tuple5.of(21L, "TYHG", 16, FALSE, 33.0d);
        return Stream.of(
                //@formatter:off
                //            tuple,   objectToCompare,   expectedResult
                Arguments.of( t1,      "1",               false ),
                Arguments.of( t2,      t2._1,             false ),
                Arguments.of( t1,      t2,                false ),
                Arguments.of( t1,      t1,                true ),
                Arguments.of( t2,      t2,                true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("equalsTestCases")
    @DisplayName("equals: test cases")
    public <T1, T2, T3, T4, T5> void equals_testCases(Tuple5<T1, T2, T3, T4, T5> tuple,
                                                      Object objectToCompare,
                                                      boolean expectedResult) {
        assertEquals(expectedResult, tuple.equals(objectToCompare));
    }


    @Test
    @DisplayName("hashCode: when is invoked then hash of internal elements is returned")
    public void hashCode_whenIsInvoked_thenHashCodeOfInternalElementsIsReturned() {
        Tuple5<Long, Integer, String, Boolean, Double> tuple = Tuple5.of(19L, 913, "XTHCY", TRUE, 41.1d);
        int expectedHashCode = Objects.hash(tuple._1, tuple._2, tuple._3, tuple._4, tuple._5);

        assertEquals(expectedHashCode, tuple.hashCode());
    }


    @Test
    @DisplayName("toString: when is invoked then toString of internal elements is returned")
    public void toString_whenIsInvoked_thenToStringOfInternalElementsIsReturned() {
        Tuple5<Long, Integer, String, Boolean, Double> tuple = Tuple5.of(191L, 91, "XCY", TRUE, 61.2d);
        String expectedToString = "(" + tuple._1 + ", " + tuple._2 + ", " + tuple._3 + ", " + tuple._4 + ", " + tuple._5 + ")";

        assertEquals(expectedToString, tuple.toString());
    }


    static Stream<Arguments> update1TestCases() {
        Tuple5<String, Integer, Long, Boolean, Double> tuple = Tuple5.of("A", 1, 33L, TRUE, 23.1d);
        Tuple5<String, Integer, Long, Boolean, Double> updatedTuple = Tuple5.of("B", 1, 33L, TRUE, 23.1d);
        return Stream.of(
                //@formatter:off
                //            tuple,    value,             expectedResult
                Arguments.of( tuple,   null,              Tuple5.of(null, tuple._2, tuple._3, tuple._4, tuple._5) ),
                Arguments.of( tuple,   updatedTuple._1,   updatedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("update1TestCases")
    @DisplayName("update1: test cases")
    public <T1, T2, T3, T4, T5> void update1_testCases(Tuple5<T1, T2, T3, T4, T5> tuple,
                                                       T1 value,
                                                       Tuple5<T1, T2, T3, T4, T5> expectedResult) {
        assertEquals(expectedResult, tuple.update1(value));
    }


    @Test
    @DisplayName("remove1: when is invoked then Tuple4 is returned")
    public void remove1_whenIsInvoked_thenTuple4IsReturned() {
        Tuple4<Integer, Long, Boolean, Double> result = Tuple5.of("A", 1, 3L, TRUE, 21.1d).remove1();
        assertEquals(Tuple4.of(1, 3L, TRUE, 21.1d), result);
    }


    static Stream<Arguments> update2TestCases() {
        Tuple5<String, Integer, Long, Boolean, Double> tuple = Tuple5.of("A", 1, 33L, TRUE, 23.1d);
        Tuple5<String, Integer, Long, Boolean, Double> updatedTuple = Tuple5.of("A", 3, 33L, TRUE, 23.1d);
        return Stream.of(
                //@formatter:off
                //            tuple,   value,             expectedResult
                Arguments.of( tuple,   null,              Tuple5.of(tuple._1, null, tuple._3, tuple._4, tuple._5) ),
                Arguments.of( tuple,   updatedTuple._2,   updatedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("update2TestCases")
    @DisplayName("update2: test cases")
    public <T1, T2, T3, T4, T5> void update2_testCases(Tuple5<T1, T2, T3, T4, T5> tuple,
                                                       T2 value,
                                                       Tuple5<T1, T2, T3, T4, T5> expectedResult) {
        assertEquals(expectedResult, tuple.update2(value));
    }


    @Test
    @DisplayName("remove2: when is invoked then Tuple4 is returned")
    public void remove2_whenIsInvoked_thenTuple4IsReturned() {
        Tuple4<String, Long, Boolean, Double> result = Tuple5.of("A", 1, 3L, TRUE, 21.1d).remove2();
        assertEquals(Tuple4.of("A", 3L, TRUE, 21.1d), result);
    }


    static Stream<Arguments> update3TestCases() {
        Tuple5<String, Integer, Long, Boolean, Double> tuple = Tuple5.of("A", 1, 33L, TRUE, 23.1d);
        Tuple5<String, Integer, Long, Boolean, Double> updatedTuple = Tuple5.of("A", 1, 44L, TRUE, 23.1d);
        return Stream.of(
                //@formatter:off
                //            tuple,   value,             expectedResult
                Arguments.of( tuple,   null,              Tuple5.of(tuple._1, tuple._2, null, tuple._4, tuple._5) ),
                Arguments.of( tuple,   updatedTuple._3,   updatedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("update3TestCases")
    @DisplayName("update3: test cases")
    public <T1, T2, T3, T4, T5> void update3_testCases(Tuple5<T1, T2, T3, T4, T5> tuple,
                                                       T3 value,
                                                       Tuple5<T1, T2, T3, T4, T5> expectedResult) {
        assertEquals(expectedResult, tuple.update3(value));
    }


    @Test
    @DisplayName("remove3: when is invoked then Tuple4 is returned")
    public void remove3_whenIsInvoked_thenTuple4IsReturned() {
        Tuple4<String, Integer, Boolean, Double> result = Tuple5.of("A", 1, 3L, TRUE, 21.1d).remove3();
        assertEquals(Tuple4.of("A", 1, TRUE, 21.1d), result);
    }


    static Stream<Arguments> update4TestCases() {
        Tuple5<String, Integer, Long, Boolean, Double> tuple = Tuple5.of("A", 1, 33L, TRUE, 23.1d);
        Tuple5<String, Integer, Long, Boolean, Double> updatedTuple = Tuple5.of("A", 1, 33L, FALSE, 23.1d);
        return Stream.of(
                //@formatter:off
                //            tuple,   value,             expectedResult
                Arguments.of( tuple,   null,              Tuple5.of(tuple._1, tuple._2, tuple._3, null, tuple._5) ),
                Arguments.of( tuple,   updatedTuple._4,   updatedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("update4TestCases")
    @DisplayName("update4: test cases")
    public <T1, T2, T3, T4, T5> void update4_testCases(Tuple5<T1, T2, T3, T4, T5> tuple,
                                                       T4 value,
                                                       Tuple5<T1, T2, T3, T4, T5> expectedResult) {
        assertEquals(expectedResult, tuple.update4(value));
    }


    @Test
    @DisplayName("remove4: when is invoked then Tuple4 is returned")
    public void remove4_whenIsInvoked_thenTuple4IsReturned() {
        Tuple4<String, Integer, Long, Double> result = Tuple5.of("A", 1, 3L, TRUE, 21.1d).remove4();
        assertEquals(Tuple4.of("A", 1, 3L, 21.1d), result);
    }


    static Stream<Arguments> update5TestCases() {
        Tuple5<String, Integer, Long, Boolean, Double> tuple = Tuple5.of("A", 1, 33L, TRUE, 23.1d);
        Tuple5<String, Integer, Long, Boolean, Double> updatedTuple = Tuple5.of("A", 1, 33L, TRUE, 34.7d);
        return Stream.of(
                //@formatter:off
                //            tuple,   value,             expectedResult
                Arguments.of( tuple,   null,              Tuple5.of(tuple._1, tuple._2, tuple._3, tuple._4, null) ),
                Arguments.of( tuple,   updatedTuple._5,   updatedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("update5TestCases")
    @DisplayName("update5: test cases")
    public <T1, T2, T3, T4, T5> void update5_testCases(Tuple5<T1, T2, T3, T4, T5> tuple,
                                                       T5 value,
                                                       Tuple5<T1, T2, T3, T4, T5> expectedResult) {
        assertEquals(expectedResult, tuple.update5(value));
    }


    @Test
    @DisplayName("remove5: when is invoked then Tuple4 is returned")
    public void remove5_whenIsInvoked_thenTuple4IsReturned() {
        Tuple4<String, Integer, Long, Boolean> result = Tuple5.of("A", 1, 3L, TRUE, 21.1d).remove5();
        assertEquals(Tuple4.of("A", 1, 3L, TRUE), result);
    }


    static Stream<Arguments> mapPentaFunctionTestCases() {
        Tuple5<String, Integer, Long, Boolean, Double> tuple = Tuple5.of("BC", 9, 12L, TRUE, 31.2d);

        PentaFunction<String, Integer, Long, Boolean, Double, Tuple5<String, Integer, Long, Boolean, Double>> identity = Tuple5::of;
        PentaFunction<String, Integer, Long, Boolean, Double, Tuple5<Long, String, Boolean, Integer, String>> mappedFunction =
                (s, i, l, b, d) -> Tuple5.of((long) s.length(), String.valueOf(l + 1), !b, i * 3, d.toString());

        Tuple5<Long, String, Boolean, Integer, String> mappedTuple = Tuple5.of(2L, "13", FALSE, 27, "31.2");
        return Stream.of(
                //@formatter:off
                //            tuple,   mapper,           expectedException,                expectedResult
                Arguments.of( tuple,   null,             IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   identity,         null,                             tuple ),
                Arguments.of( tuple,   mappedFunction,   null,                             mappedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapPentaFunctionTestCases")
    @DisplayName("map: using PentaFunction test cases")
    public <T1, T2, T3, T4, T5, U1, U2, U3, U4, U5> void mapPentaFunction_testCases(Tuple5<T1, T2, T3, T4, T5> tuple,
                                                                                    PentaFunction<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, Tuple5<U1, U2, U3, U4, U5>> mapper,
                                                                                    Class<? extends Exception> expectedException,
                                                                                    Tuple5<U1, U2, U3, U4, U5> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> tuple.map(mapper));
        }
        else {
            assertEquals(expectedResult, tuple.map(mapper));
        }
    }


    static Stream<Arguments> mapFunctionTestCases() {
        Tuple5<String, Integer, Long, Boolean, Double> tuple = Tuple5.of("CFD", 92, 45L, TRUE, 43.4d);
        Function<String, Long> fromStringToLong = s -> 3L + s.length();
        Function<Integer, String> fromIntegerToString = i -> String.valueOf(i - 2);
        Function<Long, String> fromLongToString = l -> String.valueOf(l + 10);
        Function<Boolean, String> fromBooleanToString = Object::toString;
        Function<Double, Integer> fromDoubleToInteger = Double::intValue;
        Tuple5<Long, String, String, String, Integer> mappedTuple = Tuple5.of(6L, "90", "55", "true", 43);
        return Stream.of(
                //@formatter:off
                //            tuple,   f1,                 f2,                    f3,                 f4,                    f5,                    expectedException,                expectedResult
                Arguments.of( tuple,   null,               null,                  null,               null,                  null,                  IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   fromStringToLong,   null,                  null,               null,                  null,                  IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   fromStringToLong,   fromIntegerToString,   null,               null,                  null,                  IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   fromStringToLong,   fromIntegerToString,   fromLongToString,   null,                  null,                  IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   fromStringToLong,   fromIntegerToString,   fromLongToString,   fromBooleanToString,   null,                  IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   fromStringToLong,   fromIntegerToString,   fromLongToString,   null,                  fromDoubleToInteger,   IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   fromStringToLong,   fromIntegerToString,   fromLongToString,   fromBooleanToString,   fromDoubleToInteger,   null,                             mappedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapFunctionTestCases")
    @DisplayName("map: using Function test cases")
    public <T1, T2, T3, T4, T5, U1, U2, U3, U4, U5> void mapFunction_testCases(Tuple5<T1, T2, T3, T4, T5> tuple,
                                                                               Function<? super T1, ? extends U1> f1,
                                                                               Function<? super T2, ? extends U2> f2,
                                                                               Function<? super T3, ? extends U3> f3,
                                                                               Function<? super T4, ? extends U4> f4,
                                                                               Function<? super T5, ? extends U5> f5,
                                                                               Class<? extends Exception> expectedException,
                                                                               Tuple5<U1, U2, U3, U4, U5> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> tuple.map(f1, f2, f3, f4, f5));
        }
        else {
            assertEquals(expectedResult, tuple.map(f1, f2, f3, f4, f5));
        }
    }


    static Stream<Arguments> map1TestCases() {
        Tuple5<String, Integer, Long, Boolean, Double> tuple = Tuple5.of("ZW", 23, 76L, TRUE, 521.45d);
        Function<String, Long> fromStringToLong = s -> 3L + s.length();
        Tuple5<Long, Integer, Long, Boolean, Double> mappedTuple = Tuple5.of(5L, 23, 76L, TRUE, 521.45d);
        return Stream.of(
                //@formatter:off
                //            tuple,   mapper,                expectedException,                expectedResult
                Arguments.of( tuple,   null,                  IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   Function.identity(),   null,                             tuple ),
                Arguments.of( tuple,   fromStringToLong,      null,                             mappedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("map1TestCases")
    @DisplayName("map1: test cases")
    public <T1, T2, T3, T4, T5, U> void map1_testCases(Tuple5<T1, T2, T3, T4, T5> tuple,
                                                       Function<? super T1, ? extends U> mapper,
                                                       Class<? extends Exception> expectedException,
                                                       Tuple5<U, T2, T3, T4, T5> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> tuple.map1(mapper));
        }
        else {
            assertEquals(expectedResult, tuple.map1(mapper));
        }
    }


    static Stream<Arguments> map2TestCases() {
        Tuple5<Integer, Integer, String, Boolean, Double> tuple = Tuple5.of(7, 9, "ERT", FALSE, 32.19d);
        Function<Integer, Long> fromIntegerToLong = i -> 2L * i;
        Tuple5<Integer, Long, String, Boolean, Double> mappedTuple = Tuple5.of(7, 18L, "ERT", FALSE, 32.19d);
        return Stream.of(
                //@formatter:off
                //            tuple,   mapper,                expectedException,                expectedResult
                Arguments.of( tuple,   null,                  IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   Function.identity(),   null,                             tuple ),
                Arguments.of( tuple,   fromIntegerToLong,     null,                             mappedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("map2TestCases")
    @DisplayName("map2: test cases")
    public <T1, T2, T3, T4, T5, U> void map2_testCases(Tuple5<T1, T2, T3, T4, T5> tuple,
                                                       Function<? super T2, ? extends U> mapper,
                                                       Class<? extends Exception> expectedException,
                                                       Tuple5<T1, U, T3, T4, T5> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> tuple.map2(mapper));
        }
        else {
            assertEquals(expectedResult, tuple.map2(mapper));
        }
    }


    static Stream<Arguments> map3TestCases() {
        Tuple5<Long, Long, String, Boolean, Double> tuple = Tuple5.of(15L, 99L, "GH", TRUE, 9.3d);
        Function<String, Long> fromStringToLong = s -> s.length() * 3L;
        Tuple5<Long, Long, Long, Boolean, Double> mappedTuple = Tuple5.of(15L, 99L, 6L, TRUE, 9.3d);
        return Stream.of(
                //@formatter:off
                //            tuple,   mapper,                expectedException,                expectedResult
                Arguments.of( tuple,   null,                  IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   Function.identity(),   null,                             tuple ),
                Arguments.of( tuple,   fromStringToLong,      null,                             mappedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("map3TestCases")
    @DisplayName("map3: test cases")
    public <T1, T2, T3, T4, T5, U> void map3_testCases(Tuple5<T1, T2, T3, T4, T5> tuple,
                                                       Function<? super T3, ? extends U> mapper,
                                                       Class<? extends Exception> expectedException,
                                                       Tuple5<T1, T2, U, T4, T5> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> tuple.map3(mapper));
        }
        else {
            assertEquals(expectedResult, tuple.map3(mapper));
        }
    }


    static Stream<Arguments> map4TestCases() {
        Tuple5<Long, Long, String, Boolean, Double> tuple = Tuple5.of(15L, 99L, "GH", TRUE, 11d);
        Function<Boolean, Boolean> fromBooleanToBoolean = b -> !b;
        Tuple5<Long, Long, String, Boolean, Double> mappedTuple = Tuple5.of(15L, 99L, "GH", FALSE, 11d);
        return Stream.of(
                //@formatter:off
                //            tuple,   mapper,                 expectedException,                expectedResult
                Arguments.of( tuple,   null,                   IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   Function.identity(),    null,                             tuple ),
                Arguments.of( tuple,   fromBooleanToBoolean,   null,                             mappedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("map4TestCases")
    @DisplayName("map4: test cases")
    public <T1, T2, T3, T4, T5, U> void map4_testCases(Tuple5<T1, T2, T3, T4, T5> tuple,
                                                       Function<? super T4, ? extends U> mapper,
                                                       Class<? extends Exception> expectedException,
                                                       Tuple5<T1, T2, T3, U, T5> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> tuple.map4(mapper));
        }
        else {
            assertEquals(expectedResult, tuple.map4(mapper));
        }
    }


    static Stream<Arguments> map5TestCases() {
        Tuple5<Long, Long, String, Boolean, Double> tuple = Tuple5.of(15L, 99L, "GH", TRUE, 12.132d);
        Function<Double, Integer> fromDoubleToInteger = Double::intValue;
        Tuple5<Long, Long, String, Boolean, Integer> mappedTuple = Tuple5.of(15L, 99L, "GH", TRUE, 12);
        return Stream.of(
                //@formatter:off
                //            tuple,   mapper,                expectedException,                expectedResult
                Arguments.of( tuple,   null,                  IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   Function.identity(),   null,                             tuple ),
                Arguments.of( tuple,   fromDoubleToInteger,   null,                             mappedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("map5TestCases")
    @DisplayName("map5: test cases")
    public <T1, T2, T3, T4, T5, U> void map5_testCases(Tuple5<T1, T2, T3, T4, T5> tuple,
                                                       Function<? super T5, ? extends U> mapper,
                                                       Class<? extends Exception> expectedException,
                                                       Tuple5<T1, T2, T3, T4, U> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> tuple.map5(mapper));
        }
        else {
            assertEquals(expectedResult, tuple.map5(mapper));
        }
    }


    static Stream<Arguments> applyTestCases() {
        Tuple5<Long, Integer, String, Integer, Double> tuple = Tuple5.of(12L, 93, "THC", 11, 99.8d);
        PentaFunction<Long, Integer, String, Integer, Double, Long> fromLongIntegerStringIntegerDoubleToLong = (l, i1, s, i2, d) -> l + i1 - s.length() + i2 + d.longValue();
        PentaFunction<Long, Integer, String, Integer, Double, String> fromLongIntegerStringIntegerDoubleToString = (l, i1, s, i2, d) -> i1 + l + s + i2 + d.toString();
        Long appliedLong = 212L;
        String appliedString = "105THC1199.8";
        return Stream.of(
                //@formatter:off
                //            tuple,   f,                                            expectedException,                expectedResult
                Arguments.of( tuple,   null,                                         IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   fromLongIntegerStringIntegerDoubleToLong,     null,                             appliedLong ),
                Arguments.of( tuple,   fromLongIntegerStringIntegerDoubleToString,   null,                             appliedString )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("applyTestCases")
    @DisplayName("apply: test cases")
    public <T1, T2, T3, T4, T5, U> void apply_testCases(Tuple5<T1, T2, T3, T4, T5> tuple,
                                                        PentaFunction<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? extends U> f,
                                                        Class<? extends Exception> expectedException,
                                                        U expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> tuple.apply(f));
        }
        else {
            assertEquals(expectedResult, tuple.apply(f));
        }
    }

}
