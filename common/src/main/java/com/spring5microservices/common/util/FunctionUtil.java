package com.spring5microservices.common.util;

import lombok.experimental.UtilityClass;
import org.springframework.util.Assert;

import java.util.AbstractMap;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;

@UtilityClass
public class FunctionUtil {

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
    public static <T, K, V> Function<T, Map.Entry<K, V>> fromKeyValueMapperToMapEntry(final Function<? super T, ? extends K> keyMapper,
                                                                                      final Function<? super T, ? extends V> valueMapper) {
        Assert.notNull(keyMapper, "keyMapper must be not null");
        Assert.notNull(valueMapper, "valueMapper must be not null");
        return (t) -> {
            K key = keyMapper.apply(t);
            V value = valueMapper.apply(t);
            return new AbstractMap.SimpleEntry<>(
                    key,
                    value
            );
        };
    }


    /**
     * Helper method used in conflict resolution when old and new instance already exist, returning the new one.
     *
     * <pre>
     * Example:
     *
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
