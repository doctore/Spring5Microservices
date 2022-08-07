package com.spring5microservices.common.collection.tuple;

import com.spring5microservices.common.interfaces.functional.QuadFunction;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;

import static java.util.Optional.ofNullable;

/**
 * A {@link Tuple} of four elements.
 *
 * @param <T1>
 *    Type of the 1st element
 * @param <T2>
 *    Type of the 2nd element
 * @param <T3>
 *    Type of the 3rd element
 * @param <T4>
 *    Type of the 4th element
 */
public final class Tuple4<T1, T2, T3, T4> implements Tuple, Serializable {

    private static final long serialVersionUID = 6896787091356264950L;

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

    /**
     * The 4th element of this tuple.
     */
    public final T4 _4;


    private Tuple4(T1 t1, T2 t2, T3 t3, T4 t4) {
        this._1 = t1;
        this._2 = t2;
        this._3 = t3;
        this._4 = t4;
    }


    public static <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4> of(final T1 t1,
                                                             final T2 t2,
                                                             final T3 t3,
                                                             final T4 t4) {
        return new Tuple4<>(t1, t2, t3, t4);
    }


    public static <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4> empty() {
        return new Tuple4<>(null, null, null, null);
    }


    public static <T1, T2, T3, T4> Comparator<Tuple4<T1, T2, T3, T4>> comparator(final Comparator<? super T1> t1Comp,
                                                                                 final Comparator<? super T2> t2Comp,
                                                                                 final Comparator<? super T3> t3Comp,
                                                                                 final Comparator<? super T4> t4Comp) {
        return (t1, t2) -> {
            final int check1 = t1Comp.compare(t1._1, t2._1);
            if (check1 != 0) {
                return check1;
            }
            final int check2 = t2Comp.compare(t1._2, t2._2);
            if (check2 != 0) {
                return check2;
            }
            final int check3 = t3Comp.compare(t1._3, t2._3);
            if (check3 != 0) {
                return check3;
            }
            return t4Comp.compare(t1._4, t2._4);
        };
    }


    @SuppressWarnings("unchecked")
    public static <U1 extends Comparable<? super U1>,
                   U2 extends Comparable<? super U2>,
                   U3 extends Comparable<? super U3>,
                   U4 extends Comparable<? super U4>> int compareTo(final Tuple4<?, ?, ?, ?> o1,
                                                                    final Tuple4<?, ?, ?, ?> o2) {
        final Tuple4<U1, U2, U3, U4> t1 = (Tuple4<U1, U2, U3, U4>) o1;
        final Tuple4<U1, U2, U3, U4> t2 = (Tuple4<U1, U2, U3, U4>) o2;

        final int check1 = t1._1.compareTo(t2._1);
        if (check1 != 0) {
            return check1;
        }
        final int check2 = t1._2.compareTo(t2._2);
        if (check2 != 0) {
            return check2;
        }
        final int check3 = t1._3.compareTo(t2._3);
        if (check3 != 0) {
            return check3;
        }
        return t1._4.compareTo(t2._4);
    }


    @Override
    public int arity() {
        return 4;
    }


    @Override
    public boolean equals(Object obj) {
        return obj == this ||
                (obj instanceof Tuple4 &&
                        Objects.equals(_1, ((Tuple4<?, ?, ?, ?>) obj)._1) &&
                        Objects.equals(_2, ((Tuple4<?, ?, ?, ?>) obj)._2) &&
                        Objects.equals(_3, ((Tuple4<?, ?, ?, ?>) obj)._3) &&
                        Objects.equals(_4, ((Tuple4<?, ?, ?, ?>) obj)._4)
                );
    }


    @Override
    public int hashCode() {
        return Objects.hash(_1, _2, _3, _4);
    }


    @Override
    public String toString() {
        return "(" + _1 + ", " + _2 + ", " + _3 + ", " + _4 + ")";
    }


