package com.spring5microservices.common.util;

import com.spring5microservices.common.interfaces.functional.PartialFunction;
import com.spring5microservices.common.interfaces.functional.TriFunction;
import lombok.experimental.UtilityClass;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.spring5microservices.common.util.CollectorsUtil.toMapNullableValues;
import static com.spring5microservices.common.util.FunctionUtil.overwriteWithNew;
import static com.spring5microservices.common.util.PredicateUtil.biAlwaysTrue;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

@UtilityClass
public class MapUtil {


    /**
     *    Returns a new {@link Map} using the given {@code sourceMap}, applying to its elements the compose
     * {@link BiFunction} {@code secondMapper}({@code firstMapper}(x))
     *
     * <pre>
     * Example:
     *
     *   Parameters:                                                      Result:
     *    [(1, "AGTF"), (3, "CD")]                                         [(2, "4"), (4, "2")]
     *    (k, v) -> new AbstractMap.SimpleEntry<>(k, v.length())
     *    (k, v) -> new AbstractMap.SimpleEntry<>(k+1, v.toString())
     * </pre>
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to transform
     * @param firstMapper
     *    {@link BiFunction} with the first modification to apply
     * @param secondMapper
     *    {@link BiFunction} with the second modification to apply
     *
     * @return {@link Map} applying {@code firstMapper} and {@code secondMapper} to the provided {@code sourceMap}
     *
     * @throws IllegalArgumentException if {@code firstMapper} or {@code secondMapper} is {@code null}
     *                                  with a not empty {@code sourceMap}
     */
    public static <K1, V1, K2, V2, T, R> Map<T, R> andThen(final Map<? extends K1, ? extends V1> sourceMap,
                                                           final BiFunction<? super K1, ? super V1, Map.Entry<? extends K2, ? extends V2>> firstMapper,
                                                           final BiFunction<? super K2, ? super V2, Map.Entry<? extends T, ? extends R>> secondMapper) {
        return andThen(
                sourceMap,
                firstMapper,
                secondMapper,
                HashMap::new
        );
    }


    /**
     *    Returns a new {@link Map} using the given {@code sourceMap}, applying to its elements the compose
     * {@link BiFunction} {@code secondMapper}({@code firstMapper}(x))
     *
     * <pre>
     * Example:
     *
     *   Parameters:                                                      Result:
     *    [(1, "AGTF"), (3, "CD")]                                         [(2, "4"), (4, "2")]
     *    (k, v) -> new AbstractMap.SimpleEntry<>(k, v.length())
     *    (k, v) -> new AbstractMap.SimpleEntry<>(k+1, v.toString())
     *    HashMap::new
     * </pre>
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to transform
     * @param firstMapper
     *    {@link BiFunction} with the first modification to apply
     * @param secondMapper
     *    {@link BiFunction} with the second modification to apply
     * @param mapFactory
     *    {@link Supplier} of the {@link Map} used to store the returned elements.
     *    If {@code null} then {@link HashMap}
     *
     * @return {@link Map} applying {@code firstMapper} and {@code secondMapper} to the provided {@code sourceMap}
     *
     * @throws IllegalArgumentException if {@code firstMapper} or {@code secondMapper} is {@code null}
     *                                  with a not empty {@code sourceMap}
     */
    public static <K1, V1, K2, V2, T, R> Map<T, R> andThen(final Map<? extends K1, ? extends V1> sourceMap,
                                                           final BiFunction<? super K1, ? super V1, Map.Entry<? extends K2, ? extends V2>> firstMapper,
                                                           final BiFunction<? super K2, ? super V2, Map.Entry<? extends T, ? extends R>> secondMapper,
                                                           final Supplier<Map<T, R>> mapFactory) {
        final Supplier<Map<T, R>> finalMapFactory = getFinalMapFactory(mapFactory);
        if (CollectionUtils.isEmpty(sourceMap)) {
            return finalMapFactory.get();
        }
        Assert.notNull(firstMapper, "firstMapper must be not null");
        Assert.notNull(secondMapper, "secondMapper must be not null");
        final BiFunction<? super K1, ? super V1, Map.Entry<? extends T, ? extends R>> finalMapper =
                (k1, v1) -> {
                    Map.Entry<? extends K2, ? extends V2> firstMapperResult = firstMapper.apply(k1, v1);
                    return secondMapper.apply(
                            firstMapperResult.getKey(),
                            firstMapperResult.getValue()
                    );
                };
        return map(
                sourceMap,
                finalMapper,
                mapFactory
        );
    }


    /**
     *    Returns a new {@link HashMap} using the given {@code sourceMap}, applying {@code defaultMapper} if
     * the current element verifies {@code filterPredicate}, {@code orElseMapper} otherwise.
     *
     * <pre>
     * Example:
     *
     *   Parameters:               Result:
     *    [("A", 1), ("B", 2)]      [("A", 2), ("B", 4)]
     *    (k, v) -> v % 2 == 1
     *    (k, v) -> v + 1
     *    (k, v) -> v * 2
     * </pre>
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to filter and transform
     * @param filterPredicate
     *    {@link BiPredicate} to filter elements of {@code sourceMap}
     * @param defaultMapper
     *    {@link BiFunction} to transform elements of {@code sourceMap} that verify {@code filterPredicate}
     * @param orElseMapper
     *    {@link BiFunction} to transform elements of {@code sourceMap} do not verify {@code filterPredicate}
     *
     * @return {@link Map}
     *
     * @throws IllegalArgumentException if {@code defaultMapper} or {@code orElseMapper} is {@code null}
     *                                  with a not empty {@code sourceMap}
     */
    public static <K1, K2, V1, V2> Map<K2, V2> applyOrElse(final Map<? extends K1, ? extends V1> sourceMap,
                                                           final BiPredicate<? super K1, ? super V1> filterPredicate,
                                                           final BiFunction<? super K1, ? super V1, ? extends Map.Entry<K2, V2>> defaultMapper,
                                                           final BiFunction<? super K1, ? super V1, ? extends Map.Entry<K2, V2>> orElseMapper) {
        return applyOrElse(
                sourceMap,
                filterPredicate,
                defaultMapper,
                orElseMapper,
                HashMap::new
        );
    }


    /**
     *    Returns a new {@link Map} using the given {@code sourceMap}, applying {@code defaultMapper} if
     * the current element verifies {@code filterPredicate}, {@code orElseMapper} otherwise.
     *
     * <pre>
     * Example:
     *
     *   Parameters:               Result:
     *    [("A", 1), ("B", 2)]      [("A", 2), ("B", 4)]
     *    (k, v) -> v % 2 == 1
     *    (k, v) -> v + 1
     *    (k, v) -> v * 2
     *    HashMap::new
     * </pre>
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to filter and transform
     * @param filterPredicate
     *    {@link BiPredicate} to filter elements of {@code sourceMap}
     * @param defaultMapper
     *    {@link BiFunction} to transform elements of {@code sourceMap} that verify {@code filterPredicate}
     * @param orElseMapper
     *    {@link BiFunction} to transform elements of {@code sourceMap} do not verify {@code filterPredicate}
     * @param mapFactory
     *    {@link Supplier} of the {@link Map} used to store the returned elements.
     *    If {@code null} then {@link HashMap}
     *
     * @return {@link Map}
     *
     * @throws IllegalArgumentException if {@code defaultMapper} or {@code orElseMapper} is {@code null}
     *                                  with a not empty {@code sourceMap}
     */
    public static <K1, K2, V1, V2> Map<K2, V2> applyOrElse(final Map<? extends K1, ? extends V1> sourceMap,
                                                           final BiPredicate<? super K1, ? super V1> filterPredicate,
                                                           final BiFunction<? super K1, ? super V1, ? extends Map.Entry<K2, V2>> defaultMapper,
                                                           final BiFunction<? super K1, ? super V1, ? extends Map.Entry<K2, V2>> orElseMapper,
                                                           final Supplier<Map<K2, V2>> mapFactory) {
        final Supplier<Map<K2, V2>> finalMapFactory = getFinalMapFactory(mapFactory);
        if (CollectionUtils.isEmpty(sourceMap)) {
            return finalMapFactory.get();
        }
        Assert.notNull(defaultMapper, "defaultMapper must be not null");
        Assert.notNull(orElseMapper, "orElseMapper must be not null");
        final BiPredicate<? super K1, ? super V1> finalFilterPredicate = ObjectUtil.getOrElse(
                filterPredicate,
                biAlwaysTrue()
        );
        return applyOrElse(
                sourceMap,
                PartialFunction.of(
                        finalFilterPredicate,
                        defaultMapper
                ),
                orElseMapper,
                mapFactory
        );
    }


