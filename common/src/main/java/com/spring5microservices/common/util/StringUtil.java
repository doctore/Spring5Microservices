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
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.spring5microservices.common.util.CollectionUtil.toList;
import static com.spring5microservices.common.util.PredicateUtil.alwaysTrue;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static org.springframework.util.StringUtils.hasLength;

@UtilityClass
public class StringUtil {

    private final String BLANK_SPACE = " ";

    private final String DEFAULT_ABBREVIATION_STRING = "...";

    private final String DEFAULT_SEPARATOR_STRING = ",";

    private final String EMPTY_STRING = "";


    /**
     * Abbreviates the given {@code sourceString} using provided {@link StringUtil#DEFAULT_ABBREVIATION_STRING} as replacement marker.
     * <p>
     *    The following use cases will not return the expected replaced {@link String}:</p>
     *    <ul>
     *      <li>If {@code sourceString} is {@code null} or empty then empty {@link String} will be returned</li>
     *      <li>If {@code maxLength} is less than or equal to 0 then empty {@link String} will be returned</li>
     *      <li>If {@code maxLength} is greater than or equal to {@code sourceString}'s length then {@code sourceString} will be returned</li>
     *    </ul>
     * <p>
     *    If {@code maxLength} is less than the first character of {@code sourceString} and {@link StringUtil#DEFAULT_ABBREVIATION_STRING}'s
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
     * @param sourceString
     *    {@link String} to abbreviate
     * @param maxLength
     *    Max size of the returned {@link String}. If it is less than 0 then 0 will be used
     *
     * @return the abbreviated {@link String} if {@code maxLength} is greater than {@code sourceString}'s length,
     *         {@code sourceString} otherwise
     *
     * @throws IllegalArgumentException if {@code maxLength} is less than the first character of {@code sourceString}
     *                                  and {@link StringUtil#DEFAULT_ABBREVIATION_STRING}'s length
     */
    public static String abbreviate(final String sourceString,
                                    final int maxLength) {
        return abbreviate(
                sourceString,
                maxLength,
                DEFAULT_ABBREVIATION_STRING
        );
    }


    /**
     * Abbreviates the given {@code sourceString} using provided {@code abbreviationString} as replacement marker.
     * <p>
     *    The following use cases will not return the expected replaced {@link String}:</p>
     *    <ul>
     *      <li>If {@code sourceString} is {@code null} or empty then empty {@link String} will be returned</li>
     *      <li>If {@code maxLength} is less than or equal to 0 then empty {@link String} will be returned</li>
     *      <li>If {@code maxLength} is greater than or equal to {@code sourceString}'s length then {@code sourceString} will be returned</li>
     *    </ul>
     * <p>
     *    If {@code maxLength} is less than the first character of {@code sourceString} and {@code abbreviationString}'s
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
     * @param sourceString
     *    {@link String} to abbreviate
     * @param maxLength
     *    Max size of the returned {@link String}. If it is less than 0 then 0 will be used
     * @param abbreviationString
     *    {@link String} to replace the middle characters. Default value will be {@link StringUtil#DEFAULT_ABBREVIATION_STRING}
     *
     * @return the abbreviated {@link String} if {@code maxLength} is greater than {@code sourceString}'s length,
     *         {@code sourceString} otherwise
     *
     * @throws IllegalArgumentException if {@code maxLength} is less than the first character of {@code sourceString}
     *                                  and {@code abbreviationString}'s length
     */
    public static String abbreviate(final String sourceString,
                                    final int maxLength,
                                    final String abbreviationString) {
        if (isEmpty(sourceString) ||
                0 >= maxLength) {
            return EMPTY_STRING;
        }
        if (sourceString.length() <= maxLength) {
            return sourceString;
        }
        final String finalAbbreviationString = ObjectUtil.getOrElse(
                abbreviationString,
                DEFAULT_ABBREVIATION_STRING
        );
        Assert.isTrue(
                maxLength >= (finalAbbreviationString.length() + 1),
                format("Provided maxLength: %s is not enough to abbreviate at least first character of given sourceString: %s using abbreviationString: %s",
                        maxLength,
                        sourceString,
                        finalAbbreviationString
                )
        );
        final int startOffset = maxLength - finalAbbreviationString.length();

        return sourceString.substring(0, startOffset)
                + finalAbbreviationString;
    }


