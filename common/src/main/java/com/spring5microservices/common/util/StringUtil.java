package com.spring5microservices.common.util;

import com.spring5microservices.common.interfaces.function.PartialFunction;
import lombok.experimental.UtilityClass;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.spring5microservices.common.util.CollectionUtil.toList;
import static com.spring5microservices.common.util.PredicateUtil.alwaysTrue;
import static com.spring5microservices.common.util.PredicateUtil.getOrAlwaysTrue;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;

@UtilityClass
public class StringUtil {

    public final String EMPTY_STRING = "";

    private final String BLANK_SPACE = " ";

    private final String DEFAULT_ABBREVIATION_STRING = "...";

    private final String DEFAULT_SEPARATOR_STRING = ",";

    private static final int INDEX_NOT_FOUND = -1;


    /**
     * Abbreviates the given {@code sourceCS} using provided {@link StringUtil#DEFAULT_ABBREVIATION_STRING} as replacement marker.
     * <p>
     *    The following use cases will not return the expected replaced {@link String}:</p>
     *    <ul>
     *      <li>If {@code sourceCS} is {@code null} or empty then empty {@link String} is returned</li>
     *      <li>If {@code maxLength} is less than or equal to 0 then empty {@link String} is returned</li>
     *      <li>If {@code maxLength} is greater than or equal to {@code sourceCS}'s length then {@code sourceCS} is returned</li>
     *    </ul>
     * <p>
     *    If {@code maxLength} is less than the first character of {@code sourceCS} and {@link StringUtil#DEFAULT_ABBREVIATION_STRING}'s
     * length, then an {@link IllegalArgumentException} will be thrown.
     *
     * @apiNote
     *    If {@code maxLength} is less than 0 then 0 will be used.
     * <p>
     * Examples:
     * <pre>
     *    abbreviate(null, *)       = ""
     *    abbreviate("abc", -1)     = ""
     *    abbreviate("abc", 1)      = {@link IllegalArgumentException} (minimum {@code maxLength} must be {@link StringUtil#DEFAULT_ABBREVIATION_STRING}'s length + 1)
     *    abbreviate("abc", 3)      = "abc"
     *    abbreviate("abcdef", 4)   = "a..."
     *    abbreviate("abcdef", 5)   = "ab..."
     *    abbreviate("abcdef", 10)  = "abcdef"
     * </pre>
     *
     * @param sourceCS
     *    {@link CharSequence} to abbreviate
     * @param maxLength
     *    Max size of the returned {@link String}. If it is less than 0 then 0 will be used
     *
     * @return the abbreviated {@link String} if {@code maxLength} is greater than {@code sourceCS}'s length,
     *         {@link String} conversion of {@code sourceCS} otherwise
     *
     * @throws IllegalArgumentException if {@code maxLength} is less than the first character of {@code sourceCS}
     *                                  and {@link StringUtil#DEFAULT_ABBREVIATION_STRING}'s length
     */
    public static String abbreviate(final CharSequence sourceCS,
                                    final int maxLength) {
        return abbreviate(
                sourceCS,
                maxLength,
                DEFAULT_ABBREVIATION_STRING
        );
    }


    /**
     * Abbreviates the given {@code sourceCS} using provided {@code abbreviationString} as replacement marker.
     * <p>
     *    The following use cases will not return the expected replaced {@link String}:</p>
     *    <ul>
     *      <li>If {@code sourceCS} is {@code null} or empty then empty {@link String} is returned</li>
     *      <li>If {@code maxLength} is less than or equal to 0 then empty {@link String} is returned</li>
     *      <li>If {@code maxLength} is greater than or equal to {@code sourceCS}'s length then {@code sourceCS} is returned</li>
     *    </ul>
     * <p>
     *    If {@code maxLength} is less than the first character of {@code sourceCS} and {@code abbreviationString}'s
     * length, then an {@link IllegalArgumentException} will be thrown.
     *
     * @apiNote
     *    If {@code abbreviationString} is {@code null} then {@link StringUtil#DEFAULT_ABBREVIATION_STRING} will be used.
     * If {@code maxLength} is less than 0 then 0 will be used.
     * <p>
     * Examples:
     * <pre>
     *    abbreviate(null, *, *)          = ""
     *    abbreviate("abc", -1, ".")      = ""
     *    abbreviate("abc", 1, ".")       = {@link IllegalArgumentException} (minimum {@code maxLength} must be 2)
     *    abbreviate("abc", 3, ".")       = "abc"
     *    abbreviate("abcdef", 4, ".")    = "abc."
     *    abbreviate("abcdef", 5, "...")  = "ab..."
     *    abbreviate("abcdef", 10, "...") = "abcdef"
     * </pre>
     *
     * @param sourceCS
     *    {@link CharSequence} to abbreviate
     * @param maxLength
     *    Max size of the returned {@link String}. If it is less than 0 then 0 will be used
     * @param abbreviationString
     *    {@link String} to replace the middle characters. Default value will be {@link StringUtil#DEFAULT_ABBREVIATION_STRING}
     *
     * @return the abbreviated {@link String} if {@code maxLength} is greater than {@code sourceCS}'s length,
     *         {@link String} conversion of {@code sourceCS} otherwise
     *
     * @throws IllegalArgumentException if {@code maxLength} is less than the first character of {@code sourceCS}
     *                                  and {@code abbreviationString}'s length
     */
    public static String abbreviate(final CharSequence sourceCS,
                                    final int maxLength,
                                    final String abbreviationString) {
        if (isEmpty(sourceCS) ||
                0 >= maxLength) {
            return EMPTY_STRING;
        }
        if (sourceCS.length() <= maxLength) {
            return sourceCS.toString();
        }
        final String finalAbbreviationString = ObjectUtil.getOrElse(
                abbreviationString,
                DEFAULT_ABBREVIATION_STRING
        );
        Assert.isTrue(
                maxLength >= (finalAbbreviationString.length() + 1),
                format("Provided maxLength: %s is not enough to abbreviate at least first character of given sourceCS: %s using abbreviationString: %s",
                        maxLength,
                        sourceCS,
                        finalAbbreviationString
                )
        );
        final int startOffset = maxLength - finalAbbreviationString.length();

        return sourceCS.subSequence(0, startOffset)
                + finalAbbreviationString;
    }


    /**
     *    Abbreviates the given {@code sourceCS} to the length passed, replacing the middle characters with
     * {@link StringUtil#DEFAULT_ABBREVIATION_STRING}.
     * <p>
     *    The following use cases will not return the expected replaced {@link String}:</p>
     *    <ul>
     *      <li>If {@code sourceCS} is {@code null} or empty then empty {@link String} is returned</li>
     *      <li>If {@code maxLength} is less than or equal to 0 then empty {@link String} is returned</li>
     *      <li>If {@code maxLength} is greater than or equal to {@code sourceCS}'s length then {@code sourceCS} is returned</li>
     *    </ul>
     * <p>
     *    If {@code maxLength} is less than the first and last characters of {@code sourceCS} and {@link StringUtil#DEFAULT_ABBREVIATION_STRING}'s
     * length, then an {@link IllegalArgumentException} will be thrown.
     *
     * @apiNote
     *    If {@code maxLength} is less than 0 then 0 will be used.
     * <p>
     * Examples:
     * <pre>
     *    abbreviateMiddle(null, *)       = ""
     *    abbreviateMiddle("abc", -1)     = ""
     *    abbreviateMiddle("abc", 2)      = {@link IllegalArgumentException} (minimum {@code maxLength} must be {@link StringUtil#DEFAULT_ABBREVIATION_STRING}'s length + 2)
     *    abbreviateMiddle("abc", 3)      = "abc"
     *    abbreviateMiddle("abcdef", 4)   = "ab...f"
     *    abbreviateMiddle("abcdef", 5)   = "a...f"
     *    abbreviateMiddle("abcdef", 10)  = "abcdef"
     * </pre>
     *
     * @param sourceCS
     *    {@link CharSequence} to abbreviate
     * @param maxLength
     *    Max size of the returned {@link String}. If it is less than 0 then 0 will be used
     *
     * @return the abbreviated {@link String} if {@code maxLength} is greater than {@code sourceCS}'s length,
     *         {@link String} conversion of {@code sourceCS} otherwise
     *
     * @throws IllegalArgumentException if {@code maxLength} is less than the first and last characters of {@code sourceCS}
     *                                  and {@link StringUtil#DEFAULT_ABBREVIATION_STRING}'s length
     */
    public static String abbreviateMiddle(final CharSequence sourceCS,
                                          final int maxLength) {
        return abbreviateMiddle(
                sourceCS,
                maxLength,
                DEFAULT_ABBREVIATION_STRING
        );
    }


