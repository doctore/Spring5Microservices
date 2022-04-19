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
    @DisplayName("arity: when is invoked then 0 returned")
    public void arity_whenIsInvoked_then0IsReturned() {
        int result = instance().arity();
        assertEquals(0, result);
    }


    @Test
    @DisplayName("comparator: when is invoked then 0 returned")
    public void comparator_whenIsInvoked_then0IsReturned() {
        int result = comparator().compare(instance(), instance());
        assertEquals(0, result);
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
        }
        else {
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
        Tuple1<String> stringTuple = Tuple1.of("TYHG");
        Tuple1<Long> longTuple = Tuple1.of(21L);
        return Stream.of(
                //@formatter:off
                //            tuple,         expectedException,                expectedResult
                Arguments.of( null,          IllegalArgumentException.class,   null ),
                Arguments.of( stringTuple,   null,                             stringTuple ),
                Arguments.of( longTuple,     null,                             longTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("concatTuple1TestCases")
    @DisplayName("concat: using Tuple1 test cases")
    public <T1> void concatTuple1_testCases(Tuple1<T1> tuple,
                                            Class<? extends Exception> expectedException,
                                            Tuple1<T1> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> instance().concat(tuple));
        }
        else {
            assertEquals(expectedResult, instance().concat(tuple));
        }
    }


    static Stream<Arguments> concatTuple2TestCases() {
        Tuple2<String, Integer> stringIntegerTuple = Tuple2.of("TYHG", 534);
        Tuple2<Long, Integer> longIntegerTuple = Tuple2.of(21L, 677);
        return Stream.of(
                //@formatter:off
                //            tuple,                expectedException,                expectedResult
                Arguments.of( null,                 IllegalArgumentException.class,   null ),
                Arguments.of( stringIntegerTuple,   null,                             stringIntegerTuple ),
                Arguments.of( longIntegerTuple,     null,                             longIntegerTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("concatTuple2TestCases")
    @DisplayName("concat: using Tuple2 test cases")
    public <T1, T2> void concatTuple2_testCases(Tuple2<T1, T2> tuple,
                                                Class<? extends Exception> expectedException,
                                                Tuple2<T1, T2> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> instance().concat(tuple));
        }
        else {
            assertEquals(expectedResult, instance().concat(tuple));
        }
    }


    static Stream<Arguments> concatTuple3TestCases() {
        Tuple3<String, Integer, Integer> stringIntegerIntegerTuple = Tuple3.of("TYHG", 534, 999);
        Tuple3<Long, Integer, String> longIntegerStringTuple = Tuple3.of(21L, 677, "POL");
        return Stream.of(
                //@formatter:off
                //            tuple,                       expectedException,                expectedResult
                Arguments.of( null,                        IllegalArgumentException.class,   null ),
                Arguments.of( stringIntegerIntegerTuple,   null,                             stringIntegerIntegerTuple ),
                Arguments.of( longIntegerStringTuple,      null,                             longIntegerStringTuple )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("concatTuple3TestCases")
    @DisplayName("concat: using Tuple3 test cases")
    public <T1, T2, T3> void concatTuple3_testCases(Tuple3<T1, T2, T3> tuple,
                                                    Class<? extends Exception> expectedException,
                                                    Tuple3<T1, T2, T3> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> instance().concat(tuple));
        }
        else {
            assertEquals(expectedResult, instance().concat(tuple));
        }
    }


    static Stream<Arguments> equalsTestCases() {
        return Stream.of(
                //@formatter:off
                //            tuple,        objectToCompare,   expectedResult
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

}
