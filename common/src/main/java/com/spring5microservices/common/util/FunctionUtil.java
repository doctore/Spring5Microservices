package com.spring5microservices.common.util;

import lombok.experimental.UtilityClass;
import org.springframework.util.Assert;

import java.util.AbstractMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

@UtilityClass
public class FunctionUtil {

    /**
     *    Transforms the given {@link BiFunction} {@code keyValueMapper} into another one used to create new objects from
     * {@link Map#entry(Object, Object)} instances.
     *
     * @param keyValueMapper
     *    {@link BiFunction} to transform key/value of returned {@link Function} {@link Map#entry(Object, Object)} source
     *
     * @return {@link Function} to transform {@link Map#entry(Object, Object)} instances into another object
     *
     * @throws IllegalArgumentException if {@code keyMapper} or {@code valueMapper} are {@code null}
     * @throws NullPointerException if provided {@link Map#entry(Object, Object)} is {@code null}
     */
    public static <T, K, V> Function<Map.Entry<K, V>, T> fromBiFunctionToMapEntryFunction(final BiFunction<? super K, ? super V, ? extends T> keyValueMapper) {
        Assert.notNull(keyValueMapper, "keyValueMapper must be not null");
        return (entry) ->
                keyValueMapper.apply(
                        entry.getKey(),
                        entry.getValue()
                );
    }


    /**
     *    Transforms the given {@link BiFunction} {@code keyMapper} and {@code valueMapper} into another one used to create
     * {@link Map#entry(Object, Object)} instances.
     *
     * @param keyMapper
     *    {@link BiFunction} to transform given {@link Map.Entry} to a key of returned {@link Map#entry(Object, Object)}
     * @param valueMapper
     *    {@link BiFunction} to transform given {@link Map.Entry} to a value of returned {@link Map#entry(Object, Object)}
     *
     * @return {@link Function} to create {@link Map#entry(Object, Object)} instances from other ones
     *
     * @throws IllegalArgumentException if {@code keyMapper} or {@code valueMapper} are {@code null}
     */
    public static <K1, K2, V1, V2> Function<Map.Entry<K1, V1>, Map.Entry<K2, V2>> fromBiFunctionsToMapEntriesFunction(final BiFunction<? super K1, ? super V1, ? extends K2> keyMapper,
                                                                                                                      final BiFunction<? super K1, ? super V1, ? extends V2> valueMapper) {
        Assert.notNull(keyMapper, "keyMapper must be not null");
        Assert.notNull(valueMapper, "valueMapper must be not null");
        return (entryMap) ->
                new AbstractMap.SimpleEntry<>(
                        keyMapper.apply(
                                entryMap.getKey(),
                                entryMap.getValue()
                        ),
                        valueMapper.apply(
                                entryMap.getKey(),
                                entryMap.getValue()
                        )
                );
    }


    /**
     *    Transforms the given {@link Function} {@code keyMapper} and {@code valueMapper} into another one used to create
     * {@link Map#entry(Object, Object)} instances.
     *
     * @param keyMapper
     *    {@link Function} to transform given element to a key of returned {@link Map#entry(Object, Object)}
     * @param valueMapper
     *    {@link Function} to transform given element to a value of returned {@link Map#entry(Object, Object)}
     *
     * @return {@link Function} to create {@link Map#entry(Object, Object)} instances
     *
     * @throws IllegalArgumentException if {@code keyMapper} or {@code valueMapper} are {@code null}
     */
    public static <T, K, V> Function<T, Map.Entry<K, V>> fromFunctionsToMapEntryFunction(final Function<? super T, ? extends K> keyMapper,
                                                                                         final Function<? super T, ? extends V> valueMapper) {
        Assert.notNull(keyMapper, "keyMapper must be not null");
        Assert.notNull(valueMapper, "valueMapper must be not null");
        return (t) ->
                new AbstractMap.SimpleEntry<>(
                        keyMapper.apply(t),
                        valueMapper.apply(t)
                );
    }



    /**
     * Helper method used in conflict resolution when old and new instance already exist, returning the new one.
     *
     * <pre>
     *   toMap(
     *      Map.Entry::getKey,
     *      Map.Entry::getValue,
     *      overwriteWithNew()
     *   )
     * </pre>
     */
    public static <T> BinaryOperator<T> overwriteWithNew() {
        return (oldInstance, newInstance) -> newInstance;
    }

}
