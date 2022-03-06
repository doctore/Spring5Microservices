package com.spring5microservices.common.util;

import lombok.experimental.UtilityClass;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

import static java.util.Optional.ofNullable;

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
        Map<T, R> result = new HashMap<>();
        sourceMap.forEach(
                (k, v) -> {
                    if (filterPredicate.test(k, v)) {
                        result.put(
                                k,
                                defaultFunction.apply(k, v)
                        );
                    }
                    else {
                        result.put(
                                k,
                                orElseFunction.apply(k, v)
                        );
                    }
                }
        );
        return result;
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
        Map<T, R> result = new HashMap<>();
        sourceMap.forEach(
                (k, v) -> {
                    if (filterPredicate.test(k, v)) {
                        result.put(
                                k,
                                mapFunction.apply(k, v)
                        );
                    }
                }
        );
        return result;
    }


    /**
     * Return a {@link Map} with the information of the given {@code sourceMap} excluding the keys of {@code keysToExclude}
     *
     * @param sourceMap
     *    {@link Map} with the information to filter
     * @param keysToExclude
     *    Keys to exclude from the provided {@link Map}
     *
     * @return {@link HashMap}
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
        Map<T, R> result = new HashMap<>();
        sourceMap.forEach(
                (k, v) ->
                        result.put(
                                k,
                                mapFunction.apply(k, v)
                        )
        );
        return result;
    }

}
