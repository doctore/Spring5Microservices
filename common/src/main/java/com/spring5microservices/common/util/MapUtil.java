package com.spring5microservices.common.util;

import com.spring5microservices.common.collection.tuple.Tuple;
import com.spring5microservices.common.collection.tuple.Tuple2;
import com.spring5microservices.common.interfaces.functional.TriFunction;
import lombok.experimental.UtilityClass;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static com.spring5microservices.common.util.PredicateUtil.biAlwaysTrue;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@UtilityClass
public class MapUtil {

    /**
     *    In the given {@code sourceMap}, applies {@code defaultFunction} if the current element verifies
     * {@code filterPredicate}, otherwise applies {@code orElseFunction}.
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
     *    {@link BiPredicate} to filter elements from {@code sourceMap}
     * @param defaultFunction
     *    {@link BiFunction} to transform elements of {@code sourceMap} that verify {@code filterPredicate}
     * @param orElseFunction
     *    {@link BiFunction} to transform elements of {@code sourceMap} do not verify {@code filterPredicate}
     *
     * @return {@link Map}
     *
     * @throws IllegalArgumentException if {@code defaultFunction} or {@code orElseFunction} is {@code null}
     *                                  with a not empty {@code sourceMap}
     */
    public static <T, E, R> Map<T, R> applyOrElse(final Map<? extends T, ? extends E> sourceMap,
                                                  final BiPredicate<? super T, ? super E> filterPredicate,
                                                  final BiFunction<? super T, ? super E, ? extends R> defaultFunction,
                                                  final BiFunction<? super T, ? super E, ? extends R> orElseFunction) {
        return applyOrElse(
                sourceMap,
                filterPredicate,
                defaultFunction,
                orElseFunction,
                HashMap::new
        );
    }