    /**
     *    Abbreviates the given {@code sourceString} to the length passed, replacing the middle characters with
     * {@link StringUtil#DEFAULT_ABBREVIATION_STRING}.
     * <p>
     *    The following use cases will not return the expected replaced {@link String}:</p>
     *    <ul>
     *      <li>If {@code sourceString} is {@code null} or empty then empty {@link String} will be returned</li>
     *      <li>If {@code maxLength} is less than or equal to 0 then empty {@link String} will be returned</li>
     *      <li>If {@code maxLength} is greater than or equal to {@code sourceString}'s length then {@code sourceString} will be returned</li>
     *    </ul>
     * <p>
     *    If {@code maxLength} is less than the first and last characters of {@code sourceString} and {@link StringUtil#DEFAULT_ABBREVIATION_STRING}'s
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
     * @param sourceString
     *    {@link String} to abbreviate
     * @param maxLength
     *    Max size of the returned {@link String}. If it is less than 0 then 0 will be used
     *
     * @return the abbreviated {@link String} if {@code maxLength} is greater than {@code sourceString}'s length,
     *         {@code sourceString} otherwise
     *
     * @throws IllegalArgumentException if {@code maxLength} is less than the first and last characters of {@code sourceString}
     *                                  and {@link StringUtil#DEFAULT_ABBREVIATION_STRING}'s length
     */
    public static String abbreviateMiddle(final String sourceString,
                                          final int maxLength) {
        return abbreviateMiddle(
          sourceString,
          maxLength,
          DEFAULT_ABBREVIATION_STRING
        );
    }


    /**
     *    Abbreviates the given {@code sourceString} to the length passed, replacing the middle characters with the supplied
     * {@code abbreviationString}.
     * <p>
     *    The following use cases will not return the expected replaced {@link String}:</p>
     *    <ul>
     *      <li>If {@code sourceString} is {@code null} or empty then empty {@link String} will be returned</li>
     *      <li>If {@code maxLength} is less than or equal to 0 then empty {@link String} will be returned</li>
     *      <li>If {@code maxLength} is greater than or equal to {@code sourceString}'s length then {@code sourceString} will be returned</li>
     *    </ul>
     * <p>
     *    If {@code maxLength} is less than the first and last characters of {@code sourceString} and {@code abbreviationString}'s
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
     * @param sourceString
     *    {@link String} to abbreviate
     * @param maxLength
     *    Max size of the returned {@link String}. If it is less than 0 then 0 will be used
     * @param abbreviationString
     *    {@link String} to replace the middle characters. Default value will be {@link StringUtil#DEFAULT_ABBREVIATION_STRING}
     *
     * @return the abbreviated {@link String} if {@code maxLength} is greater than {@code sourceString}'s length,
     *         {@code sourceString} otherwise
     *
     * @throws IllegalArgumentException if {@code maxLength} is less than the first and last characters of {@code sourceString}
     *                                  and {@code abbreviationString}'s length
     */
    public static String abbreviateMiddle(final String sourceString,
                                          final int maxLength,
                                          final String abbreviationString) {
        if (isEmpty(sourceString) ||
                0 >= maxLength) {
            return EMPTY_STRING;
        }
        if (sourceString.length() <= maxLength) {
            return sourceString;
        }
        final String finalAbbreviationString = ObjectUtil.getOrElse(
                abbreviationString,
                DEFAULT_ABBREVIATION_STRING
        );
        Assert.isTrue(
                maxLength >= (finalAbbreviationString.length() + 2),
                format("Provided maxLength: %s is not enough to abbreviate at least first and last character of given sourceString: %s using abbreviationString: %s",
                        maxLength,
                        sourceString,
                        finalAbbreviationString
                )
        );
        final int sizeOfDisplayedSourceString = maxLength - finalAbbreviationString.length();
        final int startOffset = (sizeOfDisplayedSourceString / 2) + (sizeOfDisplayedSourceString % 2);
        final int endOffset = sourceString.length() - (sizeOfDisplayedSourceString / 2);

        return sourceString.substring(0, startOffset)
                + finalAbbreviationString
                + sourceString.substring(endOffset);
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
        if (isNull(sourceString) || isNull(stringToSearch)) {
            return false;
        }
        return sourceString.toLowerCase()
                .contains(stringToSearch.toLowerCase());
    }


