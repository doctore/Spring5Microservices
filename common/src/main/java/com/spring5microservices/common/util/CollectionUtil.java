package com.spring5microservices.common.util;

import com.spring5microservices.common.collection.tuple.Tuple;
import com.spring5microservices.common.collection.tuple.Tuple2;
import lombok.experimental.UtilityClass;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

@UtilityClass
public class CollectionUtil {

    /**
     *    In the given {@code sourceCollection}, applies {@code defaultFunction} if the current element verifies
     * {@code filterPredicate}, otherwise applies {@code orElseFunction}.
     *
     * Example:
     *
     *   Parameters:              Result:
     *    [1, 2, 3, 6]             [2, 4, 4, 12]
     *    i -> i % 2 == 1
     *    i -> i + 1
     *    i -> i * 2
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to filter and transform.
     * @param filterPredicate
     *    {@link Predicate} to filter elements from {@code sourceCollection}
     * @param defaultFunction
     *    {@link Function} to transform elements of {@code sourceCollection} that verify {@code filterPredicate}
     * @param orElseFunction
     *    {@link Function} to transform elements of {@code sourceCollection} do not verify {@code filterPredicate}
     *
     * @return {@link List}
     *
     * @throws IllegalArgumentException if {@code filterPredicate}, {@code defaultFunction} or {@code orElseFunction}
     *                                  is {@code null}
     */
    public static <T, E> List<E> applyOrElse(final Collection<? extends T> sourceCollection,
                                             final Predicate<? super T> filterPredicate,
                                             final Function<? super T, ? extends E> defaultFunction,
                                             final Function<? super T, ? extends E> orElseFunction) {
        return (List<E>) applyOrElse(sourceCollection, filterPredicate, defaultFunction, orElseFunction, ArrayList::new);
    }


    /**
     *    In the given {@code sourceCollection}, applies {@code defaultFunction} if the current element verifies
     * {@code filterPredicate}, otherwise applies {@code orElseFunction}.
     *
     * Example:
     *
     *   Parameters:              Result:
     *    [1, 2, 3, 6]             [2, 4, 4, 12]
     *    i -> i % 2 == 1
     *    i -> i + 1
     *    i -> i * 2
     *    ArrayList::new
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to filter and transform
     * @param filterPredicate
     *    {@link Predicate} to filter elements from {@code sourceCollection}
     * @param defaultFunction
     *    {@link Function} to transform elements of {@code sourceCollection} that verify {@code filterPredicate}
     * @param orElseFunction
     *    {@link Function} to transform elements of {@code sourceCollection} do not verify {@code filterPredicate}
     * @param collectionFactory
     *    {@link Supplier} of the {@link Collection} used to store the returned elements.
     *    If {@code null} then {@link ArrayList}
     *
     * @return {@link Collection}
     *
     * @throws IllegalArgumentException if {@code filterPredicate}, {@code defaultFunction} or {@code orElseFunction}
     *                                  is {@code null}
     */
    public static <T, E> Collection<E> applyOrElse(final Collection<? extends T> sourceCollection,
                                                   final Predicate<? super T> filterPredicate,
                                                   final Function<? super T, ? extends E> defaultFunction,
                                                   final Function<? super T, ? extends E> orElseFunction,
                                                   final Supplier<Collection<E>> collectionFactory) {
        Assert.notNull(filterPredicate, "filterPredicate must be not null");
        Assert.notNull(defaultFunction, "defaultFunction must be not null");
        Assert.notNull(orElseFunction, "orElseFunction must be not null");
        Supplier<Collection<E>> finalCollectionFactory =
                isNull(collectionFactory)
                        ? ArrayList::new
                        : collectionFactory;

        if (CollectionUtils.isEmpty(sourceCollection)) {
            return finalCollectionFactory.get();
        }
        return sourceCollection.stream()
                .map(elto ->
                        filterPredicate.test(elto)
                                ? defaultFunction.apply(elto)
                                : orElseFunction.apply(elto)
                )
                .collect(toCollection(finalCollectionFactory));
    }


    /**
     * Returns a {@link LinkedHashSet} with the provided {@code elements}.
     *
     * @param elements
     *    Elements to include.
     *
     * @return {@link LinkedHashSet}
     */
    @SafeVarargs
    public static <T> Set<T> asSet(final T ...elements) {
        return ofNullable(elements)
                .map(e -> new LinkedHashSet<>(asList(elements)))
                .orElseGet(LinkedHashSet::new);
    }