    /**
     *    Returns a new {@link HashMap} using the given {@code sourceMap}, applying applying {@link PartialFunction#apply(Object)}
     * if the current element verifies {@link PartialFunction#isDefinedAt(Object)}, {@code orElseMapper} otherwise.
     *
     * <pre>
     * Example:
     *
     *   Parameters:                                      Result:
     *    [("A", 1), ("B", 2)]                              [("A", 2), ("B", 4)]
     *    new PartialFunction<>() {
     *
     *      public Map.Entry<String, Integer> apply(final Map.Entry<String, Integer> entry) {
     *        return null == entry
     *                 ? null
     *                 : new AbstractMap.SimpleEntry<>(
     *                      entry.getKey(),
     *                      null == entry.getValue()
     *                         ? 0
     *                         : entry.getValue() + 1
     *                   );
     *      }
     *
     *      public boolean isDefinedAt(final Map.Entry<String, Integer> entry) {
     *        return null != entry &&
     *               1 == entry.getValue() % 2;
     *      }
     *    }
     *    (k, v) -> v * 2
     * </pre>
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to filter and transform
     * @param partialFunction
     *    {@link PartialFunction} to filter and transform elements of {@code sourceMap}
     * @param orElseMapper
     *    {@link BiFunction} to transform elements of {@code sourceMap} do not verify {@code filterPredicate}
     *
     * @return {@link Map}
     *
     * @throws IllegalArgumentException if {@code partialFunction} or {@code orElseMapper} is {@code null}
     *                                  with a not empty {@code sourceMap}
     */
    public static <K1, K2, V1, V2> Map<K2, V2> applyOrElse(final Map<? extends K1, ? extends V1> sourceMap,
                                                           final PartialFunction<? super Map.Entry<K1, V1>, ? extends Map.Entry<K2, V2>> partialFunction,
                                                           final BiFunction<? super K1, ? super V1, ? extends Map.Entry<K2, V2>> orElseMapper) {
        return applyOrElse(
                sourceMap,
                partialFunction,
                orElseMapper,
                HashMap::new
        );
    }


    /**
     *    Returns a new {@link HashMap} using the given {@code sourceMap}, applying applying {@link PartialFunction#apply(Object)}
     * if the current element verifies {@link PartialFunction#isDefinedAt(Object)}, {@code orElseMapper} otherwise.
     *
     * <pre>
     * Example:
     *
     *   Parameters:                                      Result:
     *    [("A", 1), ("B", 2)]                              [("A", 2), ("B", 4)]
     *    new PartialFunction<>() {
     *
     *      public Map.Entry<String, Integer> apply(final Map.Entry<String, Integer> entry) {
     *        return null == entry
     *                 ? null
     *                 : new AbstractMap.SimpleEntry<>(
     *                      entry.getKey(),
     *                      null == entry.getValue()
     *                         ? 0
     *                         : entry.getValue() + 1
     *                   );
     *      }
     *
     *      public boolean isDefinedAt(final Map.Entry<String, Integer> entry) {
     *        return null != entry &&
     *               1 == entry.getValue() % 2;
     *      }
     *    }
     *    (k, v) -> v * 2
     *    HashMap::new
     * </pre>
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to filter and transform
     * @param partialFunction
     *    {@link PartialFunction} to filter and transform elements of {@code sourceMap}
     * @param orElseMapper
     *    {@link BiFunction} to transform elements of {@code sourceMap} do not verify {@code filterPredicate}
     * @param mapFactory
     *    {@link Supplier} of the {@link Map} used to store the returned elements.
     *    If {@code null} then {@link HashMap}
     *
     * @return {@link Map}
     *
     * @throws IllegalArgumentException if {@code partialFunction} or {@code orElseMapper} is {@code null}
     *                                  with a not empty {@code sourceMap}
     */
    public static <K1, K2, V1, V2> Map<K2, V2> applyOrElse(final Map<? extends K1, ? extends V1> sourceMap,
                                                           final PartialFunction<? super Map.Entry<K1, V1>, ? extends Map.Entry<K2, V2>> partialFunction,
                                                           final BiFunction<? super K1, ? super V1, ? extends Map.Entry<K2, V2>> orElseMapper,
                                                           final Supplier<Map<K2, V2>> mapFactory) {
        final Supplier<Map<K2, V2>> finalMapFactory = getFinalMapFactory(mapFactory);
        if (CollectionUtils.isEmpty(sourceMap)) {
            return finalMapFactory.get();
        }
        Assert.notNull(partialFunction, "partialFunction must be not null");
        Assert.notNull(orElseMapper, "orElseMapper must be not null");

        // Allowed because immutable/read-only Maps are covariant.
        Map<K1, V1> narrowedSourceMap = (Map<K1, V1>) sourceMap;
        return narrowedSourceMap.entrySet()
                .stream()
                .map(e ->
                        partialFunction.isDefinedAt(e)
                                ? partialFunction.apply(e)
                                : orElseMapper.apply(
                                        e.getKey(),
                                        e.getValue()
                                  )
                )
                .collect(
                        toMapNullableValues(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                finalMapFactory
                        )
                );
    }


    /**
     * Returns a {@link Map} after:
     * <p>
     *  - Filter its elements using {@code filterPredicate}
     *  - Transform its filtered elements using {@code mapFunction}
     *
     * <pre>
     * Example:
     *
     *   Parameters:                                                     Result:
     *    [(1, "Hi"), (2, "Hello")]                                       [(3, 4)]
     *    (k, v) -> k % 2 == 0
     *    (k, v) -> new AbstractMap.SimpleEntry<>(k+1, v.length())
     * </pre>
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to filter and transform
     * @param filterPredicate
     *    {@link BiPredicate} to filter elements of {@code sourceMap}
     * @param mapFunction
     *    {@link BiFunction} to transform filtered elements of {@code sourceMap}
     *
     * @return {@link Map}
     *
     * @throws IllegalArgumentException if {@code mapFunction} is {@code null} with a not empty {@code sourceMap}
     */
    public static <K1, K2, V1, V2> Map<K2, V2> collect(final Map<? extends K1, ? extends V1> sourceMap,
                                                       final BiPredicate<? super K1, ? super V1> filterPredicate,
                                                       final BiFunction<? super K1, ? super V1, ? extends Map.Entry<K2, V2>> mapFunction) {
        return collect(
                sourceMap,
                filterPredicate,
                mapFunction,
                HashMap::new
        );
    }


    /**
     * Returns a {@link Map} after:
     * <p>
     *  - Filter its elements using {@code filterPredicate}
     *  - Transform its filtered elements using {@code mapFunction}
     *
     * <pre>
     * Example:
     *
     *   Parameters:                                                     Result:
     *    [(1, "Hi"), (2, "Hello")]                                       [(3, 4)]
     *    (k, v) -> k % 2 == 0
     *    (k, v) -> new AbstractMap.SimpleEntry<>(k+1, v.length())
     *    HashMap::new
     * </pre>
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to filter and transform
     * @param filterPredicate
     *    {@link BiPredicate} to filter elements of {@code sourceMap}
     * @param mapFunction
     *    {@link BiFunction} to transform filtered elements of {@code sourceMap}
     * @param mapFactory
     *    {@link Supplier} of the {@link Map} used to store the returned elements
     *    If {@code null} then {@link HashMap}
     *
     * @return {@link Map}
     *
     * @throws IllegalArgumentException if {@code mapFunction} is {@code null} with a not empty {@code sourceMap}
     */
    public static <K1, K2, V1, V2> Map<K2, V2> collect(final Map<? extends K1, ? extends V1> sourceMap,
                                                       final BiPredicate<? super K1, ? super V1> filterPredicate,
                                                       final BiFunction<? super K1, ? super V1, ? extends Map.Entry<K2, V2>> mapFunction,
                                                       final Supplier<Map<K2, V2>> mapFactory) {
        final Supplier<Map<K2, V2>> finalMapFactory = getFinalMapFactory(mapFactory);
        if (CollectionUtils.isEmpty(sourceMap)) {
            return finalMapFactory.get();
        }
        Assert.notNull(mapFunction, "mapFunction must be not null");
        final BiPredicate<? super K1, ? super V1> finalFilterPredicate = ObjectUtil.getOrElse(
                filterPredicate,
                biAlwaysTrue()
        );
        return collect(
                sourceMap,
                PartialFunction.of(
                        finalFilterPredicate,
                        mapFunction
                ),
                mapFactory
        );
    }


    /**
     * Returns a {@link Map} after:
     * <p>
     *  - Filter its elements using {@link PartialFunction#isDefinedAt(Object)} of {@code partialFunction}
     *  - Transform its filtered elements using {@link PartialFunction#apply(Object)} of {@code partialFunction}
     *
     * <pre>
     * Example:
     *
     *   Parameters:                                                                                 Result:
     *    [(1, "Hi"), (2, "Hello")]                                                                   [(1, 2)]
     *    new PartialFunction<>() {
     *
     *      public Map.Entry<Integer, Integer> apply(final Map.Entry<Integer, String> entry) {
     *        return null == entry
     *                 ? null
     *                 : new AbstractMap.SimpleEntry<>(
     *                      entry.getKey(),
     *                      null == entry.getValue()
     *                         ? 0
     *                         : entry.getValue().length()
     *                   );
     *      }
     *
     *      public boolean isDefinedAt(final Map.Entry<Integer, String> entry) {
     *        return null != entry &&
     *               1 == entry.getKey() % 2;
     *      }
     *    }
     * </pre>
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to filter and transform
     * @param partialFunction
     *    {@link PartialFunction} to filter and transform elements from {@code sourceMap}
     *
     * @return {@link Map}
     *
     * @throws IllegalArgumentException if {@code partialFunction} is {@code null} with a not empty {@code sourceMap}
     */
    public static <K1, K2, V1, V2> Map<K2, V2> collect(final Map<? extends K1, ? extends V1> sourceMap,
                                                       final PartialFunction<? super Map.Entry<K1, V1>, ? extends Map.Entry<K2, V2>> partialFunction) {
        return collect(
                sourceMap,
                partialFunction,
                HashMap::new
        );
    }


