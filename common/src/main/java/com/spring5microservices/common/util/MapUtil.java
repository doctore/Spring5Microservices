package com.spring5microservices.common.util;

import lombok.experimental.UtilityClass;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import static java.util.Optional.ofNullable;

@UtilityClass
public class MapUtil {

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
     * @param sourceMap
     *    {@link Map} to update its values
     * @param mapFunction
     *    {@link BiFunction} used to update given {@link Map} values
     *
     * @return updated {@link Map}
     */
    public static <T, U, R> Map<T, R> transform(final Map<? extends T, ? extends U> sourceMap,
                                                final BiFunction<? super T, ? super U, ? extends R> mapFunction) {
        Assert.notNull(mapFunction, "mapFunction must be not null");
        if (CollectionUtils.isEmpty(sourceMap)) {
            return new HashMap<>();
        }
        Map<T, R> result = new HashMap<>();
        sourceMap.forEach(
                (k, v) ->
                        result.put(k, mapFunction.apply(k, v))
        );
        return result;
    }

}
