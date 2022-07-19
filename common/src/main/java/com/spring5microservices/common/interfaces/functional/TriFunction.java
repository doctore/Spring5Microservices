package com.spring5microservices.common.interfaces.functional;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Represents a function that accepts three arguments and produces a result.
 * This is the three-arity specialization of {@link Function}.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #apply(Object, Object, Object)}.
 *
 * @param <T>
 *    The type of the first argument to the function
 * @param <U>
 *    The type of the second argument to the function
 * @param <V>
 *    The type of the third argument to the function
 * @param <R>
 *    The type of the result of the function
 *
 * @see {@link Function} and {@link BiFunction}
 */
@FunctionalInterface
public interface TriFunction<T, U, V, R> {

    /**
     * Applies this function to the given arguments.
     *
     * @param t
     *    The first function argument
     * @param u
     *    The second function argument
     * @param v
     *    The third function argument
     *
     * @return the function result
     */
    R apply(final T t,
            final U u,
            final V v);


    /**
     *    Returns a composed function that first applies this function to its input, and then applies the after function
     * to the result. If evaluation of either function throws an exception, it is relayed to the caller of the composed
     * function.
     *
     * @param after
     *    The function to apply after this function is applied
     * @param <Z>
     *    The type of the output of the {@code after} function, and of the composed function
     *
     * @return a composed function that first applies this function and then applies the {@code after} function.
     *
     * @throws NullPointerException if {@code after} is null
     */
    default <Z> TriFunction<T, U, V, Z> andThen(final Function<? super R, ? extends Z> after) {
        Objects.requireNonNull(after, "after must be not null");
        return (T t, U u, V v) -> after.apply(apply(t, u, v));
    }

}