    /**
     *    Abbreviates the given {@code sourceCS} to the length passed, replacing the middle characters with the supplied
     * {@code abbreviationString}.
     * <p>
     *    The following use cases will not return the expected replaced {@link String}:</p>
     *    <ul>
     *      <li>If {@code sourceCS} is {@code null} or empty then empty {@link String} will be returned</li>
     *      <li>If {@code maxLength} is less than or equal to 0 then empty {@link String} will be returned</li>
     *      <li>If {@code maxLength} is greater than or equal to {@code sourceCS}'s length then {@code sourceCS} will be returned</li>
     *    </ul>
     * <p>
     *    If {@code maxLength} is less than the first and last characters of {@code sourceCS} and {@code abbreviationString}'s
     * length, then an {@link IllegalArgumentException} will be thrown.
     *
     * @apiNote
     *    If {@code abbreviationString} is {@code null} then {@link StringUtil#DEFAULT_ABBREVIATION_STRING} will be used.
     * If {@code maxLength} is less than 0 then 0 will be used.
     * <p>
     * Examples:
     * <pre>
     *    abbreviateMiddle(null, *, *)           = ""
     *    abbreviateMiddle("abc", -1, ".")       = ""
     *    abbreviateMiddle("abc", 2, ".")        = {@link IllegalArgumentException} (minimum {@code maxLength} must be 3)
     *    abbreviateMiddle("abc", 3, ".")        = "abc"
     *    abbreviateMiddle("abcdef", 4, ".")     = "ab.f"
     *    abbreviateMiddle("abcdef", 5, "...")   = "a...f"
     *    abbreviateMiddle("abcdef", 10, "...")  = "abcdef"
     * </pre>
     *
     * @param sourceCS
     *    {@link CharSequence} to abbreviate
     * @param maxLength
     *    Max size of the returned {@link String}. If it is less than 0 then 0 will be used
     * @param abbreviationString
     *    {@link String} to replace the middle characters. Default value will be {@link StringUtil#DEFAULT_ABBREVIATION_STRING}
     *
     * @return the abbreviated {@link String} if {@code maxLength} is greater than {@code sourceCS}'s length,
     *         {@link String} conversion of {@code sourceCS} otherwise
     *
     * @throws IllegalArgumentException if {@code maxLength} is less than the first and last characters of {@code sourceCS}
     *                                  and {@code abbreviationString}'s length
     */
    public static String abbreviateMiddle(final CharSequence sourceCS,
                                          final int maxLength,
                                          final String abbreviationString) {
        if (isEmpty(sourceCS) ||
                0 >= maxLength) {
            return EMPTY_STRING;
        }
        if (sourceCS.length() <= maxLength) {
            return sourceCS.toString();
        }
        final String finalAbbreviationString = ObjectUtil.getOrElse(
                abbreviationString,
                DEFAULT_ABBREVIATION_STRING
        );
        Assert.isTrue(
                maxLength >= (finalAbbreviationString.length() + 2),
                format("Provided maxLength: %s is not enough to abbreviate at least first and last character of given sourceCS: %s using abbreviationString: %s",
                        maxLength,
                        sourceCS,
                        finalAbbreviationString
                )
        );
        final int sizeOfDisplayedString = maxLength - finalAbbreviationString.length();
        final int startOffset = (sizeOfDisplayedString / 2) + (sizeOfDisplayedString % 2);
        final int endOffset = sourceCS.length() - (sizeOfDisplayedString / 2);

        return sourceCS.subSequence(0, startOffset)
                + finalAbbreviationString
                + sourceCS.subSequence(endOffset, sourceCS.length());
    }


    /**
     * Returns a {@link String} after applying to {@code sourceCS}:
     * <p>
     *  - Filter its {@link Character}s using {@code filterPredicate}
     *  - Transform its filtered {@link Character}s using {@code mapFunction}
     *
     * @apiNote
     *    If {@code sourceCS} is {@code null} or empty then {@link StringUtil#EMPTY_STRING} is returned. If {@code filterPredicate}
     * is {@code null} then all {@link Character}s will be transformed.
     *
     * <pre>
     *    collect(                                         Result:
     *       "abcDEfgIoU12",                                "a2E2I2o2U2"
     *       c -> -1 != "aeiouAEIOU".indexOf(c),
     *       c -> c + "2"
     *    )
     * </pre>
     *
     * @param sourceCS
     *    {@link CharSequence} with the {@link Character}s to filter and transform
     * @param filterPredicate
     *    {@link Predicate} to filter {@link Character}s from {@code sourceCS}
     * @param mapFunction
     *    {@link Function} to transform filtered {@link Character}s of {@code sourceCS}
     *
     * @return new {@link String} from applying the given {@link Function} to each {@link Character}s of {@code sourceCS}
     *         on which {@link Predicate} returns {@code true} and collecting the results
     *
     * @throws IllegalArgumentException if {@code mapFunction} is {@code null} with a not empty {@code sourceCS}
     */
    public static String collect(final CharSequence sourceCS,
                                 final Predicate<Character> filterPredicate,
                                 final Function<Character, String> mapFunction) {
        if (isEmpty(sourceCS)) {
            return EMPTY_STRING;
        }
        Assert.notNull(mapFunction, "mapFunction must be not null");
        return collect(
          sourceCS,
          PartialFunction.of(
                  getOrAlwaysTrue(filterPredicate),
                  mapFunction
          )
        );
    }


    /**
     * Returns a {@link String} after applying to {@code sourceCS}:
     * <p>
     *  - Filter its {@link Character}s using {@link PartialFunction#isDefinedAt(Object)} of {@code partialFunction}
     *  - Transform its filtered {@link Character}s using {@link PartialFunction#apply(Object)} of {@code partialFunction}
     *
     * @apiNote
     *    If {@code sourceCS} is {@code null} or empty then {@link StringUtil#EMPTY_STRING} is returned.
     *
     * <pre>
     *    collect(                                                        Result:
     *       "abcDEfgIoU12",                                               "a2E2I2o2U2"
     *       PartialFunction.of(
     *          c -> null != c && -1 != "aeiouAEIOU".indexOf(c),
     *          c -> null == c
     *             ? ""
     *             : c + "2"
     *       )
     *    )
     * </pre>
     *
     * @param sourceCS
     *    {@link CharSequence} with the {@link Character}s to filter and transform
     * @param partialFunction
     *    {@link PartialFunction} to filter and transform elements of {@code sourceCS}
     *
     * @return new {@link String} from applying the given {@link PartialFunction} to each {@link Character}s of {@code sourceCS}
     *         on which it is defined and collecting the results
     *
     * @throws IllegalArgumentException if {@code partialFunction} is {@code null} with a not empty {@code sourceCS}
     */
    public static String collect(final CharSequence sourceCS,
                                 final PartialFunction<Character, String> partialFunction) {
        if (isEmpty(sourceCS)) {
            return EMPTY_STRING;
        }
        Assert.notNull(partialFunction, "partialFunction must be not null");
        return sourceCS
                .codePoints()
                .filter(c -> partialFunction.isDefinedAt((char) c))
                .mapToObj(c -> partialFunction.apply((char) c))
                .collect(joining());
    }


    /**
     * Verifies if the given {@code sourceCS} contains {@code stringToSearch} ignoring case.
     *
     * @apiNote
     *    If {@code sourceCS} or {@code stringToSearch} are {@code null} then {@code false} is returned.
     * <p>
     * Examples:
     * <pre>
     *    containsIgnoreCase(null, *)       = false
     *    containsIgnoreCase("", "a")       = false
     *    containsIgnoreCase("abc", "ac")   = false
     *    containsIgnoreCase("", "")        = true
     *    containsIgnoreCase("a", "")       = true
     *    containsIgnoreCase("ABcD", "bC")  = true
     * </pre>
     *
     * @param sourceCS
     *    {@link CharSequence} to check if contains {@code stringToSearch}
     * @param stringToSearch
     *    {@link String} to search
     *
     * @return {@code true} if {@code sourceCS} contains {@code stringToSearch}, {@code false} otherwise.
     */
    public static boolean containsIgnoreCase(final CharSequence sourceCS,
                                             final String stringToSearch) {
        if (isNull(sourceCS) || isNull(stringToSearch)) {
            return false;
        }
        return sourceCS.toString().toLowerCase()
                .contains(stringToSearch.toLowerCase());
    }


