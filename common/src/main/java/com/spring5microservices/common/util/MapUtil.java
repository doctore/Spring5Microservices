package com.spring5microservices.common.util;

import com.spring5microservices.common.dto.PairDto;
import com.spring5microservices.common.interfaces.functional.TriFunction;
import lombok.experimental.UtilityClass;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;

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
     *   [1, 2, 3, 6],  i -> i % 2 == 1,  i -> i + 1,  i -> i * 2  =>  [2, 4, 4, 12]
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to filter and transform.
     * @param filterPredicate
     *    {@link BiPredicate} to filter elements from the source {@code sourceMap}.
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
        Assert.notNull(filterPredicate, "filterPredicate must be not null");
        Assert.notNull(defaultFunction, "defaultFunction must be not null");
        Assert.notNull(orElseFunction, "orElseFunction must be not null");
        if (CollectionUtils.isEmpty(sourceMap)) {
            return new HashMap<>();
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
                                overwriteWithNew()
                        )
                );
    }


    /**
     * Return a {@link Map} after:
     *
     *  - Filter its elements using {@code filterPredicate}
     *  - Transform its filtered elements using {@code mapFunction}
     *
     * Example:
     *   [(1, "Hi"), (2, "Hello")],  (k, v) -> k % 2 == 0,  (k, v) -> k + v.length()  =>  [(2, 6)]
     *
     * @param sourceMap
     *    Source {@link Map} with the elements to filter and transform.
     * @param filterPredicate
     *    {@link BiPredicate} to filter elements from the source {@code sourceMap}.
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
        Assert.notNull(filterPredicate, "filterPredicate must be not null");
        Assert.notNull(mapFunction, "mapFunction must be not null");
        if (CollectionUtils.isEmpty(sourceMap)) {
            return new HashMap<>();
        }
        return sourceMap.entrySet()
                .stream()
                .filter(entry -> filterPredicate.test(entry.getKey(), entry.getValue()))
                .collect(
                        toMap(
                                Map.Entry::getKey,
                                entry -> mapFunction.apply(entry.getKey(), entry.getValue()),
                                overwriteWithNew()
                        )
                );
    }


    /**
     * Finds the first element of the given {@link Map} satisfying the provided {@link BiPredicate}.
     *
     * @param sourceMap
     *    {@link Map} to search
     * @param filterPredicate
     *    {@link BiPredicate} used to filter elements of {@code sourceMap}
     *
     * @return {@link Optional} of {@link PairDto} containing the first element that satisfies {@code filterPredicate},
     *         {@link Optional#empty()} otherwise.
     */
    public static <T, E> Optional<PairDto<T, E>> find(final Map<? extends T, ? extends E> sourceMap,
                                                      final BiPredicate<? super T, ? super E> filterPredicate) {
        if (CollectionUtils.isEmpty(sourceMap) ||
                Objects.isNull(filterPredicate)) {
            return empty();
        }
        return sourceMap.entrySet()
                .stream()
                .filter(entry -> filterPredicate.test(entry.getKey(), entry.getValue()))
                .findFirst()
                .map(entry -> PairDto.of(entry.getKey(), entry.getValue()));
    }


    /**
     *    Folds given {@link Map} values from the left, starting with {@code initialValue} and successively
     * calling {@code accumulator}.
     *
     * Example:
     *   [(1, "Hi"), (2, "Hello")],  0,  (k, v) -> k + v.length()  =>  10
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
     *   [(1, "Hi"), (2, "Hello"), (5, "World")],  (k, v) -> k % 2  =>  [(0,  [(2, "Hello")]
     *                                                                   (1,  [(1, "Hi"), (5, "World")]]
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
                Objects.isNull(discriminator)) {
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
     *    Returns a {@link Map} of {@link Boolean} as key, on which {@code true} contains all elements that satisfy given
     * {@code discriminator} and {@code false}, all elements that do not.
     *
     * Example:
     *   [(1, "Hi"), (2, "Hello")],  (k, v) -> k % 2 == 0  =>  [(true,  [(2, "Hello")]
     *                                                          (false, [(1, "Hi")]]
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
                Objects.isNull(discriminator)) {
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
                    if (null != keysToExclude) {
                        keysToExclude.forEach(filteredMap::remove);
                    }
                    return filteredMap;
                })
                .orElseGet(HashMap::new);
    }


    /**
     * Transforms all the values of given {@code sourceMap} using the provided {@code mapFunction}
     *
     * Example:
     *   [(1, "A"), (3, "C")],  (k, v) -> k + v.length()  =>  [(1, 2), (3, 4)]
     *
     * @param sourceMap
     *    {@link Map} to update its values
     * @param mapFunction
     *    {@link BiFunction} used to update given {@link Map} values
     *
     * @return updated {@link Map}
     */
    public static <T, E, R> Map<T, R> transform(final Map<? extends T, ? extends E> sourceMap,
                                                final BiFunction<? super T, ? super E, ? extends R> mapFunction) {
        Assert.notNull(mapFunction, "mapFunction must be not null");
        if (CollectionUtils.isEmpty(sourceMap)) {
            return new HashMap<>();
        }
        return sourceMap.entrySet()
                .stream()
                .collect(
                        toMap(
                                Map.Entry::getKey,
                                entry -> mapFunction.apply(entry.getKey(), entry.getValue()),
                                overwriteWithNew()
                        )
                );
    }


    private static <T> BinaryOperator<T> overwriteWithNew() {
        return (oldValue, newValue) -> newValue;
    }

}
