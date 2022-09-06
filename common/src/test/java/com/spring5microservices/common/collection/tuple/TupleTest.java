package com.spring5microservices.common.collection.tuple;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static com.spring5microservices.common.collection.tuple.Tuple.fromEntry;
import static java.lang.Boolean.FALSE;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TupleTest {

    static Stream<Arguments> globalAppendTestCases() {
        Tuple0 tuple0 = Tuple0.instance();
        Tuple1<String> tuple1 = Tuple1.of("t1_value");
        Tuple2<String, Long> tuple2 = tuple1.append(12L);
        Tuple3<String, Long, Boolean> tuple3 = tuple2.append(FALSE);
        Tuple4<String, Long, Boolean, Integer> tuple4 = tuple3.append(11);
        Tuple5<String, Long, Boolean, Integer, String> tuple5 = tuple4.append("t5_last_value");
        return Stream.of(
                //@formatter:off
                //            tuple,    value,             expectedException,                     expectedResult
                Arguments.of( tuple0,   "t1_value",        null,                                  tuple1 ),
                Arguments.of( tuple1,   12L,               null,                                  tuple2 ),
                Arguments.of( tuple2,   FALSE,             null,                                  tuple3 ),
                Arguments.of( tuple3,   11,                null,                                  tuple4 ),
                Arguments.of( tuple4,   "t5_last_value",   null,                                  tuple5 ),
                Arguments.of( tuple5,   "Does not care",   UnsupportedOperationException.class,   null )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("globalAppendTestCases")
    @DisplayName("globalAppend: test cases")
    public <T> void globalAppend_testCases(Tuple tuple,
                                           T value,
                                           Class<? extends Exception> expectedException,
                                           Tuple expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> tuple.globalAppend(value));
        } else {
            assertEquals(expectedResult, tuple.globalAppend(value));
        }
    }


    static Stream<Arguments> fromEntryTestCases() {
        Map.Entry<String, String> nullKeyValueEntry = new AbstractMap.SimpleEntry<>(null, null);
        Map.Entry<Integer, String> onlyKeyEntry = new AbstractMap.SimpleEntry<>(1, null);
        Map.Entry<String, Integer> onlyValueEntry = new AbstractMap.SimpleEntry<>(null, 12);
        Map.Entry<String, String> keyAndValueEntry = new AbstractMap.SimpleEntry<>("A", "11");
        return Stream.of(
                //@formatter:off
                //            entry,               expectedResult
                Arguments.of( null,                empty() ),
                Arguments.of( nullKeyValueEntry,   of(Tuple2.of(null, null)) ),
                Arguments.of( onlyKeyEntry,        of(Tuple2.of(onlyKeyEntry.getKey(), null)) ),
                Arguments.of( onlyValueEntry,      of(Tuple2.of(null, onlyValueEntry.getValue())) ),
                Arguments.of( keyAndValueEntry,    of(Tuple2.of(keyAndValueEntry.getKey(), keyAndValueEntry.getValue())) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromEntryTestCases")
    @DisplayName("fromEntry: test cases")
    public <T1, T2> void fromEntry_testCases(Map.Entry<? extends T1, ? extends T2> entry,
                                             Optional<Tuple2<T1, T2>> expectedResult) {
        assertEquals(expectedResult, fromEntry(entry));
    }


    static Stream<Arguments> ofTuple1TestCases() {
        String stringValue = "ABC";
        Integer integerValue = 25;
        return Stream.of(
                //@formatter:off
                //            t1,             expectedResult
                Arguments.of( null,           Tuple1.of(null) ),
                Arguments.of( stringValue,    Tuple1.of(stringValue) ),
                Arguments.of( integerValue,   Tuple1.of(integerValue) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("ofTuple1TestCases")
    @DisplayName("of: returning Tuple1 test cases")
    public <T1> void ofTuple1_testCases(T1 t1,
                                        Tuple1<T1> expectedResult) {
        assertEquals(expectedResult, Tuple.of(t1));
    }


    static Stream<Arguments> ofTuple2TestCases() {
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
    @MethodSource("ofTuple2TestCases")
    @DisplayName("of: returning Tuple2 test cases")
    public <T1, T2> void ofTuple2_testCases(T1 t1,
                                            T2 t2,
                                            Tuple2<T1, T2> expectedResult) {
        assertEquals(expectedResult, Tuple.of(t1, t2));
    }


    static Stream<Arguments> ofTuple3TestCases() {
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
    @MethodSource("ofTuple3TestCases")
    @DisplayName("of: returning Tuple3 test cases")
    public <T1, T2, T3> void ofTuple3_testCases(T1 t1,
                                                T2 t2,
                                                T3 t3,
                                                Tuple3<T1, T2, T3> expectedResult) {
        assertEquals(expectedResult, Tuple.of(t1, t2, t3));
    }


    static Stream<Arguments> ofTuple4TestCases() {
        String stringValue = "ABC";
        Integer integerValue = 25;
        Long longValue = 33L;
        Boolean booleanValue = Boolean.TRUE;
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
                Arguments.of( null,           null,           stringValue,    integerValue,   Tuple4.of(null, null, stringValue, integerValue) ),
                Arguments.of( stringValue,    integerValue,   longValue,      booleanValue,   Tuple4.of(stringValue, integerValue, longValue, booleanValue) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("ofTuple4TestCases")
    @DisplayName("of: returning Tuple4 test cases")
    public <T1, T2, T3, T4> void ofTuple4_testCases(T1 t1,
                                                    T2 t2,
                                                    T3 t3,
                                                    T4 t4,
                                                    Tuple4<T1, T2, T3, T4> expectedResult) {
        assertEquals(expectedResult, Tuple.of(t1, t2, t3, t4));
    }


    static Stream<Arguments> ofTuple5TestCases() {
        String stringValue = "ABC";
        Integer integerValue = 25;
        Long longValue = 33L;
        Boolean booleanValue = Boolean.TRUE;
        Double doubleValue = 23.2d;
        return Stream.of(
                //@formatter:off
                //            t1,             t2,             t3,             t4,             t5,            expectedResult
                Arguments.of( null,           null,           null,           null,           null,          Tuple5.of(null, null, null, null, null) ),
                Arguments.of( stringValue,    null,           null,           null,           null,          Tuple5.of(stringValue, null, null, null, null) ),
                Arguments.of( null,           stringValue,    null,           null,           null,          Tuple5.of(null, stringValue, null, null, null) ),
                Arguments.of( null,           null,           stringValue,    null,           null,          Tuple5.of(null, null, stringValue, null, null) ),
                Arguments.of( null,           null,           null,           stringValue,    null,          Tuple5.of(null, null, null, stringValue, null) ),
                Arguments.of( null,           null,           null,           null,           stringValue,   Tuple5.of(null, null, null, null, stringValue) ),
                Arguments.of( null,           stringValue,    integerValue,   null,           null,          Tuple5.of(null, stringValue, integerValue, null, null) ),
                Arguments.of( stringValue,    integerValue,   null,           null,           null,          Tuple5.of(stringValue, integerValue, null, null, null) ),
                Arguments.of( null,           null,           stringValue,    integerValue,   doubleValue,   Tuple5.of(null, null, stringValue, integerValue, doubleValue) ),
                Arguments.of( stringValue,    integerValue,   longValue,      booleanValue,   doubleValue,   Tuple5.of(stringValue, integerValue, longValue, booleanValue, doubleValue) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("ofTuple5TestCases")
    @DisplayName("of: returning Tuple5 test cases")
    public <T1, T2, T3, T4, T5> void ofTuple5_testCases(T1 t1,
                                                        T2 t2,
                                                        T3 t3,
                                                        T4 t4,
                                                        T5 t5,
                                                        Tuple5<T1, T2, T3, T4, T5> expectedResult) {
        assertEquals(expectedResult, Tuple.of(t1, t2, t3, t4, t5));
    }

}
