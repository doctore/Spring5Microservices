package com.spring5microservices.common.interfaces.consumer;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 *    Represents an operation that accepts four input arguments and returns no result. This is the four-arity specialization
 * of {@link Consumer}
 * <p>
 * This is a functional interface whose functional method is {@link QuadConsumer#accept(Object, Object, Object, Object)}.
 *
 * @param <T1>
 *    The type of the first argument to the {@link QuadConsumer}
 * @param <T2>
 *    The type of the second argument to the {@link QuadConsumer}
 * @param <T3>
 *    The type of the third argument to the {@link QuadConsumer}
 * @param <T4>
 *    The type of the fourth argument to the  {@link QuadConsumer}
 *
 * @see {@link Consumer} and {@link BiConsumer}
 */
@FunctionalInterface
public interface QuadConsumer<T1, T2, T3, T4> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param t1
     *    The first {@link QuadConsumer} argument
     * @param t2
     *    The second {@link QuadConsumer} argument
     * @param t3
     *    The third {@link QuadConsumer} argument
     * @param t4
     *    The fourth {@link QuadConsumer} argument
     */
    void accept(T1 t1,
                T2 t2,
                T3 t3,
                T4 t4);


    /**
     *    Returns a composed {@link QuadConsumer} that performs, in sequence, this operation followed by the
     * {@code after} operation.
     *
     * @param after
     *    {@link QuadConsumer}
     *
     * @throws NullPointerException if {@code after} is {@code null}
     */
    default QuadConsumer<T1, T2, T3, T4> andThen(QuadConsumer<? super T1, ? super T2, ? super T3, ? super T4> after) {
        Objects.requireNonNull(after);
        return (t1, t2, t3, t4) -> {
            this.accept(t1, t2, t3, t4);
            after.accept(t1, t2, t3, t4);
        };
    }

}
