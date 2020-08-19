package com.spring5microservices.common.util;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
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
    public static <T> Set<T> concatUniqueElements(final Collection<T>... collections) {
        return ofNullable(collections)
                .map(c -> Stream.of(c).filter(Objects::nonNull)
                                      .flatMap(Collection::stream)
                                      .collect(toCollection(LinkedHashSet::new)))
                .orElse(new LinkedHashSet<>());
    }

}
