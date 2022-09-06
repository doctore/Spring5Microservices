package com.spring5microservices.common.collection.tuple;

import com.spring5microservices.common.interfaces.functional.TriFunction;
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


    @Test
    @DisplayName("empty: when is invoked then a tuple with all values a null is returned")
    public void empty_whenIsInvoked_thenTupleWithAllValuesEqualsNullIsReturned() {
        Tuple3<?, ?, ?> result = Tuple3.empty();
        assertNotNull(result);
        assertNull(result._1);
        assertNull(result._2);
        assertNull(result._3);
    }


    static Stream<Arguments> comparatorTestCases() {
        Tuple3<String, Integer, Long> t1 = Tuple3.of("A", 1, 3L);
        Tuple3<String, Integer, Long> t2 = Tuple3.of("B", 2, 2L);
        Comparator<String> defaultStringComparator = Comparator.naturalOrder();
        Comparator<String> reverseStringComparator = Comparator.reverseOrder();
        Comparator<Integer> defaultIntegerComparator = Comparator.naturalOrder();
        Comparator<Integer> reverseIntegerComparator = Comparator.reverseOrder();
        Comparator<Long> defaultLongComparator = Comparator.naturalOrder();
        Comparator<Long> reverseLongComparator = Comparator.reverseOrder();
        return Stream.of(
                //@formatter:off
                //            t1,   t2,   comparatorT1,              comparatorT2,               comparatorT3,            expectedResult
                Arguments.of( t1,   t1,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   0 ),
                Arguments.of( t1,   t1,   reverseStringComparator,   reverseIntegerComparator,   reverseLongComparator,   0 ),
                Arguments.of( t1,   t2,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,  -1 ),
                Arguments.of( t1,   t2,   reverseStringComparator,   reverseIntegerComparator,   reverseLongComparator,   1 ),
                Arguments.of( t2,   t1,   defaultStringComparator,   defaultIntegerComparator,   defaultLongComparator,   1 ),
                Arguments.of( t2,   t1,   reverseStringComparator,   reverseIntegerComparator,   reverseLongComparator,  -1 ),
                Arguments.of( t1,   t2,   defaultStringComparator,   reverseIntegerComparator,   reverseLongComparator,  -1 ),
                Arguments.of( t2,   t1,   defaultStringComparator,   reverseIntegerComparator,   reverseLongComparator,   1 ),
                Arguments.of( t1,   t2,   defaultStringComparator,   defaultIntegerComparator,   reverseLongComparator,  -1 ),
                Arguments.of( t2,   t1,   defaultStringComparator,   defaultIntegerComparator,   reverseLongComparator,   1 )
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
        Tuple3<String, Integer, Long> t1 = Tuple3.of("A", 1, 3L);
        Tuple3<String, Integer, Long> t2 = Tuple3.of("B", 2, 2L);
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


    static Stream<Arguments> equalsTestCases() {
        Tuple3<String, Long, Integer> t1 = Tuple3.of("TYHG", 21L, 16);
        Tuple3<Long, String, Integer> t2 = Tuple3.of(21L, "TYHG", 16);
        Tuple3<String, Long, Integer> t3 = Tuple3.of("TYHG", 21L, 16);
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
    public <T1, T2, T3> void equals_testCases(Tuple3<T1, T2, T3> tuple,
                                              Object objectToCompare,
                                              boolean expectedResult) {
        assertEquals(expectedResult, tuple.equals(objectToCompare));
    }


    @Test
    @DisplayName("hashCode: when is invoked then hash of internal elements is returned")
    public void hashCode_whenIsInvoked_thenHashCodeOfInternalElementsIsReturned() {
        Tuple3<Long, Integer, String> tuple = Tuple3.of(19L, 913, "XTHCY");
        int expectedHashCode = Objects.hash(tuple._1, tuple._2, tuple._3);

        assertEquals(expectedHashCode, tuple.hashCode());
    }


    @Test
    @DisplayName("toString: when is invoked then toString of internal elements is returned")
    public void toString_whenIsInvoked_thenToStringOfInternalElementsIsReturned() {
        Tuple3<Long, Integer, String> tuple = Tuple3.of(191L, 91, "XCY");
        String expectedToString = "(" + tuple._1 + ", " + tuple._2 + ", " + tuple._3 + ")";

        assertEquals(expectedToString, tuple.toString());
    }


    static Stream<Arguments> update1TestCases() {
        Tuple3<String, Integer, Long> tuple = Tuple3.of("A", 1, 33L);
        Tuple3<String, Integer, Long> updatedTuple = Tuple3.of("B", 1, 33L);
        return Stream.of(
                //@formatter:off
                //            tuple,   value,             expectedResult
                Arguments.of( tuple,   null,              Tuple3.of(null, tuple._2, tuple._3) ),
                Arguments.of( tuple,   updatedTuple._1,   updatedTuple )
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
        Tuple3<String, Integer, Long> tuple = Tuple3.of("A", 1, 33L);
        Tuple3<String, Integer, Long> updatedTuple = Tuple3.of("A", 3, 33L);
        return Stream.of(
                //@formatter:off
                //            tuple,   value,             expectedResult
                Arguments.of( tuple,   null,              Tuple3.of(tuple._1, null, tuple._3) ),
                Arguments.of( tuple,   updatedTuple._2,   updatedTuple )
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
        Tuple3<String, Integer, Long> tuple = Tuple3.of("A", 1, 33L);
        Tuple3<String, Integer, Long> updatedTuple = Tuple3.of("A", 1, 20L);
        return Stream.of(
                //@formatter:off
                //            tuple,   value,             expectedResult
                Arguments.of( tuple,   null,              Tuple3.of(tuple._1, tuple._2, null) ),
                Arguments.of( tuple,   updatedTuple._3,   updatedTuple )
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
        Tuple3<String, Integer, Long> tuple = Tuple3.of("BC", 9, 12L);

        TriFunction<String, Integer, Long, Tuple3<String, Integer, Long>> identity = Tuple3::of;
        TriFunction<String, Integer, Long, Tuple3<Long, String, Integer>> fromStringIntegerLongToTuple =
                (s, i, l) -> Tuple3.of((long) s.length(), String.valueOf(l + 1), i * 3);

        Tuple3<Long, String, Integer> mappedTuple = Tuple3.of(2L, "13", 27);
        return Stream.of(
                //@formatter:off
                //            tuple,   mapper,                         expectedException,                expectedResult
                Arguments.of( tuple,   null,                           IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   identity,                       null,                             tuple ),
                Arguments.of( tuple,   fromStringIntegerLongToTuple,   null,                             mappedTuple )
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
        } else {
            assertEquals(expectedResult, tuple.map(mapper));
        }
    }


    static Stream<Arguments> mapFunctionTestCases() {
        Tuple3<String, Integer, Long> tuple = Tuple3.of("CFD", 92, 45L);
        Function<String, Long> fromStringToLong = s -> 3L + s.length();
        Function<Integer, String> fromIntegerToString = i -> String.valueOf(i - 2);
        Function<Long, String> fromLongToString = l -> String.valueOf(l + 10);
        Tuple3<Long, String, String> mappedTuple = Tuple3.of(6L, "90", "55");
        return Stream.of(
                //@formatter:off
                //            tuple,   f1,                 f2,                    f3,                 expectedException,                expectedResult
                Arguments.of( tuple,   null,               null,                  null,               IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   fromStringToLong,   null,                  null,               IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   fromStringToLong,   fromIntegerToString,   null,               IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   fromStringToLong,   null,                  fromLongToString,   IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   null,               fromIntegerToString,   fromLongToString,   IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   fromStringToLong,   fromIntegerToString,   fromLongToString,   null,                             mappedTuple )
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
        } else {
            assertEquals(expectedResult, tuple.map(f1, f2, f3));
        }
    }


    static Stream<Arguments> map1TestCases() {
        Tuple3<String, Integer, Long> tuple = Tuple3.of("ZW", 23, 76L);
        Function<String, Long> fromStringToLong = s -> 3L + s.length();
        Tuple3<Long, Integer, Long> mappedTuple = Tuple3.of(5L, 23, 76L);
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
    public <T1, T2, T3, U> void map1_testCases(Tuple3<T1, T2, T3> tuple,
                                               Function<? super T1, ? extends U> mapper,
                                               Class<? extends Exception> expectedException,
                                               Tuple3<U, T2, T3> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> tuple.map1(mapper));
        } else {
            assertEquals(expectedResult, tuple.map1(mapper));
        }
    }


    static Stream<Arguments> map2TestCases() {
        Tuple3<Integer, Integer, String> tuple = Tuple3.of(7, 9, "ERT");
        Function<Integer, Long> fromIntegerToLong = i -> 2L * i;
        Tuple3<Integer, Long, String> mappedTuple = Tuple3.of(7, 18L, "ERT");
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
    public <T1, T2, T3, U> void map2_testCases(Tuple3<T1, T2, T3> tuple,
                                               Function<? super T2, ? extends U> mapper,
                                               Class<? extends Exception> expectedException,
                                               Tuple3<T1, U, T3> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> tuple.map2(mapper));
        } else {
            assertEquals(expectedResult, tuple.map2(mapper));
        }
    }


    static Stream<Arguments> map3TestCases() {
        Tuple3<Long, Long, String> tuple = Tuple3.of(15L, 99L, "GH");
        Function<String, Long> fromStringToLong = s -> s.length() * 3L;
        Tuple3<Long, Long, Long> mappedTuple = Tuple3.of(15L, 99L, 6L);
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
    public <T1, T2, T3, U> void map3_testCases(Tuple3<T1, T2, T3> tuple,
                                               Function<? super T3, ? extends U> mapper,
                                               Class<? extends Exception> expectedException,
                                               Tuple3<T1, T2, U> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> tuple.map3(mapper));
        }
        else {
            assertEquals(expectedResult, tuple.map3(mapper));
        }
    }


    static Stream<Arguments> applyTestCases() {
        Tuple3<Long, Integer, String> tuple = Tuple3.of(12L, 93, "THC");
        TriFunction<Long, Integer, String, Long> fromLongIntegerStringToLong = (l, i, s) -> l + i - s.length();
        TriFunction<Long, Integer, String, String> fromLongIntegerStringToString = (l, i, s) -> i + l + s;
        Long appliedLong = 102L;
        String appliedString = "105THC";
        return Stream.of(
                //@formatter:off
                //            tuple,   f,                               expectedException,                expectedResult
                Arguments.of( tuple,   null,                            IllegalArgumentException.class,   null ),
                Arguments.of( tuple,   fromLongIntegerStringToLong,     null,                             appliedLong ),
                Arguments.of( tuple,   fromLongIntegerStringToString,   null,                             appliedString )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("applyTestCases")
    @DisplayName("apply: test cases")
    public <T1, T2, T3, U> void apply_testCases(Tuple3<T1, T2, T3> tuple,
                                                TriFunction<? super T1, ? super T2, ? super T3, ? extends U> f,
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
        Tuple3<String, Integer, Boolean> tuple = Tuple3.of("ZZ", 77, TRUE);
        Long longValue = 34L;
        Integer integerValue = 55;
        return Stream.of(
                //@formatter:off
                //            tuple,   value,          expectedResult
                Arguments.of( tuple,   null,           Tuple4.of(null, tuple._1, tuple._2, tuple._3) ),
                Arguments.of( tuple,   longValue,      Tuple4.of(longValue, tuple._1, tuple._2, tuple._3) ),
                Arguments.of( tuple,   integerValue,   Tuple4.of(integerValue, tuple._1, tuple._2, tuple._3) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("prependTestCases")
    @DisplayName("prepend: test cases")
    public <T, T1, T2, T3> void prepend_testCases(Tuple3<T1, T2, T3> tuple,
                                                  T value,
                                                  Tuple4<T, T1, T2, T3> expectedResult) {
        assertEquals(expectedResult, tuple.prepend(value));
    }


    static Stream<Arguments> appendTestCases() {
        Tuple3<String, Integer, Boolean> tuple = Tuple3.of("ABC", 41, FALSE);
        Long longValue = 11L;
        Integer integerValue = 66;
        return Stream.of(
                //@formatter:off
                //            tuple,   value,          expectedResult
                Arguments.of( tuple,   null,           Tuple4.of(tuple._1, tuple._2, tuple._3, null) ),
                Arguments.of( tuple,   longValue,      Tuple4.of(tuple._1, tuple._2, tuple._3, longValue) ),
                Arguments.of( tuple,   integerValue,   Tuple4.of(tuple._1, tuple._2, tuple._3, integerValue) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("appendTestCases")
    @DisplayName("append: test cases")
    public <T, T1, T2, T3> void append_testCases(Tuple3<T1, T2, T3> tuple,
                                                 T value,
                                                 Tuple4<T1, T2, T3, T> expectedResult) {
        assertEquals(expectedResult, tuple.append(value));
    }


    static Stream<Arguments> concatTuple1TestCases() {
        Tuple3<String, Integer, Boolean> t1 = Tuple3.of("YHG", 33, TRUE);
        Tuple1<Long> t2 = Tuple1.of(21L);
        Tuple1<Integer> nullValueTuple = Tuple1.of(null);
        return Stream.of(
                //@formatter:off
                //            tuple,   tupleToConcat,    expectedResult
                Arguments.of( t1,      null,             Tuple4.of(t1._1, t1._2, t1._3, null) ),
                Arguments.of( t1,      nullValueTuple,   Tuple4.of(t1._1, t1._2, t1._3, null) ),
                Arguments.of( t1,      t2,               Tuple4.of(t1._1, t1._2, t1._3, t2._1) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("concatTuple1TestCases")
    @DisplayName("concat: using Tuple1 test cases")
    public <T1, T2, T3, T4> void concatTuple1_testCases(Tuple3<T1, T2, T3> tuple,
                                                        Tuple1<T4> tupleToConcat,
                                                        Tuple4<T1, T2, T3, T4> expectedResult) {
        assertEquals(expectedResult, tuple.concat(tupleToConcat));
    }


    static Stream<Arguments> concatTuple2TestCases() {
        Tuple3<String, Integer, Boolean> t1 = Tuple3.of("YHG", 33, TRUE);
        Tuple2<Long, Integer> t2 = Tuple2.of(21L, 11);
        Tuple2<Integer, Integer> nullValueTuple = Tuple2.of(null, null);
        return Stream.of(
                //@formatter:off
                //            tuple,   tupleToConcat,    expectedResult
                Arguments.of( t1,      null,             Tuple5.of(t1._1, t1._2, t1._3, null, null) ),
                Arguments.of( t1,      nullValueTuple,   Tuple5.of(t1._1, t1._2, t1._3, null, null) ),
                Arguments.of( t1,      t2,               Tuple5.of(t1._1, t1._2, t1._3, t2._1, t2._2) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("concatTuple2TestCases")
    @DisplayName("concat: using Tuple2 test cases")
    public <T1, T2, T3, T4, T5> void concatTuple2_testCases(Tuple3<T1, T2, T3> tuple,
                                                            Tuple2<T4, T5> tupleToConcat,
                                                            Tuple5<T1, T2, T3, T4, T5> expectedResult) {
        assertEquals(expectedResult, tuple.concat(tupleToConcat));
    }

}
