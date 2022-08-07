package com.spring5microservices.common.collection.tuple;

import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Comparator;
import java.util.function.Supplier;

import static java.util.Optional.ofNullable;

/**
 * A {@link Tuple} of no elements.
 */
public final class Tuple0 implements Tuple, Serializable {

    private static final long serialVersionUID = 5791060796518852381L;

    /**
     * The singleton instance of {@link Tuple0}.
     */
    private static final Tuple0 INSTANCE = new Tuple0();

    /**
     * The singleton Tuple0 comparator.
     */
    private static final Comparator<Tuple0> COMPARATOR = (t1, t2) -> 0;

    /**
     * Returns the singleton instance of {@link Tuple0}.
     *
     * @return The singleton instance of {@link Tuple0}
     */
    public static Tuple0 instance() {
        return INSTANCE;
    }


    public static Comparator<Tuple0> comparator() {
        return COMPARATOR;
    }


    @Override
    public int arity() {
        return 0;
    }


    @Override
    public boolean equals(Object o) {
        return o == this;
    }


    @Override
    public int hashCode() {
        return 1;
    }


    @Override
    public String toString() {
        return "()";
    }


    /**
     * Transforms this {@link Tuple0} to an object of type U.
     *
     * @param f
     *    Transformation {@link Supplier} which creates a new object of type U based on this tuple's contents.
     *
     * @return An object of type U
     *
     * @throws IllegalArgumentException if {@code f} is {@code null}
     */
    public <U> U apply(final Supplier<? extends U> f) {
        Assert.notNull(f, "f must be not null");
        return f.get();
    }


    /**
     * Prepend a value to this {@link Tuple0}.
     *
     * @param t
     *    The value to prepend
     *
     * @return a new {@link Tuple1} with the value prepended
     */
    public <T> Tuple1<T> prepend(final T t) {
        return Tuple.of(t);
    }


    /**
     * Append a value to this {@link Tuple0}.
     *
     * @param t
     *    The value to append
     *
     * @return a new {@link Tuple1} with the value appended
     */
    public <T> Tuple1<T> append(final T t) {
        return Tuple.of(t);
    }


    /**
     * Concat a {@link Tuple1}'s values to this {@link Tuple0}.
     *
     * @param tuple
     *    The {@link Tuple1} to concat
     *
     * @return a new {@link Tuple1} with the tuple values appended
     *
     * @throws IllegalArgumentException if {@code tuple} is {@code null}
     */
    public <T1> Tuple1<T1> concat(final Tuple1<T1> tuple) {
        return ofNullable(tuple)
                .map(t -> Tuple.of(t._1))
                .orElseGet(Tuple1::empty);
    }


    /**
     * Concat a {@link Tuple2}'s values to this {@link Tuple0}.
     *
     * @param tuple
     *    The {@link Tuple2} to concat
     *
     * @return a new {@link Tuple2} with the tuple values appended
     */
    public <T1, T2> Tuple2<T1, T2> concat(final Tuple2<T1, T2> tuple) {
        return ofNullable(tuple)
                .map(t -> Tuple.of(t._1, t._2))
                .orElseGet(Tuple2::empty);
    }


    /**
     * Concat a {@link Tuple3}'s values to this {@link Tuple0}.
     *
     * @param tuple
     *   The {@link Tuple3} to concat
     *
     * @return a new {@link Tuple3} with the tuple values appended
     */
    public <T1, T2, T3> Tuple3<T1, T2, T3> concat(final Tuple3<T1, T2, T3> tuple) {
        return ofNullable(tuple)
                .map(t -> Tuple.of(t._1, t._2, t._3))
                .orElseGet(Tuple3::empty);
    }


    /**
     * Concat a {@link Tuple4}'s values to this {@link Tuple0}.
     *
     * @param tuple
     *   The {@link Tuple4} to concat
     *
     * @return a new {@link Tuple4} with the tuple values appended
     */
    public <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4> concat(final Tuple4<T1, T2, T3, T4> tuple) {
        return ofNullable(tuple)
                .map(t -> Tuple.of(t._1, t._2, t._3, t._4))
                .orElseGet(Tuple4::empty);
    }


    /**
     * Concat a {@link Tuple5}'s values to this {@link Tuple0}.
     *
     * @param tuple
     *   The {@link Tuple5} to concat
     *
     * @return a new {@link Tuple5} with the tuple values appended
     */
    public <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5> concat(final Tuple5<T1, T2, T3, T4, T5> tuple) {
        return ofNullable(tuple)
                .map(t -> Tuple.of(t._1, t._2, t._3, t._4, t._5))
                .orElseGet(Tuple5::empty);
    }

}
