package com.spring5microservices.common.interfaces.functional;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 *    Represents a {@link Function} that accepts four arguments and produces a result. This is the five-arity specialization
 * of {@link Function}.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #apply(Object, Object, Object, Object, Object)}.
 *
 * @param <T>
 *    The type of the first argument to the {@link PentaFunction}
 * @param <U>
 *    The type of the second argument to the {@link PentaFunction}
 * @param <V>
 *    The type of the third argument to the {@link PentaFunction}
 * @param <W>
 *    The type of the fourth argument to the {@link PentaFunction}
 * @param <X>
 *    The type of the fifth argument to the {@link PentaFunction}
 * @param <R>
 *    The type of the result of the {@link PentaFunction}
 *
 * @see {@link Function} and {@link BiFunction}
 */
@FunctionalInterface
public interface PentaFunction<T, U, V, W, X, R> {

    /**
     * Applies this {@link PentaFunction} to the given arguments.
     *
     * @param t
     *    The first {@link PentaFunction} argument
     * @param u
     *    The second {@link PentaFunction} argument
     * @param v
     *    The third {@link PentaFunction} argument
     * @param w
     *    The fourth {@link PentaFunction} argument
     * @param x
     *    The fifth {@link PentaFunction} argument
     *
     * @return the {@link PentaFunction} result
     */
    R apply(final T t,
            final U u,
            final V v,
            final W w,
            final X x);


    /**
     *    Returns a composed {@link PentaFunction} that first applies this {@link PentaFunction} to its input, and then
     * applies the after {@link Function} to the result. If evaluation of either function throws an exception, it is relayed
     * to the caller of the composed {@link PentaFunction}.
     *
     * @param after
     *    The {@link Function} to apply after this {@link PentaFunction} is applied
     * @param <Z>
     *    The type of the output of the {@code after} {@link Function}, and of the composed {@link PentaFunction}
     *
     * @return a composed {@link PentaFunction} that first applies this {@link PentaFunction} and then applies the
     *         {@code after} {@link Function}.
     *
     * @throws NullPointerException if {@code after} is {@code null}
     */
    default <Z> PentaFunction<T, U, V, W, X, Z> andThen(final Function<? super R, ? extends Z> after) {
        Objects.requireNonNull(after, "after must be not null");
        return (T t, U u, V v, W w, X x) -> after.apply(apply(t, u, v, w, x));
    }

}