    /**
     * Sets the 1st element of this {@link Tuple4} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple4} with a new value for the 1st element of this {@link Tuple4}
     */
    public Tuple4<T1, T2, T3, T4> update1(final T1 value) {
        return of(value, _2, _3, _4);
    }


    /**
     * Remove the 1st value from this {@link Tuple4}.
     *
     * @return {@link Tuple3} with a copy of this {@link Tuple4} with the 1st value element removed
     */
    public Tuple3<T2, T3, T4> remove1() {
        return Tuple.of(_2, _3, _4);
    }


    /**
     * Sets the 2nd element of this {@link Tuple4} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple4} with a new value for the 2nd element of this {@link Tuple4}
     */
    public Tuple4<T1, T2, T3, T4> update2(final T2 value) {
        return of(_1, value, _3, _4);
    }


    /**
     * Remove the 2nd value from this {@link Tuple4}.
     *
     * @return {@link Tuple3} with a copy of this {@link Tuple4} with the 2nd value element removed
     */
    public Tuple3<T1, T3, T4> remove2() {
        return Tuple.of(_1, _3, _4);
    }


    /**
     * Sets the 3rd element of this {@link Tuple4} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple4} with a new value for the 3rd element of this {@link Tuple4}
     */
    public Tuple4<T1, T2, T3, T4> update3(final T3 value) {
        return of(_1, _2, value, _4);
    }


    /**
     * Remove the 3rd value from this {@link Tuple4}.
     *
     * @return {@link Tuple3} with a copy of this {@link Tuple4} with the 3rd value element removed
     */
    public Tuple3<T1, T2, T4> remove3() {
        return Tuple.of(_1, _2, _4);
    }


    /**
     * Sets the 4th element of this {@link Tuple4} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple4} with a new value for the 4th element of this {@link Tuple4}
     */
    public Tuple4<T1, T2, T3, T4> update4(final T4 value) {
        return of(_1, _2, _3, value);
    }


    /**
     * Remove the 4th value from this {@link Tuple4}.
     *
     * @return {@link Tuple3} with a copy of this {@link Tuple4} with the 4th value element removed
     */
    public Tuple3<T1, T2, T3> remove4() {
        return Tuple.of(_1, _2, _3);
    }


    /**
     * Maps the components of this {@link Tuple4} using a mapper function.
     *
     * @param mapper
     *    The mapper {@link QuadFunction}
     *
     * @return A new {@link Tuple4} of same arity
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null}
     */
    public <U1, U2, U3, U4> Tuple4<U1, U2, U3, U4> map(final QuadFunction<? super T1, ? super T2, ? super T3, ? super T4, Tuple4<U1, U2, U3, U4>> mapper) {
        Assert.notNull(mapper, "mapper must be not null");
        return mapper.apply(_1, _2, _3, _4);
    }


    /**
     * Maps the components of this {@link Tuple4} using a mapper function for each component.
     *
     * @param f1
     *    The mapper {@link Function} of the 1st component
     * @param f2
     *    The mapper {@link Function} of the 2nd component
     * @param f3
     *    The mapper {@link Function} of the 3rd component
     * @param f4
     *    The mapper {@link Function} of the 4th component
     *
     * @return A new {@link Tuple4} of same arity.
     *
     * @throws IllegalArgumentException if {@code f1}, {@code f2}, {@code f3} or {@code f4} are {@code null}
     */
    public <U1, U2, U3, U4> Tuple4<U1, U2, U3, U4> map(final Function<? super T1, ? extends U1> f1,
                                                       final Function<? super T2, ? extends U2> f2,
                                                       final Function<? super T3, ? extends U3> f3,
                                                       final Function<? super T4, ? extends U4> f4) {
        Assert.notNull(f1, "f1 must be not null");
        Assert.notNull(f2, "f2 must be not null");
        Assert.notNull(f3, "f3 must be not null");
        Assert.notNull(f4, "f4 must be not null");
        return of(f1.apply(_1), f2.apply(_2), f3.apply(_3), f4.apply(_4));
    }


