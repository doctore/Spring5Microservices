package com.spring5microservices.common.collection.tuple;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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


    @Test
    @DisplayName("empty: when is invoked then a tuple with all values a null is returned")
    public void empty_whenIsInvoked_thenTupleWithAllValuesEqualsNullIsReturned() {
        Tuple2<?, ?> result = Tuple2.empty();
        assertNotNull(result);
        assertNull(result._1);
        assertNull(result._2);
    }


    static Stream<Arguments> comparatorTestCases() {
        Tuple2<String, Integer> t1 = Tuple2.of("A", 1);
        Tuple2<String, Integer> t2 = Tuple2.of("B", 2);
        Comparator<String> defaultStringComparator = Comparator.naturalOrder();
        Comparator<String> reverseStringComparator = Comparator.reverseOrder();
        Comparator<Integer> defaultIntegerComparator = Comparator.naturalOrder();
        Comparator<Integer> reverseIntegerComparator = Comparator.reverseOrder();
        return Stream.of(
                //@formatter:off
                //            t1,   t2,   comparatorT1,              comparatorT2,               expectedResult
                Arguments.of( t1,   t1,   defaultStringComparator,   defaultIntegerComparator,   0 ),
                Arguments.of( t1,   t1,   reverseStringComparator,   reverseIntegerComparator,   0 ),
                Arguments.of( t1,   t2,   defaultStringComparator,   defaultIntegerComparator,  -1 ),
                Arguments.of( t1,   t2,   reverseStringComparator,   reverseIntegerComparator,   1 ),
                Arguments.of( t2,   t1,   defaultStringComparator,   defaultIntegerComparator,   1 ),
                Arguments.of( t2,   t1,   reverseStringComparator,   reverseIntegerComparator,  -1 ),
                Arguments.of( t1,   t2,   defaultStringComparator,   reverseIntegerComparator,  -1 ),
                Arguments.of( t2,   t1,   defaultStringComparator,   reverseIntegerComparator,   1 )
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
        Tuple2<String, Integer> t1 = Tuple2.of("A", 1);
        Tuple2<String, Integer> t2 = Tuple2.of("B", 2);
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


    static Stream<Arguments> equalsTestCases() {
        Tuple2<String, Long> t1 = Tuple2.of("TYHG", 21L);
        Tuple2<Long, String> t2 = Tuple2.of(21L, "TYHG");
        Tuple2<String, Long> t3 = Tuple2.of("TYHG", 21L);
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
    public <T1, T2> void equals_testCases(Tuple2<T1, T2> tuple,
                                          Object objectToCompare,
                                          boolean expectedResult) {
        assertEquals(expectedResult, tuple.equals(objectToCompare));
    }


    @Test
    @DisplayName("hashCode: when is invoked then hash of internal elements is returned")
    public void hashCode_whenIsInvoked_thenHashCodeOfInternalElementsIsReturned() {
        Tuple2<String, Integer> tuple = Tuple2.of("123", 98);
        int expectedHashCode = Objects.hash(tuple._1, tuple._2);

        assertEquals(expectedHashCode, tuple.hashCode());
    }


    @Test
    @DisplayName("toString: when is invoked then toString of internal elements is returned")
    public void toString_whenIsInvoked_thenToStringOfInternalElementsIsReturned() {
        Tuple2<Integer, Long> tuple = Tuple2.of(778, 43L);
        String expectedToString = "(" + tuple._1 + ", " + tuple._2 + ")";

        assertEquals(expectedToString, tuple.toString());
    }


    static Stream<Arguments> update1TestCases() {
        Tuple2<String, Integer> tuple = Tuple2.of("A", 1);
        Tuple2<String, Integer> updatedTuple = Tuple2.of("B", 1);
        return Stream.of(
                //@formatter:off
                //            tuple,   value,             expectedResult
                Arguments.of( tuple,   null,              Tuple2.of(null, tuple._2) ),
                Arguments.of( tuple,   updatedTuple._1,   updatedTuple )
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
        Tuple2<String, Integer> tuple = Tuple2.of("A", 1);
        Tuple2<String, Integer> updatedTuple = Tuple2.of("A", 2);
        return Stream.of(
                //@formatter:off
                //            tuple,   value,             expectedResult
                Arguments.of( tuple,   null,              Tuple2.of(tuple._1, null) ),
                Arguments.of( tuple,   updatedTuple._2,   updatedTuple )
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
        Tuple2<Integer, String> swappedIntegerStringTuple = Tuple2.of(1, "A");
        return Stream.of(
                //@formatter:off
                //            tuple,                   expectedResult
                Arguments.of( Tuple2.of(null, null),   Tuple2.of(null, null) ),
                Arguments.of( Tuple2.of(null, "C"),    Tuple2.of("C", null) ),
                Arguments.of( Tuple2.of(3, null),      Tuple2.of(null, 3) ),
                Arguments.of( stringIntegerTuple,      swappedIntegerStringTuple )
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
        Tuple2<String, Integer> tuple = Tuple2.of("A", 2);

        BiFunction<String, Integer, Tuple2<String, Integer>> identity = Tuple2::of;
        BiFunction<String, Integer, Tuple2<Integer, Long>> fromStringIntegerToTuple =
                (s, i) -> Tuple2.of(i * 2, (long) s.length());

        Tuple2<Integer, Long> mappedTuple = Tuple2.of(4, 1L);
        return Stream.of(
                //@formatter:off
                //            tuple,   mapper,                     expectedException,                expectedResult
                Arguments.of( tuple,   null,                       IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   identity,                   null,                             tuple ),
                Arguments.of( tuple,   fromStringIntegerToTuple,   null,                             mappedTuple )
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
        } else {
            assertEquals(expectedResult, tuple.map(mapper));
        }
    }


    static Stream<Arguments> mapFunctionTestCases() {
        Tuple2<String, Integer> tuple = Tuple2.of("AB", 2);
        Function<String, Long> fromStringToLong = s -> 3L + s.length();
        Function<Integer, String> fromIntegerToString = i -> String.valueOf(i - 2);
        Tuple2<Long, String> mappedTuple = Tuple2.of(5L, "0");
        return Stream.of(
                //@formatter:off
                //            tuple,   f1,                 f2,                    expectedException,                expectedResult
                Arguments.of( tuple,   null,               null,                  IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   fromStringToLong,   null,                  IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   null,               fromIntegerToString,   IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   fromStringToLong,   fromIntegerToString,   null,                             mappedTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapFunctionTestCases")
    @DisplayName("map: using Function test cases")
    public <T1, T2, U1, U2> void mapFunction_testCases(Tuple2<T1, T2> tuple,
                                                       Function<? super T1, ? extends U1> f1,
                                                       Function<? super T2, ? extends U2> f2,
                                                       Class<? extends Exception> expectedException,
                                                       Tuple2<U1, U2> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> tuple.map(f1, f2));
        } else {
            assertEquals(expectedResult, tuple.map(f1, f2));
        }
    }


    static Stream<Arguments> map1TestCases() {
        Tuple2<String, Integer> tuple = Tuple2.of("AB", 2);
        Function<String, Long> fromStringToLong = s -> 3L + s.length();
        Tuple2<Long, Integer> mappedLongIntegerTuple = Tuple2.of(5L, 2);
        return Stream.of(
                //@formatter:off
                //            tuple,   mapper,                expectedException,                expectedResult
                Arguments.of( tuple,   null,                  IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   Function.identity(),   null,                             tuple ),
                Arguments.of( tuple,   fromStringToLong,      null,                             mappedLongIntegerTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("map1TestCases")
    @DisplayName("map1: test cases")
    public <T1, T2, U> void map1_testCases(Tuple2<T1, T2> tuple,
                                           Function<? super T1, ? extends U> mapper,
                                           Class<? extends Exception> expectedException,
                                           Tuple2<U, T2> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> tuple.map1(mapper));
        } else {
            assertEquals(expectedResult, tuple.map1(mapper));
        }
    }


    static Stream<Arguments> map2TestCases() {
        Tuple2<Integer, String> tuple = Tuple2.of(4, "CFD");
        Function<String, Long> fromStringToLong = s -> 1L + s.length();
        Tuple2<Integer, Long> mappedIntegerLongTuple = Tuple2.of(4, 4L);
        return Stream.of(
                //@formatter:off
                //            tuple,   mapper,                expectedException,                expectedResult
                Arguments.of( tuple,   null,                  IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   Function.identity(),   null,                             tuple ),
                Arguments.of( tuple,   fromStringToLong,      null,                             mappedIntegerLongTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("map2TestCases")
    @DisplayName("map2: test cases")
    public <T1, T2, U> void map2_testCases(Tuple2<T1, T2> tuple,
                                           Function<? super T2, ? extends U> mapper,
                                           Class<? extends Exception> expectedException,
                                           Tuple2<T1, U> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> tuple.map2(mapper));
        } else {
            assertEquals(expectedResult, tuple.map2(mapper));
        }
    }


    static Stream<Arguments> applyTestCases() {
        Tuple2<Integer, Long> tuple = Tuple2.of(41, 22L);
        BiFunction<Integer, Long, Long> fromIntegerLongToLong = Long::sum;
        BiFunction<Integer, Long, String> fromIntegerLongToString = (i, l) -> String.valueOf(i - l);
        Long appliedLong = 63L;
        String appliedString = "19";
        return Stream.of(
                //@formatter:off
                //            tuple,   f,                         expectedException,                expectedResult
                Arguments.of( tuple,   null,                      IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   fromIntegerLongToLong,     null,                             appliedLong ),
                Arguments.of( tuple,   fromIntegerLongToString,   null,                             appliedString )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("applyTestCases")
    @DisplayName("apply: test cases")
    public <T1, T2, U> void apply_testCases(Tuple2<T1, T2> tuple,
                                            BiFunction<? super T1, ? super T2, ? extends U> f,
                                            Class<? extends Exception> expectedException,
                                            U expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> tuple.apply(f));
        } else {
            assertEquals(expectedResult, tuple.apply(f));
        }
    }


    static Stream<Arguments> prependTestCases() {
        Tuple2<String, Integer> tuple = Tuple2.of("ZZ", 77);
        Long longValue = 34L;
        Integer integerValue = 55;
        return Stream.of(
                //@formatter:off
                //            tuple,   value,          expectedResult
                Arguments.of( tuple,   null,           Tuple3.of(null, tuple._1, tuple._2) ),
                Arguments.of( tuple,   longValue,      Tuple3.of(longValue, tuple._1, tuple._2) ),
                Arguments.of( tuple,   integerValue,   Tuple3.of(integerValue, tuple._1, tuple._2) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("prependTestCases")
    @DisplayName("prepend: test cases")
    public <T, T1, T2> void prepend_testCases(Tuple2<T1, T2> tuple,
                                              T value,
                                              Tuple3<T, T1, T2> expectedResult) {
        assertEquals(expectedResult, tuple.prepend(value));
    }


    static Stream<Arguments> appendTestCases() {
        Tuple2<String, Integer> tuple = Tuple2.of("ABC", 41);
        Long longValue = 11L;
        Integer integerValue = 66;
        return Stream.of(
                //@formatter:off
                //            tuple,   value,          expectedResult
                Arguments.of( tuple,   null,           Tuple3.of(tuple._1, tuple._2, null) ),
                Arguments.of( tuple,   longValue,      Tuple3.of(tuple._1, tuple._2, longValue) ),
                Arguments.of( tuple,   integerValue,   Tuple3.of(tuple._1, tuple._2, integerValue) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("appendTestCases")
    @DisplayName("append: test cases")
    public <T, T1, T2> void append_testCases(Tuple2<T1, T2> tuple,
                                             T value,
                                             Tuple3<T1, T2, T> expectedResult) {
        assertEquals(expectedResult, tuple.append(value));
    }


    static Stream<Arguments> concatTuple1TestCases() {
        Tuple2<String, Integer> t1 = Tuple2.of("YHG", 33);
        Tuple1<Long> t2 = Tuple1.of(21L);
        Tuple1<Integer> nullValueTuple = Tuple1.of(null);
        return Stream.of(
                //@formatter:off
                //            tuple,   tupleToConcat,    expectedResult
                Arguments.of( t1,      null,             Tuple3.of(t1._1, t1._2, null) ),
                Arguments.of( t1,      nullValueTuple,   Tuple3.of(t1._1, t1._2, null) ),
                Arguments.of( t1,      t2,               Tuple3.of(t1._1, t1._2, t2._1) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("concatTuple1TestCases")
    @DisplayName("concat: using Tuple1 test cases")
    public <T1, T2, T3> void concatTuple1_testCases(Tuple2<T1, T2> tuple,
                                                    Tuple1<T3> tupleToConcat,
                                                    Tuple3<T1, T2, T3> expectedResult) {
        assertEquals(expectedResult, tuple.concat(tupleToConcat));
    }


    static Stream<Arguments> concatTuple2TestCases() {
        Tuple2<String, Integer> t1 = Tuple2.of("YHG", 33);
        Tuple2<Long, Integer> t2 = Tuple2.of(21L, 55);
        Tuple2<Integer, Integer> nullValueTuple = Tuple2.of(null, null);
        return Stream.of(
                //@formatter:off
                //            tuple,   tupleToConcat,    expectedResult
                Arguments.of( t1,      null,             Tuple4.of(t1._1, t1._2, null, null) ),
                Arguments.of( t1,      nullValueTuple,   Tuple4.of(t1._1, t1._2, null, null) ),
                Arguments.of( t1,      t2,               Tuple4.of(t1._1, t1._2, t2._1, t2._2) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("concatTuple2TestCases")
    @DisplayName("concat: using Tuple2 test cases")
    public <T1, T2, T3, T4> void concatTuple2_testCases(Tuple2<T1, T2> tuple,
                                                        Tuple2<T3, T4> tupleToConcat,
                                                        Tuple4<T1, T2, T3, T4> expectedResult) {
        assertEquals(expectedResult, tuple.concat(tupleToConcat));
    }


    static Stream<Arguments> concatTuple3TestCases() {
        Tuple2<String, Integer> t1 = Tuple2.of("YHG", 33);
        Tuple3<Long, Integer, String> t2 = Tuple3.of(21L, 55, "DFs");
        Tuple3<Integer, Integer, Integer> nullValueTuple = Tuple3.of(null, null, null);
        return Stream.of(
                //@formatter:off
                //            tuple,   tupleToConcat,    expectedResult
                Arguments.of( t1,      null,             Tuple5.of(t1._1, t1._2, null, null, null) ),
                Arguments.of( t1,      nullValueTuple,   Tuple5.of(t1._1, t1._2, null, null, null) ),
                Arguments.of( t1,      t2,               Tuple5.of(t1._1, t1._2, t2._1, t2._2, t2._3) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("concatTuple3TestCases")
    @DisplayName("concat: using Tuple3 test cases")
    public <T1, T2, T3, T4, T5> void concatTuple3_testCases(Tuple2<T1, T2> tuple,
                                                            Tuple3<T3, T4, T5> tupleToConcat,
                                                            Tuple5<T1, T2, T3, T4, T5> expectedResult) {
        assertEquals(expectedResult, tuple.concat(tupleToConcat));
    }

}