    /**
     * Counts how many times {@code stringToSearch} appears in the given {@code sourceCS}.
     *
     * @apiNote
     *    If {@code sourceCS} or {@code stringToSearch} are {@code null} or empty then 0 is returned. Only counts
     * non-overlapping matches.
     * <p>
     * Examples:
     * <pre>
     *    count(null, *)        = 0
     *    count("", *)          = 0
     *    count(*, null)        = 0
     *    count("abcab", "ab")  = 2
     *    count("abcab", "xx")  = 0
     *    count("aaaaa", "aa")  = 2
     * </pre>
     *
     * @param sourceCS
     *    {@link CharSequence} to check
     * @param stringToSearch
     *    {@link String} to count
     *
     * @return the number of occurrences, 0 if {@code stringToSearch} or {@code sourceCS} are {@code null} or empty
     */
    public static int count(final CharSequence sourceCS,
                            final String stringToSearch) {
        if (isEmpty(sourceCS) ||
                isEmpty(stringToSearch)) {
            return 0;
        }
        final String sourceCSToString = sourceCS.toString();
        int count = 0;
        int idx = 0;
        while ((idx = sourceCSToString.indexOf(stringToSearch, idx)) != INDEX_NOT_FOUND) {
            count++;
            idx += stringToSearch.length();
        }
        return count;
    }


    /**
     *    Returns a {@link String} removing the longest prefix of {@link Character}s included in {@code sourceCS} that
     * satisfy the {@link Predicate} {@code filterPredicate}.
     *
     * @apiNote
     *    If {@code sourceCS} is {@code null} or empty then {@link StringUtil#EMPTY_STRING} is returned.
     * If {@code filterPredicate} is {@code null} then {@link String} conversion of {@code sourceCS} is returned.
     *
     * <pre>
     *    dropWhile(                                       Result:
     *       "aEibc12",                                     "bc12"
     *       c -> -1 != "aeiouAEIOU".indexOf(c)
     *    )
     * </pre>
     *
     * @param sourceCS
     *    {@link CharSequence} with the {@link Character}s to filter
     * @param filterPredicate
     *    {@link Predicate} to filter {@link Character}s from {@code sourceCS}
     *
     * @return the longest suffix of provided {@code sourceCS} whose first {@link Character} does not satisfy {@code filterPredicate}
     */
    public static String dropWhile(final CharSequence sourceCS,
                                   final Predicate<Character> filterPredicate) {
        if (isEmpty(sourceCS)) {
            return EMPTY_STRING;
        }
        if (isNull(filterPredicate)) {
            return new String(
                    sourceCS.toString()
            );
        }
        return sourceCS
                .codePoints()
                .dropWhile(c ->
                        filterPredicate.test((char) c)
                )
                .mapToObj(i ->
                        Character.valueOf((char) i).toString()
                )
                .collect(joining());
    }


