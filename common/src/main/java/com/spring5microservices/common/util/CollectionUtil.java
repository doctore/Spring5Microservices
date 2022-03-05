package com.spring5microservices.common.util;

import com.spring5microservices.common.dto.PairDto;
import lombok.experimental.UtilityClass;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Arrays.asList;
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
     *   [1, 2, 3, 6],  i -> i % 2 == 1,  i -> i + 1,  i -> i * 2  =>  [2, 4, 4, 12]
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to filter and transform.
     * @param filterPredicate
     *    {@link Predicate} to filter elements from the source {@code sourceCollection}.
     * @param defaultFunction
     *    {@link Function} to transform elements of {@code sourceCollection} that verify {@code filterPredicate}.
     * @param orElseFunction
     *    {@link Function} to transform elements of {@code sourceCollection} do not verify {@code filterPredicate}.
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
        return (List<E>)applyOrElse(sourceCollection, filterPredicate, defaultFunction, orElseFunction, ArrayList::new);
    }


    /**
     *    In the given {@code sourceCollection}, applies {@code defaultFunction} if the current element verifies
     * {@code filterPredicate}, otherwise applies {@code orElseFunction}.
     *
     * Example:
     *   [1, 2, 3, 6],  i -> i % 2 == 1,  i -> i + 1,  i -> i * 2,  ArrayList::new  =>  [2, 4, 4, 12]
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to filter and transform.
     * @param filterPredicate
     *    {@link Predicate} to filter elements from the source {@code sourceCollection}.
     * @param defaultFunction
     *    {@link Function} to transform elements of {@code sourceCollection} that verify {@code filterPredicate}.
     * @param orElseFunction
     *    {@link Function} to transform elements of {@code sourceCollection} do not verify {@code filterPredicate}.
     * @param collectionFactory
     *    {@link Supplier} of the {@link Collection} used to store the returned elements.
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
        if (CollectionUtils.isEmpty(sourceCollection)) {
            return new ArrayList<>();
        }
        Supplier<Collection<E>> definitiveCollectionFactory =
                null == collectionFactory
                        ? ArrayList::new
                        : collectionFactory;

        return sourceCollection.stream()
                .map(elto ->
                        filterPredicate.test(elto)
                                ? defaultFunction.apply(elto)
                                : orElseFunction.apply(elto)
                )
                .collect(toCollection(definitiveCollectionFactory));
    }


    /**
     * Return a {@link LinkedHashSet} with the provided {@code elements}.
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
     * Return a {@link Collection} after:
     *
     *  - Filter its elements using {@code filterPredicate}
     *  - Transform its filtered elements using {@code mapFunction}
     *
     * Example:
     *   [1, 2, 3, 6],  i -> i % 2 == 1,  i -> i.toString()  =>  ["1", "3"]
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to filter and transform.
     * @param filterPredicate
     *    {@link Predicate} to filter elements from the source {@code sourceCollection}.
     * @param mapFunction
     *    {@link Function} to transform filtered elements from the source {@code sourceCollection}.
     *
     * @return {@link List}
     *
     * @throws IllegalArgumentException if {@code filterPredicate} or {@code mapFunction} is {@code null}
     */
    public static <T, E> List<E> collect(final Collection<? extends T> sourceCollection,
                                         final Predicate<? super T> filterPredicate,
                                         final Function<? super T, ? extends E> mapFunction) {
        return (List<E>)collect(sourceCollection, filterPredicate, mapFunction, ArrayList::new);
    }


    /**
     * Return a {@link Collection} after:
     *
     *  - Filter its elements using {@code filterPredicate}
     *  - Transform its filtered elements using {@code mapFunction}
     *
     * Example:
     *   [1, 2, 3, 6],  i -> i % 2 == 1,  i -> i.toString(),  ArrayList::new  =>  ["1", "3"]
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to filter and transform.
     * @param filterPredicate
     *    {@link Predicate} to filter elements from the source {@code sourceCollection}.
     * @param mapFunction
     *    {@link Function} to transform filtered elements from the source {@code sourceCollection}.
     * @param collectionFactory
     *    {@link Supplier} of the {@link Collection} used to store the returned elements.
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
        if (CollectionUtils.isEmpty(sourceCollection)) {
            return new ArrayList<>();
        }
        Supplier<Collection<E>> definitiveCollectionFactory =
                null == collectionFactory
                        ? ArrayList::new
                        : collectionFactory;

        return sourceCollection
                .stream()
                .filter(filterPredicate)
                .map(mapFunction)
                .collect(toCollection(definitiveCollectionFactory));
    }


    /**
     * Return a {@link Collection} with the extracted property of the given {@code sourceCollection}
     *
     * @param sourceCollection
     *    Source {@link Collection} with the property to extract.
     * @param keyExtractor
     *    {@link Function} used to get the key we want to use to include in returned {@link Collection}.
     *
     * @return {@link List}
     */
    public static <T, E> List<E> collectProperty(final Collection<? extends T> sourceCollection,
                                                 final Function<? super T, ? extends E> keyExtractor) {
        return (List<E>)collectProperty(sourceCollection, keyExtractor, ArrayList::new);
    }


    /**
     * Return a {@link Collection} with the extracted property of the given {@code sourceCollection}
     *
     * @param sourceCollection
     *    Source {@link Collection} with the property to extract.
     * @param keyExtractor
     *    {@link Function} used to get the key we want to use to include in returned {@link Collection}.
     * @param collectionFactory
     *    {@link Supplier} of the {@link Collection} used to store the returned elements.
     *
     * @return {@link Collection}
     */
    public static <T, E> Collection<E> collectProperty(final Collection<? extends T> sourceCollection,
                                                       final Function<? super T, ? extends E> keyExtractor,
                                                       final Supplier<Collection<E>> collectionFactory) {
        return ofNullable(sourceCollection)
                .map(c -> {
                    if (null == keyExtractor) {
                        return null;
                    }
                    Stream<E> keyExtractedStream = sourceCollection.stream().map(keyExtractor);
                    return null == collectionFactory
                            ? keyExtractedStream.collect(toList())
                            : keyExtractedStream.collect(toCollection(collectionFactory));
                })
                .orElseGet(() ->
                        null == collectionFactory
                                ? new ArrayList<>()
                                : collectionFactory.get());
    }


    /**
     * Return the unique elements of the given {@link Collection}s.
     *
     * @param collections
     *    {@link Collection}s to concat.
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
     * Finds the first element of the given {@link Collection} satisfying the provided {@link Predicate}.
     *
     * @param sourceCollection
     *    {@link Collection} to search
     * @param filterPredicate
     *    {@link Predicate} used to test elements of {@code sourceCollection}
     *
     * @return {@link Optional} containing the first element that satisfies {@code filterPredicate},
     *         {@link Optional#empty()} otherwise.
     */
    public static <T> Optional<? extends T> find(final Collection<? extends T> sourceCollection,
                                                 final Predicate<? super T> filterPredicate) {
        if (CollectionUtils.isEmpty(sourceCollection) ||
                Objects.isNull(filterPredicate)) {
            return empty();
        }
        return sourceCollection
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
     *    {@link Predicate} used to test elements of {@code sourceCollection}
     *
     * @return {@link Optional} containing the last element that satisfies {@code filterPredicate},
     *         {@link Optional#empty()} otherwise.
     */
    public static <T> Optional<? extends T> findLast(final Collection<? extends T> sourceCollection,
                                                     final Predicate<? super T> filterPredicate) {
        if (CollectionUtils.isEmpty(sourceCollection) ||
                Objects.isNull(filterPredicate)) {
            return empty();
        }
        return sourceCollection
                .stream()
                .filter(filterPredicate)
                .reduce((previous, current) -> current);
    }


    /**
     *    Folds given {@link Collection} elements from the left, starting with {@code initialValue} and successively
     * calling {@code accumulator}.
     *
     * Examples:
     *   [5, 7, 9],     1,  (a, b) -> a * b   => 315
     *   ["a", "h"],  "!",  (a, b) -> a + b   => "!ah"
     *
     * @param sourceCollection
     *    {@link Collection} with elements to combine.
     * @param initialValue
     *    The initial value to start with.
     * @param accumulator
     *    A {@link BiFunction} which combines elements.
     *
     * @return a folded value
     *
     * @throws IllegalArgumentException if {@code initialValue} is {@code null}
     */
    public static <T, E> E foldLeft(final Collection<? extends T> sourceCollection,
                                    final E initialValue,
                                    final BiFunction<E, ? super T, E> accumulator) {
        Assert.notNull(initialValue, "initialValue must be not null");
        return ofNullable(sourceCollection)
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
     *    Using {@code initialValue} as first element, apply {@code applyFunction} up to {@code untilPredicate} function
     * is {@code true}. The accumulated results are returned in a {@link List}.
     *
     * Examples:
     *    42,  a -> a / 10,  a -> 50 >= a  =>  []
     *    42,  a -> a / 10,  a -> 0 >= a   =>  [42, 4]
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
                .map(sc -> {
                    if (sc instanceof List) {
                        return (List<T>) sc;
                    }
                    if (sc instanceof PriorityQueue) {
                        PriorityQueue<T> cloneQueue = new PriorityQueue<>(sc);
                        List<T> result = new ArrayList<>(sc.size());
                        for (int i = 0; i < sc.size(); i++) {
                            result.add(cloneQueue.poll());
                        }
                        return result;
                    }
                    return new ArrayList<T>(sc);
                })
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
     * Examples:
     *    [5, 7, 9, 6],   1,  3  =>  [7, 9]
     *    [a, b, c, d],   3,  7  =>  [d]
     *    [a, b, c, d],  -1,  2  =>  [a, b]
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
        for (T element: sourceCollection) {
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
     * Examples:
     *   [1, 2]    with size = 5  =>  [[1, 2]]
     *   [7, 8, 9] with size = 2  =>  [[7, 8], [8, 9]]
     *
     * @param sourceCollection
     *    {@link Collection} to slide
     * @param size
     *    Size of every sublist
     *
     * @return {@link List} of {@link List}s
     */
    public static <T> List<List<T>> sliding(final Collection<? extends T> sourceCollection,
                                            final int size) {
        if (CollectionUtils.isEmpty(sourceCollection)) {
            return new ArrayList<>();
        }
        List<T> listToSlide = new ArrayList<>(sourceCollection);
        if (size > listToSlide.size()) {
            return asList(listToSlide);
        }
        return IntStream.range(0, listToSlide.size() - size + 1)
                .mapToObj(start -> listToSlide.subList(start, start + size))
                .collect(toList());
    }


    /**
     * Splits the given {@link Collection} in sublists with a size equal to the given {@code size}
     *
     * Examples:
     *   [1, 2, 3, 4] with size = 2  =>  [[1, 2], [3, 4]]
     *   [1, 2, 3, 4] with size = 3  =>  [[1, 2, 3], [4]]
     *   [1, 2, 3, 4] with size = 5  =>  [[1, 2, 3, 4]]
     *
     * @param sourceCollection
     *    {@link Collection} to split
     * @param size
     *    Size of every sublist
     *
     * @return {@link List} of {@link List}s
     */
    public static <T> List<List<T>> split(final Collection<? extends T> sourceCollection,
                                          final int size) {
        if (CollectionUtils.isEmpty(sourceCollection)) {
            return new ArrayList<>();
        }
        List<T> listToSplit = new ArrayList<>(sourceCollection);
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
     * Examples:
     *   [[1, 2, 3], [4, 5, 6]]                     =>  [[1, 4], [2, 5], [3, 6]]
     *   [["a1", "a2"], ["b1", "b2], ["c1", "c2"]]  =>  [["a1", "b1", "c1"], ["a2", "b2", "c2"]]
     *   [[1, 2], [0], [7, 8, 9]]                   =>  [[1, 0, 7], [2, 8], [9]]
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
     *    Converts given {@code sourceCollection} of {@link PairDto} into two {@link List} of the first and
     * second half of each pair.
     *
     * Example:
     *    [("d", 6), ("h", 7), ("y", 11)]  =>  [("d", "h", "y"), (6, 7, 11)]
     *
     * @param sourceCollection
     *    {@link Collection} of {@link PairDto} to split its elements
     *
     * @return {@link PairDto} of two {@link List}
     */
    public static <T, E> PairDto<List<T>, List<E>> unzip(final Collection<PairDto<T, E>> sourceCollection) {
        return foldLeft(
                sourceCollection,
                PairDto.of(new ArrayList<>(), new ArrayList<>()),
                (pairOfLists, currentElto) -> {
                    pairOfLists.getFirst().add(currentElto.getFirst());
                    pairOfLists.getSecond().add(currentElto.getSecond());
                    return pairOfLists;
                }
        );
    }


    /**
     *    Returns a {@link List} formed from {@code sourceLeftCollection} and {@code sourceRightCollection}
     * by combining corresponding elements in {@link PairDto}. If one of the two collections is longer than
     * the other, its remaining elements are ignored.
     *
     * Examples:
     *   ["d", "h", "y"],  [6, 7, 11]  =>  [("d", 6), ("h", 7), ("y", 11)]
     *   [4, 9, 14],       [23, 8]     =>  [(4, 23), (9, 8)]
     *
     * @param sourceLeftCollection
     *    {@link Collection} with elements to be included as left side of returned {@link PairDto}
     * @param sourceRightCollection
     *    {@link Collection} with elements to be included as right side of returned {@link PairDto}
     *
     * @return {@link List} of {@link PairDto}
     */
    public static <T, E> List<PairDto<T, E>> zip(final Collection<? extends T> sourceLeftCollection,
                                                 final Collection<? extends E> sourceRightCollection) {
        if (CollectionUtils.isEmpty(sourceLeftCollection) ||
                CollectionUtils.isEmpty(sourceRightCollection)) {
            return new ArrayList<>();
        }
        int minCollectionsSize = Math.min(sourceLeftCollection.size(), sourceRightCollection.size());

        Iterator<? extends T> leftIterator = sourceLeftCollection.iterator();
        Iterator<? extends E> rightIterator = sourceRightCollection.iterator();
        List<PairDto<T, E>> result = new ArrayList<>();
        for (int i = 0; i < minCollectionsSize; i++) {
            result.add(
                    PairDto.of(leftIterator.next(), rightIterator.next())
            );
        }
        return result;
    }


    /**
     *    Returns a {@link List} formed from {@code sourceLeftCollection} and {@code sourceRightCollection}
     * by combining corresponding elements in {@link PairDto}. If one of the two collections is shorter than
     * the other, placeholder elements are used to extend the shorter collection to the length of the longer.
     *
     * Examples:
     *   ["d", "h", "y"],  [6, 7, 11],      "z",   55  =>  [("d", 6), ("h", 7), ("y", 11)]
     *   [4, 9, 14],       [23, 8],          17,   10  =>  [(4, 23), (9, 8), (14, 10)]
     *   [4, 9],           ["f", "g", "m"],  11,  "u"  =>  [(4, "f"), (9, "g"), (11, "m")]
     *
     * @param sourceLeftCollection
     *    {@link Collection} with elements to be included as left side of returned {@link PairDto}
     * @param sourceRightCollection
     *    {@link Collection} with elements to be included as right side of returned {@link PairDto}
     * @param defaultLeftElement
     *    Element to be used to fill up the result if {@code sourceLeftCollection} is shorter than {@code sourceRightCollection}
     * @param defaultRightElement
     *    Element to be used to fill up the result if {@code sourceRightCollection} is shorter than {@code sourceLeftCollection}
     *
     * @return {@link List} of {@link PairDto}
     */
    public static <T, E> List<PairDto<T, E>> zipAll(final Collection<T> sourceLeftCollection,
                                                    final Collection<E> sourceRightCollection,
                                                    final T defaultLeftElement,
                                                    final E defaultRightElement) {
        int maxCollectionSize = Math.max(
                CollectionUtils.isEmpty(sourceLeftCollection) ? 0 : sourceLeftCollection.size(),
                CollectionUtils.isEmpty(sourceRightCollection) ? 0 : sourceRightCollection.size()
        );
        Iterator<T> leftIterator = ofNullable(sourceLeftCollection).map(Collection::iterator).orElse(null);
        Iterator<E> rightIterator = ofNullable(sourceRightCollection).map(Collection::iterator).orElse(null);
        List<PairDto<T, E>> result = new ArrayList<>();

        for (int i = 0; i < maxCollectionSize; i++) {
            result.add(
                    PairDto.of(
                            ofNullable(leftIterator)
                                    .filter(Iterator::hasNext)
                                    .map(Iterator::next)
                                    .orElseGet(() -> defaultLeftElement),
                            ofNullable(rightIterator)
                                    .filter(Iterator::hasNext)
                                    .map(Iterator::next)
                                    .orElseGet(() -> defaultRightElement)
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
     *   ["d", "h", "y"]  =>  [(0, "d"), (1, "h"), (2, "y")]
     *
     * @param sourceCollection
     *    {@link Collection} to extract: index and element
     *
     * @return {@link List} of {@link PairDto}s
     */
    public static <T> List<PairDto<Integer, T>> zipWithIndex(Collection<? extends T> sourceCollection) {
        if (CollectionUtils.isEmpty(sourceCollection)) {
            return new ArrayList<>();
        }
        int i = 0;
        List<PairDto<Integer, T>> result = new ArrayList<>(sourceCollection.size());
        for (T element: sourceCollection) {
            result.add(PairDto.of(i, element));
            i++;
        }
        return result;
    }

}