    /**
     * Returns the substring of {@code sourceString} after the last occurrence of a {@code stringToFind}.
     *
     * <pre>
     *    getBeforeLastIndexOf(                  Result:
     *       "1234-9-56",                         Optional("1234")
     *       "-9"
     *    )
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
                    final int lastIndex = ss.lastIndexOf(stringToFind);
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
     *    getBeforeLastIndexOf(                  Result:
     *       "1234-9-56",                         "1234"
     *       "-9",
     *       "654"
     *    )
     *    getBeforeLastIndexOf(                  Result:
     *       "1234-9-56",                         "1234-9-56"
     *       "88",
     *       "654"
     *    )
     *    getBeforeLastIndexOf(                  Result:
     *       "1234",                              "999"
     *       "1234",
     *       "999"
     *    )
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
     * Returns a new {@link String} removing from the given {@code sourceString} all non-numeric characters.
     *
     * @param sourceString
     *    {@link String} to get all non-numeric characters.
     *
     * @return new {@link String} without non-numeric characters.
     */
    public static String getDigits(final String sourceString) {
        if (isEmpty(sourceString)) {
            return EMPTY_STRING;
        }
        final int sourceStringLength = sourceString.length();
        final StringBuilder sourceStringDigits = new StringBuilder(EMPTY_STRING);
        for (int i = 0; i < sourceStringLength; i++) {
            final char tempChar = sourceString.charAt(i);
            if (Character.isDigit(tempChar)) {
                sourceStringDigits.append(tempChar);
            }
        }
        return sourceStringDigits.toString();
    }


    /**
     * Return the given {@code sourceString} if is not {@code null}. Otherwise, returns {@code defaultValue}.
     *
     * @param sourceString
     *    {@link String} returned only if is not {@code null}
     * @param defaultValue
     *    Alternative value to return
     *
     * @return {@code sourceString} if is not {@code null}, {@code defaultValue} otherwise
     */
    public static String getOrElse(final String sourceString,
                                   final String defaultValue) {
        return ofNullable(sourceString)
                .orElse(defaultValue);
    }


    /**
     *    Return the given {@code sourceString} if is not {@code null} and verifies {@code predicateToMatch}.
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
     * @param sourceString
     *    {@link String} returned only if is not {@code null}
     * @param predicateToMatch
     *    {@link Predicate} to apply if {@code sourceInstance} is not {@code null}
     * @param defaultValue
     *    Alternative value to return
     *
     * @return {@code sourceString} if is not {@code null} and verifies {@code predicateToMatch},
     *         {@code defaultValue} otherwise
     */
    public static String getOrElse(final String sourceString,
                                   final Predicate<String> predicateToMatch,
                                   final String defaultValue) {
        final Predicate<String> finalPredicateToMatch = ObjectUtil.getOrElse(
                predicateToMatch,
                alwaysTrue()
        );
        return ofNullable(sourceString)
                .filter(finalPredicateToMatch)
                .orElse(defaultValue);
    }


    /**
     * Return the given {@code sourceString} if is not {@code null}. Otherwise, returns an empty {@link String}.
     *
     * @param sourceString
     *    {@link String} returned only if is not {@code null}
     *
     * @return {@code sourceString} if is not {@code null}, empty {@link String} otherwise
     */
    public static String getOrEmpty(final String sourceString) {
        return getOrElse(
                sourceString,
                EMPTY_STRING
        );
    }


    /**
     *    Abbreviates the given {@code sourceString} to the length passed, replacing the middle characters with
     * {@link StringUtil#DEFAULT_ABBREVIATION_STRING}.
     * <p>
     * The following use cases will not return the expected replaced {@link String}:
     *    <ul>
     *      <li>If {@code sourceString} is {@code null} or empty then empty {@link String} will be returned</li>
     *      <li>If {@code maxLength} is less than or equal to 0 then empty {@link String} will be returned</li>
     *    </ul>
     * <p>
     *    If {@code maxLength} is less than the first and last characters of {@code sourceString} and {@link StringUtil#DEFAULT_ABBREVIATION_STRING}'s
     * length, then an {@link IllegalArgumentException} will be thrown.
     * <p>
     *    {@link StringUtil#abbreviateMiddle(String, int)} returns {@code sourceString} when {@code maxLength} is greater
     * than or equals to {@code sourceString}'s length however, the current function always tries to hide middle characters
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
     * @param sourceString
     *    {@link String} to abbreviate
     * @param maxLength
     *    Max size of the returned {@link String}. If it is less than 0 then 0 will be used
     *
     * @return the abbreviated {@link String} if {@code maxLength} is greater than 2,
     *         {@code sourceString} otherwise
     *
     * @throws IllegalArgumentException if {@code maxLength} is less than the first and last characters of {@code sourceString}
     *                                  and {@link StringUtil#DEFAULT_ABBREVIATION_STRING}'s length
     */
    public static String hideMiddle(final String sourceString,
                                    final int maxLength) {
        return hideMiddle(
                sourceString,
                maxLength,
                DEFAULT_ABBREVIATION_STRING
        );
    }


