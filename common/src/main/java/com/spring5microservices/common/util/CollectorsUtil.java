package com.spring5microservices.common.util;

import lombok.experimental.UtilityClass;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

@UtilityClass
public class CollectorsUtil {

    /**
     *    Returns a {@link Collector} that accumulates elements into a {@link Map} whose keys and values are the result
     * of applying the provided mapping functions to the input elements.
     * <p>
     *    If the mapped keys contain duplicates (according to {@link Object#equals(Object)}), the value mapping function
     * is applied to each equal element, and the results are merged keeping the last value. The {@link Map} returned will
     * be an instance of {@link HashMap}.
     * <p>
     *    This function overwrites existing {@link Collectors#toMap(Function, Function)} because it does not allow
     * {@code null} values and throwing {@link NullPointerException} when happens. There is an open bug related with:
     * <p>
     *     <a href="https://bugs.openjdk.org/browse/JDK-8148463">Null values not allowed</a>
     *
     * @param keyMapper
     *    Mapping {@link Function} to produce keys
     * @param valueMapper
     *    Mapping {@link Function} to produce values
     *
     * @return {@link Collector} which collects elements into a {@link Map} whose keys are the result of applying
     *         {@code keyMapper} to the input elements, and whose values are the result of applying {@code valueMapper}
     *         to all input elements equal to the key.
     *
     * @throws IllegalArgumentException if {@code keyMapper} or {@code valueMapper} are {@code null}
     */
    public static <T, K, U> Collector<T, ?, Map<K, U>> toMapNullableValues(final Function<? super T, ? extends K> keyMapper,
                                                                           final Function<? super T, ? extends U> valueMapper) {
        return toMapNullableValues(
                keyMapper,
                valueMapper,
                overwriteWithNew(),
                HashMap::new
        );
    }


    /**
     *    Returns a {@link Collector} that accumulates elements into a {@link Map} whose keys and values are the result
     * of applying the provided mapping functions to the input elements.
     * <p>
     *    If the mapped keys contain duplicates (according to {@link Object#equals(Object)}), the value mapping function
     * is applied to each equal element, and the results are merged using the provided {@code mergeFunction}. The {@link Map}
     * returned will be an instance of {@link HashMap}.
     * <p>
     *    This function overwrites existing {@link Collectors#toMap(Function, Function)} because it does not allow
     * {@code null} values and throwing {@link NullPointerException} when happens. There is an open bug related with:
     * <p>
     *     <a href="https://bugs.openjdk.org/browse/JDK-8148463">Null values not allowed</a>
     *
     * @param keyMapper
     *    Mapping {@link Function} to produce keys
     * @param valueMapper
     *    Mapping {@link Function} to produce values
     * @param mergeFunction
     *    Merge {@link Function}, used to resolve collisions between values associated with the same key, as supplied to
     *    {@link Map#merge(Object, Object, BiFunction)}. If no one was given, new values will overwrite existing ones.
     *
     * @return {@link Collector} which collects elements into a {@link Map} whose keys are the result of applying
     *         {@code keyMapper} to the input elements, and whose values are the result of applying {@code valueMapper}
     *         to all input elements equal to the key and combining them using {@code mergeFunction}.
     *
     * @throws IllegalArgumentException if {@code keyMapper} or {@code valueMapper} are {@code null}
     */
    public static <T, K, U> Collector<T, ?, Map<K, U>> toMapNullableValues(final Function<? super T, ? extends K> keyMapper,
                                                                           final Function<? super T, ? extends U> valueMapper,
                                                                           final BinaryOperator<U> mergeFunction) {
        return toMapNullableValues(
                keyMapper,
                valueMapper,
                mergeFunction,
                HashMap::new
        );
    }