    /**
     * Returns a {@link Collection} after:
     *
     *  - Filter its elements using {@code filterPredicate}
     *  - Transform its filtered elements using {@code mapFunction}
     *
     * Example:
     *
     *   Parameters:              Result:
     *    [1, 2, 3, 6]             ["1", "3"]
     *    i -> i % 2 == 1
     *    i -> i.toString()
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to filter and transform
     * @param filterPredicate
     *    {@link Predicate} to filter elements from {@code sourceCollection}
     * @param mapFunction
     *    {@link Function} to transform filtered elements from the source {@code sourceCollection}
     *
     * @return {@link List}
     *
     * @throws IllegalArgumentException if {@code filterPredicate} or {@code mapFunction} is {@code null}
     */
    public static <T, E> List<E> collect(final Collection<? extends T> sourceCollection,
                                         final Predicate<? super T> filterPredicate,
                                         final Function<? super T, ? extends E> mapFunction) {
        return (List<E>) collect(sourceCollection, filterPredicate, mapFunction, ArrayList::new);
    }


    /**
     * Returns a {@link Collection} after:
     *
     *  - Filter its elements using {@code filterPredicate}
     *  - Transform its filtered elements using {@code mapFunction}
     *
     * Example:
     *
     *   Parameters:              Result:
     *    [1, 2, 3, 6]             ["1", "3"]
     *    i -> i % 2 == 1
     *    i -> i.toString()
     *    ArrayList::new
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to filter and transform
     * @param filterPredicate
     *    {@link Predicate} to filter elements from {@code sourceCollection}
     * @param mapFunction
     *    {@link Function} to transform filtered elements from the source {@code sourceCollection}
     * @param collectionFactory
     *    {@link Supplier} of the {@link Collection} used to store the returned elements.
     *    If {@code null} then {@link ArrayList}
     *
     * @return {@link Collection}
     *
     * @throws IllegalArgumentException if {@code filterPredicate} or {@code mapFunction} is {@code null}
     */
    public static <T, E> Collection<E> collect(final Collection<? extends T> sourceCollection,
                                               final Predicate<? super T> filterPredicate,
                                               final Function<? super T, ? extends E> mapFunction,
                                               final Supplier<Collection<E>> collectionFactory) {
        Assert.notNull(filterPredicate, "filterPredicate must be not null");
        Assert.notNull(mapFunction, "mapFunction must be not null");
        Supplier<Collection<E>> finalCollectionFactory =
                isNull(collectionFactory)
                        ? ArrayList::new
                        : collectionFactory;

        if (CollectionUtils.isEmpty(sourceCollection)) {
            return finalCollectionFactory.get();
        }
        return sourceCollection
                .stream()
                .filter(filterPredicate)
                .map(mapFunction)
                .collect(toCollection(finalCollectionFactory));
    }


    /**
     *    Returns a {@link List} with the extracted property of the given {@code sourceCollection} using provided
     *  {@code propertyExtractor}.
     *
     * @param sourceCollection
     *    Source {@link Collection} with the property to extract.
     * @param propertyExtractor
     *    {@link Function} used to get the property value we want to use to include in returned {@link Collection}.
     *
     * @return {@link List}
     */
    public static <T, E> List<E> collectProperty(final Collection<? extends T> sourceCollection,
                                                 final Function<? super T, ? extends E> propertyExtractor) {
        return (List<E>) collectProperty(sourceCollection, propertyExtractor, ArrayList::new);
    }


    /**
     *    Returns a {@link Collection} with the extracted property of the given {@code sourceCollection} using provided
     * {@code propertyExtractor}.
     *
     * @param sourceCollection
     *    Source {@link Collection} with the property to extract
     * @param propertyExtractor
     *    {@link Function} used to get the property value we want to use to include in returned {@link Collection}
     * @param collectionFactory
     *    {@link Supplier} of the {@link Collection} used to store the returned elements.
     *    If {@code null} then {@link ArrayList}
     *
     * @return {@link Collection}
     */
    public static <T, E> Collection<E> collectProperty(final Collection<? extends T> sourceCollection,
                                                       final Function<? super T, ? extends E> propertyExtractor,
                                                       final Supplier<Collection<E>> collectionFactory) {
        return ofNullable(sourceCollection)
                .map(c -> {
                    if (isNull(propertyExtractor)) {
                        return null;
                    }
                    Stream<E> propertyExtractedStream = sourceCollection.stream().map(propertyExtractor);
                    return isNull(collectionFactory)
                            ? propertyExtractedStream.collect(toList())
                            : propertyExtractedStream.collect(toCollection(collectionFactory));
                })
                .orElseGet(() ->
                        isNull(collectionFactory)
                                ? new ArrayList<>()
                                : collectionFactory.get());
    }


    /**
     *    Returns a {@link List} of {@link Tuple} with the extracted properties of the given {@code sourceCollection}
     * using provided {@code propertyExtractors}.
     *
     * @param sourceCollection
     *    Source {@link Collection} with the properties to extract
     * @param propertyExtractors
     *    Array of {@link Function} used to get the properties values we want to use to include in returned {@link List}
     *
     * @return {@link List}
     *
     * @throws IllegalArgumentException if {@code propertyExtractors} is not {@code null} and its length > 5
     */
    @SafeVarargs
    public static <T> List<Tuple> collectProperties(final Collection<? extends T> sourceCollection,
                                                    final Function<? super T, ?> ...propertyExtractors) {
        return (List<Tuple>) collectProperties(sourceCollection, ArrayList::new, propertyExtractors);
    }


