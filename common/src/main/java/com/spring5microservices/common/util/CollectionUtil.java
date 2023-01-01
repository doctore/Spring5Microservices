package com.spring5microservices.common.util;

import com.spring5microservices.common.collection.tuple.Tuple;
import com.spring5microservices.common.collection.tuple.Tuple2;
import lombok.experimental.UtilityClass;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
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
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.spring5microservices.common.util.ObjectsUtil.getOrElse;
import static com.spring5microservices.common.util.PredicateUtil.alwaysTrue;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
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
     * <pre>
     * Example:
     *
     *   Parameters:             Result:
     *    [1, 2, 3, 6]            [2, 4, 4, 12]
     *    i -> i % 2 == 1
     *    i -> i + 1
     *    i -> i * 2
     * </pre>
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
     * @throws IllegalArgumentException if {@code defaultFunction} or {@code orElseFunction} is {@code null}
     *                                  with a not empty {@code sourceCollection}
     */
    public static <T, E> List<E> applyOrElse(final Collection<? extends T> sourceCollection,
                                             final Predicate<? super T> filterPredicate,
                                             final Function<? super T, ? extends E> defaultFunction,
                                             final Function<? super T, ? extends E> orElseFunction) {
        return (List<E>) applyOrElse(
                sourceCollection,
                filterPredicate,
                defaultFunction,
                orElseFunction,
                ArrayList::new
        );
    }


    /**
     *    In the given {@code sourceCollection}, applies {@code defaultFunction} if the current element verifies
     * {@code filterPredicate}, otherwise applies {@code orElseFunction}.
     *
     * <pre>
     * Example:
     *
     *   Parameters:             Result:
     *    [1, 2, 3, 6]            [2, 4, 4, 12]
     *    i -> i % 2 == 1
     *    i -> i + 1
     *    i -> i * 2
     *    ArrayList::new
     * </pre>
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
     * @throws IllegalArgumentException if {@code defaultFunction} or {@code orElseFunction} is {@code null}
     *                                  with a not empty {@code sourceCollection}
     */
    public static <T, E> Collection<E> applyOrElse(final Collection<? extends T> sourceCollection,
                                                   final Predicate<? super T> filterPredicate,
                                                   final Function<? super T, ? extends E> defaultFunction,
                                                   final Function<? super T, ? extends E> orElseFunction,
                                                   final Supplier<Collection<E>> collectionFactory) {
        final Supplier<Collection<E>> finalCollectionFactory = getOrElse(
                collectionFactory,
                ArrayList::new
        );
        if (CollectionUtils.isEmpty(sourceCollection)) {
            return finalCollectionFactory.get();
        }
        Assert.notNull(defaultFunction, "defaultFunction must be not null");
        Assert.notNull(orElseFunction, "orElseFunction must be not null");
        final Predicate<? super T> finalFilterPredicate = getOrElse(
                filterPredicate,
                alwaysTrue()
        );
        return sourceCollection.stream()
                .map(elto ->
                        finalFilterPredicate.test(elto)
                                ? defaultFunction.apply(elto)
                                : orElseFunction.apply(elto)
                )
                .collect(
                        toCollection(finalCollectionFactory)
                );
    }


    /**
     * Returns a {@link Set} with the provided {@code elements}.
     *
     * @param elements
     *    Elements to include
     *
     * @return {@link LinkedHashSet}
     */
    @SafeVarargs
    public static <T> Set<T> asSet(final T ...elements) {
        return asSet(
                LinkedHashSet::new,
                elements
        );
    }


    /**
     * Returns a {@link Set} with the provided {@code elements}.
     *
     * <pre>
     * Example:
     *
     *   Parameters:              Result:
     *    LinkedHashSet::new       [2, 1]
     *    2
     *    1
     *    2
     * </pre>
     *
     * @param setFactory
     *   {@link Supplier} of the {@link Set} used to store the returned elements
     * @param elements
     *    Elements to include
     *
     * @return {@link Set}
     */
    @SafeVarargs
    public static <T> Set<T> asSet(final Supplier<Set<T>> setFactory,
                                   final T ...elements) {
        final Supplier<Set<T>> finalSetFactory = getOrElse(
                setFactory,
                LinkedHashSet::new
        );
        return ofNullable(elements)
                .map(e ->
                        Arrays.stream(e)
                                .collect(
                                        toCollection(finalSetFactory)
                                )
                )
                .orElseGet(finalSetFactory);
    }


    /**
     * Returns a {@link Collection} after:
     * <p>
     *  - Filter its elements using {@code filterPredicate}
     *  - Transform its filtered elements using {@code mapFunction}
     *
     * <pre>
     * Example:
     *
     *   Parameters:              Result:
     *    [1, 2, 3, 6]             ["1", "3"]
     *    i -> i % 2 == 1
     *    i -> i.toString()
     * </pre>
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
     * @throws IllegalArgumentException if {@code mapFunction} is {@code null} with a not empty {@code sourceCollection}
     */
    @SuppressWarnings("unchecked")
    public static <T, E> List<E> collect(final Collection<? extends T> sourceCollection,
                                         final Predicate<? super T> filterPredicate,
                                         final Function<? super T, ? extends E> mapFunction) {
        return (List<E>) collect(
                sourceCollection,
                filterPredicate,
                mapFunction,
                ArrayList::new
        );
    }


    /**
     * Returns a {@link Collection} after:
     * <p>
     *  - Filter its elements using {@code filterPredicate}
     *  - Transform its filtered elements using {@code mapFunction}
     *
     * <pre>
     * Example:
     *
     *   Parameters:              Result:
     *    [1, 2, 3, 6]             ["1", "3"]
     *    i -> i % 2 == 1
     *    i -> i.toString()
     *    ArrayList::new
     * </pre>
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
     * @throws IllegalArgumentException if {@code mapFunction} is {@code null} with a not empty {@code sourceCollection}
     */
    public static <T, E> Collection<E> collect(final Collection<? extends T> sourceCollection,
                                               final Predicate<? super T> filterPredicate,
                                               final Function<? super T, ? extends E> mapFunction,
                                               final Supplier<Collection<E>> collectionFactory) {
        final Supplier<Collection<E>> finalCollectionFactory = getOrElse(
                collectionFactory,
                ArrayList::new
        );
        if (CollectionUtils.isEmpty(sourceCollection)) {
            return finalCollectionFactory.get();
        }
        Assert.notNull(mapFunction, "mapFunction must be not null");
        final Predicate<? super T> finalFilterPredicate = getOrElse(
                filterPredicate,
                alwaysTrue()
        );
        return sourceCollection
                .stream()
                .filter(finalFilterPredicate)
                .map(mapFunction)
                .collect(
                        toCollection(finalCollectionFactory)
                );
    }


    /**
     *    Returns a {@link List} with the extracted property of the given {@code sourceCollection} using provided
     *  {@code propertyExtractor}.
     *
     * <pre>
     * Example:
     *
     *   Parameters:                                                             Result:
     *    [new PizzaDto("Carbonara", 5D), new PizzaDto("Margherita", 10D)]        ["Carbonara", "Margherita"]
     *    PizzaDto::getName
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} with the property to extract.
     * @param propertyExtractor
     *    {@link Function} used to get the property value we want to use to include in returned {@link Collection}.
     *
     * @return {@link List}
     */
    @SuppressWarnings("unchecked")
    public static <T, E> List<E> collectProperty(final Collection<? extends T> sourceCollection,
                                                 final Function<? super T, ? extends E> propertyExtractor) {
        return (List<E>) collectProperty(
                sourceCollection,
                propertyExtractor,
                ArrayList::new
        );
    }


    /**
     *    Returns a {@link Collection} with the extracted property of the given {@code sourceCollection} using provided
     * {@code propertyExtractor}.
     *
     * <pre>
     * Example:
     *
     *   Parameters:                                                             Result:
     *    [new PizzaDto("Carbonara", 5D), new PizzaDto("Margherita", 10D)]        ["Carbonara", "Margherita"]
     *    PizzaDto::getName
     *    ArrayList::new
     * </pre>
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
        final Supplier<Collection<E>> finalCollectionFactory = getOrElse(
                collectionFactory,
                ArrayList::new
        );
        return ofNullable(sourceCollection)
                .map(c -> {
                    if (isNull(propertyExtractor)) {
                        return null;
                    }
                    return sourceCollection.stream()
                            .map(propertyExtractor)
                            .collect(
                                    toCollection(finalCollectionFactory)
                            );
                })
                .orElseGet(finalCollectionFactory);
    }


    /**
     *    Returns a {@link List} of {@link Tuple} with the extracted properties of the given {@code sourceCollection}
     * using provided {@code propertyExtractors}.
     *
     * <pre>
     * Example:
     *
     *   Parameters:                                                                       Result:
     *    [new UserDto(1L, "user1 name", "user1 address", 11, "2011-11-11 13:00:05")]       [Tuple2.of("user1 name", 11)]
     *    [UserDto::getName, UserDto::getAge]
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} with the properties to extract
     * @param propertyExtractors
     *    Array of {@link Function} used to get the properties values we want to use to include in returned {@link List}
     *
     * @return {@link List}
     *
     * @throws IllegalArgumentException if {@code propertyExtractors} is not {@code null} and
     *                                  its length > {@link Tuple#MAX_ALLOWED_TUPLE_ARITY}
     */
    @SafeVarargs
    public static <T> List<Tuple> collectProperties(final Collection<? extends T> sourceCollection,
                                                    final Function<? super T, ?> ...propertyExtractors) {
        return (List<Tuple>) collectProperties(
                sourceCollection,
                ArrayList::new,
                propertyExtractors
        );
    }


    /**
     *    Returns a {@link Collection} of {@link Tuple} with the extracted properties of the given {@code sourceCollection}
     * using provided {@code propertyExtractors}.
     *
     * <pre>
     * Example:
     *
     *   Parameters:                                                                       Result:
     *    [new UserDto(1L, "user1 name", "user1 address", 11, "2011-11-11 13:00:05")]       [Tuple2.of("user1 name", 11)]
     *    [UserDto::getName, UserDto::getAge]
     *    ArrayList::new
     * </pre>
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
     * @throws IllegalArgumentException if {@code propertyExtractors} is not {@code null} and
     *                                  its length > {@link Tuple#MAX_ALLOWED_TUPLE_ARITY}
     */
    @SafeVarargs
    public static <T> Collection<Tuple> collectProperties(final Collection<? extends T> sourceCollection,
                                                          final Supplier<Collection<Tuple>> collectionFactory,
                                                          final Function<? super T, ?> ...propertyExtractors) {
        final Supplier<Collection<Tuple>> finalCollectionFactory = getOrElse(
                collectionFactory,
                ArrayList::new
        );
        if (nonNull(propertyExtractors)) {
            Assert.isTrue(
                    Tuple.MAX_ALLOWED_TUPLE_ARITY >= propertyExtractors.length,
                    format("If propertyExtractors is not null then its size should be <= %d",
                            Tuple.MAX_ALLOWED_TUPLE_ARITY
                    )
            );
        } else {
            return finalCollectionFactory.get();
        }
        return ofNullable(sourceCollection)
                .map(c ->
                        c.stream()
                            .map(elto -> {
                                Tuple result = Tuple.empty();
                                for (Function<? super T, ?> propertyExtractor: propertyExtractors) {
                                    result = result.globalAppend(
                                            propertyExtractor.apply(elto)
                                    );
                                }
                                return result;
                            })
                            .collect(
                                    toCollection(finalCollectionFactory)
                            )
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
                .map(c ->
                        Stream.of(c)
                                .filter(Objects::nonNull)
                                .flatMap(Collection::stream)
                                .collect(
                                        toCollection(LinkedHashSet::new)
                                )
                )
                .orElseGet(LinkedHashSet::new);
    }


    /**
     * Counts the number of elements in the {@code sourceCollection} which satisfy the {@code filterPredicate}.
     *
     * <pre>
     * Example:
     *
     *   Parameters:             Result:
     *    [1, 2, 3, 6]            2
     *    i -> i % 2 == 1
     * </pre>
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
     *    Returns a {@link List} removing the elements of provided {@code sourceCollection} that satisfy the {@link Predicate}
     * {@code filterPredicate}.
     *
     * <pre>
     * Example:
     *
     *   Parameters:             Result:
     *    [1, 2, 3, 6]            [2, 6]
     *    i -> i % 2 == 1
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to filter
     * @param filterPredicate
     *    {@link Predicate} to filter elements from {@code sourceCollection}
     *
     * @return {@link List}
     */
    public static <T> List<T> dropWhile(final Collection<? extends T> sourceCollection,
                                        final Predicate<? super T> filterPredicate) {
        return (List<T>) dropWhile(
                sourceCollection,
                filterPredicate,
                ArrayList::new
        );
    }


    /**
     *    Returns a {@link Collection} removing the elements of provided {@code sourceCollection} that satisfy the {@link Predicate}
     * {@code filterPredicate}.
     *
     * <pre>
     * Example:
     *
     *   Parameters:             Result:
     *    [1, 2, 3, 6]            [2, 6]
     *    i -> i % 2 == 1
     *    ArrayList::new
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to filter
     * @param filterPredicate
     *    {@link Predicate} to filter elements from {@code sourceCollection}
     * @param collectionFactory
     *   {@link Supplier} of the {@link Collection} used to store the returned elements
     *
     * @return {@link Collection}
     */
    public static <T> Collection<T> dropWhile(final Collection<? extends T> sourceCollection,
                                              final Predicate<? super T> filterPredicate,
                                              final Supplier<Collection<T>> collectionFactory) {
        final Predicate<? super T> finalFilterPredicate =
                isNull(filterPredicate)
                        ? alwaysTrue()
                        : filterPredicate.negate();

        return takeWhile(
                sourceCollection,
                finalFilterPredicate,
                collectionFactory
        );
    }


    /**
     * Finds the first element of the given {@link Collection} satisfying the provided {@link Predicate}.
     *
     * <pre>
     * Example:
     *
     *   Parameters:             Result:
     *    [1, 2, 3, 6]            Optional(2)
     *    i -> i % 2 == 0
     * </pre>
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
        if (CollectionUtils.isEmpty(sourceCollection) || isNull(filterPredicate)) {
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
     * <pre>
     * Example:
     *
     *   Parameters:             Result:
     *    [1, 2, 3, 6]            Optional(6)
     *    i -> i % 2 == 0
     * </pre>
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
        if (CollectionUtils.isEmpty(sourceCollection) || isNull(filterPredicate)) {
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
     * <pre>
     * Example 1:
     *
     *   Parameters:              Result:
     *    [5, 7, 9]                315
     *    1
     *    (a, b) -> a * b
     * </pre>
     *
     * <pre>
     * Example 2:
     *
     *   Parameters:              Result:
     *    ["a", "h"]               "!ah"
     *    "!"
     *    (a, b) -> a + b
     * </pre>
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
                    if (nonNull(accumulator)) {
                        for (T element: sc) {
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
     * <pre>
     * Example 1:
     *
     *   Parameters:              Result:
     *    [5, 7, 9]                315
     *    1
     *    (a, b) -> a * b
     * </pre>
     *
     * <pre>
     * Example 2:
     *
     *   Parameters:              Result:
     *    ["a", "h"]               "!ha"
     *    "!"
     *    (a, b) -> a + b
     * </pre>
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
     * Returns a {@link List} with the elements included in the given {@link Iterator}.
     *
     * @param sourceIterator
     *    {@link Iterator} with the elements to add in the returned {@link List}
     *
     * @return {@link List}
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> fromIterator(final Iterator<? extends T> sourceIterator) {
        return (List<T>) fromIterator(
                sourceIterator,
                ArrayList::new
        );
    }


    /**
     * Returns a {@link Collection} with the elements included in the given {@link Iterator}.
     *
     * @param sourceIterator
     *    {@link Iterator} with the elements to add in the returned {@link List}
     * @param collectionFactory
     *   {@link Supplier} of the {@link Collection} used to store the returned elements
     *
     * @return {@link Collection}
     */
    public static <T> Collection<T> fromIterator(final Iterator<? extends T> sourceIterator,
                                                 final Supplier<Collection<T>> collectionFactory) {
        final Supplier<Collection<T>> finalCollectionFactory = getOrElse(
                collectionFactory,
                ArrayList::new
        );
        if (isNull(sourceIterator) || !sourceIterator.hasNext()) {
            return finalCollectionFactory.get();
        }
        return StreamSupport.stream(
                        Spliterators.spliteratorUnknownSize(
                                sourceIterator,
                                Spliterator.ORDERED
                        ),
                        false
                )
                .collect(
                        toCollection(finalCollectionFactory)
                );
    }


    /**
     *    Partitions given {@code sourceCollection} into a {@link Map} of {@link List} according to {@code discriminatorKey}.
     * Each element in a group is transformed into a value of type V using {@code valueMapper} {@link Function}.
     * <p>
     * It is equivalent to:
     * <p>
     *    Map<K, List<T>> groupedMap = sourceCollection.stream().collect(groupingBy(discriminatorKey))
     *    Map<K, List<V>> finalMap = mapValues(groupedMap, valueMapper)
     *
     * <pre>
     * Example:
     *
     *   Parameters:             Result:
     *    [1, 2, 3, 6]            [(0,  [4, 7])
     *    k -> i % 3               (1,  [2])
     *    i -> i + 1               (2,  [3])]
     * </pre>
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
    @SuppressWarnings("unchecked")
    public static <T, K, V> Map<K, List<V>> groupMap(final Collection<? extends T> sourceCollection,
                                                     final Function<? super T, ? extends K> discriminatorKey,
                                                     final Function<? super T, ? extends V> valueMapper) {
        return (Map) groupMap(
                sourceCollection,
                discriminatorKey,
                valueMapper,
                ArrayList::new
        );
    }


    /**
     *    Partitions given {@code sourceCollection} into a {@link Map} of {@link List} according to {@code discriminatorKey}.
     * Each element in a group is transformed into a value of type V using {@code valueMapper} {@link Function}.
     * <p>
     * It is equivalent to:
     * <p>
     *    Map<K, List<T>> groupedMap = sourceCollection.stream().collect(groupingBy(discriminatorKey))
     *    Map<K, List<V>> finalMap = mapValues(groupedMap, valueMapper)
     *
     * <pre>
     * Example:
     *
     *   Parameters:              Result:
     *    [1, 2, 3, 6]             [(0,  [4, 7])
     *    k -> i % 3                (1,  [2])
     *    i -> i + 1                (2,  [3])]
     *    ArrayList::new
     * </pre>
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
        final Supplier<Collection<V>> finalCollectionFactory = getOrElse(
                collectionFactory,
                ArrayList::new
        );
        Map<K, Collection<V>> result = new HashMap<>();
        sourceCollection.forEach(
                e -> {
                    K discriminatorKeyResult = discriminatorKey.apply(e);
                    result.putIfAbsent(
                            discriminatorKeyResult,
                            finalCollectionFactory.get()
                    );
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
     * <pre>
     * Example:
     *
     *   Parameters:              Intermediate Map:          Result:
     *    [1, 2, 3, 6]             [(0,  [4, 7])               [(0, 11), (1, 2), (2, 3)]
     *    k -> i % 3                (1,  [2])
     *    i -> i + 1                (2,  [3])]
     *    v -> v++
     * </pre>
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
     *                                  is {@code null}
     */
    public static <T, K, V> Map<K, V> groupMapReduce(final Collection<? extends T> sourceCollection,
                                                     final Function<? super T, ? extends K> discriminatorKey,
                                                     final Function<? super T, V> valueMapper,
                                                     final BinaryOperator<V> reduceValues) {
        Assert.notNull(discriminatorKey, "discriminatorKey must be not null");
        Assert.notNull(valueMapper, "valueMapper must be not null");
        Assert.notNull(reduceValues, "reduceValues must be not null");
        Map<K, V> result = new HashMap<>();
        groupMap(
                sourceCollection,
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
     *    Using {@code initialValue} as first element, apply {@code applyFunction} up to {@code untilPredicate} function
     * is {@code true}. The accumulated results are returned in a {@link List}.
     *
     * <pre>
     * Example 1:
     *
     *   Parameters:            Result:
     *    42                     []
     *    a -> a / 10
     *    a -> 50 >= a
     * </pre>
     *
     * <pre>
     * Example 2:
     *
     *   Parameters:            Result:
     *    42                     [42, 4]
     *    a -> a / 10
     *    a -> 0 >= a
     * </pre>
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
                .orElseGet(() ->
                        asList(initialValue)
                );
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
     * <pre>
     * Example 1:
     *
     *   Parameters:            Result:
     *    [5, 7, 9, 6]           [7, 9]
     *    1
     *    3
     * </pre>
     *
     * <pre>
     * Example 2:
     *
     *   Parameters:            Result:
     *    [a, b, c, d]           [d]
     *    3
     *    7
     * </pre>
     *
     * <pre>
     * Example 3:
     *
     *   Parameters:            Result:
     *    [a, b, c, d]           [a, b]
     *    -1
     *    2
     * </pre>
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
    @SuppressWarnings("unchecked")
    public static <T> List<T> slice(final Collection<? extends T> sourceCollection,
                                    final int from,
                                    final int until) {
        Assert.isTrue(
                from < until,
                format("from: %d must be lower than to: %d",
                        from, until
                )
        );
        if (CollectionUtils.isEmpty(sourceCollection) || from > sourceCollection.size() - 1) {
            return new ArrayList<>();
        }
        final int finalFrom = Math.max(0, from);
        final int finalUntil = Math.min(sourceCollection.size(), until);
        if (sourceCollection instanceof List) {
            return ((List<T>) sourceCollection).subList(
                    finalFrom,
                    finalUntil
            );
        }

        int i = 0;
        List<T> result = new ArrayList<>(
                Math.max(
                        finalUntil - finalFrom,
                        finalUntil - finalFrom - 1
                )
        );
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
     * <pre>
     * Example 1:
     *
     *   Parameters:            Result:
     *    [1, 2]                 [[1, 2]]
     *    5
     * </pre>
     *
     * <pre>
     * Example 2:
     *
     *   Parameters:            Result:
     *    [7, 8, 9]              [[7, 8], [8, 9]]
     *    2
     * </pre>
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
        if (CollectionUtils.isEmpty(sourceCollection) || 0 == size) {
            return new ArrayList<>();
        }
        final List<T> listToSlide = new ArrayList<>(
                getCollectionKeepingInternalOrdination(sourceCollection)
        );
        if (size >= listToSlide.size()) {
            return asList(listToSlide);
        }
        return IntStream.range(0, listToSlide.size() - size + 1)
                .mapToObj(start ->
                        listToSlide.subList(
                                start,
                                start + size
                        )
                )
                .collect(toList());
    }


    /**
     * Splits the given {@link Collection} in sublists with a size equal to the given {@code size}
     *
     * <pre>
     * Example 1:
     *
     *   Parameters:             Result:
     *    [1, 2, 3, 4]            [[1, 2], [3, 4]]
     *    2
     * </pre>
     *
     * <pre>
     * Example 2:
     *
     *   Parameters:             Result:
     *    [1, 2, 3, 4]            [[1, 2, 3], [4]]
     *    3
     * </pre>
     *
     * <pre>
     * Example 3:
     *
     *   Parameters:             Result:
     *    [1, 2, 3, 4]            [[1, 2, 3, 4]]
     *    5
     * </pre>
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
        if (CollectionUtils.isEmpty(sourceCollection) || 0 == size) {
            return new ArrayList<>();
        }
        final List<T> listToSplit = new ArrayList<>(
                getCollectionKeepingInternalOrdination(sourceCollection)
        );
        final int expectedSize = 0 == listToSplit.size() % size
                ? listToSplit.size() / size
                : (listToSplit.size() / size) + 1;

        List<List<T>> splits = new ArrayList<>(expectedSize);
        for (int i = 0; i < listToSplit.size(); i += size) {
            splits.add(
                    new ArrayList<>(
                            listToSplit.subList(
                                    i,
                                    Math.min(
                                            listToSplit.size(),
                                            i + size
                                    )
                            )
                    )
            );
        }
        return splits;
    }


    /**
     *    Returns a {@link List} with the elements of provided {@code sourceCollection} that satisfy the {@link Predicate}
     * {@code filterPredicate}.
     *
     * <pre>
     * Example:
     *
     *   Parameters:             Result:
     *    [1, 2, 3, 6]            [1, 3]
     *    i -> i % 2 == 1
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to filter
     * @param filterPredicate
     *    {@link Predicate} to filter elements from {@code sourceCollection}
     *
     * @return {@link List}
     */
    public static <T> List<T> takeWhile(final Collection<? extends T> sourceCollection,
                                        final Predicate<? super T> filterPredicate) {
        return (List<T>) takeWhile(
                sourceCollection,
                filterPredicate,
                ArrayList::new
        );
    }


    /**
     *    Returns a {@link Collection} with the elements of provided {@code sourceCollection} that satisfy the {@link Predicate}
     * {@code filterPredicate}.
     *
     * <pre>
     * Example:
     *
     *   Parameters:             Result:
     *    [1, 2, 3, 6]            [1, 3]
     *    i -> i % 2 == 1
     *    ArrayList::new
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to filter
     * @param filterPredicate
     *    {@link Predicate} to filter elements from {@code sourceCollection}
     * @param collectionFactory
     *   {@link Supplier} of the {@link Collection} used to store the returned elements.
     *
     * @return {@link Collection}
     */
    public static <T> Collection<T> takeWhile(final Collection<? extends T> sourceCollection,
                                              final Predicate<? super T> filterPredicate,
                                              final Supplier<Collection<T>> collectionFactory) {
        final Supplier<Collection<T>> finalCollectionFactory = getOrElse(
                collectionFactory,
                ArrayList::new
        );
        if (CollectionUtils.isEmpty(sourceCollection)) {
            return finalCollectionFactory.get();
        }
        final Predicate<? super T> finalFilterPredicate = getOrElse(
                filterPredicate,
                alwaysTrue()
        );
        return sourceCollection
                .stream()
                .filter(finalFilterPredicate)
                .collect(
                        toCollection(finalCollectionFactory)
                );
    }


    /**
     * Transposes the rows and columns of the given {@code sourceCollection}.
     *
     * <pre>
     * Example 1:
     *
     *   Parameters:                                    Result:
     *    [[1, 2, 3], [4, 5, 6]]                         [[1, 4], [2, 5], [3, 6]]
     * </pre>
     *
     * <pre>
     * Example 2:
     *
     *   Parameters:                                    Result:
     *    [["a1", "a2"], ["b1", "b2], ["c1", "c2"]]      [["a1", "b1", "c1"], ["a2", "b2", "c2"]]
     * </pre>
     *
     * <pre>
     * Example 3:
     *
     *   Parameters:                                    Result:
     *    [[1, 2], [0], [7, 8, 9]]                       [[1, 0, 7], [2, 8], [9]]
     * </pre>
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
        final List<Iterator<T>> iteratorList = new ArrayList<>(sourceCollection.size());
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
     * <pre>
     * Example:
     *
     *   Parameters:                           Result:
     *    [("d", 6), ("h", 7), ("y", 11)]       [("d", "h", "y"), (6, 7, 11)]
     * </pre>
     *
     * @param sourceCollection
     *    {@link Collection} of {@link Tuple2} to split its elements
     *
     * @return {@link Tuple2} of two {@link List}
     */
    public static <T, E> Tuple2<List<T>, List<E>> unzip(final Collection<Tuple2<T, E>> sourceCollection) {
        return foldLeft(
                sourceCollection,
                Tuple.of(
                        new ArrayList<>(),
                        new ArrayList<>()
                ),
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
     * <pre>
     * Example 1:
     *
     *   Parameters:              Result:
     *    ["d", "h", "y"]          [("d", 6), ("h", 7), ("y", 11)]
     *    [6, 7, 11]
     * </pre>
     *
     * <pre>
     * Example 2:
     *
     *   Parameters:              Result:
     *    [4, 9, 14]               [(4, 23), (9, 8)]
     *    [23, 8]
     * </pre>
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
        final int minCollectionsSize = Math.min(
                sourceLeftCollection.size(),
                sourceRightCollection.size()
        );

        final Iterator<? extends T> leftIterator = sourceLeftCollection.iterator();
        final Iterator<? extends E> rightIterator = sourceRightCollection.iterator();
        List<Tuple2<T, E>> result = new ArrayList<>();
        for (int i = 0; i < minCollectionsSize; i++) {
            result.add(
                    Tuple.of(
                            leftIterator.next(),
                            rightIterator.next()
                    )
            );
        }
        return result;
    }


    /**
     *    Returns a {@link List} formed from {@code sourceLeftCollection} and {@code sourceRightCollection}
     * by combining corresponding elements in {@link Tuple2}. If one of the two collections is shorter than
     * the other, placeholder elements are used to extend the shorter collection to the length of the longer.
     *
     * <pre>
     * Example 1:
     *
     *   Parameters:              Result:
     *    ["d", "h", "y"]          [("d", 6), ("h", 7), ("y", 11)]
     *    [6, 7, 11]
     *    "z"
     *    55
     * </pre>
     *
     * <pre>
     * Example 2:
     *
     *   Parameters:              Result:
     *    [4, 9, 14]               [(4, 23), (9, 8), (14, 10)]
     *    [23, 8]
     *    17
     *    10
     * </pre>
     *
     * <pre>
     * Example 3:
     *
     *   Parameters:              Result:
     *    [4, 9]                   [(4, "f"), (9, "g"), (11, "m")]
     *    ["f", "g", "m"]
     *    11
     *    "u"
     * </pre>
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
        final int maxCollectionSize = Math.max(
                CollectionUtils.isEmpty(sourceLeftCollection)
                        ? 0
                        : sourceLeftCollection.size(),
                CollectionUtils.isEmpty(sourceRightCollection)
                        ? 0
                        : sourceRightCollection.size()
        );
        final Iterator<T> leftIterator = ofNullable(sourceLeftCollection)
                .map(Collection::iterator)
                .orElse(null);
        final Iterator<E> rightIterator = ofNullable(sourceRightCollection)
                .map(Collection::iterator)
                .orElse(null);

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
     * <pre>
     * Example:
     *
     *   Parameters:              Result:
     *    ["d", "h", "y"]          [(0, "d"), (1, "h"), (2, "y")]
     * </pre>
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
            result.add(
                    Tuple.of(
                            i,
                            element
                    )
            );
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
            final PriorityQueue<T> cloneQueue = new PriorityQueue<>(sourceCollection);
            List<T> result = new ArrayList<>(sourceCollection.size());
            for (int i = 0; i < sourceCollection.size(); i++) {
                result.add(cloneQueue.poll());
            }
            return result;
        } else {
            return sourceCollection;
        }
    }

}
