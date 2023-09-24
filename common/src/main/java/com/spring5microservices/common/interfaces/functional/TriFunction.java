package com.spring5microservices.common.interfaces.functional;

import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 *    Represents a {@link Function} that accepts three arguments and produces a result. This is the three-arity specialization
 * of {@link Function}.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #apply(Object, Object, Object)}.
 *
 * @param <T1>
 *    The type of the first argument to the {@link TriFunction}
 * @param <T2>
 *    The type of the second argument to the {@link TriFunction}
 * @param <T3>
 *    The type of the third argument to the {@link TriFunction}
 * @param <R>
 *    The type of the result of the {@link TriFunction}
 *
 * @see {@link Function} and {@link BiFunction}
 */
@FunctionalInterface
public interface TriFunction<T1, T2, T3, R> {

    /**
     * Applies this {@link TriFunction} to the given arguments.
     *
     * @param t1
     *    The first {@link TriFunction} argument
     * @param t2
     *    The second {@link TriFunction} argument
     * @param t3
     *    The third {@link TriFunction} argument
     *
     * @return the {@link TriFunction} result
     */
    R apply(final T1 t1,
            final T2 t2,
            final T3 t3);


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
    default <Z> TriFunction<T1, T2, T3, Z> andThen(final Function<? super R, ? extends Z> after) {
        requireNonNull(after, "after must be not null");
        return (T1 t1,
                T2 t2,
                T3 t3) ->
                   after.apply(
                           apply(t1, t2, t3)
                   );
    }

}
