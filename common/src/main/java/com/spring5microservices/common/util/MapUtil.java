package com.spring5microservices.common.util;

import com.spring5microservices.common.collection.tuple.Tuple;
import com.spring5microservices.common.collection.tuple.Tuple2;
import com.spring5microservices.common.interfaces.functional.TriFunction;
import lombok.experimental.UtilityClass;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;

@UtilityClass
public class MapUtil {

    /**
     *    In the given {@code sourceMap}, applies {@code defaultFunction} if the current element verifies
     * {@code filterPredicate}, otherwise applies {@code orElseFunction}.
     *
     * Example:
     *
     *   Parameters:              Result:
     *    [("A", 1), ("B", 2)]     [("A", 2), ("B", 4)]
     *    i -> i % 2 == 1
     *    i -> i + 1
     *    i -> i * 2
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to filter and transform.
     * @param filterPredicate
     *    {@link BiPredicate} to filter elements from {@code sourceMap}.
     * @param defaultFunction
     *    {@link BiFunction} to transform elements of {@code sourceMap} that verify {@code filterPredicate}.
     * @param orElseFunction
     *    {@link BiFunction} to transform elements of {@code sourceMap} do not verify {@code filterPredicate}.
     *
     * @return {@link Map}
     *
     * @throws IllegalArgumentException if {@code filterPredicate}, {@code defaultFunction} or {@code orElseFunction}
     *                                  is {@code null}
     */
    public static <T, E, R> Map<T, R> applyOrElse(final Map<? extends T, ? extends E> sourceMap,
                                                  final BiPredicate<? super T, ? super E> filterPredicate,
                                                  final BiFunction<? super T, ? super E, ? extends R> defaultFunction,
                                                  final BiFunction<? super T, ? super E, ? extends R> orElseFunction) {
        return applyOrElse(sourceMap, filterPredicate, defaultFunction, orElseFunction, HashMap::new);
    }