    /**
     * Returns a {@link Map} after:
     * <p>
     *  - Filter its elements using {@link PartialFunction#isDefinedAt(Object)} of {@code partialFunction}
     *  - Transform its filtered elements using {@link PartialFunction#apply(Object)} of {@code partialFunction}
     *
     * <pre>
     * Example:
     *
     *   Parameters:                                                                                 Result:
     *    [(1, "Hi"), (2, "Hello")]                                                                   [(1, 2)]
     *    new PartialFunction<>() {
     *
     *      public Map.Entry<Integer, Integer> apply(final Map.Entry<Integer, String> entry) {
     *        return null == entry
     *                 ? null
     *                 : new AbstractMap.SimpleEntry<>(
     *                      entry.getKey(),
     *                      null == entry.getValue()
     *                         ? 0
     *                         : entry.getValue().length()
     *                   );
     *      }
     *
     *      public boolean isDefinedAt(final Map.Entry<Integer, String> entry) {
     *        return null != entry &&
     *               1 == entry.getKey() % 2;
     *      }
     *    },
     *    HashMap::new
     * </pre>
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to filter and transform
     * @param partialFunction
     *    {@link PartialFunction} to filter and transform elements from {@code sourceMap}
     * @param mapFactory
     *    {@link Supplier} of the {@link Map} used to store the returned elements
     *    If {@code null} then {@link HashMap}
     *
     * @return {@link Map}
     *
     * @throws IllegalArgumentException if {@code partialFunction} is {@code null} with a not empty {@code sourceMap}
     */
    @SuppressWarnings("unchecked")
    public static <K1, K2, V1, V2> Map<K2, V2> collect(final Map<? extends K1, ? extends V1> sourceMap,
                                                       final PartialFunction<? super Map.Entry<K1, V1>, ? extends Map.Entry<K2, V2>> partialFunction,
                                                       final Supplier<Map<K2, V2>> mapFactory) {
        final Supplier<Map<K2, V2>> finalMapFactory = getFinalMapFactory(mapFactory);
        if (CollectionUtils.isEmpty(sourceMap)) {
            return finalMapFactory.get();
        }
        Assert.notNull(partialFunction, "partialFunction must be not null");

        // Allowed because immutable/read-only Maps are covariant.
        Map<K1, V1> narrowedSourceMap = (Map<K1, V1>) sourceMap;
        return narrowedSourceMap.entrySet()
                .stream()
                .filter(partialFunction::isDefinedAt)
                .map(partialFunction)
                .collect(
                        toMapNullableValues(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                finalMapFactory
                        )
                );
    }


    /**
     *    Finds the first element of the {@code sourceMap} for which the given {@link PartialFunction} is defined, and
     * applies the {@link PartialFunction} to it.
     *
     * <pre>
     * Example:
     *
     *   Parameters:                                                                                 Result:
     *    [(1, "Hi"), (2, "Hello")]                                                                   Optional[(2, 4)]
     *    new PartialFunction<>() {
     *
     *      public Map.Entry<Integer, Integer> apply(final Map.Entry<Integer, String> entry) {
     *        return null == entry
     *                 ? null
     *                 : new AbstractMap.SimpleEntry<>(
     *                      entry.getKey(),
     *                      null == entry.getValue()
     *                         ? 0
     *                         : entry.getValue().length()
     *                   );
     *      }
     *
     *      public boolean isDefinedAt(final Map.Entry<Integer, String> entry) {
     *        return null != entry &&
     *               0 == entry.getKey() % 2;
     *      }
     *    }
     * </pre>
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to filter and transform
     * @param partialFunction
     *    {@link PartialFunction} to filter elements of {@code sourceMap} and transform the first one defined at function's domain
     *
     * @return {@link Optional} value containing {@code partialFunction} applied to the first value for which it is defined,
     *         {@link Optional#empty()} if none exists.
     *
     * @throws IllegalArgumentException if {@code partialFunction} is {@code null} with a not empty {@code sourceMap}
     */
    public static <K1, K2, V1, V2> Optional<Map.Entry<K2, V2>> collectFirst(final Map<? extends K1, ? extends V1> sourceMap,
                                                                            final PartialFunction<? super Map.Entry<K1, V1>, ? extends Map.Entry<K2, V2>> partialFunction) {
        if (CollectionUtils.isEmpty(sourceMap)) {
            return empty();
        }
        Assert.notNull(partialFunction, "partialFunction must be not null");
        final BiPredicate<? super K1, ? super V1> filterPredicate =
                (k, v) ->
                        partialFunction.isDefinedAt(
                                new AbstractMap.SimpleEntry<>(
                                        k,
                                        v
                                )
                        );
        return find(
                sourceMap,
                filterPredicate
        )
        .map(e ->
                partialFunction.apply(
                        new AbstractMap.SimpleEntry<>(
                                e.getKey(),
                                e.getValue()
                        )
                )
        );
    }


    /**
     *    Returns a new {@link Map} containing the elements of provided {@link Map}s {@code maps}. By default, merging
     * the maps if the key exists its value will be updated with the latest one.
     *
     * <pre>
     * Example:
     *
     *   Parameters:                     Result:
     *    [(1, "Hi"), (2, "Hello")]       [(1, "Hi"), (2, "Dear"), (5, "World")]
     *    [(2, "Dear"), (5, "World")]
     * </pre>
     *
     * @param maps
     *    {@link Map}s to concat
     *
     * @return {@link Map} with the elements of {@code maps}
     */
    @SafeVarargs
    public static <T, E> Map<T, E> concat(final Map<? extends T, ? extends E> ...maps) {
        return concat(
                LinkedHashMap::new,
                overwriteWithNew(),
                maps
        );
    }


    /**
     *    Returns a new {@link Map} containing the elements of provided {@link Map}s {@code maps}. By default, merging
     * the maps, if the key exists its value will be updated with the latest one.
     *
     * <pre>
     * Example:
     *
     *   Parameters:                     Result:
     *    HashMap::new                    [(1, "Hi"), (2, "Dear"), (5, "World")]
     *    [(1, "Hi"), (2, "Hello")]
     *    [(2, "Dear"), (5, "World")]
     * </pre>
     *
     * @param mapFactory
     *    {@link Supplier} of the {@link Map} used to store the returned elements
     *    If {@code null} then {@link HashMap}
     * @param maps
     *    {@link Map}s to concat
     *
     * @return {@link Map} with the elements of {@code maps}
     */
    @SafeVarargs
    public static <T, E> Map<T, E> concat(final Supplier<Map<T, E>> mapFactory,
                                          final Map<? extends T, ? extends E> ...maps) {
        return concat(
                mapFactory,
                overwriteWithNew(),
                maps
        );
    }


    /**
     * Returns a new {@link Map} containing the elements of provided {@link Map}s {@code maps}.
     *
     * <pre>
     * Example:
     *
     *   Parameters:                     Result:
     *    HashMap::new                    [(1, "Hi"), (2, "Hello"), (5, "World")]
     *    (oldV, newV) -> oldV
     *    [(1, "Hi"), (2, "Hello")]
     *    [(2, "Dear"), (5, "World")]
     * </pre>
     *
     * @param mapFactory
     *    {@link Supplier} of the {@link Map} used to store the returned elements
     *    If {@code null} then {@link HashMap}
     * @param mergeValueFunction
     *    {@link BinaryOperator} used to resolve collisions between values associated with the same key. If no one is
     *    provided, by default last value will be used
     * @param maps
     *    {@link Map}s to concat
     *
     * @return {@link Map} with the elements of {@code maps}
     */
    @SafeVarargs
    public static <T, E> Map<T, E> concat(final Supplier<Map<T, E>> mapFactory,
                                          final BinaryOperator<E> mergeValueFunction,
                                          final Map<? extends T, ? extends E> ...maps) {
        final Supplier<Map<T, E>> finalMapFactory = getFinalMapFactory(mapFactory);
        final BinaryOperator<E> finalMergeValueFunction = ObjectUtil.getOrElse(
                mergeValueFunction,
                overwriteWithNew()
        );
        return ofNullable(maps)
                .map(m ->
                        Stream.of(m)
                                .filter(Objects::nonNull)
                                .flatMap(notNullMap ->
                                        notNullMap.entrySet().stream()
                                )
                                .collect(
                                        toMapNullableValues(
                                                Map.Entry::getKey,
                                                Map.Entry::getValue,
                                                finalMergeValueFunction,
                                                finalMapFactory
                                        )
                                )
                )
                .orElseGet(finalMapFactory);
    }


    /**
     * Counts the number of elements in the {@code sourceMap} which satisfy the {@code filterPredicate}.
     *
     * <pre>
     * Example:
     *
     *   Parameters:                    Result:
     *    [(1, "Hi"), (2, "Hello")]      1
     *    (k, v) -> k % 2 == 0
     * </pre>
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to filter
     * @param filterPredicate
     *   {@link Predicate} to filter elements from {@code sourceCollection}
     *
     * @return the number of elements satisfying the {@link Predicate} {@code filterPredicate}
     */
    public static <T, E> int count(final Map<? extends T, ? extends E> sourceMap,
                                   final BiPredicate<? super T, ? super E> filterPredicate) {
        if (CollectionUtils.isEmpty(sourceMap)) {
            return 0;
        }
        if (isNull(filterPredicate)) {
            return sourceMap.size();
        }
        return sourceMap.entrySet()
                .stream()
                .filter(entry ->
                        filterPredicate.test(
                                entry.getKey(),
                                entry.getValue()
                        )
                )
                .mapToInt(elto -> 1)
                .sum();
    }


