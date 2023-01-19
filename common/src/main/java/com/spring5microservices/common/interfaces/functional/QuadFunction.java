package com.spring5microservices.common.interfaces.functional;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Represents a function that accepts four arguments and produces a result.
 * This is the four-arity specialization of {@link Function}.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #apply(Object, Object, Object, Object)}.
 *
 * @param <T>
 *    The type of the first argument to the function
 * @param <U>
 *    The type of the second argument to the function
 * @param <V>
 *    The type of the third argument to the function
 * @param <W>
 *    The type of the fourth argument to the function
 * @param <R>
 *    The type of the result of the function
 *
 * @see {@link Function} and {@link BiFunction}
 */
@FunctionalInterface
public interface QuadFunction<T, U, V, W, R> {

    /**
     * Applies this function to the given arguments.
     *
     * @param t
     *    The first function argument
     * @param u
     *    The second function argument
     * @param v
     *    The third function argument
     * @param w
     *    The fourth function argument
     *
     * @return the function result
     */
    R apply(final T t,
            final U u,
            final V v,
            final W w);


    /**
     *    Returns a composed function that first applies this function to its input, and then applies the after function
     * to the result. If evaluation of either function throws an exception, it is relayed to the caller of the composed
     * function.
     *
     * @param after
     *    The {@link Function} to apply after this function is applied
     * @param <Z>
     *    The type of the output of the {@code after} function, and of the composed function
     *
     * @return a composed function that first applies this function and then applies the {@code after} function.
     *
     * @throws NullPointerException if {@code after} is {@code null}
     */
    default <Z> QuadFunction<T, U, V, W, Z> andThen(final Function<? super R, ? extends Z> after) {
        Objects.requireNonNull(after, "after must be not null");
        return (T t, U u, V v, W w) -> after.apply(apply(t, u, v, w));
    }

}
