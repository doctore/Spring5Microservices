package com.spring5microservices.common.collection.tuple;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Tuple1Test {

    static Stream<Arguments> ofTestCases() {
        String stringValue = "ABC";
        Integer integerValue = 25;
        return Stream.of(
                //@formatter:off
                //            value,          expectedResult
                Arguments.of( null,           Tuple1.of(null) ),
                Arguments.of( stringValue,    Tuple1.of(stringValue) ),
                Arguments.of( integerValue,   Tuple1.of(integerValue) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("ofTestCases")
    @DisplayName("of: test cases")
    public <T1> void of_testCases(T1 value,
                                  Tuple1<T1> expectedResult) {
        assertEquals(expectedResult, Tuple1.of(value));
    }


    @Test
    @DisplayName("empty: when is invoked then a tuple with all values a null is returned")
    public void empty_whenIsInvoked_thenTupleWithAllValuesEqualsNullIsReturned() {
        Tuple1<?> result = Tuple1.empty();
        assertNotNull(result);
        assertNull(result._1);
    }


    static Stream<Arguments> comparatorTestCases() {
        Tuple1<String> string1Tuple = Tuple1.of("A");
        Tuple1<String> string2Tuple = Tuple1.of("B");
        Comparator<String> defaultComparator = Comparator.naturalOrder();
        Comparator<String> reverseComparator = Comparator.reverseOrder();
        return Stream.of(
                //@formatter:off
                //            t1,             t2,             comparator,          expectedResult
                Arguments.of( string1Tuple,   string1Tuple,   defaultComparator,   0 ),
                Arguments.of( string1Tuple,   string2Tuple,   defaultComparator,  -1 ),
                Arguments.of( string2Tuple,   string1Tuple,   defaultComparator,   1 ),
                Arguments.of( string1Tuple,   string2Tuple,   reverseComparator,   1 ),
                Arguments.of( string2Tuple,   string1Tuple,   reverseComparator,  -1 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("comparatorTestCases")
    @DisplayName("comparator: test cases")
    public <T1> void comparator_testCases(Tuple1<T1> t1,
                                          Tuple1<T1> t2,
                                          Comparator<T1> comparator,
                                          int expectedResult) {
        assertEquals(expectedResult, Tuple1.comparator(comparator).compare(t1, t2));
    }


    static Stream<Arguments> compareToTestCases() {
        Tuple1<String> string1Tuple = Tuple1.of("A");
        Tuple1<String> string2Tuple = Tuple1.of("B");
        return Stream.of(
                //@formatter:off
                //            t1,             t2,             expectedResult
                Arguments.of( string1Tuple,   string1Tuple,   0 ),
                Arguments.of( string1Tuple,   string2Tuple,  -1 ),
                Arguments.of( string2Tuple,   string1Tuple,   1 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("compareToTestCases")
    @DisplayName("compareTo: test cases")
    public <T1> void compareTo_testCases(Tuple1<T1> t1,
                                         Tuple1<T1> t2,
                                         int expectedResult) {
        assertEquals(expectedResult, Tuple1.compareTo(t1, t2));
    }


    @Test
    @DisplayName("arity: when is invoked then 0 returned")
    public void arity_whenIsInvoked_then0IsReturned() {
        int result = Tuple1.of(1).arity();
        assertEquals(1, result);
    }


    static Stream<Arguments> equalsTestCases() {
        Tuple1<String> stringTuple = Tuple1.of("TYHG");
        Tuple1<Long> longTuple = Tuple1.of(21L);
        return Stream.of(
                //@formatter:off
                //            tuple,         objectToCompare,   expectedResult
                Arguments.of( stringTuple,   "1",               false ),
                Arguments.of( longTuple,     longTuple._1,      false ),
                Arguments.of( stringTuple,   longTuple,         false ),
                Arguments.of( stringTuple,   stringTuple,       true ),
                Arguments.of( longTuple,     longTuple,         true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("equalsTestCases")
    @DisplayName("equals: test cases")
    public <T1> void equals_testCases(Tuple1<T1> tuple,
                                      Object objectToCompare,
                                      boolean expectedResult) {
        assertEquals(expectedResult, tuple.equals(objectToCompare));
    }


    @Test
    @DisplayName("hashCode: when is invoked then hashcode of internal elements is returned")
    public void hashCode_whenIsInvoked_thenHashCodeOfInternalElementsIsReturned() {
        Tuple1<String> tuple = Tuple1.of("123");
        int expectedHashCode = Objects.hashCode(tuple._1);

        assertEquals(expectedHashCode, tuple.hashCode());
    }


    @Test
    @DisplayName("toString: when is invoked then toString of internal elements is returned")
    public void toString_whenIsInvoked_thenToStringOfInternalElementsIsReturned() {
        Tuple1<Integer> tuple = Tuple1.of(778);
        String expectedToString = "(" + tuple._1.toString() + ")";

        assertEquals(expectedToString, tuple.toString());
    }


    static Stream<Arguments> update1TestCases() {
        Tuple1<String> stringTuple = Tuple1.of("A");
        Tuple1<Integer> integerTuple = Tuple1.of(11);
        Tuple1<String> updatedStringTuple = Tuple1.of("B");
        Tuple1<Integer> updatedIntegerTuple = Tuple1.of(21);
        return Stream.of(
                //@formatter:off
                //            tuple,          value,                   expectedResult
                Arguments.of( stringTuple,    null,                    Tuple1.of(null) ),
                Arguments.of( stringTuple,    updatedStringTuple._1,   updatedStringTuple ),
                Arguments.of( integerTuple,   updatedIntegerTuple._1,  updatedIntegerTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("update1TestCases")
    @DisplayName("update1: test cases")
    public <T1> void update1_testCases(Tuple1<T1> tuple,
                                       T1 value,
                                       Tuple1<T1> expectedResult) {
        assertEquals(expectedResult, tuple.update1(value));
    }


    @Test
    @DisplayName("remove1: when is invoked then Tuple0 is returned")
    public void remove1_whenIsInvoked_thenTuple0IsReturned() {
        Tuple0 result = Tuple1.of(1).remove1();
        assertNotNull(result);
    }


    static Stream<Arguments> mapTestCases() {
        Tuple1<String> stringTuple = Tuple1.of("A");
        Function<String, Integer> fromStringToInteger = s -> s.length() + s.length();
        Tuple1<Integer> mappedIntegerTuple = Tuple1.of(2);
        return Stream.of(
                //@formatter:off
                //            tuple,         mapper,                expectedException,                expectedResult
                Arguments.of( stringTuple,   null,                  IllegalArgumentException.class,   null ),
                Arguments.of( stringTuple,   Function.identity(),   null,                             stringTuple ),
                Arguments.of( stringTuple,   fromStringToInteger,   null,                             mappedIntegerTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapTestCases")
    @DisplayName("map: test cases")
    public <T1, U1> void map_testCases(Tuple1<T1> tuple,
                                       Function<? super T1, ? extends U1> mapper,
                                       Class<? extends Exception> expectedException,
                                       Tuple1<U1> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> tuple.map(mapper));
        }
        else {
            assertEquals(expectedResult, tuple.map(mapper));
        }
    }


    static Stream<Arguments> applyTestCases() {
        Tuple1<String> stringTuple = Tuple1.of("A");
        Function<String, Integer> fromStringToInteger = s -> s.length() + 10;
        Integer appliedInteger = 11;
        return Stream.of(
                //@formatter:off
                //            tuple,         f,                     expectedException,                expectedResult
                Arguments.of( stringTuple,   null,                  IllegalArgumentException.class,   null ),
                Arguments.of( stringTuple,   Function.identity(),   null,                             stringTuple._1 ),
                Arguments.of( stringTuple,   fromStringToInteger,   null,                             appliedInteger )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("applyTestCases")
    @DisplayName("apply: test cases")
    public <T1, U> void apply_testCases(Tuple1<T1> tuple,
                                        Function<? super T1, ? extends U> f,
                                        Class<? extends Exception> expectedException,
                                        U expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> tuple.apply(f));
        }
        else {
            assertEquals(expectedResult, tuple.apply(f));
        }
    }


    static Stream<Arguments> prependTestCases() {
        Tuple1<String> stringTuple = Tuple1.of("TYHG");
        String stringValue = "TFG";
        Integer integerValue = 43;
        return Stream.of(
                //@formatter:off
                //            tuple,         value,          expectedResult
                Arguments.of( stringTuple,   null,           Tuple2.of(null, stringTuple._1) ),
                Arguments.of( stringTuple,   stringValue,    Tuple2.of(stringValue, stringTuple._1) ),
                Arguments.of( stringTuple,   integerValue,   Tuple2.of(integerValue, stringTuple._1) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("prependTestCases")
    @DisplayName("prepend: test cases")
    public <T, T1> void prepend_testCases(Tuple1<T1> tuple,
                                          T value,
                                          Tuple2<T, T1> expectedResult) {
        assertEquals(expectedResult, tuple.prepend(value));
    }


    static Stream<Arguments> appendTestCases() {
        Tuple1<String> stringTuple = Tuple1.of("TIO");
        String stringValue = "YYY";
        Integer integerValue = 99;
        return Stream.of(
                //@formatter:off
                //            tuple,         value,          expectedResult
                Arguments.of( stringTuple,   null,           Tuple2.of(stringTuple._1, null) ),
                Arguments.of( stringTuple,   stringValue,    Tuple2.of(stringTuple._1, stringValue) ),
                Arguments.of( stringTuple,   integerValue,   Tuple2.of(stringTuple._1, integerValue) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("appendTestCases")
    @DisplayName("append: test cases")
    public <T, T1> void append_testCases(Tuple1<T1> tuple,
                                         T value,
                                         Tuple2<T1, T> expectedResult) {
        assertEquals(expectedResult, tuple.append(value));
    }


    static Stream<Arguments> concatTuple1TestCases() {
        Tuple1<String> stringTuple = Tuple1.of("TYHG");
        Tuple1<Long> longTuple = Tuple1.of(21L);
        Tuple1<Integer> nullValueTuple = Tuple1.of(null);
        return Stream.of(
                //@formatter:off
                //            tuple,            tupleToConcat,    expectedResult
                Arguments.of( stringTuple,      null,             Tuple2.of(stringTuple._1, null) ),
                Arguments.of( stringTuple,      nullValueTuple,   Tuple2.of(stringTuple._1, null) ),
                Arguments.of( nullValueTuple,   longTuple,        Tuple2.of(null, longTuple._1) ),
                Arguments.of( stringTuple,      longTuple,        Tuple2.of(stringTuple._1, longTuple._1) ),
                Arguments.of( longTuple,        stringTuple,      Tuple2.of(longTuple._1, stringTuple._1) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("concatTuple1TestCases")
    @DisplayName("concat: using Tuple1 test cases")
    public <T1, T2> void concatTuple1_testCases(Tuple1<T1> tuple,
                                                Tuple1<T2> tupleToConcat,
                                                Tuple2<T1, T2> expectedResult) {
        assertEquals(expectedResult, tuple.concat(tupleToConcat));
    }


    static Stream<Arguments> concatTuple2TestCases() {
        Tuple1<String> stringTuple = Tuple1.of("TYHG");
        Tuple2<Long, Integer> longIntegerTuple = Tuple2.of(17L, 87);
        Tuple2<Integer, Integer> nullValueTuple = Tuple2.of(null, null);
        return Stream.of(
                //@formatter:off
                //            tuple,         tupleToConcat,      expectedResult
                Arguments.of( stringTuple,   null,               Tuple3.of(stringTuple._1, null, null) ),
                Arguments.of( stringTuple,   nullValueTuple,     Tuple3.of(stringTuple._1, null, null) ),
                Arguments.of( stringTuple,   longIntegerTuple,   Tuple3.of(stringTuple._1, longIntegerTuple._1, longIntegerTuple._2) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("concatTuple2TestCases")
    @DisplayName("concat: using Tuple2 test cases")
    public <T1, T2, T3> void concatTuple2_testCases(Tuple1<T1> tuple,
                                                    Tuple2<T2, T3> tupleToConcat,
                                                    Tuple3<T1, T2, T3> expectedResult) {
        assertEquals(expectedResult, tuple.concat(tupleToConcat));
    }


    static Stream<Arguments> concatTuple3TestCases() {
        Tuple1<String> stringTuple = Tuple1.of("TYHG");
        Tuple3<Long, Integer, Boolean> longIntegerBooleanTuple = Tuple3.of(17L, 87, TRUE);
        Tuple3<Integer, Integer, Long> nullValueTuple = Tuple3.of(null, null, null);
        return Stream.of(
                //@formatter:off
                //            tuple,         tupleToConcat,             expectedResult
                Arguments.of( stringTuple,   null,                      Tuple4.of(stringTuple._1, null, null, null) ),
                Arguments.of( stringTuple,   nullValueTuple,            Tuple4.of(stringTuple._1, null, null, null) ),
                Arguments.of( stringTuple,   longIntegerBooleanTuple,   Tuple4.of(stringTuple._1, longIntegerBooleanTuple._1, longIntegerBooleanTuple._2, longIntegerBooleanTuple._3) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("concatTuple3TestCases")
    @DisplayName("concat: using Tuple3 test cases")
    public <T1, T2, T3, T4> void concatTuple3_testCases(Tuple1<T1> tuple,
                                                        Tuple3<T2, T3, T4> tupleToConcat,
                                                        Tuple4<T1, T2, T3, T4> expectedResult) {
        assertEquals(expectedResult, tuple.concat(tupleToConcat));
    }

}