    /**
     *    Returns a {@link Collection} of {@link Tuple} with the extracted properties of the given {@code sourceCollection}
     * using provided {@code propertyExtractors}.
     *
     * @param sourceCollection
     *    Source {@link Collection} with the properties to extract
     * @param propertyExtractors
     *    Array of {@link Function} used to get the properties values we want to use to include in returned {@link Collection}
     * @param collectionFactory
     *    {@link Supplier} of the {@link Collection} used to store the returned elements.
     *    If {@code null} then {@link ArrayList}
     *
     * @return {@link Collection}
     *
     * @throws IllegalArgumentException if {@code propertyExtractors} is not {@code null} and its length > 5
     */
    @SafeVarargs
    public static <T> Collection<Tuple> collectProperties(final Collection<? extends T> sourceCollection,
                                                          final Supplier<Collection<Tuple>> collectionFactory,
                                                          final Function<? super T, ?> ...propertyExtractors) {
        Supplier<Collection<Tuple>> finalCollectionFactory =
                isNull(collectionFactory)
                        ? ArrayList::new
                        : collectionFactory;

        if (Objects.nonNull(propertyExtractors)) {
            Assert.isTrue(
                    Tuple.MAX_ALLOWED_TUPLE_ARITY >= propertyExtractors.length,
                    format("If propertyExtractors is not null then its size should be <= %s", Tuple.MAX_ALLOWED_TUPLE_ARITY)
            );
        }
        else {
            return finalCollectionFactory.get();
        }
        return ofNullable(sourceCollection)
                .map(c ->
                        c.stream()
                            .map(elto -> {
                                Tuple result = Tuple.empty();
                                for (Function<? super T, ?> propertyExtractor: propertyExtractors) {
                                    result = result.globalAppend(propertyExtractor.apply(elto));
                                }
                                return result;
                            })
                            .collect(toCollection(finalCollectionFactory))
                )
                .orElseGet(finalCollectionFactory);
    }


    /**
     * Returns the unique elements of the given {@link Collection}s.
     *
     * @param collections
     *    {@link Collection}s to concat
     *
     * @return {@link LinkedHashSet}
     */
    @SafeVarargs
    public static <T> Set<T> concatUniqueElements(final Collection<T> ...collections) {
        return ofNullable(collections)
                .map(c -> Stream.of(c).filter(Objects::nonNull)
                                      .flatMap(Collection::stream)
                                      .collect(toCollection(LinkedHashSet::new)))
                .orElseGet(LinkedHashSet::new);
    }


    /**
     * Counts the number of elements in the {@code sourceCollection} which satisfy the {@code filterPredicate}.
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to filter
     * @param filterPredicate
     *   {@link Predicate} to filter elements from {@code sourceCollection}
     *
     * @return the number of elements satisfying the {@link Predicate} {@code filterPredicate}
     */
    public static <T> int count(final Collection<? extends T> sourceCollection,
                                final Predicate<? super T> filterPredicate) {
        if (CollectionUtils.isEmpty(sourceCollection)) {
            return 0;
        }
        if (isNull(filterPredicate)) {
            return sourceCollection.size();
        }
        return sourceCollection
                .stream()
                .filter(filterPredicate)
                .mapToInt(elto -> 1)
                .sum();
    }


    /**
     * Finds the first element of the given {@link Collection} satisfying the provided {@link Predicate}.
     *
     * @param sourceCollection
     *    {@link Collection} to search
     * @param filterPredicate
     *    {@link Predicate} used to filter elements of {@code sourceCollection}
     *
     * @return {@link Optional} containing the first element that satisfies {@code filterPredicate},
     *         {@link Optional#empty()} otherwise.
     */
    public static <T> Optional<? extends T> find(final Collection<? extends T> sourceCollection,
                                                 final Predicate<? super T> filterPredicate) {
        if (CollectionUtils.isEmpty(sourceCollection) ||
                isNull(filterPredicate)) {
            return empty();
        }
        return getCollectionKeepingInternalOrdination(sourceCollection)
                .stream()
                .filter(filterPredicate)
                .findFirst();
    }


    /**
     * Finds the last element of the given {@link Collection} satisfying the provided {@link Predicate}.
     *
     * @param sourceCollection
     *    {@link Collection} to search
     * @param filterPredicate
     *    {@link Predicate} used to filter elements of {@code sourceCollection}
     *
     * @return {@link Optional} containing the last element that satisfies {@code filterPredicate},
     *         {@link Optional#empty()} otherwise.
     */
    public static <T> Optional<? extends T> findLast(final Collection<? extends T> sourceCollection,
                                                     final Predicate<? super T> filterPredicate) {
        if (CollectionUtils.isEmpty(sourceCollection) ||
                isNull(filterPredicate)) {
            return empty();
        }
        return reverseList(sourceCollection)
                .stream()
                .filter(filterPredicate)
                .findFirst();
    }