    /**
     *    Abbreviates the given {@code sourceString} to the length passed, replacing the middle characters with the supplied
     * {@code abbreviationString}.
     * <p>
     * The following use cases will not return the expected replaced {@link String}:
     *    <ul>
     *      <li>If {@code sourceString} is {@code null} or empty then empty {@link String} will be returned</li>
     *      <li>If {@code maxLength} is less than or equal to 0 then empty {@link String} will be returned</li>
     *    </ul>
     * <p>
     *    If {@code maxLength} is less than the first and last characters of {@code sourceString} and {@code abbreviationString}'s
     * length, then an {@link IllegalArgumentException} will be thrown.
     * <p>
     *    {@link StringUtil#abbreviateMiddle(String, int, String)} returns {@code sourceString} when {@code maxLength}
     * is greater than or equals to {@code sourceString}'s length however, the current function always tries to hide middle
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
     * @param sourceString
     *    {@link String} to abbreviate
     * @param maxLength
     *    Max size of the returned {@link String}. If it is less than 0 then 0 will be used
     * @param abbreviationString
     *    {@link String} to replace the middle characters. Default value will be {@link StringUtil#DEFAULT_ABBREVIATION_STRING}
     *
     * @return the abbreviated {@link String} if {@code maxLength} is greater than 2,
     *         {@code sourceString} otherwise
     *
     * @throws IllegalArgumentException if {@code maxLength} is less than the first and last characters of {@code sourceString}
     *                                  and {@code abbreviationString}'s length
     */
    public static String hideMiddle(final String sourceString,
                                    final int maxLength,
                                    final String abbreviationString) {
        if (isEmpty(sourceString) ||
                0 >= maxLength) {
            return EMPTY_STRING;
        }
        if (2 >= sourceString.length()) {
            return sourceString;
        }
        final String finalAbbreviationString = ObjectUtil.getOrElse(
                abbreviationString,
                DEFAULT_ABBREVIATION_STRING
        );
        Assert.isTrue(
                maxLength >= (finalAbbreviationString.length() + 2),
                format("Provided maxLength: %s is not enough to abbreviate at least first and last character of given sourceString: %s using abbreviationString: %s",
                        maxLength,
                        sourceString,
                        finalAbbreviationString
                )
        );
        final int sizeOfDisplayedSourceString = maxLength < sourceString.length()
                ? maxLength - finalAbbreviationString.length()
                : sourceString.length() - finalAbbreviationString.length();

        final int startOffset = (sizeOfDisplayedSourceString / 2) + (sizeOfDisplayedSourceString % 2);
        final int endOffset = sourceString.length() - (sizeOfDisplayedSourceString / 2);

        return sourceString.substring(0, startOffset)
                + finalAbbreviationString
                + sourceString.substring(endOffset);
    }


    /**
     * Checks if the given {@code sourceString} is {@code null}, an empty {@link String} ('') or whitespace.
     *
     * <pre>
     *    isBlank(null)    = true
     *    isBlank("")      = true
     *    isBlank("   ")   = true
     *    isBlank("  a ")  = false
     * </pre>
     *
     * @param sourceString
     *    {@link String} to verify
     *
     * @return {@code true} if {@code sourceString} is {@code null} or has no characters
     */
    public static boolean isBlank(final String sourceString) {
        if (isEmpty(sourceString)) {
            return true;
        }
        return IntStream.range(0, sourceString.length())
                .allMatch(i ->
                        Character.isWhitespace(
                                sourceString.charAt(i)
                        )
                );
    }


