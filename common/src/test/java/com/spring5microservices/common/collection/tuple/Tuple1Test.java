package com.spring5microservices.common.collection.tuple;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        Tuple1<Integer> integerFromStringTuple = Tuple1.of(2);
        return Stream.of(
                //@formatter:off
                //            tuple,         mapper,                expectedException,                expectedResult
                Arguments.of( stringTuple,   null,                  IllegalArgumentException.class,   null ),
                Arguments.of( stringTuple,   Function.identity(),   null,                             stringTuple ),
                Arguments.of( stringTuple,   fromStringToInteger,   null,                             integerFromStringTuple )
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

}
