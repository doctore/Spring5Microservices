package com.spring5microservices.common.interfaces.function;

import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 *    Represents a {@link Function} that accepts eight arguments and produces a result. This is the eight-arity specialization
 * of {@link Function}.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #apply(Object, Object, Object, Object, Object, Object, Object, Object)}.
 *
 * @param <T1>
 *    The type of the first argument to the {@link OctaFunction}
 * @param <T2>
 *    The type of the second argument to the {@link OctaFunction}
 * @param <T3>
 *    The type of the third argument to the {@link OctaFunction}
 * @param <T4>
 *    The type of the fourth argument to the {@link OctaFunction}
 * @param <T5>
 *    The type of the fifth argument to the {@link OctaFunction}
 * @param <T6>
 *    The type of the sixth argument to the {@link OctaFunction}
 * @param <T7>
 *    The type of the seventh argument to the {@link OctaFunction}
 * @param <T8>
 *    The type of the eighth argument to the {@link OctaFunction}
 * @param <R>
 *    The type of the result of the {@link OctaFunction}
 *
 * @see {@link Function} and {@link BiFunction}
 */
@FunctionalInterface
public interface OctaFunction<T1, T2, T3, T4, T5, T6, T7, T8, R> {

    /**
     * Applies this {@link OctaFunction} to the given arguments.
     *
     * @param t1
     *    The first {@link OctaFunction} argument
     * @param t2
     *    The second {@link OctaFunction} argument
     * @param t3
     *    The third {@link OctaFunction} argument
     * @param t4
     *    The fourth {@link OctaFunction} argument
     * @param t5
     *    The fifth {@link OctaFunction} argument
     * @param t6
     *    The sixth {@link OctaFunction} argument
     * @param t7
     *    The seventh {@link OctaFunction} argument
     * @param t8
     *    The eighth {@link OctaFunction} argument
     *
     * @return the {@link OctaFunction} result
     */
    R apply(final T1 t1,
            final T2 t2,
            final T3 t3,
            final T4 t4,
            final T5 t5,
            final T6 t6,
            final T7 t7,
            final T8 t8);


    /**
     *    Returns a composed {@link OctaFunction} that first applies this {@link OctaFunction} to its input, and then
     * applies the {@code after} {@link Function} to the result. If evaluation of either function throws an exception,
     * it is relayed to the caller of the composed {@link OctaFunction}.
     *
     * @param after
     *    The {@link Function} to apply after this {@link OctaFunction}
     * @param <Z>
     *    The type of the output of the {@code after} {@link Function}, and of the composed {@link OctaFunction}
     *
     * @return a composed {@link OctaFunction} that first applies this {@link OctaFunction} and then applies the
     *         {@code after} {@link Function}.
     *
     * @throws NullPointerException if {@code after} is {@code null}
     */
    default <Z> OctaFunction<T1, T2, T3, T4, T5, T6, T7, T8, Z> andThen(final Function<? super R, ? extends Z> after) {
        requireNonNull(after, "after must be not null");
        return (t1, t2, t3, t4, t5, t6, t7, t8) ->
                after.apply(
                        apply(t1, t2, t3, t4, t5, t6, t7, t8)
                );
    }

}
