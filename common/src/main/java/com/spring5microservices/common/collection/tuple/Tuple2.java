package com.spring5microservices.common.collection.tuple;

import org.springframework.util.Assert;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Optional.ofNullable;

/**
 * A {@link Tuple} of two elements.
 *
 * @param <T1> type of the 1st element
 * @param <T2> type of the 2nd element
 */
public final class Tuple2<T1, T2> implements Tuple {

    /**
     * The 1st element of this tuple.
     */
    public final T1 _1;

    /**
     * The 2nd element of this tuple.
     */
    public final T2 _2;


    private Tuple2(T1 t1, T2 t2) {
        this._1 = t1;
        this._2 = t2;
    }


    public static <T1, T2> Tuple2<T1, T2> of(final T1 t1,
                                             final T2 t2) {
        return new Tuple2<>(t1, t2);
    }


    public static <T1, T2> Tuple2<T1, T2> empty() {
        return new Tuple2<>(null, null);
    }


    public static <T1, T2> Comparator<Tuple2<T1, T2>> comparator(final Comparator<? super T1> t1Comp,
                                                                 final Comparator<? super T2> t2Comp) {
        return (t1, t2) -> {
            final int check1 = t1Comp.compare(t1._1, t2._1);
            if (check1 != 0) {
                return check1;
            }
            return t2Comp.compare(t1._2, t2._2);
        };
    }


    @SuppressWarnings("unchecked")
    public static <U1 extends Comparable<? super U1>, U2 extends Comparable<? super U2>> int compareTo(final Tuple2<?, ?> o1,
                                                                                                       final Tuple2<?, ?> o2) {
        final Tuple2<U1, U2> t1 = (Tuple2<U1, U2>) o1;
        final Tuple2<U1, U2> t2 = (Tuple2<U1, U2>) o2;

        final int check1 = t1._1.compareTo(t2._1);
        if (check1 != 0) {
            return check1;
        }
        return t1._2.compareTo(t2._2);
    }


