package com.spring5microservices.common.interfaces.functional;

import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 *    Represents a {@link Function} that accepts seven arguments and produces a result. This is the seven-arity specialization
 * of {@link Function}.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #apply(Object, Object, Object, Object, Object, Object, Object)}.
 *
 * @param <T1>
 *    The type of the first argument to the {@link HeptaFunction}
 * @param <T2>
 *    The type of the second argument to the {@link HeptaFunction}
 * @param <T3>
 *    The type of the third argument to the {@link HeptaFunction}
 * @param <T4>
 *    The type of the fourth argument to the {@link HeptaFunction}
 * @param <T5>
 *    The type of the fifth argument to the {@link HeptaFunction}
 * @param <T6>
 *    The type of the sixth argument to the {@link HeptaFunction}
 * @param <T7>
 *    The type of the seventh argument to the {@link HeptaFunction}
 * @param <R>
 *    The type of the result of the {@link HeptaFunction}
 *
 * @see {@link Function} and {@link BiFunction}
 */
@FunctionalInterface
public interface HeptaFunction<T1, T2, T3, T4, T5, T6, T7, R> {

    /**
     * Applies this {@link HeptaFunction} to the given arguments.
     *
     * @param t1
     *    The first {@link HeptaFunction} argument
     * @param t2
     *    The second {@link HeptaFunction} argument
     * @param t3
     *    The third {@link HeptaFunction} argument
     * @param t4
     *    The fourth {@link HeptaFunction} argument
     * @param t5
     *    The fifth {@link HeptaFunction} argument
     * @param t6
     *    The sixth {@link HeptaFunction} argument
     * @param t7
     *    The seventh {@link HeptaFunction} argument
     *
     * @return the {@link HeptaFunction} result
     */
    R apply(final T1 t1,
            final T2 t2,
            final T3 t3,
            final T4 t4,
            final T5 t5,
            final T6 t6,
            final T7 t7);


    /**
     *    Returns a composed {@link HeptaFunction} that first applies this {@link HeptaFunction} to its input, and then
     * applies the after {@link Function} to the result. If evaluation of either function throws an exception, it is relayed
     * to the caller of the composed {@link HeptaFunction}.
     *
     * @param after
     *    The {@link Function} to apply after this {@link HeptaFunction} is applied
     * @param <Z>
     *    The type of the output of the {@code after} {@link Function}, and of the composed {@link HeptaFunction}
     *
     * @return a composed {@link HeptaFunction} that first applies this {@link HeptaFunction} and then applies the
     *         {@code after} {@link Function}.
     *
     * @throws NullPointerException if {@code after} is {@code null}
     */
    default <Z> HeptaFunction<T1, T2, T3, T4, T5, T6, T7, Z> andThen(final Function<? super R, ? extends Z> after) {
        requireNonNull(after, "after must be not null");
        return (T1 t1,
                T2 t2,
                T3 t3,
                T4 t4,
                T5 t5,
                T6 t6,
                T7 t7) ->
                   after.apply(
                           apply(t1, t2, t3, t4, t5, t6, t7)
                   );
    }

}
