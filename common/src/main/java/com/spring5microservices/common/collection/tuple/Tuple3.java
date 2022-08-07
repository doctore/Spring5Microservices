package com.spring5microservices.common.collection.tuple;

import com.spring5microservices.common.interfaces.functional.TriFunction;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;

import static java.util.Optional.ofNullable;

/**
 * A {@link Tuple} of three elements.
 *
 * @param <T1>
 *    Type of the 1st element
 * @param <T2>
 *    Type of the 2nd element
 * @param <T3>
 *    Type of the 3rd element
 */
public final class Tuple3<T1, T2, T3> implements Tuple, Serializable {

    private static final long serialVersionUID = -2829082275463455053L;

    /**
     * The 1st element of this tuple.
     */
    public final T1 _1;

    /**
     * The 2nd element of this tuple.
     */
    public final T2 _2;

    /**
     * The 3rd element of this tuple.
     */
    public final T3 _3;


    private Tuple3(T1 t1, T2 t2, T3 t3) {
        this._1 = t1;
        this._2 = t2;
        this._3 = t3;
    }


    public static <T1, T2, T3> Tuple3<T1, T2, T3> of(final T1 t1,
                                                     final T2 t2,
                                                     final T3 t3) {
        return new Tuple3<>(t1, t2, t3);
    }


    public static <T1, T2, T3> Tuple3<T1, T2, T3> empty() {
        return new Tuple3<>(null, null, null);
    }


    public static <T1, T2, T3> Comparator<Tuple3<T1, T2, T3>> comparator(final Comparator<? super T1> t1Comp,
                                                                         final Comparator<? super T2> t2Comp,
                                                                         final Comparator<? super T3> t3Comp) {
        return (t1, t2) -> {
            final int check1 = t1Comp.compare(t1._1, t2._1);
            if (check1 != 0) {
                return check1;
            }
            final int check2 = t2Comp.compare(t1._2, t2._2);
            if (check2 != 0) {
                return check2;
            }
            return t3Comp.compare(t1._3, t2._3);
        };
    }


    @SuppressWarnings("unchecked")
    public static <U1 extends Comparable<? super U1>, U2 extends Comparable<? super U2>, U3 extends Comparable<? super U3>> int compareTo(final Tuple3<?, ?, ?> o1,
                                                                                                                                          final Tuple3<?, ?, ?> o2) {
        final Tuple3<U1, U2, U3> t1 = (Tuple3<U1, U2, U3>) o1;
        final Tuple3<U1, U2, U3> t2 = (Tuple3<U1, U2, U3>) o2;

        final int check1 = t1._1.compareTo(t2._1);
        if (check1 != 0) {
            return check1;
        }
        final int check2 = t1._2.compareTo(t2._2);
        if (check2 != 0) {
            return check2;
        }
        return t1._3.compareTo(t2._3);
    }


    @Override
    public int arity() {
        return 3;
    }


    @Override
    public boolean equals(Object obj) {
        return obj == this ||
                (obj instanceof Tuple3 &&
                        Objects.equals(_1, ((Tuple3<?, ?, ?>) obj)._1) &&
                        Objects.equals(_2, ((Tuple3<?, ?, ?>) obj)._2) &&
                        Objects.equals(_3, ((Tuple3<?, ?, ?>) obj)._3)
                );
    }


    @Override
    public int hashCode() {
        return Objects.hash(_1, _2, _3);
    }


    @Override
    public String toString() {
        return "(" + _1 + ", " + _2 + ", " + _3 + ")";
    }


    /**
     * Sets the 1st element of this {@link Tuple3} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple3} with a new value for the 1st element of this {@link Tuple3}
     */
    public Tuple3<T1, T2, T3> update1(final T1 value) {
        return of(value, _2, _3);
    }


    /**
     * Remove the 1st value from this {@link Tuple3}.
     *
     * @return {@link Tuple2} with a copy of this {@link Tuple3} with the 1st value element removed
     */
    public Tuple2<T2, T3> remove1() {
        return Tuple.of(_2, _3);
    }


    /**
     * Sets the 2nd element of this {@link Tuple3} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple3} with a new value for the 2nd element of this {@link Tuple3}
     */
    public Tuple3<T1, T2, T3> update2(final T2 value) {
        return of(_1, value, _3);
    }


    /**
     * Remove the 2nd value from this {@link Tuple3}.
     *
     * @return {@link Tuple2} with a copy of this {@link Tuple3} with the 2nd value element removed
     */
    public Tuple2<T1, T3> remove2() {
        return Tuple.of(_1, _3);
    }


    /**
     * Sets the 3rd element of this {@link Tuple3} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple3} with a new value for the 3rd element of this {@link Tuple3}
     */
    public Tuple3<T1, T2, T3> update3(final T3 value) {
        return of(_1, _2, value);
    }


    /**
     * Remove the 3rd value from this {@link Tuple3}.
     *
     * @return {@link Tuple2} with a copy of this {@link Tuple3} with the 3rd value element removed
     */
    public Tuple2<T1, T2> remove3() {
        return Tuple.of(_1, _2);
    }


