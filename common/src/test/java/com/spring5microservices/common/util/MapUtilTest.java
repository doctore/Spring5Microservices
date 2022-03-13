package com.spring5microservices.common.util;

import com.spring5microservices.common.dto.PairDto;
import com.spring5microservices.common.interfaces.functional.TriFunction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import static com.spring5microservices.common.util.MapUtil.applyOrElse;
import static com.spring5microservices.common.util.MapUtil.collect;
import static com.spring5microservices.common.util.MapUtil.find;
import static com.spring5microservices.common.util.MapUtil.foldLeft;
import static com.spring5microservices.common.util.MapUtil.groupBy;
import static com.spring5microservices.common.util.MapUtil.partition;
import static com.spring5microservices.common.util.MapUtil.removeKeys;
import static com.spring5microservices.common.util.MapUtil.transform;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MapUtilTest {

    static Stream<Arguments> applyOrElseTestCases() {
        Map<Integer, String> emptyMap = new HashMap<>();
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "A");
            put(2, "B");
            put(3, "C");
            put(4, "o");
        }};

        BiPredicate<Integer, String> isKeyOddAndValueVowel = (k, v) -> k % 2 == 1 && "AEIOUaeiou".contains(v);
        BiFunction<Integer, String, Long> multiply2KeyPlusValueLength = (k, v) -> (long) (k * 2 + v.length());
        BiFunction<Integer, String, Long> sumKeyPlusValueLength = (k, v) -> (long) (k + v.length());

        Map<Integer, Long> emptyMapResult = new HashMap<>();
        Map<Integer, Long> intsAndStringsResult = new HashMap<>() {{
            put(1, 3L);
            put(2, 3L);
            put(3, 4L);
            put(4, 5L);
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,        filterPredicate,         defaultFunction,               orElseFunction,          expectedException,                expectedResult
                Arguments.of( null,             null,                    null,                          null,                    IllegalArgumentException.class,   null ),
                Arguments.of( emptyMap,         null,                    null,                          null,                    IllegalArgumentException.class,   null ),
                Arguments.of( emptyMap,         isKeyOddAndValueVowel,   null,                          null,                    IllegalArgumentException.class,   null ),
                Arguments.of( emptyMap,         isKeyOddAndValueVowel,   multiply2KeyPlusValueLength,   null,                    IllegalArgumentException.class,   null ),
                Arguments.of( null,             isKeyOddAndValueVowel,   multiply2KeyPlusValueLength,   sumKeyPlusValueLength,   null,                             emptyMapResult ),
                Arguments.of( emptyMap,         isKeyOddAndValueVowel,   multiply2KeyPlusValueLength,   sumKeyPlusValueLength,   null,                             emptyMapResult ),
                Arguments.of( intsAndStrings,   isKeyOddAndValueVowel,   multiply2KeyPlusValueLength,   sumKeyPlusValueLength,   null,                             intsAndStringsResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("applyOrElseTestCases")
    @DisplayName("applyOrElse: test cases")
    public <T, E, R> void applyOrElse_testCases(Map<? extends T, ? extends E> sourceMap,
                                                BiPredicate<? super T, ? super E> filterPredicate,
                                                BiFunction<? super T, ? super E, ? extends R> defaultFunction,
                                                BiFunction<? super T, ? super E, ? extends R> orElseFunction,
                                                Class<? extends Exception> expectedException,
                                                Map<T, R> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException,
                    () -> applyOrElse(
                            sourceMap, filterPredicate, defaultFunction, orElseFunction
                    )
            );
        }
        else {
            assertEquals(expectedResult,
                    applyOrElse(
                            sourceMap, filterPredicate, defaultFunction, orElseFunction
                    )
            );
        }
    }


    static Stream<Arguments> collectTestCases() {
        Map<Integer, String> emptyMap = new HashMap<>();
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "A");
            put(2, "B");
            put(4, "o");
        }};

        BiPredicate<Integer, String> isKeyEvenAndValueVowel = (k, v) -> k % 2 == 0 && "AEIOUaeiou".contains(v);
        BiFunction<Integer, String, Long> multiply2KeyPlusValueLength = (k, v) -> (long) (k * 2 + v.length());

        Map<Integer, Long> emptyMapResult = new HashMap<>();
        Map<Integer, Long> intsAndStringsResult = new HashMap<>() {{
            put(4, 9L);
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,        filterPredicate,          mapFunction,                   expectedException,                expectedResult
                Arguments.of( null,             null,                     null,                          IllegalArgumentException.class,   null ),
                Arguments.of( emptyMap,         null,                     null,                          IllegalArgumentException.class,   null ),
                Arguments.of( emptyMap,         null,                     null,                          IllegalArgumentException.class,   null ),
                Arguments.of( emptyMap,         isKeyEvenAndValueVowel,   null,                          IllegalArgumentException.class,   null ),
                Arguments.of( null,             isKeyEvenAndValueVowel,   multiply2KeyPlusValueLength,   null,                             emptyMapResult ),
                Arguments.of( emptyMap,         isKeyEvenAndValueVowel,   multiply2KeyPlusValueLength,   null,                             emptyMapResult ),
                Arguments.of( intsAndStrings,   isKeyEvenAndValueVowel,   multiply2KeyPlusValueLength,   null,                             intsAndStringsResult )
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


    static Stream<Arguments> findTestCases() {
        Map<Integer, String> intsAndStrings = new LinkedHashMap<>() {{
            put(1, "A");
            put(2, "B");
            put(4, "o");
            put(8, "E");
        }};
        Map<String, Long> stringsAndLongs = new TreeMap<>() {{
            put("HY", 23L);
            put("ZW", 62L);
            put("ZZ", 63L);
            put("TZ", 69L);
        }};

        BiPredicate<Integer, String> isKeyEvenAndValueVowel =
                (k, v) ->
                        k % 2 == 0 && "AEIOUaeiou".contains(v);

        BiPredicate<Integer, String> isValueContainsZ =
                (k, v) ->
                        v.toUpperCase().contains("Z");

        BiPredicate<String, Long> isKeyContainsZAndValueIsOdd =
                (k, v) ->
                        k.toUpperCase().contains("Z") && v % 2 == 1;

        return Stream.of(
                //@formatter:off
                //            sourceMap,         filterPredicate,               expectedResult
                Arguments.of( null,              null,                          empty() ),
                Arguments.of( new HashMap<>(),   null,                          empty() ),
                Arguments.of( null,              isKeyEvenAndValueVowel,        empty() ),
                Arguments.of( new HashMap<>(),   isKeyEvenAndValueVowel,        empty() ),
                Arguments.of( intsAndStrings,    isValueContainsZ,              empty() ),
                Arguments.of( intsAndStrings,    isKeyEvenAndValueVowel,        of(PairDto.of(4, "o")) ),
                Arguments.of( stringsAndLongs,   isKeyContainsZAndValueIsOdd,   of(PairDto.of("TZ", 69L)) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findTestCases")
    @DisplayName("find: test cases")
    public <T, E> void find_testCases(Map<? extends T, ? extends E> sourceMap,
                                      BiPredicate<? super T, ? super E> filterPredicate,
                                      Optional<PairDto<T, E>> expectedResult) {
        assertEquals(expectedResult, find(sourceMap, filterPredicate));
    }


    static Stream<Arguments> foldLeftTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "A");
            put(2, "B");
            put(4, "o");
        }};
        Map<Integer, Long> intsAndLongs = new HashMap<>() {{
            put(7, 3L);
            put(11, 6L);
            put(21, 9L);
        }};
        TriFunction<Integer, Integer, String, Integer> multiply = (a, b, c) -> a * b * c.length();
        TriFunction<Long, Integer, Long, Long> sum = (a, b, c) -> a + (long)b + c;
        return Stream.of(
                //@formatter:off
                //            sourceMap,         initialValue,   accumulator,   expectedException,                expectedResult
                Arguments.of( null,              null,           null,          IllegalArgumentException.class,   null ),
                Arguments.of( new HashMap<>(),   2,              null,          null,                             2 ),
                Arguments.of( new HashMap<>(),   1,              multiply,      null,                             1 ),
                Arguments.of( intsAndStrings,    0,              null,          null,                             0 ),
                Arguments.of( intsAndStrings,    1,              multiply,      null,                             8 ),
                Arguments.of( intsAndLongs,      5L,             sum,           null,                             62L )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("foldLeftTestCases")
    @DisplayName("foldLeft: test cases")
    public <T, E, R> void foldLeft_testCases(Map<T, E> sourceMap,
                                             R initialValue,
                                             TriFunction<R, ? super T, ?super E, R> accumulator,
                                             Class<? extends Exception> expectedException,
                                             R expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> foldLeft(sourceMap, initialValue, accumulator));
        }
        else {
            assertEquals(expectedResult, foldLeft(sourceMap, initialValue, accumulator));
        }
    }


    static Stream<Arguments> groupByTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "A");
            put(2, "B");
            put(4, "o");
        }};
        Map<Integer, Long> intsAndLongs = new HashMap<>() {{
            put(7, 3L);
            put(11, 6L);
            put(21, 9L);
        }};

        BiFunction<Integer, String, Integer> isKeyEven = (k, v) -> k % 2;
        BiFunction<Integer, Long, Long> isKeyPlusValueModThree = (k, v) -> (k + v) % 3;

        Map<Integer, Map<Integer, String>> intsAndStringsResult = new HashMap<>() {{
            put(
                    0,
                    new HashMap<>() {{
                        put(2, "B");
                        put(4, "o");
                    }}
            );
            put(
                    1,
                    new HashMap<>() {{
                        put(1, "A");
                    }}
            );
        }};
        Map<Long, Map<Integer, Long>> intsAndLongsResult = new HashMap<>() {{
            put(
                    0L,
                    new HashMap<>() {{
                        put(21, 9L);
                    }}
            );
            put(
                    1L,
                    new HashMap<>() {{
                        put(7, 3L);
                    }}
            );
            put(
                    2L,
                    new HashMap<>() {{
                        put(11, 6L);
                    }}
            );
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,         discriminator,            expectedResult
                Arguments.of( null,              null,                     new HashMap<>() ),
                Arguments.of( null,              isKeyEven,                new HashMap<>() ),
                Arguments.of( new HashMap<>(),   null,                     new HashMap<>() ),
                Arguments.of( new HashMap<>(),   isKeyEven,                new HashMap<>() ),
                Arguments.of( intsAndStrings,    isKeyEven,                intsAndStringsResult ),
                Arguments.of( intsAndLongs,      isKeyPlusValueModThree,   intsAndLongsResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("groupByTestCases")
    @DisplayName("groupBy: test cases")
    public <T, E, R> void groupBy_testCases(Map<T, E> sourceMap,
                                            BiFunction<? super T, ? super E, ? extends R> discriminator,
                                            Map<R, Map<T, E>> expectedResult) {
        assertEquals(expectedResult, groupBy(sourceMap, discriminator));
    }


    static Stream<Arguments> partitionTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "A");
            put(2, "B");
            put(4, "o");
        }};
        Map<Integer, Long> intsAndLongs = new HashMap<>() {{
            put(7, 3L);
            put(11, 6L);
            put(21, 9L);
        }};

        BiPredicate<Integer, String> isKeyEven = (k, v) -> k % 2 == 0;
        BiPredicate<Integer, Long> isKeyPlusValueOdd = (k, v) -> (k + v) % 2 == 1;

        Map<Boolean, Map<Integer, String>> intsAndStringsResult = new HashMap<>() {{
            put(
                    Boolean.TRUE,
                    new HashMap<>() {{
                        put(2, "B");
                        put(4, "o");
                    }}
            );
            put(
                    Boolean.FALSE,
                    new HashMap<>() {{
                        put(1, "A");
                    }}
            );
        }};
        Map<Boolean, Map<Integer, Long>> intsAndLongsResult = new HashMap<>() {{
            put(
                    Boolean.TRUE,
                    new HashMap<>() {{
                        put(11, 6L);
                    }}
            );
            put(
                    Boolean.FALSE,
                    new HashMap<>() {{
                        put(7, 3L);
                        put(21, 9L);
                    }}
            );
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,         discriminator,       expectedResult
                Arguments.of( null,              null,                new HashMap<>() ),
                Arguments.of( null,              isKeyEven,           new HashMap<>() ),
                Arguments.of( new HashMap<>(),   null,                new HashMap<>() ),
                Arguments.of( new HashMap<>(),   isKeyEven,           new HashMap<>() ),
                Arguments.of( intsAndStrings,    isKeyEven,           intsAndStringsResult ),
                Arguments.of( intsAndLongs,      isKeyPlusValueOdd,   intsAndLongsResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("partitionTestCases")
    @DisplayName("partition: test cases")
    public <T, E> void partition_testCases(Map<T, E> sourceMap,
                                           BiPredicate<? super T, ? super E> discriminator,
                                           Map<Boolean, Map<T, E>> expectedResult) {
        assertEquals(expectedResult, partition(sourceMap, discriminator));
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
    public <T, E> void removeKeys_testCases(Map<T, E> sourceMap,
                                            Collection<T> keysToExclude,
                                            HashMap<T, E> expectedResult) {
        assertEquals(expectedResult, removeKeys(sourceMap, keysToExclude));
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
    public <T, U, R> void transform_testCases(Map<T, U> sourceMap,
                                              BiFunction<T, ? super U, R> mapFunction,
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
