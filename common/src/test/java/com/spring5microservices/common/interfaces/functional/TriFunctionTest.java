package com.spring5microservices.common.interfaces.functional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TriFunctionTest {

    static Stream<Arguments> applyTestCases() {
        TriFunction<Integer, Integer, Integer, Integer> sumAllIntegers = (t, u, v) -> t + u + v;
        TriFunction<String, String, String, Integer> sumAllStringLength = (t, u, v) -> t.length() + u.length() + v.length();
        TriFunction<Integer, String, Integer, Long> multiplyIntegerAndStringLength = (t, u, v) -> (long) t * u.length() * v;
        return Stream.of(
                //@formatter:off
                //            t,     u,      v,     triFunction,                      expectedResult
                Arguments.of( 0,     5,      4,     sumAllIntegers,                   9 ),
                Arguments.of( "A",   "Bb",   "C",   sumAllStringLength,               4 ),
                Arguments.of( 3,     "x",    7,     multiplyIntegerAndStringLength,   21L )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("applyTestCases")
    @DisplayName("apply: test cases")
    public <T, U, V, R> void apply_testCases(T t, U u, V v,
                                             TriFunction<T, U, V, R> triFunction,
                                             R expectedResult) {
        assertEquals(expectedResult, triFunction.apply(t, u, v));
    }


    static Stream<Arguments> andThenTestCases() {
        TriFunction<Integer, Integer, Integer, Integer> sumAllIntegers = (t, u, v) -> t + u + v;
        TriFunction<String, String, String, Integer> sumAllStringLength = (t, u, v) -> t.length() + u.length() + v.length();
        TriFunction<String, String, String, String> joinAllStrings = (t, u, v) -> t + u + v;

        Function<Integer, Integer> multiply2 = i -> i * 2;
        Function<String, Integer> stringLength = String::length;
        return Stream.of(
                //@formatter:off
                //            t,       u,      v,      triFunction,          afterFunction,       expectedException,            expectedResult
                Arguments.of( 0,       0,      0,      null,                 null,                NullPointerException.class,   null ),
                Arguments.of( 3,       3,      3,      sumAllIntegers,       null,                NullPointerException.class,   null ),
                Arguments.of( 2,       5,      1,      sumAllIntegers,       multiply2,           null,                         16 ),
                Arguments.of( "A",     "Bb",   "C",    sumAllStringLength,   multiply2,           null,                         8 ),
                Arguments.of( "yxT",   "tg",   "cf",   joinAllStrings,       stringLength,        null,                         7 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("andThenTestCases")
    @DisplayName("andThen: test cases")
    public <T, U, V, R, Z> void andThen_testCases(T t, U u, V v,
                                                  TriFunction<T, U, V, R> triFunction,
                                                  Function<? super R, ? extends Z> afterFunction,
                                                  Class<? extends Exception> expectedException,
                                                  Z expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> triFunction.andThen(afterFunction).apply(t, u, v));
        }
        else {
            assertEquals(expectedResult, triFunction.andThen(afterFunction).apply(t, u, v));
        }
    }

}