    /**
     *    In the given {@code sourceMap}, applies {@code defaultFunction} if the current element verifies
     * {@code filterPredicate}, otherwise applies {@code orElseFunction}.
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
     *    {@link BiPredicate} to filter elements from {@code sourceMap}
     * @param defaultFunction
     *    {@link BiFunction} to transform elements of {@code sourceMap} that verify {@code filterPredicate}
     * @param orElseFunction
     *    {@link BiFunction} to transform elements of {@code sourceMap} do not verify {@code filterPredicate}
     * @param mapFactory
     *    {@link Supplier} of the {@link Map} used to store the returned elements.
     *    If {@code null} then {@link HashMap}
     *
     * @return {@link Map}
     *
     * @throws IllegalArgumentException if {@code defaultFunction} or {@code orElseFunction} is {@code null}
     *                                  with a not empty {@code sourceMap}
     */
    public static <T, E, R> Map<T, R> applyOrElse(final Map<? extends T, ? extends E> sourceMap,
                                                  final BiPredicate<? super T, ? super E> filterPredicate,
                                                  final BiFunction<? super T, ? super E, ? extends R> defaultFunction,
                                                  final BiFunction<? super T, ? super E, ? extends R> orElseFunction,
                                                  final Supplier<Map<T, R>> mapFactory) {
        final Supplier<Map<T, R>> finalMapFactory = ObjectUtil.getOrElse(
                mapFactory,
                HashMap::new
        );
        if (CollectionUtils.isEmpty(sourceMap)) {
            return finalMapFactory.get();
        }
        Assert.notNull(defaultFunction, "defaultFunction must be not null");
        Assert.notNull(orElseFunction, "orElseFunction must be not null");
        final BiPredicate<? super T, ? super E> finalFilterPredicate = ObjectUtil.getOrElse(
                filterPredicate,
                biAlwaysTrue()
        );
        return sourceMap.entrySet()
                .stream()
                .map(entry ->
                        finalFilterPredicate.test(entry.getKey(), entry.getValue())
                                ? Map.entry(
                                        entry.getKey(),
                                        defaultFunction.apply(
                                                entry.getKey(),
                                                entry.getValue()
                                        )
                                )
                                : Map.entry(
                                        entry.getKey(),
                                        orElseFunction.apply(
                                                entry.getKey(),
                                                entry.getValue()
                                        )
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
     * <p>
     *  - Filter its elements using {@code filterPredicate}
     *  - Transform its filtered elements using {@code mapFunction}
     *
     * <pre>
     * Example:
     *
     *   Parameters:                    Result:
     *    [(1, "Hi"), (2, "Hello")]      [(2, 7)]
     *    (k, v) -> k % 2 == 0
     *    (k, v) -> k + v.length()
     * </pre>
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to filter and transform
     * @param filterPredicate
     *    {@link BiPredicate} to filter elements from {@code sourceMap}
     * @param mapFunction
     *    {@link BiFunction} to transform filtered elements from the source {@code sourceMap}
     *
     * @return {@link Map}
     *
     * @throws IllegalArgumentException if {@code mapFunction} is {@code null} with a not empty {@code sourceMap}
     */
    public static <T, E, R> Map<T, R> collect(final Map<? extends T, ? extends E> sourceMap,
                                              final BiPredicate<? super T, ? super E> filterPredicate,
                                              final BiFunction<? super T, ? super E, ? extends R> mapFunction) {
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
     *   Parameters:                    Result:
     *    [(1, "Hi"), (2, "Hello")]      [(2, 7)]
     *    (k, v) -> k % 2 == 0
     *    (k, v) -> k + v.length()
     *    HashMap::new
     * </pre>
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to filter and transform
     * @param filterPredicate
     *    {@link BiPredicate} to filter elements from {@code sourceMap}
     * @param mapFunction
     *    {@link BiFunction} to transform filtered elements from the source {@code sourceMap}
     * @param mapFactory
     *    {@link Supplier} of the {@link Map} used to store the returned elements
     *    If {@code null} then {@link HashMap}
     *
     * @return {@link Map}
     *
     * @throws IllegalArgumentException if {@code mapFunction} is {@code null} with a not empty {@code sourceMap}
     */
    public static <T, E, R> Map<T, R> collect(final Map<? extends T, ? extends E> sourceMap,
                                              final BiPredicate<? super T, ? super E> filterPredicate,
                                              final BiFunction<? super T, ? super E, ? extends R> mapFunction,
                                              final Supplier<Map<T, R>> mapFactory) {
        final Supplier<Map<T, R>> finalMapFactory = ObjectUtil.getOrElse(
                mapFactory,
                HashMap::new
        );
        if (CollectionUtils.isEmpty(sourceMap)) {
            return finalMapFactory.get();
        }
        Assert.notNull(mapFunction, "mapFunction must be not null");
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
                        toMap(
                                Map.Entry::getKey,
                                entry ->
                                        mapFunction.apply(
                                                entry.getKey(),
                                                entry.getValue()
                                        ),
                                overwriteWithNew(),
                                finalMapFactory
                        )
                );
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
     *    [(1, "Hi"), (2, "Hello")]      Optional(Tuple2.of(2, "Hello"))
     *    (k, v) -> k % 2 == 0
     * </pre>
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
                        Tuple.of(
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
     * Converts given {@code sourceMap} into a {@link List} formed by the elements of these iterable collections.
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
        final Supplier<Map<R, Map<T, E>>> finalMapResultFactory = ObjectUtil.getOrElse(
                mapResultFactory,
                HashMap::new
        );
        final Supplier<Map<T, E>> finalMapValuesFactory = ObjectUtil.getOrElse(
                mapValuesFactory,
                HashMap::new
        );
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
     *   Parameters:                             Result:
     *    [(1, "AGTF"), (3, "CD")]                [(1, 4), (3, 2)]
     *    (k, v) -> Tuple.of(k, v.length())
     * </pre>
     *
     * @param sourceMap
     *    {@link Map} to used as source of the new one
     * @param mapFunction
     *    {@link BiFunction} used to transform given {@code sourceMap} elements
     *
     * @return {@link Map}
     *
     * @throws IllegalArgumentException if {@code mapFunction} is {@code null}
     */
    public static <T, E, R, V> Map<R, V> map(final Map<? extends T, ? extends E> sourceMap,
                                             final BiFunction<? super T, ? super E, Tuple2<? extends R, ? extends V>> mapFunction) {
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
     *   Parameters:                             Result:
     *    [(1, "AGTF"), (3, "CD")]                [(1, 4), (3, 2)]
     *    (k, v) -> Tuple.of(k, v.length())
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
                                             final BiFunction<? super T, ? super E, Tuple2<? extends R, ? extends V>> mapFunction,
                                             final Supplier<Map<R, V>> mapFactory) {
        final Supplier<Map<R, V>> finalMapFactory = ObjectUtil.getOrElse(
                mapFactory,
                HashMap::new
        );
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
        final Supplier<Map<T, R>> finalMapFactory = ObjectUtil.getOrElse(
                mapFactory,
                HashMap::new
        );
        if (CollectionUtils.isEmpty(sourceMap)) {
            return finalMapFactory.get();
        }
        Assert.notNull(mapFunction, "mapFunction must be not null");
        return sourceMap.entrySet()
                .stream()
                .collect(
                        toMap(
                                Map.Entry::getKey,
                                entry ->
                                        mapFunction.apply(
                                                entry.getKey(),
                                                entry.getValue()
                                        ),
                                overwriteWithNew(),
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
     *    (t1, t2) -> t1._1.compareTo(t2._1)
     * </pre>
     *
     * @param sourceMap
     *    {@link Map} used to find the largest element
     * @param comparator
     *    {@link Comparator} to be used for comparing elements
     *
     * @return {@link Optional} of {@link Tuple2} containing the largest element using {@code comparator},
     *         {@link Optional#empty()} if {@code sourceMap} has no elements.
     *
     * @throws IllegalArgumentException if {@code comparator} is {@code null}
     */
    public static <T, E> Optional<Tuple2<T, E>> max(final Map<? extends T, ? extends E> sourceMap,
                                                    final Comparator<Tuple2<? extends T, ? extends E>> comparator) {
        Assert.notNull(comparator, "comparator must be not null");
        return ofNullable(sourceMap)
                .map(m -> {
                    Tuple2<T, E> largestElement = null;
                    for (var entry : m.entrySet()) {
                        Tuple2<T, E> currentElement = Tuple.of(
                                entry.getKey(),
                                entry.getValue()
                        );
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
     * @return {@link Optional} of {@link Tuple2} containing the smallest value using {@code comparator},
     *         {@link Optional#empty()} if {@code sourceMap} has no elements.
     *
     * @throws IllegalArgumentException if {@code comparator} is {@code null}
     */
    public static <T, E> Optional<Tuple2<T, E>> min(final Map<? extends T, ? extends E> sourceMap,
                                                    final Comparator<Tuple2<? extends T, ? extends E>> comparator) {
        Assert.notNull(comparator, "comparator must be not null");
        return ofNullable(sourceMap)
                .map(m -> {
                    Tuple2<T, E> smallestElement = null;
                    for (var entry : m.entrySet()) {
                        Tuple2<T, E> currentElement = Tuple.of(
                                entry.getKey(),
                                entry.getValue()
                        );
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
        final Supplier<Map<T, E>> finalMapFactory = ObjectUtil.getOrElse(
                mapFactory,
                HashMap::new
        );
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
     * @throws IllegalArgumentException if {@code from} is upper than {@code until}
     */
    public static <T, E> Map<T, E> slice(final Map<? extends T, ? extends E> sourceMap,
                                         final int from,
                                         final int until) {
        Assert.isTrue(
                from < until,
                format("from: %d must be lower than to: %d",
                        from, until
                )
        );
        if (CollectionUtils.isEmpty(sourceMap) || from > sourceMap.size() - 1) {
            return new HashMap<>();
        }
        final int finalFrom = Math.max(0, from);
        final int finalUntil = Math.min(sourceMap.size(), until);
        int i = 0;
        Map<T, E> result = new LinkedHashMap<>(
                Math.max(
                        finalUntil - finalFrom,
                        finalUntil - finalFrom - 1
                )
        );
        for (var entry : sourceMap.entrySet()) {
            if (i >= finalUntil) {
                break;
            }
            if (i >= finalFrom) {
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
        final Supplier<Map<T, E>> finalMapFactory = ObjectUtil.getOrElse(
                mapFactory,
                HashMap::new
        );
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
                        toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                overwriteWithNew(),
                                finalMapFactory
                        )
                );
    }


    private static <T> BinaryOperator<T> overwriteWithNew() {
        return (oldValue, newValue) -> newValue;
    }

}