    /**
     *    Returns a {@link Collector} that accumulates elements into a {@link Map} whose keys and values are the result
     * of applying the provided mapping functions to the input elements.
     * <p>
     *    If the mapped keys contain duplicates (according to {@link Object#equals(Object)}), the value mapping function
     * is applied to each equal element, and the results are merged keeping the last value. The {@link Map} returned will
     * be an instance of {@link HashMap}.
     * <p>
     *    This function overwrites existing {@link Collectors#toMap(Function, Function)} because it does not allow
     * {@code null} values and throwing {@link NullPointerException} when happens. There is an open bug related with:
     * <p>
     *     <a href="https://bugs.openjdk.org/browse/JDK-8148463">Null values not allowed</a>
     *
     * @param keyMapper
     *    Mapping {@link Function} to produce keys
     * @param valueMapper
     *    Mapping {@link Function} to produce values
     * @param mapFactory
     *    {@link Supplier} providing a new empty {@link Map} into which the results will be inserted. If no one was given,
     *    {@link HashMap} will be used by default
     *
     * @return {@link Collector} which collects elements into a {@link Map} whose keys are the result of applying
     *         {@code keyMapper} to the input elements, and whose values are the result of applying {@code valueMapper}
     *         to all input elements equal to the key.
     *
     * @throws IllegalArgumentException if {@code keyMapper} or {@code valueMapper} are {@code null}
     */
    public static <T, K, U> Collector<T, ?, Map<K, U>> toMapNullableValues(final Function<? super T, ? extends K> keyMapper,
                                                                           final Function<? super T, ? extends U> valueMapper,
                                                                           final Supplier<Map<K, U>> mapFactory) {
        return toMapNullableValues(
                keyMapper,
                valueMapper,
                overwriteWithNew(),
                mapFactory
        );
    }


    /**
     *    Returns a {@link Collector} that accumulates elements into a {@link Map} whose keys and values are the result
     * of applying the provided mapping functions to the input elements.
     * <p>
     *    If the mapped keys contain duplicates (according to {@link Object#equals(Object)}), the value mapping function
     * is applied to each equal element, and the results are merged using the provided {@code mergeFunction}. The
     * {@link Map} is created by a provided {@code mapFactory}.
     * <p>
     *    This function overwrites existing {@link Collectors#toMap(Function, Function)} because it does not allow
     * {@code null} values and throwing {@link NullPointerException} when happens. There is an open bug related with:
     * <p>
     *     <a href="https://bugs.openjdk.org/browse/JDK-8148463">Null values not allowed</a>
     *
     * @param keyMapper
     *    Mapping {@link Function} to produce keys
     * @param valueMapper
     *    Mapping {@link Function} to produce values
     * @param mergeFunction
     *    Merge {@link Function}, used to resolve collisions between values associated with the same key, as supplied to
     *    {@link Map#merge(Object, Object, BiFunction)}. If no one was given, new values will overwrite existing ones
     * @param mapFactory
     *    {@link Supplier} providing a new empty {@link Map} into which the results will be inserted. If no one was given,
     *    {@link HashMap} will be used by default
     *
     * @return {@link Collector} which collects elements into a {@link Map} whose keys are the result of applying
     *         {@code keyMapper} to the input elements, and whose values are the result of applying {@code valueMapper}
     *         to all input elements equal to the key and combining them using {@code mergeFunction}.
     *
     * @throws IllegalArgumentException if {@code keyMapper} or {@code valueMapper} are {@code null}
     */
    public static <T, K, U> Collector<T, ?, Map<K, U>> toMapNullableValues(final Function<? super T, ? extends K> keyMapper,
                                                                           final Function<? super T, ? extends U> valueMapper,
                                                                           final BinaryOperator<U> mergeFunction,
                                                                           final Supplier<Map<K, U>> mapFactory) {
        Assert.notNull(keyMapper, "keyMapper must be not null");
        Assert.notNull(valueMapper, "valueMapper must be not null");
        final BinaryOperator<U> finalMergeFunction = ObjectUtil.getOrElse(
                mergeFunction,
                overwriteWithNew()
        );
        final Supplier<Map<K, U>> finalMapFactory = ObjectUtil.getOrElse(
                mapFactory,
                HashMap::new
        );
        return collectingAndThen(
                toList(),
                list -> {
                    Map<K, U> result = finalMapFactory.get();
                    for(T item : list) {
                        K key = keyMapper.apply(item);
                        U newValue = valueMapper.apply(item);
                        U value = result.containsKey(key)
                                ? finalMergeFunction.apply(
                                        result.get(key),
                                        newValue
                                  )
                                : newValue;
                        result.put(key, value);
                    }
                    return result;
                }
      );
    }


    /**
     *    Helper method used in conflict resolution on {@link Map}'s creation, when the new instance already contains
     * an entry with the same key but different value than the new one to add. In this case, the new value will be returned.
     */
    private static <T> BinaryOperator<T> overwriteWithNew() {
        return (oldValue, newValue) -> newValue;
    }

}
