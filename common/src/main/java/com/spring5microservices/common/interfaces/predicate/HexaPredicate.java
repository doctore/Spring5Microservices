package com.spring5microservices.common.interfaces.predicate;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

/**
 *    Represents a predicate (boolean-valued function) of six arguments. This is the six-arity specialization of
 * {@link Predicate}.
 * <p>
 * This is a functional interface whose functional method is {@link HexaPredicate#test(Object, Object, Object, Object, Object, Object)}.
 *
 * @param <T1>
 *    The type of the first argument to the {@link HexaPredicate}
 * @param <T2>
 *    The type of the second argument to the {@link HexaPredicate}
 * @param <T3>
 *    The type of the third argument to the {@link HexaPredicate}
 * @param <T4>
 *    The type of the fourth argument to the {@link HexaPredicate}
 * @param <T5>
 *    The type of the fifth argument to the {@link HexaPredicate}
 * @param <T6>
 *    The type of the sixth argument to the {@link HexaPredicate}
 *
 * @see {@link Predicate} and {@link BiPredicate}
 */
@FunctionalInterface
public interface HexaPredicate<T1, T2, T3, T4, T5, T6> {

    /**
     * Evaluates this predicate on the given arguments.
     *
     * @param t1
     *    The first {@link HexaPredicate} argument
     * @param t2
     *    The second {@link HexaPredicate} argument
     * @param t3
     *    The third {@link HexaPredicate} argument
     * @param t4
     *    The fourth {@link HexaPredicate} argument
     * @param t5
     *    The fifth {@link HexaPredicate} argument
     * @param t6
     *    The sixth {@link HexaPredicate} argument
     *
     * @return the {@link HexaPredicate} result
     */
    boolean test(T1 t1,
                 T2 t2,
                 T3 t3,
                 T4 t4,
                 T5 t5,
                 T6 t6);


    /**
     *    Returns a composed {@link HexaPredicate} that represents a short-circuiting logical AND of this {@link HexaPredicate}
     * and {@code other}.
     *
     * @param other
     *    {@link HexaPredicate} to check after this {@link HexaPredicate}
     *
     * @return {@code true} if both this {@link HexaPredicate} and {@code other} returns {@code true},
     *         {@code false} otherwise
     *
     * @throws NullPointerException if {@code other} is {@code null}
     */
    default HexaPredicate<T1, T2, T3, T4, T5, T6> and(HexaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6> other) {
        requireNonNull(other);
        return (t1, t2, t3, t4, t5, t6) ->
                this.test(t1, t2, t3, t4, t5, t6) &&
                        other.test(t1, t2, t3, t4, t5, t6);
    }


    /**
     * Returns a {@link HexaPredicate} that represents the logical negation of this {@link HexaPredicate}.
     *
     * @return {@code true} if this {@link HexaPredicate} returns {@code false},
     *         {@code false} otherwise
     */
    default HexaPredicate<T1, T2, T3, T4, T5, T6> negate() {
        return (t1, t2, t3, t4, t5, t6) ->
                !this.test(t1, t2, t3, t4, t5, t6);
    }


    /**
     *    Returns a composed {@link HexaPredicate} that represents a short-circuiting logical OR of this {@link HexaPredicate}
     * and {@code other}.
     *
     * @param other
     *    {@link HexaPredicate} to check after this {@link HexaPredicate}
     *
     * @return {@code true} if this {@link HexaPredicate} or {@code other} returns {@code true},
     *         {@code false} otherwise
     *
     * @throws NullPointerException if {@code other} is {@code null}
     */
    default HexaPredicate<T1, T2, T3, T4, T5, T6> or(HexaPredicate<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6> other) {
        requireNonNull(other);
        return (t1, t2, t3, t4, t5, t6) ->
                this.test(t1, t2, t3, t4, t5, t6) ||
                        other.test(t1, t2, t3, t4, t5, t6);
    }

}