    /**
     *    Returns a {@link Map} removing the elements of provided {@code sourceMap} that satisfy the {@link BiPredicate}
     * {@code filterPredicate}.
     *
     * <pre>
     * Example:
     *
     *   Parameters:                    Result:
     *    [(1, "Hi"), (2, "Hello")]      [(1, "Hi")]
     *    (k, v) -> k % 2 == 0
     * </pre>
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to filter
     * @param filterPredicate
     *    {@link BiPredicate} to filter elements from {@code sourceMap}
     *
     * @return {@link Map}
     */
    public static <T, E> Map<T, E> dropWhile(final Map<? extends T, ? extends E> sourceMap,
                                             final BiPredicate<? super T, ? super E> filterPredicate) {
        return dropWhile(
                sourceMap,
                filterPredicate,
                HashMap::new
        );
    }


    /**
     *    Returns a {@link Map} removing the elements of provided {@code sourceMap} that satisfy the {@link BiPredicate}
     * {@code filterPredicate}.
     *
     * <pre>
     * Example:
     *
     *   Parameters:                    Result:
     *    [(1, "Hi"), (2, "Hello")]      [(1, "Hi")]
     *    (k, v) -> k % 2 == 0
     *    HashMap::new
     * </pre>
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to filter
     * @param filterPredicate
     *    {@link BiPredicate} to filter elements from {@code sourceMap}
     *
     * @return {@link Map}
     */
    public static <T, E> Map<T, E> dropWhile(final Map<? extends T, ? extends E> sourceMap,
                                             final BiPredicate<? super T, ? super E> filterPredicate,
                                             final Supplier<Map<T, E>> mapFactory) {
        final BiPredicate<? super T, ? super E> finalFilterPredicate =
                isNull(filterPredicate)
                        ? biAlwaysTrue()
                        : filterPredicate.negate();

        return takeWhile(
                sourceMap,
                finalFilterPredicate,
                mapFactory
        );
    }


    /**
     * Finds the first element of the given {@link Map} satisfying the provided {@link BiPredicate}.
     *
     * <pre>
     * Example:
     *
     *   Parameters:                    Result:
     *    [(1, "Hi"), (2, "Hello")]      Optional((2, "Hello"))
     *    (k, v) -> k % 2 == 0
     * </pre>
     *
     * @param sourceMap
     *    {@link Map} to search
     * @param filterPredicate
     *    {@link BiPredicate} used to filter elements of {@code sourceMap}
     *
     * @return {@link Optional} of {@link Map.Entry} containing the first element that satisfies {@code filterPredicate},
     *         {@link Optional#empty()} otherwise.
     */
    public static <T, E> Optional<Map.Entry<T, E>> find(final Map<? extends T, ? extends E> sourceMap,
                                                        final BiPredicate<? super T, ? super E> filterPredicate) {
        if (CollectionUtils.isEmpty(sourceMap) || isNull(filterPredicate)) {
            return empty();
        }
        return sourceMap.entrySet()
                .stream()
                .filter(entry ->
                        filterPredicate.test(
                                entry.getKey(),
                                entry.getValue()
                        )
                )
                .findFirst()
                .map(entry ->
                        new AbstractMap.SimpleEntry<>(
                                entry.getKey(),
                                entry.getValue()
                        )
                );
    }


    /**
     * Converts given {@code sourceMap} into a {@link List} formed by the elements of these iterable collections.
     *
     * <pre>
     * Example:
     *
     *   Parameters:                                        Result:
     *     [(1, ["Hi"]), (2, ["Hello", "World"])]            [(1, "Hi"), (2, "Hello"), (2, "World")]
     *     (i, l) -> l.stream()
     *                .map(elto -> Tuple2.of(i, elto))
     *                .collect(toList())
     * </pre>
     *
     * @param sourceMap
     *    {@link Map} of elements to concat
     * @param flattener
     *    {@link BiFunction} to transform elements of {@code sourceMap}
     *
     * @return {@link List} resulting from concatenating all element of {@code sourceMap}
     *
     * @throws IllegalArgumentException if {@code flattener} is {@code null} and {@code sourceMap} is not empty.
     */
    public static <T, E, R, U> List<U> flatten(final Map<? extends T, ? extends E> sourceMap,
                                               final BiFunction<? super T, ? super E, ? extends R> flattener) {
        return (List<U>) flatten(
                sourceMap,
                flattener,
                ArrayList::new
        );
    }


    /**
     * Converts given {@code sourceMap} into a {@link Collection} formed by the elements of these iterable collections.
     *
     * <pre>
     * Example:
     *
     *   Parameters:                                        Result:
     *     [(1, ["Hi"]), (2, ["Hello", "Hello"])]            [(1, "Hi"), (2, "Hello")]
     *     (i, l) -> l.stream()
     *                .map(elto -> Tuple2.of(i, elto))
     *                .collect(toList())
     *     HashSet::new
     * </pre>
     *
     * @param sourceMap
     *    {@link Map} of elements to concat
     * @param flattener
     *    {@link BiFunction} to transform elements of {@code sourceMap}
     * @param collectionFactory
     *    {@link Supplier} of the {@link Collection} used to store the returned elements.
     *    If {@code null} then {@link ArrayList}
     *
     * @return {@link List} resulting from concatenating all element of {@code sourceMap}
     *
     * @throws IllegalArgumentException if {@code flattener} is {@code null} and {@code sourceMap} is not empty.
     */
    @SuppressWarnings("unchecked")
    public static <T, E, R, U> Collection<U> flatten(final Map<? extends T, ? extends E> sourceMap,
                                                     final BiFunction<? super T, ? super E, ? extends R> flattener,
                                                     final Supplier<Collection<U>> collectionFactory) {
        final Supplier<Collection<U>> finalCollectionFactory = ObjectUtil.getOrElse(
                collectionFactory,
                ArrayList::new
        );
        if (CollectionUtils.isEmpty(sourceMap)) {
            return finalCollectionFactory.get();
        }
        Assert.notNull(flattener, "flattener must be not null");
        List<R> result = sourceMap.entrySet()
                .stream()
                .map(entry ->
                        flattener.apply(
                                entry.getKey(),
                                entry.getValue()
                        )
                )
                .collect(toList());

        return CollectionUtil.flatten(
                (Collection<Object>)result,
                collectionFactory
        );
    }


    /**
     *    Folds given {@link Map} values from the left, starting with {@code initialValue} and successively
     * calling {@code accumulator}.
     *
     * <pre>
     * Example:
     *
     *   Parameters:                       Result:
     *    [(1, "Hi"), (2, "Hello")]         10
     *    0
     *    (k, v) -> k + v.length()
     * </pre>
     *
     * @param sourceMap
     *    {@link Map} with elements to combine
     * @param initialValue
     *    The initial value to start with
     * @param accumulator
     *    A {@link TriFunction} which combines elements
     *
     * @return a folded value
     *
     * @throws IllegalArgumentException if {@code initialValue} is {@code null}
     */
    public static <T, E, R> R foldLeft(final Map<? extends T, ? extends E> sourceMap,
                                       final R initialValue,
                                       final TriFunction<R, ? super T, ? super E, R> accumulator) {
        Assert.notNull(initialValue, "initialValue must be not null");
        return ofNullable(sourceMap)
                .map(sm -> {
                    R result = initialValue;
                    if (nonNull(accumulator)) {
                        for (var entry : sm.entrySet()) {
                            result = accumulator.apply(
                                    result,
                                    entry.getKey(),
                                    entry.getValue()
                            );
                        }
                    }
                    return result;
                })
                .orElse(initialValue);
    }


    /**
     *    Returns the value associated with the given {@code key}, or the result of {@code defaultValue} if the {@code key}
     * is not contained in {@code sourceMap}.
     *
     * <pre>
     * Example 1:
     *
     *   Parameters:                      Result:
     *    [(1, "Hi"), (2, "Hello")]        "Hi"
     *    1
     *    "World"
     * </pre>
     *
     * <pre>
     * Example 2:
     *
     *   Parameters:                      Result:
     *    [(1, "Hi"), (2, "Hello")]        "World"
     *    5
     *    "World"
     * </pre>
     *
     * @param sourceMap
     *    {@link Map} to search {@code key}
     * @param key
     *    Key to search in {@code sourceMap}
     * @param defaultValue
     *    {@link Supplier} that yields a default value in case no binding for {@code key} is found in {@code sourceMap}
     *
     * @return value related with given {@code key} is exists,
     *         {@code defaultValue} otherwise.
     */
    public static <T, E> E getOrElse(final Map<? extends T, ? extends E> sourceMap,
                                     final T key,
                                     final E defaultValue) {
        return getOrElse(
                sourceMap,
                key,
                () -> defaultValue
        );
    }


    /**
     *    Returns the value associated with the given {@code key}, or the result of {@code defaultValue} if the {@code key}
     * is not contained in {@code sourceMap}.
     *
     * <pre>
     * Example 1:
     *
     *   Parameters:                      Result:
     *    [(1, "Hi"), (2, "Hello")]        "Hi"
     *    1
     *    () -> "World"
     * </pre>
     *
     * <pre>
     * Example 2:
     *
     *   Parameters:                      Result:
     *    [(1, "Hi"), (2, "Hello")]        "World"
     *    5
     *    () -> "World"
     * </pre>
     *
     * @param sourceMap
     *    {@link Map} to search {@code key}
     * @param key
     *    Key to search in {@code sourceMap}
     * @param defaultValue
     *    {@link Supplier} that yields a default value in case no binding for {@code key} is found in {@code sourceMap}
     *
     * @return value related with given {@code key} is exists,
     *         {@code defaultValue} otherwise.
     *
     * @throws IllegalArgumentException if {@code defaultValue} is {@code null}
     */
    public static <T, E> E getOrElse(final Map<? extends T, ? extends E> sourceMap,
                                     final T key,
                                     final Supplier<E> defaultValue) {
        Assert.notNull(defaultValue, "defaultValue must be not null");
        final Map<? extends T, ? extends E> finalSourceMap = ObjectUtil.getOrElse(
                sourceMap,
                Map.of()
        );
        return ofNullable(key)
                .map(k -> (E) finalSourceMap.get(k))
                .orElseGet(defaultValue);
    }


