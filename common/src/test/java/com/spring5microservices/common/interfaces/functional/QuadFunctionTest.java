package com.spring5microservices.common.interfaces.functional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class QuadFunctionTest {

    static Stream<Arguments> applyTestCases() {
        QuadFunction<Integer, Integer, Integer, Integer, Integer> sumAllIntegers = (t, u, v, w) -> t + u + v + w;
        QuadFunction<String, String, String, String, Integer> sumAllStringLength = (t, u, v, w) -> t.length() + u.length() + v.length() + w.length();
        QuadFunction<Integer, String, Integer, String, Long> multiplyIntegerAndStringLength = (t, u, v, w) -> (long) t * u.length() * v * w.length();
        return Stream.of(
                //@formatter:off
                //            t,     u,      v,     w,       quadFunction,                     expectedResult
                Arguments.of( 0,     5,      4,     3,       sumAllIntegers,                   12 ),
                Arguments.of( "A",   "Bb",   "C",   "FFF",   sumAllStringLength,               7 ),
                Arguments.of( 3,     "x",    7,     "RT",    multiplyIntegerAndStringLength,   42L )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("applyTestCases")
    @DisplayName("apply: test cases")
    public <T, U, V, W, R> void apply_testCases(T t,
                                                U u,
                                                V v,
                                                W w,
                                                QuadFunction<T, U, V, W, R> quadFunction,
                                                R expectedResult) {
        assertEquals(expectedResult, quadFunction.apply(t, u, v, w));
    }


    static Stream<Arguments> andThenTestCases() {
        QuadFunction<Integer, Integer, Integer, Integer, Integer> sumAllIntegers = (t, u, v, w) -> t + u + v + w;
        QuadFunction<String, String, String, String, Integer> sumAllStringLength = (t, u, v, w) -> t.length() + u.length() + v.length() + w.length();
        QuadFunction<String, String, String, String, String> joinAllStrings = (t, u, v, w) -> t + u + v + w;

        Function<Integer, Integer> multiply2 = i -> i * 2;
        Function<String, Integer> stringLength = String::length;
        return Stream.of(
                //@formatter:off
                //            t,       u,      v,      w,      quadFunction,         afterFunction,       expectedException,            expectedResult
                Arguments.of( 0,       0,      0,      0,      null,                 null,                NullPointerException.class,   null ),
                Arguments.of( 3,       3,      3,      3,      sumAllIntegers,       null,                NullPointerException.class,   null ),
                Arguments.of( 2,       5,      1,      4,      sumAllIntegers,       multiply2,           null,                         24 ),
                Arguments.of( "A",     "Bb",   "C",    "TT",   sumAllStringLength,   multiply2,           null,                         12 ),
                Arguments.of( "yxT",   "tg",   "cf",   "Y",    joinAllStrings,       stringLength,        null,                         8 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("andThenTestCases")
    @DisplayName("andThen: test cases")
    public <T, U, V, W, R, Z> void andThen_testCases(T t,
                                                     U u,
                                                     V v,
                                                     W w,
                                                     QuadFunction<T, U, V, W, R> quadFunction,
                                                     Function<? super R, ? extends Z> afterFunction,
                                                     Class<? extends Exception> expectedException,
                                                     Z expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> quadFunction.andThen(afterFunction).apply(t, u, v, w));
        }
        else {
            assertEquals(expectedResult, quadFunction.andThen(afterFunction).apply(t, u, v, w));
        }
    }

}