    /**
     *    Using the given value {@code initialValue} as initial one, applies the provided {@link BiFunction} to all
     * elements of {@code sourceCollection}, going left to right.
     *
     * Example 1:
     *
     *   Parameters:              Result:
     *    [5, 7, 9]                315
     *    1
     *    (a, b) -> a * b
     *
     * Example 2:
     *
     *   Parameters:              Result:
     *    ["a", "h"]               "!ah"
     *    "!"
     *    (a, b) -> a + b
     *
     * @param sourceCollection
     *    {@link Collection} with elements to combine.
     * @param initialValue
     *    The initial value to start with.
     * @param accumulator
     *    A {@link BiFunction} which combines elements.
     *
     * @return result of inserting {@code accumulator} between consecutive elements {@code sourceCollection}, going
     *         left to right with the start value {@code initialValue} on the left.
     *
     * @throws IllegalArgumentException if {@code initialValue} is {@code null}
     */
    public static <T, E> E foldLeft(final Collection<? extends T> sourceCollection,
                                    final E initialValue,
                                    final BiFunction<E, ? super T, E> accumulator) {
        Assert.notNull(initialValue, "initialValue must be not null");
        return ofNullable(sourceCollection)
                .map(CollectionUtil::getCollectionKeepingInternalOrdination)
                .map(sc -> {
                    E result = initialValue;
                    if (Objects.nonNull(accumulator)) {
                        for (T element : sc) {
                            result = accumulator.apply(result, element);
                        }
                    }
                    return result;
                })
                .orElse(initialValue);
    }


    /**
     *    Using the given value {@code initialValue} as initial one, applies the provided {@link BiFunction} to all
     * elements of {@code sourceCollection}, going right to left.
     *
     * Example 1:
     *
     *   Parameters:              Result:
     *    [5, 7, 9]                315
     *    1
     *    (a, b) -> a * b
     *
     * Example 2:
     *
     *   Parameters:              Result:
     *    ["a", "h"]               "!ha"
     *    "!"
     *    (a, b) -> a + b
     *
     * @param sourceCollection
     *    {@link Collection} with elements to combine.
     * @param initialValue
     *    The initial value to start with.
     * @param accumulator
     *    A {@link BiFunction} which combines elements.
     *
     * @return result of inserting {@code accumulator} between consecutive elements {@code sourceCollection}, going
     *         right to left with the start value {@code initialValue} on the right.
     *
     * @throws IllegalArgumentException if {@code initialValue} is {@code null}
     */
    public static <T, E> E foldRight(final Collection<? extends T> sourceCollection,
                                     final E initialValue,
                                     final BiFunction<E, ? super T, E> accumulator) {
        return foldLeft(
                reverseList(sourceCollection),
                initialValue,
                accumulator
        );
    }


    /**
     *    Partitions given {@code sourceCollection} into a {@link Map} of {@link List} according to {@code discriminatorKey}.
     * Each element in a group is transformed into a value of type V using {@code valueMapper} {@link Function}.
     *
     * It is equivalent to:
     *
     *    Map<K, List<T>> groupedMap = sourceCollection.stream().collect(groupingBy(discriminatorKey))
     *    Map<K, List<V>> finalMap = mapValues(groupedMap, valueMapper)
     *
     * Example:
     *
     *   Parameters:              Result:
     *    [1, 2, 3, 6]             [(0,  [4, 7])
     *    k -> i % 3                (1,  [2])
     *    i -> i + 1                (2,  [3])]
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to transform.
     * @param discriminatorKey
     *    The discriminator {@link Function} to get the key values of returned {@link Map}
     * @param valueMapper
     *    {@link Function} to transform elements of {@code sourceCollection}
     *
     * @return {@link Map}
     *
     * @throws IllegalArgumentException if {@code discriminatorKey} or {@code valueMapper} is {@code null}
     */
    public static <T, K, V> Map<K, List<V>> groupMap(final Collection<? extends T> sourceCollection,
                                                     final Function<? super T, ? extends K> discriminatorKey,
                                                     final Function<? super T, ? extends V> valueMapper) {
        return (Map) groupMap(sourceCollection, discriminatorKey, valueMapper, ArrayList::new);
    }