    /**
     * Partitions {@code sourceMap} into a {@link Map} of maps according to given {@code discriminator} {@link BiFunction}.
     *
     * <pre>
     * Example:
     *
     *   Parameters:                                    Result:
     *    [(1, "Hi"), (2, "Hello"), (5, "World")]        [(0,  [(2, "Hello")])
     *    (k, v) -> k % 2                                 (1,  [(1, "Hi"), (5, "World")])]
     * </pre>
     *
     * @param sourceMap
     *    {@link Map} to filter
     * @param discriminator
     *    {@link BiFunction} used to split the elements of {@code sourceMap}
     *
     * @return {@link Map}
     */
    public static <T, E, R> Map<R, Map<T, E>> groupBy(final Map<? extends T, ? extends E> sourceMap,
                                                      final BiFunction<? super T, ? super E, ? extends R> discriminator) {
        return groupBy(
                sourceMap,
                discriminator,
                HashMap::new,
                HashMap::new
        );
    }


    /**
     * Partitions {@code sourceMap} into a {@link Map} of maps according to given {@code discriminator} {@link BiFunction}.
     *
     * <pre>
     * Example:
     *
     *   Parameters:                                    Result:
     *    [(1, "Hi"), (2, "Hello"), (5, "World")]        [(0,  [(2, "Hello")])
     *    (k, v) -> k % 2                                 (1,  [(1, "Hi"), (5, "World")])]
     *    HashMap::new
     *    HashMap::new
     * </pre>
     *
     * @param sourceMap
     *    {@link Map} to filter
     * @param discriminator
     *    {@link BiFunction} used to split the elements of {@code sourceMap}
     * @param mapResultFactory
     *    {@link Supplier} of the {@link Map} used to store the returned elements.
     *    If {@code null} then {@link HashMap}
     * @param mapValuesFactory
     *    {@link Supplier} of the {@link Map} used to store the values inside returned {@link Map}.
     *    If {@code null} then {@link HashMap}
     *
     * @return {@link Map}
     */
    public static <T, E, R> Map<R, Map<T, E>> groupBy(final Map<? extends T, ? extends E> sourceMap,
                                                      final BiFunction<? super T, ? super E, ? extends R> discriminator,
                                                      final Supplier<Map<R, Map<T, E>>> mapResultFactory,
                                                      final Supplier<Map<T, E>> mapValuesFactory) {
        final Supplier<Map<R, Map<T, E>>> finalMapResultFactory = getFinalMapFactory(mapResultFactory);
        final Supplier<Map<T, E>> finalMapValuesFactory = getFinalMapFactory(mapValuesFactory);
        Map<R, Map<T, E>> result = finalMapResultFactory.get();

        if (!CollectionUtils.isEmpty(sourceMap) && nonNull(discriminator)) {
            sourceMap.forEach(
                    (k, v) -> {
                        R discriminatorResult = discriminator.apply(k, v);
                        result.putIfAbsent(
                                discriminatorResult,
                                finalMapValuesFactory.get()
                        );
                        result.get(discriminatorResult)
                                .put(k, v);
                    }
            );
        }
        return result;
    }


    /**
     *    Partitions given {@code sourceMap} into a {@link Map} of {@link List} according to {@code discriminatorKey}.
     * Each element in a group is transformed into a value of type V using {@code valueMapper} {@link BiFunction}.
     * <p>
     * It is equivalent to:
     *
     * <pre>
     *    Map<R, Map<T, E>> groupedMap = groupBy(sourceMap, discriminatorKey)
     *    Map<R, List<V>> finalMap = mapValues(groupedMap, valueMapper)
     * </pre>
     *
     * <pre>
     * Example:
     *
     *   Parameters:                                             Result:
     *    [(1, "Hi"), (2, "Hello"), (5, "World"), (6, "!")]       [(0,  [1])
     *    (k, v) -> k % 3                                          (1,  [2])
     *    (k, v) -> v.length()                                     (2,  [5, 5])]
     * </pre>
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to transform.
     * @param discriminatorKey
     *    The discriminator {@link BiFunction} to get the key values of returned {@link Map}
     * @param valueMapper
     *    {@link BiFunction} to transform elements of {@code sourceMap}
     *
     * @return {@link Map}
     *
     * @throws IllegalArgumentException if {@code discriminatorKey} or {@code valueMapper} is {@code null}
     */
    @SuppressWarnings("unchecked")
    public static <T, E, R, V> Map<R, List<V>> groupMap(final Map<? extends T, ? extends E> sourceMap,
                                                        final BiFunction<? super T, ? super E, ? extends R> discriminatorKey,
                                                        final BiFunction<? super T, ? super E, ? extends V> valueMapper) {
        return (Map) groupMap(
                sourceMap,
                discriminatorKey,
                valueMapper,
                ArrayList::new
        );
    }


    /**
     *    Partitions given {@code sourceMap} into a {@link Map} of {@link List} according to {@code discriminatorKey}.
     * Each element in a group is transformed into a value of type V using {@code valueMapper} {@link BiFunction}.
     * <p>
     * It is equivalent to:
     *
     * <pre>
     *    Map<R, Map<T, E>> groupedMap = groupBy(sourceMap, discriminatorKey)
     *    Map<R, List<V>> finalMap = mapValues(groupedMap, valueMapper)
     * </pre>
     *
     * <pre>
     * Example:
     *
     *   Parameters:                                             Result:
     *    [(1, "Hi"), (2, "Hello"), (5, "World"), (6, "!")]       [(0,  [1])
     *    (k, v) -> k % 3                                          (1,  [2])
     *    (k, v) -> v.length()                                     (2,  [5, 5])]
     *    ArrayList::new
     * </pre>
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to transform.
     * @param discriminatorKey
     *    The discriminator {@link BiFunction} to get the key values of returned {@link Map}
     * @param valueMapper
     *    {@link BiFunction} to transform elements of {@code sourceMap}
     * @param collectionFactory
     *    {@link Supplier} of the {@link Collection} used to store the returned elements.
     *    If {@code null} then {@link ArrayList}
     *
     * @return {@link Map}
     *
     * @throws IllegalArgumentException if {@code discriminatorKey} or {@code valueMapper} is {@code null}
     */
    public static <T, E, R, V> Map<R, Collection<V>> groupMap(final Map<? extends T, ? extends E> sourceMap,
                                                              final BiFunction<? super T, ? super E, ? extends R> discriminatorKey,
                                                              final BiFunction<? super T, ? super E, ? extends V> valueMapper,
                                                              final Supplier<Collection<V>> collectionFactory) {
        Assert.notNull(discriminatorKey, "discriminatorKey must be not null");
        Assert.notNull(valueMapper, "valueMapper must be not null");
        if (CollectionUtils.isEmpty(sourceMap)) {
            return new HashMap<>();
        }
        final Supplier<Collection<V>> finalCollectionFactory = ObjectUtil.getOrElse(
                collectionFactory,
                ArrayList::new
        );
        Map<R, Collection<V>> result = new HashMap<>();
        sourceMap.forEach(
                (k, v) -> {
                    R discriminatorKeyResult = discriminatorKey.apply(k, v);
                    result.putIfAbsent(
                            discriminatorKeyResult,
                            finalCollectionFactory.get()
                    );
                    result.get(discriminatorKeyResult)
                            .add(valueMapper.apply(k, v));
                }
        );
        return result;
    }


    /**
     *    Partitions given {@code sourceMap} into a {@link Map} of {@link List} according to {@code discriminatorKey}.
     * All the values that have the same discriminator are then transformed by {@code valueMapper} {@link BiFunction}
     * and then reduced into a single value with {@code reduceValues}.
     *
     * <pre>
     * Example:
     *
     *   Parameters:                                              Intermediate Map:          Result:
     *    [(1, "Hi"), (2, "Hello"), (5, "World"), (6, "!")]        [(0,  [1])                 [(0, 1), (1, 2), (2, 10)]
     *    (k, v) -> k % 3                                           (1,  [2])
     *    (k, v) -> v.length()                                      (2,  [5, 5])]
     *    v -> v++
     * </pre>
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to transform.
     * @param discriminatorKey
     *    The discriminator {@link BiFunction} to get the key values of returned {@link Map}
     * @param valueMapper
     *    {@link BiFunction} to transform elements of {@code sourceMap}
     * @param reduceValues
     *    {@link BinaryOperator} used to reduces the values related with same key
     *
     * @return {@link Map}
     *
     * @throws IllegalArgumentException if {@code discriminatorKey}, {@code valueMapper} or {@code reduceValues}
     *                                  is {@code null}
     */
    public static <T, E, R, V> Map<R, V> groupMapReduce(final Map<? extends T, ? extends E> sourceMap,
                                                        final BiFunction<? super T, ? super E, ? extends R> discriminatorKey,
                                                        final BiFunction<? super T, ? super E, V> valueMapper,
                                                        final BinaryOperator<V> reduceValues) {
        Assert.notNull(discriminatorKey, "discriminatorKey must be not null");
        Assert.notNull(valueMapper, "valueMapper must be not null");
        Assert.notNull(reduceValues, "reduceValues must be not null");
        Map<R, V> result = new HashMap<>();
        groupMap(
                sourceMap,
                discriminatorKey,
                valueMapper
        )
        .forEach(
                (k, v) ->
                        result.put(
                                k,
                                v.stream().reduce(reduceValues).get()
                        )
        );
        return result;
    }