    /**
     * Maps the 1st component of this {@link Tuple4} to a new value.
     *
     * @param mapper
     *    A mapping {@link Function}
     *
     * @return a new {@link Tuple4} based on this one and substituted 1st component
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null}
     */
    public <U> Tuple4<U, T2, T3, T4> map1(final Function<? super T1, ? extends U> mapper) {
        Assert.notNull(mapper, "mapper must be not null");
        final U u = mapper.apply(_1);
        return of(u, _2, _3, _4);
    }


    /**
     * Maps the 2nd component of this {@link Tuple4} to a new value.
     *
     * @param mapper
     *    A mapping {@link Function}
     *
     * @return a new {@link Tuple4} based on this one and substituted 2nd component
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null}
     */
    public <U> Tuple4<T1, U, T3, T4> map2(final Function<? super T2, ? extends U> mapper) {
        Assert.notNull(mapper, "mapper must be not null");
        final U u = mapper.apply(_2);
        return Tuple.of(_1, u, _3, _4);
    }


    /**
     * Maps the 3rd component of this {@link Tuple4} to a new value.
     *
     * @param mapper
     *    A mapping {@link Function}
     *
     * @return a new {@link Tuple4} based on this one and substituted 3rd component
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null}
     */
    public <U> Tuple4<T1, T2, U, T4> map3(final Function<? super T3, ? extends U> mapper) {
        Assert.notNull(mapper, "mapper must be not null");
        final U u = mapper.apply(_3);
        return Tuple.of(_1, _2, u, _4);
    }


    /**
     * Maps the 4th component of this {@link Tuple4} to a new value.
     *
     * @param mapper
     *    A mapping {@link Function}
     *
     * @return a new {@link Tuple4} based on this one and substituted 4th component
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null}
     */
    public <U> Tuple4<T1, T2, T3, U> map4(final Function<? super T4, ? extends U> mapper) {
        Assert.notNull(mapper, "mapper must be not null");
        final U u = mapper.apply(_4);
        return Tuple.of(_1, _2, _3, u);
    }


    /**
     * Transforms this {@link Tuple4} to an object of type U.
     *
     * @param f
     *    Transformation {@link QuadFunction} which creates a new object of type U based on this tuple's contents.
     *
     * @return An object of type U
     *
     * @throws IllegalArgumentException if {@code f} is {@code null}
     */
    public <U> U apply(final QuadFunction<? super T1, ? super T2, ? super T3, ? super T4, ? extends U> f) {
        Assert.notNull(f, "f must be not null");
        return f.apply(_1, _2, _3, _4);
    }


    /**
     * Prepend a value to this {@link Tuple4}.
     *
     * @param t
     *    The value to prepend
     *
     * @return a new {@link Tuple5} with the value prepended
     */
    public <T> Tuple5<T, T1, T2, T3, T4> prepend(final T t) {
        return Tuple.of(t, _1, _2, _3, _4);
    }


    /**
     * Append a value to this {@link Tuple4}.
     *
     * @param t
     *    The value to append
     *
     * @return a new {@link Tuple5} with the value appended
     */
    public <T> Tuple5<T1, T2, T3, T4, T> append(final T t) {
        return Tuple.of(_1, _2, _3, _4, t);
    }


    /**
     * Concat a {@link Tuple1}'s values to this {@link Tuple4}.
     *
     * @param tuple
     *    The {@link Tuple1} to concat
     *
     * @return a new {@link Tuple5} with the tuple values appended
     */
    public <T5> Tuple5<T1, T2, T3, T4, T5> concat(final Tuple1<T5> tuple) {
        return ofNullable(tuple)
                .map(t -> Tuple.of(_1, _2, _3, _4, t._1))
                .orElseGet(() -> Tuple.of(_1, _2, _3, _4, null));
    }

}
