package com.spring5microservices.common.collection.tuple;

import org.springframework.util.Assert;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;

import static java.util.Optional.ofNullable;

/**
 * A {@link Tuple} of one element.
 *
 * @param <T1> type of the 1st element
 */
public final class Tuple1<T1> implements Tuple {

    /**
     * The 1st element of this tuple.
     */
    public final T1 _1;


    private Tuple1(T1 t1) {
        this._1 = t1;
    }


    public static <T1> Tuple1<T1> of(final T1 t1) {
        return new Tuple1<>(t1);
    }


    public static <T1> Tuple1<T1> empty() {
        return new Tuple1<>(null);
    }


    public static <T1> Comparator<Tuple1<T1>> comparator(final Comparator<? super T1> t1Comp) {
        return (t1, t2) ->
                t1Comp.compare(t1._1, t2._1);
    }


    @SuppressWarnings("unchecked")
    public static <U1 extends Comparable<? super U1>> int compareTo(final Tuple1<?> o1,
                                                                    final Tuple1<?> o2) {
        final Tuple1<U1> t1 = (Tuple1<U1>) o1;
        final Tuple1<U1> t2 = (Tuple1<U1>) o2;
        return t1._1.compareTo(t2._1);
    }


    @Override
    public int arity() {
        return 1;
    }


    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Tuple1)) {
            return false;
        } else {
            final Tuple1<?> that = (Tuple1<?>) o;
            return Objects.equals(this._1, that._1);
        }
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(_1);
    }


    @Override
    public String toString() {
        return "(" + _1 + ")";
    }


    /**
     * Sets the 1st element of this {@link Tuple1} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple1} with a new value for the 1st element of this {@link Tuple1}
     */
    public Tuple1<T1> update1(final T1 value) {
        return of(value);
    }


    /**
     * Remove the 1st value from this {@link Tuple1}.
     *
     * @return {@link Tuple0} with a copy of this {@link Tuple1} with the 1st value element removed
     */
    public Tuple0 remove1() {
        return Tuple.empty();
    }


    /**
     * Maps the components of this {@link Tuple1} using a mapper function.
     *
     * @param mapper
     *    The mapper {@link Function}
     *
     * @return A new {@link Tuple1}
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null}
     */
    public <U1> Tuple1<U1> map(final Function<? super T1, ? extends U1> mapper) {
        Assert.notNull(mapper, "mapper must be not null");
        return of(mapper.apply(_1));
    }


    /**
     * Transforms this {@link Tuple1} to an object of type U.
     *
     * @param f
     *    Transformation {@link Function} which creates a new object of type U based on this tuple's contents.
     *
     * @return An object of type U
     *
     * @throws IllegalArgumentException if {@code f} is {@code null}
     */
    public <U> U apply(final Function<? super T1, ? extends U> f) {
        Assert.notNull(f, "f must be not null");
        return f.apply(_1);
    }


    /**
     * Prepend a value to this {@link Tuple1}.
     *
     * @param t
     *    The value to prepend
     *
     * @return a new {@link Tuple2} with the value prepended
     */
    public <T> Tuple2<T, T1> prepend(final T t) {
        return Tuple.of(t, _1);
    }


    /**
     * Append a value to this {@link Tuple1}.
     *
     * @param t
     *    The value to append
     *
     * @return a new {@link Tuple2} with the value appended
     */
    public <T> Tuple2<T1, T> append(final T t) {
        return Tuple.of(_1, t);
    }


    /**
     * Concat a {@link Tuple1}'s values to this {@link Tuple1}.
     *
     * @param tuple
     *    The {@link Tuple1} to concat
     *
     * @return a new {@link Tuple2} with the tuple values appended
     */
    public <T2> Tuple2<T1, T2> concat(final Tuple1<T2> tuple) {
        return ofNullable(tuple)
                .map(t -> Tuple.of(_1, t._1))
                .orElseGet(() -> Tuple.of(_1, null));
    }


    /**
     * Concat a {@link Tuple2}'s values to this {@link Tuple1}.
     *
     * @param tuple
     *    The {@link Tuple2} to concat
     *
     * @return a new {@link Tuple3} with the tuple values appended
     */
    public <T2, T3> Tuple3<T1, T2, T3> concat(final Tuple2<T2, T3> tuple) {
        return ofNullable(tuple)
                .map(t -> Tuple.of(_1, t._1, t._2))
                .orElseGet(() -> Tuple.of(_1, null, null));
    }


    /**
     * Concat a {@link Tuple3}'s values to this {@link Tuple1}.
     *
     * @param tuple
     *    The {@link Tuple3} to concat
     *
     * @return a new {@link Tuple4} with the tuple values appended
     */
    public <T2, T3, T4> Tuple4<T1, T2, T3, T4> concat(final Tuple3<T2, T3, T4> tuple) {
        return ofNullable(tuple)
                .map(t -> Tuple.of(_1, t._1, t._2, t._3))
                .orElseGet(() -> Tuple.of(_1, null, null, null));
    }

}