    /**
     * Builds a new {@link Map} by applying a function to all elements of {@code sourceMap}.
     *
     * <pre>
     * Example:
     *
     *   Parameters:                                                 Result:
     *    [(1, "AGTF"), (3, "CD")]                                    [(1, 4), (3, 2)]
     *    (k, v) -> new AbstractMap.SimpleEntry<>(k, v.length())
     * </pre>
     *
     * @param sourceMap
     *    {@link Map} to used as source of the new one
     * @param mapFunction
     *    {@link BiFunction} used to transform given {@code sourceMap} elements
     *
     * @return {@link Map}
     *
     * @throws IllegalArgumentException if {@code mapFunction} is {@code null} and {@code sourceMap} is not empty.
     */
    public static <T, E, R, V> Map<R, V> map(final Map<? extends T, ? extends E> sourceMap,
                                             final BiFunction<? super T, ? super E, Map.Entry<? extends R, ? extends V>> mapFunction) {
        return map(
                sourceMap,
                mapFunction,
                HashMap::new
        );
    }


    /**
     * Builds a new {@link Map} by applying a function to all elements of {@code sourceMap}.
     *
     * <pre>
     * Example:
     *
     *   Parameters:                                                 Result:
     *    [(1, "AGTF"), (3, "CD")]                                    [(1, 4), (3, 2)]
     *    (k, v) -> new AbstractMap.SimpleEntry<>(k, v.length())
     *    HashMap::new
     * </pre>
     *
     * @param sourceMap
     *    {@link Map} to used as source of the new one
     * @param mapFunction
     *    {@link BiFunction} used to transform given {@code sourceMap} elements
     * @param mapFactory
     *    {@link Supplier} of the {@link Map} used to store the returned elements.
     *    If {@code null} then {@link HashMap}
     *
     * @return {@link Map}
     *
     * @throws IllegalArgumentException if {@code mapFunction} is {@code null} and {@code sourceMap} is not empty.
     */
    public static <T, E, R, V> Map<R, V> map(final Map<? extends T, ? extends E> sourceMap,
                                             final BiFunction<? super T, ? super E, Map.Entry<? extends R, ? extends V>> mapFunction,
                                             final Supplier<Map<R, V>> mapFactory) {
        final Supplier<Map<R, V>> finalMapFactory = getFinalMapFactory(mapFactory);
        if (CollectionUtils.isEmpty(sourceMap)) {
            return finalMapFactory.get();
        }
        Assert.notNull(mapFunction, "mapFunction must be not null");
        return sourceMap.entrySet()
                .stream()
                .map(entry ->
                        mapFunction.apply(
                                entry.getKey(),
                                entry.getValue()
                        )
                )
                .collect(
                        toMapNullableValues(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                finalMapFactory
                        )
                );
    }


    /**
     * Builds a new {@link Map} by applying a function to all values of {@code sourceMap}.
     *
     * <pre>
     * Example:
     *
     *   Parameters:                    Result:
     *    [(1, "A"), (3, "C")]           [(1, 2), (3, 4)]
     *    (k, v) -> k + v.length()
     * </pre>
     *
     * @param sourceMap
     *    {@link Map} to used as source of the new one
     * @param mapFunction
     *    {@link BiFunction} used to transform given {@code sourceMap} values
     *
     * @return updated {@link Map}
     *
     * @throws IllegalArgumentException if {@code mapFunction} is {@code null}
     */
    public static <T, E, R> Map<T, R> mapValues(final Map<? extends T, ? extends E> sourceMap,
                                                final BiFunction<? super T, ? super E, ? extends R> mapFunction) {
        return mapValues(
                sourceMap,
                mapFunction,
                HashMap::new
        );
    }


    /**
     * Builds a new {@link Map} by applying a function to all values of {@code sourceMap}.
     *
     * <pre>
     * Example:
     *
     *   Parameters:                    Result:
     *    [(1, "A"), (3, "C")]           [(1, 2), (3, 4)]
     *    (k, v) -> k + v.length()
     *    HashMap::new
     * </pre>
     *
     * @param sourceMap
     *    {@link Map} to used as source of the new one
     * @param mapFunction
     *    {@link BiFunction} used to transform given {@code sourceMap} values
     * @param mapFactory
     *    {@link Supplier} of the {@link Map} used to store the returned elements.
     *    If {@code null} then {@link HashMap}
     *
     * @return updated {@link Map}
     *
     * @throws IllegalArgumentException if {@code mapFunction} is {@code null} and {@code sourceMap} is not empty.
     */
    public static <T, E, R> Map<T, R> mapValues(final Map<? extends T, ? extends E> sourceMap,
                                                final BiFunction<? super T, ? super E, ? extends R> mapFunction,
                                                final Supplier<Map<T, R>> mapFactory) {
        final Supplier<Map<T, R>> finalMapFactory = getFinalMapFactory(mapFactory);
        if (CollectionUtils.isEmpty(sourceMap)) {
            return finalMapFactory.get();
        }
        Assert.notNull(mapFunction, "mapFunction must be not null");
        return sourceMap.entrySet()
                .stream()
                .collect(
                        toMapNullableValues(
                                Map.Entry::getKey,
                                entry ->
                                        mapFunction.apply(
                                                entry.getKey(),
                                                entry.getValue()
                                        ),
                                finalMapFactory
                        )
                );
    }


    /**
     * Finds the first element of provided {@link Map} which yields the largest value measured by given {@link Comparator}.
     *
     * <pre>
     * Example:
     *
     *   Parameters:                                   Result:
     *    [(1, "Hi"), (3, "Hello"), (5, "World")]       Optional((5, "World"))
     *    Map.Entry.comparingByKey();
     * </pre>
     *
     * @param sourceMap
     *    {@link Map} used to find the largest element
     * @param comparator
     *    {@link Comparator} to be used for comparing elements
     *
     * @return {@link Optional} of {@link Map.Entry} containing the largest element using {@code comparator},
     *         {@link Optional#empty()} if {@code sourceMap} has no elements.
     *
     * @throws IllegalArgumentException if {@code comparator} is {@code null}
     */
    public static <T, E> Optional<Map.Entry<T, E>> max(final Map<? extends T, ? extends E> sourceMap,
                                                       final Comparator<Map.Entry<? extends T, ? extends E>> comparator) {
        Assert.notNull(comparator, "comparator must be not null");
        return ofNullable(sourceMap)
                .map(m -> {
                    Map.Entry<T, E> largestElement = null;
                    for (var entry: m.entrySet()) {
                        Map.Entry<T, E> currentElement = (Map.Entry<T, E>) entry;
                        if (isNull(largestElement)) {
                            largestElement = currentElement;
                        } else {
                            largestElement =
                                    0 > comparator.compare(largestElement, currentElement)
                                            ? currentElement
                                            : largestElement;
                        }
                    }
                    return largestElement;
                });
    }


    /**
     *    Finds the largest element of provided {@link Map}. To avoid {@link NullPointerException}, {@link Comparable}
     * implementation required in the type E, will be overwritten by:
     *
     *       <pre>
     *          Comparator.nullsFirst(
     *             Comparator.naturalOrder()
     *          )
     *       </pre>
     *
     *    In that way, {@code null} values will be considered the smallest ones in the returned {@link Optional}·
     * If you still want to avoid this default behaviour, you can use the alternative method:
     *
     *       <pre>
     *          maxValue(
     *             sourceMap,
     *             comparator          // Comparator.naturalOrder() uses Comparable definition provided by E class
     *          )
     *       </pre>
     *
     * <pre>
     * Example:
     *
     *   Parameters:                                   Result:
     *    [(1, "Hi"), (3, "Hello"), (5, "World")]       Optional("World")
     * </pre>
     *
     * @param sourceMap
     *    {@link Map} used to find the largest value
     *
     * @return {@link Optional} with largest value using developed {@link Comparable} in the class of value's instances,
     *         {@link Optional#empty()} if {@code sourceMap} has no elements or its largest value is {@code null}.
     */
    public static <T, E extends Comparable<? super E>> Optional<E> maxValue(final Map<? extends T, ? extends E> sourceMap) {
        return ofNullable(sourceMap)
                .flatMap(m ->
                        CollectionUtil.max(
                                m.values()
                        )
                );
    }


    /**
     * Finds the first value of provided {@link Map} which yields the largest value measured by given {@link Comparator}.
     *
     * <pre>
     * Example:
     *
     *   Parameters:                                   Result:
     *    [(1, "Hi"), (3, "Hello"), (5, "World")]       Optional("World")
     *    String::compareTo
     * </pre>
     *
     * @param sourceMap
     *    {@link Map} used to find the largest value
     * @param comparator
     *    {@link Comparator} to be used for comparing values
     *
     * @return {@link Optional} with largest value using {@code comparator},
     *         {@link Optional#empty()} if {@code sourceMap} has no elements.
     *
     * @throws IllegalArgumentException if {@code comparator} is {@code null}
     */
    public static <T, E> Optional<E> maxValue(final Map<? extends T, ? extends E> sourceMap,
                                              final Comparator<? super E> comparator) {
        Assert.notNull(comparator, "comparator must be not null");
        return ofNullable(sourceMap)
                .flatMap(m ->
                        m.values()
                                .stream()
                                .max(comparator)
                );
    }


