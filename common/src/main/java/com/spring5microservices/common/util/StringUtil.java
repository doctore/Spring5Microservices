package com.spring5microservices.common.util;

import lombok.experimental.UtilityClass;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

@UtilityClass
public class StringUtil {

    private final String DEFAULT_MIDDLE_STRING_ABBREVIATION = "...";
    private final String DEFAULT_STRING_SEPARATOR = ",";
    private final Function<String, String> DEFAULT_STRING_EXTRACTOR = String::trim;


    /**
     *    Abbreviates the given {@code sourceString} to the chunk size provided {@code sizeOfEveryChunk}, replacing the middle
     *  characters with the supplied replacement string {@code putInTheMiddle}.
     *
     * @param sourceString
     *    {@link String} to abbreviate
     * @param putInTheMiddle
     *    {@link String} to replace the middle characters. Default value will be "..."
     * @param sizeOfEveryChunk
     *    Size of visible parts on every side
     *
     * @return {@link Optional} with the abbreviated {@link String}
     *      or {@link Optional#empty()} if given {@code sourceString} is {@code null}
     *
     * @throws IllegalArgumentException if {@code sizeOfEveryChunk} is lower to 1
     */
    public static Optional<String> abbreviateMiddle(final String sourceString,
                                                    final String putInTheMiddle,
                                                    final int sizeOfEveryChunk) {
        Assert.isTrue(0 < sizeOfEveryChunk, "sizeOfEveryChunk must be a positive value");
        return ofNullable(sourceString)
                .map(s -> {
                    String finalPutInTheMiddle = Objects.isNull(putInTheMiddle) || putInTheMiddle.trim().isEmpty()
                            ? DEFAULT_MIDDLE_STRING_ABBREVIATION
                            : putInTheMiddle;

                    if (sourceString.length() > (2 * sizeOfEveryChunk)) {
                        int endPos = sourceString.length() - sizeOfEveryChunk;

                        return sourceString.substring(0, sizeOfEveryChunk)
                                + finalPutInTheMiddle
                                + sourceString.substring(endPos);
                    }
                    return sourceString;
                });
    }


    /**
     * Verifies if the given {@code sourceString} contains {@code stringToSearch} ignoring case.
     *
     * @param sourceString
     *    {@link String} to check if contains {@code stringToSearch}
     * @param stringToSearch
     *    {@link String} to search in {@code sourceString}
     *
     * @return {@code true} if {@code sourceString} contains {@code stringToSearch}, {@code false} otherwise.
     */
    public static boolean containsIgnoreCase(final String sourceString,
                                             final String stringToSearch) {
        if (Objects.isNull(sourceString) || Objects.isNull(stringToSearch)) {
            return false;
        }
        return sourceString.toLowerCase().contains(stringToSearch.toLowerCase());
    }


    /**
     * Removes from the given {@code sourceString} all non-numeric characters.
     *
     * @param sourceString
     *    {@link String} to delete all non-numeric characters.
     *
     * @return {@code sourceString} without non-numeric characters.
     */
    public static Optional<String> keepOnlyDigits(final String sourceString) {
        return ofNullable(sourceString)
                .map(s -> s.replaceAll("[\\D]", ""));
    }


    /**
     * Loops through the provided {@link String} one position every time, returning sublists with {@code size}
     *
     * Example 1:
     *
     *   Parameters:              Result:
     *    "12"                     ["12"]
     *    5
     *
     * Example 2:
     *
     *   Parameters:              Result:
     *    "789"                    ["78", "89"]
     *    2
     *
     * @param sourceString
     *    {@link String} to slide
     * @param size
     *    Size of every sublist
     *
     * @return {@link List} of {@link String}
     */
    public static List<String> sliding(final String sourceString,
                                       final int size) {
        if (Objects.isNull(sourceString)) {
            return new ArrayList<>();
        }
        if (1 > size ||
                size > sourceString.length()) {
            return asList(sourceString);
        }
        List<String> parts = new ArrayList<>();
        for (int i = 0; i < sourceString.length() - size + 1; i++) {
            parts.add(sourceString.substring(i, i + size));
        }
        return parts;
    }


