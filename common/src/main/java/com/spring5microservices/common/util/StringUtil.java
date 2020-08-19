package com.spring5microservices.common.util;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

@UtilityClass
public class StringUtil {

    private final String DEFAULT_STRING_SEPARATOR = ",";


    /**
     *    Return a {@link List} splitting the given {@code source} in different parts, using {@code valueExtractor}
     * to know how to do it.
     *
     * @param source
     *    Source {@link String} with the values to extract
     * @param valueExtractor
     *    {@link Function} used to know how to split the provided {@link String}
     *
     * @return {@link List}
     */
    public static <T> List<T> splitFromString(final String source, Function<String, T> valueExtractor) {
        return (List)splitFromString(source, valueExtractor, DEFAULT_STRING_SEPARATOR, ArrayList::new);
    }


    /**
     *    Return a {@link List} splitting the given {@code source} in different parts, using {@code valueExtractor}
     * to know how to do it.
     *
     * @param source
     *    Source {@link String} with the values to extract
     * @param valueExtractor
     *    {@link Function} used to know how to split the provided {@link String}
     * @param separator
     *    {@link String} used to know how the values are splitted inside {@code source}
     *
     * @return {@link List}
     */
    public static <T> List<T> splitFromString(final String source, Function<String, T> valueExtractor, final String separator) {
        return (List)splitFromString(source, valueExtractor, separator, ArrayList::new);
    }


    /**
     *    Return a {@link Collection} splitting the given {@code source} in different parts, using {@code valueExtractor}
     * to know how to do it.
     *
     * @param source
     *    Source {@link String} with the values to extract
     * @param valueExtractor
     *    {@link Function} used to know how to split the provided {@link String}
     * @param separator
     *    {@link String} used to know how the values are splitted inside {@code source}
     * @param collectionFactory
     *    {@link Supplier} of the {@link Collection} used to store the returned elements.
     *
     * @return {@link Collection}
     */
    public static <T> Collection<T> splitFromString(final String source, Function<String, T> valueExtractor, final String separator,
                                                    Supplier<Collection<T>> collectionFactory) {
        return ofNullable(source)
                .map(s -> {
                    if (null == valueExtractor) {
                        return null;
                    }
                    Stream<T> valueExtractedStream = Stream.of(s.split(null == separator ? DEFAULT_STRING_SEPARATOR : separator))
                            .map(valueExtractor);
                    return null == collectionFactory
                            ? valueExtractedStream.collect(toList())
                            : valueExtractedStream.collect(toCollection(collectionFactory));
                })
                .orElse(null == collectionFactory ? asList() : collectionFactory.get());
    }

}
