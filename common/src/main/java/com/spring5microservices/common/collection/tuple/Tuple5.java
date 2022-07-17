package com.spring5microservices.common.collection.tuple;

import com.spring5microservices.common.interfaces.functional.PentaFunction;
import org.springframework.util.Assert;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;

/**
 * A {@link Tuple} of five elements.
 *
 * @param <T1>
 *    Type of the 1st element
 * @param <T2>
 *    Type of the 2nd element
 * @param <T3>
 *    Type of the 3rd element
 * @param <T4>
 *    Type of the 4th element
 * @param <T5>
 *    Type of the 5th element
 */
public class Tuple5<T1, T2, T3, T4, T5> implements Tuple {

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

    /**
     * The 5th element of this tuple.
     */
    public final T5 _5;


    private Tuple5(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
        this._1 = t1;
        this._2 = t2;
        this._3 = t3;
        this._4 = t4;
        this._5 = t5;
    }


    public static <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5> of(final T1 t1,
                                                                     final T2 t2,
                                                                     final T3 t3,
                                                                     final T4 t4,
                                                                     final T5 t5) {
        return new Tuple5<>(t1, t2, t3, t4, t5);
    }


    public static <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5> empty() {
        return new Tuple5<>(null, null, null, null, null);
    }


    public static <T1, T2, T3, T4, T5> Comparator<Tuple5<T1, T2, T3, T4, T5>> comparator(final Comparator<? super T1> t1Comp,
                                                                                         final Comparator<? super T2> t2Comp,
                                                                                         final Comparator<? super T3> t3Comp,
                                                                                         final Comparator<? super T4> t4Comp,
                                                                                         final Comparator<? super T5> t5Comp) {
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
            final int check4 = t4Comp.compare(t1._4, t2._4);
            if (check4 != 0) {
                return check4;
            }
            return t5Comp.compare(t1._5, t2._5);
        };
    }


    @SuppressWarnings("unchecked")
    public static <U1 extends Comparable<? super U1>,
                   U2 extends Comparable<? super U2>,
                   U3 extends Comparable<? super U3>,
                   U4 extends Comparable<? super U4>,
                   U5 extends Comparable<? super U5>> int compareTo(final Tuple5<?, ?, ?, ?, ?> o1,
                                                                    final Tuple5<?, ?, ?, ?, ?> o2) {
        final Tuple5<U1, U2, U3, U4, U5> t1 = (Tuple5<U1, U2, U3, U4, U5>) o1;
        final Tuple5<U1, U2, U3, U4, U5> t2 = (Tuple5<U1, U2, U3, U4, U5>) o2;

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
        final int check4 = t1._4.compareTo(t2._4);
        if (check4 != 0) {
            return check4;
        }
        return t1._5.compareTo(t2._5);
    }


    @Override
    public int arity() {
        return 5;
    }


    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Tuple5)) {
            return false;
        } else {
            final Tuple5<?, ?, ?, ?, ?> that = (Tuple5<?, ?, ?, ?, ?>) o;
            return Objects.equals(this._1, that._1)
                    && Objects.equals(this._2, that._2)
                    && Objects.equals(this._3, that._3)
                    && Objects.equals(this._4, that._4)
                    && Objects.equals(this._5, that._5);
        }
    }


    @Override
    public int hashCode() {
        return Objects.hash(_1, _2, _3, _4, _5);
    }


    @Override
    public String toString() {
        return "(" + _1 + ", " + _2 + ", " + _3 + ", " + _4 + ", " + _5 + ")";
    }


    /**
     * Sets the 1st element of this {@link Tuple5} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple5} with a new value for the 1st element of this {@link Tuple5}
     */
    public Tuple5<T1, T2, T3, T4, T5> update1(final T1 value) {
        return of(value, _2, _3, _4, _5);
    }


    /**
     * Remove the 1st value from this {@link Tuple5}.
     *
     * @return {@link Tuple4} with a copy of this {@link Tuple5} with the 1st value element removed
     */
    public Tuple4<T2, T3, T4, T5> remove1() {
        return Tuple.of(_2, _3, _4, _5);
    }


    /**
     * Sets the 2nd element of this {@link Tuple5} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple5} with a new value for the 2nd element of this {@link Tuple5}
     */
    public Tuple5<T1, T2, T3, T4, T5> update2(final T2 value) {
        return of(_1, value, _3, _4, _5);
    }


    /**
     * Remove the 2nd value from this {@link Tuple5}.
     *
     * @return {@link Tuple4} with a copy of this {@link Tuple5} with the 2nd value element removed
     */
    public Tuple4<T1, T3, T4, T5> remove2() {
        return Tuple.of(_1, _3, _4, _5);
    }


    /**
     * Sets the 3rd element of this {@link Tuple5} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple5} with a new value for the 3rd element of this {@link Tuple5}
     */
    public Tuple5<T1, T2, T3, T4, T5> update3(final T3 value) {
        return of(_1, _2, value, _4, _5);
    }


    /**
     * Remove the 3rd value from this {@link Tuple5}.
     *
     * @return {@link Tuple4} with a copy of this {@link Tuple5} with the 3rd value element removed
     */
    public Tuple4<T1, T2, T4, T5> remove3() {
        return Tuple.of(_1, _2, _4, _5);
    }


    /**
     * Sets the 4th element of this {@link Tuple5} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple5} with a new value for the 4th element of this {@link Tuple5}
     */
    public Tuple5<T1, T2, T3, T4, T5> update4(final T4 value) {
        return of(_1, _2, _3, value, _5);
    }


    /**
     * Remove the 4th value from this {@link Tuple5}.
     *
     * @return {@link Tuple4} with a copy of this {@link Tuple5} with the 4th value element removed
     */
    public Tuple4<T1, T2, T3, T5> remove4() {
        return Tuple.of(_1, _2, _3, _5);
    }


    /**
     * Sets the 5th element of this {@link Tuple5} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple5} with a new value for the 5th element of this {@link Tuple5}
     */
    public Tuple5<T1, T2, T3, T4, T5> update5(final T5 value) {
        return of(_1, _2, _3, _4, value);
    }


    /**
     * Remove the 5th value from this {@link Tuple5}.
     *
     * @return {@link Tuple4} with a copy of this {@link Tuple5} with the 5th value element removed
     */
    public Tuple4<T1, T2, T3, T4> remove5() {
        return Tuple.of(_1, _2, _3, _4);
    }


    /**
     * Maps the components of this {@link Tuple5} using a mapper function.
     *
     * @param mapper
     *    The mapper {@link PentaFunction}
     *
     * @return A new {@link Tuple5} of same arity
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null}
     */
    public <U1, U2, U3, U4, U5> Tuple5<U1, U2, U3, U4, U5> map(final PentaFunction<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, Tuple5<U1, U2, U3, U4, U5>> mapper) {
        Assert.notNull(mapper, "mapper must be not null");
        return mapper.apply(_1, _2, _3, _4, _5);
    }


    /**
     * Maps the components of this {@link Tuple5} using a mapper function for each component.
     *
     * @param f1
     *    The mapper {@link Function} of the 1st component
     * @param f2
     *    The mapper {@link Function} of the 2nd component
     * @param f3
     *    The mapper {@link Function} of the 3rd component
     * @param f4
     *    The mapper {@link Function} of the 4th component
     * @param f5
     *    The mapper {@link Function} of the 5th component
     *
     * @return A new {@link Tuple5} of same arity.
     *
     * @throws IllegalArgumentException if {@code f1}, {@code f2}, {@code f3}, {@code f4} or {@code f5} are {@code null}
     */
    public <U1, U2, U3, U4, U5> Tuple5<U1, U2, U3, U4, U5> map(final Function<? super T1, ? extends U1> f1,
                                                               final Function<? super T2, ? extends U2> f2,
                                                               final Function<? super T3, ? extends U3> f3,
                                                               final Function<? super T4, ? extends U4> f4,
                                                               final Function<? super T5, ? extends U5> f5) {
        Assert.notNull(f1, "f1 must be not null");
        Assert.notNull(f2, "f2 must be not null");
        Assert.notNull(f3, "f3 must be not null");
        Assert.notNull(f4, "f4 must be not null");
        Assert.notNull(f5, "f5 must be not null");
        return of(f1.apply(_1), f2.apply(_2), f3.apply(_3), f4.apply(_4), f5.apply(_5));
    }


    /**
     * Maps the 1st component of this {@link Tuple5} to a new value.
     *
     * @param mapper
     *    A mapping {@link Function}
     *
     * @return a new {@link Tuple5} based on this one and substituted 1st component
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null}
     */
    public <U> Tuple5<U, T2, T3, T4, T5> map1(final Function<? super T1, ? extends U> mapper) {
        Assert.notNull(mapper, "mapper must be not null");
        final U u = mapper.apply(_1);
        return of(u, _2, _3, _4, _5);
    }


    /**
     * Maps the 2nd component of this {@link Tuple5} to a new value.
     *
     * @param mapper
     *    A mapping {@link Function}
     *
     * @return a new {@link Tuple5} based on this one and substituted 2nd component
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null}
     */
    public <U> Tuple5<T1, U, T3, T4, T5> map2(final Function<? super T2, ? extends U> mapper) {
        Assert.notNull(mapper, "mapper must be not null");
        final U u = mapper.apply(_2);
        return Tuple.of(_1, u, _3, _4, _5);
    }


    /**
     * Maps the 3rd component of this {@link Tuple5} to a new value.
     *
     * @param mapper
     *    A mapping {@link Function}
     *
     * @return a new {@link Tuple5} based on this one and substituted 3rd component
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null}
     */
    public <U> Tuple5<T1, T2, U, T4, T5> map3(final Function<? super T3, ? extends U> mapper) {
        Assert.notNull(mapper, "mapper must be not null");
        final U u = mapper.apply(_3);
        return Tuple.of(_1, _2, u, _4, _5);
    }


    /**
     * Maps the 4th component of this {@link Tuple5} to a new value.
     *
     * @param mapper
     *    A mapping {@link Function}
     *
     * @return a new {@link Tuple5} based on this one and substituted 4th component
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null}
     */
    public <U> Tuple5<T1, T2, T3, U, T5> map4(final Function<? super T4, ? extends U> mapper) {
        Assert.notNull(mapper, "mapper must be not null");
        final U u = mapper.apply(_4);
        return Tuple.of(_1, _2, _3, u, _5);
    }


    /**
     * Maps the 5th component of this {@link Tuple5} to a new value.
     *
     * @param mapper
     *    A mapping {@link Function}
     *
     * @return a new {@link Tuple5} based on this one and substituted 5th component
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null}
     */
    public <U> Tuple5<T1, T2, T3, T4, U> map5(final Function<? super T5, ? extends U> mapper) {
        Assert.notNull(mapper, "mapper must be not null");
        final U u = mapper.apply(_5);
        return Tuple.of(_1, _2, _3, _4, u);
    }


    /**
     * Transforms this {@link Tuple5} to an object of type U.
     *
     * @param f
     *    Transformation {@link PentaFunction} which creates a new object of type U based on this tuple's contents.
     *
     * @return An object of type U
     *
     * @throws IllegalArgumentException if {@code f} is {@code null}
     */
    public <U> U apply(final PentaFunction<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? extends U> f) {
        Assert.notNull(f, "f must be not null");
        return f.apply(_1, _2, _3, _4, _5);
    }

}
