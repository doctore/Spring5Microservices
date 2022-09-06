package com.spring5microservices.common.collection.tuple;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.spring5microservices.common.collection.tuple.Tuple0.comparator;
import static com.spring5microservices.common.collection.tuple.Tuple0.instance;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Tuple0Test {

    @Test
    @DisplayName("instance: when is invoked then empty Tuple0 is returned")
    public void instance_whenIsInvoked_thenEmptyTuple0IsReturned() {
        Tuple0 result = instance();
        assertNotNull(result);
    }


    @Test
    @DisplayName("comparator: when is invoked then 0 returned")
    public void comparator_whenIsInvoked_then0IsReturned() {
        int result = comparator().compare(instance(), instance());
        assertEquals(0, result);
    }


    @Test
    @DisplayName("arity: when is invoked then 0 returned")
    public void arity_whenIsInvoked_then0IsReturned() {
        int result = instance().arity();
        assertEquals(0, result);
    }


    static Stream<Arguments> equalsTestCases() {
        return Stream.of(
                //@formatter:off
                //            tuple,        objectToCompare,   expectedResult
                Arguments.of( instance(),   null,              false ),
                Arguments.of( instance(),   "1",               false ),
                Arguments.of( instance(),   instance(),        true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("equalsTestCases")
    @DisplayName("equals: test cases")
    public void equals_testCases(Tuple0 tuple,
                                 Object objectToCompare,
                                 boolean expectedResult) {
        assertEquals(expectedResult, tuple.equals(objectToCompare));
    }


    @Test
    @DisplayName("hashCode: when is invoked then 1 returned")
    public void hashCode_whenIsInvoked_then1IsReturned() {
        int result = instance().hashCode();
        assertEquals(1, result);
    }


    @Test
    @DisplayName("toString: when is invoked then () returned")
    public void toString_whenIsInvoked_thenEmptyIsReturned() {
        String result = instance().toString();
        assertEquals("()", result);
    }


    static Stream<Arguments> applyTestCases() {
        Supplier<Integer> fInteger = () -> 1;
        Supplier<Tuple0> fIdentity = Tuple0::instance;
        return Stream.of(
                //@formatter:off
                //            f,           expectedException,                expectedResult
                Arguments.of( null,        IllegalArgumentException.class,   null ),
                Arguments.of( fInteger,    null,                             1 ),
                Arguments.of( fIdentity,   null,                             instance() )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("applyTestCases")
    @DisplayName("apply: test cases")
    public <U> void apply_testCases(Supplier<? extends U> f,
                                    Class<? extends Exception> expectedException,
                                    U expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> instance().apply(f));
        } else {
            assertEquals(expectedResult, instance().apply(f));
        }
    }


    static Stream<Arguments> prependTestCases() {
        String stringValue = "TFG";
        Integer integerValue = 43;
        return Stream.of(
                //@formatter:off
                //            value,          expectedResult
                Arguments.of( null,           Tuple1.of(null) ),
                Arguments.of( stringValue,    Tuple1.of(stringValue) ),
                Arguments.of( integerValue,   Tuple1.of(integerValue) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("prependTestCases")
    @DisplayName("prepend: test cases")
    public <T1> void prepend_testCases(T1 value,
                                       Tuple1<T1> expectedResult) {
        assertEquals(expectedResult, instance().prepend(value));
    }


    static Stream<Arguments> appendTestCases() {
        String stringValue = "TY";
        Integer integerValue = 4;
        return Stream.of(
                //@formatter:off
                //            value,          expectedResult
                Arguments.of( null,           Tuple1.of(null) ),
                Arguments.of( stringValue,    Tuple1.of(stringValue) ),
                Arguments.of( integerValue,   Tuple1.of(integerValue) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("appendTestCases")
    @DisplayName("append: test cases")
    public <T1> void append_testCases(T1 value,
                                      Tuple1<T1> expectedResult) {
        assertEquals(expectedResult, instance().append(value));
    }


    static Stream<Arguments> concatTuple1TestCases() {
        Tuple1<String> t1 = Tuple1.of("TYHG");
        Tuple1<Long> t2 = Tuple1.of(21L);
        return Stream.of(
                //@formatter:off
                //            tuple,   expectedResult
                Arguments.of( null,    Tuple1.empty() ),
                Arguments.of( t1,      t1 ),
                Arguments.of( t2,      t2 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("concatTuple1TestCases")
    @DisplayName("concat: using Tuple1 test cases")
    public <T1> void concatTuple1_testCases(Tuple1<T1> tuple,
                                            Tuple1<T1> expectedResult) {
        assertEquals(expectedResult, instance().concat(tuple));
    }


    static Stream<Arguments> concatTuple2TestCases() {
        Tuple2<String, Integer> t1 = Tuple2.of("TYHG", 534);
        Tuple2<Long, Integer> t2 = Tuple2.of(21L, 677);
        return Stream.of(
                //@formatter:off
                //            tuple,   expectedResult
                Arguments.of( null,    Tuple2.empty() ),
                Arguments.of( t1,      t1 ),
                Arguments.of( t2,      t2 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("concatTuple2TestCases")
    @DisplayName("concat: using Tuple2 test cases")
    public <T1, T2> void concatTuple2_testCases(Tuple2<T1, T2> tuple,
                                                Tuple2<T1, T2> expectedResult) {
        assertEquals(expectedResult, instance().concat(tuple));
    }


    static Stream<Arguments> concatTuple3TestCases() {
        Tuple3<String, Integer, Integer> t1 = Tuple3.of("TYHG", 534, 999);
        Tuple3<Long, Integer, String> t2 = Tuple3.of(21L, 677, "POL");
        return Stream.of(
                //@formatter:off
                //            tuple,   expectedResult
                Arguments.of( null,    Tuple3.empty() ),
                Arguments.of( t1,      t1 ),
                Arguments.of( t2,      t2 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("concatTuple3TestCases")
    @DisplayName("concat: using Tuple3 test cases")
    public <T1, T2, T3> void concatTuple3_testCases(Tuple3<T1, T2, T3> tuple,
                                                    Tuple3<T1, T2, T3> expectedResult) {
        assertEquals(expectedResult, instance().concat(tuple));
    }


    static Stream<Arguments> concatTuple4TestCases() {
        Tuple4<String, Integer, Integer, Boolean> t1 = Tuple4.of("TYHG", 534, 999, TRUE);
        Tuple4<Long, Integer, Boolean, String> t2 = Tuple4.of(21L, 677, FALSE, "POL");
        return Stream.of(
                //@formatter:off
                //            tuple,   expectedResult
                Arguments.of( null,    Tuple4.empty() ),
                Arguments.of( t1,      t1 ),
                Arguments.of( t2,      t2 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("concatTuple4TestCases")
    @DisplayName("concat: using Tuple4 test cases")
    public <T1, T2, T3, T4> void concatTuple4_testCases(Tuple4<T1, T2, T3, T4> tuple,
                                                        Tuple4<T1, T2, T3, T4> expectedResult) {
        assertEquals(expectedResult, instance().concat(tuple));
    }


    static Stream<Arguments> concatTuple5TestCases() {
        Tuple5<String, Integer, Integer, Integer, Boolean> t1 = Tuple5.of("TYHG", 534, 999, 1, TRUE);
        Tuple5<Long, Integer, Boolean, Boolean, String> t2 = Tuple5.of(21L, 677, FALSE, TRUE, "POL");
        return Stream.of(
                //@formatter:off
                //            tuple,   expectedResult
                Arguments.of( null,    Tuple5.empty() ),
                Arguments.of( t1,      t1 ),
                Arguments.of( t2,      t2 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("concatTuple5TestCases")
    @DisplayName("concat: using Tuple5 test cases")
    public <T1, T2, T3, T4, T5> void concatTuple5_testCases(Tuple5<T1, T2, T3, T4, T5> tuple,
                                                            Tuple5<T1, T2, T3, T4, T5> expectedResult) {
        assertEquals(expectedResult, instance().concat(tuple));
    }

}
