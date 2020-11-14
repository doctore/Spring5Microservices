package com.spring5microservices.common.util;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

@UtilityClass
public class CollectionUtil {

    /**
     * Return a {@link Collection} with the extracted property of the given {@code collection}
     *
     * @param collection
     *    Source {@link Collection} with the property to extract
     * @param keyExtractor
     *    {@link Function} used to get the key we want to use to include in returned {@link Collection}
     *
     * @return {@link List}
     */
    public static <T, E> List<E> collectProperty(final Collection<T> collection, Function<? super T, E> keyExtractor) {
        return (List)collectProperty(collection, keyExtractor, ArrayList::new);
    }


    /**
     * Return a {@link Collection} with the extracted property of the given {@code collection}
     *
     * @param collection
     *    Source {@link Collection} with the property to extract
     * @param keyExtractor
     *    {@link Function} used to get the key we want to use to include in returned {@link Collection}
     * @param collectionFactory
     *    {@link Supplier} of the {@link Collection} used to store the returned elements.
     *
     * @return {@link Collection}
     */
    public static <T, E> Collection<E> collectProperty(final Collection<T> collection, Function<? super T, E> keyExtractor,
                                                       Supplier<Collection<E>> collectionFactory) {
        return ofNullable(collection)
                .map(c -> {
                    if (null == keyExtractor) {
                        return null;
                    }
                    Stream<E> keyExtractedStream = collection.stream().map(keyExtractor);
                    return null == collectionFactory
                            ? keyExtractedStream.collect(toList())
                            : keyExtractedStream.collect(toCollection(collectionFactory));
                })
                .orElse(null == collectionFactory ? asList() : collectionFactory.get());
    }


    /**
     * Return the unique elements of the given {@link Collection}s.
     *
     * @param collections
     *    {@link Collection}s to concat
     *
     * @return {@link LinkedHashSet}
     */
    public static <T> Set<T> concatUniqueElements(final Collection<T> ...collections) {
        return ofNullable(collections)
                .map(c -> Stream.of(c).filter(Objects::nonNull)
                                      .flatMap(Collection::stream)
                                      .collect(toCollection(LinkedHashSet::new)))
                .orElse(new LinkedHashSet<>());
    }


    /**
     * Folds this elements from the left, starting with {@code initialValue} and successively calling {@code accumulator}.
     * Examples:
     *   [5, 7, 9],   1,  (a, b) -> a * b   => 315
     *   ["a", "h"], "!", (a, b) -> a + b   => "!ah"
     *
     * @param collection
     *    {@link Collection} with elements to combine
     * @param initialValue
     *    The initial value to start with
     * @param accumulator
     *    A function which combines elements
     *
     * @return a folded value
     *
     * @throws IllegalArgumentException if {@code initialValue} is {@code null}
     */
    public static <T, E> E foldLeft(final Collection<T> collection, final E initialValue,
                                    final BiFunction<E, ? super T, E> accumulator) {
        if (null == initialValue) {
            throw new IllegalArgumentException("initialValue must be not null");
        }
        return ofNullable(collection)
                .map(c -> {
                    E result = initialValue;
                    if (null != accumulator) {
                        for (T element : c) {
                            result = accumulator.apply(result, element);
                        }
                    }
                    return result;
                })
                .orElse(initialValue);
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
    public static <T, E> Map<T, E> removeKeys(final Map<T, E> sourceMap, final Collection<T> keysToExclude) {
        return ofNullable(sourceMap)
                .map(sm -> {
                    Map<T, E> filteredMap = new HashMap<>(sourceMap);
                    if (null != keysToExclude) {
                        keysToExclude.forEach(filteredMap::remove);
                    }
                    return filteredMap;
                })
                .orElse(new HashMap<>());
    }


    /**
     * Loops through the provided {@link List} one position every time, returning sublists with {@code size}
     * Examples:
     *   [1, 2]    with size = 5 => [[1, 2]]
     *   [7, 8, 9] with size = 2 => [[7, 8], [8, 9]]
     *
     * @param listToSlide
     *    {@link List} to slide
     * @param size
     *    Size of every sublist
     *
     * @return {@link List} of {@link List}s
     */
    public static <T> List<List<T>> sliding(final List<T> listToSlide, final int size) {
        if (null == listToSlide || 1 > size) {
            return new ArrayList<>();
        }
        if (size > listToSlide.size()) {
            return asList(listToSlide);
        }
        return IntStream.range(0, listToSlide.size() - size + 1)
                .mapToObj(start -> listToSlide.subList(start, start + size))
                .collect(toList());
    }


    /**
     * Splits the given {@link List} in sublists with a size equal to the given pageSize
     *
     * @param listToSplit
     *    {@link List} to split
     * @param size
     *    Size of every sublist
     *
     * @return {@link List} of {@link List}s
     */
    public static <T> List<List<T>> split(final List<T> listToSplit, final int size) {
        if (null == listToSplit || 1 > size) {
            return new ArrayList<>();
        }
        List<List<T>> splits = new ArrayList<>();
        for (int i = 0; i < listToSplit.size(); i += size) {
            splits.add(new ArrayList<>(
                    listToSplit.subList(i, Math.min(listToSplit.size(), i + size)))
            );
        }
        return splits;
    }


    /**
     * Transposes the given {@code collectionsToTranspose}.
     * Examples:
     *   [[1, 2, 3], [4, 5, 6]]                     =>  [[1, 4], [2, 5], [3, 6]]
     *   [["a1", "a2"], ["b1", "b2], ["c1", "c2"]]  =>  [["a1", "b1", "c1"], ["a2", "b2", "c2"]]
     *
     * @param collectionsToTranspose
     *    {@link Collection} of {@link Collection}s to transpose
     *
     * @return {@link List} of {@link List}s
     *
     * @throws IllegalArgumentException if not all {@code collectionsToTranspose} have the same size
     */
    public static <T> List<List<T>> transpose(final Collection<Collection<T>> collectionsToTranspose) {
        if (null == collectionsToTranspose || 1 > collectionsToTranspose.size()) {
            return new ArrayList<>();
        }
        int expectedSize = -1;
        List<Iterator<T>> iteratorList = new ArrayList<>(collectionsToTranspose.size());
        for (Collection<T> c: collectionsToTranspose) {
            if (expectedSize != c.size()) {
                if (-1 == expectedSize) {
                    expectedSize = c.size();
                }
                else {
                    throw new IllegalArgumentException("transpose requires all collections have the same size");
                }
            }
            iteratorList.add(c.iterator());
        }
        List<List<T>> result = new ArrayList<>(collectionsToTranspose.size());
        for (int i = 0; i < expectedSize; i++) {
            List<T> newRow = new ArrayList<>(expectedSize);
            for (Iterator<T> iterator: iteratorList) {
                newRow.add(iterator.next());
            }
            result.add(newRow);
        }
        return result;
    }

}