    /**
     * Selects all {@link Character} of {@code sourceCS} which satisfy the {@link Predicate} {@code filterPredicate}.
     *
     * @apiNote
     *    If {@code sourceCS} is {@code null} or empty then {@link StringUtil#EMPTY_STRING} is returned. If {@code filterPredicate}
     * is {@code null} then {@link String} conversion of {@code sourceCS} will be returned.
     *
     * <pre>
     *    filter(                                          Result:
     *       "abcDEfgIoU12",                                "aEIoU"
     *       c -> -1 != "aeiouAEIOU".indexOf(c)
     *    )
     * </pre>
     *
     * @param sourceCS
     *    {@link CharSequence} to filter
     * @param filterPredicate
     *    {@link Predicate} to filter {@link Character}s from {@code sourceCS}
     *
     * @return {@link Character}s of {@code sourceCS} which satisfy {@code filterPredicate}
     */
    public static String filter(final CharSequence sourceCS,
                                final Predicate<Character> filterPredicate) {
        if (isEmpty(sourceCS)) {
            return EMPTY_STRING;
        }
        if (isNull(filterPredicate)) {
            return new String(sourceCS.toString());
        }
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < sourceCS.length(); i++) {
            Character currentChar = sourceCS.charAt(i);
            if (filterPredicate.test(currentChar)) {
                result.append(currentChar);
            }
        }
        return result.toString();
    }


    /**
     * Selects all {@link Character} of {@code sourceCS} which do not satisfy the {@link Predicate} {@code filterPredicate}.
     *
     * @apiNote
     *    If {@code sourceCS} is {@code null} or empty then {@link StringUtil#EMPTY_STRING} is returned. If {@code filterPredicate}
     * is {@code null} then {@link String} conversion of {@code sourceCS} will be returned.
     *
     * <pre>
     *    filterNot(                                       Result:
     *       "abcDEfgIoU12",                                "bcDfg12"
     *       c -> -1 != "aeiouAEIOU".indexOf(c)
     *    )
     * </pre>
     *
     * @param sourceCS
     *    {@link CharSequence} to filter
     * @param filterPredicate
     *    {@link Predicate} to filter characters from {@code sourceCS}
     *
     * @return {@link Character}s of {@code sourceCS} which do not satisfy {@code filterPredicate}
     */
    public static String filterNot(final CharSequence sourceCS,
                                   final Predicate<Character> filterPredicate) {
        final Predicate<Character> finalFilterPredicate =
                isNull(filterPredicate)
                        ? null
                        : filterPredicate.negate();

        return filter(
                sourceCS,
                finalFilterPredicate
        );
    }


    /**
     *    Using the given value {@code initialValue} as initial one, applies the provided {@link BiFunction} to all
     * {@link Character}s of {@code sourceCS}, going left to right.
     *
     * @apiNote
     *    If {@code sourceCS} or {@code accumulator} are {@code null} then {@code initialValue} is returned.
     *
     * <pre>
     *    foldLeft(                              Result:
     *       "ab12",                              295
     *       1,
     *       (r, c) -> r + (int) c
     *    )
     * </pre>
     *
     * @param sourceCS
     *    {@link CharSequence} with {@link Character}s to combine
     * @param initialValue
     *    The initial value to start with
     * @param accumulator
     *    A {@link BiFunction} which combines elements
     *
     * @return result of inserting {@code accumulator} between consecutive {@link Character}s of {@code sourceCS}, going
     *         left to right with the start value {@code initialValue} on the left.
     */
    public static <R> R foldLeft(final CharSequence sourceCS,
                                 final R initialValue,
                                 final BiFunction<R, Character, R> accumulator) {
        return ofNullable(sourceCS)
                .map(sc -> {
                    R result = initialValue;
                    if (nonNull(accumulator)) {
                        for (int i = 0; i < sourceCS.length(); i++) {
                            Character currentChar = sourceCS.charAt(i);
                            result = accumulator.apply(result, currentChar);
                        }
                    }
                    return result;
                })
                .orElse(initialValue);
    }


    /**
     * Returns a new {@link String} removing from the given {@code sourceCS} all non-numeric characters.
     *
     * @param sourceCS
     *    {@link CharSequence} to get all non-numeric characters.
     *
     * @return new {@link String} without non-numeric characters.
     */
    public static String getDigits(final CharSequence sourceCS) {
        if (isEmpty(sourceCS)) {
            return EMPTY_STRING;
        }
        final int sourceCSLength = sourceCS.length();
        final StringBuilder sourceCSDigits = new StringBuilder(EMPTY_STRING);
        for (int i = 0; i < sourceCSLength; i++) {
            final char tempChar = sourceCS.charAt(i);
            if (Character.isDigit(tempChar)) {
                sourceCSDigits.append(tempChar);
            }
        }
        return sourceCSDigits.toString();
    }


    /**
     * Return the given {@code sourceCS} if is neither {@code null} nor empty. Otherwise, returns {@code defaultValue}.
     *
     * <pre>
     *    getNotEmptyOrElse(null, "other")   = "other"
     *    getNotEmptyOrElse("", "other")     = "other"
     *    getNotEmptyOrElse("  ", "other")   = "  "
     *    getNotEmptyOrElse("abc", "other")  = "abc"
     * </pre>
     *
     * @param sourceCS
     *    {@link CharSequence} returned only if is not {@code null}
     * @param defaultValue
     *    Alternative value to return
     *
     * @return {@link String} conversion of {@code sourceCS} if contains characters, {@code defaultValue} otherwise
     */
    public static String getNotEmptyOrElse(final CharSequence sourceCS,
                                           final String defaultValue) {
        return ofNullable(sourceCS)
                .map(CharSequence::toString)
                .filter(s -> !isEmpty(s))
                .orElse(defaultValue);
    }


    /**
     * Return the given {@code sourceCS} if is not {@code null}. Otherwise, returns {@code defaultValue}.
     *
     * @param sourceCS
     *    {@link CharSequence} returned only if is not {@code null}
     * @param defaultValue
     *    Alternative value to return
     *
     * @return {@link String} conversion of {@code sourceCS} if is not {@code null}, {@code defaultValue} otherwise
     */
    public static String getOrElse(final CharSequence sourceCS,
                                   final String defaultValue) {
        return ofNullable(sourceCS)
                .map(CharSequence::toString)
                .orElse(defaultValue);
    }


    /**
     *    Return the given {@code sourceCS} if is not {@code null} and verifies {@code filterPredicate}.
     * Otherwise, returns {@code defaultValue}.
     *
     * <pre>
     *    getOrElse(                             Result:
     *       "   ",                               "other"
     *       s -> s.trim().size() > 0,
     *       "other"
     *    )
     * </pre>
     *
     * @param sourceCS
     *    {@link CharSequence} returned only if is not {@code null}
     * @param filterPredicate
     *    {@link Predicate} to apply if {@code sourceCS} is not {@code null}
     * @param defaultValue
     *    Alternative value to return
     *
     * @return {@link String} conversion of {@code sourceCS} if is not {@code null} and verifies {@code filterPredicate},
     *         {@code defaultValue} otherwise
     */
    public static String getOrElse(final CharSequence sourceCS,
                                   final Predicate<CharSequence> filterPredicate,
                                   final String defaultValue) {
        final Predicate<CharSequence> finalFilterPredicate = ObjectUtil.getOrElse(
                filterPredicate,
                alwaysTrue()
        );
        return ofNullable(sourceCS)
                .filter(finalFilterPredicate)
                .map(CharSequence::toString)
                .orElse(defaultValue);
    }


    /**
     * Partitions given {@code sourceCS} into a {@link Map} of {@link String} according to {@code discriminatorKey}.
     *
     * <pre>
     *    groupBy(                                                   Result:
     *       "essae",                                                 [("e", "ee")
     *       Object::toString                                          ("s", "ss")
     *    )                                                            ("a", "a")]
     *
     *    groupBy(                                                   Result:
     *       "essae",                                                 [(1, "a")
     *       c -> StringUtil.count("essae", c.toString())              (2, "esse")]
     *    )
     * </pre>
     *
     * @param sourceCS
     *    Source {@link CharSequence} with the elements to group
     * @param discriminatorKey
     *    The discriminator {@link Function} to get the key values of returned {@link Map}
     *
     * @return new {@link Map} from applying the given {@code discriminatorKey} to each element of {@code sourceCS}
     *
     * @throws IllegalArgumentException if {@code discriminatorKey} is {@code null} with a not empty {@code sourceCS}
     */
    public static <K> Map<K, String> groupBy(final CharSequence sourceCS,
                                             final Function<Character, ? extends K> discriminatorKey) {
        return groupBy(
              sourceCS,
              discriminatorKey,
              null
        );
    }


    /**
     * Partitions given {@code sourceCS} into a {@link Map} of {@link String} according to {@code discriminatorKey}.
     *
     * @apiNote
     *    If {@code filterPredicate} is {@code null} then all elements will be used.
     *
     * <pre>
     *    groupBy(                                                   Result:
     *       "essae",                                                 [("e", "ee")
     *       Object::toString,                                         ("a", "a")]
     *       c -> -1 != "aeiouAEIOU".indexOf(c)
     *    )
     *    groupBy(                                                   Result:
     *       "essae",                                                 [(1, "a")
     *       c -> StringUtil.count("essae", c.toString()),             (2, "ee")]
     *       c -> -1 != "aeiouAEIOU".indexOf(c)
     *    )
     * </pre>
     *
     * @param sourceCS
     *    Source {@link CharSequence} with the elements to filter and group
     * @param discriminatorKey
     *    The discriminator {@link Function} to get the key values of returned {@link Map}
     * @param filterPredicate
     *    {@link Predicate} to filter {@link Character}s from {@code sourceCS}
     *
     * @return new {@link Map} from applying the given {@code discriminatorKey} to each element of {@code sourceCS}
     *
     * @throws IllegalArgumentException if {@code discriminatorKey} is {@code null} with a not empty {@code sourceCS}
     */
    public static <K> Map<K, String> groupBy(final CharSequence sourceCS,
                                             final Function<Character, ? extends K> discriminatorKey,
                                             final Predicate<Character> filterPredicate) {
        if (isEmpty(sourceCS)) {
            return new HashMap<>();
        }
        Assert.notNull(discriminatorKey, "discriminatorKey must be not null");
        final Predicate<Character> finalFilterPredicate = ObjectUtil.getOrElse(
                filterPredicate,
                alwaysTrue()
        );
        final Map<K, StringBuilder> tempResult = new HashMap<>();
        for (int i = 0; i < sourceCS.length(); i++) {
            Character currentChar = sourceCS.charAt(i);

            if (finalFilterPredicate.test(currentChar)) {
                K discriminatorKeyResult = discriminatorKey.apply(currentChar);
                tempResult.putIfAbsent(
                        discriminatorKeyResult,
                        new StringBuilder()
                );
                tempResult.get(discriminatorKeyResult)
                        .append(currentChar);
            }
        }
        return MapUtil.mapValues(
                tempResult,
                (k, v) -> v.toString()
        );
    }


    /**
     *    Abbreviates the given {@code sourceCS} to the length passed, replacing the middle characters with
     * {@link StringUtil#DEFAULT_ABBREVIATION_STRING}.
     * <p>
     * The following use cases will not return the expected replaced {@link String}:
     *    <ul>
     *      <li>If {@code sourceCS} is {@code null} or empty then empty {@link String} is returned</li>
     *      <li>If {@code maxLength} is less than or equal to 0 then empty {@link String} is returned</li>
     *    </ul>
     * <p>
     *    If {@code maxLength} is less than the first and last characters of {@code sourceCS} and {@link StringUtil#DEFAULT_ABBREVIATION_STRING}'s
     * length, then an {@link IllegalArgumentException} will be thrown.
     * <p>
     *    {@link StringUtil#abbreviateMiddle(CharSequence, int)} returns {@code sourceCS} when {@code maxLength} is greater
     * than or equals to {@code sourceCS}'s length however, the current function always tries to hide middle characters
     * if it is possible:
     * <p>
     * Examples:
     * <pre>
     *    abbreviateMiddle("abcdef", 10) = "abcdef"
     *    hideMiddle("abcdef", 10)       = "ab...f"
     * </pre>
     *
     * @apiNote
     *    If {@code maxLength} is less than 0 then 0 will be used.
     * <p>
     * Examples:
     * <pre>
     *    hideMiddle(null, *)       = ""
     *    hideMiddle("abc", -1)     = ""
     *    hideMiddle("abc", 3)      = (minimum {@code maxLength} must be {@link StringUtil#DEFAULT_ABBREVIATION_STRING}'s length + 2)
     *    hideMiddle("abcdef", 5)   = "a...f"
     *    hideMiddle("abcdef", 10)  = "ab...f"
     * </pre>
     *
     * @param sourceCS
     *    {@link CharSequence} to abbreviate
     * @param maxLength
     *    Max size of the returned {@link String}. If it is less than 0 then 0 will be used
     *
     * @return the abbreviated {@link String} if {@code maxLength} is greater than 2,
     *         {@link String} conversion of {@code sourceCS} otherwise
     *
     * @throws IllegalArgumentException if {@code maxLength} is less than the first and last characters of {@code sourceCS}
     *                                  and {@link StringUtil#DEFAULT_ABBREVIATION_STRING}'s length
     */
    public static String hideMiddle(final CharSequence sourceCS,
                                    final int maxLength) {
        return hideMiddle(
                sourceCS,
                maxLength,
                DEFAULT_ABBREVIATION_STRING
        );
    }


    /**
     *    Abbreviates the given {@code sourceCS} to the length passed, replacing the middle characters with the supplied
     * {@code abbreviationString}.
     * <p>
     * The following use cases will not return the expected replaced {@link String}:
     *    <ul>
     *      <li>If {@code sourceCS} is {@code null} or empty then empty {@link String} is returned</li>
     *      <li>If {@code maxLength} is less than or equal to 0 then empty {@link String} is returned</li>
     *    </ul>
     * <p>
     *    If {@code maxLength} is less than the first and last characters of {@code sourceCS} and {@code abbreviationString}'s
     * length, then an {@link IllegalArgumentException} will be thrown.
     * <p>
     *    {@link StringUtil#abbreviateMiddle(CharSequence, int, String)} returns {@code sourceCS} when {@code maxLength}
     * is greater than or equals to {@code sourceCS}'s length however, the current function always tries to hide middle
     * characters if it is possible:
     * <p>
     * Examples:
     * <pre>
     *    abbreviateMiddle("abc", 3, ".") = "abc"
     *    hideMiddle("abc", 3, ".")       = "a.c"
     *
     *    abbreviateMiddle("abcdef", 10, "...") = "abcdef"
     *    hideMiddle("abcdef", 10, "...")       = "ab...f"
     * </pre>
     *
     * @apiNote
     *    If {@code abbreviationString} is {@code null} then {@link StringUtil#DEFAULT_ABBREVIATION_STRING} will be used.
     * If {@code maxLength} is less than 0 then 0 will be used.
     * <p>
     * Examples:
     * <pre>
     *    hideMiddle(null, *, *)           = ""
     *    hideMiddle("abc", -1, ".")       = ""
     *    hideMiddle("abc", 2, ".")        = {@link IllegalArgumentException} (minimum {@code maxLength} must be 3)
     *    hideMiddle("abc", 3, ".")        = "a.c"
     *    hideMiddle("abcdef", 4, ".")     = "ab.f"
     *    hideMiddle("abcdef", 5, "...")   = "a...f"
     *    hideMiddle("abcdef", 10, "...")  = "ab...f"
     * </pre>
     *
     * @param sourceCS
     *    {@link CharSequence} to abbreviate
     * @param maxLength
     *    Max size of the returned {@link String}. If it is less than 0 then 0 will be used
     * @param abbreviationString
     *    {@link String} to replace the middle characters. Default value will be {@link StringUtil#DEFAULT_ABBREVIATION_STRING}
     *
     * @return the abbreviated {@link String} if {@code maxLength} is greater than 2,
     *         {@link String} conversion of {@code sourceCS} otherwise
     *
     * @throws IllegalArgumentException if {@code maxLength} is less than the first and last characters of {@code sourceCS}
     *                                  and {@code abbreviationString}'s length
     */
    public static String hideMiddle(final CharSequence sourceCS,
                                    final int maxLength,
                                    final String abbreviationString) {
        if (isEmpty(sourceCS) ||
                0 >= maxLength) {
            return EMPTY_STRING;
        }
        if (2 >= sourceCS.length()) {
            return sourceCS.toString();
        }
        final String finalAbbreviationString = ObjectUtil.getOrElse(
                abbreviationString,
                DEFAULT_ABBREVIATION_STRING
        );
        Assert.isTrue(
                maxLength >= (finalAbbreviationString.length() + 2),
                format("Provided maxLength: %s is not enough to abbreviate at least first and last character of given sourceCS: %s using abbreviationString: %s",
                        maxLength,
                        sourceCS,
                        finalAbbreviationString
                )
        );
        final int sizeOfDisplayedString = maxLength < sourceCS.length()
                ? maxLength - finalAbbreviationString.length()
                : sourceCS.length() - finalAbbreviationString.length();

        final int startOffset = (sizeOfDisplayedString / 2) + (sizeOfDisplayedString % 2);
        final int endOffset = sourceCS.length() - (sizeOfDisplayedString / 2);

        return sourceCS.subSequence(0, startOffset)
                + finalAbbreviationString
                + sourceCS.subSequence(endOffset, sourceCS.length());
    }


    /**
     * Checks if the given {@code sourceCS} is {@code null}, an empty {@link String} ('') or whitespace.
     *
     * <pre>
     *    isBlank(null)    = true
     *    isBlank("")      = true
     *    isBlank("   ")   = true
     *    isBlank("  a ")  = false
     * </pre>
     *
     * @param sourceCS
     *    {@link CharSequence} to verify
     *
     * @return {@code true} if {@code sourceCS} is {@code null} or has no characters
     */
    public static boolean isBlank(final CharSequence sourceCS) {
        if (isEmpty(sourceCS)) {
            return true;
        }
        return IntStream.range(0, sourceCS.length())
                .allMatch(i ->
                        Character.isWhitespace(
                                sourceCS.charAt(i)
                        )
                );
    }


    /**
     * Checks if the given {@code sourceCS} is {@code null} or an empty {@link String} ('').
     *
     * <pre>
     *    isEmpty(null)    = true
     *    isEmpty("")      = true
     *    isEmpty("   ")   = false
     *    isEmpty("  a ")  = false
     * </pre>
     *
     * @param sourceCS
     *    {@link CharSequence} to verify
     *
     * @return {@code true} if {@code sourceCS} is {@code null} or has no characters
     */
    public static boolean isEmpty(final CharSequence sourceCS) {
        return null == sourceCS ||
                sourceCS.isEmpty();
    }


    /**
     * Joins the elements of the provided {@link Collection} into a single {@link String} containing the provided elements.
     *
     * @apiNote
     *    {@code null} elements will be managed as empty {@link String}.
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to include in the returned {@link String}
     *
     * @return the joined {@link String}, empty one if {@code null} or empty {@code sourceCollection}
     */
    public static <T> String join(final Collection<? extends T> sourceCollection) {
        return join(
                sourceCollection,
                null,
                EMPTY_STRING
        );
    }


    /**
     * Joins the given {@code elements} into a single {@link String}.
     *
     * @apiNote
     *    {@code null} elements will be managed as empty {@link String}.
     *
     * @param elements
     *    The values to join together
     *
     * @return the joined {@link String}, empty one if {@code null} or empty {@code elements}
     */
    @SafeVarargs
    public static <T> String join(final T... elements) {
        return join(
                toList(elements),
                null,
                EMPTY_STRING
        );
    }


    /**
     *    Joins the elements of the provided {@link Collection} into a single {@link String} containing the provided
     * elements if the current one verifies {@code filterPredicate}.
     *
     * @apiNote
     *    If {@code filterPredicate} is {@code null} then all elements will be converted to their equivalent {@link String}
     * representation and {@code null} elements will be managed as empty {@link String}.
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to include in the returned {@link String}
     * @param filterPredicate
     *    {@link Predicate} to filter {@code sourceCollection}
     *
     * @return the joined {@link String}, empty one if {@code null} or empty {@code sourceCollection}
     */
    public static <T> String join(final Collection<? extends T> sourceCollection,
                                  final Predicate<? super T> filterPredicate) {
        return join(
                sourceCollection,
                filterPredicate,
                EMPTY_STRING
        );
    }


    /**
     *    Joins the elements of the provided {@link Collection} into a single {@link String} containing the provided
     * elements if the current one verifies {@code filterPredicate}.
     *
     * @apiNote
     *    {@code null} elements will be managed as empty {@link String}. If {@code separator} is {@code null} then
     * empty {@link String} will be used.
     *
     * <pre>
     *    join(                                  Result:
     *       [1, 12, 33],                         "1;12;33"
     *       ";"
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to include in the returned {@link String}
     * @param separator
     *    The separator character to use, {@code null} treated as empty {@link String}
     *
     * @return the joined {@link String}, empty one if {@code null} or empty {@code sourceCollection}
     */
    public static <T> String join(final Collection<? extends T> sourceCollection,
                                  final String separator) {
        return join(
                sourceCollection,
                null,
                separator
        );
    }


    /**
     *    Joins the elements of the provided {@link Collection} into a single {@link String} containing the provided
     * elements if the current one verifies {@code filterPredicate}.
     *
     * @apiNote
     *    If {@code filterPredicate} is {@code null} then all elements will be converted to their equivalent {@link String}
     * representation and {@code null} elements will be managed as empty {@link String}. If {@code separator} is
     * {@code null} then empty {@link String} will be used.
     *
     * <pre>
     *    join(                                  Result:
     *       [1, 12, 33],                         "1;33"
     *       i -> 1 == i % 2,
     *       ";"
     *    )
     * </pre>
     *
     * @param sourceCollection
     *    Source {@link Collection} with the elements to include in the returned {@link String}
     * @param filterPredicate
     *    {@link Predicate} to filter {@code sourceCollection}
     * @param separator
     *    The separator character to use, {@code null} treated as empty {@link String}
     *
     * @return the joined {@link String}, empty one if {@code null} or empty {@code sourceCollection}
     */
    public static <T> String join(final Collection<? extends T> sourceCollection,
                                  final Predicate<? super T> filterPredicate,
                                  final String separator) {
        return ofNullable(sourceCollection)
                .map(c -> {
                    final String finalSeparator =
                            isNull(separator)
                                    ? EMPTY_STRING
                                    : separator;

                    final Stream<? extends T> collectionStream = Objects.isNull(filterPredicate)
                            ? c.stream()
                            : c.stream().filter(filterPredicate);

                    return collectionStream
                            .map(e -> Objects.toString(e, EMPTY_STRING))
                            .collect(
                                    joining(finalSeparator)
                            );
                })
                .orElse(EMPTY_STRING);
    }


    /**
     * Left pad the {@link String} {@code sourceCS} with spaces (' ') up to the provided {@code size}.
     *
     * @apiNote
     *    If {@code size} is less than 0 then 0 will be used.
     * <p>
     * Examples:
     * <pre>
     *    leftPad(null, -1)   = ""
     *    leftPad(null, 0)    = ""
     *    leftPad(null, 2)    = "  "
     *    leftPad("", 3)      = "   "
     *    leftPad("bat", -1)  = "bat"
     *    leftPad("bat", 1)   = "bat"
     *    leftPad("bat", 3)   = "bat"
     *    leftPad("bat", 5)   = "  bat"
     * </pre>
     *
     * @param sourceCS
     *    {@link CharSequence} to pad out
     * @param size
     *    The size to pad to
     *
     * @return left padded {@link String} or {@link String} conversion of {@code sourceCS} if no padding is necessary
     */
    public static String leftPad(final CharSequence sourceCS,
                                 final int size) {
        return leftPad(
                sourceCS,
                size,
                BLANK_SPACE
        );
    }


    /**
     * Left pad the {@link String} {@code sourceCS} with {@code padString} up to the provided {@code size}.
     *
     * @apiNote
     *    If {@code size} is less than 0 then 0 will be used. If {@code padString} is {@code null} then
     * {@link StringUtil#BLANK_SPACE} will be used.
     * <p>
     * Examples:
     * <pre>
     *    leftPad(null, -1, *)     = ""
     *    leftPad(null, 0, *)      = ""
     *    leftPad(null, 2, "z")    = "zz"
     *    leftPad("", 3, "z")      = "zzz"
     *    leftPad("bat", -1, "z")  = "bat"
     *    leftPad("bat", 1, "z")   = "bat"
     *    leftPad("bat", 3, "z")   = "bat"
     *    leftPad("bat", 5, null)  = "  bat"
     *    leftPad("bat", 5, "")    = "  bat"
     *    leftPad("bat", 5, "z")   = "zzbat"
     * </pre>
     *
     * @param sourceCS
     *    {@link CharSequence} to pad out
     * @param size
     *    The size to pad to
     * @param padString
     *    {@link String} to pad with, {@code null} or empty treated as {@link StringUtil#BLANK_SPACE}
     *
     * @return left padded {@link String} or {@link String} conversion of {@code sourceCS} if no padding is necessary
     */
    public static String leftPad(final CharSequence sourceCS,
                                 final int size,
                                 final String padString) {
        final String finalSourceCS = isNull(sourceCS)
                ? EMPTY_STRING
                : sourceCS.toString();

        final int finalSize = Math.max(0, size);
        final String finalPadString =
                isNull(padString)
                        ? BLANK_SPACE
                        : padString;

        final int sourceStringLength = finalSourceCS.length();
        final int padStringLength = finalPadString.length();
        final int pads = finalSize - sourceStringLength;

        // Returns original sourceCS when possible
        if (0 >= pads) {
            return finalSourceCS;
        }
        if (pads == padStringLength) {
            return finalPadString.concat(finalSourceCS);
        }
        if (pads < padStringLength) {
            return finalPadString.substring(0, pads)
                    .concat(finalSourceCS);
        }
        final char[] padding = new char[pads];
        final char[] padChars = finalPadString.toCharArray();
        for (int i = 0; i < pads; i++) {
            padding[i] = padChars[i % padStringLength];
        }
        return new String(padding)
                .concat(finalSourceCS);
    }


    /**
     * Builds a new {@link String} by applying a {@link Function} to all {@link Character}s of provided {@code sourceCS}.
     *
     * @apiNote
     *    If {@code sourceCS} is {@code null} or empty then {@link StringUtil#EMPTY_STRING} is returned. If {@code mapFunction}
     * is {@code null} then {@link String} conversion of {@code sourceCS} is returned.
     *
     * <pre>
     *    map(                                                                 Result:
     *       "aEibc1U2"                                                         "bc12"
     *       c -> -1 != "aeiouAEIOU".indexOf(c) ? "" : c.toString()
     *    )
     * </pre>
     *
     * @param sourceCS
     *     {@link CharSequence} to used as source of the returned {@link String}
     * @param mapFunction
     *    {@link Function} to apply to each {@link Character}
     *
     * @return new {@link String} from applying the given {@link Function} to each {@link Character} of {@code sourceCS}
     */
    public static String map(final CharSequence sourceCS,
                             final Function<Character, String> mapFunction) {
        if (isEmpty(sourceCS)) {
            return EMPTY_STRING;
        }
        if (isNull(mapFunction)) {
            return new String(sourceCS.toString());
        }
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < sourceCS.length(); i++) {
            result.append(
                    mapFunction.apply(
                            sourceCS.charAt(i)
                    )
            );
        }
        return result.toString();
    }


    /**
     * Right pad the {@link String} {@code sourceCS} with spaces (' ') up to the provided {@code size}.
     *
     * @apiNote
     *    If {@code size} is less than 0 then 0 will be used.
     * <p>
     * Examples:
     * <pre>
     *    rightPad(null, -1)   = ""
     *    rightPad(null, 0)    = ""
     *    rightPad(null, 2)    = "  "
     *    rightPad("", 3)      = "   "
     *    rightPad("bat", -1)  = "bat"
     *    rightPad("bat", 1)   = "bat"
     *    rightPad("bat", 3)   = "bat"
     *    rightPad("bat", 5)   = "bat  "
     * </pre>
     *
     * @param sourceCS
     *    {@link CharSequence} to pad out
     * @param size
     *    The size to pad to
     *
     * @return right padded {@link String} or {@link String} conversion of {@code sourceCS} if no padding is necessary
     */
    public static String rightPad(final CharSequence sourceCS,
                                  final int size) {
        return rightPad(
                sourceCS,
                size,
                BLANK_SPACE
        );
    }


    /**
     * Right pad the {@link String} {@code sourceCS} with {@code padString} up to the provided {@code size}.
     *
     * @apiNote
     *    If {@code size} is less than 0 then 0 will be used. If {@code padString} is {@code null} then
     * {@link StringUtil#BLANK_SPACE} will be used.
     * <p>
     * Examples:
     * <pre>
     *    rightPad(null, -1, *)     = ""
     *    rightPad(null, 0, *)      = ""
     *    rightPad(null, 2, "z")    = "zz"
     *    rightPad("", 3, "z")      = "zzz"
     *    rightPad("bat", -1, "z")  = "bat"
     *    rightPad("bat", 1, "z")   = "bat"
     *    rightPad("bat", 3, "z")   = "bat"
     *    rightPad("bat", 5, null)  = "bat  "
     *    rightPad("bat", 5, "")    = "bat  "
     *    rightPad("bat", 5, "z")   = "batzz"
     * </pre>
     *
     * @param sourceCS
     *    {@link CharSequence} to pad out
     * @param size
     *    The size to pad to
     * @param padString
     *    {@link String} to pad with, {@code null} or empty treated as {@link StringUtil#BLANK_SPACE}
     *
     * @return right padded {@link String} or {@link String} conversion of {@code sourceCS} if no padding is necessary
     */
    public static String rightPad(final CharSequence sourceCS,
                                  final int size,
                                  final String padString) {
        final String finalSourceCS = isNull(sourceCS)
                ? EMPTY_STRING
                : sourceCS.toString();

        final int finalSize = Math.max(0, size);
        final String finalPadString =
                isNull(padString)
                        ? BLANK_SPACE
                        : padString;

        final int sourceStringLength = finalSourceCS.length();
        final int padStringLength = finalPadString.length();
        final int pads = finalSize - sourceStringLength;

        // Returns original sourceCS when possible
        if (0 >= pads) {
            return finalSourceCS;
        }
        if (pads == padStringLength) {
            return finalSourceCS.concat(finalPadString);
        }
        if (pads < padStringLength) {
            return finalSourceCS.concat(
                    finalPadString.substring(0, pads)
            );
        }
        final char[] padding = new char[pads];
        final char[] padChars = finalPadString.toCharArray();
        for (int i = 0; i < pads; i++) {
            padding[i] = padChars[i % padStringLength];
        }
        return finalSourceCS.concat(
                new String(padding)
        );
    }


    /**
     * Loops through the provided {@link CharSequence} one position every time, returning sublists with {@code size}.
     *
     * <pre>
     *    sliding(                     Result:
     *       "12",                      ["12"]
     *       5
     *    )
     *    sliding(                     Result:
     *       "789",                     ["78", "89"]
     *       2
     *    )
     * </pre>
     *
     * @param sourceCS
     *    {@link CharSequence} to slide
     * @param size
     *    Size of every sublist
     *
     * @return {@link List} of {@link String}
     */
    public static List<String> sliding(final CharSequence sourceCS,
                                       final int size) {
        if (isNull(sourceCS)) {
            return new ArrayList<>();
        }
        if (1 > size ||
                size >= sourceCS.length()) {
            return asList(sourceCS.toString());
        }
        List<String> parts = new ArrayList<>();
        for (int i = 0; i < sourceCS.length() - size + 1; i++) {
            parts.add(
                    sourceCS.subSequence(
                            i,
                            i + size
                    ).toString()
            );
        }
        return parts;
    }


    /**
     * Splits the given {@link String} in substrings with a size equal to the given {@code size}
     *
     * <pre>
     *    split(                       Result:
     *       "123",                     ["123"]
     *       4
     *    )
     *    split(                       Result:
     *       "123",                     ["12", "3"]
     *       2
     *    )
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
        if (isNull(sourceString)) {
            return new ArrayList<>();
        }
        if (1 > size || size > sourceString.length()) {
            return asList(sourceString);
        }
        List<String> result = new ArrayList<>();
        for (int i = 0; i < sourceString.length(); i += size) {
            result.add(
                    sourceString.substring(
                            i,
                            Math.min(
                                    sourceString.length(),
                                    i + size
                            )
                    )
            );
        }
        return result;
    }


    /**
     *    Returns a {@link List} splitting the given {@code sourceString} in different parts, using {@code valueExtractor}
     * to know how to do it.
     *
     * @apiNote
     *    {@link StringUtil#DEFAULT_SEPARATOR_STRING} will be used to split the given {@code sourceString}.
     *
     * <pre>
     *    split(                                 Result:
     *       "1,2,3",                             [1, 2, 3]
     *       Integer::parseInt
     *    )
     * </pre>
     *
     * @param sourceString
     *    Source {@link String} with the values to extract
     * @param valueExtractor
     *    {@link Function} used to know how to split the provided {@link String}
     *
     * @return {@link List}
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> split(final String sourceString,
                                    final Function<String, ? extends T> valueExtractor) {
        return (List<T>)split(
                sourceString,
                valueExtractor,
                DEFAULT_SEPARATOR_STRING,
                ArrayList::new
        );
    }


    /**
     *    Returns a {@link List} splitting the given {@code sourceString} in different parts, using {@code valueExtractor}
     * to know how to do it.
     *
     * <pre>
     *    split(                                 Result:
     *       "1;2;3",                             [1, 2, 3]
     *       Integer::parseInt,
     *       ";"
     *    )
     * </pre>
     *
     * @apiNote
     *    If {@code separator} is {@code null} then {@link StringUtil#DEFAULT_SEPARATOR_STRING} will be used.
     *
     * @param sourceString
     *    Source {@link String} with the values to extract
     * @param valueExtractor
     *    {@link Function} used to know how to split the provided {@link String}
     * @param separator
     *    {@link String} used to know how the values are split inside {@code sourceString}
     *
     * @return {@link List}
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> split(final String sourceString,
                                    final Function<String, ? extends T> valueExtractor,
                                    final String separator) {
        return (List<T>)split(
                sourceString,
                valueExtractor,
                separator,
                ArrayList::new
        );
    }


    /**
     *    Returns a {@link Collection} splitting the given {@code sourceString} in different parts, using {@code valueExtractor}
     * to know how to do it.
     *
     * @apiNote
     *    If {@code separator} is {@code null} then {@link StringUtil#DEFAULT_SEPARATOR_STRING} will be used.
     *
     * <pre>
     *    split(                                 Result:
     *       "1;2;3",                             [1, 2, 3]
     *       Integer::parseInt,
     *       ";",
     *       ArrayList::new
     *    )
     * </pre>
     *
     * @param sourceString
     *    Source {@link String} with the values to extract
     * @param valueExtractor
     *    {@link Function} used to know how to split the provided {@link String}
     * @param separator
     *    {@link String} used to know how the values are split inside {@code sourceString}
     * @param collectionFactory
     *    {@link Supplier} of the {@link Collection} used to store the returned elements
     *
     * @return {@link Collection}
     */
    public static <T> Collection<T> split(final String sourceString,
                                          final Function<String, ? extends T> valueExtractor,
                                          final String separator,
                                          final Supplier<Collection<T>> collectionFactory) {
        return split(
                sourceString,
                separator,
                -1,
                valueExtractor,
                collectionFactory
        );
    }


    /**
     *    Returns a {@link Collection} splitting the given {@code sourceString} in different parts, using
     * {@code valueExtractor} to know how to do it.
     *
     * @apiNote
     *    If {@code separator} is {@code null} then {@link StringUtil#DEFAULT_SEPARATOR_STRING} will be used.
     *
     * <pre>
     *    split(                                 Result:
     *       "R1,  R2, R3,R3",                    ["R1", "R2", "R3"]
     *       ",",
     *       4,
     *       String::trim,
     *       ArrayList::new
     *    )
     * </pre>
     *
     * @param sourceString
     *    Source {@link String} with the values to extract
     * @param separator
     *    {@link String} used to know how the values are split inside {@code sourceString}
     * @param chunkLimit
     *    Maximum number of elements of which the given {@code sourceString} will be split
     * @param valueExtractor
     *    {@link Function} used to know how to split the provided {@link String}
     * @param collectionFactory
     *    {@link Supplier} of the {@link Collection} used to store the returned elements.
     *    If {@code null} then {@link ArrayList}
     *
     * @return {@link Collection}
     *
     * @throws Exception if there is a split part that cannot be extracted using {@code valueExtractor}.
     *
     *                   <pre>
     *                   For example:
     *                        source = "1,2,3"
     *                        chunkLimit = 2
     *                        valueExtractor = Integer::parseInt
     *
     *                   Split parts will be:
     *                        ["1", "2,3"] => second one could not be converted to an {@link Integer}
     *                   </pre>
     */
    public static <T> Collection<T> split(final String sourceString,
                                          final String separator,
                                          final int chunkLimit,
                                          final Function<String, ? extends T> valueExtractor,
                                          final Supplier<Collection<T>> collectionFactory) {
        final Supplier<Collection<T>> finalCollectionFactory = ObjectUtil.getOrElse(
                collectionFactory,
                ArrayList::new
        );
        return ofNullable(sourceString)
                .map(s -> {
                    if (isNull(valueExtractor)) {
                        return null;
                    }
                    final String[] splitString =
                            0 >= chunkLimit
                                    ? s.split(
                                            null == separator
                                                    ? DEFAULT_SEPARATOR_STRING
                                                    : separator
                                    )
                                    : s.split(
                                            null == separator
                                                    ? DEFAULT_SEPARATOR_STRING
                                                    : separator, chunkLimit
                                    );
                    Stream<T> valueExtractedStream = Stream.of(splitString)
                            .map(valueExtractor);

                    return valueExtractedStream
                            .collect(
                                    Collectors.toCollection(finalCollectionFactory)
                            );
                })
                .orElseGet(finalCollectionFactory);
    }


    /**
     *    Returns a {@link List} splitting the given {@code sourceString} in different parts, using provided
     * {@code separators} to know how to do it, iterating over each one every time.
     *
     * <pre>
     *    splitMultilevel(                       Result:
     *       "AB,C",                              ["AB", "C"]
     *       ","
     *    )
     *    splitMultilevel(                       Result:
     *       "A,B#D,E,B",                         ["A", "B", "D", "E", "B"]
     *       "#",
     *       ","
     *    )
     * </pre>
     *
     * @param sourceString
     *    Source {@link String} with the values to extract
     * @param separators
     *    Array used to know how the values are split inside {@code sourceString}
     *
     * @return {@link Collection}
     */
    public static List<String> splitMultilevel(final String sourceString,
                                               final String ...separators) {
        return (List<String>) splitMultilevel(
                sourceString,
                ArrayList::new,
                separators
        );
    }


    /**
     *    Returns a {@link List} splitting the given {@code sourceString} in different parts, using provided
     * {@code separators} to know how to do it, iterating over each one every time.
     *
     * <pre>
     *    splitMultilevel(                       Result:
     *       "AB,C",                              ["AB", "C"]
     *       ArrayList::new,
     *       ","
     *    )
     *    splitMultilevel(                       Result:
     *       "A,B#D,E,B",                         ["A", "B", "D", "E", "B"]
     *       LinkedHashSet::new,
     *       "#",
     *       ","
     *    )
     * </pre>
     *
     * @param sourceString
     *    Source {@link String} with the values to extract
     * @param collectionFactory
     *    {@link Supplier} of the {@link Collection} used to store the returned elements.
     *    If {@code null} then {@link ArrayList}
     * @param separators
     *    Array used to know how the values are split inside {@code sourceString}
     *
     * @return {@link Collection}
     */
    public static Collection<String> splitMultilevel(final String sourceString,
                                                     final Supplier<Collection<String>> collectionFactory,
                                                     final String ...separators) {
        final Supplier<Collection<String>> finalCollectionFactory = ObjectUtil.getOrElse(
                collectionFactory,
                ArrayList::new
        );
        return ofNullable(sourceString)
                .map(s -> {
                    Collection<String> result = finalCollectionFactory.get();
                    if (isNull(separators)) {
                        result.add(sourceString);
                        return result;
                    }
                    List<String> currentSplitValues = asList(sourceString);
                    for (int i = 0; i < separators.length; i++) {
                        int finalI = i;
                        currentSplitValues = currentSplitValues
                                .stream()
                                .flatMap(elto ->
                                        Arrays.stream(
                                                elto.split(
                                                        Pattern.quote(separators[finalI])
                                                )
                                        )
                                )
                                .toList();
                    }
                    result.addAll(currentSplitValues);
                    return result;
                })
                .orElseGet(finalCollectionFactory);
    }


    /**
     *    Returns the substring of {@code sourceCS} after the first occurrence of a {@code separator}. {@code separator}
     * is not returned.
     * <p>
     *    The following are special use cases:</p>
     *    <ul>
     *      <li>If {@code sourceCS} is {@code null} or empty then empty {@link String} is returned</li>
     *      <li>If {@code separator} is {@code null} then empty {@link String} is returned</li>
     *      <li>If {@code separator} is empty then {@link String} conversion of {@code sourceCS} is returned</li>
     *      <li>If nothing is found, empty {@link String} is returned</li>
     *    </ul>
     * <p>
     * Examples:
     * <pre>
     *    substringAfter(null, *)      = ""
     *    substringAfter("", *)        = ""
     *    substringAfter(*, null)      = ""
     *    substringAfter("abc", "")    = "abc"
     *    substringAfter("abc", "z")   = ""
     *    substringAfter("abc", "a")   = "bd"
     *    substringAfter("abc", "c")   = ""
     *    substringAfter("abcb", "b")  = "cb"
     * </pre>
     *
     * @param sourceCS
     *    {@link CharSequence} to get a substring from
     * @param separator
     *     {@link String} to search for
     *
     * @return the substring after the first occurrence of the {@code separator},
     *         empty {@link String} if {@code sourceCS} is {@code null} or empty
     */
    public static String substringAfter(final CharSequence sourceCS,
                                        final String separator) {
        if (isNull(sourceCS) || isNull(separator)) {
            return EMPTY_STRING;
        }
        final String sourceCSToString = sourceCS.toString();
        final int pos = sourceCSToString.indexOf(separator);
        return INDEX_NOT_FOUND == pos
                ? EMPTY_STRING
                : sourceCSToString.substring(pos + separator.length());
    }


    /**
     *    Returns the substring of {@code sourceCS} after the last occurrence of a {@code separator}. {@code separator}
     * is not returned.
     * <p>
     *    The following are special use cases:</p>
     *    <ul>
     *      <li>If {@code sourceCS} is {@code null} or empty then empty {@link String} is returned</li>
     *      <li>If {@code separator} is {@code null} or empty then empty {@link String} is returned</li>
     *      <li>If nothing is found, empty {@link String} is returned</li>
     *    </ul>
     * <p>
     * Examples:
     * <pre>
     *    substringAfterLast(null, *)       = ""
     *    substringAfterLast("", *)         = ""
     *    substringAfterLast(*, null)       = ""
     *    substringAfterLast(*, "")         = ""
     *    substringAfterLast("abc", "z")    = ""
     *    substringAfterLast("abc", "a")    = "bd"
     *    substringAfterLast("abc", "c")    = ""
     *    substringAfterLast("abcba", "b")  = "a"
     * </pre>
     *
     * @param sourceCS
     *    {@link CharSequence} to get a substring from
     * @param separator
     *     {@link String} to search for
     *
     * @return the substring after the last occurrence of the {@code separator},
     *         empty {@link String} if {@code sourceCS} is {@code null} or empty
     */
    public static String substringAfterLast(final CharSequence sourceCS,
                                            final String separator) {
        if (isEmpty(sourceCS) || isEmpty(separator)) {
            return EMPTY_STRING;
        }
        final String sourceCSToString = sourceCS.toString();
        final int pos = sourceCSToString.lastIndexOf(separator);
        if (pos == INDEX_NOT_FOUND ||
                pos == sourceCSToString.length() - separator.length()) {
            return EMPTY_STRING;
        }
        return sourceCSToString.substring(pos + separator.length());
    }


    /**
     *    Returns the substring of {@code sourceCS} before the first occurrence of a {@code separator}. {@code separator}
     * is not returned.
     * <p>
     *    The following are special use cases:</p>
     *    <ul>
     *      <li>If {@code sourceCS} is {@code null} or empty then empty {@link String} is returned</li>
     *      <li>If {@code separator} is {@code null} or empty then {@link String} conversion of {@code sourceCS} is returned</li>
     *      <li>If nothing is found, {@link String} conversion of {@code sourceCS} is returned</li>
     *    </ul>
     * <p>
     * Examples:
     * <pre>
     *    substringBefore(null, *)      = ""
     *    substringBefore("", *)        = ""
     *    substringBefore("abc", null)  = "abc"
     *    substringBefore("abc", "")    = "abc"
     *    substringBefore("a", "a")     = ""
     *    substringBefore("a", "z")     = "a"
     *    substringBefore("abc", "c")   = "ab"
     *    substringBefore("abcb", "b")  = "a"
     * </pre>
     *
     * @param sourceCS
     *    {@link CharSequence} to get a substring from
     * @param separator
     *     {@link String} to search for
     *
     * @return the substring before the first occurrence of the {@code separator},
     *         empty {@link String} if {@code sourceCS} is {@code null} or empty
     */
    public static String substringBefore(final CharSequence sourceCS,
                                         final String separator) {
        return ofNullable(sourceCS)
                .map(CharSequence::toString)
                .map(source -> {
                    if (isEmpty(separator)) {
                        return source;
                    }
                    final int pos = source.indexOf(separator);
                    return INDEX_NOT_FOUND == pos
                            ? source
                            : source.substring(0, pos);
                })
                .orElse(EMPTY_STRING);
    }


    /**
     *    Returns the substring of {@code sourceCS} before the last occurrence of a {@code separator}. {@code separator}
     * is not returned.
     * <p>
     *    The following are special use cases:</p>
     *    <ul>
     *      <li>If {@code sourceCS} is {@code null} or empty then empty {@link String} is returned</li>
     *      <li>If {@code separator} is {@code null} or empty then {@link String} conversion of {@code sourceCS} is returned</li>
     *      <li>If nothing is found, {@link String} conversion of {@code sourceCS} is returned</li>
     *    </ul>
     * <p>
     * Examples:
     * <pre>
     *    substringBeforeLast(null, *)      = ""
     *    substringBeforeLast("", *)        = ""
     *    substringBeforeLast("abc", null)  = "abc"
     *    substringBeforeLast("abc", "")    = "abc"
     *    substringBeforeLast("a", "a")     = ""
     *    substringBeforeLast("a", "z")     = "a"
     *    substringBeforeLast("abc", "c")   = "ab"
     *    substringBeforeLast("abcb", "b")  = "abc"
     * </pre>
     *
     * @param sourceCS
     *    {@link CharSequence} to get a substring from
     * @param separator
     *     {@link String} to search for
     *
     * @return the substring before the last occurrence of the {@code separator},
     *         empty {@link String} if {@code sourceCS} is {@code null} or empty
     */
    public static String substringBeforeLast(final CharSequence sourceCS,
                                             final String separator) {
        return ofNullable(sourceCS)
                .map(CharSequence::toString)
                .map(source -> {
                    if (isEmpty(separator)) {
                        return source;
                    }
                    final int pos = source.lastIndexOf(separator);
                    return INDEX_NOT_FOUND == pos
                            ? source
                            : source.substring(0, pos);
                })
                .orElse(EMPTY_STRING);
    }


    /**
     *    Returns a {@link String} with the longest prefix of {@link Character}s included in {@code sourceCS} that
     * satisfy the {@link Predicate} {@code filterPredicate}.
     *
     * @apiNote
     *    If {@code sourceCS} is {@code null} or empty then {@link StringUtil#EMPTY_STRING} is returned.
     * If {@code filterPredicate} is {@code null} then {@link String} conversion of {@code sourceCS} is returned.
     *
     * <pre>
     *    takeWhile(                                       Result:
     *       "aEibc12",                                     "aEi"
     *       c -> -1 != "aeiouAEIOU".indexOf(c)
     *    )
     * </pre>
     *
     * @param sourceCS
     *    {@link CharSequence} with the {@link Character}s to filter
     * @param filterPredicate
     *    {@link Predicate} to filter {@link Character}s from {@code sourceCS}
     *
     * @return the longest prefix of provided {@code sourceCS} whose {@link Character}s all satisfy {@code filterPredicate}
     */
    public static String takeWhile(final CharSequence sourceCS,
                                   final Predicate<Character> filterPredicate) {
        if (isEmpty(sourceCS)) {
            return EMPTY_STRING;
        }
        if (isNull(filterPredicate)) {
            return new String(
                    sourceCS.toString()
            );
        }
        return sourceCS
                .codePoints()
                .takeWhile(c ->
                        filterPredicate.test((char) c)
                )
                .mapToObj(i ->
                        Character.valueOf((char) i).toString()
                )
                .collect(
                        joining()
                );
    }

}