    /**
     * Finds the first element of provided {@link Map} which yields the smallest value measured by given {@link Comparator}.
     *
     * <pre>
     * Example:
     *
     *   Parameters:                                   Result:
     *    [(1, "Hi"), (3, "Hello"), (5, "World")]       Optional((1, "Hi"))
     *    (t1, t2) -> t1._1.compareTo(t2._1)
     * </pre>
     *
     * @param sourceMap
     *    {@link Map} used to find the smallest element
     * @param comparator
     *    {@link Comparator} to be used for comparing elements
     *
     * @return {@link Optional} of {@link Map.Entry} containing the smallest value using {@code comparator},
     *         {@link Optional#empty()} if {@code sourceMap} has no elements.
     *
     * @throws IllegalArgumentException if {@code comparator} is {@code null}
     */
    public static <T, E> Optional<Map.Entry<T, E>> min(final Map<? extends T, ? extends E> sourceMap,
                                                       final Comparator<Map.Entry<? extends T, ? extends E>> comparator) {
        Assert.notNull(comparator, "comparator must be not null");
        return ofNullable(sourceMap)
                .map(m -> {
                    Map.Entry<T, E> smallestElement = null;
                    for (var entry: m.entrySet()) {
                        Map.Entry<T, E> currentElement = (Map.Entry<T, E>) entry;
                        if (isNull(smallestElement)) {
                            smallestElement = currentElement;
                        } else {
                            smallestElement =
                                    0 < comparator.compare(smallestElement, currentElement)
                                            ? currentElement
                                            : smallestElement;
                        }
                    }
                    return smallestElement;
                });
    }


    /**
     *    Finds the smallest element of provided {@link Map}. To avoid {@link NullPointerException}, {@link Comparable}
     * implementation required in the type E, will be overwritten by:
     *
     *       <pre>
     *          Comparator.nullsLast(
     *             Comparator.naturalOrder()
     *          )
     *       </pre>
     *
     *    In that way, {@code null} values will be considered the largest ones in the returned {@link Optional}·
     * If you still want to avoid this default behaviour, you can use the alternative method:
     *
     *       <pre>
     *          minValue(
     *             sourceMap,
     *             comparator          // Comparator.naturalOrder() uses Comparable definition provided by E class
     *          )
     *       </pre>
     *
     * <pre>
     * Example:
     *
     *   Parameters:                                   Result:
     *    [(1, "Hi"), (3, "Hello"), (5, "World")]       Optional("World")
     * </pre>
     *
     * @param sourceMap
     *    {@link Map} used to find the smallest value
     *
     * @return {@link Optional} with smallest value using developed {@link Comparable} in the class of value's instances,
     *         {@link Optional#empty()} if {@code sourceMap} has no elements or its largest value is {@code null}.
     */
    public static <T, E extends Comparable<? super E>> Optional<E> minValue(final Map<? extends T, ? extends E> sourceMap) {
        return ofNullable(sourceMap)
                .flatMap(m ->
                        CollectionUtil.min(
                                m.values()
                        )
                );
    }


    /**
     * Finds the first value of provided {@link Map} which yields the smallest value measured by given {@link Comparator}.
     *
     * <pre>
     * Example:
     *
     *   Parameters:                                   Result:
     *    [(1, "Hi"), (3, "Hello"), (5, "World")]       Optional("Hello")
     *    String::compareTo
     * </pre>
     *
     * @param sourceMap
     *    {@link Map} used to find the smallest value
     * @param comparator
     *    {@link Comparator} to be used for comparing values
     *
     * @return {@link Optional} with smallest value using {@code comparator},
     *         {@link Optional#empty()} if {@code sourceMap} has no elements.
     *
     * @throws IllegalArgumentException if {@code comparator} is {@code null}
     */
    public static <T, E> Optional<E> minValue(final Map<? extends T, ? extends E> sourceMap,
                                              final Comparator<? super E> comparator) {
        Assert.notNull(comparator, "comparator must be not null");
        return ofNullable(sourceMap)
                .flatMap(m ->
                        m.values()
                                .stream()
                                .min(comparator)
                );
    }


    /**
     *    Returns a {@link Map} of {@link Boolean} as key, on which {@code true} contains all elements that satisfy given
     * {@code discriminator} and {@code false}, all elements that do not.
     *
     * <pre>
     * Example:
     *
     *   Parameters:                       Result:
     *    [(1, "Hi"), (2, "Hello")]         [(true,  [(2, "Hello")])
     *    (k, v) -> k % 2 == 0               (false, [(1, "Hi")])]
     * </pre>
     *
     * @param sourceMap
     *    {@link Map} to filter
     * @param discriminator
     *    {@link BiPredicate} used to split the elements of {@code sourceMap}
     *
     * @return {@link Map}
     */
    public static <T, E> Map<Boolean, Map<T, E>> partition(final Map<? extends T, ? extends E> sourceMap,
                                                           final BiPredicate<? super T, ? super E> discriminator) {
        return partition(
                sourceMap,
                discriminator,
                HashMap::new
        );
    }


    /**
     *    Returns a {@link Map} of {@link Boolean} as key, on which {@code true} contains all elements that satisfy given
     * {@code discriminator} and {@code false}, all elements that do not.
     *
     * <pre>
     * Example:
     *
     *   Parameters:                       Result:
     *    [(1, "Hi"), (2, "Hello")]         [(true,  [(2, "Hello")])
     *    (k, v) -> k % 2 == 0               (false, [(1, "Hi")])]
     *    HashMap::new
     * </pre>
     *
     * @param sourceMap
     *    {@link Map} to filter
     * @param discriminator
     *    {@link BiPredicate} used to split the elements of {@code sourceMap}
     * @param mapFactory
     *    {@link Supplier} of the {@link Map} used to store the values inside returned {@link Map}.
     *    If {@code null} then {@link HashMap}
     *
     * @return {@link Map}
     */
    public static <T, E> Map<Boolean, Map<T, E>> partition(final Map<? extends T, ? extends E> sourceMap,
                                                           final BiPredicate<? super T, ? super E> discriminator,
                                                           final Supplier<Map<T, E>> mapFactory) {
        final Supplier<Map<T, E>> finalMapFactory = getFinalMapFactory(mapFactory);
        Map<Boolean, Map<T, E>> result = new HashMap<>() {{
            put(Boolean.TRUE, finalMapFactory.get());
            put(Boolean.FALSE, finalMapFactory.get());
        }};
        if (!CollectionUtils.isEmpty(sourceMap) && nonNull(discriminator)) {
            sourceMap.forEach(
                    (k, v) ->
                            result.get(discriminator.test(k, v))
                                    .put(k, v)
            );
        }
        return result;
    }


    /**
     *    Using the provided {@code sourceMap}, return all elements beginning at index {@code from} and afterwards,
     * up to index {@code until} (excluding this one).
     *
     * <pre>
     * Example 1:
     *
     *   Parameters:                      Result:
     *    [(1, "Hi"), (2, "Hello")]        [(2, "Hello")]
     *    1
     *    3
     * </pre>
     *
     * <pre>
     * Example 2:
     *
     *   Parameters:                      Result:
     *    [(1, "Hi"), (2, "Hello")]        [(1, "Hi")]
     *    0
     *    1
     * </pre>
     *
     * <pre>
     * Example 3:
     *
     *   Parameters:                      Result:
     *    [(1, "Hi"), (2, "Hello")]        [(1, "Hi"), (2, "Hello")]
     *    -1
     *    2
     * </pre>
     *
     * @param sourceMap
     *    {@link Map} to slice
     * @param from
     *    Lower limit of the chunk to extract from provided {@link Map} (starting from {@code 0})
     * @param until
     *    Upper limit of the chunk to extract from provided {@link Map} (up to {@link Map#size()})
     *
     * @return {@link Map}
     *
     * @throws IllegalArgumentException if {@code from} is greater than {@code until} or {@code zero}
     */
    public static <T, E> Map<T, E> slice(final Map<? extends T, ? extends E> sourceMap,
                                         final int from,
                                         final int until) {
        Assert.isTrue(0 <= from, "from cannot be a negative value");
        Assert.isTrue(
                from < until,
                format("from: %d must be lower than to: %d",
                        from, until
                )
        );
        if (CollectionUtils.isEmpty(sourceMap) || from > sourceMap.size() - 1) {
            return new HashMap<>();
        }
        final int finalUntil = Math.min(sourceMap.size(), until);
        int i = 0;
        Map<T, E> result = new LinkedHashMap<>(
                Math.max(
                        finalUntil - from,
                        finalUntil - from - 1
                )
        );
        for (var entry : sourceMap.entrySet()) {
            if (i >= finalUntil) {
                break;
            }
            if (i >= from) {
                result.put(
                        entry.getKey(),
                        entry.getValue()
                );
            }
            i++;
        }
        return result;
    }


