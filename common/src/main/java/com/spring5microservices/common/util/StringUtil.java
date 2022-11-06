package com.spring5microservices.common.util;

import lombok.experimental.UtilityClass;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.StringUtils.hasLength;

@UtilityClass
public class StringUtil {

    private final String DEFAULT_MIDDLE_STRING_ABBREVIATION = "...";
    private final String DEFAULT_STRING_SEPARATOR = ",";


    /**
     *    Abbreviates the given {@code sourceString} to the chunk size provided {@code sizeOfEveryChunk}, replacing the middle
     * characters with the supplied replacement string {@code putInTheMiddle}.
     *
     * <pre>
     * Example 1:
     *
     *   Parameters:              Result:
     *    "ABC"                    Optional("A..C")
     *    ".."
     *    1
     * </pre>
     *
     * <pre>
     * Example 2:
     *
     *   Parameters:              Result:
     *    "ABCDE"                  Optional("AB...DE")
     *    null
     *    2
     * </pre>
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
     * @throws IllegalArgumentException if {@code sizeOfEveryChunk} < 1
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
     * Returns the substring of {@code sourceString} after the last occurrence of a {@code stringToFind}.
     *
     * <pre>
     * Example:
     *
     *   Parameters:              Result:
     *    "1234-9-56"              Optional("1234")
     *    "-9"
     * </pre>
     *
     * @param sourceString
     *    The {@link String} to get a substring from, may be {@code null}
     * @param stringToFind
     *    {@link String} to search
     *
     * @return {@link Optional} with the substring after the last occurrence of {@code stringToFind} if {@code sourceString} is not {@code null}.
     *         {@link Optional#empty()} otherwise.
     */
    public static Optional<String> getBeforeLastIndexOf(final String sourceString,
                                                        final String stringToFind) {
        return ofNullable(sourceString)
                .map(ss -> {
                    if (!hasLength(stringToFind)) {
                        return ss;
                    }
                    int lastIndex = ss.lastIndexOf(stringToFind);
                    return -1 == lastIndex
                            ? ss
                            : ss.substring(0, lastIndex);
                });
    }


    /**
     *    Returns the substring of {@code sourceString} after the last occurrence of a {@code stringToFind}. If given
     * {@code sourceString} is {@code null} or after removing {@code stringToFind} the result is an empty {@link String},
     * returns provided {@code defaultValue}.
     *
     * <pre>
     * Example 1:
     *
     *   Parameters:              Result:
     *    "1234-9-56"              "1234"
     *    "-9"
     *    "654"
     * </pre>
     *
     * <pre>
     * Example 2:
     *
     *   Parameters:              Result:
     *    "1234-9-56"              "1234-9-56"
     *    "88"
     *    "654"
     * </pre>
     *
     * <pre>
     * Example 3:
     *
     *   Parameters:              Result:
     *    "1234"                   "999"
     *    "1234"
     *    "999"
     * </pre>
     *
     * @param sourceString
     *    The {@link String} to get a substring from, may be {@code null}
     * @param stringToFind
     *    {@link String} to search
     * @param defaultValue
     *    {@link String} to return if {@code sourceString} is {@code null} or {@code stringToFind} was not found
     *
     * @return the substring after the last occurrence of {@code stringToFind} if {@code sourceString} is not {@code null}
     *         and {@code stringToFind} was found but result is not an empty {@link String}. {@code defaultValue} otherwise.
     */
    public static String getBeforeLastIndexOf(final String sourceString,
                                              final String stringToFind,
                                              final String defaultValue) {
        return getBeforeLastIndexOf(sourceString, stringToFind)
                .filter(StringUtils::hasLength)
                .orElse(defaultValue);
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
                .map(s -> s.replaceAll("\\D", ""));
    }


