package com.spring5microservices.common.util;

import com.spring5microservices.common.collection.tuple.Tuple2;
import com.spring5microservices.common.interfaces.function.PartialFunction;
import com.spring5microservices.common.interfaces.function.TriFunction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.spring5microservices.common.util.ComparatorUtil.safeNaturalOrderNullLast;
import static com.spring5microservices.common.util.MapUtil.*;
import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MapUtilTest {


    static Stream<Arguments> andThenNoMapFactoryTestCases() {
        Map<Integer, Integer> integersMap = new HashMap<>() {{
            put(3, 45);
            put(6, null);
            put(11, 67);
        }};
        BiFunction<Integer, Integer, Map.Entry<String, Integer>> add1ToStringAndMultiply2 =
                (k, v) ->
                        new AbstractMap.SimpleEntry<>(
                                String.valueOf(k + 1),
                                null == v
                                        ? null
                                        : v * 2
                        );
        BiFunction<String, Integer, Map.Entry<String, String>> lengthAndToString =
                (k, v) ->
                        new AbstractMap.SimpleEntry<>(
                                String.valueOf(k.length()),
                                null == v
                                        ? null
                                        : v.toString()
                        );
        BiFunction<String, Integer, Map.Entry<String, Integer>> stringIntegerIdentity =
                AbstractMap.SimpleEntry::new;
        Map<String, Integer> expectedApplyAdd1ToStringAndMultiply2AndIdentityResult = new HashMap<>() {{
            put("4", 90);
            put("7", null);
            put("12", 134);
        }};
        Map<String, String> expectedApplyAdd1ToStringAndMultiply2AndLengthAndToStringResult = new HashMap<>() {{
            put("1", null);
            put("2", "134");
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,     firstMapper,                secondMapper             expectedException,                expectedResult
                Arguments.of( null,          null,                       null,                    null,                             Map.of() ),
                Arguments.of( null,          add1ToStringAndMultiply2,   null,                    null,                             Map.of() ),
                Arguments.of( null,          null,                       stringIntegerIdentity,   null,                             Map.of() ),
                Arguments.of( null,          add1ToStringAndMultiply2,   stringIntegerIdentity,   null,                             Map.of() ),
                Arguments.of( Map.of(),      null,                       null,                    null,                             Map.of() ),
                Arguments.of( Map.of(),      add1ToStringAndMultiply2,   null,                    null,                             Map.of() ),
                Arguments.of( Map.of(),      null,                       stringIntegerIdentity,   null,                             Map.of() ),
                Arguments.of( Map.of(),      add1ToStringAndMultiply2,   stringIntegerIdentity,   null,                             Map.of() ),
                Arguments.of( integersMap,   null,                       null,                    IllegalArgumentException.class,   null ),
                Arguments.of( integersMap,   add1ToStringAndMultiply2,   null,                    IllegalArgumentException.class,   null ),
                Arguments.of( integersMap,   null,                       stringIntegerIdentity,   IllegalArgumentException.class,   null ),
                Arguments.of( integersMap,   add1ToStringAndMultiply2,   stringIntegerIdentity,   null,                             expectedApplyAdd1ToStringAndMultiply2AndIdentityResult ),
                Arguments.of( integersMap,   add1ToStringAndMultiply2,   lengthAndToString,       null,                             expectedApplyAdd1ToStringAndMultiply2AndLengthAndToStringResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("andThenNoMapFactoryTestCases")
    @DisplayName("andThen: without map factory test cases")
    public <K1, V1, K2, V2, T, R> void andThenNoMapFactory_testCases(Map<? extends K1, ? extends V1> sourceMap,
                                                                     BiFunction<? super K1, ? super V1, Map.Entry<? extends K2, ? extends V2>> firstMapper,
                                                                     BiFunction<? super K2, ? super V2, Map.Entry<? extends T, ? extends R>> secondMapper,
                                                                     Class<? extends Exception> expectedException,
                                                                     Map<T, T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException,
                    () ->
                            andThen(
                                    sourceMap, firstMapper, secondMapper
                            )
            );
        } else {
            assertEquals(expectedResult,
                    andThen(
                            sourceMap, firstMapper, secondMapper
                    )
            );
        }
    }


    static Stream<Arguments> andThenAllParametersTestCases() {
        Map<Integer, Integer> integersMap = new HashMap<>() {{
            put(3, 45);
            put(6, null);
            put(11, 67);
        }};
        BiFunction<Integer, Integer, Map.Entry<String, Integer>> add1ToStringAndMultiply2 =
                (k, v) ->
                        new AbstractMap.SimpleEntry<>(
                                String.valueOf(k + 1),
                                null == v
                                        ? null
                                        : v * 2
                        );
        BiFunction<String, Integer, Map.Entry<String, String>> lengthAndToString =
                (k, v) ->
                        new AbstractMap.SimpleEntry<>(
                                String.valueOf(k.length()),
                                null == v
                                        ? null
                                        : v.toString()
                        );
        BiFunction<String, Integer, Map.Entry<String, Integer>> stringIntegerIdentity =
                AbstractMap.SimpleEntry::new;

        Supplier<Map<Integer, Long>> linkedMapSupplier = LinkedHashMap::new;

        Map<String, Integer> expectedApplyAdd1ToStringAndMultiply2AndIdentityResult = new HashMap<>() {{
            put("4", 90);
            put("7", null);
            put("12", 134);
        }};
        Map<String, Integer> expectedApplyAdd1ToStringAndMultiply2AndIdentityResultLinked = new LinkedHashMap<>() {{
            put("4", 90);
            put("7", null);
            put("12", 134);
        }};
        Map<String, String> expectedApplyAdd1ToStringAndMultiply2AndLengthAndToStringResult = new HashMap<>() {{
            put("1", null);
            put("2", "134");
        }};
        Map<String, String> expectedApplyAdd1ToStringAndMultiply2AndLengthAndToStringResultLinked = new LinkedHashMap<>() {{
            put("1", null);
            put("2", "134");
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,     firstMapper,                secondMapper             mapFactory,          expectedException,                expectedResult
                Arguments.of( null,          null,                       null,                    null,                null,                             Map.of() ),
                Arguments.of( null,          null,                       null,                    linkedMapSupplier,   null,                             Map.of() ),
                Arguments.of( null,          add1ToStringAndMultiply2,   null,                    null,                null,                             Map.of() ),
                Arguments.of( null,          add1ToStringAndMultiply2,   null,                    linkedMapSupplier,   null,                             Map.of() ),
                Arguments.of( null,          null,                       stringIntegerIdentity,   null,                null,                             Map.of() ),
                Arguments.of( null,          null,                       stringIntegerIdentity,   linkedMapSupplier,   null,                             Map.of() ),
                Arguments.of( null,          add1ToStringAndMultiply2,   stringIntegerIdentity,   null,                null,                             Map.of() ),
                Arguments.of( null,          add1ToStringAndMultiply2,   stringIntegerIdentity,   linkedMapSupplier,   null,                             Map.of() ),
                Arguments.of( Map.of(),      null,                       null,                    null,                null,                             Map.of() ),
                Arguments.of( Map.of(),      null,                       null,                    linkedMapSupplier,   null,                             Map.of() ),
                Arguments.of( Map.of(),      add1ToStringAndMultiply2,   null,                    null,                null,                             Map.of() ),
                Arguments.of( Map.of(),      add1ToStringAndMultiply2,   null,                    linkedMapSupplier,   null,                             Map.of() ),
                Arguments.of( Map.of(),      null,                       stringIntegerIdentity,   null,                null,                             Map.of() ),
                Arguments.of( Map.of(),      null,                       stringIntegerIdentity,   linkedMapSupplier,   null,                             Map.of() ),
                Arguments.of( Map.of(),      add1ToStringAndMultiply2,   stringIntegerIdentity,   null,                null,                             Map.of() ),
                Arguments.of( Map.of(),      add1ToStringAndMultiply2,   stringIntegerIdentity,   linkedMapSupplier,   null,                             Map.of() ),
                Arguments.of( integersMap,   null,                       null,                    null,                IllegalArgumentException.class,   null ),
                Arguments.of( integersMap,   null,                       null,                    linkedMapSupplier,   IllegalArgumentException.class,   null ),
                Arguments.of( integersMap,   add1ToStringAndMultiply2,   null,                    null,                IllegalArgumentException.class,   null ),
                Arguments.of( integersMap,   add1ToStringAndMultiply2,   null,                    linkedMapSupplier,   IllegalArgumentException.class,   null ),
                Arguments.of( integersMap,   null,                       stringIntegerIdentity,   null,                IllegalArgumentException.class,   null ),
                Arguments.of( integersMap,   null,                       stringIntegerIdentity,   linkedMapSupplier,   IllegalArgumentException.class,   null ),
                Arguments.of( integersMap,   add1ToStringAndMultiply2,   stringIntegerIdentity,   null,                null,                             expectedApplyAdd1ToStringAndMultiply2AndIdentityResult ),
                Arguments.of( integersMap,   add1ToStringAndMultiply2,   stringIntegerIdentity,   linkedMapSupplier,   null,                             expectedApplyAdd1ToStringAndMultiply2AndIdentityResultLinked ),
                Arguments.of( integersMap,   add1ToStringAndMultiply2,   lengthAndToString,       null,                null,                             expectedApplyAdd1ToStringAndMultiply2AndLengthAndToStringResult ),
                Arguments.of( integersMap,   add1ToStringAndMultiply2,   lengthAndToString,       linkedMapSupplier,   null,                             expectedApplyAdd1ToStringAndMultiply2AndLengthAndToStringResultLinked )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("andThenAllParametersTestCases")
    @DisplayName("andThen: with all parameters test cases")
    public <K1, V1, K2, V2, T, R> void andThenAllParameters_testCases(Map<? extends K1, ? extends V1> sourceMap,
                                                                      BiFunction<? super K1, ? super V1, Map.Entry<? extends K2, ? extends V2>> firstMapper,
                                                                      BiFunction<? super K2, ? super V2, Map.Entry<? extends T, ? extends R>> secondMapper,
                                                                      Supplier<Map<T, R>> mapFactory,
                                                                      Class<? extends Exception> expectedException,
                                                                      Map<T, T> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException,
                    () ->
                            andThen(
                                    sourceMap, firstMapper, secondMapper, mapFactory
                            )
            );
        } else {
            assertEquals(expectedResult,
                    andThen(
                            sourceMap, firstMapper, secondMapper, mapFactory
                    )
            );
        }
    }


    static Stream<Arguments> applyOrElseWithBiPredicateAndBiFunctionsTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "A");
            put(2, "B");
            put(3, "C");
            put(4, "o");
        }};
        BiPredicate<Integer, String> isKeyOddAndValueVowel = (k, v) -> k % 2 == 1 && "AEIOUaeiou".contains(v);
        BiFunction<Integer, String, Map.Entry<Integer, Long>> multiply2KeyPlusValueLength =
                (k, v) ->
                        new AbstractMap.SimpleEntry<>(
                                k,
                                (long) (
                                        k * 2 +
                                                (
                                                        null == v
                                                                ? 0
                                                                : v.length()
                                                )
                                )
                        );
        BiFunction<Integer, String, Map.Entry<Integer, Long>> sumKeyPlusValueLength =
                (k, v) ->
                        new AbstractMap.SimpleEntry<>(
                                k,
                                (long) (k +
                                          (
                                                null == v
                                                        ? 0
                                                        : v.length()
                                          )
                                )
                        );
        Map<Integer, Long> intsAndStringsNoFilterResult = new HashMap<>() {{
            put(1, 3L);
            put(2, 5L);
            put(3, 7L);
            put(4, 9L);
        }};
        Map<Integer, Long> intsAndStringsResult = new HashMap<>() {{
            put(1, 3L);
            put(2, 3L);
            put(3, 4L);
            put(4, 5L);
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,        filterPredicate,         defaultMapper,                 orElseMapper,            expectedException,                expectedResult
                Arguments.of( null,             null,                    null,                          null,                    null,                             Map.of() ),
                Arguments.of( null,             isKeyOddAndValueVowel,   null,                          null,                    null,                             Map.of() ),
                Arguments.of( null,             isKeyOddAndValueVowel,   multiply2KeyPlusValueLength,   null,                    null,                             Map.of() ),
                Arguments.of( null,             isKeyOddAndValueVowel,   null,                          sumKeyPlusValueLength,   null,                             Map.of() ),
                Arguments.of( null,             isKeyOddAndValueVowel,   multiply2KeyPlusValueLength,   sumKeyPlusValueLength,   null,                             Map.of() ),
                Arguments.of( Map.of(),         null,                    null,                          null,                    null,                             Map.of() ),
                Arguments.of( Map.of(),         isKeyOddAndValueVowel,   null,                          null,                    null,                             Map.of() ),
                Arguments.of( Map.of(),         isKeyOddAndValueVowel,   multiply2KeyPlusValueLength,   null,                    null,                             Map.of() ),
                Arguments.of( Map.of(),         isKeyOddAndValueVowel,   null,                          sumKeyPlusValueLength,   null,                             Map.of() ),
                Arguments.of( Map.of(),         isKeyOddAndValueVowel,   multiply2KeyPlusValueLength,   sumKeyPlusValueLength,   null,                             Map.of() ),
                Arguments.of( intsAndStrings,   null,                    null,                          null,                    IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   isKeyOddAndValueVowel,   null,                          null,                    IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   isKeyOddAndValueVowel,   multiply2KeyPlusValueLength,   null,                    IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   isKeyOddAndValueVowel,   null,                          sumKeyPlusValueLength,   IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   null,                    multiply2KeyPlusValueLength,   sumKeyPlusValueLength,   null,                             intsAndStringsNoFilterResult ),
                Arguments.of( intsAndStrings,   isKeyOddAndValueVowel,   multiply2KeyPlusValueLength,   sumKeyPlusValueLength,   null,                             intsAndStringsResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("applyOrElseWithBiPredicateAndBiFunctionsTestCases")
    @DisplayName("applyOrElse: with BiPredicate and BiFunctions test cases")
    public <K1, K2, V1, V2> void applyOrElseWithBiPredicateAndBiFunctions_testCases(Map<? extends K1, ? extends V1> sourceMap,
                                                                                    BiPredicate<? super K1, ? super V1> filterPredicate,
                                                                                    BiFunction<? super K1, ? super V1, ? extends Map.Entry<K2, V2>> defaultMapper,
                                                                                    BiFunction<? super K1, ? super V1, ? extends Map.Entry<K2, V2>> orElseMapper,
                                                                                    Class<? extends Exception> expectedException,
                                                                                    Map<K2, V2> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException,
                    () ->
                            applyOrElse(
                                    sourceMap, filterPredicate, defaultMapper, orElseMapper
                            )
            );
        } else {
            assertEquals(expectedResult,
                    applyOrElse(
                            sourceMap, filterPredicate, defaultMapper, orElseMapper
                    )
            );
        }
    }


    static Stream<Arguments> applyOrElseWithBiPredicateBiFunctionsAndSupplierTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "A");
            put(2, "B");
            put(3, null);
        }};
        BiPredicate<Integer, String> isKeyOddAndValueVowel = (k, v) ->
                k % 2 == 1 &&
                (
                    null != v &&
                    "AEIOUaeiou".contains(v)
                );
        BiFunction<Integer, String, Map.Entry<Integer, Long>> multiply2KeyPlusValueLength =
                (k, v) ->
                        new AbstractMap.SimpleEntry<>(
                                k,
                                (long) (
                                        k * 2 +
                                                (
                                                        null == v
                                                                ? 0
                                                                : v.length()
                                                )
                                )
                        );
        BiFunction<Integer, String, Map.Entry<Integer, Long>> sumKeyPlusValueLength =
                (k, v) ->
                        new AbstractMap.SimpleEntry<>(
                                k,
                                (long) (k +
                                          (
                                                null == v
                                                        ? 0
                                                        : v.length()
                                          )
                                )
                        );
        Supplier<Map<Integer, Long>> linkedMapSupplier = LinkedHashMap::new;

        Map<Integer, Long> intsAndStringsNoFilterResult = new HashMap<>() {{
            put(1, 3L);
            put(2, 5L);
            put(3, 6L);
        }};
        Map<Integer, Long> intsAndStringsResult = new HashMap<>() {{
            put(1, 3L);
            put(2, 3L);
            put(3, 3L);
        }};
        LinkedHashMap<Integer, Long> intsAndStringsLinkedMapResult = new LinkedHashMap<>(intsAndStringsResult);
        return Stream.of(
                //@formatter:off
                //            sourceMap,        filterPredicate,         defaultMapper,                 orElseMapper,            mapFactory,          expectedException,                expectedResult
                Arguments.of( null,             null,                    null,                          null,                    null,                null,                             Map.of() ),
                Arguments.of( null,             null,                    null,                          null,                    linkedMapSupplier,   null,                             Map.of() ),
                Arguments.of( null,             isKeyOddAndValueVowel,   null,                          null,                    null,                null,                             Map.of() ),
                Arguments.of( null,             isKeyOddAndValueVowel,   null,                          null,                    linkedMapSupplier,   null,                             Map.of() ),
                Arguments.of( null,             isKeyOddAndValueVowel,   multiply2KeyPlusValueLength,   null,                    null,                null,                             Map.of() ),
                Arguments.of( null,             isKeyOddAndValueVowel,   multiply2KeyPlusValueLength,   null,                    linkedMapSupplier,   null,                             Map.of() ),
                Arguments.of( null,             isKeyOddAndValueVowel,   null,                          sumKeyPlusValueLength,   null,                null,                             Map.of() ),
                Arguments.of( null,             isKeyOddAndValueVowel,   null,                          sumKeyPlusValueLength,   linkedMapSupplier,   null,                             Map.of() ),
                Arguments.of( null,             isKeyOddAndValueVowel,   multiply2KeyPlusValueLength,   sumKeyPlusValueLength,   null,                null,                             Map.of() ),
                Arguments.of( null,             isKeyOddAndValueVowel,   multiply2KeyPlusValueLength,   sumKeyPlusValueLength,   linkedMapSupplier,   null,                             Map.of() ),
                Arguments.of( Map.of(),         null,                    null,                          null,                    null,                null,                             Map.of() ),
                Arguments.of( Map.of(),         null,                    null,                          null,                    linkedMapSupplier,   null,                             Map.of() ),
                Arguments.of( Map.of(),         isKeyOddAndValueVowel,   null,                          null,                    null,                null,                             Map.of() ),
                Arguments.of( Map.of(),         isKeyOddAndValueVowel,   null,                          null,                    linkedMapSupplier,   null,                             Map.of() ),
                Arguments.of( Map.of(),         isKeyOddAndValueVowel,   multiply2KeyPlusValueLength,   null,                    null,                null,                             Map.of() ),
                Arguments.of( Map.of(),         isKeyOddAndValueVowel,   multiply2KeyPlusValueLength,   null,                    linkedMapSupplier,   null,                             Map.of() ),
                Arguments.of( Map.of(),         isKeyOddAndValueVowel,   null,                          sumKeyPlusValueLength,   null,                null,                             Map.of() ),
                Arguments.of( Map.of(),         isKeyOddAndValueVowel,   null,                          sumKeyPlusValueLength,   linkedMapSupplier,   null,                             Map.of() ),
                Arguments.of( Map.of(),         isKeyOddAndValueVowel,   multiply2KeyPlusValueLength,   sumKeyPlusValueLength,   null,                null,                             Map.of() ),
                Arguments.of( Map.of(),         isKeyOddAndValueVowel,   multiply2KeyPlusValueLength,   sumKeyPlusValueLength,   linkedMapSupplier,   null,                             Map.of() ),
                Arguments.of( intsAndStrings,   null,                    null,                          null,                    null,                IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   null,                    null,                          null,                    linkedMapSupplier,   IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   isKeyOddAndValueVowel,   null,                          null,                    null,                IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   isKeyOddAndValueVowel,   null,                          null,                    linkedMapSupplier,   IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   isKeyOddAndValueVowel,   multiply2KeyPlusValueLength,   null,                    null,                IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   isKeyOddAndValueVowel,   multiply2KeyPlusValueLength,   null,                    linkedMapSupplier,   IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   isKeyOddAndValueVowel,   null,                          sumKeyPlusValueLength,   null,                IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   isKeyOddAndValueVowel,   null,                          sumKeyPlusValueLength,   linkedMapSupplier,   IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   null,                    multiply2KeyPlusValueLength,   sumKeyPlusValueLength,   null,                null,                             intsAndStringsNoFilterResult ),
                Arguments.of( intsAndStrings,   isKeyOddAndValueVowel,   multiply2KeyPlusValueLength,   sumKeyPlusValueLength,   null,                null,                             intsAndStringsResult ),
                Arguments.of( intsAndStrings,   isKeyOddAndValueVowel,   multiply2KeyPlusValueLength,   sumKeyPlusValueLength,   linkedMapSupplier,   null,                             intsAndStringsLinkedMapResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("applyOrElseWithBiPredicateBiFunctionsAndSupplierTestCases")
    @DisplayName("applyOrElse: with BiPredicate, BiFunctions and Supplier test cases")
    public <K1, K2, V1, V2> void applyOrElseWithBiPredicateBiFunctionsAndSupplier_testCases(Map<? extends K1, ? extends V1> sourceMap,
                                                                                            BiPredicate<? super K1, ? super V1> filterPredicate,
                                                                                            BiFunction<? super K1, ? super V1, ? extends Map.Entry<K2, V2>> defaultMapper,
                                                                                            BiFunction<? super K1, ? super V1, ? extends Map.Entry<K2, V2>> orElseMapper,
                                                                                            Supplier<Map<K2, V2>> mapFactory,
                                                                                            Class<? extends Exception> expectedException,
                                                                                            Map<K2, V2> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException,
                    () ->
                            applyOrElse(
                                    sourceMap, filterPredicate, defaultMapper, orElseMapper, mapFactory

                            )
            );
        } else {
            assertEquals(expectedResult,
                    applyOrElse(
                            sourceMap, filterPredicate, defaultMapper, orElseMapper, mapFactory
                    )
            );
        }
    }


    static Stream<Arguments> applyOrElseWithPartialFunctionAndBiFunctionTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "A");
            put(2, "B");
            put(3, null);
            put(4, "o");
        }};
        PartialFunction<Map.Entry<Integer, String>, Map.Entry<Integer, Long>> multiply2KeyPlusValueLength = PartialFunction.of(
                e -> null != e &&
                        null != e.getValue() &&
                        1 == e.getKey() % 2 &&
                        "AEIOUaeiou".contains(e.getValue()),
                e -> null == e
                        ? null
                        : new AbstractMap.SimpleEntry<>(
                                e.getKey(),
                                null == e.getValue()
                                        ? 0L
                                        : (long) (e.getKey() * 2 + e.getValue().length())
                          )
        );
        BiFunction<Integer, String, Map.Entry<Integer, Long>> sumKeyPlusValueLength =
                (k, v) ->
                        new AbstractMap.SimpleEntry<>(
                                k,
                                (long) (k +
                                          (
                                                  null == v
                                                          ? 0
                                                          : v.length()
                                          )
                                )
                        );
        Map<Integer, Long> intsAndStringsResult = new HashMap<>() {{
            put(1, 3L);
            put(2, 3L);
            put(3, 3L);
            put(4, 5L);
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,        partialFunction,               orElseMapper,            expectedException,                expectedResult
                Arguments.of( null,             null,                          null,                    null,                             Map.of() ),
                Arguments.of( null,             null,                          sumKeyPlusValueLength,   null,                             Map.of() ),
                Arguments.of( null,             multiply2KeyPlusValueLength,   sumKeyPlusValueLength,   null,                             Map.of() ),
                Arguments.of( Map.of(),         null,                          null,                    null,                             Map.of() ),
                Arguments.of( Map.of(),         null,                          sumKeyPlusValueLength,   null,                             Map.of() ),
                Arguments.of( Map.of(),         multiply2KeyPlusValueLength,   sumKeyPlusValueLength,   null,                             Map.of() ),
                Arguments.of( intsAndStrings,   null,                          null,                    IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   null,                          sumKeyPlusValueLength,   IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   multiply2KeyPlusValueLength,   null,                    IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   multiply2KeyPlusValueLength,   sumKeyPlusValueLength,   null,                             intsAndStringsResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("applyOrElseWithPartialFunctionAndBiFunctionTestCases")
    @DisplayName("applyOrElse: with PartialFunction and BiFunction test cases")
    public <K1, K2, V1, V2> void applyOrElseWithPartialFunctionAndBiFunction_testCases(Map<? extends K1, ? extends V1> sourceMap,
                                                                                       PartialFunction<? super Map.Entry<K1, V1>, ? extends Map.Entry<K2, V2>> partialFunction,
                                                                                       BiFunction<? super K1, ? super V1, ? extends Map.Entry<K2, V2>> orElseMapper,
                                                                                       Class<? extends Exception> expectedException,
                                                                                       Map<K2, V2> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException,
                    () ->
                            applyOrElse(
                                    sourceMap, partialFunction, orElseMapper
                            )
            );
        } else {
            assertEquals(expectedResult,
                    applyOrElse(
                            sourceMap, partialFunction, orElseMapper
                    )
            );
        }
    }


    static Stream<Arguments> applyOrElseWithPartialFunctionBiFunctionAndSupplierTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "A");
            put(2, "B");
            put(3, null);
            put(4, "o");
        }};
        PartialFunction<Map.Entry<Integer, String>, Map.Entry<Integer, Long>> multiply2KeyPlusValueLength = PartialFunction.of(
                e -> null != e &&
                        null != e.getValue() &&
                        1 == e.getKey() % 2 &&
                        "AEIOUaeiou".contains(e.getValue()),
                e -> null == e
                        ? null
                        : new AbstractMap.SimpleEntry<>(
                                e.getKey(),
                                null == e.getValue()
                                        ? 0L
                                        : (long) (e.getKey() * 2 + e.getValue().length())
                          )
        );
        BiFunction<Integer, String, Map.Entry<Integer, Long>> sumKeyPlusValueLength =
                (k, v) ->
                        new AbstractMap.SimpleEntry<>(
                                k,
                                (long) (k +
                                        (
                                                null == v
                                                        ? 0
                                                        : v.length()
                                        )
                                )
                        );
        Supplier<Map<Integer, Long>> linkedMapSupplier = LinkedHashMap::new;
        Map<Integer, Long> intsAndStringsResult = new HashMap<>() {{
            put(1, 3L);
            put(2, 3L);
            put(3, 3L);
            put(4, 5L);
        }};
        LinkedHashMap<Integer, Long> intsAndStringLinkedMapResult = new LinkedHashMap<>(intsAndStringsResult);
        return Stream.of(
                //@formatter:off
                //            sourceMap,        partialFunction,               orElseMapper,            mapFactory,          expectedException,                expectedResult
                Arguments.of( null,             null,                          null,                    null,                null,                             Map.of() ),
                Arguments.of( null,             null,                          null,                    linkedMapSupplier,   null,                             Map.of() ),
                Arguments.of( null,             null,                          sumKeyPlusValueLength,   null,                null,                             Map.of() ),
                Arguments.of( null,             null,                          sumKeyPlusValueLength,   linkedMapSupplier,   null,                             Map.of() ),
                Arguments.of( null,             multiply2KeyPlusValueLength,   sumKeyPlusValueLength,   null,                null,                             Map.of() ),
                Arguments.of( null,             multiply2KeyPlusValueLength,   sumKeyPlusValueLength,   linkedMapSupplier,   null,                             Map.of() ),
                Arguments.of( Map.of(),         null,                          null,                    null,                null,                             Map.of() ),
                Arguments.of( Map.of(),         null,                          null,                    linkedMapSupplier,   null,                             Map.of() ),
                Arguments.of( Map.of(),         null,                          sumKeyPlusValueLength,   null,                null,                             Map.of() ),
                Arguments.of( Map.of(),         null,                          sumKeyPlusValueLength,   linkedMapSupplier,   null,                             Map.of() ),
                Arguments.of( Map.of(),         multiply2KeyPlusValueLength,   sumKeyPlusValueLength,   null,                null,                             Map.of() ),
                Arguments.of( Map.of(),         multiply2KeyPlusValueLength,   sumKeyPlusValueLength,   linkedMapSupplier,   null,                             Map.of() ),
                Arguments.of( intsAndStrings,   null,                          null,                    null,                IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   null,                          null,                    linkedMapSupplier,   IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   null,                          sumKeyPlusValueLength,   null,                IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   null,                          sumKeyPlusValueLength,   linkedMapSupplier,   IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   multiply2KeyPlusValueLength,   null,                    null,                IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   multiply2KeyPlusValueLength,   null,                    linkedMapSupplier,   IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   multiply2KeyPlusValueLength,   sumKeyPlusValueLength,   null,                null,                             intsAndStringsResult ),
                Arguments.of( intsAndStrings,   multiply2KeyPlusValueLength,   sumKeyPlusValueLength,   linkedMapSupplier,   null,                             intsAndStringLinkedMapResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("applyOrElseWithPartialFunctionBiFunctionAndSupplierTestCases")
    @DisplayName("applyOrElse: with PartialFunction, BiFunction and Supplier test cases")
    public <K1, K2, V1, V2> void applyOrElseWithPartialFunctionBiFunctionAndSupplier_testCases(Map<? extends K1, ? extends V1> sourceMap,
                                                                                               PartialFunction<? super Map.Entry<K1, V1>, ? extends Map.Entry<K2, V2>> partialFunction,
                                                                                               BiFunction<? super K1, ? super V1, ? extends Map.Entry<K2, V2>> orElseMapper,
                                                                                               Supplier<Map<K2, V2>> mapFactory,
                                                                                               Class<? extends Exception> expectedException,
                                                                                               Map<K2, V2> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException,
                    () ->
                            applyOrElse(
                                    sourceMap, partialFunction, orElseMapper, mapFactory
                            )
            );
        } else {
            assertEquals(expectedResult,
                    applyOrElse(
                            sourceMap, partialFunction, orElseMapper, mapFactory
                    )
            );
        }
    }


    static Stream<Arguments> collectWithBiPredicateAndBiFunctionTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "A");
            put(2, "B");
            put(4, "o");
        }};
        BiPredicate<Integer, String> isKeyEvenAndValueVowel = (k, v) -> k % 2 == 0 && "AEIOUaeiou".contains(v);
        BiFunction<Integer, String, Map.Entry<Integer, Long>> multiply2KeyPlusValueLength =
                (k, v) ->
                        new AbstractMap.SimpleEntry<>(
                                k,
                                null == v
                                        ? 0L
                                        : (long) (k * 2 + v.length())
                        );
        Map<Integer, Long> intsAndLongsNoFilterResult = new HashMap<>() {{
            put(1, 3L);
            put(2, 5L);
            put(4, 9L);
        }};
        Map<Integer, Long> intsAndLongsResult = new HashMap<>() {{
            put(4, 9L);
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,        filterPredicate,          mapFunction,                   expectedException,                expectedResult
                Arguments.of( null,             null,                     null,                          null,                             Map.of() ),
                Arguments.of( null,             isKeyEvenAndValueVowel,   null,                          null,                             Map.of() ),
                Arguments.of( null,             null,                     multiply2KeyPlusValueLength,   null,                             Map.of() ),
                Arguments.of( null,             isKeyEvenAndValueVowel,   multiply2KeyPlusValueLength,   null,                             Map.of() ),
                Arguments.of( Map.of(),         null,                     null,                          null,                             Map.of() ),
                Arguments.of( Map.of(),         isKeyEvenAndValueVowel,   null,                          null,                             Map.of() ),
                Arguments.of( Map.of(),         null,                     multiply2KeyPlusValueLength,   null,                             Map.of() ),
                Arguments.of( Map.of(),         isKeyEvenAndValueVowel,   multiply2KeyPlusValueLength,   null,                             Map.of() ),
                Arguments.of( intsAndStrings,   null,                     null,                          IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   isKeyEvenAndValueVowel,   null,                          IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   null,                     multiply2KeyPlusValueLength,   null,                             intsAndLongsNoFilterResult ),
                Arguments.of( intsAndStrings,   isKeyEvenAndValueVowel,   multiply2KeyPlusValueLength,   null,                             intsAndLongsResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("collectWithBiPredicateAndBiFunctionTestCases")
    @DisplayName("collect: with BiPredicate and BiFunction test cases")
    public <K1, K2, V1, V2> void collectWithBiPredicateAndBiFunction_testCases(Map<? extends K1, ? extends V1> sourceMap,
                                                                               BiPredicate<? super K1, ? super V1> filterPredicate,
                                                                               BiFunction<? super K1, ? super V1, ? extends Map.Entry<K2, V2>> mapFunction,
                                                                               Class<? extends Exception> expectedException,
                                                                               Map<K2, V2> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> collect(sourceMap, filterPredicate, mapFunction));
        } else {
            assertEquals(expectedResult, collect(sourceMap, filterPredicate, mapFunction));
        }
    }


    static Stream<Arguments> collectWithBiPredicateBiFunctionAndSupplierTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "A");
            put(2, null);
            put(4, "o");
        }};
        BiPredicate<Integer, String> isKeyEvenAndValueVowel = (k, v) ->
                k % 2 == 0 &&
                (
                    null != v &&
                    "AEIOUaeiou".contains(v)
                );
        BiFunction<Integer, String, Map.Entry<Integer, Long>> multiply2KeyPlusValueLength =
                (k, v) ->
                        new AbstractMap.SimpleEntry<>(
                                k,
                                (long) (
                                        k * 2 +
                                                (
                                                        null == v
                                                                ? 0
                                                                : v.length()
                                                )
                                )
                        );
        Supplier<Map<Integer, Long>> linkedMapSupplier = LinkedHashMap::new;

        Map<Integer, Long> intsAndLongsNoFilterResult = new HashMap<>() {{
            put(1, 3L);
            put(2, 4L);
            put(4, 9L);
        }};
        Map<Integer, Long> intsAndLongsResult = new HashMap<>() {{
            put(4, 9L);
        }};
        LinkedHashMap<Integer, Long> intsAndLongsLinkedMapResult = new LinkedHashMap<>(intsAndLongsResult);
        return Stream.of(
                //@formatter:off
                //            sourceMap,        filterPredicate,          mapFunction,                   mapFactory,          expectedException,                expectedResult
                Arguments.of( null,             null,                     null,                          null,                null,                             Map.of() ),
                Arguments.of( null,             isKeyEvenAndValueVowel,   null,                          null,                null,                             Map.of() ),
                Arguments.of( null,             null,                     multiply2KeyPlusValueLength,   null,                null,                             Map.of() ),
                Arguments.of( null,             isKeyEvenAndValueVowel,   multiply2KeyPlusValueLength,   null,                null,                             Map.of() ),
                Arguments.of( Map.of(),         null,                     null,                          null,                null,                             Map.of() ),
                Arguments.of( Map.of(),         isKeyEvenAndValueVowel,   null,                          null,                null,                             Map.of() ),
                Arguments.of( Map.of(),         null,                     multiply2KeyPlusValueLength,   null,                null,                             Map.of() ),
                Arguments.of( Map.of(),         isKeyEvenAndValueVowel,   multiply2KeyPlusValueLength,   null,                null,                             Map.of() ),
                Arguments.of( intsAndStrings,   null,                     null,                          null,                IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   isKeyEvenAndValueVowel,   null,                          null,                IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   null,                     multiply2KeyPlusValueLength,   null,                null,                             intsAndLongsNoFilterResult ),
                Arguments.of( intsAndStrings,   isKeyEvenAndValueVowel,   multiply2KeyPlusValueLength,   null,                null,                             intsAndLongsResult ),
                Arguments.of( intsAndStrings,   isKeyEvenAndValueVowel,   multiply2KeyPlusValueLength,   linkedMapSupplier,   null,                             intsAndLongsLinkedMapResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("collectWithBiPredicateBiFunctionAndSupplierTestCases")
    @DisplayName("collect: with BiPredicate, BiFunction and Supplier test cases")
    public <K1, K2, V1, V2> void collectBiPredicateBiFunctionAndSupplier_testCases(Map<? extends K1, ? extends V1> sourceMap,
                                                                                   BiPredicate<? super K1, ? super V1> filterPredicate,
                                                                                   BiFunction<? super K1, ? super V1, ? extends Map.Entry<K2, V2>> mapFunction,
                                                                                   Supplier<Map<K2, V2>> mapFactory,
                                                                                   Class<? extends Exception> expectedException,
                                                                                   Map<K2, V2> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> collect(sourceMap, filterPredicate, mapFunction, mapFactory));
        } else {
            assertEquals(expectedResult, collect(sourceMap, filterPredicate, mapFunction, mapFactory));
        }
    }


    static Stream<Arguments> collectWithPartialFunctionTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "A");
            put(2, null);
            put(4, "o");
        }};
        PartialFunction<Map.Entry<Integer, String>, Map.Entry<Integer, Long>> multiply2KeyPlusValueLength = PartialFunction.of(
                e -> null != e &&
                        null != e.getValue() &&
                        0 == e.getKey() % 2 &&
                        "AEIOUaeiou".contains(e.getValue()),
                e -> null == e
                        ? null
                        : new AbstractMap.SimpleEntry<>(
                                e.getKey(),
                                null == e.getValue()
                                        ? 0L
                                        : (long) (e.getKey() * 2 + e.getValue().length())
                          )
        );
        Map<Integer, Long> intsAndLongsResult = new HashMap<>() {{
            put(4, 9L);
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,        partialFunction,               expectedException,                expectedResult
                Arguments.of( null,             null,                          null,                             Map.of() ),
                Arguments.of( null,             multiply2KeyPlusValueLength,   null,                             Map.of() ),
                Arguments.of( Map.of(),         null,                          null,                             Map.of() ),
                Arguments.of( Map.of(),         multiply2KeyPlusValueLength,   null,                             Map.of() ),
                Arguments.of( intsAndStrings,   null,                          IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   multiply2KeyPlusValueLength,   null,                             intsAndLongsResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("collectWithPartialFunctionTestCases")
    @DisplayName("collect: with PartialFunction test cases")
    public <K1, K2, V1, V2> void collectWithPartialFunction_testCases(Map<? extends K1, ? extends V1> sourceMap,
                                                                      PartialFunction<? super Map.Entry<K1, V1>, ? extends Map.Entry<K2, V2>> partialFunction,
                                                                      Class<? extends Exception> expectedException,
                                                                      Map<K2, V2> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> collect(sourceMap, partialFunction));
        } else {
            assertEquals(expectedResult, collect(sourceMap, partialFunction));
        }
    }


    static Stream<Arguments> collectWithPartialFunctionAndSupplierTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "A");
            put(2, null);
            put(4, "o");
        }};
        PartialFunction<Map.Entry<Integer, String>, Map.Entry<Integer, Long>> multiply2KeyPlusValueLength = PartialFunction.of(
                e -> null != e && 0 == e.getKey() % 2,
                e -> null == e
                        ? null
                        : new AbstractMap.SimpleEntry<>(
                                e.getKey() + 1,
                                null == e.getValue()
                                        ? 0L
                                        : (long) (e.getKey() * 2 + e.getValue().length())
                          )
        );
        Supplier<Map<Integer, Long>> linkedMapSupplier = LinkedHashMap::new;
        Map<Integer, Long> intsAndLongsResult = new HashMap<>() {{
            put(3, 0L);
            put(5, 9L);
        }};
        LinkedHashMap<Integer, Long> intsAndLongsLinkedMapResult = new LinkedHashMap<>(intsAndLongsResult);
        return Stream.of(
                //@formatter:off
                //            sourceMap,        partialFunction,               mapFactory,          expectedException,                expectedResult
                Arguments.of( null,             null,                          null,                null,                             Map.of() ),
                Arguments.of( null,             null,                          linkedMapSupplier,   null,                             Map.of() ),
                Arguments.of( null,             multiply2KeyPlusValueLength,   null,                null,                             Map.of() ),
                Arguments.of( null,             multiply2KeyPlusValueLength,   linkedMapSupplier,   null,                             Map.of() ),
                Arguments.of( Map.of(),         null,                          null,                null,                             Map.of() ),
                Arguments.of( Map.of(),         null,                          linkedMapSupplier,   null,                             Map.of() ),
                Arguments.of( Map.of(),         multiply2KeyPlusValueLength,   null,                null,                             Map.of() ),
                Arguments.of( Map.of(),         multiply2KeyPlusValueLength,   linkedMapSupplier,   null,                             Map.of() ),
                Arguments.of( intsAndStrings,   null,                          null,                IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   null,                          linkedMapSupplier,   IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   multiply2KeyPlusValueLength,   null,                null,                             intsAndLongsResult ),
                Arguments.of( intsAndStrings,   multiply2KeyPlusValueLength,   linkedMapSupplier,   null,                             intsAndLongsLinkedMapResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("collectWithPartialFunctionAndSupplierTestCases")
    @DisplayName("collect: with PartialFunction and Supplier test cases")
    public <K1, K2, V1, V2> void collectWithPartialFunctionAndSupplier_testCases(Map<? extends K1, ? extends V1> sourceMap,
                                                                                 PartialFunction<? super Map.Entry<K1, V1>, ? extends Map.Entry<K2, V2>> partialFunction,
                                                                                 Supplier<Map<K2, V2>> mapFactory,
                                                                                 Class<? extends Exception> expectedException,
                                                                                 Map<K2, V2> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> collect(sourceMap, partialFunction, mapFactory));
        } else {
            assertEquals(expectedResult, collect(sourceMap, partialFunction, mapFactory));
        }
    }


    static Stream<Arguments> collectFirstTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "A");
            put(2, null);
            put(4, "o");
        }};
        PartialFunction<Map.Entry<Integer, String>, Map.Entry<Integer, Long>> multiply2KeyPlusValueLength = PartialFunction.of(
                e -> null != e &&
                        null != e.getValue() &&
                        0 == e.getKey() % 2 &&
                        "AEIOUaeiou".contains(e.getValue()),
                e -> null == e
                        ? null
                        : new AbstractMap.SimpleEntry<>(
                                e.getKey(),
                                null == e.getValue()
                                        ? 0L
                                        : (long) (e.getKey() * 2 + e.getValue().length())
                          )
        );
        Map.Entry<Integer, Long> intsAndLongsResult = new AbstractMap.SimpleEntry<>(4, 9L);
        return Stream.of(
                //@formatter:off
                //            sourceMap,        partialFunction,               expectedException,                expectedResult
                Arguments.of( null,             null,                          null,                             empty() ),
                Arguments.of( null,             multiply2KeyPlusValueLength,   null,                             empty() ),
                Arguments.of( Map.of(),         null,                          null,                             empty() ),
                Arguments.of( Map.of(),         multiply2KeyPlusValueLength,   null,                             empty() ),
                Arguments.of( intsAndStrings,   null,                          IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   multiply2KeyPlusValueLength,   null,                             of(intsAndLongsResult) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("collectFirstTestCases")
    @DisplayName("collectFirst: test cases")
    public <K1, K2, V1, V2> void collectFirst_testCases(Map<? extends K1, ? extends V1> sourceMap,
                                                                      PartialFunction<? super Map.Entry<K1, V1>, ? extends Map.Entry<K2, V2>> partialFunction,
                                                                      Class<? extends Exception> expectedException,
                                                                      Optional<Map<K2, V2>> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> collectFirst(sourceMap, partialFunction));
        } else {
            assertEquals(expectedResult, collectFirst(sourceMap, partialFunction));
        }
    }


    static Stream<Arguments> concatOnlyMapsTestCases() {
        Map<Integer, String> map1 = new HashMap<>() {{
            put(1, "A");
            put(4, "o");
        }};
        Map<Integer, String> map2 = new HashMap<>() {{
            put(1, "t");
            put(2, "y");
        }};
        Map<Integer, String> map3 = new HashMap<>() {{
            put(6, "w");
        }};
        Map<Integer, String> mapWithNullValues = new LinkedHashMap<>() {{
            put(1, "AB");
            put(2, null);
        }};

        Map<Integer, String> expectedResultMaps123 = new LinkedHashMap<>() {{
            put(1, "t");
            put(2, "y");
            put(4, "o");
            put(6, "w");
        }};
        Map<Integer, String> expectedResultMap2AndMapWithNullValues = new LinkedHashMap<>() {{
            put(1, "AB");
            put(2, null);
        }};
        return Stream.of(
                //@formatter:off
                //            mapToConcat1,   mapToConcat2,   mapToConcat3,        expectedResult
                Arguments.of( null,           null,           null,                Map.of() ),
                Arguments.of( Map.of(),       null,           null,                Map.of() ),
                Arguments.of( Map.of(),       Map.of(),       null,                Map.of() ),
                Arguments.of( Map.of(),       Map.of(),       Map.of(),            Map.of() ),
                Arguments.of( map1,           null,           Map.of(),            map1 ),
                Arguments.of( map1,           map2,           map3,                expectedResultMaps123 ),
                Arguments.of( map2,           null,           mapWithNullValues,   expectedResultMap2AndMapWithNullValues )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("concatOnlyMapsTestCases")
    @DisplayName("concat: only with the maps to concat test cases")
    public <T, E> void concatOnlyMaps_testCases(Map<? extends T, ? extends E> mapToConcat1,
                                                Map<? extends T, ? extends E> mapToConcat2,
                                                Map<? extends T, ? extends E> mapToConcat3,
                                                Map<T, E> expectedResult) {
        assertEquals(expectedResult, concat(mapToConcat1, mapToConcat2, mapToConcat3));
    }


    static Stream<Arguments> concatMapsAndMapFactoryTestCases() {
        Map<Integer, String> map1 = new HashMap<>() {{
            put(1, "A");
            put(4, "o");
        }};
        Map<Integer, String> map2 = new HashMap<>() {{
            put(1, "t");
            put(2, "y");
        }};
        Map<Integer, String> map3 = new HashMap<>() {{
            put(6, "w");
        }};
        Map<Integer, String> mapWithNullValues = new LinkedHashMap<>() {{
            put(1, "AB");
            put(2, null);
        }};

        Supplier<Map<Integer, Long>> linkedMapSupplier = LinkedHashMap::new;

        Map<Integer, String> expectedResultMaps123 = new LinkedHashMap<>() {{
            put(1, "t");
            put(2, "y");
            put(4, "o");
            put(6, "w");
        }};
        Map<Integer, String> expectedResultMap2AndMapWithNullValues = new LinkedHashMap<>() {{
            put(1, "AB");
            put(2, null);
        }};
        return Stream.of(
                //@formatter:off
                //            mapToConcat1,   mapToConcat2,   mapToConcat3,        mapFactory,          expectedResult
                Arguments.of( null,           null,           null,                null,                Map.of() ),
                Arguments.of( null,           null,           null,                linkedMapSupplier,   Map.of() ),
                Arguments.of( Map.of(),       null,           null,                null,                Map.of() ),
                Arguments.of( Map.of(),       null,           null,                linkedMapSupplier,   Map.of() ),
                Arguments.of( Map.of(),       Map.of(),       null,                null,                Map.of() ),
                Arguments.of( Map.of(),       Map.of(),       null,                linkedMapSupplier,   Map.of() ),
                Arguments.of( Map.of(),       Map.of(),       Map.of(),            null,                Map.of() ),
                Arguments.of( Map.of(),       Map.of(),       Map.of(),            linkedMapSupplier,   Map.of() ),
                Arguments.of( map1,           null,           Map.of(),            null,                map1 ),
                Arguments.of( map1,           null,           Map.of(),            linkedMapSupplier,   map1 ),
                Arguments.of( map1,           map2,           map3,                null,                expectedResultMaps123 ),
                Arguments.of( map1,           map2,           map3,                linkedMapSupplier,   expectedResultMaps123 ),
                Arguments.of( map2,           null,           mapWithNullValues,   null,                expectedResultMap2AndMapWithNullValues ),
                Arguments.of( map2,           null,           mapWithNullValues,   linkedMapSupplier,   expectedResultMap2AndMapWithNullValues )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("concatMapsAndMapFactoryTestCases")
    @DisplayName("concat: with map factory and maps to concat test cases")
    public <T, E> void concatMapsAndMapFactory_testCases(Map<? extends T, ? extends E> mapToConcat1,
                                                         Map<? extends T, ? extends E> mapToConcat2,
                                                         Map<? extends T, ? extends E> mapToConcat3,
                                                         Supplier<Map<T, E>> mapFactory,
                                                         Map<T, E> expectedResult) {
        assertEquals(expectedResult, concat(mapFactory, mapToConcat1, mapToConcat2, mapToConcat3));
    }


    static Stream<Arguments> concatAllParametersTestCases() {
        Map<Integer, String> map1 = new HashMap<>() {{
            put(1, "A");
            put(4, "o");
        }};
        Map<Integer, String> map2 = new HashMap<>() {{
            put(1, "t");
            put(2, "y");
        }};
        Map<Integer, String> map3 = new HashMap<>() {{
            put(6, "w");
        }};
        Map<Integer, String> mapWithNullValues = new LinkedHashMap<>() {{
            put(1, "AB");
            put(2, null);
        }};

        Supplier<Map<Integer, String>> linkedMapSupplier = LinkedHashMap::new;
        BinaryOperator<String> keepsOldValue = (oldValue, newValue) -> oldValue;

        Map<Integer, String> expectedResultMaps123DefaultMerge = new LinkedHashMap<>() {{
            put(1, "t");
            put(2, "y");
            put(4, "o");
            put(6, "w");
        }};
        Map<Integer, String> expectedResultMaps123ProvidedMerge = new LinkedHashMap<>() {{
            put(1, "A");
            put(2, "y");
            put(4, "o");
            put(6, "w");
        }};
        Map<Integer, String> expectedResultMap2AndMapWithNullValuesDefaultMerge = new LinkedHashMap<>() {{
            put(1, "AB");
            put(2, null);
        }};
        Map<Integer, String> expectedResultMap2AndMapWithNullValuesProvidedMerge = new LinkedHashMap<>() {{
            put(1, "t");
            put(2, "y");
        }};
        return Stream.of(
                //@formatter:off
                //            mapToConcat1,   mapToConcat2,   mapToConcat3,        mapFactory,          mergeValueFunction,   expectedResult
                Arguments.of( null,           null,           null,                null,                null,                 Map.of() ),
                Arguments.of( null,           null,           null,                null,                keepsOldValue,        Map.of() ),
                Arguments.of( null,           null,           null,                linkedMapSupplier,   null,                 Map.of() ),
                Arguments.of( null,           null,           null,                linkedMapSupplier,   keepsOldValue,        Map.of() ),
                Arguments.of( Map.of(),       null,           null,                null,                null,                 Map.of() ),
                Arguments.of( Map.of(),       null,           null,                null,                keepsOldValue,        Map.of() ),
                Arguments.of( Map.of(),       null,           null,                linkedMapSupplier,   null,                 Map.of() ),
                Arguments.of( Map.of(),       null,           null,                linkedMapSupplier,   keepsOldValue,        Map.of() ),
                Arguments.of( Map.of(),       Map.of(),       null,                null,                null,                 Map.of() ),
                Arguments.of( Map.of(),       Map.of(),       null,                null,                keepsOldValue,        Map.of() ),
                Arguments.of( Map.of(),       Map.of(),       null,                linkedMapSupplier,   null,                 Map.of() ),
                Arguments.of( Map.of(),       Map.of(),       null,                linkedMapSupplier,   keepsOldValue,        Map.of() ),
                Arguments.of( Map.of(),       Map.of(),       Map.of(),            null,                null,                 Map.of() ),
                Arguments.of( Map.of(),       Map.of(),       Map.of(),            null,                keepsOldValue,        Map.of() ),
                Arguments.of( Map.of(),       Map.of(),       Map.of(),            linkedMapSupplier,   null,                 Map.of() ),
                Arguments.of( Map.of(),       Map.of(),       Map.of(),            linkedMapSupplier,   keepsOldValue,        Map.of() ),
                Arguments.of( map1,           null,           Map.of(),            null,                null,                 map1 ),
                Arguments.of( map1,           null,           Map.of(),            null,                keepsOldValue,        map1 ),
                Arguments.of( map1,           null,           Map.of(),            linkedMapSupplier,   null,                 map1 ),
                Arguments.of( map1,           null,           Map.of(),            linkedMapSupplier,   keepsOldValue,        map1 ),
                Arguments.of( map1,           map2,           map3,                null,                null,                 expectedResultMaps123DefaultMerge ),
                Arguments.of( map1,           map2,           map3,                null,                keepsOldValue,        expectedResultMaps123ProvidedMerge ),
                Arguments.of( map1,           map2,           map3,                linkedMapSupplier,   null,                 expectedResultMaps123DefaultMerge ),
                Arguments.of( map1,           map2,           map3,                linkedMapSupplier,   keepsOldValue,        expectedResultMaps123ProvidedMerge ),
                Arguments.of( map2,           null,           mapWithNullValues,   null,                null,                 expectedResultMap2AndMapWithNullValuesDefaultMerge ),
                Arguments.of( map2,           null,           mapWithNullValues,   null,                keepsOldValue,        expectedResultMap2AndMapWithNullValuesProvidedMerge ),
                Arguments.of( map2,           null,           mapWithNullValues,   linkedMapSupplier,   null,                 expectedResultMap2AndMapWithNullValuesDefaultMerge ),
                Arguments.of( map2,           null,           mapWithNullValues,   linkedMapSupplier,   keepsOldValue,        expectedResultMap2AndMapWithNullValuesProvidedMerge )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("concatAllParametersTestCases")
    @DisplayName("concat: with all parameters test cases")
    public <T, E> void concatAllParameters_testCases(Map<? extends T, ? extends E> mapToConcat1,
                                                     Map<? extends T, ? extends E> mapToConcat2,
                                                     Map<? extends T, ? extends E> mapToConcat3,
                                                     Supplier<Map<T, E>> mapFactory,
                                                     BinaryOperator<E> mergeValueFunction,
                                                     Map<T, E> expectedResult) {
        assertEquals(expectedResult, concat(mapFactory, mergeValueFunction, mapToConcat1, mapToConcat2, mapToConcat3));
    }


    static Stream<Arguments> copyNoMapFactoryTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "A");
            put(2, "B");
            put(4, "o");
        }};
        Map<Integer, String> intsAndStringsResult = new HashMap<>(intsAndStrings);
        return Stream.of(
                //@formatter:off
                //            sourceMap,        expectedResult
                Arguments.of( null,             Map.of() ),
                Arguments.of( Map.of(),         Map.of() ),
                Arguments.of( intsAndStrings,   intsAndStringsResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("copyNoMapFactoryTestCases")
    @DisplayName("copy: without map factory test cases")
    public <T, E> void copyNoMapFactory_testCases(Map<? extends T, ? extends E> sourceMap,
                                                  Map<T, E> expectedResult) {
        Map<T, E> result = copy(sourceMap);
        assertEquals(expectedResult, result);
        if (null != expectedResult && !expectedResult.isEmpty()) {
            expectedResult.clear();
            assertEquals(0, expectedResult.size());
            assertFalse(result.isEmpty());
        }
    }


    static Stream<Arguments> copyAllParametersTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "A");
            put(2, "B");
            put(4, "o");
            put(6, null);
        }};
        Supplier<Map<Integer, Long>> treeMapSupplier = TreeMap::new;
        Map<Integer, String> intsAndStringsResultHash = new HashMap<>(intsAndStrings);
        Map<Integer, String> intsAndStringsResultTree = new TreeMap<>(intsAndStrings);
        return Stream.of(
                //@formatter:off
                //            sourceMap,        mapFactory,        expectedResult
                Arguments.of( null,             null,              Map.of() ),
                Arguments.of( null,             treeMapSupplier,   Map.of() ),
                Arguments.of( Map.of(),         null,              Map.of() ),
                Arguments.of( Map.of(),         treeMapSupplier,   Map.of() ),
                Arguments.of( intsAndStrings,   null,              intsAndStringsResultHash ),
                Arguments.of( intsAndStrings,   treeMapSupplier,   intsAndStringsResultTree )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("copyAllParametersTestCases")
    @DisplayName("copy: with all parameters test cases")
    public <T, E> void copyAllParameters_testCases(Map<? extends T, ? extends E> sourceMap,
                                                   Supplier<Map<T, E>> mapFactory,
                                                   Map<T, E> expectedResult) {
        Map<T, E> result = copy(sourceMap, mapFactory);
        assertEquals(expectedResult, result);
        if (null != expectedResult && !expectedResult.isEmpty()) {
            expectedResult.clear();
            assertEquals(0, expectedResult.size());
            assertFalse(result.isEmpty());
        }
    }


    static Stream<Arguments> countTestCases() {
        Map<Integer, String> intsAndStrings = new LinkedHashMap<>() {{
            put(1, "A");
            put(2, "i");
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

        BiPredicate<String, Long> isKeyContainsZAndValueIsOdd =
                (k, v) ->
                        k.toUpperCase().contains("Z") && v % 2 == 1;
        return Stream.of(
                //@formatter:off
                //            sourceMap,         filterPredicate,               expectedResult
                Arguments.of( null,              null,                          0 ),
                Arguments.of( Map.of(),          null,                          0 ),
                Arguments.of( null,              isKeyEvenAndValueVowel,        0 ),
                Arguments.of( Map.of(),          isKeyEvenAndValueVowel,        0 ),
                Arguments.of( intsAndStrings,    isKeyEvenAndValueVowel,        3 ),
                Arguments.of( stringsAndLongs,   isKeyContainsZAndValueIsOdd,   2 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("countTestCases")
    @DisplayName("count: test cases")
    public <T, E> void count_testCases(Map<? extends T, ? extends E> sourceMap,
                                       BiPredicate<? super T, ? super E> filterPredicate,
                                       int expectedResult) {
        assertEquals(expectedResult, count(sourceMap, filterPredicate));
    }


    static Stream<Arguments> dropWhileNoMapFactoryTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(2, "A");
            put(4, "o");
            put(5, "H");
        }};
        BiPredicate<Integer, String> isKeyEvenAndValueVowel = (k, v) -> k % 2 == 0 && "AEIOUaeiou".contains(v);
        Map<Integer, String> intsAndStringsResult = new HashMap<>() {{
            put(5, "H");
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,        filterPredicate,          expectedResult
                Arguments.of( null,             null,                     Map.of() ),
                Arguments.of( null,             isKeyEvenAndValueVowel,   Map.of() ),
                Arguments.of( Map.of(),         null,                     Map.of() ),
                Arguments.of( Map.of(),         isKeyEvenAndValueVowel,   Map.of() ),
                Arguments.of( intsAndStrings,   null,                     intsAndStrings ),
                Arguments.of( intsAndStrings,   isKeyEvenAndValueVowel,   intsAndStringsResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("dropWhileNoMapFactoryTestCases")
    @DisplayName("dropWhile: without map factory test cases")
    public <T, E> void dropWhileNoMapFactory_testCases(Map<? extends T, ? extends E> sourceMap,
                                                       BiPredicate<? super T, ? super E> filterPredicate,
                                                       Map<T, E> expectedResult) {
        assertEquals(expectedResult, dropWhile(sourceMap, filterPredicate));
    }


    static Stream<Arguments> dropWhileAllParametersTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(2, "A");
            put(4, "o");
            put(5, "H");
            put(6, null);
        }};
        BiPredicate<Integer, String> isKeyEvenAndValueVowel = (k, v) ->
                k % 2 == 0 &&
                        (
                                null == v ||
                                        "AEIOUaeiou".contains(v)
                        );
        Supplier<Map<Integer, Long>> linkedMapSupplier = LinkedHashMap::new;
        Map<Integer, String> intsAndStringsResult = new LinkedHashMap<>() {{
            put(5, "H");
            put(6, null);
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,        filterPredicate,          mapFactory,          expectedResult
                Arguments.of( null,             null,                     null,                Map.of() ),
                Arguments.of( Map.of(),         null,                     null,                Map.of() ),
                Arguments.of( null,             isKeyEvenAndValueVowel,   null,                Map.of() ),
                Arguments.of( Map.of(),         isKeyEvenAndValueVowel,   null,                Map.of() ),
                Arguments.of( intsAndStrings,   null,                     null,                intsAndStrings ),
                Arguments.of( intsAndStrings,   null,                     linkedMapSupplier,   intsAndStrings ),
                Arguments.of( intsAndStrings,   isKeyEvenAndValueVowel,   linkedMapSupplier,   intsAndStringsResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("dropWhileAllParametersTestCases")
    @DisplayName("dropWhile: with all parameters test cases")
    public <T, E> void dropWhileAllParameters_testCases(Map<? extends T, ? extends E> sourceMap,
                                                        BiPredicate<? super T, ? super E> filterPredicate,
                                                        Supplier<Map<T, E>> mapFactory,
                                                        Map<T, E> expectedResult) {
        assertEquals(expectedResult, dropWhile(sourceMap, filterPredicate, mapFactory));
    }


    static Stream<Arguments> filterNoMapFactoryTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "A");
            put(2, "B");
            put(4, "o");
        }};
        BiPredicate<Integer, String> isKeyEvenAndValueVowel = (k, v) -> k % 2 == 0 && "AEIOUaeiou".contains(v);
        Map<Integer, String> intsAndStringsResult = new HashMap<>() {{
            put(4, "o");
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,        filterPredicate,          expectedResult
                Arguments.of( null,             null,                     Map.of() ),
                Arguments.of( null,             isKeyEvenAndValueVowel,   Map.of() ),
                Arguments.of( Map.of(),         null,                     Map.of() ),
                Arguments.of( Map.of(),         isKeyEvenAndValueVowel,   Map.of() ),
                Arguments.of( intsAndStrings,   null,                     intsAndStrings ),
                Arguments.of( intsAndStrings,   isKeyEvenAndValueVowel,   intsAndStringsResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("filterNoMapFactoryTestCases")
    @DisplayName("filter: without map factory test cases")
    public <T, E> void filterNoMapFactory_testCases(Map<? extends T, ? extends E> sourceMap,
                                                    BiPredicate<? super T, ? super E> filterPredicate,
                                                    Map<T, E> expectedResult) {
        assertEquals(expectedResult, filter(sourceMap, filterPredicate));
    }


    static Stream<Arguments> filterAllParametersTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "A");
            put(2, "B");
            put(4, "o");
            put(6, null);
        }};
        BiPredicate<Integer, String> isKeyEvenAndValueVowel = (k, v) ->
                k % 2 == 0 &&
                        (
                                null == v ||
                                        "AEIOUaeiou".contains(v)
                        );
        Supplier<Map<Integer, Long>> linkedMapSupplier = LinkedHashMap::new;
        Map<Integer, String> intsAndStringsResult = new LinkedHashMap<>() {{
            put(4, "o");
            put(6, null);
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,        filterPredicate,          mapFactory,          expectedResult
                Arguments.of( null,             null,                     null,                Map.of() ),
                Arguments.of( Map.of(),         null,                     null,                Map.of() ),
                Arguments.of( null,             isKeyEvenAndValueVowel,   null,                Map.of() ),
                Arguments.of( Map.of(),         isKeyEvenAndValueVowel,   null,                Map.of() ),
                Arguments.of( intsAndStrings,   null,                     null,                intsAndStrings ),
                Arguments.of( intsAndStrings,   null,                     linkedMapSupplier,   intsAndStrings ),
                Arguments.of( intsAndStrings,   isKeyEvenAndValueVowel,   linkedMapSupplier,   intsAndStringsResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("filterAllParametersTestCases")
    @DisplayName("filter: with all parameters test cases")
    public <T, E> void filterAllParameters_testCases(Map<? extends T, ? extends E> sourceMap,
                                                     BiPredicate<? super T, ? super E> filterPredicate,
                                                     Supplier<Map<T, E>> mapFactory,
                                                     Map<T, E> expectedResult) {
        assertEquals(expectedResult, filter(sourceMap, filterPredicate, mapFactory));
    }


    static Stream<Arguments> filterNotNoMapFactoryTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "A");
            put(2, "B");
            put(4, "o");
        }};
        BiPredicate<Integer, String> isKeyEvenAndValueVowel = (k, v) -> k % 2 == 0 && "AEIOUaeiou".contains(v);
        Map<Integer, String> intsAndStringsResult = new HashMap<>() {{
            put(1, "A");
            put(2, "B");
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,        filterPredicate,          expectedResult
                Arguments.of( null,             null,                     Map.of() ),
                Arguments.of( null,             isKeyEvenAndValueVowel,   Map.of() ),
                Arguments.of( Map.of(),         null,                     Map.of() ),
                Arguments.of( Map.of(),         isKeyEvenAndValueVowel,   Map.of() ),
                Arguments.of( intsAndStrings,   null,                     intsAndStrings ),
                Arguments.of( intsAndStrings,   isKeyEvenAndValueVowel,   intsAndStringsResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("filterNotNoMapFactoryTestCases")
    @DisplayName("filterNot: without map factory test cases")
    public <T, E> void filterNotNoMapFactory_testCases(Map<? extends T, ? extends E> sourceMap,
                                                       BiPredicate<? super T, ? super E> filterPredicate,
                                                       Map<T, E> expectedResult) {
        assertEquals(expectedResult, filterNot(sourceMap, filterPredicate));
    }


    static Stream<Arguments> filterNotAllParametersTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "A");
            put(2, null);
            put(4, "o");
        }};
        BiPredicate<Integer, String> isKeyEvenAndValueVowel = (k, v) ->
                k % 2 == 0 &&
                (
                    null != v &&
                    "AEIOUaeiou".contains(v)
                );
        Supplier<Map<Integer, Long>> linkedMapSupplier = LinkedHashMap::new;
        Map<Integer, String> intsAndStringsResult = new LinkedHashMap<>() {{
            put(1, "A");
            put(2, null);
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,        filterPredicate,          mapFactory,          expectedResult
                Arguments.of( null,             null,                     null,                Map.of() ),
                Arguments.of( Map.of(),         null,                     null,                Map.of() ),
                Arguments.of( null,             isKeyEvenAndValueVowel,   null,                Map.of() ),
                Arguments.of( Map.of(),         isKeyEvenAndValueVowel,   null,                Map.of() ),
                Arguments.of( intsAndStrings,   null,                     null,                intsAndStrings ),
                Arguments.of( intsAndStrings,   null,                     linkedMapSupplier,   intsAndStrings ),
                Arguments.of( intsAndStrings,   isKeyEvenAndValueVowel,   linkedMapSupplier,   intsAndStringsResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("filterNotAllParametersTestCases")
    @DisplayName("filterNot: with all parameters test cases")
    public <T, E> void filterNotAllParameters_testCases(Map<? extends T, ? extends E> sourceMap,
                                                        BiPredicate<? super T, ? super E> filterPredicate,
                                                        Supplier<Map<T, E>> mapFactory,
                                                        Map<T, E> expectedResult) {
        assertEquals(expectedResult, filterNot(sourceMap, filterPredicate, mapFactory));
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
                Arguments.of( Map.of(),          null,                          empty() ),
                Arguments.of( null,              isKeyEvenAndValueVowel,        empty() ),
                Arguments.of( Map.of(),          isKeyEvenAndValueVowel,        empty() ),
                Arguments.of( intsAndStrings,    isValueContainsZ,              empty() ),
                Arguments.of( intsAndStrings,    isKeyEvenAndValueVowel,        of(Map.entry(4, "o")) ),
                Arguments.of( stringsAndLongs,   isKeyContainsZAndValueIsOdd,   of(Map.entry("TZ", 69L)) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findTestCases")
    @DisplayName("find: test cases")
    public <T, E> void find_testCases(Map<? extends T, ? extends E> sourceMap,
                                      BiPredicate<? super T, ? super E> filterPredicate,
                                      Optional<Map.Entry<T, E>> expectedResult) {
        assertEquals(expectedResult, find(sourceMap, filterPredicate));
    }


    static Stream<Arguments> flattenNoCollectionFactoryTestCases() {
        Map<Integer, String> intsAndStringsRaw = new LinkedHashMap<>() {{
            put(1, "A");
            put(2, "B");
            put(4, "o");
        }};
        Map<Integer, List<String>> intsAndStringsList = new LinkedHashMap<>() {{
            put(1, List.of("A"));
            put(8, List.of("E", "H"));
        }};

        BiFunction<Integer, String, Tuple2<Integer, String>> flattenerRaw =
                Tuple2::of;

        BiFunction<Integer, List<String>, List<Tuple2<Integer, String>>> flattenerList =
                (i, l) ->
                        l.stream()
                                .map(elto -> Tuple2.of(i, elto))
                                .collect(Collectors.toList());

        List<Tuple2<Integer, String>> resultRaw = List.of(Tuple2.of(1, "A"), Tuple2.of(2, "B"), Tuple2.of(4, "o"));
        List<Tuple2<Integer, String>> resultList = List.of(Tuple2.of(1, "A"), Tuple2.of(8, "E"), Tuple2.of(8, "H"));
        return Stream.of(
                //@formatter:off
                //            sourceMap,            flattener,       expectedException,                expectedResult
                Arguments.of( intsAndStringsRaw,    null,            IllegalArgumentException.class,   null ),
                Arguments.of( null,                 null,            null,                             List.of() ),
                Arguments.of( Map.of(),             null,            null,                             List.of() ),
                Arguments.of( intsAndStringsRaw,    flattenerRaw,    null,                             resultRaw ),
                Arguments.of( intsAndStringsList,   flattenerList,   null,                             resultList )

        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("flattenNoCollectionFactoryTestCases")
    @DisplayName("flatten: without collection factory test cases")
    public <T, E, R, U> void flattenNoCollectionFactory_testCases(Map<? extends T, ? extends E> sourceMap,
                                                                  BiFunction<? super T, ? super E, ? extends R> flattener,
                                                                  Class<? extends Exception> expectedException,
                                                                  List<U> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> flatten(sourceMap, flattener));
        } else {
            assertEquals(expectedResult, flatten(sourceMap, flattener));
        }
    }


    static Stream<Arguments> flattenAllParametersTestCases() {
        Map<Integer, String> intsAndStringsRaw = new LinkedHashMap<>() {{
            put(1, "A");
            put(2, "B");
            put(4, "o");
        }};
        Map<Integer, List<String>> intsAndStringsList = new LinkedHashMap<>() {{
            put(1, List.of("A"));
            put(8, List.of("E", "H"));
        }};
        Supplier<Set<Tuple2<Integer, String>>> setSupplier = LinkedHashSet::new;

        BiFunction<Integer, String, Tuple2<Integer, String>> flattenerRaw =
                Tuple2::of;

        BiFunction<Integer, List<String>, List<Tuple2<Integer, String>>> flattenerList =
                (i, l) ->
                        l.stream()
                                .map(elto -> Tuple2.of(i, elto))
                                .collect(Collectors.toList());

        List<Tuple2<Integer, String>> resultRaw = List.of(Tuple2.of(1, "A"), Tuple2.of(2, "B"), Tuple2.of(4, "o"));
        List<Tuple2<Integer, String>> resultList = List.of(Tuple2.of(1, "A"), Tuple2.of(8, "E"), Tuple2.of(8, "H"));
        return Stream.of(
                //@formatter:off
                //            sourceMap,            flattener,       collectionFactory,   expectedException,                expectedResult
                Arguments.of( intsAndStringsRaw,    null,            null,                IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStringsRaw,    null,            setSupplier,         IllegalArgumentException.class,   null ),
                Arguments.of( null,                 null,            null,                null,                             List.of() ),
                Arguments.of( null,                 null,            setSupplier,         null,                             Set.of() ),
                Arguments.of( Map.of(),             null,            null,                null,                             List.of() ),
                Arguments.of( Map.of(),             null,            setSupplier,         null,                             Set.of() ),
                Arguments.of( intsAndStringsRaw,    flattenerRaw,    null,                null,                             resultRaw ),
                Arguments.of( intsAndStringsRaw,    flattenerRaw,    setSupplier,         null,                             new LinkedHashSet<>(resultRaw) ),
                Arguments.of( intsAndStringsList,   flattenerList,   null,                null,                             resultList ),
                Arguments.of( intsAndStringsList,   flattenerList,   setSupplier,         null,                             new LinkedHashSet<>(resultList) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("flattenAllParametersTestCases")
    @DisplayName("flatten: with all parameters test cases")
    public <T, E, R, U> void flattenAllParameters_testCases(Map<? extends T, ? extends E> sourceMap,
                                                            BiFunction<? super T, ? super E, ? extends R> flattener,
                                                            Supplier<Collection<U>> collectionFactory,
                                                            Class<? extends Exception> expectedException,
                                                            Collection<U> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> flatten(sourceMap, flattener, collectionFactory));
        } else {
            assertEquals(expectedResult, flatten(sourceMap, flattener, collectionFactory));
        }
    }


    static Stream<Arguments> foldLeftWithInitialValueAndAccumulatorTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "A");
            put(2, "B");
            put(4, null);
        }};
        Map<Integer, Long> intsAndLongs = new HashMap<>() {{
            put(7, 3L);
            put(11, 6L);
            put(21, 9L);
        }};
        TriFunction<Integer, Integer, String, Integer> multiply = (a, b, c) -> ofNullable(a).orElse(1) * b * ofNullable(c).map(String::length).orElse(1);
        TriFunction<Long, Integer, Long, Long> sum = (a, b, c) -> a + (long)b + c;
        return Stream.of(
                //@formatter:off
                //            sourceMap,         initialValue,   accumulator,   expectedResult
                Arguments.of( null,              null,           null,          null ),
                Arguments.of( new HashMap<>(),   2,              null,          2 ),
                Arguments.of( new HashMap<>(),   1,              multiply,      1 ),
                Arguments.of( intsAndStrings,    0,              null,          0 ),
                Arguments.of( intsAndStrings,    1,              multiply,      8 ),
                Arguments.of( intsAndStrings,    null,           multiply,      8 ),
                Arguments.of( intsAndLongs,      5L,             sum,           62L )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("foldLeftWithInitialValueAndAccumulatorTestCases")
    @DisplayName("foldLeft: with initialValue and accumulator test cases")
    public <T, E, R> void foldLeftWithInitialValueAndAccumulator_testCases(Map<? extends T, ? extends E> sourceMap,
                                                                           R initialValue,
                                                                           TriFunction<R, ? super T, ?super E, R> accumulator,
                                                                           R expectedResult) {
        assertEquals(expectedResult, foldLeft(sourceMap, initialValue, accumulator));
    }


    static Stream<Arguments> foldLeftAllParametersTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "A");
            put(2, "B");
            put(4, null);
            put(6, "e");
        }};
        Map<Integer, Long> intsAndLongs = new HashMap<>() {{
            put(7, 3L);
            put(11, 6L);
            put(21, null);
        }};
        BiPredicate<Integer, String> isKeyEvenAndValueVowel = (k, v) ->
                k % 2 == 0 &&
                        (
                                null != v &&
                                        "AEIOUaeiou".contains(v)
                        );
        TriFunction<Integer, Integer, String, Integer> multiply = (a, b, c) -> ofNullable(a).orElse(1) * b * ofNullable(c).map(String::length).orElse(1);
        TriFunction<Long, Integer, Long, Long> sum = (a, b, c) -> ofNullable(a).orElse(0L) + (long)b + ofNullable(c).orElse(0L);
        return Stream.of(
                //@formatter:off
                //            sourceMap,         filterPredicate,           initialValue,   accumulator,   expectedResult
                Arguments.of( null,               null,                     null,           null,          null ),
                Arguments.of( null,               isKeyEvenAndValueVowel,   null,           null,          null ),
                Arguments.of( new HashMap<>(),    null,                     null,           null,          null ),
                Arguments.of( new HashMap<>(),    isKeyEvenAndValueVowel,   null,           null,          null ),
                Arguments.of( new HashMap<>(),    null,                     2,              null,          2 ),
                Arguments.of( new HashMap<>(),    isKeyEvenAndValueVowel,   2,              null,          2 ),
                Arguments.of( new HashMap<>(),    null,                     1,              multiply,      1 ),
                Arguments.of( new HashMap<>(),    isKeyEvenAndValueVowel,   1,              multiply,      1 ),
                Arguments.of( intsAndLongs,       null,                     null,           null,          null ),
                Arguments.of( intsAndLongs,       null,                     null,           sum,           48L ),
                Arguments.of( intsAndLongs,       null,                     0L,             null,          0L ),
                Arguments.of( intsAndLongs,       null,                     2L,             sum,           50L ),
                Arguments.of( intsAndStrings,     null,                     null,           null,          null ),
                Arguments.of( intsAndStrings,     isKeyEvenAndValueVowel,   null,           null,          null ),
                Arguments.of( intsAndStrings,     null,                     0,              null,          0 ),
                Arguments.of( intsAndStrings,     isKeyEvenAndValueVowel,   0,              null,          0 ),
                Arguments.of( intsAndStrings,     null,                     1,              multiply,      48 ),
                Arguments.of( intsAndStrings,     isKeyEvenAndValueVowel,   1,              multiply,      6 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("foldLeftAllParametersTestCases")
    @DisplayName("foldLeft: with all parameters test cases")
    public <T, E, R> void foldLeftAllParameters_testCases(Map<? extends T, ? extends E> sourceMap,
                                                          BiPredicate<? super T, ? super E> filterPredicate,
                                                          R initialValue,
                                                          TriFunction<R, ? super T, ?super E, R> accumulator,
                                                          R expectedResult) {
        assertEquals(expectedResult, foldLeft(sourceMap, filterPredicate, initialValue, accumulator));
    }


    static Stream<Arguments> getOrElseDirectValueAsDefaultTestCases() {
        Map<Integer, Integer> integersMap = new HashMap<>() {{
            put(1, 21);
            put(4, 43);
            put(9, 101);
        }};
        Integer notNullIntegerValue = 25;
        Integer nullIntegerValue = null;
        return Stream.of(
                //@formatter:off
                //            sourceMap,     key,    defaultValue,          expectedResult
                Arguments.of( null,          null,   null,                  null ),
                Arguments.of( null,          2,      null,                  null ),
                Arguments.of( Map.of(),      null,   notNullIntegerValue,   notNullIntegerValue ),
                Arguments.of( Map.of(),      2,      notNullIntegerValue,   notNullIntegerValue ),
                Arguments.of( Map.of(),      2,      nullIntegerValue,      nullIntegerValue ),
                Arguments.of( integersMap,   null,   nullIntegerValue,      nullIntegerValue ),
                Arguments.of( integersMap,   null,   notNullIntegerValue,   notNullIntegerValue ),
                Arguments.of( integersMap,   2,      nullIntegerValue,      nullIntegerValue ),
                Arguments.of( integersMap,   2,      notNullIntegerValue,   notNullIntegerValue ),
                Arguments.of( integersMap,   1,      nullIntegerValue,      21 ),
                Arguments.of( integersMap,   1,      notNullIntegerValue,   21 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getOrElseDirectValueAsDefaultTestCases")
    @DisplayName("getOrElse: with a direct value as default test cases")
    public <T, E> void getOrElseDirectValueAsDefault_testCases(Map<? extends T, ? extends E> sourceMap,
                                                               T key,
                                                               E defaultValue,
                                                               E expectedResult) {
        assertEquals(expectedResult, getOrElse(sourceMap, key, defaultValue));
    }


    static Stream<Arguments> getOrElseSupplierAsDefaultValueTestCases() {
        Map<Integer, Integer> integersMap = new HashMap<>() {{
            put(1, 21);
            put(4, 43);
            put(9, 101);
        }};
        Supplier<Integer> always25 = () -> 25;
        return Stream.of(
                //@formatter:off
                //            sourceMap,     key,    defaultValue,   expectedException,                expectedResult
                Arguments.of( null,          null,   null,           IllegalArgumentException.class,   null ),
                Arguments.of( null,          2,      null,           IllegalArgumentException.class,   null ),
                Arguments.of( Map.of(),      2,      null,           IllegalArgumentException.class,   null ),
                Arguments.of( Map.of(),      null,   always25,       null,                             always25.get() ),
                Arguments.of( Map.of(),      1,      always25,       null,                             always25.get() ),
                Arguments.of( integersMap,   null,   always25,       null,                             always25.get() ),
                Arguments.of( integersMap,   1,      always25,       null,                             21 ),
                Arguments.of( integersMap,   2,      always25,       null,                             always25.get() )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getOrElseSupplierAsDefaultValueTestCases")
    @DisplayName("getOrElse: with Supplier as default value test cases")
    public <T, E> void getOrElseSupplierAsDefaultValue_testCases(Map<? extends T, ? extends E> sourceMap,
                                                                 T key,
                                                                 Supplier<E> defaultValue,
                                                                 Class<? extends Exception> expectedException,
                                                                 E expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> getOrElse(sourceMap, key, defaultValue));
        } else {
            assertEquals(expectedResult, getOrElse(sourceMap, key, defaultValue));
        }
    }


    static Stream<Arguments> groupByNoMapFactoryTestCases() {
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
        BiFunction<Integer, String, Integer> keyMod2 = (k, v) -> k % 2;
        BiFunction<Integer, Long, Long> keyPlusValueMod3 = (k, v) -> (k + v) % 3;

        Map<Integer, Map<Integer, String>> intsAndStringsResult = new HashMap<>() {{
            put(0,
                new HashMap<>() {{
                    put(2, "B");
                    put(4, "o");
                }}
            );
            put(1,
                new HashMap<>() {{
                    put(1, "A");
                }}
            );
        }};
        Map<Long, Map<Integer, Long>> intsAndLongsResult = new HashMap<>() {{
            put(0L,
                new HashMap<>() {{
                    put(21, 9L);
                }}
            );
            put(1L,
                new HashMap<>() {{
                    put(7, 3L);
                }}
            );
            put(2L,
                new HashMap<>() {{
                    put(11, 6L);
                }}
            );
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,         discriminatorKey,   expectedException,                expectedResult
                Arguments.of( null,              null,               null,                             Map.of() ),
                Arguments.of( null,              keyMod2,            null,                             Map.of() ),
                Arguments.of( Map.of(),          null,               null,                             Map.of() ),
                Arguments.of( Map.of(),          keyMod2,            null,                             Map.of() ),
                Arguments.of( intsAndStrings,    null,               IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,    keyMod2,            null,                             intsAndStringsResult ),
                Arguments.of( intsAndLongs,      keyPlusValueMod3,   null,                             intsAndLongsResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("groupByNoMapFactoryTestCases")
    @DisplayName("groupBy: without map factory test cases")
    public <T, E, R> void groupByNoMapFactory_testCases(Map<? extends T, ? extends E> sourceMap,
                                                        BiFunction<? super T, ? super E, ? extends R> discriminatorKey,
                                                        Class<? extends Exception> expectedException,
                                                        Map<R, Map<T, E>> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> groupBy(sourceMap, discriminatorKey));
        } else {
            assertEquals(expectedResult, groupBy(sourceMap, discriminatorKey));
        }
    }


    static Stream<Arguments> groupByAllParametersTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(6, "Aa");
            put(8, "Bb");
            put(9, "Oo");
        }};
        BiFunction<Integer, String, Integer> keyMod2 = (k, v) -> k % 2;
        Supplier<Map<String, String>> linkedMapSupplier = LinkedHashMap::new;

        Map<Integer, Map<Integer, String>> intsAndStringsResult = new HashMap<>() {{
            put(0,
                    new HashMap<>() {{
                        put(6, "Aa");
                        put(8, "Bb");
                    }}
            );
            put(1,
                    new HashMap<>() {{
                        put(9, "Oo");
                    }}
            );
        }};
        Map<Integer, Map<Integer, String>> intsAndStringsLinkedMapResult = new LinkedHashMap<>() {{
            put(0,
                    new LinkedHashMap<>() {{
                        put(6, "Aa");
                        put(8, "Bb");
                    }}
            );
            put(1,
                    new LinkedHashMap<>() {{
                        put(9, "Oo");
                    }}
            );
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,         discriminatorKey,   mapResultFactory,    mapValuesFactory,    expectedException,                expectedResult
                Arguments.of( null,              null,               null,                null,                null,                             Map.of() ),
                Arguments.of( null,              keyMod2,            null,                null,                null,                             Map.of() ),
                Arguments.of( null,              keyMod2,            linkedMapSupplier,   null,                null,                             new LinkedHashMap<>() ),
                Arguments.of( null,              keyMod2,            null,                linkedMapSupplier,   null,                             Map.of() ),
                Arguments.of( null,              keyMod2,            linkedMapSupplier,   linkedMapSupplier,   null,                             new LinkedHashMap<>() ),
                Arguments.of( Map.of(),          null,               null,                null,                null,                             Map.of() ),
                Arguments.of( Map.of(),          keyMod2,            null,                null,                null,                             Map.of() ),
                Arguments.of( Map.of(),          keyMod2,            linkedMapSupplier,   null,                null,                             new LinkedHashMap<>() ),
                Arguments.of( Map.of(),          keyMod2,            null,                linkedMapSupplier,   null,                             Map.of() ),
                Arguments.of( Map.of(),          keyMod2,            linkedMapSupplier,   linkedMapSupplier,   null,                             new LinkedHashMap<>() ),
                Arguments.of( intsAndStrings,    null,               null,                null,                IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,    null,               linkedMapSupplier,   null,                IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,    null,               linkedMapSupplier,   linkedMapSupplier,   IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,    keyMod2,            null,                null,                null,                             intsAndStringsResult ),
                Arguments.of( intsAndStrings,    keyMod2,            linkedMapSupplier,   linkedMapSupplier,   null,                             intsAndStringsLinkedMapResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("groupByAllParametersTestCases")
    @DisplayName("groupBy: with all parameters test cases")
    public <T, E, R> void groupByAllParameters_testCases(Map<? extends T, ? extends E> sourceMap,
                                                         BiFunction<? super T, ? super E, ? extends R> discriminatorKey,
                                                         Supplier<Map<R, Map<T, E>>> mapResultFactory,
                                                         Supplier<Map<T, E>> mapValuesFactory,
                                                         Class<? extends Exception> expectedException,
                                                         Map<R, Map<T, E>> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> groupBy(sourceMap, discriminatorKey, mapResultFactory, mapValuesFactory));
        } else {
            assertEquals(expectedResult, groupBy(sourceMap, discriminatorKey, mapResultFactory, mapValuesFactory));
        }
    }


    static Stream<Arguments> groupByMultiKeyNoMapFactoryTestCases() {
        Map<Integer, String> sourceMap = new HashMap<>() {{
            put(1, "Hi");
            put(2, "Hello");
            put(11, "World");
        }};
        BiFunction<Integer, String, List<String>> oddEvenAndCompareWith10Key = (k, v) -> {
            List<String> keys = new ArrayList<>();
            if (0 == k % 2) {
                keys.add("evenKey");
            } else {
                keys.add("oddKey");
            }
            if (10 > k) {
                keys.add("smaller10Key");
            } else {
                keys.add("greaterEqual10Key");
            }
            return keys;
        };

        Map<String, Map<Integer, String>> expectedResult = new HashMap<>() {{
            put("evenKey",
                    new HashMap<>() {{
                        put(2, "Hello");
                    }}
            );
            put("oddKey",
                    new HashMap<>() {{
                        put(1, "Hi");
                        put(11, "World");
                    }}
            );
            put("smaller10Key",
                    new HashMap<>() {{
                        put(1, "Hi");
                        put(2, "Hello");
                    }}
            );
            put("greaterEqual10Key",
                    new HashMap<>() {{
                        put(11, "World");
                    }}
            );
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,   discriminatorKey,             expectedException,                expectedResult
                Arguments.of( null,        null,                         null,                             Map.of() ),
                Arguments.of( null,        oddEvenAndCompareWith10Key,   null,                             Map.of() ),
                Arguments.of( Map.of(),    null,                         null,                             Map.of() ),
                Arguments.of( Map.of(),    oddEvenAndCompareWith10Key,   null,                             Map.of() ),
                Arguments.of( sourceMap,   null,                         IllegalArgumentException.class,   null ),
                Arguments.of( sourceMap,   oddEvenAndCompareWith10Key,   null,                             expectedResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("groupByMultiKeyNoMapFactoryTestCases")
    @DisplayName("groupByMultiKey: without map factory test cases")
    public <T, E, R> void groupByMultiKeyNoMapFactory_testCases(Map<? extends T, ? extends E> sourceMap,
                                                                BiFunction<? super T, ? super E, Collection<? extends R>> discriminatorKey,
                                                                Class<? extends Exception> expectedException,
                                                                Map<R, Map<T, E>> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> groupByMultiKey(sourceMap, discriminatorKey));
        } else {
            assertEquals(expectedResult, groupByMultiKey(sourceMap, discriminatorKey));
        }
    }


    static Stream<Arguments> groupByMultiKeyAllParametersTestCases() {
        Map<Integer, String> sourceMap = new HashMap<>() {{
            put(1, "Hi");
            put(2, "Hello");
            put(11, "World");
        }};
        BiFunction<Integer, String, List<String>> oddEvenAndCompareWith10Key = (k, v) -> {
            List<String> keys = new ArrayList<>();
            if (0 == k % 2) {
                keys.add("evenKey");
            } else {
                keys.add("oddKey");
            }
            if (10 > k) {
                keys.add("smaller10Key");
            } else {
                keys.add("greaterEqual10Key");
            }
            return keys;
        };
        Supplier<Map<String, String>> linkedMapSupplier = LinkedHashMap::new;

        Map<String, Map<Integer, String>> expectedResultWithoutMapFactories = new HashMap<>() {{
            put("evenKey",
                    new HashMap<>() {{
                        put(2, "Hello");
                    }}
            );
            put("oddKey",
                    new HashMap<>() {{
                        put(1, "Hi");
                        put(11, "World");
                    }}
            );
            put("smaller10Key",
                    new HashMap<>() {{
                        put(1, "Hi");
                        put(2, "Hello");
                    }}
            );
            put("greaterEqual10Key",
                    new HashMap<>() {{
                        put(11, "World");
                    }}
            );
        }};
        Map<String, Map<Integer, String>> expectedResultWithMapFactories = new LinkedHashMap<>() {{
            put("evenKey",
                    new LinkedHashMap<>() {{
                        put(2, "Hello");
                    }}
            );
            put("oddKey",
                    new LinkedHashMap<>() {{
                        put(1, "Hi");
                        put(11, "World");
                    }}
            );
            put("smaller10Key",
                    new LinkedHashMap<>() {{
                        put(1, "Hi");
                        put(2, "Hello");
                    }}
            );
            put("greaterEqual10Key",
                    new LinkedHashMap<>() {{
                        put(11, "World");
                    }}
            );
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,   discriminatorKey,             mapResultFactory,    mapValuesFactory,    expectedException,                expectedResult
                Arguments.of( null,        null,                         null,                null,                null,                             Map.of() ),
                Arguments.of( null,        oddEvenAndCompareWith10Key,   null,                null,                null,                             Map.of() ),
                Arguments.of( null,        oddEvenAndCompareWith10Key,   linkedMapSupplier,   null,                null,                             new LinkedHashMap<>() ),
                Arguments.of( null,        oddEvenAndCompareWith10Key,   null,                linkedMapSupplier,   null,                             Map.of() ),
                Arguments.of( null,        oddEvenAndCompareWith10Key,   linkedMapSupplier,   linkedMapSupplier,   null,                             new LinkedHashMap<>() ),
                Arguments.of( Map.of(),    null,                         null,                null,                null,                             Map.of() ),
                Arguments.of( Map.of(),    oddEvenAndCompareWith10Key,   null,                null,                null,                             Map.of() ),
                Arguments.of( Map.of(),    oddEvenAndCompareWith10Key,   linkedMapSupplier,   null,                null,                             new LinkedHashMap<>() ),
                Arguments.of( Map.of(),    oddEvenAndCompareWith10Key,   null,                linkedMapSupplier,   null,                             Map.of() ),
                Arguments.of( Map.of(),    oddEvenAndCompareWith10Key,   linkedMapSupplier,   linkedMapSupplier,   null,                             new LinkedHashMap<>() ),
                Arguments.of( sourceMap,   null,                         null,                null,                IllegalArgumentException.class,   expectedResultWithoutMapFactories ),
                Arguments.of( sourceMap,   null,                         linkedMapSupplier,   null,                IllegalArgumentException.class,   expectedResultWithoutMapFactories ),
                Arguments.of( sourceMap,   null,                         linkedMapSupplier,   linkedMapSupplier,   IllegalArgumentException.class,   expectedResultWithoutMapFactories ),
                Arguments.of( sourceMap,   oddEvenAndCompareWith10Key,   null,                null,                null,                             expectedResultWithoutMapFactories ),
                Arguments.of( sourceMap,   oddEvenAndCompareWith10Key,   linkedMapSupplier,   linkedMapSupplier,   null,                             expectedResultWithMapFactories )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("groupByMultiKeyAllParametersTestCases")
    @DisplayName("groupByMultiKey: with all parameters test cases")
    public <T, E, R> void groupByMultiKeyAllParameters_testCases(Map<? extends T, ? extends E> sourceMap,
                                                                 BiFunction<? super T, ? super E, Collection<? extends R>> discriminatorKey,
                                                                 Supplier<Map<R, Map<T, E>>> mapResultFactory,
                                                                 Supplier<Map<T, E>> mapValuesFactory,
                                                                 Class<? extends Exception> expectedException,
                                                                 Map<R, Map<T, E>> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> groupByMultiKey(sourceMap, discriminatorKey, mapResultFactory, mapValuesFactory));
        } else {
            assertEquals(expectedResult, groupByMultiKey(sourceMap, discriminatorKey, mapResultFactory, mapValuesFactory));
        }
    }


    static Stream<Arguments> groupMapWithPartialFunctionTestCases() {
        Map<String, Integer> stringsAndIntegers = new LinkedHashMap<>() {{
            put("A", 10);
            put("BY", 20);
            put("C", 30);
            put("DH", 40);
            put("GHT", 55);
        }};
        PartialFunction<Map.Entry<String, Integer>, Map.Entry<Integer, Integer>> partialFunction = PartialFunction.of(
                e -> null != e &&
                        3 > e.getKey().length() &&
                        50 > e.getValue(),
                e -> new AbstractMap.SimpleEntry<>(
                        e.getKey().length(),
                        e.getValue() + 5
                     )
        );
        Map<Integer, List<Integer>> expectedResult = new HashMap<>() {{
            put(1, List.of(15, 35));
            put(2, List.of(25, 45));
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,            partialFunction,   expectedException,                expectedResult
                Arguments.of( null,                 null,              null,                             Map.of() ),
                Arguments.of( Map.of(),             null,              null,                             Map.of() ),
                Arguments.of( stringsAndIntegers,   null,              IllegalArgumentException.class,   null ),
                Arguments.of( null,                 partialFunction,   null,                             Map.of() ),
                Arguments.of( Map.of(),             partialFunction,   null,                             Map.of() ),
                Arguments.of( stringsAndIntegers,   partialFunction,   null,                             expectedResult )
        ); //@formatter:on
    }


    static Stream<Arguments> groupMapWithFunctionsTestCases() {
        Map<Integer, String> intsAndStrings = new LinkedHashMap<>() {{
            put(1, "Hi");
            put(2, "Hello");
            put(5, "World");
            put(6, "!");
        }};
        BiFunction<Integer, String, Integer> keyMod3 = (k, v) -> k % 3;
        BiFunction<Integer, String, Integer> valueLength = (k, v) -> v.length();
        Map<Integer, List<Integer>> usingKeyMod3AsDiscriminatorKey = new HashMap<>() {{
            put(0, List.of(1));
            put(1, List.of(2));
            put(2, List.of(5, 5));
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,        discriminatorKey,   valueMapper,   expectedException,                expectedResult
                Arguments.of( null,             null,               null,          null,                             Map.of() ),
                Arguments.of( Map.of(),         null,               null,          null,                             Map.of() ),
                Arguments.of( intsAndStrings,   null,               null,          IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   keyMod3,            null,          IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   null,               valueLength,   IllegalArgumentException.class,   null ),
                Arguments.of( null,             keyMod3,            valueLength,   null,                             Map.of() ),
                Arguments.of( Map.of(),         keyMod3,            valueLength,   null,                             Map.of() ),
                Arguments.of( intsAndStrings,   keyMod3,            valueLength,   null,                             usingKeyMod3AsDiscriminatorKey )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("groupMapWithFunctionsTestCases")
    @DisplayName("groupMap: with discriminatorKey and valueMapper test cases")
    public <T, E, R, V> void groupMapWithFunctions_testCases(Map<? extends T, ? extends E> sourceMap,
                                                             BiFunction<? super T, ? super E, ? extends R> discriminatorKey,
                                                             BiFunction<? super T, ? super E, ? extends V> valueMapper,
                                                             Class<? extends Exception> expectedException,
                                                             Map<R, List<V>> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> groupMap(sourceMap, discriminatorKey, valueMapper));
        } else {
            assertEquals(expectedResult, groupMap(sourceMap, discriminatorKey, valueMapper));
        }
    }


    static Stream<Arguments> groupMapWithPredicateAndFunctionsTestCases() {
        Map<String, Integer> stringsAndIntegers = new LinkedHashMap<>() {{
            put("A", 10);
            put("BY", 20);
            put("C", 30);
            put("DH", 40);
            put("GHT", 55);
        }};
        BiPredicate<String, Integer> keySmallerThan3AndValueLowerThan50 = (k, v) -> 3 > k.length() && 50 > v;
        BiFunction<String, Integer, Integer> keyLength = (k, v) -> k.length();
        BiFunction<String, Integer, Integer> valuePlus5 = (k, v) -> v + 5;

        Map<Integer, List<Integer>> resultNoFilter = new HashMap<>() {{
            put(1, List.of(15, 35));
            put(2, List.of(25, 45));
            put(3, List.of(60));
        }};
        Map<Integer, List<Integer>> resultFilter = new HashMap<>() {{
            put(1, List.of(15, 35));
            put(2, List.of(25, 45));
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,            filterPredicate,                      discriminatorKey,   valueMapper,   expectedException,                expectedResult
                Arguments.of( null,                 null,                                 null,               null,          null,                             Map.of() ),
                Arguments.of( Map.of(),             null,                                 null,               null,          null,                             Map.of() ),
                Arguments.of( stringsAndIntegers,   null,                                 null,               null,          IllegalArgumentException.class,   null ),
                Arguments.of( stringsAndIntegers,   keySmallerThan3AndValueLowerThan50,   null,               null,          IllegalArgumentException.class,   null ),
                Arguments.of( stringsAndIntegers,   keySmallerThan3AndValueLowerThan50,   keyLength,          null,          IllegalArgumentException.class,   null ),
                Arguments.of( stringsAndIntegers,   keySmallerThan3AndValueLowerThan50,   null,               valuePlus5,    IllegalArgumentException.class,   null ),
                Arguments.of( null,                 null,                                 keyLength,          valuePlus5,    null,                             Map.of() ),
                Arguments.of( null,                 keySmallerThan3AndValueLowerThan50,   keyLength,          valuePlus5,    null,                             Map.of() ),
                Arguments.of( Map.of(),             null,                                 keyLength,          valuePlus5,    null,                             Map.of() ),
                Arguments.of( Map.of(),             keySmallerThan3AndValueLowerThan50,   keyLength,          valuePlus5,    null,                             Map.of() ),
                Arguments.of( stringsAndIntegers,   null,                                 keyLength,          valuePlus5,    null,                             resultNoFilter ),
                Arguments.of( stringsAndIntegers,   keySmallerThan3AndValueLowerThan50,   keyLength,          valuePlus5,    null,                             resultFilter )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("groupMapWithPredicateAndFunctionsTestCases")
    @DisplayName("groupMap: with filterPredicate, discriminatorKey and valueMapper test cases")
    public <K1, K2, V1, V2> void groupMapWithPredicateAndFunctions_testCases(Map<? extends K1, ? extends V1> sourceMap,
                                                                             BiPredicate<? super K1, ? super V1> filterPredicate,
                                                                             BiFunction<? super K1, ? super V1, ? extends K2> discriminatorKey,
                                                                             BiFunction<? super K1, ? super V1, ? extends V2> valueMapper,
                                                                             Class<? extends Exception> expectedException,
                                                                             Map<K2, Collection<V2>> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> groupMap(sourceMap, filterPredicate, discriminatorKey, valueMapper));
        } else {
            assertEquals(expectedResult, groupMap(sourceMap, filterPredicate, discriminatorKey, valueMapper));
        }
    }


    static Stream<Arguments> groupMapWithPredicateFunctionsAndSupplierTestCases() {
        Map<String, Integer> stringsAndIntegers = new LinkedHashMap<>() {{
            put("A", 10);
            put("BY", 20);
            put("C", 30);
            put("DH", 40);
            put("GHT", 55);
        }};
        BiPredicate<String, Integer> keySmallerThan3AndValueLowerThan50 = (k, v) -> 3 > k.length() && 50 > v;
        BiFunction<String, Integer, Integer> keyLength = (k, v) -> k.length();
        BiFunction<String, Integer, Integer> valuePlus5 = (k, v) -> v + 5;
        Supplier<Collection<String>> setSupplier = LinkedHashSet::new;

        Map<Integer, List<Integer>> resultNoFilterWithDefaultCollectionFactory = new HashMap<>() {{
            put(1, List.of(15, 35));
            put(2, List.of(25, 45));
            put(3, List.of(60));
        }};
        Map<Integer, List<Integer>> resultFilterWithDefaultCollectionFactory = new HashMap<>() {{
            put(1, List.of(15, 35));
            put(2, List.of(25, 45));
        }};
        Map<Integer, Set<Integer>> resultNoFilterWithSetCollectionFactory = new HashMap<>() {{
            put(1, new LinkedHashSet<>(List.of(15, 35)));
            put(2, new LinkedHashSet<>(List.of(25, 45)));
            put(3, new LinkedHashSet<>(List.of(60)));
        }};
        Map<Integer, Set<Integer>> resultFilterWithSetCollectionFactory = new HashMap<>() {{
            put(1, new LinkedHashSet<>(List.of(15, 35)));
            put(2, new LinkedHashSet<>(List.of(25, 45)));
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,            filterPredicate,                      discriminatorKey,   valueMapper,   collectionFactory,   expectedException,                expectedResult
                Arguments.of( null,                 null,                                 null,               null,          null,                null,                             Map.of() ),
                Arguments.of( null,                 null,                                 null,               null,          setSupplier,         null,                             Map.of() ),
                Arguments.of( Map.of(),             null,                                 null,               null,          null,                null,                             Map.of() ),
                Arguments.of( Map.of(),             null,                                 null,               null,          setSupplier,         null,                             Map.of() ),
                Arguments.of( stringsAndIntegers,   null,                                 null,               null,          null,                IllegalArgumentException.class,   null ),
                Arguments.of( stringsAndIntegers,   keySmallerThan3AndValueLowerThan50,   null,               null,          null,                IllegalArgumentException.class,   null ),
                Arguments.of( stringsAndIntegers,   keySmallerThan3AndValueLowerThan50,   keyLength,          null,          null,                IllegalArgumentException.class,   null ),
                Arguments.of( stringsAndIntegers,   keySmallerThan3AndValueLowerThan50,   null,               valuePlus5,    null,                IllegalArgumentException.class,   null ),
                Arguments.of( null,                 null,                                 keyLength,          valuePlus5,    null,                null,                             Map.of() ),
                Arguments.of( null,                 keySmallerThan3AndValueLowerThan50,   keyLength,          valuePlus5,    null,                null,                             Map.of() ),
                Arguments.of( Map.of(),             null,                                 keyLength,          valuePlus5,    null,                null,                             Map.of() ),
                Arguments.of( Map.of(),             keySmallerThan3AndValueLowerThan50,   keyLength,          valuePlus5,    null,                null,                             Map.of() ),
                Arguments.of( stringsAndIntegers,   null,                                 keyLength,          valuePlus5,    null,                null,                             resultNoFilterWithDefaultCollectionFactory ),
                Arguments.of( stringsAndIntegers,   keySmallerThan3AndValueLowerThan50,   keyLength,          valuePlus5,    null,                null,                             resultFilterWithDefaultCollectionFactory ),
                Arguments.of( stringsAndIntegers,   null,                                 keyLength,          valuePlus5,    setSupplier,         null,                             resultNoFilterWithSetCollectionFactory ),
                Arguments.of( stringsAndIntegers,   keySmallerThan3AndValueLowerThan50,   keyLength,          valuePlus5,    setSupplier,         null,                             resultFilterWithSetCollectionFactory )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("groupMapWithPredicateFunctionsAndSupplierTestCases")
    @DisplayName("groupMap: with filterPredicate, discriminatorKey, valueMapper and collectionFactory test cases")
    public <K1, K2, V1, V2> void groupMapWithPredicateFunctionsAndSupplier_testCases(Map<? extends K1, ? extends V1> sourceMap,
                                                                                     BiPredicate<? super K1, ? super V1> filterPredicate,
                                                                                     BiFunction<? super K1, ? super V1, ? extends K1> discriminatorKey,
                                                                                     BiFunction<? super K1, ? super V1, ? extends V2> valueMapper,
                                                                                     Supplier<Collection<V2>> collectionFactory,
                                                                                     Class<? extends Exception> expectedException,
                                                                                     Map<K2, Collection<V2>> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> groupMap(sourceMap, filterPredicate, discriminatorKey, valueMapper, collectionFactory));
        } else {
            assertEquals(expectedResult, groupMap(sourceMap, filterPredicate, discriminatorKey, valueMapper, collectionFactory));
        }
    }


    @ParameterizedTest
    @MethodSource("groupMapWithPartialFunctionTestCases")
    @DisplayName("groupMap: with partialFunction test cases")
    public <K1, K2, V1, V2> void groupMapWithPartialFunction_testCases(Map<? extends K1, ? extends V1> sourceMap,
                                                                       PartialFunction<? super Map.Entry<K1, V1>, ? extends Map.Entry<K2, V2>> partialFunction,
                                                                       Class<? extends Exception> expectedException,
                                                                       Map<K2, Collection<V2>> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> groupMap(sourceMap, partialFunction));
        } else {
            assertEquals(expectedResult, groupMap(sourceMap, partialFunction));
        }
    }


    static Stream<Arguments> groupMapWithPartialFunctionAndSupplierTestCases() {
        Map<String, Integer> stringsAndIntegers = new LinkedHashMap<>() {{
            put("A", 10);
            put("BY", 20);
            put("C", 30);
            put("DH", 40);
            put("GHT", 55);
        }};
        Supplier<Collection<Integer>> setSupplier = LinkedHashSet::new;

        PartialFunction<Map.Entry<String, Integer>, Map.Entry<Integer, Integer>> partialFunction = PartialFunction.of(
                e -> null != e &&
                        3 > e.getKey().length() &&
                        50 > e.getValue(),
                e -> new AbstractMap.SimpleEntry<>(
                        e.getKey().length(),
                        e.getValue() + 5
                )
        );
        Map<Integer, List<Integer>> expectedResultDefaultCollectionFactory = new HashMap<>() {{
            put(1, List.of(15, 35));
            put(2, List.of(25, 45));
        }};
        Map<Integer, Set<Integer>> expectedResultSetCollectionFactory = new HashMap<>() {{
            put(1, new LinkedHashSet<>(List.of(15, 35)));
            put(2, new LinkedHashSet<>(List.of(25, 45)));
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,            partialFunction,   collectionFactory,   expectedException,                expectedResult
                Arguments.of( null,                 null,              null,                null,                             Map.of() ),
                Arguments.of( null,                 null,              setSupplier,         null,                             Map.of() ),
                Arguments.of( Map.of(),             null,              null,                null,                             Map.of() ),
                Arguments.of( Map.of(),             null,              setSupplier,         null,                             Map.of() ),
                Arguments.of( stringsAndIntegers,   null,              null,                IllegalArgumentException.class,   null ),
                Arguments.of( stringsAndIntegers,   null,              setSupplier,         IllegalArgumentException.class,   null ),
                Arguments.of( null,                 partialFunction,   null,                null,                             Map.of() ),
                Arguments.of( null,                 partialFunction,   setSupplier,         null,                             Map.of() ),
                Arguments.of( Map.of(),             partialFunction,   null,                null,                             Map.of() ),
                Arguments.of( Map.of(),             partialFunction,   setSupplier,         null,                             Map.of() ),
                Arguments.of( stringsAndIntegers,   partialFunction,   null,                null,                             expectedResultDefaultCollectionFactory ),
                Arguments.of( stringsAndIntegers,   partialFunction,   setSupplier,         null,                             expectedResultSetCollectionFactory )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("groupMapWithPartialFunctionAndSupplierTestCases")
    @DisplayName("groupMap: with partialFunction and collectionFactory test cases")
    public <K1, K2, V1, V2> void groupMapWithPartialFunctionAndSupplier_testCases(Map<? extends K1, ? extends V1> sourceMap,
                                                                                  PartialFunction<? super Map.Entry<K1, V1>, ? extends Map.Entry<K2, V2>> partialFunction,
                                                                                  Supplier<Collection<V2>> collectionFactory,
                                                                                  Class<? extends Exception> expectedException,
                                                                                  Map<K2, Collection<V2>> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> groupMap(sourceMap, partialFunction, collectionFactory));
        } else {
            assertEquals(expectedResult, groupMap(sourceMap, partialFunction, collectionFactory));
        }
    }


    static Stream<Arguments> groupMapReduceWithBiFunctionsAndBinaryOperatorTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "AB");
            put(2, "BCD");
            put(3, "Z3");
            put(4, "oPQRT");
        }};
        BiFunction<Integer, String, Integer> keyMod2 = (k, v) -> k % 2;
        BiFunction<Integer, String, Integer> valueLength = (k, v) -> v.length();
        BinaryOperator<Integer> multiplyAll = (i1, i2) -> i1 * i2;
        Map<Integer, Integer> expectedResult = new HashMap<>() {{
            put(0, 15);
            put(1, 4);
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,        discriminatorKey,   valueMapper,   reduceValues,   expectedException,                expectedResult
                Arguments.of( null,             null,               null,          null,           null,                             Map.of() ),
                Arguments.of( null,             keyMod2,            null,          null,           null,                             Map.of() ),
                Arguments.of( null,             keyMod2,            valueLength,   null,           null,                             Map.of() ),
                Arguments.of( null,             keyMod2,            valueLength,   multiplyAll,    null,                             Map.of() ),
                Arguments.of( Map.of(),         null,               null,          null,           null,                             Map.of() ),
                Arguments.of( Map.of(),         keyMod2,            null,          null,           null,                             Map.of() ),
                Arguments.of( Map.of(),         keyMod2,            valueLength,   null,           null,                             Map.of() ),
                Arguments.of( Map.of(),         keyMod2,            valueLength,   multiplyAll,    null,                             Map.of() ),
                Arguments.of( intsAndStrings,   null,               null,          null,           IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   keyMod2,            null,          null,           IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   keyMod2,            valueLength,   null,           IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   keyMod2,            valueLength,   multiplyAll,    null,                             expectedResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("groupMapReduceWithBiFunctionsAndBinaryOperatorTestCases")
    @DisplayName("groupMapReduce: with discriminatorKey, valueMapper and reduceValues test cases")
    public <K1, K2, V1, V2> void groupMapReduceWithBiFunctionsAndBinaryOperator_testCases(Map<? extends K1, ? extends V1> sourceMap,
                                                                                          BiFunction<? super K1, ? super V1, ? extends K2> discriminatorKey,
                                                                                          BiFunction<? super K1, ? super V1, V2> valueMapper,
                                                                                          BinaryOperator<V2> reduceValues,
                                                                                          Class<? extends Exception> expectedException,
                                                                                          Map<K2, V2> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> groupMapReduce(sourceMap, discriminatorKey, valueMapper, reduceValues));
        } else {
            assertEquals(expectedResult, groupMapReduce(sourceMap, discriminatorKey, valueMapper, reduceValues));
        }
    }


    static Stream<Arguments> groupMapReduceWithPartialFunctionAndBinaryOperatorTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "Hi");
            put(2, "Hola");
            put(4, "");
            put(5, "World");
            put(6, "!");
            put(11, "ABC");
        }};
        PartialFunction<Map.Entry<Integer, String>, Map.Entry<Integer, Integer>> partialFunction = PartialFunction.of(
                e -> null != e && 10 > e.getKey(),
                e -> null == e
                        ? null
                        : new AbstractMap.SimpleEntry<>(
                                e.getKey() % 3,
                                null == e.getValue()
                                       ? 0
                                       : e.getValue().length() + 1
                               )
        );
        BinaryOperator<Integer> sumAll = Integer::sum;
        Map<Integer, Integer> expectedResult = new HashMap<>() {{
            put(0, 2);
            put(1, 4);
            put(2, 11);
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,        partialFunction,   reduceValues,   expectedException,                expectedResult
                Arguments.of( null,             null,              null,           null,                             Map.of() ),
                Arguments.of( null,             partialFunction,   null,           null,                             Map.of() ),
                Arguments.of( null,             partialFunction,   sumAll,         null,                             Map.of() ),
                Arguments.of( Map.of(),         null,              null,           null,                             Map.of() ),
                Arguments.of( Map.of(),         partialFunction,   null,           null,                             Map.of() ),
                Arguments.of( Map.of(),         partialFunction,   sumAll,         null,                             Map.of() ),
                Arguments.of( intsAndStrings,   null,              null,           IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   partialFunction,   null,           IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   null,              sumAll,         IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   partialFunction,   sumAll,         null,                             expectedResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("groupMapReduceWithPartialFunctionAndBinaryOperatorTestCases")
    @DisplayName("groupMapReduce: with partialFunction and reduceValues test cases")
    public <K1, K2, V1, V2> void groupMapReduceWithPartialFunctionAndBinaryOperator_testCases(Map<? extends K1, ? extends V1> sourceMap,
                                                                                              PartialFunction<? super Map.Entry<K1, V1>, ? extends Map.Entry<K2, V2>> partialFunction,
                                                                                              BinaryOperator<V2> reduceValues,
                                                                                              Class<? extends Exception> expectedException,
                                                                                              Map<K2, V2> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> groupMapReduce(sourceMap, partialFunction, reduceValues));
        } else {
            assertEquals(expectedResult, groupMapReduce(sourceMap, partialFunction, reduceValues));
        }
    }


    static Stream<Arguments> mapNoMapFactoryTestCases() {
        Map<Integer, Integer> integersMap = new HashMap<>() {{
            put(1, 21);
            put(4, 43);
            put(9, 101);
        }};
        BiFunction<Integer, Integer, Map.Entry<String, String>> add1AndMultiply2AndConvertToString =
                (k, v) ->
                        new AbstractMap.SimpleEntry<>(
                                String.valueOf(k + 1),
                                String.valueOf(v * 2)
                        );
        Map<String, String> stringsMapResult = new HashMap<>() {{
            put("2", "42");
            put("5", "86");
            put("10", "202");
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,         mapFunction,                          expectedException,                expectedResult
                Arguments.of( integersMap,       null,                                 IllegalArgumentException.class,   null ),
                Arguments.of( null,              null,                                 null,                             Map.of() ),
                Arguments.of( Map.of(),          null,                                 null,                             Map.of() ),
                Arguments.of( null,              add1AndMultiply2AndConvertToString,   null,                             Map.of() ),
                Arguments.of( Map.of(),          add1AndMultiply2AndConvertToString,   null,                             Map.of() ),
                Arguments.of( integersMap,       add1AndMultiply2AndConvertToString,   null,                             stringsMapResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapNoMapFactoryTestCases")
    @DisplayName("map: without map factory test cases")
    public <T, E, R, V> void mapNoMapFactory_testCases(Map<? extends T, ? extends E> sourceMap,
                                                       BiFunction<? super T, ? super E, Map.Entry<? extends R, ? extends V>> mapFunction,
                                                       Class<? extends Exception> expectedException,
                                                       Map<R, V> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> map(sourceMap, mapFunction));
        } else {
            assertEquals(expectedResult, map(sourceMap, mapFunction));
        }
    }


    static Stream<Arguments> mapAllParametersTestCases() {
        Map<Integer, Integer> integersMap = new HashMap<>() {{
            put(1, 21);
            put(4, 43);
            put(9, 101);
            put(11, null);
        }};
        BiFunction<Integer, Integer, Map.Entry<String, String>> add1AndMultiply2AndConvertToString =
                (k, v) ->
                        new AbstractMap.SimpleEntry<>(
                                String.valueOf(k + 1),
                                null == v
                                        ? null
                                        : String.valueOf(v * 2)
                        );
        Supplier<Map<String, String>> linkedMapSupplier = LinkedHashMap::new;
        Map<String, String> stringsMapResult = new HashMap<>() {{
            put("2", "42");
            put("5", "86");
            put("10", "202");
            put("12", null);
        }};
        LinkedHashMap<String, String> stringsLinkedMapResult = new LinkedHashMap<>(stringsMapResult);
        return Stream.of(
                //@formatter:off
                //            sourceMap,         mapFunction,                          mapFactory,          expectedException,                expectedResult
                Arguments.of( integersMap,       null,                                 null,                IllegalArgumentException.class,   null ),
                Arguments.of( null,              null,                                 null,                null,                             Map.of() ),
                Arguments.of( Map.of(),          null,                                 null,                null,                             Map.of() ),
                Arguments.of( null,              add1AndMultiply2AndConvertToString,   null,                null,                             Map.of() ),
                Arguments.of( Map.of(),          add1AndMultiply2AndConvertToString,   null,                null,                             Map.of() ),
                Arguments.of( integersMap,       add1AndMultiply2AndConvertToString,   null,                null,                             stringsMapResult ),
                Arguments.of( integersMap,       add1AndMultiply2AndConvertToString,   linkedMapSupplier,   null,                             stringsLinkedMapResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapAllParametersTestCases")
    @DisplayName("map: with all parameters test cases")
    public <T, E, R, V> void mapAllParameters_testCases(Map<? extends T, ? extends E> sourceMap,
                                                        BiFunction<? super T, ? super E, Map.Entry<? extends R, ? extends V>> mapFunction,
                                                        Supplier<Map<R, V>> mapFactory,
                                                        Class<? extends Exception> expectedException,
                                                        Map<R, V> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> map(sourceMap, mapFunction, mapFactory));
        } else {
            assertEquals(expectedResult, map(sourceMap, mapFunction, mapFactory));
        }
    }


    static Stream<Arguments> mapValuesNoMapFactoryTestCases() {
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
                Arguments.of( integersMap,       null,                            IllegalArgumentException.class,   null ),
                Arguments.of( null,              null,                            null,                             Map.of() ),
                Arguments.of( Map.of(),          null,                            null,                             Map.of() ),
                Arguments.of( Map.of(),          add1ToValueAndConvertToString,   null,                             Map.of() ),
                Arguments.of( integersMap,       add1ToValueAndConvertToString,   null,                             integersMapResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapValuesNoMapFactoryTestCases")
    @DisplayName("mapValues: without map factory test cases")
    public <T, E, R> void mapValuesNoMapFactory_testCases(Map<? extends T, ? extends E> sourceMap,
                                                          BiFunction<? super T, ? super E, ? extends R> mapFunction,
                                                          Class<? extends Exception> expectedException,
                                                          Map<T, R> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> mapValues(sourceMap, mapFunction));
        } else {
            assertEquals(expectedResult, mapValues(sourceMap, mapFunction));
        }
    }


    static Stream<Arguments> mapValuesAllParametersTestCases() {
        Map<Integer, Integer> integersMap = new HashMap<>() {{
            put(1, 21);
            put(4, 43);
            put(9, 101);
            put(12, null);
        }};
        BiFunction<Integer, Integer, String> add1ToValueAndConvertToString = (k, v) ->
                null == v
                   ? null
                   : String.valueOf(v + 1);

        Supplier<Map<Integer, String>> linkedMapSupplier = LinkedHashMap::new;
        Map<Integer, String> integersMapResult = new HashMap<>() {{
            put(1, "22");
            put(4, "44");
            put(9, "102");
            put(12, null);
        }};
        LinkedHashMap<Integer, String> integersLinkedMapResult = new LinkedHashMap<>(integersMapResult);
        return Stream.of(
                //@formatter:off
                //            sourceMap,         mapFunction,                     mapFactory,          expectedException,                expectedResult
                Arguments.of( integersMap,       null,                            null,                IllegalArgumentException.class,   null ),
                Arguments.of( null,              null,                            null,                null,                             Map.of() ),
                Arguments.of( Map.of(),          null,                            null,                null,                             Map.of() ),
                Arguments.of( Map.of(),          add1ToValueAndConvertToString,   null,                null,                             Map.of() ),
                Arguments.of( integersMap,       add1ToValueAndConvertToString,   null,                null,                             integersMapResult ),
                Arguments.of( integersMap,       add1ToValueAndConvertToString,   linkedMapSupplier,   null,                             integersLinkedMapResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("mapValuesAllParametersTestCases")
    @DisplayName("mapValues: with all parameters test cases")
    public <T, E, R> void mapValuesAllParameters_testCases(Map<? extends T, ? extends E> sourceMap,
                                                           BiFunction<? super T, ? super E, ? extends R> mapFunction,
                                                           Supplier<Map<T, R>> mapFactory,
                                                           Class<? extends Exception> expectedException,
                                                           Map<T, R> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> mapValues(sourceMap, mapFunction, mapFactory));
        } else {
            assertEquals(expectedResult, mapValues(sourceMap, mapFunction, mapFactory));
        }
    }


    static Stream<Arguments> maxTestCases() {
        Map<Integer, String> intsAndStrings = new LinkedHashMap<>() {{
            put(1, "AB");
            put(2, "Z3");
            put(3, "AB");
            put(4, "UTf");
        }};
        Comparator<Map.Entry<Integer, String>> comparatorOnlyKeys = Map.Entry.comparingByKey();
        Comparator<Map.Entry<Integer, String>> comparatorOnlyValues = Map.Entry.comparingByValue();
        Comparator<Map.Entry<Integer, String>> comparatorBoth = (e1, e2) -> {
            Comparator<String> valueComparator = safeNaturalOrderNullLast();   // Required because sometimes the Java compiler is stupid
            int valueComparison = valueComparator.compare(e1.getValue(), e2.getValue());
            return 0 != valueComparison
                    ? e1.getKey().compareTo(e2.getKey())
                    : valueComparison;
        };
        return Stream.of(
                //@formatter:off
                //            sourceMap,         comparator,             expectedException,                expectedResult
                Arguments.of( null,              null,                   null,                             empty() ),
                Arguments.of( intsAndStrings,    null,                   IllegalArgumentException.class,   null ),
                Arguments.of( null,              comparatorOnlyKeys,     null,                             empty() ),
                Arguments.of( new HashMap<>(),   comparatorOnlyKeys,     null,                             empty() ),
                Arguments.of( intsAndStrings,    comparatorOnlyKeys,     null,                             of(Map.entry(4, "UTf")) ),
                Arguments.of( intsAndStrings,    comparatorOnlyValues,   null,                             of(Map.entry(2, "Z3")) ),
                Arguments.of( intsAndStrings,    comparatorBoth,         null,                             of(Map.entry(4, "UTf")) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("maxTestCases")
    @DisplayName("max: test cases")
    public <T, E> void max_testCases(Map<? extends T, ? extends E> sourceMap,
                                     Comparator<Map.Entry<? extends T, ? extends E>> comparator,
                                     Class<? extends Exception> expectedException,
                                     Optional<Map.Entry<T, E>> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> max(sourceMap, comparator));
        } else {
            assertEquals(expectedResult, max(sourceMap, comparator));
        }
    }


    static Stream<Arguments> maxValueNoComparatorTestCases() {
        Map<Integer, String> intsAndStrings = new LinkedHashMap<>() {{
            put(1, "AB");
            put(2, "Z3");
            put(3, "AB");
        }};
        Map<Integer, String> intsAndStringsWithNullValues = new LinkedHashMap<>() {{
            put(1, "AB");
            put(2, null);
            put(3, "AB");
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,   expectedResult
                Arguments.of( null,                           empty() ),
                Arguments.of( new HashMap<>(),                empty() ),
                Arguments.of( intsAndStrings,                 of("Z3") ),
                Arguments.of( intsAndStringsWithNullValues,   of("AB") )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("maxValueNoComparatorTestCases")
    @DisplayName("maxValue: without comparator test cases")
    public <T, E extends Comparable<? super E>> void maxValueNoComparator_testCases(Map<? extends T, ? extends E> sourceMap,
                                                                                    Optional<E> expectedResult) {
        assertEquals(expectedResult, maxValue(sourceMap));
    }


    static Stream<Arguments> maxValueAllParametersTestCases() {
        Map<Integer, String> intsAndStrings = new LinkedHashMap<>() {{
            put(1, "AB");
            put(2, "Z3");
            put(3, "AB");
            put(4, "UTf");
        }};
        Comparator<String> comparatorNatural = String::compareTo;
        Comparator<String> comparatorReverse = Comparator.reverseOrder();
        return Stream.of(
                //@formatter:off
                //            sourceMap,         comparator,          expectedException,                expectedResult
                Arguments.of( null,              null,                IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,    null,                IllegalArgumentException.class,   null ),
                Arguments.of( null,              comparatorNatural,   null,                             empty() ),
                Arguments.of( new HashMap<>(),   comparatorNatural,   null,                             empty() ),
                Arguments.of( intsAndStrings,    comparatorNatural,   null,                             of("Z3") ),
                Arguments.of( intsAndStrings,    comparatorReverse,   null,                             of("AB") )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("maxValueAllParametersTestCases")
    @DisplayName("maxValue: with all parameters test cases")
    public <T, E> void maxValueAllParameters_testCases(Map<? extends T, ? extends E> sourceMap,
                                                       Comparator<? super E> comparator,
                                                       Class<? extends Exception> expectedException,
                                                       Optional<Tuple2<T, E>> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> maxValue(sourceMap, comparator));
        } else {
            assertEquals(expectedResult, maxValue(sourceMap, comparator));
        }
    }


    static Stream<Arguments> minTestCases() {
        Map<Integer, String> intsAndStrings = new LinkedHashMap<>() {{
            put(1, "TT");
            put(2, "BA");
            put(3, "Urf");
            put(4, "BA");
        }};
        Comparator<Map.Entry<Integer, String>> comparatorOnlyKeys = Map.Entry.comparingByKey();
        Comparator<Map.Entry<Integer, String>> comparatorOnlyValues = Map.Entry.comparingByValue();
        Comparator<Map.Entry<Integer, String>> comparatorBoth = (e1, e2) -> {
            Comparator<String> valueComparator = safeNaturalOrderNullLast();   // Required because sometimes the Java compiler is stupid
            int valueComparison = valueComparator.compare(e1.getValue(), e2.getValue());
            return 0 != valueComparison
                    ? e1.getKey().compareTo(e2.getKey())
                    : valueComparison;
        };
        return Stream.of(
                //@formatter:off
                //            sourceMap,         comparator,             expectedException,                expectedResult
                Arguments.of( null,              null,                   null,                             empty() ),
                Arguments.of( intsAndStrings,    null,                   IllegalArgumentException.class,   null ),
                Arguments.of( null,              comparatorOnlyKeys,     null,                             empty() ),
                Arguments.of( new HashMap<>(),   comparatorOnlyKeys,     null,                             empty() ),
                Arguments.of( intsAndStrings,    comparatorOnlyKeys,     null,                             of(Map.entry(1, "TT")) ),
                Arguments.of( intsAndStrings,    comparatorOnlyValues,   null,                             of(Map.entry(2, "BA")) ),
                Arguments.of( intsAndStrings,    comparatorBoth,         null,                             of(Map.entry(1, "TT")) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("minTestCases")
    @DisplayName("min: test cases")
    public <T, E> void min_testCases(Map<? extends T, ? extends E> sourceMap,
                                     Comparator<Map.Entry<? extends T, ? extends E>> comparator,
                                     Class<? extends Exception> expectedException,
                                     Optional<Map.Entry<T, E>> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> min(sourceMap, comparator));
        } else {
            assertEquals(expectedResult, min(sourceMap, comparator));
        }
    }


    static Stream<Arguments> minValueNoComparatorTestCases() {
        Map<Integer, String> intsAndStrings = new LinkedHashMap<>() {{
            put(1, "AB");
            put(2, "Z3");
            put(3, "AB");
        }};
        Map<Integer, String> intsAndStringsWithNullValues = new LinkedHashMap<>() {{
            put(1, "AB");
            put(2, null);
            put(3, "CD");
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,   expectedResult
                Arguments.of( null,                           empty() ),
                Arguments.of( new HashMap<>(),                empty() ),
                Arguments.of( intsAndStrings,                 of("AB") ),
                Arguments.of( intsAndStringsWithNullValues,   of("AB") )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("minValueNoComparatorTestCases")
    @DisplayName("minValue: without comparator test cases")
    public <T, E extends Comparable<? super E>> void minValueNoComparator_testCases(Map<? extends T, ? extends E> sourceMap,
                                                                                    Optional<E> expectedResult) {
        assertEquals(expectedResult, minValue(sourceMap));
    }


    static Stream<Arguments> minValueAllParametersTestCases() {
        Map<Integer, String> intsAndStrings = new LinkedHashMap<>() {{
            put(1, "AB");
            put(2, "Z3");
            put(3, "AB");
            put(4, "UTf");
        }};
        Comparator<String> comparatorNatural = String::compareTo;
        Comparator<String> comparatorReverse = Comparator.reverseOrder();
        return Stream.of(
                //@formatter:off
                //            sourceMap,         comparator,          expectedException,                expectedResult
                Arguments.of( null,              null,                IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,    null,                IllegalArgumentException.class,   null ),
                Arguments.of( null,              comparatorNatural,   null,                             empty() ),
                Arguments.of( new HashMap<>(),   comparatorNatural,   null,                             empty() ),
                Arguments.of( intsAndStrings,    comparatorNatural,   null,                             of("AB") ),
                Arguments.of( intsAndStrings,    comparatorReverse,   null,                             of("Z3") )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("minValueAllParametersTestCases")
    @DisplayName("minValue: with all parameters test cases")
    public <T, E> void minValueAllParameters_testCases(Map<? extends T, ? extends E> sourceMap,
                                                       Comparator<? super E> comparator,
                                                       Class<? extends Exception> expectedException,
                                                       Optional<Tuple2<T, E>> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> minValue(sourceMap, comparator));
        } else {
            assertEquals(expectedResult, minValue(sourceMap, comparator));
        }
    }


    static Stream<Arguments> partitionNoMapFactoryTestCases() {
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
            put(Boolean.TRUE,
                    new HashMap<>() {{
                        put(2, "B");
                        put(4, "o");
                    }}
            );
            put(Boolean.FALSE,
                    new HashMap<>() {{
                        put(1, "A");
                    }}
            );
        }};
        Map<Boolean, Map<Integer, Long>> intsAndLongsResult = new HashMap<>() {{
            put(Boolean.TRUE,
                    new HashMap<>() {{
                        put(11, 6L);
                    }}
            );
            put(Boolean.FALSE,
                    new HashMap<>() {{
                        put(7, 3L);
                        put(21, 9L);
                    }}
            );
        }};
        Map<Boolean, Map<Integer, Long>> emptyMap = new HashMap<>() {{
            put(Boolean.TRUE, new HashMap<>());
            put(Boolean.FALSE, new HashMap<>());
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,         discriminator,       expectedResult
                Arguments.of( null,              null,                emptyMap ),
                Arguments.of( null,              isKeyEven,           emptyMap ),
                Arguments.of( Map.of(),          null,                emptyMap ),
                Arguments.of( Map.of(),          isKeyEven,           emptyMap ),
                Arguments.of( intsAndStrings,    isKeyEven,           intsAndStringsResult ),
                Arguments.of( intsAndLongs,      isKeyPlusValueOdd,   intsAndLongsResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("partitionNoMapFactoryTestCases")
    @DisplayName("partition: without map factory test cases")
    public <T, E> void partitionNoMapFactory_testCases(Map<? extends T, ? extends E> sourceMap,
                                                       BiPredicate<? super T, ? super E> discriminator,
                                                       Map<Boolean, Map<T, E>> expectedResult) {
        assertEquals(expectedResult, partition(sourceMap, discriminator));
    }


    static Stream<Arguments> partitionAllParametersTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "FG");
            put(3, "HY");
            put(5, "ou");
        }};
        BiPredicate<Integer, String> isKeyMod3 = (k, v) -> k % 3 == 0;
        Supplier<Map<String, String>> linkedMapSupplier = LinkedHashMap::new;

        Map<Boolean, Map<Integer, String>> intsAndStringsResult = new HashMap<>() {{
            put(Boolean.TRUE,
                    new HashMap<>() {{
                        put(3, "HY");
                    }}
            );
            put(Boolean.FALSE,
                    new HashMap<>() {{
                        put(1, "FG");
                        put(5, "ou");
                    }}
            );
        }};
        Map<Boolean, Map<Integer, String>> intsAndStringsLinkedMapResult = new LinkedHashMap<>() {{
            put(Boolean.TRUE,
                    new LinkedHashMap<>() {{
                        put(3, "HY");
                    }}
            );
            put(Boolean.FALSE,
                    new LinkedHashMap<>() {{
                        put(1, "FG");
                        put(5, "ou");
                    }}
            );
        }};
        Map<Boolean, Map<Integer, Long>> emptyHashMap = new HashMap<>() {{
            put(Boolean.TRUE, new HashMap<>());
            put(Boolean.FALSE, new HashMap<>());
        }};
        Map<Boolean, Map<Integer, Long>> emptyLinkedHashMap = new LinkedHashMap<>() {{
            put(Boolean.TRUE, new LinkedHashMap<>());
            put(Boolean.FALSE, new LinkedHashMap<>());
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,        discriminator,   mapFactory,          expectedResult
                Arguments.of( null,             null,            null,                emptyHashMap ),
                Arguments.of( null,             isKeyMod3,       null,                emptyHashMap ),
                Arguments.of( Map.of(),         null,            null,                emptyHashMap ),
                Arguments.of( Map.of(),         isKeyMod3,       null,                emptyHashMap ),
                Arguments.of( null,             null,            linkedMapSupplier,   emptyLinkedHashMap ),
                Arguments.of( null,             isKeyMod3,       linkedMapSupplier,   emptyLinkedHashMap ),
                Arguments.of( Map.of(),         null,            linkedMapSupplier,   emptyLinkedHashMap ),
                Arguments.of( Map.of(),         isKeyMod3,       linkedMapSupplier,   emptyLinkedHashMap ),
                Arguments.of( intsAndStrings,   isKeyMod3,       null,                intsAndStringsResult ),
                Arguments.of( intsAndStrings,   isKeyMod3,       linkedMapSupplier,   intsAndStringsLinkedMapResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("partitionAllParametersTestCases")
    @DisplayName("partition: with all parameters test cases")
    public <T, E> void partitionAllParameters_testCases(Map<? extends T, ? extends E> sourceMap,
                                                        BiPredicate<? super T, ? super E> discriminator,
                                                        Supplier<Map<T, E>> mapFactory,
                                                        Map<Boolean, Map<T, E>> expectedResult) {
        assertEquals(expectedResult, partition(sourceMap, discriminator, mapFactory));
    }


    static Stream<Arguments> reduceTestCases() {
        Map<Integer, String> intsAndStrings = new LinkedHashMap<>() {{
            put(1, "Hi");
            put(2, "Hello");
            put(3, "World");
        }};

        BinaryOperator<Map.Entry<Integer, String>> sumKeysConcatValues = (oldEntry, newEntry) ->
                new AbstractMap.SimpleEntry<>(
                        oldEntry.getKey() + newEntry.getKey(),
                        ofNullable(oldEntry.getValue()).orElse("") + ofNullable(newEntry.getValue()).orElse("")
                );
        BinaryOperator<Map.Entry<Integer, String>> lastKeyAndValue = (oldEntry, newEntry) ->
                new AbstractMap.SimpleEntry<>(
                        newEntry.getKey(),
                        newEntry.getValue()
                );
        return Stream.of(
                //@formatter:off
                //            sourceMap,         accumulator,           expectedException,                expectedResult
                Arguments.of( null,              null,                  null,                             empty() ),
                Arguments.of( new HashMap<>(),   null,                  null,                             empty() ),
                Arguments.of( intsAndStrings,    null,                  IllegalArgumentException.class,   null ),
                Arguments.of( null,              sumKeysConcatValues,   null,                             empty() ),
                Arguments.of( new HashMap<>(),   sumKeysConcatValues,   null,                             empty() ),
                Arguments.of( intsAndStrings,    sumKeysConcatValues,   null,                             of(Map.entry(6, "HiHelloWorld")) ),
                Arguments.of( intsAndStrings,    lastKeyAndValue,       null,                             of(Map.entry(3, "World")) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("reduceTestCases")
    @DisplayName("reduce: test cases")
    public <T, E> void reduce_testCases(Map<T, E> sourceMap,
                                        BinaryOperator<Map.Entry<T, E>> accumulator,
                                        Class<? extends Exception> expectedException,
                                        Optional<Map.Entry<T, E>> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> reduce(sourceMap, accumulator));
        } else {
            assertEquals(expectedResult, reduce(sourceMap, accumulator));
        }
    }


    static Stream<Arguments> sliceTestCases() {
        Map<String, Integer> sourceMap = new LinkedHashMap<>() {{
            put("A", 1);
            put("B", 2);
            put("C", 3);
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,   from,   to,                 expectedException,                expectedResult
                Arguments.of( null,        2,      1,                  IllegalArgumentException.class,   null ),
                Arguments.of( null,       -1,      1,                  IllegalArgumentException.class,   null ),
                Arguments.of( Map.of(),    3,      1,                  IllegalArgumentException.class,   null ),
                Arguments.of( Map.of(),    0,     -1,                  IllegalArgumentException.class,   null ),
                Arguments.of( sourceMap,   1,      0,                  IllegalArgumentException.class,   null ),
                Arguments.of( sourceMap,  -1,      0,                  IllegalArgumentException.class,   null ),
                Arguments.of( null,        0,      1,                  null,                             Map.of() ),
                Arguments.of( Map.of(),    0,      1,                  null,                             Map.of() ),
                Arguments.of( Map.of(),    3,      4,                  null,                             Map.of() ),
                Arguments.of( sourceMap,   0,      2,                  null,                             Map.of("A", 1, "B", 2) ),
                Arguments.of( sourceMap,   1,      3,                  null,                             Map.of("B", 2, "C", 3) ),
                Arguments.of( sourceMap,   0,      sourceMap.size(),   null,                             sourceMap )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("sliceTestCases")
    @DisplayName("slice: test cases")
    public <T, E> void slice_testCases(Map<T, E> sourceMap,
                                       int from,
                                       int until,
                                       Class<? extends Exception> expectedException,
                                       Map<T, E> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> slice(sourceMap, from, until));
        } else {
            assertEquals(expectedResult, slice(sourceMap, from, until));
        }
    }


    static Stream<Arguments> slidingTestCases() {
        Map<Integer, String> sourceMap = new LinkedHashMap<>() {{
            put(1, "A");
            put(2, "B");
            put(3, "C");
            put(4, "D");
            put(5, "F");
        }};
        List<Map<Integer, String>> size1Result = List.of(
                Map.of(1, "A"),
                Map.of(2, "B"),
                Map.of(3, "C"),
                Map.of(4, "D"),
                Map.of(5, "F")
        );
        List<Map<Integer, String>> size2Result = List.of(
                new LinkedHashMap<>() {{
                    put(1, "A");
                    put(2, "B");
                }},
                new LinkedHashMap<>() {{
                    put(2, "B");
                    put(3, "C");
                }},
                new LinkedHashMap<>() {{
                    put(3, "C");
                    put(4, "D");
                }},
                new LinkedHashMap<>() {{
                    put(4, "D");
                    put(5, "F");
                }}
        );
        List<Map<Integer, String>> size3Result = List.of(
                new LinkedHashMap<>() {{
                    put(1, "A");
                    put(2, "B");
                    put(3, "C");
                }},
                new LinkedHashMap<>() {{
                    put(2, "B");
                    put(3, "C");
                    put(4, "D");
                }},
                new LinkedHashMap<>() {{
                    put(3, "C");
                    put(4, "D");
                    put(5, "F");
                }}
        );
        List<Map<Integer, String>> size4Result = List.of(
                new LinkedHashMap<>() {{
                    put(1, "A");
                    put(2, "B");
                    put(3, "C");
                    put(4, "D");
                }},
                new LinkedHashMap<>() {{
                    put(2, "B");
                    put(3, "C");
                    put(4, "D");
                    put(5, "F");
                }}
        );
        return Stream.of(
                //@formatter:off
                //            sourceMap,   size,                   expectedException,                expectedResult
                Arguments.of( null,       -1,                      IllegalArgumentException.class,   null ),
                Arguments.of( null,        5,                      null,                             List.of() ),
                Arguments.of( sourceMap,   0,                      null,                             List.of() ),
                Arguments.of( sourceMap,   sourceMap.size(),       null,                             List.of(sourceMap) ),
                Arguments.of( sourceMap,   sourceMap.size() + 1,   null,                             List.of(sourceMap) ),
                Arguments.of( sourceMap,   1,                      null,                             size1Result ),
                Arguments.of( sourceMap,   2,                      null,                             size2Result ),
                Arguments.of( sourceMap,   3,                      null,                             size3Result ),
                Arguments.of( sourceMap,   4,                      null,                             size4Result )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("slidingTestCases")
    @DisplayName("sliding: test cases")
    public <T, E> void sliding_testCases(Map<T, E> sourceMap,
                                         int size,
                                         Class<? extends Exception> expectedException,
                                         List<Map<T, E>> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> sliding(sourceMap, size));
        }
        else {
            assertEquals(expectedResult, sliding(sourceMap, size));
        }
    }


    static Stream<Arguments> sortWithComparatorAndMapsTestCases() {
        Map<Integer, String> map1 = new HashMap<>() {{
            put(1, "A");
            put(4, "o");
        }};
        Map<Integer, String> map2 = new HashMap<>() {{
            put(1, "t");
            put(2, "y");
        }};
        Map<Integer, String> map3 = new HashMap<>() {{
            put(6, "w");
        }};
        Map<Integer, String> mapWithNullValues = new LinkedHashMap<>() {{
            put(1, "AB");
            put(2, null);
        }};

        Comparator<Map.Entry<Integer, String>> comparatorOnlyKeys = Map.Entry.comparingByKey();
        Comparator<Map.Entry<Integer, String>> comparatorOnlyValues = Map.Entry.comparingByValue();
        Comparator<Map.Entry<Integer, String>> comparatorBoth = (e1, e2) -> {
            Comparator<String> valueComparator = safeNaturalOrderNullLast();   // Required because sometimes the Java compiler is stupid
            int valueComparison = valueComparator.compare(e1.getValue(), e2.getValue());
            return 0 != valueComparison
                    ? e1.getKey().compareTo(e2.getKey())
                    : valueComparison;
        };

        Map<Integer, String> expectedResultMaps123OnlyKeysComparator = new LinkedHashMap<>() {{
            put(1, "t");
            put(2, "y");
            put(4, "o");
            put(6, "w");
        }};
        Map<Integer, String> expectedResultMaps123OnlyValuesComparator = new LinkedHashMap<>() {{
            put(4, "o");
            put(1, "t");
            put(6, "w");
            put(2, "y");
        }};
        Map<Integer, String> expectedResultMap2AndMapWithNullValuesBothComparator = new LinkedHashMap<>() {{
            put(2, null);
            put(1, "AB");
        }};
        return Stream.of(
                //@formatter:off
                //            mapToSort1,   mapToSort2,          mapToSort3,   comparator,             expectedException,                expectedResult
                Arguments.of( null,         null,                null,         null,                   IllegalArgumentException.class,   null ),
                Arguments.of( map1,         null,                null,         null,                   IllegalArgumentException.class,   null ),
                Arguments.of( map1,         map2,                null,         null,                   IllegalArgumentException.class,   null ),
                Arguments.of( map1,         map2,                map3,         null,                   IllegalArgumentException.class,   null ),
                Arguments.of( Map.of(),     null,                null,         comparatorOnlyKeys,     null,                             Map.of() ),
                Arguments.of( Map.of(),     Map.of(),            null,         comparatorOnlyKeys,     null,                             Map.of() ),
                Arguments.of( Map.of(),     Map.of(),            Map.of(),     comparatorOnlyKeys,     null,                             Map.of() ),
                Arguments.of( map1,         null,                null,         comparatorOnlyKeys,     null,                             map1 ),
                Arguments.of( map1,         null,                null,         comparatorOnlyValues,   null,                             map1 ),
                Arguments.of( map1,         map2,                map3,         comparatorOnlyKeys,     null,                             expectedResultMaps123OnlyKeysComparator ),
                Arguments.of( map1,         map2,                map3,         comparatorOnlyValues,   null,                             expectedResultMaps123OnlyValuesComparator ),
                Arguments.of( map2,         mapWithNullValues,   null,         comparatorBoth,         null,                             expectedResultMap2AndMapWithNullValuesBothComparator )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("sortWithComparatorAndMapsTestCases")
    @DisplayName("sort: with comparator and maps test cases")
    public <T, E> void sortWithComparatorAndMaps_testCases(Map<? extends T, ? extends E> mapToSort1,
                                                           Map<? extends T, ? extends E> mapToSort2,
                                                           Map<? extends T, ? extends E> mapToSort3,
                                                           Comparator<Map.Entry<? extends T, ? extends E>> comparator,
                                                           Class<? extends Exception> expectedException,
                                                           Map<T, E> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> sort(comparator, mapToSort1, mapToSort2, mapToSort3));
        }
        else {
            assertEquals(expectedResult, sort(comparator, mapToSort1, mapToSort2, mapToSort3));
        }
    }


    static Stream<Arguments> sortAllParametersTestCases() {
        Map<Integer, String> map1 = new HashMap<>() {{
            put(1, "A");
            put(4, "o");
        }};
        Map<Integer, String> map2 = new HashMap<>() {{
            put(1, "t");
            put(2, "y");
        }};
        Map<Integer, String> map3 = new HashMap<>() {{
            put(6, "w");
        }};
        Map<Integer, String> mapWithNullValues = new LinkedHashMap<>() {{
            put(1, "AB");
            put(2, null);
        }};
        Comparator<Map.Entry<Integer, String>> comparatorOnlyKeys = Map.Entry.comparingByKey();
        Comparator<Map.Entry<Integer, String>> comparatorOnlyValues = Map.Entry.comparingByValue();
        Comparator<Map.Entry<Integer, String>> comparatorBoth = (e1, e2) -> {
            Comparator<String> valueComparator = safeNaturalOrderNullLast();   // Required because sometimes the Java compiler is stupid
            int valueComparison = valueComparator.compare(e1.getValue(), e2.getValue());
            return 0 != valueComparison
                    ? e1.getKey().compareTo(e2.getKey())
                    : valueComparison;
        };
        BinaryOperator<String> keepsOldValue = (oldValue, newValue) -> oldValue;

        Map<Integer, String> expectedResultMaps123OnlyKeysComparatorDefaultMerge = new LinkedHashMap<>() {{
            put(1, "t");
            put(2, "y");
            put(4, "o");
            put(6, "w");
        }};
        Map<Integer, String> expectedResultMaps123OnlyKeysComparatorProvidedMerge = new LinkedHashMap<>() {{
            put(1, "A");
            put(2, "y");
            put(4, "o");
            put(6, "w");
        }};
        Map<Integer, String> expectedResultMaps123OnlyValuesComparatorDefaultMerge = new LinkedHashMap<>() {{
            put(4, "o");
            put(1, "t");
            put(6, "w");
            put(2, "y");
        }};
        Map<Integer, String> expectedResultMaps123OnlyValuesComparatorProvidedMerge = new LinkedHashMap<>() {{
            put(1, "A");
            put(4, "o");
            put(6, "w");
            put(2, "y");
        }};
        Map<Integer, String> expectedResultMap2AndMapWithNullValuesBothComparatorDefaultMerge = new LinkedHashMap<>() {{
            put(1, "AB");
            put(2, null);
        }};
        Map<Integer, String> expectedResultMap2AndMapWithNullValuesBothComparatorProvidedMerge = new LinkedHashMap<>() {{
            put(1, "t");
            put(2, "y");
        }};
        return Stream.of(
                //@formatter:off
                //            mapToSort1,   mapToSort2,          mapToSort3,   comparator,             mergeValueFunction,   expectedException,                expectedResult
                Arguments.of( null,         null,                null,         null,                   null,                 IllegalArgumentException.class,   null ),
                Arguments.of( map1,         null,                null,         null,                   null,                 IllegalArgumentException.class,   null ),
                Arguments.of( map1,         map2,                null,         null,                   null,                 IllegalArgumentException.class,   null ),
                Arguments.of( map1,         map2,                map3,         null,                   null,                 IllegalArgumentException.class,   null ),
                Arguments.of( map1,         map2,                map3,         null,                   keepsOldValue,        IllegalArgumentException.class,   null ),
                Arguments.of( Map.of(),     null,                null,         comparatorOnlyKeys,     null,                 null,                             Map.of() ),
                Arguments.of( Map.of(),     null,                null,         comparatorOnlyKeys,     keepsOldValue,        null,                             Map.of() ),
                Arguments.of( Map.of(),     Map.of(),            null,         comparatorOnlyKeys,     null,                 null,                             Map.of() ),
                Arguments.of( Map.of(),     Map.of(),            null,         comparatorOnlyKeys,     keepsOldValue,        null,                             Map.of() ),
                Arguments.of( Map.of(),     Map.of(),            Map.of(),     comparatorOnlyKeys,     null,                 null,                             Map.of() ),
                Arguments.of( Map.of(),     Map.of(),            Map.of(),     comparatorOnlyKeys,     keepsOldValue,        null,                             Map.of() ),
                Arguments.of( map1,         null,                null,         comparatorOnlyKeys,     null,                 null,                             map1 ),
                Arguments.of( map1,         null,                null,         comparatorOnlyKeys,     keepsOldValue,        null,                             map1 ),
                Arguments.of( map1,         null,                null,         comparatorOnlyValues,   null,                 null,                             map1 ),
                Arguments.of( map1,         null,                null,         comparatorOnlyValues,   keepsOldValue,        null,                             map1 ),
                Arguments.of( map1,         map2,                map3,         comparatorOnlyKeys,     null,                 null,                             expectedResultMaps123OnlyKeysComparatorDefaultMerge ),
                Arguments.of( map1,         map2,                map3,         comparatorOnlyKeys,     keepsOldValue,        null,                             expectedResultMaps123OnlyKeysComparatorProvidedMerge ),
                Arguments.of( map1,         map2,                map3,         comparatorOnlyValues,   null,                 null,                             expectedResultMaps123OnlyValuesComparatorDefaultMerge ),
                Arguments.of( map1,         map2,                map3,         comparatorOnlyValues,   keepsOldValue,        null,                             expectedResultMaps123OnlyValuesComparatorProvidedMerge ),
                Arguments.of( map2,         mapWithNullValues,   null,         comparatorBoth,         null,                 null,                             expectedResultMap2AndMapWithNullValuesBothComparatorDefaultMerge ),
                Arguments.of( map2,         mapWithNullValues,   null,         comparatorBoth,         keepsOldValue,        null,                             expectedResultMap2AndMapWithNullValuesBothComparatorProvidedMerge )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("sortAllParametersTestCases")
    @DisplayName("sort: with all parameters test cases")
    public <T, E> void sortAllParameters_testCases(Map<? extends T, ? extends E> mapToSort1,
                                                   Map<? extends T, ? extends E> mapToSort2,
                                                   Map<? extends T, ? extends E> mapToSort3,
                                                   Comparator<Map.Entry<? extends T, ? extends E>> comparator,
                                                   BinaryOperator<E> mergeValueFunction,
                                                   Class<? extends Exception> expectedException,
                                                   Map<T, E> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> sort(comparator, mergeValueFunction, mapToSort1, mapToSort2, mapToSort3));
        }
        else {
            assertEquals(expectedResult, sort(comparator, mergeValueFunction, mapToSort1, mapToSort2, mapToSort3));
        }
    }


    static Stream<Arguments> splitTestCases() {
        Map<String, Integer> sourceMap = new LinkedHashMap<>() {{
            put("A", 1);
            put("B", 2);
            put("C", 3);
            put("D", 4);
        }};
        List<Map<String, Integer>> size2Result = List.of(
                new LinkedHashMap<>() {{
                    put("A", 1);
                    put("B", 2);
                }},
                new LinkedHashMap<>() {{
                    put("C", 3);
                    put("D", 4);
                }}
        );
        List<Map<String, Integer>> size3Result = List.of(
                new LinkedHashMap<>() {{
                    put("A", 1);
                    put("B", 2);
                    put("C", 3);
                }},
                new LinkedHashMap<>() {{
                    put("D", 4);
                }}
        );
        return Stream.of(
                //@formatter:off
                //            sourceMap,   size,                   expectedException,                expectedResult
                Arguments.of( null,       -1,                      IllegalArgumentException.class,   null ),
                Arguments.of( null,        5,                      null,                             List.of() ),
                Arguments.of( sourceMap,   0,                      null,                             List.of() ),
                Arguments.of( sourceMap,   sourceMap.size(),       null,                             List.of(sourceMap) ),
                Arguments.of( sourceMap,   sourceMap.size() + 1,   null,                             List.of(sourceMap) ),
                Arguments.of( sourceMap,   2,                      null,                             size2Result ),
                Arguments.of( sourceMap,   3,                      null,                             size3Result )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("splitTestCases")
    @DisplayName("split: test cases")
    public <T, E> void split_testCases(Map<T, E> sourceMap,
                                       int size,
                                       Class<? extends Exception> expectedException,
                                       List<Map<T, E>> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> split(sourceMap, size));
        }
        else {
            assertEquals(expectedResult, split(sourceMap, size));
        }
    }


    static Stream<Arguments> takeWhileNoMapFactoryTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(2, "A");
            put(4, "o");
            put(5, "H");
        }};
        BiPredicate<Integer, String> isKeyEvenAndValueVowel = (k, v) -> k % 2 == 0 && "AEIOUaeiou".contains(v);
        Map<Integer, String> intsAndStringsResult = new HashMap<>() {{
            put(2, "A");
            put(4, "o");
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,        filterPredicate,          expectedResult
                Arguments.of( null,             null,                     Map.of() ),
                Arguments.of( null,             isKeyEvenAndValueVowel,   Map.of() ),
                Arguments.of( Map.of(),         null,                     Map.of() ),
                Arguments.of( Map.of(),         isKeyEvenAndValueVowel,   Map.of() ),
                Arguments.of( intsAndStrings,   null,                     intsAndStrings ),
                Arguments.of( intsAndStrings,   isKeyEvenAndValueVowel,   intsAndStringsResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("takeWhileNoMapFactoryTestCases")
    @DisplayName("takeWhile: without map factory test cases")
    public <T, E> void takeWhileNoMapFactory_testCases(Map<? extends T, ? extends E> sourceMap,
                                                       BiPredicate<? super T, ? super E> filterPredicate,
                                                       Map<T, E> expectedResult) {
        assertEquals(expectedResult, takeWhile(sourceMap, filterPredicate));
    }


    static Stream<Arguments> takeWhileAllParametersTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(2, "A");
            put(4, "o");
            put(5, "H");
            put(6, null);
        }};
        BiPredicate<Integer, String> isKeyEvenAndValueVowel = (k, v) ->
                k % 2 == 0 &&
                        (
                                null == v ||
                                        "AEIOUaeiou".contains(v)
                        );
        Supplier<Map<Integer, Long>> linkedMapSupplier = LinkedHashMap::new;
        Map<Integer, String> intsAndStringsResult = new LinkedHashMap<>() {{
            put(2, "A");
            put(4, "o");
        }};
        return Stream.of(
                //@formatter:off
                //            sourceMap,        filterPredicate,          mapFactory,          expectedResult
                Arguments.of( null,             null,                     null,                Map.of() ),
                Arguments.of( Map.of(),         null,                     null,                Map.of() ),
                Arguments.of( null,             isKeyEvenAndValueVowel,   null,                Map.of() ),
                Arguments.of( Map.of(),         isKeyEvenAndValueVowel,   null,                Map.of() ),
                Arguments.of( intsAndStrings,   null,                     null,                intsAndStrings ),
                Arguments.of( intsAndStrings,   null,                     linkedMapSupplier,   intsAndStrings ),
                Arguments.of( intsAndStrings,   isKeyEvenAndValueVowel,   linkedMapSupplier,   intsAndStringsResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("takeWhileAllParametersTestCases")
    @DisplayName("takeWhile: with all parameters test cases")
    public <T, E> void takeWhileAllParameters_testCases(Map<? extends T, ? extends E> sourceMap,
                                                        BiPredicate<? super T, ? super E> filterPredicate,
                                                        Supplier<Map<T, E>> mapFactory,
                                                        Map<T, E> expectedResult) {
        assertEquals(expectedResult, takeWhile(sourceMap, filterPredicate, mapFactory));
    }


    static Stream<Arguments> toCollectionWithKeyValueMapperTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "A");
            put(2, "B");
            put(4, "o");
            put(6, null);
        }};
        BiFunction<Integer, String, Integer> sumIntegerAndStringLength =
                (i, s) -> {
                    final int finalI = null == i ? 0 : i;
                    final int sLength = null == s ? 0 : s.length();
                    return sLength + finalI;
                };
        List<Integer> expectedResult = asList(2, 3, 5, 6);
        return Stream.of(
                //@formatter:off
                //            sourceMap,        keyValueMapper,              expectedException,                expectedResult
                Arguments.of( null,             null,                        null,                             List.of() ),
                Arguments.of( null,             sumIntegerAndStringLength,   null,                             List.of() ),
                Arguments.of( Map.of(),         null,                        null,                             List.of() ),
                Arguments.of( Map.of(),         sumIntegerAndStringLength,   null,                             List.of() ),
                Arguments.of( intsAndStrings,   null,                        IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   sumIntegerAndStringLength,   null,                             expectedResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toCollectionWithKeyValueMapperTestCases")
    @DisplayName("toCollection: with keyValueMapper test cases")
    public <K, V, R> void toCollectionWithKeyValueMapper_testCases(Map<? extends K, ? extends V> sourceMap,
                                                                   BiFunction<? super K, ? super V, ? extends R> keyValueMapper,
                                                                   Class<? extends Exception> expectedException,
                                                                   List<R> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException,
                    () -> toCollection(
                            sourceMap, keyValueMapper
                    )
            );
        } else {
            assertEquals(expectedResult,
                    toCollection(
                            sourceMap, keyValueMapper
                    )
            );
        }
    }


    static Stream<Arguments> toCollectionWithKeyValueMapperAndSupplierTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "A");
            put(2, "B");
            put(4, "o");
            put(5, "");
            put(6, null);
        }};
        BiFunction<Integer, String, Integer> sumIntegerAndStringLength =
                (i, s) -> {
                    final int finalI = null == i ? 0 : i;
                    final int sLength = null == s ? 0 : s.length();
                    return sLength + finalI;
                };
        Supplier<Set<Integer>> setSupplier = HashSet::new;

        List<Integer> expectedResultWithoutSupplier = asList(2, 3, 5, 5, 6);
        Set<Integer> expectedResultWithSupplier = new HashSet<>(expectedResultWithoutSupplier);
        return Stream.of(
                //@formatter:off
                //            sourceMap,        keyValueMapper,              collectionFactory,   expectedException,                expectedResult
                Arguments.of( null,             null,                        null,              null,                             List.of() ),
                Arguments.of( null,             sumIntegerAndStringLength,   null,              null,                             List.of() ),
                Arguments.of( null,             sumIntegerAndStringLength,   setSupplier,       null,                             Set.of() ),
                Arguments.of( Map.of(),         null,                        null,              null,                             List.of() ),
                Arguments.of( Map.of(),         sumIntegerAndStringLength,   null,              null,                             List.of() ),
                Arguments.of( Map.of(),         sumIntegerAndStringLength,   setSupplier,       null,                             Set.of() ),
                Arguments.of( intsAndStrings,   null,                        null,              IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   null,                        setSupplier,       IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   sumIntegerAndStringLength,   null,              null,                             expectedResultWithoutSupplier ),
                Arguments.of( intsAndStrings,   sumIntegerAndStringLength,   setSupplier,       null,                             expectedResultWithSupplier )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toCollectionWithKeyValueMapperAndSupplierTestCases")
    @DisplayName("toCollection: with keyValueMapper and collectionFactory test cases")
    public <K, V, R> void toCollectionWithKeyValueMapperAndSupplier_testCases(Map<? extends K, ? extends V> sourceMap,
                                                                              BiFunction<? super K, ? super V, ? extends R> keyValueMapper,
                                                                              Supplier<Collection<R>> collectionFactory,
                                                                              Class<? extends Exception> expectedException,
                                                                              Collection<R> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException,
                    () -> toCollection(
                            sourceMap, keyValueMapper, collectionFactory
                    )
            );
        } else {
            assertEquals(expectedResult,
                    toCollection(
                            sourceMap, keyValueMapper, collectionFactory
                    )
            );
        }
    }


    static Stream<Arguments> toCollectionWithKeyValueMapperBiPredicateAndSupplierTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "Aa");
            put(2, "B");
            put(4, "o");
            put(6, null);
        }};
        BiFunction<Integer, String, Integer> sumIntegerAndStringLength =
                (i, s) -> {
                    final int finalI = null == i ? 0 : i;
                    final int sLength = null == s ? 0 : s.length();
                    return sLength + finalI;
                };
        BiPredicate<Integer, String> filterPredicate =
                (i, s) ->
                        null != i &&
                                0 == i % 2;
        Supplier<Set<Integer>> setSupplier = HashSet::new;

        List<Integer> expectedResultWithoutFilterDefaultSupplier = asList(3, 3, 5, 6);
        Set<Integer> expectedResultWithoutFilterSetSupplier = new HashSet<>(expectedResultWithoutFilterDefaultSupplier);
        List<Integer> expectedResultWithFilterDefaultSupplier = asList(3, 5, 6);
        Set<Integer> expectedResultWithFilterSetSupplier = new HashSet<>(expectedResultWithFilterDefaultSupplier);
        return Stream.of(
                //@formatter:off
                //            sourceMap,        keyValueMapper,              filterPredicate,   collectionFactory,   expectedException,                expectedResult
                Arguments.of( null,             null,                        null,              null,                null,                             List.of() ),
                Arguments.of( null,             sumIntegerAndStringLength,   null,              null,                null,                             List.of() ),
                Arguments.of( null,             sumIntegerAndStringLength,   filterPredicate,   null,                null,                             List.of() ),
                Arguments.of( null,             sumIntegerAndStringLength,   filterPredicate,   setSupplier,         null,                             Set.of() ),
                Arguments.of( Map.of(),         null,                        null,              null,                null,                             List.of() ),
                Arguments.of( Map.of(),         sumIntegerAndStringLength,   null,              null,                null,                             List.of() ),
                Arguments.of( Map.of(),         sumIntegerAndStringLength,   filterPredicate,   null,                null,                             List.of() ),
                Arguments.of( Map.of(),         sumIntegerAndStringLength,   filterPredicate,   setSupplier,         null,                             Set.of() ),
                Arguments.of( intsAndStrings,   null,                        null,              null,                IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   null,                        filterPredicate,   null,                IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   null,                        filterPredicate,   setSupplier,         IllegalArgumentException.class,   null ),
                Arguments.of( intsAndStrings,   sumIntegerAndStringLength,   null,              null,                null,                             expectedResultWithoutFilterDefaultSupplier ),
                Arguments.of( intsAndStrings,   sumIntegerAndStringLength,   filterPredicate,   null,                null,                             expectedResultWithFilterDefaultSupplier ),
                Arguments.of( intsAndStrings,   sumIntegerAndStringLength,   null,              setSupplier,         null,                             expectedResultWithoutFilterSetSupplier ),
                Arguments.of( intsAndStrings,   sumIntegerAndStringLength,   filterPredicate,   setSupplier,         null,                             expectedResultWithFilterSetSupplier )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toCollectionWithKeyValueMapperBiPredicateAndSupplierTestCases")
    @DisplayName("toCollection: with keyValueMapper, filterPredicate and collectionFactory test cases")
    public <K, V, R> void toCollectionWithKeyValueMapperBiPredicateAndSupplier_testCases(Map<? extends K, ? extends V> sourceMap,
                                                                                         BiFunction<? super K, ? super V, ? extends R> keyValueMapper,
                                                                                         BiPredicate<? super K, ? super V> filterPredicate,
                                                                                         Supplier<Collection<R>> collectionFactory,
                                                                                         Class<? extends Exception> expectedException,
                                                                                         Collection<R> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException,
                    () -> toCollection(
                            sourceMap, keyValueMapper, filterPredicate, collectionFactory
                    )
            );
        } else {
            assertEquals(expectedResult,
                    toCollection(
                            sourceMap, keyValueMapper, filterPredicate, collectionFactory
                    )
            );
        }
    }


    static Stream<Arguments> toCollectionWithPartialFunctionAndSupplierTestCases() {
        Map<Integer, String> intsAndStrings = new HashMap<>() {{
            put(1, "Aa");
            put(2, "B");
            put(4, "o");
            put(6, null);
        }};
        PartialFunction<Map.Entry<Integer, String>, Integer> partialFunction = PartialFunction.of(
                e -> null != e &&
                        null != e.getKey() &&
                        0 == e.getKey() % 2,
                e -> {
                    if (null == e) {
                        return 0;
                    }
                    final int finalKey = null == e.getKey()
                            ? 0
                            : e.getKey();
                    final int finalValue = null == e.getValue()
                            ? 0
                            : e.getValue().length();
                    return finalKey + finalValue;
                }
        );
        Supplier<Set<Integer>> setSupplier = HashSet::new;

        List<Integer> expectedResultWithFilterDefaultSupplier = asList(3, 5, 6);
        Set<Integer> expectedResultWithFilterSetSupplier = new HashSet<>(expectedResultWithFilterDefaultSupplier);
        return Stream.of(
                //@formatter:off
                //            sourceMap,        partialFunction,   collectionFactory,   expectedException,                expectedResult
                Arguments.of( null,             null,              null,                null,                             List.of() ),
                Arguments.of( null,             partialFunction,   null,                null,                             List.of() ),
                Arguments.of( null,             partialFunction,   setSupplier,         null,                             Set.of() ),
                Arguments.of( Map.of(),         null,              null,                null,                             List.of() ),
                Arguments.of( Map.of(),         partialFunction,   null,                null,                             List.of() ),
                Arguments.of( Map.of(),         partialFunction,   setSupplier,         null,                             Set.of() ),
                Arguments.of( intsAndStrings,   null,              null,                IllegalArgumentException.class,   List.of() ),
                Arguments.of( intsAndStrings,   null,              setSupplier,         IllegalArgumentException.class,   Set.of() ),
                Arguments.of( intsAndStrings,   partialFunction,   null,                null,                             expectedResultWithFilterDefaultSupplier ),
                Arguments.of( intsAndStrings,   partialFunction,   setSupplier,         null,                             expectedResultWithFilterSetSupplier )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("toCollectionWithPartialFunctionAndSupplierTestCases")
    @DisplayName("toCollection: with partialFunction and collectionFactory test cases")
    public <K, V, R> void toCollectionWithPartialFunctionAndSupplier_testCases(Map<? extends K, ? extends V> sourceMap,
                                                                               PartialFunction<? super Map.Entry<K, V>, ? extends R> partialFunction,
                                                                               Supplier<Collection<R>> collectionFactory,
                                                                               Class<? extends Exception> expectedException,
                                                                               Collection<R> expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException,
                    () -> toCollection(
                            sourceMap, partialFunction, collectionFactory
                    )
            );
        } else {
            assertEquals(expectedResult,
                    toCollection(
                            sourceMap, partialFunction, collectionFactory
                    )
            );
        }
    }

}
