package com.spring5microservices.common.collection.tuple;

import com.spring5microservices.common.interfaces.functional.HeptaFunction;
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

public class Tuple7Test {

    static Stream<Arguments> ofTestCases() {
        String stringValue = "ABC";
        Integer integerValue = 25;
        Long longValue = 33L;
        Boolean booleanValue = TRUE;
        Double doubleValue = 11.3d;
        Float floatValue = 19.11f;
        Short shortValue = 9;
        return Stream.of(
                //@formatter:off
                //            t1,             t2,             t3,             t4,             t5,            t6,            t7,            expectedResult
                Arguments.of( null,           null,           null,           null,           null,          null,          null,          Tuple7.of(null, null, null, null, null, null, null) ),
                Arguments.of( stringValue,    null,           null,           null,           null,          null,          null,          Tuple7.of(stringValue, null, null, null, null, null, null) ),
                Arguments.of( null,           stringValue,    null,           null,           null,          null,          null,          Tuple7.of(null, stringValue, null, null, null, null, null) ),
                Arguments.of( null,           null,           stringValue,    null,           null,          null,          null,          Tuple7.of(null, null, stringValue, null, null, null, null) ),
                Arguments.of( null,           null,           null,           stringValue,    null,          null,          null,          Tuple7.of(null, null, null, stringValue, null, null, null) ),
                Arguments.of( null,           null,           null,           null,           stringValue,   null,          null,          Tuple7.of(null, null, null, null, stringValue, null, null) ),
                Arguments.of( null,           null,           null,           null,           null,          stringValue,   null,          Tuple7.of(null, null, null, null, null, stringValue, null) ),
                Arguments.of( null,           null,           null,           null,           null,          null,          stringValue,   Tuple7.of(null, null, null, null, null, null, stringValue) ),
                Arguments.of( null,           stringValue,    integerValue,   null,           null,          null,          null,          Tuple7.of(null, stringValue, integerValue, null, null, null, null) ),
                Arguments.of( stringValue,    integerValue,   null,           null,           null,          null,          null,          Tuple7.of(stringValue, integerValue, null, null, null, null, null) ),
                Arguments.of( stringValue,    null,           integerValue,   null,           doubleValue,   null,          shortValue,    Tuple7.of(stringValue, null, integerValue, null, doubleValue, null, shortValue) ),
                Arguments.of( stringValue,    integerValue,   longValue,      booleanValue,   doubleValue,   floatValue,    shortValue,    Tuple7.of(stringValue, integerValue, longValue, booleanValue, doubleValue, floatValue, shortValue) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("ofTestCases")
    @DisplayName("of: test cases")
    public <T1, T2, T3, T4, T5, T6, T7> void of_testCases(T1 t1,
                                                          T2 t2,
                                                          T3 t3,
                                                          T4 t4,
                                                          T5 t5,
                                                          T6 t6,
                                                          T7 t7,
                                                          Tuple7<T1, T2, T3, T4, T5, T6, T7> expectedResult) {
        assertEquals(expectedResult, Tuple7.of(t1, t2, t3, t4, t5, t6, t7));
    }


    @Test
    @DisplayName("empty: when is invoked then a tuple with all values a null is returned")
    public void empty_whenIsInvoked_thenTupleWithAllValuesEqualsNullIsReturned() {
        Tuple7<?, ?, ?, ?, ?, ?, ?> result = Tuple7.empty();
        assertNotNull(result);
        assertNull(result._1);
        assertNull(result._2);
        assertNull(result._3);
        assertNull(result._4);
        assertNull(result._5);
        assertNull(result._6);
        assertNull(result._7);
    }


    static Stream<Arguments> comparatorTestCases() {
        Tuple7<String, Integer, Long, Boolean, Double, Float, Short> t1 = Tuple7.of("A", 1, 3L, TRUE, 31.1d, 23.1f, (short)33);
        Tuple7<String, Integer, Long, Boolean, Double, Float, Short> t2 = Tuple7.of("B", 2, 2L, FALSE, 11.9d, 22f, (short)49);
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
        Comparator<Float> defaultFloatComparator = Comparator.naturalOrder();
        Comparator<Float> reverseFloatComparator = Comparator.reverseOrder();
        Comparator<Short> defaultShortComparator = Comparator.naturalOrder();
        Comparator<Short> reverseShortComparator = Comparator.reverseOrder();
        return Stream.of(
                //@formatter:off
                //            t1,   t2,   comparatorT1,              comparatorT2,               comparatorT3,            comparatorT4,               comparatorT5,              comparatorT6,             comparatorT7,             expectedResult
                Arguments.of( t1,   t1,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   defaultBooleanComparator,   defaultDoubleComparator,   defaultFloatComparator,   defaultShortComparator,   0 ),
                Arguments.of( t1,   t1,   reverseStringComparator,   reverseIntegerComparator,   reverseLongComparator,   reverseBooleanComparator,   reverseDoubleComparator,   reverseFloatComparator,   reverseShortComparator,   0 ),
                Arguments.of( t1,   t2,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   defaultBooleanComparator,   defaultDoubleComparator,   defaultFloatComparator,   defaultShortComparator,   -1 ),
                Arguments.of( t1,   t2,   reverseStringComparator,   reverseIntegerComparator,   reverseLongComparator,   reverseBooleanComparator,   reverseDoubleComparator,   reverseFloatComparator,   reverseShortComparator,   1 ),
                Arguments.of( t2,   t1,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   defaultBooleanComparator,   defaultDoubleComparator,   defaultFloatComparator,   defaultShortComparator,   1 ),
                Arguments.of( t2,   t1,   reverseStringComparator,   reverseIntegerComparator,   reverseLongComparator,   reverseBooleanComparator,   reverseDoubleComparator,   reverseFloatComparator,   reverseShortComparator,   -1 ),
                Arguments.of( t1,   t2,   defaultStringComparator,   reverseIntegerComparator,   reverseLongComparator,   reverseBooleanComparator,   reverseDoubleComparator,   reverseFloatComparator,   reverseShortComparator,   -1 ),
                Arguments.of( t2,   t1,   defaultStringComparator,   reverseIntegerComparator,   reverseLongComparator,   reverseBooleanComparator,   reverseDoubleComparator,   reverseFloatComparator,   reverseShortComparator,   1 ),
                Arguments.of( t1,   t2,   defaultStringComparator,   defaultIntegerComparator,   reverseLongComparator,   reverseBooleanComparator,   reverseDoubleComparator,   reverseFloatComparator,   reverseShortComparator,   -1 ),
                Arguments.of( t2,   t1,   defaultStringComparator,   defaultIntegerComparator,   reverseLongComparator,   reverseBooleanComparator,   reverseDoubleComparator,   reverseFloatComparator,   reverseShortComparator,   1 ),
                Arguments.of( t1,   t2,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   reverseBooleanComparator,   reverseDoubleComparator,   reverseFloatComparator,   reverseShortComparator,   -1 ),
                Arguments.of( t2,   t1,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   reverseBooleanComparator,   reverseDoubleComparator,   reverseFloatComparator,   reverseShortComparator,   1 ),
                Arguments.of( t1,   t2,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   defaultBooleanComparator,   reverseDoubleComparator,   reverseFloatComparator,   reverseShortComparator,   -1 ),
                Arguments.of( t2,   t1,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   defaultBooleanComparator,   reverseDoubleComparator,   reverseFloatComparator,   reverseShortComparator,   1 ),
                Arguments.of( t1,   t2,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   defaultBooleanComparator,   defaultDoubleComparator,   reverseFloatComparator,   reverseShortComparator,   -1 ),
                Arguments.of( t2,   t1,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   defaultBooleanComparator,   defaultDoubleComparator,   reverseFloatComparator,   reverseShortComparator,   1 ),
                Arguments.of( t1,   t2,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   defaultBooleanComparator,   defaultDoubleComparator,   defaultFloatComparator,   reverseShortComparator,   -1 ),
                Arguments.of( t2,   t1,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   defaultBooleanComparator,   defaultDoubleComparator,   defaultFloatComparator,   reverseShortComparator,   1 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("comparatorTestCases")
    @DisplayName("comparator: test cases")
    public <T1, T2, T3, T4, T5, T6, T7> void comparator_testCases(Tuple7<T1, T2, T3, T4, T5, T6, T7> t1,
                                                                  Tuple7<T1, T2, T3, T4, T5, T6, T7> t2,
                                                                  Comparator<T1> comparatorT1,
                                                                  Comparator<T2> comparatorT2,
                                                                  Comparator<T3> comparatorT3,
                                                                  Comparator<T4> comparatorT4,
                                                                  Comparator<T5> comparatorT5,
                                                                  Comparator<T6> comparatorT6,
                                                                  Comparator<T7> comparatorT7,
                                                                  int expectedResult) {
        assertEquals(
                expectedResult,
                Tuple7.comparator(comparatorT1, comparatorT2, comparatorT3, comparatorT4, comparatorT5, comparatorT6, comparatorT7)
                        .compare(t1, t2)
        );
    }


    static Stream<Arguments> compareToTestCases() {
        Tuple7<String, Integer, Long, Boolean, Double, Float, Short> t1 = Tuple7.of("A", 1, 3L, TRUE, 53.1d, 67f, (short)33);
        Tuple7<String, Integer, Long, Boolean, Double, Float, Short> t2 = Tuple7.of("B", 2, 2L, FALSE, 77.5d, 80f, (short)22);
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
    public <T1, T2, T3, T4, T5, T6, T7> void compareTo_testCases(Tuple7<T1, T2, T3, T4, T5, T6, T7> t1,
                                                                 Tuple7<T1, T2, T3, T4, T5, T6, T7> t2,
                                                                 int expectedResult) {
        assertEquals(expectedResult, Tuple7.compareTo(t1, t2));
    }


    @Test
    @DisplayName("arity: when is invoked then 0 returned")
    public void arity_whenIsInvoked_then0IsReturned() {
        int result = Tuple7.of(1, "A", 3L, TRUE, 44.0d, 23f, (short)49).arity();
        assertEquals(7, result);
    }


    static Stream<Arguments> equalsTestCases() {
        Tuple7<String, Long, Integer, Boolean, Double, Float, Short> t1 = Tuple7.of("TYHG", 21L, 16, TRUE, 11.1d, 44f, (short)9);
        Tuple7<Long, String, Integer, Boolean, Double, Float, Short> t2 = Tuple7.of(21L, "TYHG", 16, FALSE, 33.0d, 43f, (short)12);
        Tuple7<String, Long, Integer, Boolean, Double, Float, Short> t3 = Tuple7.of("TYHG", 21L, 16, TRUE, 11.1d, 44f, (short)9);
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
    public <T1, T2, T3, T4, T5, T6, T7> void equals_testCases(Tuple7<T1, T2, T3, T4, T5, T6, T7> tuple,
                                                              Object objectToCompare,
                                                              boolean expectedResult) {
        assertEquals(expectedResult, tuple.equals(objectToCompare));
    }


    @Test
    @DisplayName("hashCode: when is invoked then hash of internal elements is returned")
    public void hashCode_whenIsInvoked_thenHashCodeOfInternalElementsIsReturned() {
        Tuple7<Long, Integer, String, Boolean, Double, Float, Short> tuple = Tuple7.of(19L, 913, "XTHCY", TRUE, 41.1d, 11f, (short)21);
        int expectedHashCode = Objects.hash(tuple._1, tuple._2, tuple._3, tuple._4, tuple._5, tuple._6, tuple._7);

        assertEquals(expectedHashCode, tuple.hashCode());
    }


    @Test
    @DisplayName("toString: when is invoked then toString of internal elements is returned")
    public void toString_whenIsInvoked_thenToStringOfInternalElementsIsReturned() {
        Tuple7<Long, Integer, String, Boolean, Double, Float, Short> tuple = Tuple7.of(191L, 91, "XCY", TRUE, 61.2d, 19f, (short)33);
        String expectedToString = "(" + tuple._1 + ", " + tuple._2 + ", " + tuple._3 + ", " + tuple._4 + ", " + tuple._5 + ", " + tuple._6 + ", " + tuple._7 + ")";

        assertEquals(expectedToString, tuple.toString());
    }


    static Stream<Arguments> update1TestCases() {
        Tuple7<String, Integer, Long, Boolean, Double, Float, Short> tuple = Tuple7.of("A", 1, 33L, TRUE, 23.1d, 19f, (short)21);
        Tuple7<String, Integer, Long, Boolean, Double, Float, Short> updatedTuple = Tuple7.of("B", 1, 33L, TRUE, 23.1d, 19f, (short)21);
        return Stream.of(
                //@formatter:off
                //            tuple,    value,            expectedResult
                Arguments.of( tuple,   null,              Tuple7.of(null, tuple._2, tuple._3, tuple._4, tuple._5, tuple._6, tuple._7) ),
                Arguments.of( tuple,   updatedTuple._1,   updatedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("update1TestCases")
    @DisplayName("update1: test cases")
    public <T1, T2, T3, T4, T5, T6, T7> void update1_testCases(Tuple7<T1, T2, T3, T4, T5, T6, T7> tuple,
                                                               T1 value,
                                                               Tuple7<T1, T2, T3, T4, T5, T6, T7> expectedResult) {
        assertEquals(expectedResult, tuple.update1(value));
    }


    @Test
    @DisplayName("remove1: when is invoked then Tuple6 is returned")
    public void remove1_whenIsInvoked_thenTuple5IsReturned() {
        Tuple6<Integer, Long, Boolean, Double, Float, Short> result = Tuple7.of("A", 1, 3L, TRUE, 21.1d, 19f, (short)21).remove1();
        assertEquals(Tuple6.of(1, 3L, TRUE, 21.1d, 19f, (short)21), result);
    }


    static Stream<Arguments> update2TestCases() {
        Tuple7<String, Integer, Long, Boolean, Double, Float, Short> tuple = Tuple7.of("A", 1, 33L, TRUE, 23.1d, 19f, (short)21);
        Tuple7<String, Integer, Long, Boolean, Double, Float, Short> updatedTuple = Tuple7.of("A", 2, 33L, TRUE, 23.1d, 19f, (short)21);
        return Stream.of(
                //@formatter:off
                //            tuple,   value,             expectedResult
                Arguments.of( tuple,   null,              Tuple7.of(tuple._1, null, tuple._3, tuple._4, tuple._5, tuple._6, tuple._7) ),
                Arguments.of( tuple,   updatedTuple._2,   updatedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("update2TestCases")
    @DisplayName("update2: test cases")
    public <T1, T2, T3, T4, T5, T6, T7> void update2_testCases(Tuple7<T1, T2, T3, T4, T5, T6, T7> tuple,
                                                               T2 value,
                                                               Tuple7<T1, T2, T3, T4, T5, T6, T7> expectedResult) {
        assertEquals(expectedResult, tuple.update2(value));
    }


    @Test
    @DisplayName("remove2: when is invoked then Tuple6 is returned")
    public void remove2_whenIsInvoked_thenTuple5IsReturned() {
        Tuple6<String, Long, Boolean, Double, Float, Short> result = Tuple7.of("A", 1, 3L, TRUE, 21.1d, 19f, (short)21).remove2();
        assertEquals(Tuple6.of("A", 3L, TRUE, 21.1d, 19f, (short)21), result);
    }


    static Stream<Arguments> update3TestCases() {
        Tuple7<String, Integer, Long, Boolean, Double, Float, Short> tuple = Tuple7.of("A", 1, 33L, TRUE, 23.1d, 19f, (short)21);
        Tuple7<String, Integer, Long, Boolean, Double, Float, Short> updatedTuple = Tuple7.of("A", 1, 44L, TRUE, 23.1d, 19f, (short)21);
        return Stream.of(
                //@formatter:off
                //            tuple,   value,             expectedResult
                Arguments.of( tuple,   null,              Tuple7.of(tuple._1, tuple._2, null, tuple._4, tuple._5, tuple._6, tuple._7) ),
                Arguments.of( tuple,   updatedTuple._3,   updatedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("update3TestCases")
    @DisplayName("update3: test cases")
    public <T1, T2, T3, T4, T5, T6, T7> void update3_testCases(Tuple7<T1, T2, T3, T4, T5, T6, T7> tuple,
                                                               T3 value,
                                                               Tuple7<T1, T2, T3, T4, T5, T6, T7> expectedResult) {
        assertEquals(expectedResult, tuple.update3(value));
    }


    @Test
    @DisplayName("remove3: when is invoked then Tuple6 is returned")
    public void remove3_whenIsInvoked_thenTuple5IsReturned() {
        Tuple6<String, Integer, Boolean, Double, Float, Short> result = Tuple7.of("A", 1, 3L, TRUE, 21.1d, 19f, (short)21).remove3();
        assertEquals(Tuple6.of("A", 1, TRUE, 21.1d, 19f, (short)21), result);
    }


    static Stream<Arguments> update4TestCases() {
        Tuple7<String, Integer, Long, Boolean, Double, Float, Short> tuple = Tuple7.of("A", 1, 33L, TRUE, 23.1d, 19f, (short)21);
        Tuple7<String, Integer, Long, Boolean, Double, Float, Short> updatedTuple = Tuple7.of("A", 1, 33L, FALSE, 23.1d, 19f, (short)21);
        return Stream.of(
                //@formatter:off
                //            tuple,   value,             expectedResult
                Arguments.of( tuple,   null,              Tuple7.of(tuple._1, tuple._2, tuple._3, null, tuple._5, tuple._6, tuple._7) ),
                Arguments.of( tuple,   updatedTuple._4,   updatedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("update4TestCases")
    @DisplayName("update4: test cases")
    public <T1, T2, T3, T4, T5, T6, T7> void update4_testCases(Tuple7<T1, T2, T3, T4, T5, T6, T7> tuple,
                                                               T4 value,
                                                               Tuple7<T1, T2, T3, T4, T5, T6, T7> expectedResult) {
        assertEquals(expectedResult, tuple.update4(value));
    }


    @Test
    @DisplayName("remove4: when is invoked then Tuple6 is returned")
    public void remove4_whenIsInvoked_thenTuple5IsReturned() {
        Tuple6<String, Integer, Long, Double, Float, Short> result = Tuple7.of("A", 1, 3L, TRUE, 21.1d, 19f, (short)21).remove4();
        assertEquals(Tuple6.of("A", 1, 3L, 21.1d, 19f, (short)21), result);
    }


    static Stream<Arguments> update5TestCases() {
        Tuple7<String, Integer, Long, Boolean, Double, Float, Short> tuple = Tuple7.of("A", 1, 33L, TRUE, 23.1d, 19f, (short)21);
        Tuple7<String, Integer, Long, Boolean, Double, Float, Short> updatedTuple = Tuple7.of("A", 1, 33L, TRUE, 32.3d, 19f, (short)21);
        return Stream.of(
                //@formatter:off
                //            tuple,   value,             expectedResult
                Arguments.of( tuple,   null,              Tuple7.of(tuple._1, tuple._2, tuple._3, tuple._4, null, tuple._6, tuple._7) ),
                Arguments.of( tuple,   updatedTuple._5,   updatedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("update5TestCases")
    @DisplayName("update5: test cases")
    public <T1, T2, T3, T4, T5, T6, T7> void update5_testCases(Tuple7<T1, T2, T3, T4, T5, T6, T7> tuple,
                                                               T5 value,
                                                               Tuple7<T1, T2, T3, T4, T5, T6, T7> expectedResult) {
        assertEquals(expectedResult, tuple.update5(value));
    }


    @Test
    @DisplayName("remove5: when is invoked then Tuple6 is returned")
    public void remove5_whenIsInvoked_thenTuple5IsReturned() {
        Tuple6<String, Integer, Long, Boolean, Float, Short> result = Tuple7.of("A", 1, 3L, TRUE, 21.1d, 19f, (short)21).remove5();
        assertEquals(Tuple6.of("A", 1, 3L, TRUE, 19f, (short)21), result);
    }


    static Stream<Arguments> update6TestCases() {
        Tuple7<String, Integer, Long, Boolean, Double, Float, Short> tuple = Tuple7.of("A", 1, 33L, TRUE, 23.1d, 19f, (short)21);
        Tuple7<String, Integer, Long, Boolean, Double, Float, Short> updatedTuple = Tuple7.of("A", 1, 33L, TRUE, 23.1d, 11.1f, (short)21);
        return Stream.of(
                //@formatter:off
                //            tuple,   value,             expectedResult
                Arguments.of( tuple,   null,              Tuple7.of(tuple._1, tuple._2, tuple._3, tuple._4, tuple._5, null, tuple._7) ),
                Arguments.of( tuple,   updatedTuple._6,   updatedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("update6TestCases")
    @DisplayName("update6: test cases")
    public <T1, T2, T3, T4, T5, T6, T7> void update6_testCases(Tuple7<T1, T2, T3, T4, T5, T6, T7> tuple,
                                                               T6 value,
                                                               Tuple7<T1, T2, T3, T4, T5, T6, T7> expectedResult) {
        assertEquals(expectedResult, tuple.update6(value));
    }


    @Test
    @DisplayName("remove6: when is invoked then Tuple6 is returned")
    public void remove6_whenIsInvoked_thenTuple5IsReturned() {
        Tuple6<String, Integer, Long, Boolean, Double, Short> result = Tuple7.of("A", 1, 3L, TRUE, 21.1d, 19f, (short)21).remove6();
        assertEquals(Tuple6.of("A", 1, 3L, TRUE, 21.1d, (short)21), result);
    }


    static Stream<Arguments> update7TestCases() {
        Tuple7<String, Integer, Long, Boolean, Double, Float, Short> tuple = Tuple7.of("A", 1, 33L, TRUE, 23.1d, 19f, (short)21);
        Tuple7<String, Integer, Long, Boolean, Double, Float, Short> updatedTuple = Tuple7.of("A", 1, 33L, TRUE, 23.1d, 19f, (short)33);
        return Stream.of(
                //@formatter:off
                //            tuple,   value,             expectedResult
                Arguments.of( tuple,   null,              Tuple7.of(tuple._1, tuple._2, tuple._3, tuple._4, tuple._5, tuple._6, null) ),
                Arguments.of( tuple,   updatedTuple._7,   updatedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("update7TestCases")
    @DisplayName("update7: test cases")
    public <T1, T2, T3, T4, T5, T6, T7> void update7_testCases(Tuple7<T1, T2, T3, T4, T5, T6, T7> tuple,
                                                               T7 value,
                                                               Tuple7<T1, T2, T3, T4, T5, T6, T7> expectedResult) {
        assertEquals(expectedResult, tuple.update7(value));
    }


    @Test
    @DisplayName("remove7: when is invoked then Tuple6 is returned")
    public void remove7_whenIsInvoked_thenTuple5IsReturned() {
        Tuple6<String, Integer, Long, Boolean, Double, Float> result = Tuple7.of("A", 1, 3L, TRUE, 21.1d, 19f, (short)21).remove7();
        assertEquals(Tuple6.of("A", 1, 3L, TRUE, 21.1d, 19f), result);
    }


    static Stream<Arguments> mapHeptaFunctionTestCases() {
        Tuple7<String, Integer, Long, Boolean, Double, Float, Short> tuple = Tuple7.of("BC", 9, 12L, TRUE, 31.2d, 56.4f, (short)77);

        HeptaFunction<String, Integer, Long, Boolean, Double, Float, Short, Tuple7<String, Integer, Long, Boolean, Double, Float, Short>> identity = Tuple7::of;
        HeptaFunction<String, Integer, Long, Boolean, Double, Float, Short, Tuple7<Long, String, Boolean, Integer, String, Integer, String>> mappedFunction =
                (s, i, l, b, d, f, sh) -> Tuple7.of((long) s.length(), String.valueOf(l + 1), !b, i * 3, d.toString(), f.intValue(), sh.toString());

        Tuple7<Long, String, Boolean, Integer, String, Integer, String> mappedTuple = Tuple7.of(2L, "13", FALSE, 27, "31.2", 56, "77");
        return Stream.of(
                //@formatter:off
                //            tuple,   mapper,           expectedException,                expectedResult
                Arguments.of( tuple,   null,             IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   identity,         null,                             tuple ),
                Arguments.of( tuple,   mappedFunction,   null,                             mappedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapHeptaFunctionTestCases")
    @DisplayName("map: using HeptaFunction test cases")
    public <T1, T2, T3, T4, T5, T6, T7, U1, U2, U3, U4, U5, U6, U7> void mapHeptaFunction_testCases(Tuple7<T1, T2, T3, T4, T5, T6, T7> tuple,
                                                                                                    HeptaFunction<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, Tuple7<U1, U2, U3, U4, U5, U6, U7>> mapper,
                                                                                                    Class<? extends Exception> expectedException,
                                                                                                    Tuple7<U1, U2, U3, U4, U5, U6, U7> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> tuple.map(mapper));
        } else {
            assertEquals(expectedResult, tuple.map(mapper));
        }
    }


    static Stream<Arguments> mapFunctionTestCases() {
        Tuple7<String, Integer, Long, Boolean, Double, Float, Short> tuple = Tuple7.of("CFD", 92, 45L, TRUE, 43.4d, 78.9f, (short)35);
        Function<String, Long> fromStringToLong = s -> 3L + s.length();
        Function<Integer, String> fromIntegerToString = i -> String.valueOf(i - 2);
        Function<Long, String> fromLongToString = l -> String.valueOf(l + 10);
        Function<Boolean, String> fromBooleanToString = Object::toString;
        Function<Double, Integer> fromDoubleToInteger = Double::intValue;
        Function<Float, String> fromFloatToString = Object::toString;
        Function<Short, Integer> fromShortToInteger = Integer::valueOf;
        Tuple7<Long, String, String, String, Integer, String, Integer> mappedTuple = Tuple7.of(6L, "90", "55", "true", 43, "78.9", 35);
        return Stream.of(
                //@formatter:off
                //            tuple,   f1,                 f2,                    f3,                 f4,                    f5,                    f6,                  f7,                   expectedException,                expectedResult
                Arguments.of( tuple,   null,               null,                  null,               null,                  null,                  null,                null,                 IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   fromStringToLong,   null,                  null,               null,                  null,                  null,                null,                 IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   fromStringToLong,   fromIntegerToString,   null,               null,                  null,                  null,                null,                 IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   fromStringToLong,   fromIntegerToString,   fromLongToString,   null,                  null,                  null,                null,                 IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   fromStringToLong,   fromIntegerToString,   fromLongToString,   fromBooleanToString,   null,                  null,                null,                 IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   fromStringToLong,   fromIntegerToString,   fromLongToString,   fromBooleanToString,   fromDoubleToInteger,   null,                null,                 IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   fromStringToLong,   fromIntegerToString,   fromLongToString,   fromBooleanToString,   fromDoubleToInteger,   fromFloatToString,   null,                 IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   fromStringToLong,   fromIntegerToString,   fromLongToString,   fromBooleanToString,   fromDoubleToInteger,   fromFloatToString,   fromShortToInteger,   null,                             mappedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapFunctionTestCases")
    @DisplayName("map: using Function test cases")
    public <T1, T2, T3, T4, T5, T6, T7, U1, U2, U3, U4, U5, U6, U7> void mapFunction_testCases(Tuple7<T1, T2, T3, T4, T5, T6, T7> tuple,
                                                                                               Function<? super T1, ? extends U1> f1,
                                                                                               Function<? super T2, ? extends U2> f2,
                                                                                               Function<? super T3, ? extends U3> f3,
                                                                                               Function<? super T4, ? extends U4> f4,
                                                                                               Function<? super T5, ? extends U5> f5,
                                                                                               Function<? super T6, ? extends U6> f6,
                                                                                               Function<? super T7, ? extends U7> f7,
                                                                                               Class<? extends Exception> expectedException,
                                                                                               Tuple7<U1, U2, U3, U4, U5, U6, U7> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> tuple.map(f1, f2, f3, f4, f5, f6, f7));
        } else {
            assertEquals(expectedResult, tuple.map(f1, f2, f3, f4, f5, f6, f7));
        }
    }


    static Stream<Arguments> map1TestCases() {
        Tuple7<String, Integer, Long, Boolean, Double, Float, Short> tuple = Tuple7.of("ZW", 23, 76L, TRUE, 521.45d, 76.7f, (short)83);
        Function<String, Long> fromStringToLong = s -> 3L + s.length();
        Tuple7<Long, Integer, Long, Boolean, Double, Float, Short> mappedTuple = Tuple7.of(5L, 23, 76L, TRUE, 521.45d, 76.7f, (short)83);
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
    public <T1, T2, T3, T4, T5, T6, T7, U> void map1_testCases(Tuple7<T1, T2, T3, T4, T5, T6, T7> tuple,
                                                               Function<? super T1, ? extends U> mapper,
                                                               Class<? extends Exception> expectedException,
                                                               Tuple7<U, T2, T3, T4, T5, T6, T7> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> tuple.map1(mapper));
        } else {
            assertEquals(expectedResult, tuple.map1(mapper));
        }
    }


    static Stream<Arguments> map2TestCases() {
        Tuple7<Integer, Integer, String, Boolean, Double, Float, Short> tuple = Tuple7.of(7, 9, "ERT", FALSE, 32.19d, 87.1f, (short)12);
        Function<Integer, Long> fromIntegerToLong = i -> 2L * i;
        Tuple7<Integer, Long, String, Boolean, Double, Float, Short> mappedTuple = Tuple7.of(7, 18L, "ERT", FALSE, 32.19d, 87.1f, (short)12);
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
    public <T1, T2, T3, T4, T5, T6, T7, U> void map2_testCases(Tuple7<T1, T2, T3, T4, T5, T6, T7> tuple,
                                                               Function<? super T2, ? extends U> mapper,
                                                               Class<? extends Exception> expectedException,
                                                               Tuple7<T1, U, T3, T4, T5, T6, T7> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> tuple.map2(mapper));
        } else {
            assertEquals(expectedResult, tuple.map2(mapper));
        }
    }


    static Stream<Arguments> map3TestCases() {
        Tuple7<Long, Long, String, Boolean, Double, Float, Short> tuple = Tuple7.of(15L, 99L, "GH", TRUE, 9.3d, 1f, (short)13);
        Function<String, Long> fromStringToLong = s -> s.length() * 3L;
        Tuple7<Long, Long, Long, Boolean, Double, Float, Short> mappedTuple = Tuple7.of(15L, 99L, 6L, TRUE, 9.3d, 1f, (short)13);
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
    public <T1, T2, T3, T4, T5, T6, T7, U> void map3_testCases(Tuple7<T1, T2, T3, T4, T5, T6, T7> tuple,
                                                               Function<? super T3, ? extends U> mapper,
                                                               Class<? extends Exception> expectedException,
                                                               Tuple7<T1, T2, U, T4, T5, T6, T7> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> tuple.map3(mapper));
        } else {
            assertEquals(expectedResult, tuple.map3(mapper));
        }
    }


    static Stream<Arguments> map4TestCases() {
        Tuple7<Long, Long, String, Boolean, Double, Float, Short> tuple = Tuple7.of(15L, 99L, "GH", TRUE, 11d, 6.2f, (short)7);
        Function<Boolean, Boolean> fromBooleanToBoolean = b -> !b;
        Tuple7<Long, Long, String, Boolean, Double, Float, Short> mappedTuple = Tuple7.of(15L, 99L, "GH", FALSE, 11d, 6.2f, (short)7);
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
    public <T1, T2, T3, T4, T5, T6, T7, U> void map4_testCases(Tuple7<T1, T2, T3, T4, T5, T6, T7> tuple,
                                                               Function<? super T4, ? extends U> mapper,
                                                               Class<? extends Exception> expectedException,
                                                               Tuple7<T1, T2, T3, U, T5, T6, T7> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> tuple.map4(mapper));
        }
        else {
            assertEquals(expectedResult, tuple.map4(mapper));
        }
    }


    static Stream<Arguments> map5TestCases() {
        Tuple7<Long, Long, String, Boolean, Double, Float, Short> tuple = Tuple7.of(15L, 99L, "GH", TRUE, 12.132d, 24.9f, (short)66);
        Function<Double, Integer> fromDoubleToInteger = Double::intValue;
        Tuple7<Long, Long, String, Boolean, Integer, Float, Short> mappedTuple = Tuple7.of(15L, 99L, "GH", TRUE, 12, 24.9f, (short)66);
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
    public <T1, T2, T3, T4, T5, T6, T7, U> void map5_testCases(Tuple7<T1, T2, T3, T4, T5, T6, T7> tuple,
                                                               Function<? super T5, ? extends U> mapper,
                                                               Class<? extends Exception> expectedException,
                                                               Tuple7<T1, T2, T3, T4, U, T6, T7> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> tuple.map5(mapper));
        }
        else {
            assertEquals(expectedResult, tuple.map5(mapper));
        }
    }


    static Stream<Arguments> map6TestCases() {
        Tuple7<Long, Long, String, Boolean, Double, Float, Short> tuple = Tuple7.of(15L, 99L, "GH", TRUE, 12.132d, 24.9f, (short)87);
        Function<Float, Integer> fromFloatToInteger = Float::intValue;
        Tuple7<Long, Long, String, Boolean, Double, Integer, Short> mappedTuple = Tuple7.of(15L, 99L, "GH", TRUE, 12.132d, 24, (short)87);
        return Stream.of(
                //@formatter:off
                //            tuple,   mapper,                expectedException,                expectedResult
                Arguments.of( tuple,   null,                  IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   Function.identity(),   null,                             tuple ),
                Arguments.of( tuple,   fromFloatToInteger,    null,                             mappedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("map6TestCases")
    @DisplayName("map6: test cases")
    public <T1, T2, T3, T4, T5, T6, T7, U> void map6_testCases(Tuple7<T1, T2, T3, T4, T5, T6, T7> tuple,
                                                               Function<? super T6, ? extends U> mapper,
                                                               Class<? extends Exception> expectedException,
                                                               Tuple7<T1, T2, T3, T4, T5, U, T7> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> tuple.map6(mapper));
        }
        else {
            assertEquals(expectedResult, tuple.map6(mapper));
        }
    }


    static Stream<Arguments> map7TestCases() {
        Tuple7<Long, Long, String, Boolean, Double, Float, Short> tuple = Tuple7.of(15L, 99L, "GH", TRUE, 12.132d, 24.9f, (short)87);
        Function<Short, Long> fromShortToLong = Long::valueOf;
        Tuple7<Long, Long, String, Boolean, Double, Float, Long> mappedTuple = Tuple7.of(15L, 99L, "GH", TRUE, 12.132d, 24.9f, 87L);
        return Stream.of(
                //@formatter:off
                //            tuple,   mapper,                expectedException,                expectedResult
                Arguments.of( tuple,   null,                  IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   Function.identity(),   null,                             tuple ),
                Arguments.of( tuple,   fromShortToLong,       null,                             mappedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("map7TestCases")
    @DisplayName("map7: test cases")
    public <T1, T2, T3, T4, T5, T6, T7, U> void map7_testCases(Tuple7<T1, T2, T3, T4, T5, T6, T7> tuple,
                                                               Function<? super T7, ? extends U> mapper,
                                                               Class<? extends Exception> expectedException,
                                                               Tuple7<T1, T2, T3, T4, T5, T6, U> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> tuple.map7(mapper));
        }
        else {
            assertEquals(expectedResult, tuple.map7(mapper));
        }
    }


    static Stream<Arguments> applyTestCases() {
        Tuple7<Long, Integer, String, Integer, Double, Float, Short> tuple = Tuple7.of(12L, 93, "THC", 11, 99.8d, 11.19f, (short)55);
        HeptaFunction<Long, Integer, String, Integer, Double, Float, Short, Long> fromLongIntegerStringIntegerDoubleShortToLong =
                (l, i1, s, i2, d, f, sh) -> l + i1 - s.length() + i2 + d.longValue() + f.longValue() + sh.longValue();
        HeptaFunction<Long, Integer, String, Integer, Double, Float, Short, String> fromLongIntegerStringIntegerDoubleShortToString =
                (l, i1, s, i2, d, f, sh) -> i1 + l + s + i2 + d.toString() + f.toString() + sh.toString();

        Long appliedLong = 278L;
        String appliedString = "105THC1199.811.1955";
        return Stream.of(
                //@formatter:off
                //            tuple,   f,                                                 expectedException,                expectedResult
                Arguments.of( tuple,   null,                                              IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   fromLongIntegerStringIntegerDoubleShortToLong,     null,                             appliedLong ),
                Arguments.of( tuple,   fromLongIntegerStringIntegerDoubleShortToString,   null,                             appliedString )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("applyTestCases")
    @DisplayName("apply: test cases")
    public <T1, T2, T3, T4, T5, T6, T7, U> void apply_testCases(Tuple7<T1, T2, T3, T4, T5, T6, T7> tuple,
                                                                HeptaFunction<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? extends U> f,
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
