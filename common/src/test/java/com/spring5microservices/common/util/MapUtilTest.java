package com.spring5microservices.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import static com.spring5microservices.common.util.MapUtil.collect;
import static com.spring5microservices.common.util.MapUtil.removeKeys;
import static com.spring5microservices.common.util.MapUtil.transform;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MapUtilTest {

    static Stream<Arguments> collectTestCases() {
        Map<Integer, String> emptyMap = new HashMap<>();

        BiPredicate<Integer, String> isKeyEvenAndValueVowel =
                (k, v) ->
                        k % 2 == 0 && "AEIOUaeiou".contains(v);

        BiFunction<Integer, String, Long> fromIntegerStringToLong =
                (i, s) ->
                        (long) (i * 2 + s.length());

        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "A");
            put(2, "B");
            put(4, "o");
        }};
        Map<Integer, Long> intsAndStringsResult = new HashMap<>() {{
            put(4, 9L);
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,        filterPredicate,          mapFunction,               expectedException,                expectedResult
                Arguments.of( null,             null,                     null,                      IllegalArgumentException.class,   null ),
                Arguments.of( emptyMap,         null,                     null,                      IllegalArgumentException.class,   null ),
                Arguments.of( emptyMap,         null,                     null,                      IllegalArgumentException.class,   null ),
                Arguments.of( emptyMap,         isKeyEvenAndValueVowel,   null,                      IllegalArgumentException.class,   null ),
                Arguments.of( null,             isKeyEvenAndValueVowel,   fromIntegerStringToLong,   null,                             emptyMap ),
                Arguments.of( emptyMap,         isKeyEvenAndValueVowel,   fromIntegerStringToLong,   null,                             emptyMap ),
                Arguments.of( intsAndStrings,   isKeyEvenAndValueVowel,   fromIntegerStringToLong,   null,                             intsAndStringsResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("collectTestCases")
    @DisplayName("collect: test cases")
    public <T, E, R> void collect_testCases(Map<? extends T, ? extends E> sourceMap,
                                            BiPredicate<? super T, ? super E> filterPredicate,
                                            BiFunction<? super T, ? super E, ? extends R> mapFunction,
                                            Class<? extends Exception> expectedException,
                                            Map<T, R> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> collect(sourceMap, filterPredicate, mapFunction));
        }
        else {
            assertEquals(expectedResult, collect(sourceMap, filterPredicate, mapFunction));
        }
    }


    static Stream<Arguments> removeKeysTestCases() {
        Map<String, Integer> sourceMap = new HashMap<>() {{
            put("A", 1);
            put("B", 2);
        }};
        Map<String, Integer> sourceMapFiltered = new HashMap<>() {{
            put("A", 1);
        }};
        List<String> keysToExcludeIncluded = asList("B");
        List<String> keysToExcludeNotIncluded = asList("C");
        return Stream.of(
                //@formatter:off
                //            sourceMap,   keysToExclude,              expectedResult
                Arguments.of( null,        null,                       new HashMap<>() ),
                Arguments.of( null,        keysToExcludeIncluded,      new HashMap<>() ),
                Arguments.of( sourceMap,   keysToExcludeNotIncluded,   sourceMap ),
                Arguments.of( sourceMap,   keysToExcludeIncluded,      sourceMapFiltered )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("removeKeysTestCases")
    @DisplayName("removeKeys: test cases")
    public <T, E> void removeKeys_testCases(Map<T, E> sourceMap, Collection<T> keysToExclude,
                                            HashMap<T, E> expectedResult) {
        Map<T, E> filteredMap = removeKeys(sourceMap, keysToExclude);
        assertEquals(expectedResult, filteredMap);
    }


    static Stream<Arguments> transformTestCases() {
        Map<Integer, Integer> integersMap = new HashMap<>() {{
           put(1, 21);
           put(4, 43);
           put(9, 101);
        }};
        BiFunction<Integer, Integer, String> add1ToValueAndConvertToString = (k, v) -> String.valueOf(v + 1);

        Map<Integer, String> integersMapResult = new HashMap<>() {{
            put(1, "22");
            put(4, "44");
            put(9, "102");
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,         mapFunction,                     expectedException,                expectedResult
                Arguments.of( null,              null,                            IllegalArgumentException.class,   null ),
                Arguments.of( new HashMap<>(),   null,                            IllegalArgumentException.class,   null ),
                Arguments.of( new HashMap<>(),   add1ToValueAndConvertToString,   null,                             new HashMap<>() ),
                Arguments.of( integersMap,       add1ToValueAndConvertToString,   null,                             integersMapResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("transformTestCases")
    @DisplayName("transform: test cases")
    public <T, U, R> void transform_testCases(Map<T, U> sourceMap, BiFunction<T, ? super U, R> mapFunction,
                                              Class<? extends Exception> expectedException,
                                              Map<T, R> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> transform(sourceMap, mapFunction));
        }
        else {
            assertEquals(expectedResult, transform(sourceMap, mapFunction));
        }
    }

}