    /**
     *    Partitions given {@code sourceCollection} into a {@link Map} of {@link List} according to {@code discriminatorKey}.
     * Each element in a group is transformed into a value of type V using {@code valueMapper} {@link Function}.
     *
     * It is equivalent to:
     *
     *    Map<K, List<T>> groupedMap = sourceCollection.stream().collect(groupingBy(discriminatorKey))
     *    Map<K, List<V>> finalMap = mapValues(groupedMap, valueMapper)
     *
     * Example:
     *
     *   Parameters:              Result:
     *    [1, 2, 3, 6]             [(0,  [4, 7])
     *    k -> i % 3                (1,  [2])
     *    i -> i + 1                (2,  [3])]
     *    ArrayList::new
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to transform.
     * @param discriminatorKey
     *    The discriminator {@link Function} to get the key values of returned {@link Map}
     * @param valueMapper
     *    {@link Function} to transform elements of {@code sourceCollection}
     * @param collectionFactory
     *    {@link Supplier} of the {@link Collection} used to store the returned elements.
     *    If {@code null} then {@link ArrayList}
     *
     * @return {@link Map}
     *
     * @throws IllegalArgumentException if {@code discriminatorKey} or {@code valueMapper} is {@code null}
     */
    public static <T, K, V> Map<K, Collection<V>> groupMap(final Collection<? extends T> sourceCollection,
                                                           final Function<? super T, ? extends K> discriminatorKey,
                                                           final Function<? super T, ? extends V> valueMapper,
                                                           final Supplier<Collection<V>> collectionFactory) {
        Assert.notNull(discriminatorKey, "discriminatorKey must be not null");
        Assert.notNull(valueMapper, "valueMapper must be not null");
        if (CollectionUtils.isEmpty(sourceCollection)) {
            return new HashMap<>();
        }
        Supplier<Collection<V>> finalCollectionFactory =
                isNull(collectionFactory)
                        ? ArrayList::new
                        : collectionFactory;

        Map<K, Collection<V>> result = new HashMap<>();
        sourceCollection.forEach(
                e -> {
                    K discriminatorKeyResult = discriminatorKey.apply(e);
                    result.putIfAbsent(discriminatorKeyResult, finalCollectionFactory.get());
                    result.get(discriminatorKeyResult)
                            .add(valueMapper.apply(e));
                }
        );
        return result;
    }