    @Override
    public int arity() {
        return 2;
    }


    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Tuple2)) {
            return false;
        } else {
            final Tuple2<?, ?> that = (Tuple2<?, ?>) o;
            return Objects.equals(this._1, that._1)
                    && Objects.equals(this._2, that._2);
        }
    }


    @Override
    public int hashCode() {
        return Objects.hash(_1, _2);
    }


    @Override
    public String toString() {
        return "(" + _1 + ", " + _2 + ")";
    }


    /**
     * Sets the 1st element of this {@link Tuple2} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple2} with a new value for the 1st element of this {@link Tuple2}
     */
    public Tuple2<T1, T2> update1(final T1 value) {
        return of(value, _2);
    }


    /**
     * Remove the 1st value from this {@link Tuple2}.
     *
     * @return {@link Tuple1} with a copy of this {@link Tuple2} with the 1st value element removed
     */
    public Tuple1<T2> remove1() {
        return Tuple.of(_2);
    }


    /**
     * Sets the 2nd element of this {@link Tuple2} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple2} with a new value for the 2nd element of this {@link Tuple2}
     */
    public Tuple2<T1, T2> update2(final T2 value) {
        return of(_1, value);
    }


    /**
     * Remove the 2nd value from this {@link Tuple2}.
     *
     * @return {@link Tuple1} with a copy of this {@link Tuple2} with the 2nd value element removed
     */
    public Tuple1<T1> remove2() {
        return Tuple.of(_1);
    }


    /**
     * Swaps the elements of this {@link Tuple2}.
     *
     * @return A new {@link Tuple2} where the first element is the second element of this {@link Tuple2}
     *         and the second element is the first element of this {@link Tuple2}
     */
    public Tuple2<T2, T1> swap() {
        return of(_2, _1);
    }


    /**
     * Converts the {@link Tuple2} to {@link Map.Entry}.
     *
     * @return A {@link Map.Entry} where the first element is the key and the second
     *         element is the value.
     */
    public Map.Entry<T1, T2> toEntry() {
        return new AbstractMap.SimpleEntry<>(_1, _2);
    }


    /**
     * Maps the components of this {@link Tuple2} using a mapper function.
     *
     * @param mapper
     *    The mapper {@link BiFunction}
     *
     * @return A new {@link Tuple2}
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null}
     */
    public <U1, U2> Tuple2<U1, U2> map(final BiFunction<? super T1, ? super T2, Tuple2<U1, U2>> mapper) {
        Assert.notNull(mapper, "mapper must be not null");
        return mapper.apply(_1, _2);
    }


    /**
     * Maps the components of this {@link Tuple2} using a mapper function for each component.
     *
     * @param f1
     *    The mapper {@link Function} of the 1st component
     * @param f2
     *    The mapper {@link Function} of the 2nd component
     *
     * @return A new {@link Tuple2}.
     *
     * @throws IllegalArgumentException if {@code f1} or {@code f2} are {@code null}
     */
    public <U1, U2> Tuple2<U1, U2> map(final Function<? super T1, ? extends U1> f1,
                                       final Function<? super T2, ? extends U2> f2) {
        Assert.notNull(f1, "f1 must be not null");
        Assert.notNull(f2, "f2 must be not null");
        return of(f1.apply(_1), f2.apply(_2));
    }


    /**
     * Maps the 1st component of this {@link Tuple2} to a new value.
     *
     * @param mapper
     *    A mapping {@link Function}
     *
     * @return a new {@link Tuple2} based on this one and substituted 1st component
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null}
     */
    public <U> Tuple2<U, T2> map1(final Function<? super T1, ? extends U> mapper) {
        Assert.notNull(mapper, "mapper must be not null");
        final U u = mapper.apply(_1);
        return of(u, _2);
    }


    /**
     * Maps the 2nd component of this {@link Tuple2} to a new value.
     *
     * @param mapper
     *    A mapping {@link Function}
     *
     * @return a new {@link Tuple2} based on this one and substituted 2nd component
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null}
     */
    public <U> Tuple2<T1, U> map2(final Function<? super T2, ? extends U> mapper) {
        Assert.notNull(mapper, "mapper must be not null");
        final U u = mapper.apply(_2);
        return of(_1, u);
    }


    /**
     * Transforms this {@link Tuple2} to an object of type U.
     *
     * @param f
     *    Transformation {@link BiFunction} which creates a new object of type U based on this tuple's contents.
     *
     * @return An object of type U
     *
     * @throws IllegalArgumentException if {@code f} is {@code null}
     */
    public <U> U apply(final BiFunction<? super T1, ? super T2, ? extends U> f) {
        Assert.notNull(f, "f must be not null");
        return f.apply(_1, _2);
    }


    /**
     * Prepend a value to this {@link Tuple2}.
     *
     * @param t
     *    The value to prepend
     *
     * @return a new {@link Tuple3} with the value prepended
     */
    public <T> Tuple3<T, T1, T2> prepend(final T t) {
        return Tuple.of(t, _1, _2);
    }


    /**
     * Append a value to this {@link Tuple2}.
     *
     * @param t
     *    The value to append
     *
     * @return a new {@link Tuple3} with the value appended
     */
    public <T> Tuple3<T1, T2, T> append(final T t) {
        return Tuple.of(_1, _2, t);
    }


    /**
     * Concat a {@link Tuple1}'s values to this {@link Tuple2}.
     *
     * @param tuple
     *    The {@link Tuple1} to concat
     *
     * @return a new {@link Tuple3} with the tuple values appended
     */
    public <T3> Tuple3<T1, T2, T3> concat(final Tuple1<T3> tuple) {
        return ofNullable(tuple)
                .map(t -> Tuple.of(_1, _2, t._1))
                .orElseGet(() -> Tuple.of(_1, _2, null));
    }


    /**
     * Concat a {@link Tuple2}'s values to this {@link Tuple2}.
     *
     * @param tuple
     *    The {@link Tuple2} to concat
     *
     * @return a new {@link Tuple4} with the tuple values appended
     */
    public <T3, T4> Tuple4<T1, T2, T3, T4> concat(final Tuple2<T3, T4> tuple) {
        return ofNullable(tuple)
                .map(t -> Tuple.of(_1, _2, t._1, t._2))
                .orElseGet(() -> Tuple.of(_1, _2, null, null));
    }

}
