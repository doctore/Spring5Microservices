package com.spring5microservices.common.collection.tuple;

import com.spring5microservices.common.interfaces.functional.QuadFunction;
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

public class Tuple4Test {

    static Stream<Arguments> ofTestCases() {
        String stringValue = "ABC";
        Integer integerValue = 25;
        Long longValue = 33L;
        Boolean booleanValue = TRUE;
        return Stream.of(
                //@formatter:off
                //            t1,             t2,             t3,             t4,             expectedResult
                Arguments.of( null,           null,           null,           null,           Tuple4.of(null, null, null, null) ),
                Arguments.of( stringValue,    null,           null,           null,           Tuple4.of(stringValue, null, null, null) ),
                Arguments.of( null,           stringValue,    null,           null,           Tuple4.of(null, stringValue, null, null) ),
                Arguments.of( null,           null,           stringValue,    null,           Tuple4.of(null, null, stringValue, null) ),
                Arguments.of( null,           null,           null,           stringValue,    Tuple4.of(null, null, null, stringValue) ),
                Arguments.of( null,           stringValue,    integerValue,   null,           Tuple4.of(null, stringValue, integerValue, null) ),
                Arguments.of( stringValue,    integerValue,   null,           null,           Tuple4.of(stringValue, integerValue, null, null) ),
                Arguments.of( stringValue,    null,           integerValue,   null,           Tuple4.of(stringValue, null, integerValue, null) ),
                Arguments.of( stringValue,    integerValue,   longValue,      booleanValue,   Tuple4.of(stringValue, integerValue, longValue, booleanValue) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("ofTestCases")
    @DisplayName("of: test cases")
    public <T1, T2, T3, T4> void of_testCases(T1 t1,
                                              T2 t2,
                                              T3 t3,
                                              T4 t4,
                                              Tuple4<T1, T2, T3, T4> expectedResult) {
        assertEquals(expectedResult, Tuple4.of(t1, t2, t3, t4));
    }


    @Test
    @DisplayName("empty: when is invoked then a tuple with all values a null is returned")
    public void empty_whenIsInvoked_thenTupleWithAllValuesEqualsNullIsReturned() {
        Tuple4<?, ?, ?, ?> result = Tuple4.empty();
        assertNotNull(result);
        assertNull(result._1);
        assertNull(result._2);
        assertNull(result._3);
        assertNull(result._4);
    }


    static Stream<Arguments> comparatorTestCases() {
        Tuple4<String, Integer, Long, Boolean> t1 = Tuple4.of("A", 1, 3L, TRUE);
        Tuple4<String, Integer, Long, Boolean> t2 = Tuple4.of("B", 2, 2L, FALSE);
        Comparator<String> defaultStringComparator = Comparator.naturalOrder();
        Comparator<String> reverseStringComparator = Comparator.reverseOrder();
        Comparator<Integer> defaultIntegerComparator = Comparator.naturalOrder();
        Comparator<Integer> reverseIntegerComparator = Comparator.reverseOrder();
        Comparator<Long> defaultLongComparator = Comparator.naturalOrder();
        Comparator<Long> reverseLongComparator = Comparator.reverseOrder();
        Comparator<Boolean> defaultBooleanComparator = Comparator.naturalOrder();
        Comparator<Boolean> reverseBooleanComparator = Comparator.reverseOrder();
        return Stream.of(
                //@formatter:off
                //            t1,   t2,   comparatorT1,              comparatorT2,               comparatorT3,            comparatorT4,               expectedResult
                Arguments.of( t1,   t1,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   defaultBooleanComparator,   0 ),
                Arguments.of( t1,   t1,   reverseStringComparator,   reverseIntegerComparator,   reverseLongComparator,   reverseBooleanComparator,   0 ),
                Arguments.of( t1,   t2,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   defaultBooleanComparator,  -1 ),
                Arguments.of( t1,   t2,   reverseStringComparator,   reverseIntegerComparator,   reverseLongComparator,   reverseBooleanComparator,   1 ),
                Arguments.of( t2,   t1,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   defaultBooleanComparator,   1 ),
                Arguments.of( t2,   t1,   reverseStringComparator,   reverseIntegerComparator,   reverseLongComparator,   reverseBooleanComparator,  -1 ),
                Arguments.of( t1,   t2,   defaultStringComparator,   reverseIntegerComparator,   reverseLongComparator,   reverseBooleanComparator,  -1 ),
                Arguments.of( t2,   t1,   defaultStringComparator,   reverseIntegerComparator,   reverseLongComparator,   reverseBooleanComparator,   1 ),
                Arguments.of( t1,   t2,   defaultStringComparator,   defaultIntegerComparator,   reverseLongComparator,   reverseBooleanComparator,  -1 ),
                Arguments.of( t2,   t1,   defaultStringComparator,   defaultIntegerComparator,   reverseLongComparator,   reverseBooleanComparator,   1 ),
                Arguments.of( t1,   t2,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   reverseBooleanComparator,  -1 ),
                Arguments.of( t2,   t1,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   reverseBooleanComparator,   1 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("comparatorTestCases")
    @DisplayName("comparator: test cases")
    public <T1, T2, T3, T4> void comparator_testCases(Tuple4<T1, T2, T3, T4> t1,
                                                      Tuple4<T1, T2, T3, T4> t2,
                                                      Comparator<T1> comparatorT1,
                                                      Comparator<T2> comparatorT2,
                                                      Comparator<T3> comparatorT3,
                                                      Comparator<T4> comparatorT4,
                                                      int expectedResult) {
        assertEquals(expectedResult, Tuple4.comparator(comparatorT1, comparatorT2, comparatorT3, comparatorT4).compare(t1, t2));
    }


    static Stream<Arguments> compareToTestCases() {
        Tuple4<String, Integer, Long, Boolean> t1 = Tuple4.of("A", 1, 3L, TRUE);
        Tuple4<String, Integer, Long, Boolean> t2 = Tuple4.of("B", 2, 2L, FALSE);
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
    public <T1, T2, T3, T4> void compareTo_testCases(Tuple4<T1, T2, T3, T4> t1,
                                                     Tuple4<T1, T2, T3, T4> t2,
                                                     int expectedResult) {
        assertEquals(expectedResult, Tuple4.compareTo(t1, t2));
    }


    @Test
    @DisplayName("arity: when is invoked then 0 returned")
    public void arity_whenIsInvoked_then0IsReturned() {
        int result = Tuple4.of(1, "A", 3L, TRUE).arity();
        assertEquals(4, result);
    }


    static Stream<Arguments> equalsTestCases() {
        Tuple4<String, Long, Integer, Boolean> t1 = Tuple4.of("TYHG", 21L, 16, TRUE);
        Tuple4<Long, String, Integer, Boolean> t2 = Tuple4.of(21L, "TYHG", 16, FALSE);
        Tuple4<String, Long, Integer, Boolean> t3 = Tuple4.of("TYHG", 21L, 16, TRUE);
        return Stream.of(
                //@formatter:off
                //            tuple,   objectToCompare,   expectedResult
                Arguments.of( t1,      null,              false ),
                Arguments.of( t1,      "1",               false ),
                Arguments.of( t2,      t2._1,             false ),
                Arguments.of( t1,      t2,                false ),
                Arguments.of( t1,      t1,                true ),
                Arguments.of( t2,      t2,                true ),
                Arguments.of( t1,      t3,                true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("equalsTestCases")
    @DisplayName("equals: test cases")
    public <T1, T2, T3, T4> void equals_testCases(Tuple4<T1, T2, T3, T4> tuple,
                                                  Object objectToCompare,
                                                  boolean expectedResult) {
        assertEquals(expectedResult, tuple.equals(objectToCompare));
    }


    @Test
    @DisplayName("hashCode: when is invoked then hash of internal elements is returned")
    public void hashCode_whenIsInvoked_thenHashCodeOfInternalElementsIsReturned() {
        Tuple4<Long, Integer, String, Boolean> tuple = Tuple4.of(19L, 913, "XTHCY", TRUE);

        int expectedHashCode = Objects.hash(tuple._1, tuple._2, tuple._3, tuple._4);

        assertEquals(expectedHashCode, tuple.hashCode());
    }


    @Test
    @DisplayName("toString: when is invoked then toString of internal elements is returned")
    public void toString_whenIsInvoked_thenToStringOfInternalElementsIsReturned() {
        Tuple4<Long, Integer, String, Boolean> tuple = Tuple4.of(191L, 91, "XCY", TRUE);
        String expectedToString = "(" + tuple._1 + ", " + tuple._2 + ", " + tuple._3 + ", " + tuple._4 + ")";

        assertEquals(expectedToString, tuple.toString());
    }


    static Stream<Arguments> update1TestCases() {
        Tuple4<String, Integer, Long, Boolean> tuple = Tuple4.of("A", 1, 33L, TRUE);
        Tuple4<String, Integer, Long, Boolean> updatedTuple = Tuple4.of("B", 1, 33L, TRUE);
        return Stream.of(
                //@formatter:off
                //            tuple,   value,             expectedResult
                Arguments.of( tuple,   null,              Tuple4.of(null, tuple._2, tuple._3, tuple._4) ),
                Arguments.of( tuple,   updatedTuple._1,   updatedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("update1TestCases")
    @DisplayName("update1: test cases")
    public <T1, T2, T3, T4> void update1_testCases(Tuple4<T1, T2, T3, T4> tuple,
                                                   T1 value,
                                                   Tuple4<T1, T2, T3, T4> expectedResult) {
        assertEquals(expectedResult, tuple.update1(value));
    }


    @Test
    @DisplayName("remove1: when is invoked then Tuple3 is returned")
    public void remove1_whenIsInvoked_thenTuple3IsReturned() {
        Tuple3<Integer, Long, Boolean> result = Tuple4.of("A", 1, 3L, TRUE).remove1();
        assertEquals(Tuple3.of(1, 3L, TRUE), result);
    }


    static Stream<Arguments> update2TestCases() {
        Tuple4<String, Integer, Long, Boolean> tuple = Tuple4.of("A", 1, 33L, TRUE);
        Tuple4<String, Integer, Long, Boolean> updatedTuple = Tuple4.of("A", 3, 33L, TRUE);
        return Stream.of(
                //@formatter:off
                //            tuple,   value,             expectedResult
                Arguments.of( tuple,   null,              Tuple4.of(tuple._1, null, tuple._3, tuple._4) ),
                Arguments.of( tuple,   updatedTuple._2,   updatedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("update2TestCases")
    @DisplayName("update2: test cases")
    public <T1, T2, T3, T4> void update2_testCases(Tuple4<T1, T2, T3, T4> tuple,
                                                   T2 value,
                                                   Tuple4<T1, T2, T3, T4> expectedResult) {
        assertEquals(expectedResult, tuple.update2(value));
    }


    @Test
    @DisplayName("remove2: when is invoked then Tuple3 is returned")
    public void remove2_whenIsInvoked_thenTuple3IsReturned() {
        Tuple3<String, Long, Boolean> result = Tuple4.of("A", 1, 3L, TRUE).remove2();
        assertEquals(Tuple3.of("A", 3L, TRUE), result);
    }


    static Stream<Arguments> update3TestCases() {
        Tuple4<String, Integer, Long, Boolean> tuple = Tuple4.of("A", 1, 33L, TRUE);
        Tuple4<String, Integer, Long, Boolean> updatedTuple = Tuple4.of("A", 1, 55L, TRUE);
        return Stream.of(
                //@formatter:off
                //            tuple,   value,             expectedResult
                Arguments.of( tuple,   null,              Tuple4.of(tuple._1, tuple._2, null, tuple._4) ),
                Arguments.of( tuple,   updatedTuple._3,   updatedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("update3TestCases")
    @DisplayName("update3: test cases")
    public <T1, T2, T3, T4> void update3_testCases(Tuple4<T1, T2, T3, T4> tuple,
                                                   T3 value,
                                                   Tuple4<T1, T2, T3, T4> expectedResult) {
        assertEquals(expectedResult, tuple.update3(value));
    }


    @Test
    @DisplayName("remove3: when is invoked then Tuple3 is returned")
    public void remove3_whenIsInvoked_thenTuple3IsReturned() {
        Tuple3<String, Integer, Boolean> result = Tuple4.of("A", 1, 3L, TRUE).remove3();
        assertEquals(Tuple3.of("A", 1, TRUE), result);
    }


    static Stream<Arguments> update4TestCases() {
        Tuple4<String, Integer, Long, Boolean> tuple = Tuple4.of("A", 1, 33L, TRUE);
        Tuple4<String, Integer, Long, Boolean> updatedTuple = Tuple4.of("A", 1, 33L, FALSE);
        return Stream.of(
                //@formatter:off
                //            tuple,   value,             expectedResult
                Arguments.of( tuple,   null,              Tuple4.of(tuple._1, tuple._2, tuple._3, null) ),
                Arguments.of( tuple,   updatedTuple._4,   updatedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("update4TestCases")
    @DisplayName("update4: test cases")
    public <T1, T2, T3, T4> void update4_testCases(Tuple4<T1, T2, T3, T4> tuple,
                                                   T4 value,
                                                   Tuple4<T1, T2, T3, T4> expectedResult) {
        assertEquals(expectedResult, tuple.update4(value));
    }


    @Test
    @DisplayName("remove4: when is invoked then Tuple3 is returned")
    public void remove4_whenIsInvoked_thenTuple3IsReturned() {
        Tuple3<String, Integer, Long> result = Tuple4.of("A", 1, 3L, TRUE).remove4();
        assertEquals(Tuple3.of("A", 1, 3L), result);
    }


    static Stream<Arguments> mapQuadFunctionTestCases() {
        Tuple4<String, Integer, Long, Boolean> tuple = Tuple4.of("BC", 9, 12L, TRUE);

        QuadFunction<String, Integer, Long, Boolean, Tuple4<String, Integer, Long, Boolean>> identity = Tuple4::of;
        QuadFunction<String, Integer, Long, Boolean, Tuple4<Long, String, Boolean, Integer>> mappedFunction =
                (s, i, l, b) -> Tuple4.of((long) s.length(), String.valueOf(l + 1), !b, i * 3);

        Tuple4<Long, String, Boolean, Integer> mappedTuple = Tuple4.of(2L, "13", FALSE, 27);
        return Stream.of(
                //@formatter:off
                //            tuple,   mapper,           expectedException,                expectedResult
                Arguments.of( tuple,   null,             IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   identity,         null,                             tuple ),
                Arguments.of( tuple,   mappedFunction,   null,                             mappedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapQuadFunctionTestCases")
    @DisplayName("map: using QuadFunction test cases")
    public <T1, T2, T3, T4, U1, U2, U3, U4> void mapQuadFunction_testCases(Tuple4<T1, T2, T3, T4> tuple,
                                                                           QuadFunction<? super T1, ? super T2, ? super T3, ? super T4, Tuple4<U1, U2, U3, U4>> mapper,
                                                                           Class<? extends Exception> expectedException,
                                                                           Tuple4<U1, U2, U3, U4> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> tuple.map(mapper));
        } else {
            assertEquals(expectedResult, tuple.map(mapper));
        }
    }


    static Stream<Arguments> mapFunctionTestCases() {
        Tuple4<String, Integer, Long, Boolean> tuple = Tuple4.of("CFD", 92, 45L, TRUE);
        Function<String, Long> fromStringToLong = s -> 3L + s.length();
        Function<Integer, String> fromIntegerToString = i -> String.valueOf(i - 2);
        Function<Long, String> fromLongToString = l -> String.valueOf(l + 10);
        Function<Boolean, String> fromBooleanToString = Object::toString;
        Tuple4<Long, String, String, String> mappedTuple = Tuple4.of(6L, "90", "55", "true");
        return Stream.of(
                //@formatter:off
                //            tuple,   f1,                 f2,                    f3,                 f4,                    expectedException,                expectedResult
                Arguments.of( tuple,   null,               null,                  null,               null,                  IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   fromStringToLong,   null,                  null,               null,                  IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   fromStringToLong,   fromIntegerToString,   null,               null,                  IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   fromStringToLong,   fromIntegerToString,   fromLongToString,   null,                  IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   fromStringToLong,   fromIntegerToString,   null,               fromBooleanToString,   IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   fromStringToLong,   fromIntegerToString,   fromLongToString,   fromBooleanToString,   null,                             mappedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapFunctionTestCases")
    @DisplayName("map: using Function test cases")
    public <T1, T2, T3, T4, U1, U2, U3, U4> void mapFunction_testCases(Tuple4<T1, T2, T3, T4> tuple,
                                                                       Function<? super T1, ? extends U1> f1,
                                                                       Function<? super T2, ? extends U2> f2,
                                                                       Function<? super T3, ? extends U3> f3,
                                                                       Function<? super T4, ? extends U4> f4,
                                                                       Class<? extends Exception> expectedException,
                                                                       Tuple4<U1, U2, U3, U4> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> tuple.map(f1, f2, f3, f4));
        } else {
            assertEquals(expectedResult, tuple.map(f1, f2, f3, f4));
        }
    }


    static Stream<Arguments> map1TestCases() {
        Tuple4<String, Integer, Long, Boolean> tuple = Tuple4.of("ZW", 23, 76L, TRUE);
        Function<String, Long> fromStringToLong = s -> 3L + s.length();
        Tuple4<Long, Integer, Long, Boolean> mappedTuple = Tuple4.of(5L, 23, 76L, TRUE);
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
    public <T1, T2, T3, T4, U> void map1_testCases(Tuple4<T1, T2, T3, T4> tuple,
                                                   Function<? super T1, ? extends U> mapper,
                                                   Class<? extends Exception> expectedException,
                                                   Tuple4<U, T2, T3, T4> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> tuple.map1(mapper));
        } else {
            assertEquals(expectedResult, tuple.map1(mapper));
        }
    }


    static Stream<Arguments> map2TestCases() {
        Tuple4<Integer, Integer, String, Boolean> tuple = Tuple4.of(7, 9, "ERT", FALSE);
        Function<Integer, Long> fromIntegerToLong = i -> 2L * i;
        Tuple4<Integer, Long, String, Boolean> mappedTuple = Tuple4.of(7, 18L, "ERT", FALSE);
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
    public <T1, T2, T3, T4, U> void map2_testCases(Tuple4<T1, T2, T3, T4> tuple,
                                                   Function<? super T2, ? extends U> mapper,
                                                   Class<? extends Exception> expectedException,
                                                   Tuple4<T1, U, T3, T4> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> tuple.map2(mapper));
        } else {
            assertEquals(expectedResult, tuple.map2(mapper));
        }
    }


    static Stream<Arguments> map3TestCases() {
        Tuple4<Long, Long, String, Boolean> tuple = Tuple4.of(15L, 99L, "GH", TRUE);
        Function<String, Long> fromStringToLong = s -> s.length() * 3L;
        Tuple4<Long, Long, Long, Boolean> mappedTuple = Tuple4.of(15L, 99L, 6L, TRUE);
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
    public <T1, T2, T3, T4, U> void map3_testCases(Tuple4<T1, T2, T3, T4> tuple,
                                                   Function<? super T3, ? extends U> mapper,
                                                   Class<? extends Exception> expectedException,
                                                   Tuple4<T1, T2, U, T4> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> tuple.map3(mapper));
        } else {
            assertEquals(expectedResult, tuple.map3(mapper));
        }
    }


    static Stream<Arguments> map4TestCases() {
        Tuple4<Long, Long, String, Boolean> tuple = Tuple4.of(15L, 99L, "GH", TRUE);
        Function<Boolean, Boolean> fromBooleanToBoolean = b -> !b;
        Tuple4<Long, Long, String, Boolean> mappedTuple = Tuple4.of(15L, 99L, "GH", FALSE);
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
    public <T1, T2, T3, T4, U> void map4_testCases(Tuple4<T1, T2, T3, T4> tuple,
                                                   Function<? super T4, ? extends U> mapper,
                                                   Class<? extends Exception> expectedException,
                                                   Tuple4<T1, T2, T3, U> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> tuple.map4(mapper));
        } else {
            assertEquals(expectedResult, tuple.map4(mapper));
        }
    }


    static Stream<Arguments> applyTestCases() {
        Tuple4<Long, Integer, String, Integer> tuple = Tuple4.of(12L, 93, "THC", 11);
        QuadFunction<Long, Integer, String, Integer, Long> fromLongIntegerStringIntegerToLong =
                (l, i1, s, i2) -> l + i1 - s.length() + i2;
        QuadFunction<Long, Integer, String, Integer, String> fromLongIntegerStringIntegerToString =
                (l, i1, s, i2) -> i1 + l + s + i2;

        Long appliedLong = 113L;
        String appliedString = "105THC11";
        return Stream.of(
                //@formatter:off
                //            tuple,   f,                                      expectedException,                expectedResult
                Arguments.of( tuple,   null,                                   IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   fromLongIntegerStringIntegerToLong,     null,                             appliedLong ),
                Arguments.of( tuple,   fromLongIntegerStringIntegerToString,   null,                             appliedString )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("applyTestCases")
    @DisplayName("apply: test cases")
    public <T1, T2, T3, T4, U> void apply_testCases(Tuple4<T1, T2, T3, T4> tuple,
                                                    QuadFunction<? super T1, ? super T2, ? super T3, ? super T4, ? extends U> f,
                                                    Class<? extends Exception> expectedException,
                                                    U expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> tuple.apply(f));
        } else {
            assertEquals(expectedResult, tuple.apply(f));
        }
    }


    static Stream<Arguments> prependTestCases() {
        Tuple4<String, Integer, Boolean, Long> tuple = Tuple4.of("ZZ", 77, TRUE, 45L);
        Long longValue = 34L;
        Integer integerValue = 55;
        return Stream.of(
                //@formatter:off
                //            tuple,   value,          expectedResult
                Arguments.of( tuple,   null,           Tuple5.of(null, tuple._1, tuple._2, tuple._3, tuple._4) ),
                Arguments.of( tuple,   longValue,      Tuple5.of(longValue, tuple._1, tuple._2, tuple._3, tuple._4) ),
                Arguments.of( tuple,   integerValue,   Tuple5.of(integerValue, tuple._1, tuple._2, tuple._3, tuple._4) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("prependTestCases")
    @DisplayName("prepend: test cases")
    public <T, T1, T2, T3, T4> void prepend_testCases(Tuple4<T1, T2, T3, T4> tuple,
                                                      T value,
                                                      Tuple5<T, T1, T2, T3, T4> expectedResult) {
        assertEquals(expectedResult, tuple.prepend(value));
    }


    static Stream<Arguments> appendTestCases() {
        Tuple4<String, Integer, Boolean, Long> tuple = Tuple4.of("ABC", 41, FALSE, 67L);
        Long longValue = 11L;
        Integer integerValue = 66;
        return Stream.of(
                //@formatter:off
                //            tuple,   value,          expectedResult
                Arguments.of( tuple,   null,           Tuple5.of(tuple._1, tuple._2, tuple._3, tuple._4, null) ),
                Arguments.of( tuple,   longValue,      Tuple5.of(tuple._1, tuple._2, tuple._3, tuple._4, longValue) ),
                Arguments.of( tuple,   integerValue,   Tuple5.of(tuple._1, tuple._2, tuple._3, tuple._4, integerValue) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("appendTestCases")
    @DisplayName("append: test cases")
    public <T, T1, T2, T3, T4> void append_testCases(Tuple4<T1, T2, T3, T4> tuple,
                                                     T value,
                                                     Tuple5<T1, T2, T3, T4, T> expectedResult) {
        assertEquals(expectedResult, tuple.append(value));
    }


    static Stream<Arguments> concatTuple1TestCases() {
        Tuple4<String, Integer, Boolean, Long> t1 = Tuple4.of("YHG", 33, TRUE, 89L);
        Tuple1<Long> t2 = Tuple1.of(21L);
        Tuple1<Integer> nullValueTuple = Tuple1.of(null);
        return Stream.of(
                //@formatter:off
                //            tuple,   tupleToConcat,    expectedResult
                Arguments.of( t1,      null,             Tuple5.of(t1._1, t1._2, t1._3, t1._4, null) ),
                Arguments.of( t1,      nullValueTuple,   Tuple5.of(t1._1, t1._2, t1._3, t1._4, null) ),
                Arguments.of( t1,      t2,               Tuple5.of(t1._1, t1._2, t1._3, t1._4, t2._1) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("concatTuple1TestCases")
    @DisplayName("concat: using Tuple1 test cases")
    public <T1, T2, T3, T4, T5> void concatTuple1_testCases(Tuple4<T1, T2, T3, T4> tuple,
                                                            Tuple1<T5> tupleToConcat,
                                                            Tuple5<T1, T2, T3, T4, T5> expectedResult) {
        assertEquals(expectedResult, tuple.concat(tupleToConcat));
    }


    static Stream<Arguments> concatTuple2TestCases() {
        Tuple4<String, Integer, Boolean, Long> t1 = Tuple4.of("YHG", 33, TRUE, 89L);
        Tuple2<Long, Double> t2 = Tuple2.of(21L, 99.9d);
        Tuple2<Integer, Float> nullValueTuple = Tuple2.of(null, null);
        return Stream.of(
                //@formatter:off
                //            tuple,   tupleToConcat,    expectedResult
                Arguments.of( t1,      null,             Tuple6.of(t1._1, t1._2, t1._3, t1._4, null, null) ),
                Arguments.of( t1,      nullValueTuple,   Tuple6.of(t1._1, t1._2, t1._3, t1._4, null, null) ),
                Arguments.of( t1,      t2,               Tuple6.of(t1._1, t1._2, t1._3, t1._4, t2._1, t2._2) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("concatTuple2TestCases")
    @DisplayName("concat: using Tuple2 test cases")
    public <T1, T2, T3, T4, T5, T6> void concatTuple2_testCases(Tuple4<T1, T2, T3, T4> tuple,
                                                                Tuple2<T5, T6> tupleToConcat,
                                                                Tuple6<T1, T2, T3, T4, T5, T6> expectedResult) {
        assertEquals(expectedResult, tuple.concat(tupleToConcat));
    }

}
