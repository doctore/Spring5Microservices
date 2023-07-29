package com.spring5microservices.common.util;

import com.spring5microservices.common.PizzaDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.AbstractMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.spring5microservices.common.PizzaEnum.CARBONARA;
import static com.spring5microservices.common.PizzaEnum.MARGUERITA;
import static com.spring5microservices.common.util.FunctionUtil.fromBiFunctionToMapEntryFunction;
import static com.spring5microservices.common.util.FunctionUtil.fromBiFunctionsToMapEntriesFunction;
import static com.spring5microservices.common.util.FunctionUtil.fromFunctionsToMapEntryFunction;
import static com.spring5microservices.common.util.FunctionUtil.overwriteWithNew;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FunctionUtilTest {

    static Stream<Arguments> fromBiFunctionToMapEntryFunctionTestCases() {
        Map.Entry<String, Integer> emptyEntry = new AbstractMap.SimpleEntry<>(
                null,
                null
        );
        BiFunction<String, Integer, Integer> sumStringLengthAndInteger =
                (s, i) -> {
                   final int sLength = null == s ? 0 : s.length();
                   final int finalI = null == i ? 0 : i;
                   return sLength + finalI;
                };
        return Stream.of(
                //@formatter:off
                //            entry,                 keyValueMapper,              expectedException,                expectedResult
                Arguments.of( null,                  null,                        IllegalArgumentException.class,   null ),
                Arguments.of( emptyEntry,            null,                        IllegalArgumentException.class,   null ),
                Arguments.of( null,                  sumStringLengthAndInteger,   NullPointerException.class,       null ),
                Arguments.of( emptyEntry,            sumStringLengthAndInteger,   null,                             0 ),
                Arguments.of( Map.entry("10", 32),   sumStringLengthAndInteger,   null,                             34 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromBiFunctionToMapEntryFunctionTestCases")
    @DisplayName("fromBiFunctionToMapEntryFunction: test cases")
    public <T, K, V> void fromBiFunctionToMapEntryFunction_testCases(Map.Entry<? super K, ? super V> entry,
                                                                     BiFunction<? super K, ? super V, ? extends T> keyValueMapper,
                                                                     Class<? extends Exception> expectedException,
                                                                     T expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException,
                    () -> {
                        // Required because sometimes the Java compiler is stupid
                        Function<Map.Entry<K, V>, T> functionToApply = fromBiFunctionToMapEntryFunction(
                                keyValueMapper
                        );
                        functionToApply.apply((Map.Entry<K, V>)entry);
                    }
            );
        } else {
            // Required because sometimes the Java compiler is stupid
            Function<Map.Entry<K, V>, T> functionToApply = fromBiFunctionToMapEntryFunction(
                    keyValueMapper
            );
            assertEquals(expectedResult,
                    functionToApply.apply((Map.Entry<K, V>)entry)
            );
        }
    }


    static Stream<Arguments> fromBiFunctionsToMapEntriesFunctionTestCases() {
        Map.Entry<String, Integer> emptyEntry = new AbstractMap.SimpleEntry<>(
                null,
                null
        );
        BiFunction<String, Integer, Integer> sumStringLengthAndInteger =
                (s, i) -> {
                    final int sLength = null == s ? 0 : s.length();
                    final int finalI = null == i ? 0 : i;
                    return sLength + finalI;
                };
        BiFunction<String, Integer, String> concatStringAndInteger =
                (s, i) ->
                        (null == s ? "" : s)
                      + (null == i ? "" : i.toString());

        Map.Entry<Integer, String> expectedResultEmptyEntry = Map.entry(0, "");
        Map.Entry<Integer, String> expectedResultNotEmptyEntry = Map.entry(34, "1032");
        return Stream.of(
                //@formatter:off
                //            entry,                 keyMapper,                   valueMapper               expectedException,                expectedResult
                Arguments.of( null,                  null,                        null,                     IllegalArgumentException.class,   null ),
                Arguments.of( null,                  sumStringLengthAndInteger,   null,                     IllegalArgumentException.class,   null ),
                Arguments.of( null,                  null,                        concatStringAndInteger,   IllegalArgumentException.class,   null ),
                Arguments.of( emptyEntry,            null,                        null,                     IllegalArgumentException.class,   null ),
                Arguments.of( emptyEntry,            sumStringLengthAndInteger,   null,                     IllegalArgumentException.class,   null ),
                Arguments.of( emptyEntry,            null,                        concatStringAndInteger,   IllegalArgumentException.class,   null ),
                Arguments.of( null,                  sumStringLengthAndInteger,   concatStringAndInteger,   NullPointerException.class,       null ),
                Arguments.of( emptyEntry,            sumStringLengthAndInteger,   concatStringAndInteger,   null,                             expectedResultEmptyEntry ),
                Arguments.of( Map.entry("10", 32),   sumStringLengthAndInteger,   concatStringAndInteger,   null,                             expectedResultNotEmptyEntry )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromBiFunctionsToMapEntriesFunctionTestCases")
    @DisplayName("fromBiFunctionsToMapEntriesFunction: test cases")
    public <K1, K2, V1, V2> void fromBiFunctionsToMapEntriesFunction_testCases(Map.Entry<? super K1, ? super V1> entry,
                                                                               BiFunction<? super K1, ? super V1, ? extends K2> keyMapper,
                                                                               BiFunction<? super K1, ? super V1, ? extends V2> valueMapper,
                                                                               Class<? extends Exception> expectedException,
                                                                               Map.Entry<K2, V2> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException,
                    () -> {
                        // Required because sometimes the Java compiler is stupid
                        Function<Map.Entry<K1, V1>, Map.Entry<K2, V2>> functionToApply = fromBiFunctionsToMapEntriesFunction(
                                keyMapper,
                                valueMapper
                        );
                        functionToApply.apply((Map.Entry<K1, V1>)entry);
                    }
            );
        } else {
            // Required because sometimes the Java compiler is stupid
            Function<Map.Entry<K1, V1>, Map.Entry<K2, V2>> functionToApply = fromBiFunctionsToMapEntriesFunction(
                    keyMapper,
                    valueMapper
            );
            assertEquals(expectedResult,
                    functionToApply.apply((Map.Entry<K1, V1>)entry)
            );
        }
    }


    static Stream<Arguments> fromFunctionsToMapEntryFunctionTestCases() {
        Function<Integer, String> multiply2String =
                i -> null == i
                        ? ""
                        : String.valueOf(i * 2);
        Function<Integer, Integer> plus10 =
                i -> null == i
                        ? 0
                        : i + 10;
        return Stream.of(
                //@formatter:off
                //            t,      keyMapper,         valueMapper,       expectedException,                expectedResult
                Arguments.of( null,   null,              null,              IllegalArgumentException.class,   null ),
                Arguments.of( 11,     multiply2String,   null,              IllegalArgumentException.class,   null ),
                Arguments.of( 11,     null,              plus10,            IllegalArgumentException.class,   null ),
                Arguments.of( null,   multiply2String,   plus10,            null,                             Map.entry("", 0) ),
                Arguments.of( 11,     multiply2String,   plus10,            null,                             Map.entry("22", 21) ),
                Arguments.of( 11,     plus10,            multiply2String,   null,                             Map.entry(21, "22") )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("fromFunctionsToMapEntryFunctionTestCases")
    @DisplayName("fromFunctionsToMapEntryFunction: test cases")
    public <T, K, V> void fromFunctionsToMapEntryFunction_testCases(T t,
                                                                    Function<? super T, ? extends K> keyMapper,
                                                                    Function<? super T, ? extends V> valueMapper,
                                                                    Class<? extends Exception> expectedException,
                                                                    Map.Entry<K, V> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException,
                    () ->
                            fromFunctionsToMapEntryFunction(
                                    keyMapper,
                                    valueMapper
                            )
                            .apply(t)
            );
        } else {
            assertEquals(expectedResult,
                    fromFunctionsToMapEntryFunction(
                            keyMapper,
                            valueMapper
                    )
                    .apply(t)
            );
        }
    }


    static Stream<Arguments> overwriteWithNewTestCases() {
        PizzaDto carbonara = new PizzaDto(CARBONARA.getInternalPropertyValue(), 5D);
        PizzaDto marguerita = new PizzaDto(MARGUERITA.getInternalPropertyValue(), 15D);
        return Stream.of(
                //@formatter:off
                //            oldInstance,   newInstance,   expectedResult
                Arguments.of( null,          null,          null ),
                Arguments.of( 11,            null,          null ),
                Arguments.of( null,          11,            11 ),
                Arguments.of( carbonara,     marguerita,    marguerita )

        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("overwriteWithNewTestCases")
    @DisplayName("overwriteWithNew: test cases")
    public <T> void overwriteWithNew_testCases(T oldInstance,
                                               T newInstance,
                                               T expectedResult) {
        assertEquals(expectedResult, overwriteWithNew().apply(oldInstance, newInstance));
    }

}
