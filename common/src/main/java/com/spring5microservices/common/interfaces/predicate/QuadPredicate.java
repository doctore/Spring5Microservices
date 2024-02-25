package com.spring5microservices.common.interfaces.predicate;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

/**
 *    Represents a predicate (boolean-valued function) of four arguments. This is the four-arity specialization of
 * {@link Predicate}.
 * <p>
 * This is a functional interface whose functional method is {@link QuadPredicate#test(Object, Object, Object, Object)}.
 *
 * @param <T1>
 *    The type of the first argument to the {@link QuadPredicate}
 * @param <T2>
 *    The type of the second argument to the {@link QuadPredicate}
 * @param <T3>
 *    The type of the third argument to the {@link QuadPredicate}
 * @param <T4>
 *    The type of the fourth argument to the {@link QuadPredicate}
 *
 * @see {@link Predicate} and {@link BiPredicate}
 */
@FunctionalInterface
public interface QuadPredicate<T1, T2, T3, T4> {

    /**
     * Evaluates this predicate on the given arguments.
     *
     * @param t1
     *    The first {@link QuadPredicate} argument
     * @param t2
     *    The second {@link QuadPredicate} argument
     * @param t3
     *    The third {@link QuadPredicate} argument
     * @param t4
     *    The fourth {@link QuadPredicate} argument
     *
     * @return the {@link QuadPredicate} result
     */
    boolean test(T1 t1,
                 T2 t2,
                 T3 t3,
                 T4 t4);


    /**
     *    Returns a composed {@link QuadPredicate} that represents a short-circuiting logical AND of this {@link QuadPredicate}
     * and {@code other}.
     *
     * @param other
     *    {@link QuadPredicate} to check after this {@link QuadPredicate}
     *
     * @return {@code true} if both this {@link QuadPredicate} and {@code other} returns {@code true},
     *         {@code false} otherwise
     *
     * @throws NullPointerException if {@code other} is {@code null}
     */
    default QuadPredicate<T1, T2, T3, T4> and(QuadPredicate<? super T1, ? super T2, ? super T3, ? super T4> other) {
        requireNonNull(other);
        return (t1, t2, t3, t4) ->
                this.test(t1, t2, t3, t4) &&
                        other.test(t1, t2, t3, t4);
    }


    /**
     * Returns a {@link QuadPredicate} that represents the logical negation of this {@link QuadPredicate}.
     *
     * @return {@code true} if this {@link QuadPredicate} returns {@code false},
     *         {@code false} otherwise
     */
    default QuadPredicate<T1, T2, T3, T4> negate() {
        return (t1, t2, t3, t4) ->
                !this.test(t1, t2, t3, t4);
    }


    /**
     *    Returns a composed {@link QuadPredicate} that represents a short-circuiting logical OR of this {@link QuadPredicate}
     * and {@code other}.
     *
     * @param other
     *    {@link QuadPredicate} to check after this {@link QuadPredicate}
     *
     * @return {@code true} if this {@link QuadPredicate} or {@code other} returns {@code true},
     *         {@code false} otherwise
     *
     * @throws NullPointerException if {@code other} is {@code null}
     */
    default QuadPredicate<T1, T2, T3, T4> or(QuadPredicate<? super T1, ? super T2, ? super T3, ? super T4> other) {
        requireNonNull(other);
        return (t1, t2, t3, t4) ->
                this.test(t1, t2, t3, t4) ||
                        other.test(t1, t2, t3, t4);
    }

}
