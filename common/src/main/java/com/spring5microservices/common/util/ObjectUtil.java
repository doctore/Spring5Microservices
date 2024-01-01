package com.spring5microservices.common.util;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.spring5microservices.common.util.PredicateUtil.alwaysTrue;
import static java.util.Optional.ofNullable;

@UtilityClass
public class ObjectUtil {

    /**
     * Returns the first not {@code null} value of the provided ones.
     *
     * <pre>
     *    coalesce(                      Result:
     *       null,                        Optional(12)
     *       12,
     *       15
     *    )
     * </pre>
     *
     * @param valuesToVerify
     *    Values to check the first not {@code null} one
     *
     * @return {@link Optional} containing the first not {@code null} value included in {@code valuesToVerify},
     *         {@link Optional#empty()} otherwise.
     */
    @SafeVarargs
    public static <T> Optional<T> coalesce(T ...valuesToVerify) {
        return ofNullable(valuesToVerify)
                .flatMap(values ->
                        Arrays.stream(values)
                                .filter(Objects::nonNull)
                                .findFirst()
                );
    }


    /**
     * Determine whether the given {@code sourceArray} is empty: i.e. {@code null} or of zero length.
     *
     * @param sourceArray
     *    The array to check
     *
     * @return {@code true} if given {@code sourceArray} is {@code null} or has no elements,
     *         {@code false} otherwise.
     */
    public static boolean isEmpty(final Object[] sourceArray) {
        return null == sourceArray ||
                0 == sourceArray.length;
    }


    /**
     * Return the given {@code sourceInstance} if is not {@code null}. Otherwise, returns {@code defaultValue}.
     *
     * @param sourceInstance
     *    Object returned only if is not {@code null}
     * @param defaultValue
     *    Alternative value to return
     *
     * @return {@code sourceInstance} if is not {@code null}, {@code defaultValue} otherwise
     */
    public static <T> T getOrElse(final T sourceInstance,
                                  final T defaultValue) {
        return ofNullable(sourceInstance)
                .orElse(defaultValue);
    }


    /**
     *    Return the given {@code sourceInstance} if is not {@code null} and verifies {@code predicateToMatch}.
     * Otherwise, returns {@code defaultValue}.
     *
     * <pre>
     *    getOrElse(                                  Result:
     *       "   ",                                    "other"
     *       s -> s.trim().size() > 0,
     *       "other"
     *    )
     * </pre>
     *
     * @param sourceInstance
     *    Object returned only if is not {@code null}
     * @param predicateToMatch
     *    {@link Predicate} to apply if {@code sourceInstance} is not {@code null}
     * @param defaultValue
     *    Alternative value to return
     *
     * @return {@code sourceInstance} if is not {@code null} and verifies {@code predicateToMatch},
     *         {@code defaultValue} otherwise
     */
    public static <T> T getOrElse(final T sourceInstance,
                                  final Predicate<? super T> predicateToMatch,
                                  final T defaultValue) {
        final Predicate<? super T> finalPredicateToMatch = getOrElse(
                predicateToMatch,
                alwaysTrue()
        );
        return ofNullable(sourceInstance)
                .filter(finalPredicateToMatch)
                .orElse(defaultValue);
    }


    /**
     *    Using the provided {@link Function} {@code mapper}, transform/extract from the given {@code sourceInstance}
     * the related value. Otherwise, returns {@code defaultValue}.
     *
     * <pre>
     *    getOrElse(                     Result:
     *       23,                          "23"
     *       Object::toString,
     *       "other"
     *    )
     * </pre>
     *
     * @param sourceInstance
     *    Object used to transform/extract required information.
     * @param mapper
     *    A mapping {@link Function} to use required information from {@code sourceInstance}
     * @param defaultValue
     *    Returned value if applying {@code mapper} no value is obtained.
     *
     * @return {@code mapper} {@code apply} method if not {@code null} is returned,
     *         {@code defaultValue} otherwise.
     */
    public static <T, E> E getOrElse(final T sourceInstance,
                                     final Function<? super T, ? extends E> mapper,
                                     final E defaultValue) {
        return ofNullable(sourceInstance)
                .map(si ->
                        null == mapper
                                ? defaultValue
                                : mapper.apply(sourceInstance))
                .orElse(defaultValue);
    }


    /**
     *    Return the {@link String} representation of the given {@code sourceInstance} if is not {@code null}.
     * Otherwise, returns {@code defaultValue}.
     *
     * @param sourceInstance
     *    Object returned only if is not {@code null}
     * @param defaultValue
     *    Alternative value to return
     *
     * @return {@link String} representation of {@code sourceInstance} if is not {@code null},
     *         {@code defaultValue} otherwise
     */
    public static <T> String getOrElse(final T sourceInstance,
                                       final String defaultValue) {
        return ofNullable(sourceInstance)
                .map(Object::toString)
                .orElse(defaultValue);
    }


    /**
     *    Using the provided {@link Function} {@code mapper}, transform/extract from the given {@code sourceInstance}
     * the related value, returning its {@link String} representation. Otherwise, returns {@code defaultValue}.
     *
     * <pre>
     *    getOrElse(                     Result:
     *       23,                          "24"
     *       i -> i + 1,
     *       "other"
     *    )
     * </pre>
     *
     * @param sourceInstance
     *    Object used to transform/extract required information.
     * @param mapper
     *    A mapping {@link Function} to use required information from {@code sourceInstance}
     * @param defaultValue
     *    Returned value if applying {@code mapper} no value is obtained.
     *
     * @return {@code mapper} {@code apply} method if not {@code null} is returned,
     *         {@code defaultValue} otherwise.
     */
    public static <T, E> String getOrElse(final T sourceInstance,
                                          final Function<? super T, ? extends E> mapper,
                                          final String defaultValue) {
        return ofNullable(sourceInstance)
                .map(si ->
                        null == mapper
                                ? defaultValue
                                : mapper.apply(sourceInstance))
                .map(Object::toString)
                .orElse(defaultValue);
    }

}
