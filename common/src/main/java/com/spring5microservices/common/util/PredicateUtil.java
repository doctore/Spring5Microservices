package com.spring5microservices.common.util;

import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

@UtilityClass
public class PredicateUtil {

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
