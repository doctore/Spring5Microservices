package com.spring5microservices.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.spring5microservices.common.util.CollectorsUtil.toMapNullableValues;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CollectorsUtilTest {


    static Stream<Arguments> toMapNullableValuesOnlyMappersTestCases() {
        List<Integer> ints = asList(1, 4, null, null);
        List<String> strings = asList("C", null, "K");

        Function<Integer, Integer> integerMapper = i ->
                null == i
                        ? 0
                        : i + 1;

        Function<String, String> stringMapper = s ->
                null == s
                        ? ""
                        : s + "_2";

        Map<Integer, Integer> expectedResultIntsWithIntegerMapper = new HashMap<>() {{
            put(0, 0);
            put(2, 2);
            put(5, 5);
        }};
        Map<String, String> expectedResultStringsWithStringMapper = new HashMap<>() {{
            put("", "");
            put("C_2", "C_2");
            put("K_2", "K_2");
        }};
        return Stream.of(
                //@formatter:off
                //            originalCollection,   keyMapper,       valueMapper,     expectedException,                expectedResult
                Arguments.of( List.of(),            null,            null,            IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            integerMapper,   null,            IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            null,            integerMapper,   IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            integerMapper,   integerMapper,   null,                             Map.of() ),
                Arguments.of( ints,                 integerMapper,   integerMapper,   null,                             expectedResultIntsWithIntegerMapper ),
                Arguments.of( strings,              stringMapper,    stringMapper,    null,                             expectedResultStringsWithStringMapper )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toMapNullableValuesOnlyMappersTestCases")
    @DisplayName("toMapNullableValues: only with keyMapper and keyMapper test cases")
    public <T, K, U> void toMapNullableValuesOnlyMappers_testCases(Collection<T> originalCollection,
                                                                   Function<? super T, ? extends K> keyMapper,
                                                                   Function<? super T, ? extends U> valueMapper,
                                                                   Class<? extends Exception> expectedException,
                                                                   Map<K, U> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () ->
                    originalCollection.stream()
                            .collect(
                                    toMapNullableValues(
                                            keyMapper,
                                            valueMapper
                                    )
                            )
            );
        } else {
            Map<K, U> result =
                    originalCollection
                            .stream()
                            .collect(
                                    toMapNullableValues(
                                            keyMapper,
                                            valueMapper
                                    )
                            );
            assertEquals(expectedResult, result);
        }
    }


    static Stream<Arguments> toMapNullableValuesWithMappersAndMergeFunctionTestCases() {
        List<Integer> ints = asList(1, 4, null, null, 6);

        Function<Integer, Integer> integerMapper = i ->
                null == i
                        ? 0
                        : i % 3;

        Function<Integer, String> intToStringMapper = i ->
                null == i
                        ? ""
                        : String.valueOf(i);

        BinaryOperator<String> keepsOldValue = (oldValue, newValue) -> oldValue;

        Map<Integer, String> expectedResultIntsDefaultMergeFunction = new HashMap<>() {{
            put(0, "6");
            put(1, "4");
        }};
        Map<Integer, String> expectedResultIntsProvidedMergeFunction = new HashMap<>() {{
            put(0, "");
            put(1, "1");
        }};
        return Stream.of(
                //@formatter:off
                //            originalCollection,   keyMapper,       valueMapper,         mergeFunction,   expectedException,                expectedResult
                Arguments.of( List.of(),            null,            null,                null,            IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            null,            null,                keepsOldValue,   IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            integerMapper,   null,                null,            IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            integerMapper,   null,                keepsOldValue,   IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            null,            intToStringMapper,   null,            IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            null,            intToStringMapper,   keepsOldValue,   IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            integerMapper,   intToStringMapper,   null,            null,                             Map.of() ),
                Arguments.of( List.of(),            integerMapper,   intToStringMapper,   keepsOldValue,   null,                             Map.of() ),
                Arguments.of( ints,                 integerMapper,   intToStringMapper,   null,            null,                             expectedResultIntsDefaultMergeFunction ),
                Arguments.of( ints,                 integerMapper,   intToStringMapper,   keepsOldValue,   null,                             expectedResultIntsProvidedMergeFunction )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toMapNullableValuesWithMappersAndMergeFunctionTestCases")
    @DisplayName("toMapNullableValues: with keyMapper, keyMapper and mergeFunction test cases")
    public <T, K, U> void toMapNullableValuesWithMappersAndMergeFunction_testCases(Collection<T> originalCollection,
                                                                                   Function<? super T, ? extends K> keyMapper,
                                                                                   Function<? super T, ? extends U> valueMapper,
                                                                                   BinaryOperator<U> mergeFunction,
                                                                                   Class<? extends Exception> expectedException,
                                                                                   Map<K, U> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () ->
                    originalCollection.stream()
                            .collect(
                                    toMapNullableValues(
                                            keyMapper,
                                            valueMapper,
                                            mergeFunction
                                    )
                            )
            );
        } else {
            Map<K, U> result =
                    originalCollection
                            .stream()
                            .collect(
                                    toMapNullableValues(
                                            keyMapper,
                                            valueMapper,
                                            mergeFunction
                                    )
                            );
            assertEquals(expectedResult, result);
        }
    }


    static Stream<Arguments> toMapNullableValuesWithMappersAndMapFactoryTestCases() {
        List<Integer> ints = asList(1, 4, null, null, 6);

        Function<Integer, Integer> integerMapper = i ->
                null == i
                        ? 0
                        : i % 3;

        Function<Integer, String> intToStringMapper = i ->
                null == i
                        ? ""
                        : String.valueOf(i);

        Supplier<Map<String, Integer>> linkedMapSupplier = LinkedHashMap::new;

        Map<Integer, String> expectedResultIntsDefaultSupplier = new HashMap<>() {{
            put(0, "6");
            put(1, "4");
        }};
        Map<Integer, String> expectedResultIntsProvidedSupplier = new LinkedHashMap<>() {{
            put(0, "6");
            put(1, "4");
        }};
        return Stream.of(
                //@formatter:off
                //            originalCollection,   keyMapper,       valueMapper,         mapFactory,          expectedException,                expectedResult
                Arguments.of( List.of(),            null,            null,                null,                IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            null,            null,                linkedMapSupplier,   IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            integerMapper,   null,                null,                IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            integerMapper,   null,                linkedMapSupplier,   IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            null,            intToStringMapper,   null,                IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            null,            intToStringMapper,   linkedMapSupplier,   IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            integerMapper,   intToStringMapper,   null,                null,                             Map.of() ),
                Arguments.of( List.of(),            integerMapper,   intToStringMapper,   linkedMapSupplier,   null,                             Map.of() ),
                Arguments.of( ints,                 integerMapper,   intToStringMapper,   null,                null,                             expectedResultIntsDefaultSupplier ),
                Arguments.of( ints,                 integerMapper,   intToStringMapper,   linkedMapSupplier,   null,                             expectedResultIntsProvidedSupplier )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toMapNullableValuesWithMappersAndMapFactoryTestCases")
    @DisplayName("toMapNullableValues: with keyMapper, keyMapper and mapFactory test cases")
    public <T, K, U> void toMapNullableValuesWithMappersAndMapFactory_testCases(Collection<T> originalCollection,
                                                                                Function<? super T, ? extends K> keyMapper,
                                                                                Function<? super T, ? extends U> valueMapper,
                                                                                Supplier<Map<K, U>> mapFactory,
                                                                                Class<? extends Exception> expectedException,
                                                                                Map<K, U> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () ->
                    originalCollection.stream()
                            .collect(
                                    toMapNullableValues(
                                            keyMapper,
                                            valueMapper,
                                            mapFactory
                                    )
                            )
            );
        } else {
            Map<K, U> result =
                    originalCollection
                            .stream()
                            .collect(
                                    toMapNullableValues(
                                            keyMapper,
                                            valueMapper,
                                            mapFactory
                                    )
                            );
            assertEquals(expectedResult, result);
        }
    }


    static Stream<Arguments> toMapNullableValuesAllParametersTestCases() {
        List<Integer> ints = asList(7, 10, null, null, 9);

        Function<Integer, Integer> integerMapper = i ->
                null == i
                        ? 0
                        : i % 3;

        Function<Integer, String> intToStringMapper = i ->
                null == i
                        ? ""
                        : String.valueOf(i);

        BinaryOperator<String> keepsOldValue = (oldValue, newValue) -> oldValue;
        Supplier<Map<String, Integer>> linkedMapSupplier = LinkedHashMap::new;

        Map<Integer, String> expectedResultIntsDefaultMergeFunctionAndSupplier = new HashMap<>() {{
            put(0, "9");
            put(1, "10");
        }};
        Map<Integer, String> expectedResultIntsProvidedMergeFunctionAndDefaultSupplier = new HashMap<>() {{
            put(0, "");
            put(1, "7");
        }};
        Map<Integer, String> expectedResultIntsProvidedMergeFunctionAndSupplier = new LinkedHashMap<>() {{
            put(0, "");
            put(1, "7");
        }};
        return Stream.of(
                //@formatter:off
                //            originalCollection,   keyMapper,       valueMapper,         mergeFunction,   mapFactory,          expectedException,                expectedResult
                Arguments.of( List.of(),            null,            null,                null,            null,                IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            integerMapper,   null,                null,            null,                IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            integerMapper,   null,                keepsOldValue,   null,                IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            integerMapper,   null,                keepsOldValue,   linkedMapSupplier,   IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            null,            intToStringMapper,   null,            null,                IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            null,            intToStringMapper,   keepsOldValue,   null,                IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            null,            intToStringMapper,   keepsOldValue,   linkedMapSupplier,   IllegalArgumentException.class,   null ),
                Arguments.of( List.of(),            integerMapper,   intToStringMapper,   null,            null,                null,                             Map.of() ),
                Arguments.of( List.of(),            integerMapper,   intToStringMapper,   keepsOldValue,   null,                null,                             Map.of() ),
                Arguments.of( List.of(),            integerMapper,   intToStringMapper,   keepsOldValue,   linkedMapSupplier,   null,                             Map.of() ),
                Arguments.of( ints,                 integerMapper,   intToStringMapper,   null,            null,                null,                             expectedResultIntsDefaultMergeFunctionAndSupplier ),
                Arguments.of( ints,                 integerMapper,   intToStringMapper,   keepsOldValue,   null,                null,                             expectedResultIntsProvidedMergeFunctionAndDefaultSupplier ),
                Arguments.of( ints,                 integerMapper,   intToStringMapper,   keepsOldValue,   linkedMapSupplier,   null,                             expectedResultIntsProvidedMergeFunctionAndSupplier )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toMapNullableValuesAllParametersTestCases")
    @DisplayName("toMapNullableValues: with all parameters test cases")
    public <T, K, U> void toMapNullableValuesAllParameters_testCases(Collection<T> originalCollection,
                                                                     Function<? super T, ? extends K> keyMapper,
                                                                     Function<? super T, ? extends U> valueMapper,
                                                                     BinaryOperator<U> mergeFunction,
                                                                     Supplier<Map<K, U>> mapFactory,
                                                                     Class<? extends Exception> expectedException,
                                                                     Map<K, U> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () ->
                    originalCollection.stream()
                            .collect(
                                    toMapNullableValues(
                                            keyMapper,
                                            valueMapper,
                                            mergeFunction,
                                            mapFactory
                                    )
                            )
            );
        } else {
            Map<K, U> result =
                    originalCollection
                            .stream()
                            .collect(
                                    toMapNullableValues(
                                            keyMapper,
                                            valueMapper,
                                            mergeFunction,
                                            mapFactory
                                    )
                            );
            assertEquals(expectedResult, result);
        }
    }

}
