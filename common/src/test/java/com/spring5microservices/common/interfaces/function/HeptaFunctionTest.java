package com.spring5microservices.common.interfaces.function;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HeptaFunctionTest {

    static Stream<Arguments> applyTestCases() {
        HeptaFunction<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> sumAllIntegers =
                (t1, t2, t3, t4, t5, t6, t7) -> t1 + t2 + t3 + t4 + t5 + t6 + t7;
        HeptaFunction<String, String, String, String, String, String, String, Integer> sumAllStringLength =
                (t1, t2, t3, t4, t5, t6, t7) -> t1.length() + t2.length() + t3.length() + t4.length() + t5.length() + t6.length() + t7.length();
        HeptaFunction<Integer, String, Integer, String, Integer, String, Integer, Long> multiplyIntegerAndStringLength =
                (t1, t2, t3, t4, t5, t6, t7) -> (long) t1 * t2.length() * t3 * t4.length() * t5 * t6.length() * t7;
        return Stream.of(
                //@formatter:off
                //            t1,    t2,     t3,    t4,      t5,   t6,     t7,       function,                         expectedResult
                Arguments.of( 0,     5,      4,     3,       9,    -1,     11,       sumAllIntegers,                   31 ),
                Arguments.of( "A",   "Bb",   "C",   "FFF",   "",   "h",    "12Yu",   sumAllStringLength,               12 ),
                Arguments.of( 3,     "x",    7,     "RT",    2,    "yC",   4,        multiplyIntegerAndStringLength,   672L )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("applyTestCases")
    @DisplayName("apply: test cases")
    public <T1, T2, T3, T4, T5, T6, T7, R> void apply_testCases(T1 t1,
                                                                T2 t2,
                                                                T3 t3,
                                                                T4 t4,
                                                                T5 t5,
                                                                T6 t6,
                                                                T7 t7,
                                                                HeptaFunction<T1, T2, T3, T4, T5, T6, T7, R> function,
                                                                R expectedResult) {
        assertEquals(
                expectedResult,
                function.apply(t1, t2, t3, t4, t5, t6, t7)
        );
    }


    static Stream<Arguments> andThenTestCases() {
        HeptaFunction<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer> sumAllIntegers =
                (t1, t2, t3, t4, t5, t6, t7) -> t1 + t2 + t3 + t4 + t5 + t6 + t7;
        HeptaFunction<String, String, String, String, String, String, String, Integer> sumAllStringLength =
                (t1, t2, t3, t4, t5, t6, t7) -> t1.length() + t2.length() + t3.length() + t4.length() + t5.length() + t6.length() + t7.length();
        HeptaFunction<String, String, String, String, String, String, String, String> joinAllStrings =
                (t1, t2, t3, t4, t5, t6, t7) -> t1 + t2 + t3 + t4 + t5 + t6 + t7;

        Function<Integer, Integer> multiply2 = i -> i * 2;
        Function<String, Integer> stringLength = String::length;
        return Stream.of(
                //@formatter:off
                //            t1,      t2,     t3,     t4,     t5,     t6,      t7,       function,             afterFunction,       expectedException,            expectedResult
                Arguments.of( 0,       0,      0,      0,      0,      0,       0,        null,                 null,                NullPointerException.class,   null ),
                Arguments.of( 3,       3,      3,      3,      3,      -1,      9,        sumAllIntegers,       null,                NullPointerException.class,   null ),
                Arguments.of( 2,       5,      1,      4,      7,      3,       -1,       sumAllIntegers,       multiply2,           null,                         42 ),
                Arguments.of( "A",     "Bb",   "C",    "TT",   "",     "TYH",   "12TI",   sumAllStringLength,   multiply2,           null,                         26 ),
                Arguments.of( "yxT",   "tg",   "cf",   "Y",    "hy",   "B",     "",       joinAllStrings,       stringLength,        null,                         11 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("andThenTestCases")
    @DisplayName("andThen: test cases")
    public <T1, T2, T3, T4, T5, T6, T7, R, Z> void andThen_testCases(T1 t1,
                                                                     T2 t2,
                                                                     T3 t3,
                                                                     T4 t4,
                                                                     T5 t5,
                                                                     T6 t6,
                                                                     T7 t7,
                                                                     HeptaFunction<T1, T2, T3, T4, T5, T6, T7, R> function,
                                                                     Function<? super R, ? extends Z> afterFunction,
                                                                     Class<? extends Exception> expectedException,
                                                                     Z expectedResult) {
        if (null != expectedException) {
            assertThrows(
                    expectedException,
                    () -> function.andThen(afterFunction).apply(t1, t2, t3, t4, t5, t6, t7)
            );
        } else {
            assertEquals(
                    expectedResult,
                    function.andThen(afterFunction).apply(t1, t2, t3, t4, t5, t6, t7)
            );
        }
    }

}