    /**
     *    Partitions given {@code sourceCollection} into a {@link Map} of {@link List} according to {@code discriminatorKey}.
     * All the values that have the same discriminator are then transformed by the {@code valueMapper} {@link Function} and
     * then reduced into a single value with {@code reduceValues}.
     *
     * Example:
     *
     *   Parameters:              Intermediate Map:          Result:
     *    [1, 2, 3, 6]             [(0,  [4, 7])               [(0, 11), (1, 2), (2, 3)]
     *    k -> i % 3                (1,  [2])
     *    i -> i + 1                (2,  [3])]
     *    v -> v++
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to transform and reduce.
     * @param discriminatorKey
     *    The discriminator {@link Function} to get the key values of returned {@link Map}
     * @param valueMapper
     *    {@link Function} to transform elements of {@code sourceCollection}
     * @param reduceValues
     *    {@link BinaryOperator} used to reduces the values related with same key
     *
     * @return {@link Map}
     *
     * @throws IllegalArgumentException if {@code discriminatorKey}, {@code valueMapper} or {@code reduceValues}
     *         is {@code null}
     */
    public static <T, K, V> Map<K, V> groupMapReduce(final Collection<? extends T> sourceCollection,
                                                     final Function<? super T, ? extends K> discriminatorKey,
                                                     final Function<? super T, V> valueMapper,
                                                     final BinaryOperator<V> reduceValues) {
        Assert.notNull(discriminatorKey, "discriminatorKey must be not null");
        Assert.notNull(valueMapper, "valueMapper must be not null");
        Assert.notNull(reduceValues, "reduceValues must be not null");

        Map<K, V> result = new HashMap<>();
        groupMap(sourceCollection, discriminatorKey, valueMapper)
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
     *    Using {@code initialValue} as first element, apply {@code applyFunction} up to {@code untilPredicate} function
     * is {@code true}. The accumulated results are returned in a {@link List}.
     *
     * Example 1:
     *
     *   Parameters:             Result:
     *    42                      []
     *    a -> a / 10
     *    a -> 50 >= a
     *
     * Example 2:
     *
     *   Parameters:             Result:
     *    42                      [42, 4]
     *    a -> a / 10
     *    a -> 0 >= a
     *
     * @param initialValue
     *    The initial value to start with
     * @param applyFunction
     *    {@link UnaryOperator} to apply initially to {@code initialValue} and then next results
     * @param untilPredicate
     *    {@link Predicate} to know when to stop apply {@code applyFunction}
     *
     * @return {@link List}
     *
     * @throws IllegalArgumentException if {@code initialValue} or {@code untilPredicate} are {@code null}
     */
    public static <T> List<T> iterate(final T initialValue,
                                      final UnaryOperator<T> applyFunction,
                                      final Predicate<? super T> untilPredicate) {
        Assert.notNull(initialValue, "initialValue must be not null");
        Assert.notNull(untilPredicate, "untilPredicate must be not null");
        return ofNullable(applyFunction)
                .map(af -> {
                    List<T> result = new ArrayList<>();
                    T currentValue = initialValue;
                    while (!untilPredicate.test(currentValue)) {
                        result.add(currentValue);
                        currentValue = applyFunction.apply(currentValue);
                    }
                    return result;
                })
                .orElseGet(() -> asList(initialValue));
    }


    /**
     *    Returns an {@link List} used to loop through given {@code sourceCollection} in reverse order. When provided
     * {@link Collection} does not provide any kind of internal ordination, the returned {@link List} could return
     * same values with but in different positions every time.
     *
     * @param sourceCollection
     *    {@link Collection} to get elements in reverse order.
     *
     * @return {@link List}
     */
    public static <T> List<T> reverseList(final Collection<? extends T> sourceCollection) {
        return ofNullable(sourceCollection)
                .map(CollectionUtil::getCollectionKeepingInternalOrdination)
                .map(sc -> new ArrayList<T>(sc))
                .map(sl -> {
                    Collections.reverse(sl);
                    return sl;
                })
                .orElseGet(ArrayList::new);
    }


    /**
     *    Using the provided {@code sourceCollection}, return all elements beginning at index {@code from} and afterwards,
     * up to index {@code until} (excluding this one).
     *
     * Example 1:
     *
     *   Parameters:              Result:
     *    [5, 7, 9, 6]             [7, 9]
     *    1
     *    3
     *
     * Example 2:
     *
     *   Parameters:              Result:
     *    [a, b, c, d]             [d]
     *    3
     *    7
     *
     * Example 3:
     *
     *   Parameters:              Result:
     *    [a, b, c, d]             [a, b]
     *    -1
     *    2
     *
     * @param sourceCollection
     *    {@link Collection} to slice
     * @param from
     *    Lower limit of the chunk to extract from provided {@link Collection} (starting from {@code 0})
     * @param until
     *    Upper limit of the chunk to extract from provided {@link Collection} (up to {@link Collection#size()})
     *
     * @return {@link List}
     *
     * @throws IllegalArgumentException if {@code from} is upper than {@code until}
     */
    public static <T> List<T> slice(final Collection<? extends T> sourceCollection,
                                    final int from,
                                    final int until) {
        Assert.isTrue(from < until, format("from: %d must be lower than to: %d", from, until));
        if (CollectionUtils.isEmpty(sourceCollection) ||
                from > sourceCollection.size() - 1) {
            return new ArrayList<>();
        }
        int finalFrom = Math.max(0, from);
        int finalUntil = Math.min(sourceCollection.size(), until);
        if (sourceCollection instanceof List) {
            return ((List<T>) sourceCollection).subList(finalFrom, finalUntil);
        }

        int i = 0;
        List<T> result = new ArrayList<>(Math.max(finalUntil - finalFrom, finalUntil - finalFrom - 1));
        for (T element: getCollectionKeepingInternalOrdination(sourceCollection)) {
            if (i >= finalUntil) {
                break;
            }
            if (i >= finalFrom) {
                result.add(element);
            }
            i++;
        }
        return result;
    }


    /**
     * Loops through the provided {@link Collection} one position every time, returning sublists with {@code size}
     *
     * Example 1:
     *
     *   Parameters:              Result:
     *    [1, 2]                   [[1, 2]]
     *    5
     *
     * Example 2:
     *
     *   Parameters:              Result:
     *    [7, 8, 9]                [[7, 8], [8, 9]]
     *    2
     *
     * @param sourceCollection
     *    {@link Collection} to slide
     * @param size
     *    Size of every sublist
     *
     * @return {@link List} of {@link List}s
     *
     * @throws IllegalArgumentException if {@code size} is lower than 0
     */
    public static <T> List<List<T>> sliding(final Collection<? extends T> sourceCollection,
                                            final int size) {
        Assert.isTrue(0 <= size, "size must be a positive value");
        if (CollectionUtils.isEmpty(sourceCollection) ||
                0 == size) {
            return new ArrayList<>();
        }
        List<T> listToSlide = new ArrayList<>(
                getCollectionKeepingInternalOrdination(sourceCollection)
        );
        if (size >= listToSlide.size()) {
            return asList(listToSlide);
        }
        return IntStream.range(0, listToSlide.size() - size + 1)
                .mapToObj(start -> listToSlide.subList(start, start + size))
                .collect(toList());
    }


    /**
     * Splits the given {@link Collection} in sublists with a size equal to the given {@code size}
     *
     * Example 1:
     *
     *   Parameters:              Result:
     *    [1, 2, 3, 4]             [[1, 2], [3, 4]]
     *    2
     *
     * Example 2:
     *
     *   Parameters:              Result:
     *    [1, 2, 3, 4]             [[1, 2, 3], [4]]
     *    3
     *
     * Example 3:
     *
     *   Parameters:              Result:
     *    [1, 2, 3, 4]             [[1, 2, 3, 4]]
     *    5
     *
     * @param sourceCollection
     *    {@link Collection} to split
     * @param size
     *    Size of every sublist
     *
     * @return {@link List} of {@link List}s
     *
     * @throws IllegalArgumentException if {@code size} is lower than 0
     */
    public static <T> List<List<T>> split(final Collection<? extends T> sourceCollection,
                                          final int size) {
        Assert.isTrue(0 <= size, "size must be a positive value");
        if (CollectionUtils.isEmpty(sourceCollection) ||
                0 == size) {
            return new ArrayList<>();
        }
        List<T> listToSplit = new ArrayList<>(
                getCollectionKeepingInternalOrdination(sourceCollection)
        );
        int expectedSize = 0 == listToSplit.size() % size
                ? listToSplit.size() / size
                : (listToSplit.size() / size) + 1;

        List<List<T>> splits = new ArrayList<>(expectedSize);
        for (int i = 0; i < listToSplit.size(); i += size) {
            splits.add(new ArrayList<>(
                    listToSplit.subList(i, Math.min(listToSplit.size(), i + size)))
            );
        }
        return splits;
    }


    /**
     * Transposes the rows and columns of the given {@code sourceCollection}.
     *
     * Example 1:
     *
     *   Parameters:                                   Result:
     *    [[1, 2, 3], [4, 5, 6]]                        [[1, 4], [2, 5], [3, 6]]
     *
     * Example 2:
     *
     *   Parameters:                                   Result:
     *    [["a1", "a2"], ["b1", "b2], ["c1", "c2"]]     [["a1", "b1", "c1"], ["a2", "b2", "c2"]]
     *
     * Example 3:
     *
     *   Parameters:                                   Result:
     *    [[1, 2], [0], [7, 8, 9]]                      [[1, 0, 7], [2, 8], [9]]
     *
     * @param sourceCollection
     *    {@link Collection} of {@link Collection}s to transpose
     *
     * @return {@link List} of {@link List}s
     */
    public static <T> List<List<T>> transpose(final Collection<? extends Collection<T>> sourceCollection) {
        if (CollectionUtils.isEmpty(sourceCollection)) {
            return new ArrayList<>();
        }

        int sizeOfLongestSubCollection = -1;
        List<Iterator<T>> iteratorList = new ArrayList<>(sourceCollection.size());
        for (Collection<T> c: sourceCollection) {
            if (sizeOfLongestSubCollection < c.size()) {
                sizeOfLongestSubCollection = c.size();
            }
            iteratorList.add(c.iterator());
        }

        List<List<T>> result = new ArrayList<>(sizeOfLongestSubCollection);
        for (int i = 0; i < sizeOfLongestSubCollection; i++) {
            List<T> newRow = new ArrayList<>(sourceCollection.size());
            for (Iterator<T> iterator: iteratorList) {
                if (iterator.hasNext()) {
                    newRow.add(iterator.next());
                }
            }
            result.add(newRow);
        }
        return result;
    }


    /**
     *    Converts given {@code sourceCollection} of {@link Tuple2} into two {@link List} of the first and
     * second half of each pair.
     *
     * Example:
     *
     *   Parameters:                           Result:
     *    [("d", 6), ("h", 7), ("y", 11)]       [("d", "h", "y"), (6, 7, 11)]
     *
     * @param sourceCollection
     *    {@link Collection} of {@link Tuple2} to split its elements
     *
     * @return {@link Tuple2} of two {@link List}
     */
    public static <T, E> Tuple2<List<T>, List<E>> unzip(final Collection<Tuple2<T, E>> sourceCollection) {
        return foldLeft(
                sourceCollection,
                Tuple.of(new ArrayList<>(), new ArrayList<>()),
                (tupleOfLists, currentElto) -> {
                    tupleOfLists._1.add(currentElto._1);
                    tupleOfLists._2.add(currentElto._2);
                    return tupleOfLists;
                }
        );
    }


    /**
     *    Returns a {@link List} formed from {@code sourceLeftCollection} and {@code sourceRightCollection}
     * by combining corresponding elements in {@link Tuple2}. If one of the two collections is longer than
     * the other, its remaining elements are ignored.
     *
     * Example 1:
     *
     *   Parameters:              Result:
     *    ["d", "h", "y"]          [("d", 6), ("h", 7), ("y", 11)]
     *    [6, 7, 11]
     *
     * Example 2:
     *
     *   Parameters:              Result:
     *    [4, 9, 14]               [(4, 23), (9, 8)]
     *    [23, 8]
     *
     * @param sourceLeftCollection
     *    {@link Collection} with elements to be included as left side of returned {@link Tuple2}
     * @param sourceRightCollection
     *    {@link Collection} with elements to be included as right side of returned {@link Tuple2}
     *
     * @return {@link List} of {@link Tuple2}
     */
    public static <T, E> List<Tuple2<T, E>> zip(final Collection<? extends T> sourceLeftCollection,
                                                final Collection<? extends E> sourceRightCollection) {
        if (CollectionUtils.isEmpty(sourceLeftCollection) ||
                CollectionUtils.isEmpty(sourceRightCollection)) {
            return new ArrayList<>();
        }
        int minCollectionsSize = Math.min(sourceLeftCollection.size(), sourceRightCollection.size());

        Iterator<? extends T> leftIterator = sourceLeftCollection.iterator();
        Iterator<? extends E> rightIterator = sourceRightCollection.iterator();
        List<Tuple2<T, E>> result = new ArrayList<>();
        for (int i = 0; i < minCollectionsSize; i++) {
            result.add(
                    Tuple.of(leftIterator.next(), rightIterator.next())
            );
        }
        return result;
    }


    /**
     *    Returns a {@link List} formed from {@code sourceLeftCollection} and {@code sourceRightCollection}
     * by combining corresponding elements in {@link Tuple2}. If one of the two collections is shorter than
     * the other, placeholder elements are used to extend the shorter collection to the length of the longer.
     *
     * Example 1:
     *
     *   Parameters:              Result:
     *    ["d", "h", "y"]          [("d", 6), ("h", 7), ("y", 11)]
     *    [6, 7, 11]
     *    "z"
     *    55
     *
     * Example 2:
     *
     *   Parameters:              Result:
     *    [4, 9, 14]               [(4, 23), (9, 8), (14, 10)]
     *    [23, 8]
     *    17
     *    10
     *
     * Example 3:
     *
     *   Parameters:              Result:
     *    [4, 9]                   [(4, "f"), (9, "g"), (11, "m")]
     *    ["f", "g", "m"]
     *    11
     *    "u"
     *
     * @param sourceLeftCollection
     *    {@link Collection} with elements to be included as left side of returned {@link Tuple2}
     * @param sourceRightCollection
     *    {@link Collection} with elements to be included as right side of returned {@link Tuple2}
     * @param defaultLeftElement
     *    Element to be used to fill up the result if {@code sourceLeftCollection} is shorter than {@code sourceRightCollection}
     * @param defaultRightElement
     *    Element to be used to fill up the result if {@code sourceRightCollection} is shorter than {@code sourceLeftCollection}
     *
     * @return {@link List} of {@link Tuple2}
     */
    public static <T, E> List<Tuple2<T, E>> zipAll(final Collection<T> sourceLeftCollection,
                                                   final Collection<E> sourceRightCollection,
                                                   final T defaultLeftElement,
                                                   final E defaultRightElement) {
        int maxCollectionSize = Math.max(
                CollectionUtils.isEmpty(sourceLeftCollection) ? 0 : sourceLeftCollection.size(),
                CollectionUtils.isEmpty(sourceRightCollection) ? 0 : sourceRightCollection.size()
        );
        Iterator<T> leftIterator = ofNullable(sourceLeftCollection).map(Collection::iterator).orElse(null);
        Iterator<E> rightIterator = ofNullable(sourceRightCollection).map(Collection::iterator).orElse(null);
        List<Tuple2<T, E>> result = new ArrayList<>();

        for (int i = 0; i < maxCollectionSize; i++) {
            result.add(
                    Tuple.of(
                            ofNullable(leftIterator)
                                    .filter(Iterator::hasNext)
                                    .map(Iterator::next)
                                    .orElse(defaultLeftElement),
                            ofNullable(rightIterator)
                                    .filter(Iterator::hasNext)
                                    .map(Iterator::next)
                                    .orElse(defaultRightElement)
                    )
            );
        }
        return result;
    }


    /**
     *    Returns a {@link List} containing pairs consisting of all elements of this iterable collection paired with
     * their index. Indices start at {@code 0}.
     *
     * Example:
     *
     *   Parameters:              Result:
     *    ["d", "h", "y"]          [(0, "d"), (1, "h"), (2, "y")]
     *
     * @param sourceCollection
     *    {@link Collection} to extract: index and element
     *
     * @return {@link List} of {@link Tuple2}s
     */
    public static <T> List<Tuple2<Integer, T>> zipWithIndex(final Collection<? extends T> sourceCollection) {
        if (CollectionUtils.isEmpty(sourceCollection)) {
            return new ArrayList<>();
        }
        int i = 0;
        List<Tuple2<Integer, T>> result = new ArrayList<>(sourceCollection.size());
        for (T element: sourceCollection) {
            result.add(Tuple.of(i, element));
            i++;
        }
        return result;
    }


    /**
     *    Returns a {@link Collection} with the elements of the given {@code sourceCollection} considering special use
     * cases like {@link PriorityQueue}, on which internal {@link Iterator} does not take into account internal ordering.
     *
     * @param sourceCollection
     *    {@link Collection} to iterate.
     *
     * @return {@link Collection}
     */
    private static <T> Collection<T> getCollectionKeepingInternalOrdination(Collection<T> sourceCollection) {
        if (sourceCollection instanceof PriorityQueue) {
            PriorityQueue<T> cloneQueue = new PriorityQueue<>(sourceCollection);
            List<T> result = new ArrayList<>(sourceCollection.size());
            for (int i = 0; i < sourceCollection.size(); i++) {
                result.add(cloneQueue.poll());
            }
            return result;
        }
        else {
            return sourceCollection;
        }
    }

}
