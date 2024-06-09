package com.spring5microservices.common.interfaces.function;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OctaFunctionTest {

    static Stream<Arguments> applyTestCases() {
        OctaFunction<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> sumAllIntegers =
                (t1, t2, t3, t4, t5, t6, t7, t8) -> t1 + t2 + t3 + t4 + t5 + t6 + t7 + t8;
        OctaFunction<String, String, String, String, String, String, String, String, Integer> sumAllStringLength =
                (t1, t2, t3, t4, t5, t6, t7, t8) -> t1.length() + t2.length() + t3.length() + t4.length() + t5.length() + t6.length() + t7.length() + t8.length();
        OctaFunction<Integer, String, Integer, String, Integer, String, Integer, String, Long> multiplyIntegerAndStringLength =
                (t1, t2, t3, t4, t5, t6, t7, t8) -> (long) t1 * t2.length() * t3 * t4.length() * t5 * t6.length() * t7 * t8.length();
        return Stream.of(
                //@formatter:off
                //            t1,    t2,     t3,    t4,      t5,   t6,     t7,       t8,      function,                         expectedResult
                Arguments.of( 0,     5,      4,     3,       9,    -1,     11,       -2,      sumAllIntegers,                   29 ),
                Arguments.of( "A",   "Bb",   "C",   "FFF",   "",   "h",    "12Yu",   "x",     sumAllStringLength,               13 ),
                Arguments.of( 3,     "x",    7,     "RT",    2,    "yC",   4,        "123",   multiplyIntegerAndStringLength,   2016L )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("applyTestCases")
    @DisplayName("apply: test cases")
    public <T1, T2, T3, T4, T5, T6, T7, T8, R> void apply_testCases(T1 t1,
                                                                    T2 t2,
                                                                    T3 t3,
                                                                    T4 t4,
                                                                    T5 t5,
                                                                    T6 t6,
                                                                    T7 t7,
                                                                    T8 t8,
                                                                    OctaFunction<T1, T2, T3, T4, T5, T6, T7, T8, R> function,
                                                                    R expectedResult) {
        assertEquals(
                expectedResult,
                function.apply(t1, t2, t3, t4, t5, t6, t7, t8)
        );
    }


    static Stream<Arguments> andThenTestCases() {
        OctaFunction<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> sumAllIntegers =
                (t1, t2, t3, t4, t5, t6, t7, t8) -> t1 + t2 + t3 + t4 + t5 + t6 + t7 + t8;
        OctaFunction<String, String, String, String, String, String, String, String, Integer> sumAllStringLength =
                (t1, t2, t3, t4, t5, t6, t7, t8) -> t1.length() + t2.length() + t3.length() + t4.length() + t5.length() + t6.length() + t7.length() + t8.length();
        OctaFunction<String, String, String, String, String, String, String, String, String> joinAllStrings =
                (t1, t2, t3, t4, t5, t6, t7, t8) -> t1 + t2 + t3 + t4 + t5 + t6 + t7 + t8;

        Function<Integer, Integer> multiply2 = i -> i * 2;
        Function<String, Integer> stringLength = String::length;
        return Stream.of(
                //@formatter:off
                //            t1,      t2,     t3,     t4,     t5,     t6,      t7,       t8,     function,             afterFunction,       expectedException,            expectedResult
                Arguments.of( 0,       0,      0,      0,      0,      0,       0,        0,      null,                 null,                NullPointerException.class,   null ),
                Arguments.of( 3,       3,      3,      3,      3,      -1,      9,        4,      sumAllIntegers,       null,                NullPointerException.class,   null ),
                Arguments.of( 2,       5,      1,      4,      7,      3,       -1,       -3,     sumAllIntegers,       multiply2,           null,                         36 ),
                Arguments.of( "A",     "Bb",   "C",    "TT",   "",     "TYH",   "12TI",   "x",    sumAllStringLength,   multiply2,           null,                         28 ),
                Arguments.of( "yxT",   "tg",   "cf",   "Y",    "hy",   "B",     "",       "12",   joinAllStrings,       stringLength,        null,                         13 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("andThenTestCases")
    @DisplayName("andThen: test cases")
    public <T1, T2, T3, T4, T5, T6, T7, T8, R, Z> void andThen_testCases(T1 t1,
                                                                         T2 t2,
                                                                         T3 t3,
                                                                         T4 t4,
                                                                         T5 t5,
                                                                         T6 t6,
                                                                         T7 t7,
                                                                         T8 t8,
                                                                         OctaFunction<T1, T2, T3, T4, T5, T6, T7, T8, R> function,
                                                                         Function<? super R, ? extends Z> afterFunction,
                                                                         Class<? extends Exception> expectedException,
                                                                         Z expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> function.andThen(afterFunction).apply(t1, t2, t3, t4, t5, t6, t7, t8)
            );
        } else {
            assertEquals(
                    expectedResult,
                    function.andThen(afterFunction).apply(t1, t2, t3, t4, t5, t6, t7, t8)
            );
        }
    }

}
