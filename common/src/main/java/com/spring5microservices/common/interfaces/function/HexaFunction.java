package com.spring5microservices.common.interfaces.function;

import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 *    Represents a {@link Function} that accepts six arguments and produces a result. This is the six-arity specialization
 * of {@link Function}.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #apply(Object, Object, Object, Object, Object, Object)}.
 *
 * @param <T1>
 *    The type of the first argument to the {@link HexaFunction}
 * @param <T2>
 *    The type of the second argument to the {@link HexaFunction}
 * @param <T3>
 *    The type of the third argument to the {@link HexaFunction}
 * @param <T4>
 *    The type of the fourth argument to the {@link HexaFunction}
 * @param <T5>
 *    The type of the fifth argument to the {@link HexaFunction}
 * @param <T6>
 *    The type of the sixth argument to the {@link HexaFunction}
 * @param <R>
 *    The type of the result of the {@link HexaFunction}
 *
 * @see {@link Function} and {@link BiFunction}
 */
@FunctionalInterface
public interface HexaFunction<T1, T2, T3, T4, T5, T6, R> {

    /**
     * Applies this {@link HexaFunction} to the given arguments.
     *
     * @param t1
     *    The first {@link HexaFunction} argument
     * @param t2
     *    The second {@link HexaFunction} argument
     * @param t3
     *    The third {@link HexaFunction} argument
     * @param t4
     *    The fourth {@link HexaFunction} argument
     * @param t5
     *    The fifth {@link HexaFunction} argument
     * @param t6
     *    The sixth {@link HexaFunction} argument
     *
     * @return the {@link HexaFunction} result
     */
    R apply(final T1 t1,
            final T2 t2,
            final T3 t3,
            final T4 t4,
            final T5 t5,
            final T6 t6);


    /**
     *    Returns a composed {@link HexaFunction} that first applies this {@link HexaFunction} to its input, and then
     * applies the after {@link Function} to the result. If evaluation of either function throws an exception, it is relayed
     * to the caller of the composed {@link HexaFunction}.
     *
     * @param after
     *    The {@link Function} to apply after this {@link HexaFunction}
     * @param <Z>
     *    The type of the output of the {@code after} {@link Function}, and of the composed {@link HexaFunction}
     *
     * @return a composed {@link HexaFunction} that first applies this {@link HexaFunction} and then applies the
     *         {@code after} {@link Function}.
     *
     * @throws NullPointerException if {@code after} is {@code null}
     */
    default <Z> HexaFunction<T1, T2, T3, T4, T5, T6, Z> andThen(final Function<? super R, ? extends Z> after) {
        requireNonNull(after, "after must be not null");
        return (t1, t2, t3, t4, t5, t6) ->
                after.apply(
                        apply(t1, t2, t3, t4, t5, t6)
                );
    }

}
