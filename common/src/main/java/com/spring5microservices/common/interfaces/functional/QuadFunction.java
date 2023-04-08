package com.spring5microservices.common.interfaces.functional;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 *    Represents a {@link Function} that accepts four arguments and produces a result. This is the four-arity specialization
 * of {@link Function}.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #apply(Object, Object, Object, Object)}.
 *
 * @param <T>
 *    The type of the first argument to the {@link QuadFunction}
 * @param <U>
 *    The type of the second argument to the {@link QuadFunction}
 * @param <V>
 *    The type of the third argument to the {@link QuadFunction}
 * @param <W>
 *    The type of the fourth argument to the {@link QuadFunction}
 * @param <R>
 *    The type of the result of the {@link QuadFunction}
 *
 * @see {@link Function} and {@link BiFunction}
 */
@FunctionalInterface
public interface QuadFunction<T, U, V, W, R> {

    /**
     * Applies this {@link QuadFunction} to the given arguments.
     *
     * @param t
     *    The first {@link QuadFunction} argument
     * @param u
     *    The second {@link QuadFunction} argument
     * @param v
     *    The third {@link QuadFunction} argument
     * @param w
     *    The fourth {@link QuadFunction} argument
     *
     * @return the {@link QuadFunction} result
     */
    R apply(final T t,
            final U u,
            final V v,
            final W w);


    /**
     *    Returns a composed {@link QuadFunction} that first applies this {@link QuadFunction} to its input, and then
     * applies the after {@link Function} to the result. If evaluation of either function throws an exception, it is relayed
     * to the caller of the composed {@link QuadFunction}.
     *
     * @param after
     *    The {@link Function} to apply after this {@link QuadFunction} is applied
     * @param <Z>
     *    The type of the output of the {@code after} {@link Function}, and of the composed {@link QuadFunction}
     *
     * @return a composed {@link QuadFunction} that first applies this {@link QuadFunction} and then applies the
     *         {@code after} {@link Function}.
     *
     * @throws NullPointerException if {@code after} is {@code null}
     */
    default <Z> QuadFunction<T, U, V, W, Z> andThen(final Function<? super R, ? extends Z> after) {
        Objects.requireNonNull(after, "after must be not null");
        return (T t, U u, V v, W w) -> after.apply(apply(t, u, v, w));
    }

}
