package com.spring5microservices.common.interfaces.function;

import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 *    Represents a {@link Function} that accepts four arguments and produces a result. This is the four-arity specialization
 * of {@link Function}.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #apply(Object, Object, Object, Object)}.
 *
 * @param <T1>
 *    The type of the first argument to the {@link QuadFunction}
 * @param <T2>
 *    The type of the second argument to the {@link QuadFunction}
 * @param <T3>
 *    The type of the third argument to the {@link QuadFunction}
 * @param <T4>
 *    The type of the fourth argument to the {@link QuadFunction}
 * @param <R>
 *    The type of the result of the {@link QuadFunction}
 *
 * @see {@link Function} and {@link BiFunction}
 */
@FunctionalInterface
public interface QuadFunction<T1, T2, T3, T4, R> {

    /**
     * Applies this {@link QuadFunction} to the given arguments.
     *
     * @param t1
     *    The first {@link QuadFunction} argument
     * @param t2
     *    The second {@link QuadFunction} argument
     * @param t3
     *    The third {@link QuadFunction} argument
     * @param t4
     *    The fourth {@link QuadFunction} argument
     *
     * @return the {@link QuadFunction} result
     */
    R apply(final T1 t1,
            final T2 t2,
            final T3 t3,
            final T4 t4);


    /**
     *    Returns a composed {@link QuadFunction} that first applies this {@link QuadFunction} to its input, and then
     * applies the {@code after} {@link Function} to the result. If evaluation of either function throws an exception,
     * it is relayed to the caller of the composed {@link QuadFunction}.
     *
     * @param after
     *    The {@link Function} to apply after this {@link QuadFunction}
     * @param <Z>
     *    The type of the output of the {@code after} {@link Function}, and of the composed {@link QuadFunction}
     *
     * @return a composed {@link QuadFunction} that first applies this {@link QuadFunction} and then applies the
     *         {@code after} {@link Function}.
     *
     * @throws NullPointerException if {@code after} is {@code null}
     */
    default <Z> QuadFunction<T1, T2, T3, T4, Z> andThen(final Function<? super R, ? extends Z> after) {
        requireNonNull(after, "after must be not null");
        return (t1, t2, t3, t4) ->
                after.apply(
                        apply(t1, t2, t3, t4)
                );
    }

}
