package com.spring5microservices.common.interfaces.functional;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 *    Represents a {@link Function} that accepts three arguments and produces a result. This is the three-arity specialization
 * of {@link Function}.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #apply(Object, Object, Object)}.
 *
 * @param <T>
 *    The type of the first argument to the {@link TriFunction}
 * @param <U>
 *    The type of the second argument to the {@link TriFunction}
 * @param <V>
 *    The type of the third argument to the {@link TriFunction}
 * @param <R>
 *    The type of the result of the {@link TriFunction}
 *
 * @see {@link Function} and {@link BiFunction}
 */
@FunctionalInterface
public interface TriFunction<T, U, V, R> {

    /**
     * Applies this {@link TriFunction} to the given arguments.
     *
     * @param t
     *    The first {@link TriFunction} argument
     * @param u
     *    The second {@link TriFunction} argument
     * @param v
     *    The third {@link TriFunction} argument
     *
     * @return the {@link TriFunction} result
     */
    R apply(final T t,
            final U u,
            final V v);


    /**
     *    Returns a composed {@link TriFunction} that first applies this {@link TriFunction} to its input, and then
     * applies the after {@link Function} to the result. If evaluation of either function throws an exception, it is relayed
     * to the caller of the composed {@link TriFunction}.
     *
     * @param after
     *    The {@link Function} to apply after this {@link TriFunction} is applied
     * @param <Z>
     *    The type of the output of the {@code after} {@link Function}, and of the composed {@link TriFunction}
     *
     * @return a composed {@link TriFunction} that first applies this {@link TriFunction} and then applies the
     *         {@code after} {@link Function}.
     *
     * @throws NullPointerException if {@code after} is {@code null}
     */
    default <Z> TriFunction<T, U, V, Z> andThen(final Function<? super R, ? extends Z> after) {
        Objects.requireNonNull(after, "after must be not null");
        return (T t, U u, V v) -> after.apply(apply(t, u, v));
    }

}
