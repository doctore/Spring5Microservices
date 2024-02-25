package com.spring5microservices.common.interfaces.predicate;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

/**
 *    Represents a predicate (boolean-valued function) of five arguments. This is the five-arity specialization of
 * {@link Predicate}.
 * <p>
 * This is a functional interface whose functional method is {@link PentaPredicate#test(Object, Object, Object, Object, Object)}.
 *
 * @param <T1>
 *    The type of the first argument to the {@link PentaPredicate}
 * @param <T2>
 *    The type of the second argument to the {@link PentaPredicate}
 * @param <T3>
 *    The type of the third argument to the {@link PentaPredicate}
 * @param <T4>
 *    The type of the fourth argument to the {@link PentaPredicate}
 * @param <T5>
 *    The type of the fifth argument to the {@link PentaPredicate}
 *
 * @see {@link Predicate} and {@link BiPredicate}
 */
@FunctionalInterface
public interface PentaPredicate<T1, T2, T3, T4, T5> {

    /**
     * Evaluates this predicate on the given arguments.
     *
     * @param t1
     *    The first {@link PentaPredicate} argument
     * @param t2
     *    The second {@link PentaPredicate} argument
     * @param t3
     *    The third {@link PentaPredicate} argument
     * @param t4
     *    The fourth {@link PentaPredicate} argument
     * @param t5
     *    The fifth {@link PentaPredicate} argument
     *
     * @return the {@link PentaPredicate} result
     */
    boolean test(T1 t1,
                 T2 t2,
                 T3 t3,
                 T4 t4,
                 T5 t5);


    /**
     *    Returns a composed {@link PentaPredicate} that represents a short-circuiting logical AND of this {@link PentaPredicate}
     * and {@code other}.
     *
     * @param other
     *    {@link PentaPredicate} to check after this {@link PentaPredicate}
     *
     * @return {@code true} if both this {@link PentaPredicate} and {@code other} returns {@code true},
     *         {@code false} otherwise
     *
     * @throws NullPointerException if {@code other} is {@code null}
     */
    default PentaPredicate<T1, T2, T3, T4, T5> and(PentaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5> other) {
        requireNonNull(other);
        return (t1, t2, t3, t4, t5) ->
                this.test(t1, t2, t3, t4, t5) &&
                        other.test(t1, t2, t3, t4, t5);
    }


    /**
     * Returns a {@link PentaPredicate} that represents the logical negation of this {@link PentaPredicate}.
     *
     * @return {@code true} if this {@link PentaPredicate} returns {@code false},
     *         {@code false} otherwise
     */
    default PentaPredicate<T1, T2, T3, T4, T5> negate() {
        return (t1, t2, t3, t4, t5) ->
                !this.test(t1, t2, t3, t4, t5);
    }


    /**
     *    Returns a composed {@link PentaPredicate} that represents a short-circuiting logical OR of this {@link PentaPredicate}
     * and {@code other}.
     *
     * @param other
     *    {@link PentaPredicate} to check after this {@link PentaPredicate}
     *
     * @return {@code true} if this {@link PentaPredicate} or {@code other} returns {@code true},
     *         {@code false} otherwise
     *
     * @throws NullPointerException if {@code other} is {@code null}
     */
    default PentaPredicate<T1, T2, T3, T4, T5> or(PentaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5> other) {
        requireNonNull(other);
        return (t1, t2, t3, t4, t5) ->
                this.test(t1, t2, t3, t4, t5) ||
                        other.test(t1, t2, t3, t4, t5);
    }

}
