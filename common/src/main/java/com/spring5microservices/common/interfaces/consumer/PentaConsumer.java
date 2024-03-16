package com.spring5microservices.common.interfaces.consumer;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 *    Represents an operation that accepts five input arguments and returns no result. This is the five-arity specialization
 * of {@link Consumer}
 * <p>
 * This is a functional interface whose functional method is {@link PentaConsumer#accept(Object, Object, Object, Object, Object)}.
 *
 * @param <T1>
 *    The type of the first argument to the {@link PentaConsumer}
 * @param <T2>
 *    The type of the second argument to the {@link PentaConsumer}
 * @param <T3>
 *    The type of the third argument to the {@link PentaConsumer}
 * @param <T4>
 *    The type of the fourth argument to the  {@link PentaConsumer}
 * @param <T5>
 *    The type of the fifth argument to the  {@link PentaConsumer}
 *
 * @see {@link Consumer} and {@link BiConsumer}
 */
@FunctionalInterface
public interface PentaConsumer<T1, T2, T3, T4, T5> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param t1
     *    The first {@link PentaConsumer} argument
     * @param t2
     *    The second {@link PentaConsumer} argument
     * @param t3
     *    The third {@link PentaConsumer} argument
     * @param t4
     *    The fourth {@link PentaConsumer} argument
     * @param t5
     *    The fifth {@link PentaConsumer} argument
     */
    void accept(T1 t1,
                T2 t2,
                T3 t3,
                T4 t4,
                T5 t5);


    /**
     *    Returns a composed {@link PentaConsumer} that performs, in sequence, this operation followed by the
     * {@code after} operation.
     *
     * @param after
     *    {@link PentaConsumer}
     *
     * @throws NullPointerException if {@code after} is {@code null}
     */
    default PentaConsumer<T1, T2, T3, T4, T5> andThen(PentaConsumer<? super T1, ? super T2, ? super T3, ? super T4, ? super T5> after) {
        Objects.requireNonNull(after);
        return (t1, t2, t3, t4, t5) -> {
            this.accept(t1, t2, t3, t4, t5);
            after.accept(t1, t2, t3, t4, t5);
        };
    }

}
