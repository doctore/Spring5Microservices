package com.spring5microservices.common.interfaces.functional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PentaFunctionTest {

    static Stream<Arguments> applyTestCases() {
        PentaFunction<Integer, Integer, Integer, Integer, Integer, Integer> sumAllIntegers = (t, u, v, w, x) -> t + u + v + w + x;
        PentaFunction<String, String, String, String, String, Integer> sumAllStringLength = (t, u, v, w, x) -> t.length() + u.length() + v.length() + w.length() + x.length();
        PentaFunction<Integer, String, Integer, String, Integer, Long> multiplyIntegerAndStringLength = (t, u, v, w, x) -> (long) t * u.length() * v * w.length() * x;
        return Stream.of(
                //@formatter:off
                //            t,     u,      v,     w,       x,    pentaFunction,                    expectedResult
                Arguments.of( 0,     5,      4,     3,       9,    sumAllIntegers,                   21 ),
                Arguments.of( "A",   "Bb",   "C",   "FFF",   "",   sumAllStringLength,               7 ),
                Arguments.of( 3,     "x",    7,     "RT",    2,    multiplyIntegerAndStringLength,   84L )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("applyTestCases")
    @DisplayName("apply: test cases")
    public <T, U, V, W, X, R> void apply_testCases(T t,
                                                   U u,
                                                   V v,
                                                   W w,
                                                   X x,
                                                   PentaFunction<T, U, V, W, X, R> pentaFunction,
                                                   R expectedResult) {
        assertEquals(expectedResult, pentaFunction.apply(t, u, v, w, x));
    }


    static Stream<Arguments> andThenTestCases() {
        PentaFunction<Integer, Integer, Integer, Integer, Integer, Integer> sumAllIntegers = (t, u, v, w, x) -> t + u + v + w + x;
        PentaFunction<String, String, String, String, String, Integer> sumAllStringLength = (t, u, v, w, x) -> t.length() + u.length() + v.length() + w.length() + x.length();
        PentaFunction<String, String, String, String, String, String> joinAllStrings = (t, u, v, w, x) -> t + u + v + w + x;

        Function<Integer, Integer> multiply2 = i -> i * 2;
        Function<String, Integer> stringLength = String::length;
        return Stream.of(
                //@formatter:off
                //            t,       u,      v,      w,      x,      pentaFunction,        afterFunction,       expectedException,            expectedResult
                Arguments.of( 0,       0,      0,      0,      0,      null,                 null,                NullPointerException.class,   null ),
                Arguments.of( 3,       3,      3,      3,      3,      sumAllIntegers,       null,                NullPointerException.class,   null ),
                Arguments.of( 2,       5,      1,      4,      7,      sumAllIntegers,       multiply2,           null,                         38 ),
                Arguments.of( "A",     "Bb",   "C",    "TT",   "",     sumAllStringLength,   multiply2,           null,                         12 ),
                Arguments.of( "yxT",   "tg",   "cf",   "Y",    "hy",   joinAllStrings,       stringLength,        null,                         10 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("andThenTestCases")
    @DisplayName("andThen: test cases")
    public <T, U, V, W, X, R, Z> void andThen_testCases(T t,
                                                        U u,
                                                        V v,
                                                        W w,
                                                        X x,
                                                        PentaFunction<T, U, V, W, X, R> pentaFunction,
                                                        Function<? super R, ? extends Z> afterFunction,
                                                        Class<? extends Exception> expectedException,
                                                        Z expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> pentaFunction.andThen(afterFunction).apply(t, u, v, w, x));
        } else {
            assertEquals(expectedResult, pentaFunction.andThen(afterFunction).apply(t, u, v, w, x));
        }
    }

}
