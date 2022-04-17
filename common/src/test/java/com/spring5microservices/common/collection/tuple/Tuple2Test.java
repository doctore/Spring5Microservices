package com.spring5microservices.common.collection.tuple;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Tuple2Test {

    static Stream<Arguments> ofTestCases() {
        String stringValue = "ABC";
        Integer integerValue = 25;
        return Stream.of(
                //@formatter:off
                //            t1,             t2,             expectedResult
                Arguments.of( null,           null,           Tuple2.of(null, null) ),
                Arguments.of( stringValue,    null,           Tuple2.of(stringValue, null) ),
                Arguments.of( null,           integerValue,   Tuple2.of(null, integerValue) ),
                Arguments.of( stringValue,    integerValue,   Tuple2.of(stringValue, integerValue) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("ofTestCases")
    @DisplayName("of: test cases")
    public <T1, T2> void of_testCases(T1 t1,
                                      T2 t2,
                                      Tuple2<T1, T2> expectedResult) {
        assertEquals(expectedResult, Tuple2.of(t1, t2));
    }


    static Stream<Arguments> comparatorTestCases() {
        Tuple2<String, Integer> stringInteger1Tuple = Tuple2.of("A", 1);
        Tuple2<String, Integer> stringInteger2Tuple = Tuple2.of("B", 2);
        Comparator<String> defaultStringComparator = Comparator.naturalOrder();
        Comparator<String> reverseStringComparator = Comparator.reverseOrder();
        Comparator<Integer> defaultIntegerComparator = Comparator.naturalOrder();
        Comparator<Integer> reverseIntegerComparator = Comparator.reverseOrder();
        return Stream.of(
                //@formatter:off
                //            t1,                    t2,                    comparatorT1,              comparatorT2,               expectedResult
                Arguments.of( stringInteger1Tuple,   stringInteger1Tuple,   defaultStringComparator,   defaultIntegerComparator,   0 ),
                Arguments.of( stringInteger1Tuple,   stringInteger1Tuple,   reverseStringComparator,   reverseIntegerComparator,   0 ),
                Arguments.of( stringInteger1Tuple,   stringInteger2Tuple,   defaultStringComparator,   defaultIntegerComparator,  -1 ),
                Arguments.of( stringInteger1Tuple,   stringInteger2Tuple,   reverseStringComparator,   reverseIntegerComparator,   1 ),
                Arguments.of( stringInteger2Tuple,   stringInteger1Tuple,   defaultStringComparator,   defaultIntegerComparator,   1 ),
                Arguments.of( stringInteger2Tuple,   stringInteger1Tuple,   reverseStringComparator,   reverseIntegerComparator,  -1 ),
                Arguments.of( stringInteger1Tuple,   stringInteger2Tuple,   defaultStringComparator,   reverseIntegerComparator,  -1 ),
                Arguments.of( stringInteger2Tuple,   stringInteger1Tuple,   defaultStringComparator,   reverseIntegerComparator,   1 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("comparatorTestCases")
    @DisplayName("comparator: test cases")
    public <T1, T2> void comparator_testCases(Tuple2<T1, T2> t1,
                                              Tuple2<T1, T2> t2,
                                              Comparator<T1> comparatorT1,
                                              Comparator<T2> comparatorT2,
                                              int expectedResult) {
        assertEquals(expectedResult, Tuple2.comparator(comparatorT1, comparatorT2).compare(t1, t2));
    }


    static Stream<Arguments> compareToTestCases() {
        Tuple2<String, Integer> stringInteger1Tuple = Tuple2.of("A", 1);
        Tuple2<String, Integer> stringInteger2Tuple = Tuple2.of("B", 2);
        return Stream.of(
                //@formatter:off
                //            t1,                    t2,                    expectedResult
                Arguments.of( stringInteger1Tuple,   stringInteger1Tuple,   0 ),
                Arguments.of( stringInteger1Tuple,   stringInteger2Tuple,  -1 ),
                Arguments.of( stringInteger2Tuple,   stringInteger1Tuple,   1 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("compareToTestCases")
    @DisplayName("compareTo: test cases")
    public <T1, T2> void compareTo_testCases(Tuple2<T1, T2> t1,
                                             Tuple2<T1, T2> t2,
                                             int expectedResult) {
        assertEquals(expectedResult, Tuple2.compareTo(t1, t2));
    }


    @Test
    @DisplayName("arity: when is invoked then 0 returned")
    public void arity_whenIsInvoked_then0IsReturned() {
        int result = Tuple2.of(1, "A").arity();
        assertEquals(2, result);
    }


    static Stream<Arguments> update1TestCases() {
        Tuple2<String, Integer> stringIntegerTuple = Tuple2.of("A", 1);
        Tuple2<String, Integer> updatedStringIntegerTuple = Tuple2.of("B", 1);
        return Stream.of(
                //@formatter:off
                //            tuple,                value,                          expectedResult
                Arguments.of( stringIntegerTuple,   null,                           Tuple2.of(null, stringIntegerTuple._2) ),
                Arguments.of( stringIntegerTuple,   updatedStringIntegerTuple._1,   updatedStringIntegerTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("update1TestCases")
    @DisplayName("update1: test cases")
    public <T1, T2> void update1_testCases(Tuple2<T1, T2> tuple,
                                           T1 value,
                                           Tuple2<T1, T2> expectedResult) {
        assertEquals(expectedResult, tuple.update1(value));
    }


    @Test
    @DisplayName("remove1: when is invoked then Tuple1 is returned")
    public void remove1_whenIsInvoked_thenTuple1IsReturned() {
        Tuple1<Integer> result = Tuple2.of("A", 1).remove1();
        assertEquals(Tuple.of(1), result);
    }


    static Stream<Arguments> update2TestCases() {
        Tuple2<String, Integer> stringIntegerTuple = Tuple2.of("A", 1);
        Tuple2<String, Integer> updatedStringIntegerTuple = Tuple2.of("A", 2);
        return Stream.of(
                //@formatter:off
                //            tuple,                value,                          expectedResult
                Arguments.of( stringIntegerTuple,   null,                           Tuple2.of(stringIntegerTuple._1, null) ),
                Arguments.of( stringIntegerTuple,   updatedStringIntegerTuple._2,   updatedStringIntegerTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("update2TestCases")
    @DisplayName("update2: test cases")
    public <T1, T2> void update2_testCases(Tuple2<T1, T2> tuple,
                                           T2 value,
                                           Tuple2<T1, T2> expectedResult) {
        assertEquals(expectedResult, tuple.update2(value));
    }


    @Test
    @DisplayName("remove2: when is invoked then Tuple1 is returned")
    public void remove2_whenIsInvoked_thenTuple1IsReturned() {
        Tuple1<String> result = Tuple2.of("A", 1).remove2();
        assertEquals(Tuple.of("A"), result);
    }


    static Stream<Arguments> swapTestCases() {
        Tuple2<String, Integer> stringIntegerTuple = Tuple2.of("A", 1);
        Tuple2<Integer, String> swappedStringIntegerTuple = Tuple2.of(1, "A");
        return Stream.of(
                //@formatter:off
                //            tuple,                   expectedResult
                Arguments.of( Tuple2.of(null, null),   Tuple2.of(null, null) ),
                Arguments.of( Tuple2.of(null, "C"),    Tuple2.of("C", null) ),
                Arguments.of( Tuple2.of(3, null),      Tuple2.of(null, 3) ),
                Arguments.of( stringIntegerTuple,      swappedStringIntegerTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("swapTestCases")
    @DisplayName("swap: test cases")
    public <T1, T2> void swap_testCases(Tuple2<T1, T2> tuple,
                                        Tuple2<T1, T2> expectedResult) {
        assertEquals(expectedResult, tuple.swap());
    }


    static Stream<Arguments> toEntryTestCases() {
        Map.Entry<String, String> nullKeyValueEntry = new AbstractMap.SimpleEntry<>(null, null);
        Map.Entry<Integer, String> onlyKeyEntry = new AbstractMap.SimpleEntry<>(1, null);
        Map.Entry<String, Integer> onlyValueEntry = new AbstractMap.SimpleEntry<>(null, 12);
        Map.Entry<String, String> keyAndValueEntry = new AbstractMap.SimpleEntry<>("A", "11");
        return Stream.of(
                //@formatter:off
                //            tuple,                                                               expectedResult
                Arguments.of( Tuple2.of(null, null),                                               nullKeyValueEntry ),
                Arguments.of( Tuple2.of(onlyKeyEntry.getKey(), null),                              onlyKeyEntry ),
                Arguments.of( Tuple2.of(null, onlyValueEntry.getValue()),                          onlyValueEntry ),
                Arguments.of( Tuple2.of(keyAndValueEntry.getKey(), keyAndValueEntry.getValue()),   keyAndValueEntry )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toEntryTestCases")
    @DisplayName("toEntry: test cases")
    public <T1, T2> void toEntry_testCases(Tuple2<T1, T2> tuple,
                                           Map.Entry<T1, T2> expectedResult) {
        assertEquals(expectedResult, tuple.toEntry());
    }


    static Stream<Arguments> mapBiFunctionTestCases() {
        Tuple2<String, Integer> stringIntegerTuple = Tuple2.of("A", 2);

        BiFunction<String, Integer, Tuple2<String, Integer>> identity = Tuple2::of;
        BiFunction<String, Integer, Tuple2<Integer, Long>> fromStringIntegerToTuple =
                (s, i) -> Tuple2.of(i * 2, (long) s.length());

        Tuple2<Integer, Long> mappedIntegerStringTuple = Tuple2.of(4, 1L);
        return Stream.of(
                //@formatter:off
                //            tuple,                mapper,                     expectedException,                expectedResult
                Arguments.of( stringIntegerTuple,   null,                       IllegalArgumentException.class,   null ),
                Arguments.of( stringIntegerTuple,   identity,                   null,                             stringIntegerTuple ),
                Arguments.of( stringIntegerTuple,   fromStringIntegerToTuple,   null,                             mappedIntegerStringTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapBiFunctionTestCases")
    @DisplayName("map: using BiFunction test cases")
    public <T1, T2, U1, U2> void mapBiFunction_testCases(Tuple2<T1, T2> tuple,
                                                         BiFunction<? super T1, ? super T2, Tuple2<U1, U2>> mapper,
                                                         Class<? extends Exception> expectedException,
                                                         Tuple2<U1, U2> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> tuple.map(mapper));
        }
        else {
            assertEquals(expectedResult, tuple.map(mapper));
        }
    }


    static Stream<Arguments> mapFunctionTestCases() {
        Tuple2<String, Integer> stringIntegerTuple = Tuple2.of("AB", 2);
        Function<String, Long> fromStringToLong = s -> (long) (3 + s.length());
        Function<Integer, String> fromIntegerToString = i -> String.valueOf(i - 2);
        Tuple2<Long, String> mappedStringIntegerTuple = Tuple2.of(5L, "0");
        return Stream.of(
                //@formatter:off
                //            t,                    f1,                 f2,                    expectedException,                expectedResult
                Arguments.of( stringIntegerTuple,   null,               null,                  IllegalArgumentException.class,   null ),
                Arguments.of( stringIntegerTuple,   fromStringToLong,   null,                  IllegalArgumentException.class,   null ),
                Arguments.of( stringIntegerTuple,   null,               fromIntegerToString,   IllegalArgumentException.class,   null ),
                Arguments.of( stringIntegerTuple,   fromStringToLong,   fromIntegerToString,   null,                             mappedStringIntegerTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapFunctionTestCases")
    @DisplayName("map: using Function test cases")
    public <T1, T2, U1, U2> void mapFunction_testCases(Tuple2<T1, T2> t,
                                                       Function<? super T1, ? extends U1> f1,
                                                       Function<? super T2, ? extends U2> f2,
                                                       Class<? extends Exception> expectedException,
                                                       Tuple2<U1, U2> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> t.map(f1, f2));
        }
        else {
            assertEquals(expectedResult, t.map(f1, f2));
        }
    }

}
