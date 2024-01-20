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
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.spring5microservices.common.util.CollectorsUtil.toMapNullableValues;
import static com.spring5microservices.common.util.FunctionUtil.fromBiFunctionToMapEntryFunction;
import static com.spring5microservices.common.util.FunctionUtil.overwriteWithNew;
import static com.spring5microservices.common.util.PredicateUtil.biAlwaysTrue;
import static com.spring5microservices.common.util.PredicateUtil.fromBiPredicateToMapEntryPredicate;
import static com.spring5microservices.common.util.PredicateUtil.getOrAlwaysTrue;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

@UtilityClass
public class MapUtil {

    /**
     *    Returns a new {@link Map} using the given {@code sourceMap}, applying to its elements the composed
     * {@link BiFunction} {@code secondMapper}({@code firstMapper}(x))
     *
     * <pre>
     *    andThen(                                                             Result:
     *       [(1, "AGTF"), (3, "CD")],                                          [(2, "4"), (4, "2")]
     *       (k, v) -> new AbstractMap.SimpleEntry<>(k, v.length()),
     *       (k, v) -> new AbstractMap.SimpleEntry<>(k + 1, v.toString())
     *    )
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
     *    Returns a new {@link Map} using the given {@code sourceMap}, applying to its elements the composed
     * {@link BiFunction} {@code secondMapper}({@code firstMapper}(x))
     *
     * @apiNote
     *    If {@code mapFactory} is {@code null} then {@link HashMap} will be used.
     *
     * <pre>
     *    andThen(                                                             Result:
     *       [(1, "AGTF"), (3, "CD")],                                          [(2, "4"), (4, "2")]
     *       (k, v) -> new AbstractMap.SimpleEntry<>(k, v.length()),
     *       (k, v) -> new AbstractMap.SimpleEntry<>(k + 1, v.toString()),
     *       HashMap::new
     *    )
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
     * @apiNote
     *    If {@code filterPredicate} is {@code null} then all elements of {@code sourceMap} will be updated using
     * {@code defaultMapper}.
     *
     * <pre>
     *    applyOrElse(                                               Result:
     *       [("A", 1), ("B", 2)],                                    [("A", 2), ("B", 4)]
     *       (k, v) -> v % 2 == 1,
     *       (k, v) -> new AbstractMap.SimpleEntry<>(k, v + 1),
     *       (k, v) -> new AbstractMap.SimpleEntry<>(k, v * 2)
     *    )
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
     * @return new {@link Map} from applying the given {@code defaultMapper} to each element of {@code sourceMap} that
     *         verifies {@code filterPredicate} and collecting the results or {@code orElseMapper} otherwise
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
     * @apiNote
     *    If {@code filterPredicate} is {@code null} then all elements of {@code sourceMap} will be updated using
     * {@code defaultMapper}. If {@code mapFactory} is {@code null} then {@link HashMap} will be used.
     *
     * <pre>
     *    applyOrElse(                                               Result:
     *       [("A", 1), ("B", 2)],                                    [("A", 2), ("B", 4)]
     *       (k, v) -> v % 2 == 1,
     *       (k, v) -> new AbstractMap.SimpleEntry<>(k, v + 1),
     *       (k, v) -> new AbstractMap.SimpleEntry<>(k, v * 2),
     *       HashMap::new
     *    )
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
     * @return new {@link Map} from applying the given {@code defaultMapper} to each element of {@code sourceMap} that
     *         verifies {@code filterPredicate} and collecting the results or {@code orElseMapper} otherwise
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
        return applyOrElse(
                sourceMap,
                PartialFunction.of(
                        getOrAlwaysTrue(filterPredicate),
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
     *    applyOrElse(                                               Result:
     *       [("A", 1), ("B", 2)],                                    [("A", 2), ("B", 4)]
     *       PartialFunction.of(
     *          e -> null != e && 1 == e.getValue() % 2,
     *          e -> null == e
     *             ? null
     *             : new AbstractMap.SimpleEntry<>(
     *                 e.getKey(),
     *                 null == e.getValue()
     *                    ? 0
     *                    : e.getValue() + 1
     *               )
     *       ),
     *       (k, v) -> new AbstractMap.SimpleEntry<>(k, v * 2)
     *    )
     * </pre>
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to filter and transform
     * @param partialFunction
     *    {@link PartialFunction} to filter and transform elements of {@code sourceMap}
     * @param orElseMapper
     *    {@link BiFunction} to transform elements of {@code sourceMap} do not verify {@link PartialFunction#isDefinedAt(Object)}
     *
     * @return new {@link Map} from applying the given {@link PartialFunction} to each element of {@code sourceMap}
     *         on which it is defined and collecting the results, {@code orElseMapper} otherwise
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
     * @apiNote
     *    If {@code mapFactory} is {@code null} then {@link HashMap} will be used.
     *
     * <pre>
     *    applyOrElse(                                               Result:
     *       [("A", 1), ("B", 2)],                                    [("A", 2), ("B", 4)]
     *       PartialFunction.of(
     *          e -> null != e && 1 == e.getValue() % 2,
     *          e -> null == e
     *             ? null
     *             : new AbstractMap.SimpleEntry<>(
     *                 e.getKey(),
     *                 null == e.getValue()
     *                    ? 0
     *                    : e.getValue() + 1
     *               )
     *       ),
     *       (k, v) -> new AbstractMap.SimpleEntry<>(k, v * 2),
     *       HashMap::new
     *    )
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
     * @return new {@link Map} from applying the given {@link PartialFunction} to each element of {@code sourceMap}
     *         on which it is defined and collecting the results, {@code orElseMapper} otherwise
     *
     * @throws IllegalArgumentException if {@code partialFunction} or {@code orElseMapper} is {@code null}
     *                                  with a not empty {@code sourceMap}
     */
    @SuppressWarnings("unchecked")
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
     * Returns a new {@link Map} after applying to {@code sourceMap}:
     * <p>
     *  - Filter its elements using {@code filterPredicate}
     *  - Transform its filtered elements using {@code mapFunction}
     *
     * @apiNote
     *    If {@code filterPredicate} is {@code null} then all elements will be transformed.
     *
     * <pre>
     *    collect(                                                             Result:
     *       [(1, "Hi"), (2, "Hello")],                                         [(3, 4)]
     *       (k, v) -> k % 2 == 0,
     *       (k, v) -> new AbstractMap.SimpleEntry<>(k + 1, v.length())
     *    )
     * </pre>
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to filter and transform
     * @param filterPredicate
     *    {@link BiPredicate} to filter elements of {@code sourceMap}
     * @param mapFunction
     *    {@link BiFunction} to transform filtered elements of {@code sourceMap}
     *
     * @return new {@link Map} from applying the given {@link BiFunction} to each element of {@code sourceMap}
     *         on which {@link BiPredicate} returns {@code true} and collecting the results
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
     * Returns a new {@link Map} after applying to {@code sourceMap}:
     * <p>
     *  - Filter its elements using {@code filterPredicate}
     *  - Transform its filtered elements using {@code mapFunction}
     *
     * @apiNote
     *    If {@code filterPredicate} is {@code null} then all elements will be transformed. If {@code mapFactory} is
     * {@code null} then {@link HashMap} will be used.
     *
     * <pre>
     *    collect(                                                             Result:
     *       [(1, "Hi"), (2, "Hello")],                                         [(3, 4)]
     *       (k, v) -> k % 2 == 0,
     *       (k, v) -> new AbstractMap.SimpleEntry<>(k + 1, v.length()),
     *       HashMap::new
     *    )
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
     * @return new {@link Map} from applying the given {@link BiFunction} to each element of {@code sourceMap}
     *         on which {@link BiPredicate} returns {@code true} and collecting the results
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
        return collect(
                sourceMap,
                PartialFunction.of(
                        getOrAlwaysTrue(filterPredicate),
                        mapFunction
                ),
                mapFactory
        );
    }


    /**
     * Returns a new {@link Map} after applying to {@code sourceMap}:
     * <p>
     *  - Filter its elements using {@link PartialFunction#isDefinedAt(Object)} of {@code partialFunction}
     *  - Transform its filtered elements using {@link PartialFunction#apply(Object)} of {@code partialFunction}
     *
     * <pre>
     *    collect(                                                   Result:
     *       [(1, "Hi"), (2, "Hello")],                               [(1, 2)]
     *       PartialFunction.of(
     *          e -> null != e && 1 == e.getKey() % 2,
     *          e -> null == e
     *             ? null
     *             : new AbstractMap.SimpleEntry<>(
     *                 e.getKey(),
     *                 null == e.getValue()
     *                    ? 0
     *                    : e.getValue().length()
     *               )
     *       )
     *    )
     * </pre>
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to filter and transform
     * @param partialFunction
     *    {@link PartialFunction} to filter and transform elements from {@code sourceMap}
     *
     * @return new {@link Map} from applying the given {@link PartialFunction} to each element of {@code sourceMap}
     *         on which it is defined and collecting the results
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
     * Returns a new {@link Map} after applying to {@code sourceMap}:
     * <p>
     *  - Filter its elements using {@link PartialFunction#isDefinedAt(Object)} of {@code partialFunction}
     *  - Transform its filtered elements using {@link PartialFunction#apply(Object)} of {@code partialFunction}
     *
     * @apiNote
     *    If {@code mapFactory} is {@code null} then {@link HashMap} will be used.
     *
     * <pre>
     *    collect(                                                   Result:
     *       [(1, "Hi"), (2, "Hello")],                               [(1, 2)]
     *       PartialFunction.of(
     *          e -> null != e && 1 == e.getKey() % 2,
     *          e -> null == e
     *             ? null
     *             : new AbstractMap.SimpleEntry<>(
     *                 e.getKey(),
     *                 null == e.getValue()
     *                    ? 0
     *                    : e.getValue().length()
     *               )
     *       ),
     *       HashMap::new
     *    )
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
     * @return new {@link Map} from applying the given {@link PartialFunction} to each element of {@code sourceMap}
     *         on which it is defined and collecting the results
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
     *    collectFirst(                                              Result:
     *       [(1, "Hi"), (2, "Hello")],                               Optional[(2, 4)]
     *       PartialFunction.of(
     *          e -> null != e && 0 == e.getKey() % 2,
     *          e -> null == e
     *             ? null
     *             : new AbstractMap.SimpleEntry<>(
     *                 e.getKey(),
     *                 null == e.getValue()
     *                    ? 0
     *                    : e.getValue().length()
     *               )
     *       )
     *    )
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
     *    concat(                                          Result:
     *       [(1, "Hi"), (2, "Hello")],                     [(1, "Hi"), (2, "Dear"), (5, "World")]
     *       [(2, "Dear"), (5, "World")]
     *    )
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
     *    concat(                                          Result:
     *       HashMap::new,                                  [(1, "Hi"), (2, "Dear"), (5, "World")]
     *       [(1, "Hi"), (2, "Hello")],
     *       [(2, "Dear"), (5, "World")]
     *    )
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
     * @apiNote
     *    If {@code mapFactory} is {@code null} then {@link HashMap} will be used.
     *
     * <pre>
     *    concat(                                          Result:
     *       HashMap::new,                                  [(1, "Hi"), (2, "Hello"), (5, "World")]
     *       (oldV, newV) -> oldV,
     *       [(1, "Hi"), (2, "Hello")],
     *       [(2, "Dear"), (5, "World")]
     *    )
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
     * Returns a new {@link Map} containing the elements of provided {@code sourceMap}.
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to copy
     *
     * @return {@link Map} containing all elements included in {@code sourceMap}
     */
    public static <T, E> Map<T, E> copy(final Map<? extends T, ? extends E> sourceMap) {
        return copy(
                sourceMap,
                HashMap::new
        );
    }


    /**
     * Returns a new {@link Map} containing the elements of provided {@code sourceMap}.
     *
     * @apiNote
     *    If {@code mapFactory} is {@code null} then {@link HashMap} will be used.
     *
     * <pre>
     *    copy(                                            Result:
     *       [(1, "Hi"), (2, "Hello")],                     [(1, "Hi"), (2, "Hello")]
     *       HashMap::new
     *    )
     * </pre>
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to filter
     * @param mapFactory
     *    {@link Supplier} of the {@link Map} used to store the returned elements
     *    If {@code null} then {@link HashMap}
     *
     * @return {@link Map} containing all elements included in {@code sourceMap}
     */
    public static <T, E> Map<T, E> copy(final Map<? extends T, ? extends E> sourceMap,
                                        final Supplier<Map<T, E>> mapFactory) {
        final Map<T, E> result =  getFinalMapFactory(mapFactory).get();
        if (!CollectionUtils.isEmpty(sourceMap)) {
            result.putAll(sourceMap);
        }
        return result;
    }


    /**
     * Counts the number of elements in the {@code sourceMap} which satisfy the {@code filterPredicate}.
     *
     * <pre>
     *    count(                                           Result:
     *       [(1, "Hi"), (2, "Hello")],                     1
     *       (k, v) -> 0 == k % 2
     *    )
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
     *    Returns a {@link Map} removing the longest prefix of elements included in {@code sourceMap} that satisfy
     * the {@link BiPredicate} {@code filterPredicate}.
     *
     * @apiNote
     *    If {@code filterPredicate} is {@code null} then all elements of {@code sourceMap} will be returned.
     *
     * <pre>
     *    dropWhile(                                                 Result:
     *       [(1, "Hi"), (2, "Hello"), (3, "World")],                 [(2, "Hello"), (3, "World")]
     *       (k, v) -> 1 == k % 2
     *    )
     * </pre>
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to filter
     * @param filterPredicate
     *    {@link BiPredicate} to filter elements from {@code sourceMap}
     *
     * @return the longest suffix of provided {@code sourceMap} whose first element does not satisfy {@code filterPredicate}
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
     *    Returns a {@link Map} removing the longest prefix of elements included in {@code sourceMap} that satisfy
     * the {@link BiPredicate} {@code filterPredicate}.
     *
     * @apiNote
     *    If {@code filterPredicate} is {@code null} then all elements of {@code sourceMap} will be returned. If
     * {@code mapFactory} is {@code null} then {@link HashMap} will be used.
     *
     * <pre>
     *    dropWhile(                                                 Result:
     *       [(1, "Hi"), (2, "Hello"), (3, "World")],                 [(2, "Hello"), (3, "World")]
     *       (k, v) -> 1 == k % 2,
     *       HashMap::new
     *    )
     * </pre>
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to filter
     * @param filterPredicate
     *    {@link BiPredicate} to filter elements from {@code sourceMap}
     * @param mapFactory
     *    {@link Supplier} of the {@link Map} used to store the returned elements
     *    If {@code null} then {@link HashMap}
     *
     * @return the longest suffix of provided {@code sourceMap} whose first element does not satisfy {@code filterPredicate}
     */
    public static <T, E> Map<T, E> dropWhile(final Map<? extends T, ? extends E> sourceMap,
                                             final BiPredicate<? super T, ? super E> filterPredicate,
                                             final Supplier<Map<T, E>> mapFactory) {
        if (CollectionUtils.isEmpty(sourceMap) || isNull(filterPredicate)) {
            return copy(
                    sourceMap,
                    mapFactory
            );
        }
        final Supplier<Map<T, E>> finalMapFactory = getFinalMapFactory(mapFactory);
        return sourceMap.entrySet()
                .stream()
                .dropWhile(entry ->
                        filterPredicate.test(
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
     *    Returns a {@link Map} with the elements of provided {@code sourceMap} that satisfy the {@link BiPredicate}
     * {@code filterPredicate}.
     *
     * @apiNote
     *    If {@code filterPredicate} is {@code null} then all elements of {@code sourceMap} will be returned.
     *
     * <pre>
     *    filter(                                          Result:
     *       [(1, "Hi"), (2, "Hello")],                     [(1, "Hi")]
     *       (k, v) -> 0 == k % 2
     *    )
     * </pre>
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to filter
     * @param filterPredicate
     *    {@link BiPredicate} to filter elements from {@code sourceMap}
     *
     * @return empty {@link Map} if {@code sourceMap} has no elements or no one verifies provided {@code filterPredicate},
     *         otherwise a new {@link Map} with the elements of {@code sourceMap} which verify {@code filterPredicate}
     */
    public static <T, E> Map<T, E> filter(final Map<? extends T, ? extends E> sourceMap,
                                          final BiPredicate<? super T, ? super E> filterPredicate) {
        return filter(
                sourceMap,
                filterPredicate,
                HashMap::new
        );
    }


    /**
     *    Returns a {@link Map} with the elements of provided {@code sourceMap} that satisfy the {@link BiPredicate}
     * {@code filterPredicate}.
     *
     * @apiNote
     *    If {@code filterPredicate} is {@code null} then all elements of {@code sourceMap} will be returned. If
     * {@code mapFactory} is {@code null} then {@link HashMap} will be used.
     *
     * <pre>
     *    filter(                                          Result:
     *       [(1, "Hi"), (2, "Hello")],                     [(1, "Hi")]
     *       (k, v) -> 0 == k % 2,
     *       HashMap::new
     *    )
     * </pre>
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to filter
     * @param filterPredicate
     *    {@link BiPredicate} to filter elements from {@code sourceMap}
     * @param mapFactory
     *    {@link Supplier} of the {@link Map} used to store the returned elements
     *    If {@code null} then {@link HashMap}
     *
     * @return empty {@link Map} if {@code sourceMap} has no elements or no one verifies provided {@code filterPredicate},
     *         otherwise a new {@link Map} with the elements of {@code sourceMap} which verify {@code filterPredicate}
     */
    public static <T, E> Map<T, E> filter(final Map<? extends T, ? extends E> sourceMap,
                                          final BiPredicate<? super T, ? super E> filterPredicate,
                                          final Supplier<Map<T, E>> mapFactory) {
        if (CollectionUtils.isEmpty(sourceMap) || isNull(filterPredicate)) {
            return copy(
                    sourceMap,
                    mapFactory
            );
        }
        final Supplier<Map<T, E>> finalMapFactory = getFinalMapFactory(mapFactory);
        return sourceMap.entrySet()
                .stream()
                .filter(entry ->
                        filterPredicate.test(
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
     *    Returns a {@link Map} removing the elements of provided {@code sourceMap} that satisfy the {@link BiPredicate}
     * {@code filterPredicate}.
     *
     * @apiNote
     *    If {@code filterPredicate} is {@code null} then all elements of {@code sourceCollection} will be returned.
     *
     * <pre>
     *    filterNot(                                       Result:
     *       [(1, "Hi"), (2, "Hello")],                     [(1, "Hi")]
     *       (k, v) -> 0 == k % 2
     *    )
     * </pre>
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to filter
     * @param filterPredicate
     *    {@link BiPredicate} to filter elements from {@code sourceMap}
     *
     * @return empty {@link Map} if {@code sourceMap} has no elements,
     *         otherwise a new {@link Map} with the elements of {@code sourceMap} which do not verify {@code filterPredicate}
     */
    public static <T, E> Map<T, E> filterNot(final Map<? extends T, ? extends E> sourceMap,
                                             final BiPredicate<? super T, ? super E> filterPredicate) {
        return filterNot(
                sourceMap,
                filterPredicate,
                HashMap::new
        );
    }


    /**
     *    Returns a {@link Map} removing the elements of provided {@code sourceMap} that satisfy the {@link BiPredicate}
     * {@code filterPredicate}.
     *
     * @apiNote
     *    If {@code filterPredicate} is {@code null} then all elements of {@code sourceMap} will be returned. If
     *  {@code mapFactory} is {@code null} then {@link HashMap} will be used.
     *
     * <pre>
     *    filterNot(                                       Result:
     *       [(1, "Hi"), (2, "Hello")],                     [(1, "Hi")]
     *       (k, v) -> 0 == k % 2,
     *       HashMap::new
     *    )
     * </pre>
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to filter
     * @param filterPredicate
     *    {@link BiPredicate} to filter elements from {@code sourceMap}
     * @param mapFactory
     *    {@link Supplier} of the {@link Map} used to store the returned elements
     *    If {@code null} then {@link HashMap}
     *
     * @return empty {@link Map} if {@code sourceMap} has no elements,
     *         otherwise a new {@link Map} with the elements of {@code sourceMap} which do not verify {@code filterPredicate}
     */
    public static <T, E> Map<T, E> filterNot(final Map<? extends T, ? extends E> sourceMap,
                                             final BiPredicate<? super T, ? super E> filterPredicate,
                                             final Supplier<Map<T, E>> mapFactory) {
        final BiPredicate<? super T, ? super E> finalFilterPredicate =
                isNull(filterPredicate)
                        ? null
                        : filterPredicate.negate();

        return filter(
                sourceMap,
                finalFilterPredicate,
                mapFactory
        );
    }


    /**
     * Finds the first element of the given {@link Map} satisfying the provided {@link BiPredicate}.
     *
     * <pre>
     *    find(                                            Result:
     *       [(1, "Hi"), (2, "Hello")]                      Optional((2, "Hello"))
     *       (k, v) -> 0 == k % 2
     *    )
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
     *    flatten(                                                   Result:
     *       [(1, ["Hi"]), (2, ["Hello", "World"])],                  [(1, "Hi"), (2, "Hello"), (2, "World")]
     *       (i, l) -> l.stream()
     *                  .map(elto -> Tuple2.of(i, elto))
     *                  .collect(toList())
     *    )
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
     * @apiNote
     *    If {@code collectionFactory} is {@code null} then {@link ArrayList} will be used.
     *
     * <pre>
     *    flatten(                                                   Result:
     *       [(1, ["Hi"]), (2, ["Hello", "Hello"])],                  [(1, "Hi"), (2, "Hello")]
     *       (i, l) -> l.stream()
     *                  .map(elto -> Tuple2.of(i, elto))
     *                  .collect(toList()),
     *       HashSet::new
     *    )
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
                .collect(
                        Collectors.toList()
                );
        return CollectionUtil.flatten(
                (Collection<Object>)result,
                collectionFactory
        );
    }


    /**
     *    Folds given {@link Map} values from the left, starting with {@code initialValue} and successively
     * calling {@code accumulator}.
     *
     * @apiNote
     *    If {@code sourceMap} or {@code accumulator} are {@code null} then {@code initialValue} is returned.
     *
     * <pre>
     *    foldLeft(                                        Result:
     *       [(1, "Hi"), (2, "Hello")],                     10
     *       0,
     *       (prev, k, v) -> prev + k + v.length()
     *    )
     * </pre>
     *
     * @param sourceMap
     *    {@link Map} with elements to combine
     * @param initialValue
     *    The initial value to start with
     * @param accumulator
     *    A {@link TriFunction} which combines elements
     *
     * @return result of inserting {code accumulator} between consecutive elements of {@code sourceMap}, going
     *         left to right with the start value {@code initialValue} on the left
     */
    public static <T, E, R> R foldLeft(final Map<? extends T, ? extends E> sourceMap,
                                       final R initialValue,
                                       final TriFunction<R, ? super T, ? super E, R> accumulator) {
        return foldLeft(
                sourceMap,
                biAlwaysTrue(),
                initialValue,
                accumulator
        );
    }


    /**
     *    Folds given {@link Map} values from the left, starting with {@code initialValue} and successively
     * calling {@code accumulator}.
     *
     * @apiNote
     *    If {@code sourceMap} or {@code accumulator} are {@code null} then {@code initialValue} is returned.
     * If {@code filterPredicate} is {@code null} then all elements will be used to calculate the final value.
     *
     * <pre>
     *    foldLeft(                                        Result:
     *       [(1, "Hi"), (2, "Hola"), (3, "World")]         10
     *       (k, v) -> 1 == k % 2,
     *       0,
     *       (prev, k, v) -> prev + k + v.length()
     *    )
     * </pre>
     *
     * @param sourceMap
     *    {@link Map} with elements to combine
     * @param filterPredicate
     *    {@link BiPredicate} used to filter elements of {@code sourceMap}
     * @param initialValue
     *    The initial value to start with
     * @param accumulator
     *    A {@link TriFunction} which combines elements
     *
     * @return result of inserting {code accumulator} between consecutive elements of {@code sourceMap}, going
     *         left to right with the start value {@code initialValue} on the left
     */
    public static <T, E, R> R foldLeft(final Map<? extends T, ? extends E> sourceMap,
                                       final BiPredicate<? super T, ? super E> filterPredicate,
                                       final R initialValue,
                                       final TriFunction<R, ? super T, ? super E, R> accumulator) {
        return ofNullable(sourceMap)
                .map(sm -> {
                    R result = initialValue;
                    if (nonNull(accumulator)) {
                        final BiPredicate<? super T, ? super E> finalFilterPredicate = getOrAlwaysTrue(filterPredicate);
                        for (var entry: sm.entrySet()) {
                            if (finalFilterPredicate.test(entry.getKey(), entry.getValue())) {
                                result = accumulator.apply(
                                        result,
                                        entry.getKey(),
                                        entry.getValue()
                                );
                            }
                        }
                    }
                    return result;
                })
                .orElse(initialValue);
    }


    /**
     *    Returns the value associated with the given {@code key} if {@code sourceMap} contains it, {@code defaultValue}
     * otherwise.
     *
     * <pre>
     *    getOrElse(                                       Result:
     *       [(1, "Hi"), (2, "Hello")]                      "Hi"
     *       1,
     *       "World"
     *    )
     *    getOrElse(                                       Result:
     *       [(1, "Hi"), (2, "Hello")]                      "World"
     *       5,
     *       "World"
     *    )
     * </pre>
     *
     * @param sourceMap
     *    {@link Map} to search {@code key}
     * @param key
     *    Key to search in {@code sourceMap}
     * @param defaultValue
     *    Default value to return in case no binding for {@code key} is found in {@code sourceMap}
     *
     * @return value related with given {@code key} if {@code sourceMap} contains it,
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
     *    Returns the value associated with the given {@code key} if {@code sourceMap} contains it, {@link Supplier#get()}
     * of {@code defaultValue} otherwise.
     *
     * <pre>
     *    getOrElse(                                       Result:
     *       [(1, "Hi"), (2, "Hello")]                      "Hi"
     *       1,
     *       () -> "World"
     *    )
     *    getOrElse(                                       Result:
     *       [(1, "Hi"), (2, "Hello")]                      "World"
     *       5,
     *       () -> "World"
     *    )
     * </pre>
     *
     * @param sourceMap
     *    {@link Map} to search {@code key}
     * @param key
     *    Key to search in {@code sourceMap}
     * @param defaultValue
     *    {@link Supplier} that yields a default value in case no binding for {@code key} is found in {@code sourceMap}
     *
     * @return value related with given {@code key} if {@code sourceMap} contains it,
     *         {@link Supplier#get()} of {@code defaultValue} otherwise.
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
     *    groupBy(                                                   Result:
     *       [(1, "Hi"), (2, "Hello"), (5, "World")]                  [(0,  [(2, "Hello")])
     *       (k, v) -> k % 2                                           (1,  [(1, "Hi"), (5, "World")])]
     *    )
     * </pre>
     *
     * @param sourceMap
     *    {@link Map} to filter
     * @param discriminator
     *    {@link BiFunction} used to split the elements of {@code sourceMap}
     *
     * @return new {@link Map} from applying the given {@link BiFunction} to each element of {@code sourceMap} to generate
     *         the keys of the returned one
     *
     * @throws IllegalArgumentException if {@code discriminator} or {@code valueMapper} is {@code null} with a not empty
     *                                  {@code sourceMap}
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
     *    groupBy(                                                   Result:
     *       [(1, "Hi"), (2, "Hello"), (5, "World")]                  [(0,  [(2, "Hello")])
     *       (k, v) -> k % 2,                                          (1,  [(1, "Hi"), (5, "World")])]
     *       HashMap::new,
     *       HashMap::new
     *    )
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
     * @return new {@link Map} from applying the given {@link BiFunction} to each element of {@code sourceMap} to generate
     *        the keys of the returned one
     *
     * @throws IllegalArgumentException if {@code discriminator} or {@code valueMapper} is {@code null} with a not empty
     *                                  {@code sourceMap}
     */
    public static <T, E, R> Map<R, Map<T, E>> groupBy(final Map<? extends T, ? extends E> sourceMap,
                                                      final BiFunction<? super T, ? super E, ? extends R> discriminator,
                                                      final Supplier<Map<R, Map<T, E>>> mapResultFactory,
                                                      final Supplier<Map<T, E>> mapValuesFactory) {
        if (CollectionUtils.isEmpty(sourceMap)) {
            return new HashMap<>();
        }
        Assert.notNull(discriminator, "discriminator must be not null");
        return groupByMultiKey(
                sourceMap,
                (T t, E e) ->
                    List.of(
                            discriminator.apply(t, e)
                    ),
                mapResultFactory,
                mapValuesFactory
        );
    }


    /**
     * Partitions {@code sourceMap} into a {@link Map} of maps according to given {@code discriminator} {@link BiFunction}.
     *
     * @apiNote
     *    This method is similar to {@link MapUtil#groupBy(Map, BiFunction)} but {@code discriminatorKey} returns a
     * {@link Collection} of related key values.
     *
     * <pre>
     *    groupByMultiKey(                                           Result:
     *       [(1, "Hi"), (2, "Hello"), (11, "World")],                [("evenKey",  [(2, "Hello")])
     *       (k, v) -> {                                               ("oddKey",   [(1, "Hi"), (11, "World")])
     *          List<String> keys = new ArrayList<>();                 ("smaller10Key",  [(1, "Hi"), (2, "Hello")])
     *          if (0 == k % 2) {                                      ("greaterEqual10Key",  [(11, "World")])]
     *             keys.add("evenKey");
     *          } else {
     *             keys.add("oddKey");
     *          }
     *          if (10 > k) {
     *             keys.add("smaller10Key");
     *          } else {
     *             keys.add("greaterEqual10Key");
     *          }
     *          return keys;
     *       }
     *    )
     * </pre>
     *
     * @param sourceMap
     *    {@link Map} to filter
     * @param discriminator
     *    {@link BiFunction} used to split the elements of {@code sourceMap}
     *
     * @return new {@link Map} from applying the given {@link BiFunction} to each element of {@code sourceMap} to generate
     *         the keys of the returned one
     *
     * @throws IllegalArgumentException if {@code discriminator} or {@code valueMapper} is {@code null} with a not empty
     *                                  {@code sourceMap}
     */
    public static <T, E, R> Map<R, Map<T, E>> groupByMultiKey(final Map<? extends T, ? extends E> sourceMap,
                                                              final BiFunction<? super T, ? super E, Collection<? extends R>> discriminator) {
        return groupByMultiKey(
                sourceMap,
                discriminator,
                HashMap::new,
                HashMap::new
        );
    }


    /**
     * Partitions {@code sourceMap} into a {@link Map} of maps according to given {@code discriminator} {@link BiFunction}.
     *
     * @apiNote
     *    This method is similar to {@link MapUtil#groupBy(Map, BiFunction, Supplier, Supplier)} but {@code discriminator}
     * returns a {@link Collection} of related key values.
     *
     * <pre>
     *    groupByMultiKey(                                           Result:
     *       [(1, "Hi"), (2, "Hello"), (11, "World")],                [("evenKey",  [(2, "Hello")])
     *       (k, v) -> {                                               ("oddKey",   [(1, "Hi"), (11, "World")])
     *          List<String> keys = new ArrayList<>();                 ("smaller10Key",  [(1, "Hi"), (2, "Hello")])
     *          if (0 == k % 2) {                                      ("greaterEqual10Key",  [(11, "World")])]
     *             keys.add("evenKey");
     *          } else {
     *             keys.add("oddKey");
     *          }
     *          if (10 > k) {
     *             keys.add("smaller10Key");
     *          } else {
     *             keys.add("greaterEqual10Key");
     *          }
     *          return keys;
     *       },
     *       HashMap::new,
     *       HashMap::new
     *    )
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
     * @return new {@link Map} from applying the given {@link BiFunction} to each element of {@code sourceMap} to generate
     *         the keys of the returned one
     *
     * @throws IllegalArgumentException if {@code discriminator} or {@code valueMapper} is {@code null} with a not empty
     *                                  {@code sourceMap}
     */
    public static <T, E, R> Map<R, Map<T, E>> groupByMultiKey(final Map<? extends T, ? extends E> sourceMap,
                                                              final BiFunction<? super T, ? super E, Collection<? extends R>> discriminator,
                                                              final Supplier<Map<R, Map<T, E>>> mapResultFactory,
                                                              final Supplier<Map<T, E>> mapValuesFactory) {
        if (CollectionUtils.isEmpty(sourceMap)) {
            return new HashMap<>();
        }
        Assert.notNull(discriminator, "discriminator must be not null");

        final Supplier<Map<R, Map<T, E>>> finalMapResultFactory = getFinalMapFactory(mapResultFactory);
        final Supplier<Map<T, E>> finalMapValuesFactory = getFinalMapFactory(mapValuesFactory);

        Map<R, Map<T, E>> result = finalMapResultFactory.get();
        sourceMap.forEach(
                (k, v) -> {
                    Collection<? extends R> discriminatorResult = ObjectUtil.getOrElse(
                            discriminator.apply(k, v),
                            new ArrayList<>()
                    );
                    discriminatorResult
                            .forEach(r -> {
                                result.putIfAbsent(
                                        r,
                                        finalMapValuesFactory.get()
                                );
                                result.get(r)
                                        .put(k, v);
                            });
                }
        );
        return result;
    }


    /**
     *    Partitions given {@code sourceMap} into a {@link Map} of {@link List} according to {@code discriminatorKey}.
     * Each element in a group is transformed into a value of type V using {@code valueMapper} {@link BiFunction}.
     *
     * <pre>
     *    groupMap(                                                            Result:
     *       [(1, "Hi"), (2, "Hello"), (5, "World"), (6, "!")],                 [(0,  [1])
     *       (k, v) -> k % 3,                                                    (1,  [2])
     *       (k, v) -> v.length()                                                (2,  [5, 5])]
     *    )
     * </pre>
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to transform.
     * @param discriminatorKey
     *    The discriminator {@link BiFunction} to get the key values of returned {@link Map}
     * @param valueMapper
     *    {@link BiFunction} to transform elements of {@code sourceMap}
     *
     * @return new {@link Map} from applying the given {@code discriminatorKey} and {@code valueMapper} to each element
     *         of {@code sourceMap}
     *
     * @throws IllegalArgumentException if {@code discriminatorKey} or {@code valueMapper} is {@code null}
     *                                  with a not empty {@code sourceMap}
     */
    @SuppressWarnings("unchecked")
    public static <K1, K2, V1, V2> Map<K2, List<V2>> groupMap(final Map<? extends K1, ? extends V1> sourceMap,
                                                              final BiFunction<? super K1, ? super V1, ? extends K2> discriminatorKey,
                                                              final BiFunction<? super K1, ? super V1, ? extends V2> valueMapper) {
        return (Map) groupMap(
                sourceMap,
                biAlwaysTrue(),
                discriminatorKey,
                valueMapper,
                ArrayList::new
        );
    }


    /**
     *    Partitions given {@code sourceMap} into a {@link Map} of {@link List} according to {@code discriminatorKey},
     * only if the current element matches {@code filterPredicate}. Each element in a group is transformed into a value
     * of type V using {@code valueMapper} {@link BiFunction}.
     *
     * @apiNote
     *    If {@code filterPredicate} is {@code null} then all elements will be used.
     *
     * <pre>
     *    groupMap(                                                                 Result:
     *       [(1, "Hi"), (2, "Hello"), (5, "World"), (6, "!"), (11, "Not")],         [(0,  [1])
     *       (k, v) -> 10 > k,                                                        (1,  [2])
     *       (k, v) -> k % 3,                                                         (2,  [5, 5])]
     *       (k, v) -> v.length()
     *    )
     * </pre>
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to transform
     * @param filterPredicate
     *    {@link BiPredicate} used to filter elements of {@code sourceMap}
     * @param discriminatorKey
     *    The discriminator {@link BiFunction} to get the key values of returned {@link Map}
     * @param valueMapper
     *    {@link BiFunction} to transform elements of {@code sourceMap}
     *
     * @return new {@link Map} from applying the given {@code discriminatorKey} and {@code valueMapper} to each element
     *         of {@code sourceMap} that verifies {@code filterPredicate}
     *
     * @throws IllegalArgumentException if {@code discriminatorKey} or {@code valueMapper} is {@code null}
     *                                  with a not empty {@code sourceMap}
     */
    @SuppressWarnings("unchecked")
    public static <K1, K2, V1, V2> Map<K2, List<V2>> groupMap(final Map<? extends K1, ? extends V1> sourceMap,
                                                              final BiPredicate<? super K1, ? super V1> filterPredicate,
                                                              final BiFunction<? super K1, ? super V1, ? extends K2> discriminatorKey,
                                                              final BiFunction<? super K1, ? super V1, ? extends V2> valueMapper) {
        return (Map) groupMap(
                sourceMap,
                filterPredicate,
                discriminatorKey,
                valueMapper,
                ArrayList::new
        );
    }


    /**
     *    Partitions given {@code sourceMap} into a {@link Map} of {@link List} according to {@code discriminatorKey},
     * only if the current element matches {@code filterPredicate}. Each element in a group is transformed into a value
     * of type V using {@code valueMapper} {@link BiFunction}.
     *
     * @apiNote
     *    If {@code filterPredicate} is {@code null} then all elements will be used. If {@code collectionFactory} is
     * {@code null} then {@link ArrayList} will be used.
     *
     * <pre>
     *    groupMap(                                                                 Result:
     *       [(1, "Hi"), (2, "Hello"), (5, "World"), (6, "!"), (11, "Not")],         [(0,  [1])
     *       (k, v) -> 10 > k,                                                        (1,  [2])
     *       (k, v) -> k % 3,                                                         (2,  [5, 5])]
     *       (k, v) -> v.length(),
     *       ArrayList::new
     *    )
     * </pre>
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to transform
     * @param filterPredicate
     *    {@link BiPredicate} used to filter elements of {@code sourceMap}
     * @param discriminatorKey
     *    The discriminator {@link BiFunction} to get the key values of returned {@link Map}
     * @param valueMapper
     *    {@link BiFunction} to transform elements of {@code sourceMap}
     * @param collectionFactory
     *    {@link Supplier} of the {@link Collection} used to store the returned elements.
     *    If {@code null} then {@link ArrayList}
     *
     * @return new {@link Map} from applying the given {@code discriminatorKey} and {@code valueMapper} to each element
     *         of {@code sourceMap} that verifies {@code filterPredicate}
     *
     * @throws IllegalArgumentException if {@code discriminatorKey} or {@code valueMapper} is {@code null}
     *                                  with a not empty {@code sourceMap}
     */
    public static <K1, K2, V1, V2> Map<K2, Collection<V2>> groupMap(final Map<? extends K1, ? extends V1> sourceMap,
                                                                    final BiPredicate<? super K1, ? super V1> filterPredicate,
                                                                    final BiFunction<? super K1, ? super V1, ? extends K2> discriminatorKey,
                                                                    final BiFunction<? super K1, ? super V1, ? extends V2> valueMapper,
                                                                    final Supplier<Collection<V2>> collectionFactory) {
        if (CollectionUtils.isEmpty(sourceMap)) {
            return new HashMap<>();
        }
        Assert.notNull(discriminatorKey, "discriminatorKey must be not null");
        Assert.notNull(valueMapper, "valueMapper must be not null");
        return groupMap(
                sourceMap,
                PartialFunction.of(
                        getOrAlwaysTrue(filterPredicate),
                        discriminatorKey,
                        valueMapper
                ),
                collectionFactory
        );
    }


    /**
     *    Partitions given {@code sourceMap} into a {@link Map} of {@link List} according to {@code partialFunction}.
     * Each element in the {@link Map} is transformed into a {@link Map.Entry} using {@code partialFunction}.
     *
     * <pre>
     *    groupMap(                                                            Result:
     *       [(1, "Hi"), (2, "Hello"), (5, "World"), (7, "!")],                 [(1,  [3, 2])
     *       PartialFunction.of(                                                 (2,  [6])]
     *          e -> null != e && 1 == e.getValue() % 2,
     *          e -> null == e
     *             ? null
     *             : new AbstractMap.SimpleEntry<>(
     *                 e.getKey() % 3,
     *                 null == e.getValue()
     *                    ? 0
     *                    : e.getValue().length() + 1
     *               )
     *       )
     *    )
     * </pre>
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to transform.
     * @param partialFunction
     *    {@link PartialFunction} to filter and transform elements of {@code sourceMap}
     *
     * @return new {@link Map} from applying the given {@link PartialFunction} to each element of {@code sourceMap}
     *         on which it is defined and collecting the results
     *
     * @throws IllegalArgumentException if {@code partialFunction} is {@code null} with a not empty {@code sourceMap}
     */
    @SuppressWarnings("unchecked")
    public static <K1, K2, V1, V2> Map<K2, List<V2>> groupMap(final Map<? extends K1, ? extends V1> sourceMap,
                                                              final PartialFunction<? super Map.Entry<K1, V1>, ? extends Map.Entry<K2, V2>> partialFunction) {
        return (Map) groupMap(
                sourceMap,
                partialFunction,
                ArrayList::new
        );
    }


    /**
     *    Partitions given {@code sourceMap} into a {@link Map} of {@link List} according to {@code partialFunction}.
     * Each element in the {@link Map} is transformed into a {@link Map.Entry} using {@code partialFunction}.
     *
     * @apiNote
     *    If {@code collectionFactory} is {@code null} then {@link ArrayList} will be used.
     *
     * <pre>
     *    groupMap(                                                            Result:
     *       [(1, "Hi"), (2, "Hello"), (5, "World"), (7, "!")],                 [(1,  [3, 2])
     *       PartialFunction.of(                                                 (2,  [6])]
     *          e -> null != e && 1 == e.getValue() % 2,
     *          e -> null == e
     *             ? null
     *             : new AbstractMap.SimpleEntry<>(
     *                 e.getKey() % 3,
     *                 null == e.getValue()
     *                    ? 0
     *                    : e.getValue().length() + 1
     *               )
     *       ),
     *       ArrayList::new
     *    )
     * </pre>
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to transform.
     * @param partialFunction
     *    {@link PartialFunction} to filter and transform elements of {@code sourceMap}
     * @param collectionFactory
     *    {@link Supplier} of the {@link Collection} used to store the returned elements.
     *    If {@code null} then {@link ArrayList}
     *
     * @return new {@link Map} from applying the given {@link PartialFunction} to each element of {@code sourceMap}
     *         on which it is defined and collecting the results
     *
     * @throws IllegalArgumentException if {@code partialFunction} is {@code null} with a not empty {@code sourceMap}
     */
    @SuppressWarnings("unchecked")
    public static <K1, K2, V1, V2> Map<K2, Collection<V2>> groupMap(final Map<? extends K1, ? extends V1> sourceMap,
                                                                    final PartialFunction<? super Map.Entry<K1, V1>, ? extends Map.Entry<K2, V2>> partialFunction,
                                                                    final Supplier<Collection<V2>> collectionFactory) {
        if (CollectionUtils.isEmpty(sourceMap)) {
            return new HashMap<>();
        }
        Assert.notNull(partialFunction, "partialFunction must be not null");
        final Supplier<Collection<V2>> finalCollectionFactory = ObjectUtil.getOrElse(
                collectionFactory,
                ArrayList::new
        );
        Map<K2, Collection<V2>> result = new HashMap<>();

        // Allowed because immutable/read-only Maps are covariant.
        Map<K1, V1> narrowedSourceMap = (Map<K1, V1>) sourceMap;
        narrowedSourceMap.entrySet().stream()
                .filter(partialFunction::isDefinedAt)
                .forEach(e -> {
                    Map.Entry<K2, V2> keyValue = partialFunction.apply(e);
                    result.putIfAbsent(
                            keyValue.getKey(),
                            finalCollectionFactory.get()
                    );
                    result.get(keyValue.getKey())
                            .add(keyValue.getValue());
                });
        return result;
    }


    /**
     *    Partitions given {@code sourceMap} into a {@link Map} of {@link List} as values, according to {@code discriminatorKey}.
     * All the values that have the same discriminator are then transformed by {@code valueMapper} {@link BiFunction}
     * and then reduced into a single value with {@code reduceValues}.
     *
     * <pre>
     *    groupMapReduce(                                                 Intermediate Map:             Result:
     *       [(1, "Hi"), (2, "Hello"), (5, "World"), (6, "!")],            [(0,  [1])                    [(0, 1),
     *       (k, v) -> k % 3,                                               (1,  [2])                     (1, 2),
     *       (k, v) -> v.length(),                                          (2,  [5, 5])]                 (2, 10)]
     *       Integer::sum
     *    )
     * </pre>
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to transform and reduce
     * @param discriminatorKey
     *    The discriminator {@link BiFunction} to get the key values of returned {@link Map}
     * @param valueMapper
     *    {@link BiFunction} to transform elements of {@code sourceMap}
     * @param reduceValues
     *    {@link BinaryOperator} used to reduce the values related with same key
     *
     * @return new {@link Map} from applying the given {@code discriminatorKey} and {@code valueMapper} to each element
     *         of {@code sourceMap}, collecting the results and reduce them using provided {@code reduceValues}
     *
     * @throws IllegalArgumentException if {@code discriminatorKey}, {@code valueMapper} or {@code reduceValues}
     *                                  are {@code null} with a not empty {@code sourceMap}
     */
    public static <K1, K2, V1, V2> Map<K2, V2> groupMapReduce(final Map<? extends K1, ? extends V1> sourceMap,
                                                              final BiFunction<? super K1, ? super V1, ? extends K2> discriminatorKey,
                                                              final BiFunction<? super K1, ? super V1, V2> valueMapper,
                                                              final BinaryOperator<V2> reduceValues) {
        if (CollectionUtils.isEmpty(sourceMap)) {
            return new HashMap<>();
        }
        Assert.notNull(discriminatorKey, "discriminatorKey must be not null");
        Assert.notNull(valueMapper, "valueMapper must be not null");
        Assert.notNull(reduceValues, "reduceValues must be not null");
        return groupMapReduce(
                sourceMap,
                PartialFunction.of(
                        biAlwaysTrue(),
                        discriminatorKey,
                        valueMapper
                ),
                reduceValues
        );
    }


    /**
     *    Partitions given {@code sourceMap} into a {@link Map} of {@link List} as values, according to {@code partialFunction}.
     * If the current element verifies {@link PartialFunction#isDefinedAt(Object)}, all the values that have the same key
     * after applying {@link PartialFunction#apply(Object)} are then reduced into a single value with {@code reduceValues}.
     *
     * <pre>
     *    groupMapReduce(                                                                  Intermediate Map:             Result:
     *       [(1, "Hi"), (2, "Hola"), (4, ""), (5, "World"), (6, "!"), (11, "ABC")]         [(0,  [2])                    [(0, 2),
     *       PartialFunction.of(                                                             (1,  [3, 1])                  (1, 4),
     *          e -> null != e && 10 > e.getKey(),                                           (2,  [5, 6])]                 (2, 11)]
     *          e -> null == e
     *             ? null
     *             : new AbstractMap.SimpleEntry<>(
     *                 e.getKey() % 3,
     *                 null == e.getValue()
     *                    ? 0
     *                    : e.getValue().length() + 1
     *               )
     *       ),
     *       Integer::sum
     *    )
     * </pre>
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to filter, transform and reduce
     * @param partialFunction
     *    {@link PartialFunction} to filter and transform elements of {@code sourceMap}
     * @param reduceValues
     *    {@link BinaryOperator} used to reduce the values related with same key
     *
     * @return new {@link Map} from applying the given {@link PartialFunction} to each element of {@code sourceMap}
     *         on which it is defined, collecting the results and reduce them using provided {@code reduceValues}
     *
     * @throws IllegalArgumentException if {@code partialFunction} or {@code reduceValues} are {@code null} with a not empty
     *                                  {@code sourceMap}
     */
    public static <K1, K2, V1, V2> Map<K2, V2> groupMapReduce(final Map<? extends K1, ? extends V1> sourceMap,
                                                              final PartialFunction<? super Map.Entry<K1, V1>, ? extends Map.Entry<K2, V2>> partialFunction,
                                                              final BinaryOperator<V2> reduceValues) {
        if (CollectionUtils.isEmpty(sourceMap)) {
            return new HashMap<>();
        }
        Assert.notNull(partialFunction, "discriminatorKey must be not null");
        Assert.notNull(reduceValues, "reduceValues must be not null");
        Map<K2, V2> result = new HashMap<>();
        groupMap(
                sourceMap,
                partialFunction
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
     *    map(                                                                 Result:
     *       [(1, "AGTF"), (3, "CD")],                                          [(1, 4), (3, 2)]
     *       (k, v) -> new AbstractMap.SimpleEntry<>(k, v.length())
     *    )
     * </pre>
     *
     * @param sourceMap
     *    {@link Map} to used as source of the new one
     * @param mapFunction
     *    {@link BiFunction} used to transform given {@code sourceMap} elements
     *
     * @return new {@link Map} from applying the given {@link BiFunction} to each element of {@code sourceMap}
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
     * @apiNote
     *    If {@code mapFactory} is {@code null} then {@link HashMap} will be used.
     *
     * <pre>
     *    map(                                                                 Result:
     *       [(1, "AGTF"), (3, "CD")],                                          [(1, 4), (3, 2)]
     *       (k, v) -> new AbstractMap.SimpleEntry<>(k, v.length()),
     *       HashMap::new
     *    )
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
     * @return new {@link Map} from applying the given {@link BiFunction} to each element of {@code sourceMap}
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
     *    Builds a new {@link Map} by applying a function to all elements of {@code sourceMap}, adding the results
     * as new values of returned {@link Map}, keeping existing keys.
     *
     * <pre>
     *    mapValues(                                       Result:
     *       [(1, "A"), (3, "C")],                          [(1, 2), (3, 4)]
     *       (k, v) -> k + v.length()
     *    )
     * </pre>
     *
     * @param sourceMap
     *    {@link Map} to used as source of the new one
     * @param mapFunction
     *    {@link BiFunction} used to transform given {@code sourceMap} values
     *
     * @return new {@link Map} from applying the given {@link BiFunction} to each element of {@code sourceMap}, adding
     *         as new values of returned one
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
     *    Builds a new {@link Map} by applying a function to all elements of {@code sourceMap}, adding the results
     * as new values of returned {@link Map}, keeping existing keys.
     *
     * @apiNote
     *    If {@code mapFactory} is {@code null} then {@link HashMap} will be used.
     *
     * <pre>
     *    mapValues(                                       Result:
     *       [(1, "A"), (3, "C")],                          [(1, 2), (3, 4)]
     *       (k, v) -> k + v.length(),
     *       HashMap::new
     *    )
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
     * @return new {@link Map} from applying the given {@link BiFunction} to each element of {@code sourceMap}, adding
     *         as new values of returned one
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
     *    max(                                                       Result:
     *       [(1, "Hi"), (3, "Hello"), (5, "World")],                 Optional((5, "World"))
     *       Map.Entry.comparingByKey()
     *    )
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
     * @throws IllegalArgumentException if {@code comparator} is {@code null} and {@code sourceMap} is not
     */
    @SuppressWarnings("unchecked")
    public static <T, E> Optional<Map.Entry<T, E>> max(final Map<? extends T, ? extends E> sourceMap,
                                                       final Comparator<Map.Entry<? extends T, ? extends E>> comparator) {
        return ofNullable(sourceMap)
                .map(m -> {
                    Assert.notNull(comparator, "comparator must be not null");

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
     *    maxValue(                                                  Result:
     *       [(1, "Hi"), (3, "Hello"), (5, "World")],                 Optional("World")
     *       String::compareTo
     *    )
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
     *    min(                                                       Result:
     *       [(1, "Hi"), (3, "Hello"), (5, "World")],                 Optional((1, "Hi"))
     *       (t1, t2) -> t1._1.compareTo(t2._1)
     *    )
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
     * @throws IllegalArgumentException if {@code comparator} is {@code null} and {@code sourceMap} is not
     */
    @SuppressWarnings("unchecked")
    public static <T, E> Optional<Map.Entry<T, E>> min(final Map<? extends T, ? extends E> sourceMap,
                                                       final Comparator<Map.Entry<? extends T, ? extends E>> comparator) {
        return ofNullable(sourceMap)
                .map(m -> {
                    Assert.notNull(comparator, "comparator must be not null");
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
     *    minValue(                                                  Result:
     *       [(1, "Hi"), (3, "Hello"), (5, "World")]                  Optional("Hello")
     *       String::compareTo
     *    )
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
     *    partition(                                       Result:
     *       [(1, "Hi"), (2, "Hello")],                     [(true,  [(2, "Hello")])
     *       (k, v) -> 0 == k % 2                            (false, [(1, "Hi")])]
     *    )
     * </pre>
     *
     * @param sourceMap
     *    {@link Map} to filter
     * @param discriminator
     *    {@link BiPredicate} used to split the elements of {@code sourceMap}
     *
     * @return new {@link Map} with two keys: {@code true}, {@code false} based on elements in {@code sourceMap} that
     *         satisfy given {@code discriminator}
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
     * @apiNote
     *    If {@code mapFactory} is {@code null} then {@link HashMap} will be used.
     *
     * <pre>
     *    partition(                                       Result:
     *       [(1, "Hi"), (2, "Hello")],                     [(true,  [(2, "Hello")])
     *       (k, v) -> 0 == k % 2,                           (false, [(1, "Hi")])]
     *       HashMap::new
     *    )
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
     * @return new {@link Map} with two keys: {@code true}, {@code false} based on elements in {@code sourceMap} that
     *         satisfy given {@code discriminator}
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
     *   Performs a reduction on the elements of {@code sourceMap}, using an associative accumulation {@link BinaryOperator},
     * and returns a value describing the reduced elements, if any.
     *
     * @apiNote
     *    This method is similar to {@link MapUtil#foldLeft(Map, Object, TriFunction)} but {@code accumulator} works with
     * the same type that {@code sourceMap} and only uses contained elements of provided {@link Map}.
     *
     * <pre>
     *    reduce(                                                    Result:
     *       [(1, "Hi"), (2, "Hello")],                               (3, "Hi Hello")
     *       (oldEntry, newEntry) ->
     *          new AbstractMap.SimpleEntry<>(
     *            oldEntry.getKey() + newEntry.getKey(),
     *            oldEntry.getValue() + newEntry.getValue()
     *          )
     *    )
     * </pre>
     *
     * @param sourceMap
     *    {@link Map} with elements to combine
     * @param accumulator
     *    A {@link BinaryOperator} which combines elements
     *
     * @return {@link Optional} with result of inserting {@code accumulator} between consecutive elements {@code sourceMap}, going left to right,
     *         {@link Optional#empty()} if {@code sourceMap} has no elements or the final result is {@code null}.
     *
     * @throws IllegalArgumentException if {@code accumulator} is {@code null} and {@code sourceMap} is not empty
     */
    public static <T, E> Optional<Map.Entry<T, E>> reduce(final Map<T, E> sourceMap,
                                                          final BinaryOperator<Map.Entry<T, E>> accumulator) {
        if (CollectionUtils.isEmpty(sourceMap)) {
            return empty();
        }
        Assert.notNull(accumulator, "accumulator must be not null");
        return sourceMap
                .entrySet()
                .stream()
                .reduce(accumulator);
    }


    /**
     *    Using the provided {@code sourceMap}, return all elements beginning at index {@code from} and afterward,
     * up to index {@code until} (excluding this one).
     *
     * <pre>
     *    slice(                                           Result:
     *       [(1, "Hi"), (2, "Hello")],                     [(2, "Hello")]
     *       1,
     *       3
     *    )
     *    slice(                                           Result:
     *       [(1, "Hi"), (2, "Hello")],                     [(1, "Hi")]
     *       0,
     *       1
     *    )
     *    slice(                                           Result:
     *       [(1, "Hi"), (2, "Hello")],                     [(1, "Hi"), (2, "Hello")]
     *       -1,
     *       2
     *    )
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
     * Loops through the provided {@link Map} one position every time, returning sublists with {@code size}.
     *
     * <pre>
     *    sliding(                                         Result:
     *       [(1, "A"), (3, "C")],                          [[(1, "A"), (3, "C")]]
     *       5
     *    )
     *    sliding(                                         Result:
     *       [(1, "A"), (3, "C"), (8, "Z")],                [[(1, "A"), (3, "C")], [(3, "C"), (8, "Z")]]
     *       2
     *    )
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
                        Collectors.toList()
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
     *    sort(                                            Result:
     *       Map.Entry.comparingByKey(),                    [(1, "Yes"), (2, "No"), (3, "Hello")]
     *       [(1, "Hi"), (3, "Hello")],
     *       [(1, "Yes"), (2, "No")]
     *    )
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
     *    sort(                                            Result:
     *       Map.Entry.comparingByKey(),                    [(1, "Hi"), (2, "No"), (3, "Hello")]
     *       (oldV, newV) -> oldV,
     *       [(1, "Hi"), (3, "Hello")],
     *       [(1, "Yes"), (2, "No")]
     *    )
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
     * Splits the given {@link Map} in sublists with a size equal to the given {@code size}.
     *
     * <pre>
     *    split(                                           Result:
     *       [(1, "A"), (3, "C"), (8, "Z")],                [[(1, "A"), (3, "C")], [(8, "Z")]]
     *       2
     *    )
     *    split(                                           Result:
     *       [(1, "A"), (3, "C")],                          [[(1, "A"), (3, "C")]]
     *       3
     *    )
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
                .collect(
                        Collectors.toList()
                );
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
     *    Returns a {@link Map} with the longest prefix of elements included in {@code sourceMap} that satisfy the
     * {@link BiPredicate} {@code filterPredicate}.
     *
     * @apiNote
     *    If {@code filterPredicate} is {@code null} then all elements of {@code sourceCollection} will be returned.
     *
     * <pre>
     *    takeWhile(                                                 Result:
     *       [(2, "Hi"), (4, "Hello"), (5, "World")],                 [(2, "Hi"), (4, "Hello")]
     *       (k, v) -> 0 == k % 2
     *    )
     * </pre>
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to filter
     * @param filterPredicate
     *    {@link BiPredicate} to filter elements from {@code sourceMap}
     *
     * @return the longest prefix of provided {@code sourceMap} whose first element does not satisfy {@code filterPredicate}
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
     *    Returns a {@link Map} with the longest prefix of elements included in {@code sourceMap} that satisfy the
     * {@link BiPredicate} {@code filterPredicate}.
     *
     * @apiNote
     *    If {@code filterPredicate} is {@code null} then all elements of {@code sourceCollection} will be returned. If
     * {@code mapFactory} is {@code null} then {@link HashMap} will be used.
     *
     * <pre>
     *    takeWhile(                                                 Result:
     *       [(2, "Hi"), (4, "Hello"), (5, "World")],                 [(2, "Hi"), (4, "Hello")]
     *       (k, v) -> 0 == k % 2,
     *       HashMap::new
     *    )
     * </pre>
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to filter
     * @param filterPredicate
     *    {@link BiPredicate} to filter elements from {@code sourceMap}
     * @param mapFactory
     *    {@link Supplier} of the {@link Map} used to store the returned elements
     *    If {@code null} then {@link HashMap}
     *
     * @return the longest prefix of provided {@code sourceMap} whose first element does not satisfy {@code filterPredicate}
     */
    public static <T, E> Map<T, E> takeWhile(final Map<? extends T, ? extends E> sourceMap,
                                             final BiPredicate<? super T, ? super E> filterPredicate,
                                             final Supplier<Map<T, E>> mapFactory) {
        if (CollectionUtils.isEmpty(sourceMap) || isNull(filterPredicate)) {
            return copy(
                    sourceMap,
                    mapFactory
            );
        }
        final Supplier<Map<T, E>> finalMapFactory = getFinalMapFactory(mapFactory);
        return sourceMap.entrySet()
                .stream()
                .takeWhile(entry ->
                        filterPredicate.test(
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
     * Converts the given {@link Map} in to a {@link List} using provided {@code keyValueMapper}.
     *
     * <pre>
     *    toCollection(                                    Result:
     *       [("a", 1), ("b", 2), ("d", 4)],                [2, 3, 5]
     *       (s, i) -> s.length() + i
     *    )
     * </pre>
     *
     * @param sourceMap
     *    {@link Map} with the elements to transform and include in the returned {@link Collection}
     * @param keyValueMapper
     *    {@link BiFunction} to transform elements of {@code sourceMap} into elements of the returned {@link Collection}
     *
     * @return {@link List} applying {@code keyValueMapper} to each element of {@code sourceMap} that verifies
     *         {@code filterPredicate}
     *
     * @throws IllegalArgumentException if {@code sourceMap} is not empty and {@code keyValueMapper} is {@code null}
     */
    @SuppressWarnings("unchecked")
    public static <K, V, R> List<R> toCollection(final Map<? extends K, ? extends V> sourceMap,
                                                 final BiFunction<? super K, ? super V, ? extends R> keyValueMapper) {
        return (List<R>) toCollection(
                sourceMap,
                keyValueMapper,
                PredicateUtil.biAlwaysTrue(),
                ArrayList::new
        );
    }


    /**
     * Converts the given {@link Map} in to a {@link Collection} using provided {@code keyValueMapper}
     *
     * @apiNote
     *    If {@code collectionFactory} is {@code null} then {@link ArrayList} will be used.
     *
     * <pre>
     *    toCollection(                                    Result:
     *       [("a", 1), ("b", 2), ("d", 4)],                [2, 3, 5]
     *       (s, i) -> s.length() + i,
     *       ArrayList::new
     *    )
     * </pre>
     *
     * @param sourceMap
     *    {@link Map} with the elements to transform and include in the returned {@link Collection}
     * @param keyValueMapper
     *    {@link BiFunction} to transform elements of {@code sourceMap} into elements of the returned {@link Collection}
     * @param collectionFactory
     *   {@link Supplier} of the {@link Collection} used to store the returned elements.
     *
     * @return {@link Collection} applying {@code keyValueMapper} to each element of {@code sourceMap} that verifies
     *         {@code filterPredicate}
     *
     * @throws IllegalArgumentException if {@code sourceMap} is not empty and {@code keyValueMapper} is {@code null}
     */
    public static <K, V, R> Collection<R> toCollection(final Map<? extends K, ? extends V> sourceMap,
                                                       final BiFunction<? super K, ? super V, ? extends R> keyValueMapper,
                                                       final Supplier<Collection<R>> collectionFactory) {
        return toCollection(
                sourceMap,
                keyValueMapper,
                PredicateUtil.biAlwaysTrue(),
                collectionFactory
        );
    }


    /**
     *    Converts the given {@link Map} in to a {@link Collection} using provided {@code keyValueMapper}, only with the
     * elements that satisfy the {@link BiPredicate} {@code filterPredicate}.
     *
     * @apiNote
     *    If {@code collectionFactory} is {@code null} then {@link ArrayList} will be used.
     *
     * <pre>
     *    toCollection(                                    Result:
     *       [("a", 1), ("b", 2), ("d", 4)],                [3, 5]
     *       (s, i) -> s.length() + i,
     *       (s, i) -> 0 == i % 2,
     *       ArrayList::new
     *    )
     * </pre>
     *
     * @param sourceMap
     *    {@link Map} with the elements to transform and include in the returned {@link Collection}
     * @param keyValueMapper
     *    {@link BiFunction} to transform elements of {@code sourceMap} into elements of the returned {@link Collection}
     * @param filterPredicate
     *    {@link BiPredicate} used to filter values from {@code sourceMap} that will be added in the returned {@link Collection}
     * @param collectionFactory
     *   {@link Supplier} of the {@link Collection} used to store the returned elements.
     *
     * @return {@link Collection} applying {@code keyValueMapper} to each element of {@code sourceMap} that verifies
     *         {@code filterPredicate}
     *
     * @throws IllegalArgumentException if {@code sourceMap} is not empty and {@code keyValueMapper} is {@code null}
     */
    public static <K, V, R> Collection<R> toCollection(final Map<? extends K, ? extends V> sourceMap,
                                                       final BiFunction<? super K, ? super V, ? extends R> keyValueMapper,
                                                       final BiPredicate<? super K, ? super V> filterPredicate,
                                                       final Supplier<Collection<R>> collectionFactory) {
        final Supplier<Collection<R>> finalCollectionFactory = ObjectUtil.getOrElse(
                collectionFactory,
                ArrayList::new
        );
        if (CollectionUtils.isEmpty(sourceMap)) {
            return finalCollectionFactory.get();
        }
        return toCollection(
                sourceMap,
                PartialFunction.of(
                        fromBiPredicateToMapEntryPredicate(
                                filterPredicate
                        ),
                        fromBiFunctionToMapEntryFunction(
                                keyValueMapper
                        )
                ),
                collectionFactory
        );
    }


    /**
     * Converts the given {@link Map} in to a {@link Collection} using provided {@code partialFunction}.
     *
     * @apiNote
     *    If {@code collectionFactory} is {@code null} then {@link ArrayList} will be used.
     *
     * <pre>
     *    toCollection(                                              Result:
     *       [("a", 1), ("b", 2), ("d", 4)],                          [3, 5]
     *       PartialFunction.of(
     *          e -> null != e && 0 == e.getKey() % 2,
     *          e -> null == e
     *             ? 0
     *             : e.getKey().length() + e.getValue()
     *       ),
     *       ArrayList::new
     *    )
     * </pre>
     *
     * @param sourceMap
     *    {@link Map} with the elements to transform and include in the returned {@link Collection}
     * @param partialFunction
     *    {@link PartialFunction} to filter and transform elements of {@code sourceMap}
     * @param collectionFactory
     *   {@link Supplier} of the {@link Collection} used to store the returned elements.
     *
     * @return {@link Collection} applying {@code partialFunction} to each element of {@code sourceMap}
     *
     * @throws IllegalArgumentException if {@code sourceMap} is not empty and {@code partialFunction} is {@code null}
     */
    @SuppressWarnings("unchecked")
    public static <K, V, R> Collection<R> toCollection(final Map<? extends K, ? extends V> sourceMap,
                                                       final PartialFunction<? super Map.Entry<K, V>, ? extends R> partialFunction,
                                                       final Supplier<Collection<R>> collectionFactory) {
        final Supplier<Collection<R>> finalCollectionFactory = ObjectUtil.getOrElse(
                collectionFactory,
                ArrayList::new
        );
        if (CollectionUtils.isEmpty(sourceMap)) {
            return finalCollectionFactory.get();
        }
        Assert.notNull(partialFunction, "partialFunction must be not null");

        // Allowed because immutable/read-only Maps are covariant.
        Map<K, V> narrowedSourceMap = (Map<K, V>) sourceMap;
        return narrowedSourceMap.entrySet()
                .stream()
                .filter(partialFunction::isDefinedAt)
                .map(partialFunction)
                .collect(
                        Collectors.toCollection(finalCollectionFactory)
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