    /**
     * Checks if the given {@code sourceString} is {@code null} or an empty {@link String} ('').
     *
     * <pre>
     *    isEmpty(null)    = true
     *    isEmpty("")      = true
     *    isEmpty("   ")   = false
     *    isEmpty("  a ")  = false
     * </pre>
     *
     * @param sourceString
     *    {@link String} to verify
     *
     * @return {@code true} if {@code sourceString} is {@code null} or has no characters
     */
    public static boolean isEmpty(final String sourceString) {
        return sourceString == null ||
                sourceString.isEmpty();
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
                                    Collectors.joining(finalSeparator)
                            );
                })
                .orElse(EMPTY_STRING);
    }


    /**
     * Left pad the {@link String} {@code sourceString} with spaces (' ') up to the provided {@code size}.
     *
     * @apiNote
     *    If {@code size} is less than 0 then 0 will be used.
     *
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
     * @param sourceString
     *    {@link String} to pad out
     * @param size
     *    The size to pad to
     *
     * @return left padded {@link String} or {@code sourceString} if no padding is necessary
     */
    public static String leftPad(final String sourceString,
                                 final int size) {
        return leftPad(
                sourceString,
                size,
                BLANK_SPACE
        );
    }


    /**
     * Left pad the {@link String} {@code sourceString} with {@code padString} up to the provided {@code size}.
     *
     * @apiNote
     *    If {@code size} is less than 0 then 0 will be used. If {@code padString} is {@code null} then
     * {@link StringUtil#BLANK_SPACE} will be used.
     *
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
     * @param sourceString
     *    {@link String} to pad out
     * @param size
     *    The size to pad to
     * @param padString
     *    {@link String} to pad with, {@code null} or empty treated as {@link StringUtil#BLANK_SPACE}
     *
     * @return left padded {@link String} or {@code sourceString} if no padding is necessary
     */
    public static String leftPad(final String sourceString,
                                 final int size,
                                 final String padString) {
        final String finalSourceString = isNull(sourceString)
                ? EMPTY_STRING
                : sourceString;

        final int finalSize = Math.max(0, size);

        final String finalPadString =
                isNull(padString)
                        ? BLANK_SPACE
                        : padString;

        final int sourceStringLength = finalSourceString.length();
        final int padStringLength = finalPadString.length();
        final int pads = finalSize - sourceStringLength;

        // Returns original sourceString when possible
        if (0 >= pads) {
            return finalSourceString;
        }
        if (pads == padStringLength) {
            return finalPadString.concat(finalSourceString);
        }
        if (pads < padStringLength) {
            return finalPadString.substring(0, pads)
                    .concat(finalSourceString);
        }
        final char[] padding = new char[pads];
        final char[] padChars = finalPadString.toCharArray();
        for (int i = 0; i < pads; i++) {
            padding[i] = padChars[i % padStringLength];
        }
        return new String(padding)
                .concat(finalSourceString);
    }


    /**
     * Right pad the {@link String} {@code sourceString} with spaces (' ') up to the provided {@code size}.
     *
     * @apiNote
     *    If {@code size} is less than 0 then 0 will be used.
     *
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
     * @param sourceString
     *    {@link String} to pad out
     * @param size
     *    The size to pad to
     *
     * @return right padded {@link String} or {@code sourceString} if no padding is necessary
     */
    public static String rightPad(final String sourceString,
                                  final int size) {
        return rightPad(
                sourceString,
                size,
                BLANK_SPACE
        );
    }


    /**
     * Right pad the {@link String} {@code sourceString} with {@code padString} up to the provided {@code size}.
     *
     * @apiNote
     *    If {@code size} is less than 0 then 0 will be used. If {@code padString} is {@code null} then
     * {@link StringUtil#BLANK_SPACE} will be used.
     *
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
     * @param sourceString
     *    {@link String} to pad out
     * @param size
     *    The size to pad to
     * @param padString
     *    {@link String} to pad with, {@code null} or empty treated as {@link StringUtil#BLANK_SPACE}
     *
     * @return right padded {@link String} or {@code sourceString} if no padding is necessary
     */
    public static String rightPad(final String sourceString,
                                  final int size,
                                  final String padString) {
        final String finalSourceString = isNull(sourceString)
                ? EMPTY_STRING
                : sourceString;

        final int finalSize = Math.max(0, size);

        final String finalPadString =
                isNull(padString)
                        ? BLANK_SPACE
                        : padString;

        final int sourceStringLength = finalSourceString.length();
        final int padStringLength = finalPadString.length();
        final int pads = finalSize - sourceStringLength;

        // Returns original sourceString when possible
        if (0 >= pads) {
            return finalSourceString;
        }
        if (pads == padStringLength) {
            return finalSourceString.concat(finalPadString);
        }
        if (pads < padStringLength) {
            return finalSourceString.concat(
                    finalPadString.substring(0, pads)
            );
        }
        final char[] padding = new char[pads];
        final char[] padChars = finalPadString.toCharArray();
        for (int i = 0; i < pads; i++) {
            padding[i] = padChars[i % padStringLength];
        }
        return finalSourceString.concat(
                new String(padding)
        );
    }


    /**
     * Loops through the provided {@link String} one position every time, returning sublists with {@code size}.
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
     * @param sourceString
     *    {@link String} to slide
     * @param size
     *    Size of every sublist
     *
     * @return {@link List} of {@link String}
     */
    public static List<String> sliding(final String sourceString,
                                       final int size) {
        if (isNull(sourceString)) {
            return new ArrayList<>();
        }
        if (1 > size ||
                size >= sourceString.length()) {
            return asList(sourceString);
        }
        List<String> parts = new ArrayList<>();
        for (int i = 0; i < sourceString.length() - size + 1; i++) {
            parts.add(
                    sourceString.substring(
                            i,
                            i + size
                    )
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

}