    /**
     *    In the given {@code sourceMap}, applies {@code defaultFunction} if the current element verifies
     * {@code filterPredicate}, otherwise applies {@code orElseFunction}.
     *
     * Example:
     *
     *   Parameters:              Result:
     *    [("A", 1), ("B", 2)]     [("A", 2), ("B", 4)]
     *    i -> i % 2 == 1
     *    i -> i + 1
     *    i -> i * 2
     *    HashMap::new
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to filter and transform.
     * @param filterPredicate
     *    {@link BiPredicate} to filter elements from {@code sourceMap}.
     * @param defaultFunction
     *    {@link BiFunction} to transform elements of {@code sourceMap} that verify {@code filterPredicate}.
     * @param orElseFunction
     *    {@link BiFunction} to transform elements of {@code sourceMap} do not verify {@code filterPredicate}.
     * @param mapFactory
     *    {@link Supplier} of the {@link Map} used to store the returned elements.
     *
     * @return {@link Map}
     *
     * @throws IllegalArgumentException if {@code filterPredicate}, {@code defaultFunction} or {@code orElseFunction}
     *                                  is {@code null}
     */
    public static <T, E, R> Map<T, R> applyOrElse(final Map<? extends T, ? extends E> sourceMap,
                                                  final BiPredicate<? super T, ? super E> filterPredicate,
                                                  final BiFunction<? super T, ? super E, ? extends R> defaultFunction,
                                                  final BiFunction<? super T, ? super E, ? extends R> orElseFunction,
                                                  final Supplier<Map<T, R>> mapFactory) {
        Assert.notNull(filterPredicate, "filterPredicate must be not null");
        Assert.notNull(defaultFunction, "defaultFunction must be not null");
        Assert.notNull(orElseFunction, "orElseFunction must be not null");
        Supplier<Map<T, R>> finalMapFactory =
                isNull(mapFactory)
                        ? HashMap::new
                        : mapFactory;

        if (CollectionUtils.isEmpty(sourceMap)) {
            return finalMapFactory.get();
        }
        return sourceMap.entrySet()
                .stream()
                .map(entry ->
                        filterPredicate.test(entry.getKey(), entry.getValue())
                                ? Map.entry(
                                        entry.getKey(),
                                        defaultFunction.apply(entry.getKey(), entry.getValue())
                                )
                                : Map.entry(
                                        entry.getKey(),
                                        orElseFunction.apply(entry.getKey(), entry.getValue())
                                )
                )
                .collect(
                        toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                overwriteWithNew(),
                                finalMapFactory
                        )
                );
    }


    /**
     * Returns a {@link Map} after:
     *
     *  - Filter its elements using {@code filterPredicate}
     *  - Transform its filtered elements using {@code mapFunction}
     *
     * Example:
     *
     *   Parameters:                   Result:
     *    [(1, "Hi"), (2, "Hello")]     [("A", 2), ("B", 4)]
     *    (k, v) -> k % 2 == 0
     *    (k, v) -> k + v.length()
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to filter and transform.
     * @param filterPredicate
     *    {@link BiPredicate} to filter elements from {@code sourceMap}.
     * @param mapFunction
     *    {@link BiFunction} to transform filtered elements from the source {@code sourceMap}.
     *
     * @return {@link Map}
     *
     * @throws IllegalArgumentException if {@code filterPredicate} or {@code mapFunction} is {@code null}
     */
    public static <T, E, R> Map<T, R> collect(final Map<? extends T, ? extends E> sourceMap,
                                              final BiPredicate<? super T, ? super E> filterPredicate,
                                              final BiFunction<? super T, ? super E, ? extends R> mapFunction) {
        return collect(sourceMap, filterPredicate, mapFunction, HashMap::new);
    }


    /**
     * Returns a {@link Map} after:
     *
     *  - Filter its elements using {@code filterPredicate}
     *  - Transform its filtered elements using {@code mapFunction}
     *
     * Example:
     *
     *   Parameters:                   Result:
     *    [(1, "Hi"), (2, "Hello")]     [("A", 2), ("B", 4)]
     *    (k, v) -> k % 2 == 0
     *    (k, v) -> k + v.length()
     *    HashMap::new
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to filter and transform.
     * @param filterPredicate
     *    {@link BiPredicate} to filter elements from {@code sourceMap}.
     * @param mapFunction
     *    {@link BiFunction} to transform filtered elements from the source {@code sourceMap}.
     * @param mapFactory
     *    {@link Supplier} of the {@link Map} used to store the returned elements.
     *
     * @return {@link Map}
     *
     * @throws IllegalArgumentException if {@code filterPredicate} or {@code mapFunction} is {@code null}
     */
    public static <T, E, R> Map<T, R> collect(final Map<? extends T, ? extends E> sourceMap,
                                              final BiPredicate<? super T, ? super E> filterPredicate,
                                              final BiFunction<? super T, ? super E, ? extends R> mapFunction,
                                              final Supplier<Map<T, R>> mapFactory) {
        Assert.notNull(filterPredicate, "filterPredicate must be not null");
        Assert.notNull(mapFunction, "mapFunction must be not null");
        Supplier<Map<T, R>> finalMapFactory =
                isNull(mapFactory)
                        ? HashMap::new
                        : mapFactory;

        if (CollectionUtils.isEmpty(sourceMap)) {
            return finalMapFactory.get();
        }
        return sourceMap.entrySet()
                .stream()
                .filter(entry -> filterPredicate.test(entry.getKey(), entry.getValue()))
                .collect(
                        toMap(
                                Map.Entry::getKey,
                                entry -> mapFunction.apply(entry.getKey(), entry.getValue()),
                                overwriteWithNew(),
                                finalMapFactory
                        )
                );
    }


    /**
     * Counts the number of elements in the {@code sourceMap} which satisfy the {@code filterPredicate}.
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
                .filter(entry -> filterPredicate.test(entry.getKey(), entry.getValue()))
                .mapToInt(elto -> 1)
                .sum();
    }


    /**
     * Finds the first element of the given {@link Map} satisfying the provided {@link BiPredicate}.
     *
     * @param sourceMap
     *    {@link Map} to search
     * @param filterPredicate
     *    {@link BiPredicate} used to filter elements of {@code sourceMap}
     *
     * @return {@link Optional} of {@link Tuple2} containing the first element that satisfies {@code filterPredicate},
     *         {@link Optional#empty()} otherwise.
     */
    public static <T, E> Optional<Tuple2<T, E>> find(final Map<? extends T, ? extends E> sourceMap,
                                                     final BiPredicate<? super T, ? super E> filterPredicate) {
        if (CollectionUtils.isEmpty(sourceMap) ||
                isNull(filterPredicate)) {
            return empty();
        }
        return sourceMap.entrySet()
                .stream()
                .filter(entry -> filterPredicate.test(entry.getKey(), entry.getValue()))
                .findFirst()
                .map(entry -> Tuple.of(entry.getKey(), entry.getValue()));
    }


    /**
     *    Folds given {@link Map} values from the left, starting with {@code initialValue} and successively
     * calling {@code accumulator}.
     *
     * Example:
     *
     *   Parameters:                        Result:
     *    [(1, "Hi"), (2, "Hello")]          10
     *    0
     *    (k, v) -> k + v.length()
     *
     * @param sourceMap
     *    {@link Map} with elements to combine.
     * @param initialValue
     *    The initial value to start with.
     * @param accumulator
     *    A {@link TriFunction} which combines elements.
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
                    if (Objects.nonNull(accumulator)) {
                        for (var entry : sm.entrySet()) {
                            result = accumulator.apply(result, entry.getKey(), entry.getValue());
                        }
                    }
                    return result;
                })
                .orElse(initialValue);
    }


    /**
     * Partitions {@code sourceMap} into a {@link Map} of maps according to given {@code discriminator} {@link BiFunction}.
     *
     * Example:
     *
     *   Parameters:                                     Result:
     *    [(1, "Hi"), (2, "Hello"), (5, "World")]         [(0,  [(2, "Hello")])
     *    (k, v) -> k % 2                                  (1,  [(1, "Hi"), (5, "World")])]
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
        if (CollectionUtils.isEmpty(sourceMap) ||
                isNull(discriminator)) {
            return new HashMap<>();
        }
        Map<R, Map<T, E>> result = new HashMap<>();
        sourceMap.forEach(
                (k, v) -> {
                    R discriminatorResult = discriminator.apply(k, v);
                    result.putIfAbsent(discriminatorResult, new HashMap<>());
                    result.get(discriminatorResult)
                            .put(k, v);
                }
        );
        return result;
    }


    /**
     *    Partitions given {@code sourceMap} into a {@link Map} of {@link List} according to {@code discriminatorKey}.
     * Each element in a group is transformed into a value of type V using {@code valueMapper} {@link BiFunction}.
     *
     * It is equivalent to:
     *
     *    Map<R, Map<T, E>> groupedMap = groupBy(sourceMap, discriminatorKey)
     *    Map<R, List<V>> finalMap = mapValues(groupedMap, valueMapper)
     *
     * Example:
     *
     *   Parameters:                                             Result:
     *    [(1, "Hi"), (2, "Hello"), (5, "World"), (6, "!")]       [(0,  [1])
     *    (k, v) -> k % 3                                          (1,  [2])
     *    (k, v) -> v.length()                                     (2,  [5, 5])]
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
    public static <T, E, R, V> Map<R, List<V>> groupMap(final Map<? extends T, ? extends E> sourceMap,
                                                        final BiFunction<? super T, ? super E, ? extends R> discriminatorKey,
                                                        final BiFunction<? super T, ? super E, ? extends V> valueMapper) {
        return (Map)groupMap(sourceMap, discriminatorKey, valueMapper, ArrayList::new);
    }


    /**
     *    Partitions given {@code sourceMap} into a {@link Map} of {@link List} according to {@code discriminatorKey}.
     * Each element in a group is transformed into a value of type V using {@code valueMapper} {@link BiFunction}.
     *
     * It is equivalent to:
     *
     *    Map<R, Map<T, E>> groupedMap = groupBy(sourceMap, discriminatorKey)
     *    Map<R, List<V>> finalMap = mapValues(groupedMap, valueMapper)
     *
     * Example:
     *
     *   Parameters:                                             Result:
     *    [(1, "Hi"), (2, "Hello"), (5, "World"), (6, "!")]       [(0,  [1])
     *    (k, v) -> k % 3                                          (1,  [2])
     *    (k, v) -> v.length()                                     (2,  [5, 5])]
     *    ArrayList::new
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to transform.
     * @param discriminatorKey
     *    The discriminator {@link BiFunction} to get the key values of returned {@link Map}
     * @param valueMapper
     *    {@link BiFunction} to transform elements of {@code sourceMap}
     * @param collectionFactory
     *    {@link Supplier} of the {@link Collection} used to store the returned elements.
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
        Supplier<Collection<V>> finalCollectionFactory =
                isNull(collectionFactory)
                        ? ArrayList::new
                        : collectionFactory;
        Map<R, Collection<V>> result = new HashMap<>();
        sourceMap.forEach(
                (k, v) -> {
                    R discriminatorKeyResult = discriminatorKey.apply(k, v);
                    result.putIfAbsent(discriminatorKeyResult, finalCollectionFactory.get());
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
     * Example:
     *
     *   Parameters:                                              Intermediate Map:          Result:
     *    [(1, "Hi"), (2, "Hello"), (5, "World"), (6, "!")]        [(0,  [1])                 [(0, 1), (1, 2), (2, 10)]
     *    (k, v) -> k % 3                                           (1,  [2])
     *    (k, v) -> v.length()                                      (2,  [5, 5])]
     *    v -> v++
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
     * @throws IllegalArgumentException if {@code discriminatorKey} or {@code valueMapper} is {@code null}
     */
    public static <T, E, R, V> Map<R, V> groupMapReduce(final Map<? extends T, ? extends E> sourceMap,
                                                        final BiFunction<? super T, ? super E, ? extends R> discriminatorKey,
                                                        final BiFunction<? super T, ? super E, V> valueMapper,
                                                        final BinaryOperator<V> reduceValues) {
        Assert.notNull(discriminatorKey, "discriminatorKey must be not null");
        Assert.notNull(valueMapper, "valueMapper must be not null");
        Assert.notNull(reduceValues, "reduceValues must be not null");

        Map<R, V> result = new HashMap<>();
        groupMap(sourceMap, discriminatorKey, valueMapper)
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
     * @param sourceMap
     *    {@link Map} to used as source of the new one
     * @param mapFunction
     *    {@link BiFunction} used to transform given {@link Map} elements
     *
     * @return {@link Map}
     *
     * @throws IllegalArgumentException if {@code mapFunction} is {@code null}
     */
    public static <T, E, R, V> Map<R, V> map(final Map<? extends T, ? extends E> sourceMap,
                                             final BiFunction<? super T, ? super E, Tuple2<? extends R, ? extends V>> mapFunction) {
        return map(sourceMap, mapFunction, HashMap::new);
    }


    /**
     * Builds a new {@link Map} by applying a function to all elements of {@code sourceMap}.
     *
     * @param sourceMap
     *    {@link Map} to used as source of the new one
     * @param mapFunction
     *    {@link BiFunction} used to transform given {@link Map} elements
     * @param mapFactory
     *    {@link Supplier} of the {@link Map} used to store the returned elements
     *
     * @return {@link Map}
     *
     * @throws IllegalArgumentException if {@code mapFunction} is {@code null}
     */
    public static <T, E, R, V> Map<R, V> map(final Map<? extends T, ? extends E> sourceMap,
                                             final BiFunction<? super T, ? super E, Tuple2<? extends R, ? extends V>> mapFunction,
                                             final Supplier<Map<R, V>> mapFactory) {
        Assert.notNull(mapFunction, "mapFunction must be not null");
        Supplier<Map<R, V>> finalMapFactory =
                isNull(mapFactory)
                        ? HashMap::new
                        : mapFactory;

        if (CollectionUtils.isEmpty(sourceMap)) {
            return finalMapFactory.get();
        }
        return sourceMap.entrySet()
                .stream()
                .map(entry -> mapFunction.apply(entry.getKey(), entry.getValue()))
                .collect(
                        toMap(
                                t -> t._1,
                                t -> t._2,
                                overwriteWithNew(),
                                finalMapFactory
                        )
                );
    }


    /**
     * Builds a new {@link Map} by applying a function to all values of {@code sourceMap}.
     *
     * Example:
     *
     *   Parameters:                    Result:
     *    [(1, "A"), (3, "C")]           [(1, 2), (3, 4)]
     *    (k, v) -> k + v.length()
     *
     * @param sourceMap
     *    {@link Map} to used as source of the new one
     * @param mapFunction
     *    {@link BiFunction} used to transform given {@link Map} values
     *
     * @return updated {@link Map}
     */
    public static <T, E, R> Map<T, R> mapValues(final Map<? extends T, ? extends E> sourceMap,
                                                final BiFunction<? super T, ? super E, ? extends R> mapFunction) {
        return mapValues(sourceMap, mapFunction, HashMap::new);
    }


    /**
     * Builds a new {@link Map} by applying a function to all values of {@code sourceMap}.
     *
     * Example:
     *
     *   Parameters:                    Result:
     *    [(1, "A"), (3, "C")]           [(1, 2), (3, 4)]
     *    (k, v) -> k + v.length()
     *    HashMap::new
     *
     * @param sourceMap
     *    {@link Map} to used as source of the new one
     * @param mapFunction
     *    {@link BiFunction} used to transform given {@link Map} values
     * @param mapFactory
     *    {@link Supplier} of the {@link Map} used to store the returned elements
     *
     * @return updated {@link Map}
     */
    public static <T, E, R> Map<T, R> mapValues(final Map<? extends T, ? extends E> sourceMap,
                                                final BiFunction<? super T, ? super E, ? extends R> mapFunction,
                                                final Supplier<Map<T, R>> mapFactory) {
        Assert.notNull(mapFunction, "mapFunction must be not null");
        Supplier<Map<T, R>> finalMapFactory =
                isNull(mapFactory)
                        ? HashMap::new
                        : mapFactory;

        if (CollectionUtils.isEmpty(sourceMap)) {
            return finalMapFactory.get();
        }
        return sourceMap.entrySet()
                .stream()
                .collect(
                        toMap(
                                Map.Entry::getKey,
                                entry -> mapFunction.apply(entry.getKey(), entry.getValue()),
                                overwriteWithNew(),
                                finalMapFactory
                        )
                );
    }


    /**
     *    Returns a {@link Map} of {@link Boolean} as key, on which {@code true} contains all elements that satisfy given
     * {@code discriminator} and {@code false}, all elements that do not.
     *
     * Example:
     *
     *   Parameters:                        Result:
     *    [(1, "Hi"), (2, "Hello")]          [(true,  [(2, "Hello")])
     *    (k, v) -> k % 2 == 0                (false, [(1, "Hi")])]
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
        if (CollectionUtils.isEmpty(sourceMap) ||
                isNull(discriminator)) {
            return new HashMap<>();
        }
        Map<Boolean, Map<T, E>> result = new HashMap<>() {{
            put(Boolean.TRUE, new HashMap<>());
            put(Boolean.FALSE, new HashMap<>());
        }};
        sourceMap.forEach(
                (k, v) ->
                    result.get(discriminator.test(k, v))
                            .put(k, v)
        );
        return result;
    }


    /**
     * Returns a {@link Map} with the information of the given {@code sourceMap} excluding the keys of {@code keysToExclude}
     *
     * @param sourceMap
     *    {@link Map} with the information to filter
     * @param keysToExclude
     *    Keys to exclude from the provided {@link Map}
     *
     * @return {@link Map}
     */
    public static <T, E> Map<T, E> removeKeys(final Map<? extends T, ? extends E> sourceMap,
                                              final Collection<? extends T> keysToExclude) {
        return ofNullable(sourceMap)
                .map(sm -> {
                    Map<T, E> filteredMap = new HashMap<>(sourceMap);
                    if (Objects.nonNull(keysToExclude)) {
                        keysToExclude.forEach(filteredMap::remove);
                    }
                    return filteredMap;
                })
                .orElseGet(HashMap::new);
    }


    /**
     *    Using the provided {@code sourceMap}, return all elements beginning at index {@code from} and afterwards,
     * up to index {@code until} (excluding this one).
     *
     * Example 1:
     *
     *   Parameters:                      Result:
     *    [(1, "Hi"), (2, "Hello")]        [(2, "Hello")]
     *    1
     *    3
     *
     * Example 2:
     *
     *   Parameters:                      Result:
     *    [(1, "Hi"), (2, "Hello")]        [(1, "Hi")]
     *    0
     *    1
     *
     * Example 3:
     *
     *   Parameters:                      Result:
     *    [(1, "Hi"), (2, "Hello")]        [(1, "Hi"), (2, "Hello")]
     *    -1
     *    2
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
     * @throws IllegalArgumentException if {@code from} is upper than {@code until}
     */
    public static <T, E> Map<T, E> slice(final Map<? extends T, ? extends E> sourceMap,
                                         final int from,
                                         final int until) {
        Assert.isTrue(from < until, format("from: %d must be lower than to: %d", from, until));
        if (CollectionUtils.isEmpty(sourceMap) ||
                from > sourceMap.size() - 1) {
            return new HashMap<>();
        }
        int finalFrom = Math.max(0, from);
        int finalUntil = Math.min(sourceMap.size(), until);

        int i = 0;
        Map<T, E> result = new LinkedHashMap<>(Math.max(finalUntil - finalFrom, finalUntil - finalFrom - 1));
        for (var entry : sourceMap.entrySet()) {
            if (i >= finalUntil) {
                break;
            }
            if (i >= finalFrom) {
                result.put(entry.getKey(), entry.getValue());
            }
            i++;
        }
        return result;
    }


    /**
     * Loops through the provided {@link Map} one position every time, returning sublists with {@code size}
     *
     * Example 1:
     *
     *   Parameters:                          Result:
     *    [(1, "A"), (3, "C")]                 [[(1, "A"), (3, "C")]]
     *    5
     *
     * Example 2:
     *
     *   Parameters:                          Result:
     *    [(1, "A"), (3, "C"), (8, "Z")]       [[(1, "A"), (3, "C")], [(3, "C"), (8, "Z")]]
     *    2
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
        if (CollectionUtils.isEmpty(sourceMap) ||
                0 == size) {
            return new ArrayList<>();
        }
        if (size >= sourceMap.size()) {
            return asList(sourceMap);
        }
        int expectedSize = sourceMap.size() - size + 1;

        List<Map<T, E>> slides = IntStream.range(0, expectedSize)
                .mapToObj(index -> new LinkedHashMap<T, E>())
                .collect(Collectors.toList());

        int i = 0;
        for (var entry : sourceMap.entrySet()) {
            int xCoordinate = Math.min(i, expectedSize - 1);
            int yCoordinate = slides.get(xCoordinate).size();

            int window = xCoordinate;
            while (0 <= window && size > yCoordinate) {
                slides.get(window).put(entry.getKey(), entry.getValue());
                yCoordinate = slides.get(window).size();
                window--;
            }
            i++;
        }
        return slides;
    }


    /**
     * Splits the given {@link Map} in sublists with a size equal to the given {@code size}
     *
     * Example 1:
     *
     *   Parameters:                          Result:
     *    [(1, "A"), (3, "C"), (8, "Z")]       [[(1, "A"), (3, "C")], [(8, "Z")]]
     *    2
     *
     * Example 2:
     *
     *   Parameters:                          Result:
     *    [(1, "A"), (3, "C")]                 [[(1, "A"), (3, "C")]]
     *    3
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
        if (CollectionUtils.isEmpty(sourceMap) ||
                0 == size) {
            return new ArrayList<>();
        }
        int expectedSize = 0 == sourceMap.size() % size
                ? sourceMap.size() / size
                : (sourceMap.size() / size) + 1;

        List<Map<T, E>> splits = IntStream.range(0, expectedSize)
                .mapToObj(index -> new LinkedHashMap<T, E>())
                .collect(Collectors.toList());

        int i = 0, currentSplit = 0;
        for (var entry : sourceMap.entrySet()) {
            splits.get(currentSplit).put(entry.getKey(), entry.getValue());
            i++;
            if (i == size) {
                currentSplit++;
                i = 0;
            }
        }
        return splits;
    }


    private static <T> BinaryOperator<T> overwriteWithNew() {
        return (oldValue, newValue) -> newValue;
    }

}
