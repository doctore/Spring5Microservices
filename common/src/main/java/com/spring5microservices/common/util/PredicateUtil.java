package com.spring5microservices.common.util;

import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@UtilityClass
public class PredicateUtil {

    /**
     * Checks all given {@code predicates} to verify if all of them are satisfied.
     *
     * <pre>
     * Example:
     *   Predicate<Integer> isGreaterThanTen = i -> 10 < i;
     *   Predicate<Integer> isGreaterThanTwenty = i -> 20 < i;
     *
     *   allOf().test(5);                                          // true
     *   allOf(isGreaterThanTen, isGreaterThanTwenty).test(30);    // true
     *   allOf(isGreaterThanTen, isGreaterThanTwenty).test(20);    // false
     * </pre>
     *
     * @param predicates
     *    {@link Predicate} to verify
     *
     * @return {@link Predicate} verifying all provided ones
     */
    @SafeVarargs
    public static <T> Predicate<T> allOf(Predicate<? super T>... predicates) {
        if (ObjectUtil.isEmpty(predicates)) {
            return alwaysTrue();
        }
        return t ->
                CollectionUtil.foldLeft(
                        asList(predicates),
                        true,
                        (previousBoolean, currentPred) -> {
                            boolean currentPredResult = isNull(currentPred) || currentPred.test(t);
                            return previousBoolean && currentPredResult;
                        }
                );
    }


    /**
     * Checks all given {@code predicates} to verify that at least one is satisfied.
     *
     * <pre>
     * Example:
     *   Predicate<Integer> isGreaterThanTen = i -> 10 < i;
     *   Predicate<Integer> isGreaterThanTwenty = i -> 20 < i;
     *
     *   anyOf().test(5);                                          // false
     *   anyOf(isGreaterThanTen, isGreaterThanTwenty).test(11);    // true
     *   anyOf(isGreaterThanTen, isGreaterThanTwenty).test(21);    // true
     *   anyOf(isGreaterThanTen, isGreaterThanTwenty).test(1);     // false
     * </pre>
     *
     * @param predicates
     *    {@link Predicate} to verify
     *
     * @return {@link Predicate}
     */
    @SafeVarargs
    public static <T> Predicate<T> anyOf(Predicate<? super T>... predicates) {
        if (ObjectUtil.isEmpty(predicates)) {
            return alwaysFalse();
        }
        return t -> {
            for (Predicate<? super T> predicate: predicates) {
                if (nonNull(predicate) && predicate.test(t)) {
                    return true;
                }
            }
            return false;
        };
    }


    /**
     * Returns a {@link Predicate} with {@code false} as result.
     *
     * @return {@link Predicate}
     */
    public static <T> Predicate<T> alwaysFalse() {
        return t -> false;
    }


    /**
     * Returns a {@link Predicate} with {@code true} as result.
     *
     * @return {@link Predicate}
     */
    public static <T> Predicate<T> alwaysTrue() {
        return t -> true;
    }


    /**
     * Checks all given {@code predicates} to verify if all of them are satisfied.
     *
     * <pre>
     * Example:
     *   BiPredicate<Integer, String> isIntegerGreaterThanTenAndStringLongerThan2 = (i, s) -> (10 < i) && (2 < s.length());
     *   BiPredicate<Integer, String> isGreaterThanTwentyAndStringLongerThan5 = (i, s) -> (20 < i) && (5 < s.length());
     *
     *   biAllOf().test(5, "");                                                                                              // true
     *   biAllOf(isIntegerGreaterThanTenAndStringLongerThan2, isGreaterThanTwentyAndStringLongerThan5).test(30, "abcdef");   // true
     *   biAllOf(isIntegerGreaterThanTenAndStringLongerThan2, isGreaterThanTwentyAndStringLongerThan5).test(20, "abc");      // false
     * </pre>
     *
     * @param predicates
     *    {@link BiPredicate} to verify
     *
     * @return {@link BiPredicate} containing all provided ones
     */
    @SafeVarargs
    public static <T, E> BiPredicate<T, E> biAllOf(BiPredicate<? super T, ? super E>... predicates) {
        if (ObjectUtil.isEmpty(predicates)) {
            return biAlwaysTrue();
        }
        return (t, e) ->
                CollectionUtil.foldLeft(
                        asList(predicates),
                        true,
                        (previousBoolean, currentPred) -> {
                            boolean currentPredResult = isNull(currentPred) || currentPred.test(t, e);
                            return previousBoolean && currentPredResult;
                        }
                );
    }


    /**
     * Checks all given {@code predicates} to verify that at least one is satisfied.
     *
     * <pre>
     * Example:
     *   BiPredicate<Integer, String> isIntegerGreaterThanTenAndStringLongerThan2 = (i, s) -> (10 < i) && (2 < s.length());
     *   BiPredicate<Integer, String> isLowerThanTwentyAndStringShorterThan5 = (i, s) -> (20 > i) && (5 > s.length());
     *
     *   biAnyOf().test(5, "");                                                                                            // false
     *   biAnyOf(isIntegerGreaterThanTenAndStringLongerThan2, isLowerThanTwentyAndStringShorterThan5).test(11, "abc");     // true
     *   biAnyOf(isIntegerGreaterThanTenAndStringLongerThan2, isLowerThanTwentyAndStringShorterThan5).test(8, "abc");      // true
     *   biAnyOf(isIntegerGreaterThanTenAndStringLongerThan2, isLowerThanTwentyAndStringShorterThan5).test(5, "abcdef");   // false
     * </pre>
     *
     * @param predicates
     *    {@link BiPredicate} to verify
     *
     * @return {@link BiPredicate}
     */
    @SafeVarargs
    public static <T, E> BiPredicate<T, E> biAnyOf(BiPredicate<? super T, ? super E>... predicates) {
        if (ObjectUtil.isEmpty(predicates)) {
            return biAlwaysFalse();
        }
        return (t, e) -> {
            for (BiPredicate<? super T, ? super E> predicate: predicates) {
                if (nonNull(predicate) && predicate.test(t, e)) {
                    return true;
                }
            }
            return false;
        };
    }


    /**
     * Returns a {@link BiPredicate} with {@code false} as result.
     *
     * @return {@link BiPredicate}
     */
    public static <T, E> BiPredicate<T, E> biAlwaysFalse() {
        return (t, e) -> false;
    }


    /**
     * Returns a {@link BiPredicate} with {@code true} as result.
     *
     * @return {@link BiPredicate}
     */
    public static <T, E> BiPredicate<T, E> biAlwaysTrue() {
        return (t, e) -> true;
    }


    /**
     * Used when we want to get the unique elements of a given {@link Collection} by a specific property of its objects
     *
     * @param keyExtractor
     *    {@link Function} used to get the key we want to use to distinct the elements
     *
     * @return unique object
     */
    public static <T> Predicate<T> distinctByKey(final Function<? super T, ?> keyExtractor) {
        final Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

}