    /**
     * Maps the components of this {@link Tuple3} using a mapper function.
     *
     * @param mapper
     *    The mapper {@link TriFunction}
     *
     * @return A new {@link Tuple3} of same arity
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null}
     */
    public <U1, U2, U3> Tuple3<U1, U2, U3> map(final TriFunction<? super T1, ? super T2, ? super T3, Tuple3<U1, U2, U3>> mapper) {
        Assert.notNull(mapper, "mapper must be not null");
        return mapper.apply(_1, _2, _3);
    }


    /**
     * Maps the components of this {@link Tuple3} using a mapper function for each component.
     *
     * @param f1
     *    The mapper {@link Function} of the 1st component
     * @param f2
     *    The mapper {@link Function} of the 2nd component
     * @param f3
     *    The mapper {@link Function} of the 3rd component
     *
     * @return A new {@link Tuple3} of same arity.
     *
     * @throws IllegalArgumentException if {@code f1}, {@code f2} or {@code f3} are {@code null}
     */
    public <U1, U2, U3> Tuple3<U1, U2, U3> map(final Function<? super T1, ? extends U1> f1,
                                               final Function<? super T2, ? extends U2> f2,
                                               final Function<? super T3, ? extends U3> f3) {
        Assert.notNull(f1, "f1 must be not null");
        Assert.notNull(f2, "f2 must be not null");
        Assert.notNull(f3, "f3 must be not null");
        return of(f1.apply(_1), f2.apply(_2), f3.apply(_3));
    }


    /**
     * Maps the 1st component of this {@link Tuple3} to a new value.
     *
     * @param mapper
     *    A mapping {@link Function}
     *
     * @return a new {@link Tuple3} based on this one and substituted 1st component
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null}
     */
    public <U> Tuple3<U, T2, T3> map1(final Function<? super T1, ? extends U> mapper) {
        Assert.notNull(mapper, "mapper must be not null");
        final U u = mapper.apply(_1);
        return of(u, _2, _3);
    }


    /**
     * Maps the 2nd component of this {@link Tuple3} to a new value.
     *
     * @param mapper
     *    A mapping {@link Function}
     *
     * @return a new {@link Tuple3} based on this one and substituted 2nd component
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null}
     */
    public <U> Tuple3<T1, U, T3> map2(final Function<? super T2, ? extends U> mapper) {
        Assert.notNull(mapper, "mapper must be not null");
        final U u = mapper.apply(_2);
        return Tuple.of(_1, u, _3);
    }


    /**
     * Maps the 3rd component of this {@link Tuple3} to a new value.
     *
     * @param mapper
     *    A mapping {@link Function}
     *
     * @return a new {@link Tuple3} based on this one and substituted 3rd component
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null}
     */
    public <U> Tuple3<T1, T2, U> map3(final Function<? super T3, ? extends U> mapper) {
        Assert.notNull(mapper, "mapper must be not null");
        final U u = mapper.apply(_3);
        return Tuple.of(_1, _2, u);
    }


    /**
     * Transforms this {@link Tuple3} to an object of type U.
     *
     * @param f
     *    Transformation {@link TriFunction} which creates a new object of type U based on this tuple's contents.
     *
     * @return An object of type U
     *
     * @throws IllegalArgumentException if {@code f} is {@code null}
     */
    public <U> U apply(final TriFunction<? super T1, ? super T2, ? super T3, ? extends U> f) {
        Assert.notNull(f, "f must be not null");
        return f.apply(_1, _2, _3);
    }


    /**
     * Prepend a value to this {@link Tuple3}.
     *
     * @param t
     *    The value to prepend
     *
     * @return a new {@link Tuple4} with the value prepended
     */
    public <T> Tuple4<T, T1, T2, T3> prepend(final T t) {
        return Tuple.of(t, _1, _2, _3);
    }


    /**
     * Append a value to this {@link Tuple3}.
     *
     * @param t
     *    The value to append
     *
     * @return a new {@link Tuple4} with the value appended
     */
    public <T> Tuple4<T1, T2, T3, T> append(final T t) {
        return Tuple.of(_1, _2, _3, t);
    }


    /**
     * Concat a {@link Tuple1}'s values to this {@link Tuple3}.
     *
     * @param tuple
     *    The {@link Tuple1} to concat
     *
     * @return a new {@link Tuple4} with the tuple values appended
     */
    public <T4> Tuple4<T1, T2, T3, T4> concat(final Tuple1<T4> tuple) {
        return ofNullable(tuple)
                .map(t -> Tuple.of(_1, _2, _3, t._1))
                .orElseGet(() -> Tuple.of(_1, _2, _3, null));
    }


    /**
     * Concat a {@link Tuple2}'s values to this {@link Tuple3}.
     *
     * @param tuple
     *    The {@link Tuple2} to concat
     *
     * @return a new {@link Tuple5} with the tuple values appended
     */
    public <T4, T5> Tuple5<T1, T2, T3, T4, T5> concat(final Tuple2<T4, T5> tuple) {
        return ofNullable(tuple)
                .map(t -> Tuple.of(_1, _2, _3, t._1, t._2))
                .orElseGet(() -> Tuple.of(_1, _2, _3, null, null));
    }

}