    /**
     * Loops through the provided {@link String} one position every time, returning sublists with {@code size}
     *
     * <pre>
     * Example 1:
     *
     *   Parameters:              Result:
     *    "12"                     ["12"]
     *    5
     * </pre>
     *
     * <pre>
     * Example 2:
     *
     *   Parameters:              Result:
     *    "789"                    ["78", "89"]
     *    2
     * </pre>
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
     * <pre>
     * Example 1:
     *
     *   Parameters:              Result:
     *    "123"                    ["123"]
     *    4
     * </pre>
     *
     * <pre>
     * Example 2:
     *
     *   Parameters:              Result:
     *    "123"                    ["12", "3"]
     *    2
     * </pre>
     *
     * @param sourceString
     *    {@link String} to split
     * @param size
     *    Size of every substring
     *
     * @return {@link List} of {@link String}s
     */
    public static List<String> split(final String sourceString,
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
    public static <T> List<T> split(final String source,
                                    final Function<String, ? extends T> valueExtractor) {
        return (List<T>)split(source, valueExtractor, DEFAULT_STRING_SEPARATOR, ArrayList::new);
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
    public static <T> List<T> split(final String source,
                                    final Function<String, ? extends T> valueExtractor,
                                    final String separator) {
        return (List<T>)split(source, valueExtractor, separator, ArrayList::new);
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
     *    {@link Supplier} of the {@link Collection} used to store the returned elements
     *
     * @return {@link Collection}
     */
    public static <T> Collection<T> split(final String source,
                                          final Function<String, ? extends T> valueExtractor,
                                          final String separator,
                                          final Supplier<Collection<T>> collectionFactory) {
        return split(source, separator, -1, valueExtractor, collectionFactory);
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
     *    If {@code null} then {@link ArrayList}
     *
     * @return {@link Collection}
     *
     * @throws Exception if there is a splitted part that cannot be extracted using {@code valueExtractor}.
     *
     *                   <pre>
     *                   For example:
     *                        source = "1,2,3"
     *                        chunkLimit = 2
     *                        valueExtractor = Integer::parseInt
     *
     *                   Splitted parts will be:
     *                        ["1", "2,3"] => second one could not be converted to an {@link Integer}
     *                   </pre>
     */
    public static <T> Collection<T> split(final String source,
                                          final String separator,
                                          final int chunkLimit,
                                          final Function<String, ? extends T> valueExtractor,
                                          final Supplier<Collection<T>> collectionFactory) {
        Supplier<Collection<T>> finalCollectionFactory =
                isNull(collectionFactory)
                        ? ArrayList::new
                        : collectionFactory;

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

                    return valueExtractedStream.collect(toCollection(finalCollectionFactory));
                })
                .orElseGet(finalCollectionFactory);
    }


    /**
     *    Returns a {@link List} splitting the given {@code source} in different parts, using provided {@code separators}
     * to know how to do it, iterating over each one every time.
     *
     * <pre>
     * Example 1:
     *
     *   Parameters:              Result:
     *    "AB,C"                   ["AB", "C"]
     *    ","
     * </pre>
     *
     * <pre>
     * Example 2:
     *
     *   Parameters:              Result:
     *    "A,B#D,E,B"              ["A", "B", "D", "E", "B"]
     *    "#"
     *    ","
     * </pre>
     *
     * @param source
     *    Source {@link String} with the values to extract
     * @param separators
     *    Array used to know how the values are splitted inside {@code source}
     *
     * @return {@link Collection}
     */
    public static List<String> splitMultilevel(final String source,
                                               final String ...separators) {
        return (List<String>) splitMultilevel(source, ArrayList::new, separators);
    }


    /**
     *    Returns a {@link List} splitting the given {@code source} in different parts, using provided {@code separators}
     * to know how to do it, iterating over each one every time.
     *
     * <pre>
     * Example 1:
     *
     *   Parameters:              Result:
     *    "AB,C"                   ["AB", "C"]
     *    ArrayList::new
     *    ","
     * </pre>
     *
     * <pre>
     * Example 2:
     *
     *   Parameters:              Result:
     *    "A,B#D,E,B"              ["A", "B", "D", "E"]
     *    LinkedHashSet::new
     *    "#"
     *    ","
     * </pre>
     *
     * @param source
     *    Source {@link String} with the values to extract
     * @param collectionFactory
     *    {@link Supplier} of the {@link Collection} used to store the returned elements.
     *    If {@code null} then {@link ArrayList}
     * @param separators
     *    Array used to know how the values are splitted inside {@code source}
     *
     * @return {@link Collection}
     */
    public static Collection<String> splitMultilevel(final String source,
                                                     final Supplier<Collection<String>> collectionFactory,
                                                     final String ...separators) {
        Supplier<Collection<String>> finalCollectionFactory =
                Objects.isNull(collectionFactory)
                        ? ArrayList::new
                        : collectionFactory;

        return ofNullable(source)
                .map(s -> {
                    Collection<String> result = finalCollectionFactory.get();
                    if (Objects.isNull(separators)) {
                        result.add(source);
                        return result;
                    }
                    List<String> currentSplittedValues = asList(source);
                    for (int i = 0; i < separators.length; i++) {
                        int finalI = i;
                        currentSplittedValues = currentSplittedValues
                                .stream()
                                .flatMap(elto ->
                                        Arrays.stream(
                                                elto.split(
                                                        Pattern.quote(separators[finalI])
                                                )
                                        )
                                )
                                .collect(toList());
                    }
                    result.addAll(currentSplittedValues);
                    return result;
                })
                .orElseGet(finalCollectionFactory);
    }

}