    /**
     * Loops through the provided {@link Map} one position every time, returning sublists with {@code size}
     *
     * <pre>
     * Example 1:
     *
     *   Parameters:                          Result:
     *    [(1, "A"), (3, "C")]                 [[(1, "A"), (3, "C")]]
     *    5
     * </pre>
     *
     * <pre>
     * Example 2:
     *
     *   Parameters:                          Result:
     *    [(1, "A"), (3, "C"), (8, "Z")]       [[(1, "A"), (3, "C")], [(3, "C"), (8, "Z")]]
     *    2
     * </pre>
     *
     * @param sourceMap
     *    {@link Map} to slide
     * @param size
     *    Size of every sublist
     *
     * @return {@link List} of {@link Map}s
     *
     * @throws IllegalArgumentException if {@code size} is lower than 0
     */
    public static <T, E> List<Map<T, E>> sliding(final Map<T, E> sourceMap,
                                                 final int size) {
        Assert.isTrue(0 <= size, "size must be a positive value");
        if (CollectionUtils.isEmpty(sourceMap) || 0 == size) {
            return new ArrayList<>();
        }
        if (size >= sourceMap.size()) {
            return asList(sourceMap);
        }
        final int expectedSize = sourceMap.size() - size + 1;

        List<Map<T, E>> slides = IntStream.range(0, expectedSize)
                .mapToObj(index -> new LinkedHashMap<T, E>())
                .collect(
                        toList()
                );
        int i = 0;
        for (var entry : sourceMap.entrySet()) {
            int xCoordinate = Math.min(i, expectedSize - 1);
            int yCoordinate = slides.get(xCoordinate).size();

            int window = xCoordinate;
            while (0 <= window && size > yCoordinate) {
                slides.get(window)
                        .put(
                                entry.getKey(),
                                entry.getValue()
                        );
                yCoordinate = slides.get(window).size();
                window--;
            }
            i++;
        }
        return slides;
    }


    /**
     *    Sorts a {@link Map} based on provided {@link Comparator} {@code comparator}. Using {@link SortedMap} implementations
     * you will be able to provide a {@link Comparator} to sort only the keys, that is not the case in this method, it allows
     * to deal with a key/value {@link Comparator}, returning a sorted {@link Map} based on it.
     * <p>
     *    By default, ordering  the maps, if the key exists its value will be updated with the latest one.
     *
     * <pre>
     * Example:
     *
     *   Parameters:                            Result:
     *    Map.Entry.comparingByKey();            [(1, "Yes"), (2, "No"), (3, "Hello")]
     *    [(1, "Hi"), (3, "Hello")]
     *    [(1, "Yes"), (2, "No")]
     * </pre>
     *
     * @param comparator
     *    {@link Comparator} to be used for comparing elements (both keys and values)
     * @param maps
     *    {@link Map}s to sort
     *
     * @return sorted {@link Map} with the elements of {@code maps}
     *
     * @throws IllegalArgumentException if {@code comparator} is {@code null} and {@code maps} has elements
     */
    @SafeVarargs
    public static <T, E> Map<T, E> sort(final Comparator<Map.Entry<? extends T, ? extends E>> comparator,
                                        final Map<? extends T, ? extends E> ...maps) {
        return sort(
                comparator,
                overwriteWithNew(),
                maps
        );
    }


    /**
     *    Sorts a {@link Map} based on provided {@link Comparator} {@code comparator}. Using {@link SortedMap} implementations
     * you will be able to provide a {@link Comparator} to sort only the keys, that is not the case in this method, it allows
     * to deal with a key/value {@link Comparator}, returning a sorted {@link Map} based on it.
     *
     * <pre>
     * Example:
     *
     *   Parameters:                            Result:
     *    Map.Entry.comparingByKey();            [(1, "Hi"), (2, "No"), (3, "Hello")]
     *    (oldV, newV) -> oldV
     *    [(1, "Hi"), (3, "Hello")]
     *    [(1, "Yes"), (2, "No")]
     * </pre>
     *
     * @param comparator
     *    {@link Comparator} to be used for comparing elements (both keys and values)
     * @param mergeValueFunction
     *    {@link BinaryOperator} used to resolve collisions between values associated with the same key. If no one is
     *    provided, by default last value will be used
     * @param maps
     *    {@link Map}s to sort
     *
     * @return sorted {@link Map} with the elements of {@code maps}
     *
     * @throws IllegalArgumentException if {@code comparator} is {@code null} and {@code maps} has elements
     */
    @SafeVarargs
    public static <T, E> Map<T, E> sort(final Comparator<Map.Entry<? extends T, ? extends E>> comparator,
                                        final BinaryOperator<E> mergeValueFunction,
                                        final Map<? extends T, ? extends E> ...maps) {
        if (ObjectUtil.isEmpty(maps)) {
            return new LinkedHashMap<>();
        }
        Assert.notNull(comparator, "comparator must be not null");
        final BinaryOperator<E> finalMergeValueFunction = ObjectUtil.getOrElse(
                mergeValueFunction,
                overwriteWithNew()
        );
        return concat(
                  LinkedHashMap::new,
                  finalMergeValueFunction,
                  maps
               )
               .entrySet()
               .stream()
               .sorted(comparator)
               .collect(
                       toMapNullableValues(
                               Map.Entry::getKey,
                               Map.Entry::getValue,
                               finalMergeValueFunction,
                               LinkedHashMap::new
                        )
                );
    }


    /**
     * Splits the given {@link Map} in sublists with a size equal to the given {@code size}
     *
     * <pre>
     * Example 1:
     *
     *   Parameters:                          Result:
     *    [(1, "A"), (3, "C"), (8, "Z")]       [[(1, "A"), (3, "C")], [(8, "Z")]]
     *    2
     * </pre>
     *
     * <pre>
     * Example 2:
     *
     *   Parameters:                          Result:
     *    [(1, "A"), (3, "C")]                 [[(1, "A"), (3, "C")]]
     *    3
     * </pre>
     *
     * @param sourceMap
     *    {@link Map} to split
     * @param size
     *    Size of every sublist
     *
     * @return {@link List} of {@link Map}s
     *
     * @throws IllegalArgumentException if {@code size} is lower than 0
     */
    public static <T, E> List<Map<T, E>> split(final Map<? extends T, ? extends E> sourceMap,
                                               final int size) {
        Assert.isTrue(0 <= size, "size must be a positive value");
        if (CollectionUtils.isEmpty(sourceMap) || 0 == size) {
            return new ArrayList<>();
        }
        final int expectedSize = 0 == sourceMap.size() % size
                ? sourceMap.size() / size
                : (sourceMap.size() / size) + 1;

        List<Map<T, E>> splits = IntStream.range(0, expectedSize)
                .mapToObj(index -> new LinkedHashMap<T, E>())
                .collect(toList());

        int i = 0, currentSplit = 0;
        for (var entry : sourceMap.entrySet()) {
            splits.get(currentSplit)
                    .put(
                            entry.getKey(),
                            entry.getValue()
                    );
            i++;
            if (i == size) {
                currentSplit++;
                i = 0;
            }
        }
        return splits;
    }


    /**
     *    Returns a {@link Map} with the elements of provided {@code sourceMap} that satisfy the {@link BiPredicate}
     * {@code filterPredicate}.
     *
     * <pre>
     * Example:
     *
     *   Parameters:                    Result:
     *    [(1, "Hi"), (2, "Hello")]      [(1, "Hi")]
     *    (k, v) -> k % 2 == 0
     * </pre>
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to filter
     * @param filterPredicate
     *    {@link BiPredicate} to filter elements from {@code sourceMap}
     *
     * @return {@link Map}
     */
    public static <T, E> Map<T, E> takeWhile(final Map<? extends T, ? extends E> sourceMap,
                                             final BiPredicate<? super T, ? super E> filterPredicate) {
        return takeWhile(
                sourceMap,
                filterPredicate,
                HashMap::new
        );
    }


    /**
     *    Returns a {@link Map} with the elements of provided {@code sourceMap} that satisfy the {@link BiPredicate}
     * {@code filterPredicate}.
     *
     * <pre>
     * Example:
     *
     *   Parameters:                    Result:
     *    [(1, "Hi"), (2, "Hello")]      [(1, "Hi")]
     *    (k, v) -> k % 2 == 0
     *    HashMap::new
     * </pre>
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to filter
     * @param filterPredicate
     *    {@link BiPredicate} to filter elements from {@code sourceMap}
     *
     * @return {@link Map}
     */
    public static <T, E> Map<T, E> takeWhile(final Map<? extends T, ? extends E> sourceMap,
                                             final BiPredicate<? super T, ? super E> filterPredicate,
                                             final Supplier<Map<T, E>> mapFactory) {
        final Supplier<Map<T, E>> finalMapFactory = getFinalMapFactory(mapFactory);
        if (CollectionUtils.isEmpty(sourceMap)) {
            return finalMapFactory.get();
        }
        final BiPredicate<? super T, ? super E> finalFilterPredicate = ObjectUtil.getOrElse(
                filterPredicate,
                biAlwaysTrue()
        );
        return sourceMap.entrySet()
                .stream()
                .filter(entry ->
                        finalFilterPredicate.test(
                                entry.getKey(),
                                entry.getValue()
                        )
                )
                .collect(
                        toMapNullableValues(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                finalMapFactory
                        )
                );
    }


    /**
     *    Returns provided {@link Supplier} of {@link Map} {@code mapFactory} if not {@code null},
     * {@link Supplier} of {@link HashMap} otherwise.
     *
     * @param mapFactory
     *    {@link Supplier} of {@link Map}
     *
     * @return {@link Supplier} of {@link Map}
     */
    private static <T, E> Supplier<Map<T, E>> getFinalMapFactory(final Supplier<Map<T, E>> mapFactory) {
        return ObjectUtil.getOrElse(
                mapFactory,
                HashMap::new
        );
    }

}