    /**
     * Splits the given {@link String} in substrings with a size equal to the given {@code size}
     *
     * Example 1:
     *
     *   Parameters:              Result:
     *    "123"                    ["123"]
     *    4
     *
     * Example 2:
     *
     *   Parameters:              Result:
     *    "123"                    ["12", "3"]
     *    2
     *
     * @param sourceString
     *    {@link String} to split
     * @param size
     *    Size of every substring
     *
     * @return {@link List} of {@link String}s
     */
    public static List<String> splitBySize(final String sourceString,
                                           final int size) {
        if (Objects.isNull(sourceString)) {
            return new ArrayList<>();
        }
        if (1 > size ||
                size > sourceString.length()) {
            return asList(sourceString);
        }
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < sourceString.length(); i += size) {
            result.add(
                    sourceString.substring(
                            i,
                            Math.min(sourceString.length(), i + size)
                    )
            );
        }
        return result;
    }


    /**
     *    Returns a {@link List} splitting the given {@code source} in different parts, using {@code valueExtractor}
     * to know how to do it.
     *
     * @param source
     *    Source {@link String} with the values to extract
     * @param valueExtractor
     *    {@link Function} used to know how to split the provided {@link String}
     *
     * @return {@link List}
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> splitFromString(final String source,
                                              final Function<String, ? extends T> valueExtractor) {
        return (List<T>)splitFromString(source, valueExtractor, DEFAULT_STRING_SEPARATOR, ArrayList::new);
    }


    /**
     *    Returns a {@link Collection} splitting the given {@code source} in different parts, using {@code separator}
     * to know how to split it.
     *
     * @param source
     *    Source {@link String} with the values to extract
     * @param separator
     *    {@link String} used to know how the values are splitted inside {@code source}
     * @param chunkLimit
     *    Maximum number of elements of which the given {@code source} will be splitted
     *
     * @return {@link List}
     */
    public static <T> List<T> splitFromString(final String source,
                                              final String separator,
                                              final int chunkLimit) {
        return (List<T>)splitFromString(source, separator, chunkLimit, DEFAULT_STRING_EXTRACTOR, ArrayList::new);
    }


    /**
     *    Returns a {@link List} splitting the given {@code source} in different parts, using {@code valueExtractor}
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
    @SuppressWarnings("unchecked")
    public static <T> List<T> splitFromString(final String source,
                                              final Function<String, ? extends T> valueExtractor,
                                              final String separator) {
        return (List<T>)splitFromString(source, valueExtractor, separator, ArrayList::new);
    }


    /**
     *    Returns a {@link Collection} splitting the given {@code source} in different parts, using {@code valueExtractor}
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
    public static <T> Collection<T> splitFromString(final String source,
                                                    final Function<String, ? extends T> valueExtractor,
                                                    final String separator,
                                                    final Supplier<Collection<T>> collectionFactory) {
        return splitFromString(source, separator, -1, valueExtractor, collectionFactory);
    }


    /**
     *    Returns a {@link Collection} splitting the given {@code source} in different parts, using {@code valueExtractor}
     * to know how to do it.
     *
     * @param source
     *    Source {@link String} with the values to extract
     * @param separator
     *    {@link String} used to know how the values are splitted inside {@code source}
     * @param chunkLimit
     *    Maximum number of elements of which the given {@code source} will be splitted
     * @param valueExtractor
     *    {@link Function} used to know how to split the provided {@link String}
     * @param collectionFactory
     *    {@link Supplier} of the {@link Collection} used to store the returned elements.
     *
     * @return {@link Collection}
     *
     * @throws Exception if there is an splitted part that cannot extracted using {@code valueExtractor}.
     *
     *                   For example:
     *                        source = "1,2,3"
     *                        chunkLimit = 2
     *                        valueExtractor = Integer::parseInt
     *
     *                   Splitted parts will be:
     *                        ["1", "2,3"] => second one could not be converted to an {@link Integer}
     */
    public static <T> Collection<T> splitFromString(final String source,
                                                    final String separator,
                                                    final int chunkLimit,
                                                    final Function<String, ? extends T> valueExtractor,
                                                    final Supplier<Collection<T>> collectionFactory) {
        return ofNullable(source)
                .map(s -> {
                    if (Objects.isNull(valueExtractor)) {
                        return null;
                    }
                    String[] splittedString =
                            0 >= chunkLimit
                                    ? s.split(null == separator ? DEFAULT_STRING_SEPARATOR : separator)
                                    : s.split(null == separator ? DEFAULT_STRING_SEPARATOR : separator, chunkLimit);

                    Stream<T> valueExtractedStream = Stream.of(splittedString)
                            .map(valueExtractor);

                    return Objects.isNull(collectionFactory)
                            ? valueExtractedStream.collect(toList())
                            : valueExtractedStream.collect(toCollection(collectionFactory));
                })
                .orElse(Objects.isNull(collectionFactory)
                        ? new ArrayList<>()
                        : collectionFactory.get());
    }

}
