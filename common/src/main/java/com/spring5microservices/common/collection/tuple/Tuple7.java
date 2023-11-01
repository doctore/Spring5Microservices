package com.spring5microservices.common.collection.tuple;

import com.spring5microservices.common.interfaces.functional.HeptaFunction;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;

/**
 * A {@link Tuple} of seven elements.
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
 * @param <T6>
 *    Type of the 6th element
 * @param <T7>
 *    Type of the 7th element
 */
public class Tuple7<T1, T2, T3, T4, T5, T6, T7> implements Tuple, Serializable {

    private static final long serialVersionUID = 8814223172415243276L;

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

    /**
     * The 6th element of this tuple.
     */
    public final T6 _6;

    /**
     * The 7th element of this tuple.
     */
    public final T7 _7;


    private Tuple7(T1 t1,
                   T2 t2,
                   T3 t3,
                   T4 t4,
                   T5 t5,
                   T6 t6,
                   T7 t7) {
        this._1 = t1;
        this._2 = t2;
        this._3 = t3;
        this._4 = t4;
        this._5 = t5;
        this._6 = t6;
        this._7 = t7;
    }


    public static <T1, T2, T3, T4, T5, T6, T7> Tuple7<T1, T2, T3, T4, T5, T6, T7> of(final T1 t1,
                                                                                     final T2 t2,
                                                                                     final T3 t3,
                                                                                     final T4 t4,
                                                                                     final T5 t5,
                                                                                     final T6 t6,
                                                                                     final T7 t7) {
        return new Tuple7<>(t1, t2, t3, t4, t5, t6, t7);
    }


    public static <T1, T2, T3, T4, T5, T6, T7> Tuple7<T1, T2, T3, T4, T5, T6, T7> empty() {
        return new Tuple7<>(null, null, null, null, null, null, null);
    }


    public static <T1, T2, T3, T4, T5, T6, T7> Comparator<Tuple7<T1, T2, T3, T4, T5, T6, T7>> comparator(final Comparator<? super T1> t1Comp,
                                                                                                         final Comparator<? super T2> t2Comp,
                                                                                                         final Comparator<? super T3> t3Comp,
                                                                                                         final Comparator<? super T4> t4Comp,
                                                                                                         final Comparator<? super T5> t5Comp,
                                                                                                         final Comparator<? super T6> t6Comp,
                                                                                                         final Comparator<? super T7> t7Comp) {
        return (t1, t2) -> {
            final int check1 = t1Comp.compare(t1._1, t2._1);
            if (0 != check1) {
                return check1;
            }
            final int check2 = t2Comp.compare(t1._2, t2._2);
            if (0 != check2) {
                return check2;
            }
            final int check3 = t3Comp.compare(t1._3, t2._3);
            if (0 != check3) {
                return check3;
            }
            final int check4 = t4Comp.compare(t1._4, t2._4);
            if (0 != check4) {
                return check4;
            }
            final int check5 = t5Comp.compare(t1._5, t2._5);
            if (0 != check5) {
                return check5;
            }
            final int check6 = t6Comp.compare(t1._6, t2._6);
            if (0 != check6) {
                return check6;
            }
            return t7Comp.compare(t1._7, t2._7);
        };
    }


    @SuppressWarnings("unchecked")
    public static <U1 extends Comparable<? super U1>,
                   U2 extends Comparable<? super U2>,
                   U3 extends Comparable<? super U3>,
                   U4 extends Comparable<? super U4>,
                   U5 extends Comparable<? super U5>,
                   U6 extends Comparable<? super U6>,
                   U7 extends Comparable<? super U7>> int compareTo(final Tuple7<?, ?, ?, ?, ?, ?, ?> o1,
                                                                    final Tuple7<?, ?, ?, ?, ?, ?, ?> o2) {
        final Tuple7<U1, U2, U3, U4, U5, U6, U7> t1 = (Tuple7<U1, U2, U3, U4, U5, U6, U7>) o1;
        final Tuple7<U1, U2, U3, U4, U5, U6, U7> t2 = (Tuple7<U1, U2, U3, U4, U5, U6, U7>) o2;

        final int check1 = t1._1.compareTo(t2._1);
        if (0 != check1) {
            return check1;
        }
        final int check2 = t1._2.compareTo(t2._2);
        if (0 != check2) {
            return check2;
        }
        final int check3 = t1._3.compareTo(t2._3);
        if (0 != check3) {
            return check3;
        }
        final int check4 = t1._4.compareTo(t2._4);
        if (0 != check4) {
            return check4;
        }
        final int check5 = t1._5.compareTo(t2._5);
        if (0 != check5) {
            return check5;
        }
        final int check6 = t1._6.compareTo(t2._6);
        if (0 != check6) {
            return check6;
        }
        return t1._7.compareTo(t2._7);
    }


    @Override
    public int arity() {
        return 7;
    }


    @Override
    public boolean equals(Object obj) {
        return obj == this ||
                (obj instanceof Tuple7 &&
                        Objects.equals(_1, ((Tuple7<?, ?, ?, ?, ?, ?, ?>) obj)._1) &&
                        Objects.equals(_2, ((Tuple7<?, ?, ?, ?, ?, ?, ?>) obj)._2) &&
                        Objects.equals(_3, ((Tuple7<?, ?, ?, ?, ?, ?, ?>) obj)._3) &&
                        Objects.equals(_4, ((Tuple7<?, ?, ?, ?, ?, ?, ?>) obj)._4) &&
                        Objects.equals(_5, ((Tuple7<?, ?, ?, ?, ?, ?, ?>) obj)._5) &&
                        Objects.equals(_6, ((Tuple7<?, ?, ?, ?, ?, ?, ?>) obj)._6) &&
                        Objects.equals(_7, ((Tuple7<?, ?, ?, ?, ?, ?, ?>) obj)._7)
                );
    }


    @Override
    public int hashCode() {
        return Objects.hash(_1, _2, _3, _4, _5, _6, _7);
    }


    @Override
    public String toString() {
        return "(" + _1 + ", " + _2 + ", " + _3 + ", " + _4 + ", " + _5 + ", " + _6 + ", " + _7 + ")";
    }


    /**
     * Sets the 1st element of this {@link Tuple7} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple7} with a new value for the 1st element of this {@link Tuple7}
     */
    public Tuple7<T1, T2, T3, T4, T5, T6, T7> update1(final T1 value) {
        return of(value, _2, _3, _4, _5, _6, _7);
    }


    /**
     * Remove the 1st value from this {@link Tuple7}.
     *
     * @return {@link Tuple6} with a copy of this {@link Tuple7} with the 1st value element removed
     */
    public Tuple6<T2, T3, T4, T5, T6, T7> remove1() {
        return Tuple.of(_2, _3, _4, _5, _6, _7);
    }


    /**
     * Sets the 2nd element of this {@link Tuple7} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple7} with a new value for the 2nd element of this {@link Tuple7}
     */
    public Tuple7<T1, T2, T3, T4, T5, T6, T7> update2(final T2 value) {
        return of(_1, value, _3, _4, _5, _6, _7);
    }


    /**
     * Remove the 2nd value from this {@link Tuple7}.
     *
     * @return {@link Tuple6} with a copy of this {@link Tuple7} with the 2nd value element removed
     */
    public Tuple6<T1, T3, T4, T5, T6, T7> remove2() {
        return Tuple.of(_1, _3, _4, _5, _6, _7);
    }


    /**
     * Sets the 3rd element of this {@link Tuple7} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple7} with a new value for the 3rd element of this {@link Tuple7}
     */
    public Tuple7<T1, T2, T3, T4, T5, T6, T7> update3(final T3 value) {
        return of(_1, _2, value, _4, _5, _6, _7);
    }


    /**
     * Remove the 3rd value from this {@link Tuple7}.
     *
     * @return {@link Tuple6} with a copy of this {@link Tuple7} with the 3rd value element removed
     */
    public Tuple6<T1, T2, T4, T5, T6, T7> remove3() {
        return Tuple.of(_1, _2, _4, _5, _6, _7);
    }


    /**
     * Sets the 4th element of this {@link Tuple7} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple7} with a new value for the 4th element of this {@link Tuple7}
     */
    public Tuple7<T1, T2, T3, T4, T5, T6, T7> update4(final T4 value) {
        return of(_1, _2, _3, value, _5, _6, _7);
    }


    /**
     * Remove the 4th value from this {@link Tuple7}.
     *
     * @return {@link Tuple6} with a copy of this {@link Tuple7} with the 4th value element removed
     */
    public Tuple6<T1, T2, T3, T5, T6, T7> remove4() {
        return Tuple.of(_1, _2, _3, _5, _6, _7);
    }


    /**
     * Sets the 5th element of this {@link Tuple7} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple7} with a new value for the 5th element of this {@link Tuple7}
     */
    public Tuple7<T1, T2, T3, T4, T5, T6, T7> update5(final T5 value) {
        return of(_1, _2, _3, _4, value, _6, _7);
    }


    /**
     * Remove the 5th value from this {@link Tuple7}.
     *
     * @return {@link Tuple6} with a copy of this {@link Tuple7} with the 5th value element removed
     */
    public Tuple6<T1, T2, T3, T4, T6, T7> remove5() {
        return Tuple.of(_1, _2, _3, _4, _6, _7);
    }


    /**
     * Sets the 6th element of this {@link Tuple7} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple7} with a new value for the 6th element of this {@link Tuple7}
     */
    public Tuple7<T1, T2, T3, T4, T5, T6, T7> update6(final T6 value) {
        return of(_1, _2, _3, _4, _5, value, _7);
    }


    /**
     * Remove the 6th value from this {@link Tuple7}.
     *
     * @return {@link Tuple6} with a copy of this {@link Tuple7} with the 6th value element removed
     */
    public Tuple6<T1, T2, T3, T4, T5, T7> remove6() {
        return Tuple.of(_1, _2, _3, _4, _5, _7);
    }


    /**
     * Sets the 7th element of this {@link Tuple7} to the given {@code value}.
     *
     * @param value
     *    The new value
     *
     * @return a copy of this {@link Tuple7} with a new value for the 7th element of this {@link Tuple7}
     */
    public Tuple7<T1, T2, T3, T4, T5, T6, T7> update7(final T7 value) {
        return of(_1, _2, _3, _4, _5, _6, value);
    }


    /**
     * Remove the 7th value from this {@link Tuple7}.
     *
     * @return {@link Tuple6} with a copy of this {@link Tuple7} with the 7th value element removed
     */
    public Tuple6<T1, T2, T3, T4, T5, T6> remove7() {
        return Tuple.of(_1, _2, _3, _4, _5, _6);
    }


    /**
     * Maps the components of this {@link Tuple7} using a mapper function.
     *
     * @param mapper
     *    The mapper {@link HeptaFunction}
     *
     * @return A new {@link Tuple7} of same arity
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null}
     */
    public <U1, U2, U3, U4, U5, U6, U7> Tuple7<U1, U2, U3, U4, U5, U6, U7> map(final HeptaFunction<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, Tuple7<U1, U2, U3, U4, U5, U6, U7>> mapper) {
        Assert.notNull(mapper, "mapper must be not null");
        return mapper.apply(_1, _2, _3, _4, _5, _6, _7);
    }


    /**
     * Maps the components of this {@link Tuple7} using a mapper function for each component.
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
     * @param f6
     *    The mapper {@link Function} of the 6th component
     * @param f7
     *    The mapper {@link Function} of the 7th component
     *
     * @return A new {@link Tuple7} of same arity.
     *
     * @throws IllegalArgumentException if {@code f1}, {@code f2}, {@code f3}, {@code f4}, {@code f5} or {@code f6} or {@code f7} are {@code null}
     */
    public <U1, U2, U3, U4, U5, U6, U7> Tuple7<U1, U2, U3, U4, U5, U6, U7> map(final Function<? super T1, ? extends U1> f1,
                                                                               final Function<? super T2, ? extends U2> f2,
                                                                               final Function<? super T3, ? extends U3> f3,
                                                                               final Function<? super T4, ? extends U4> f4,
                                                                               final Function<? super T5, ? extends U5> f5,
                                                                               final Function<? super T6, ? extends U6> f6,
                                                                               final Function<? super T7, ? extends U7> f7) {
        Assert.notNull(f1, "f1 must be not null");
        Assert.notNull(f2, "f2 must be not null");
        Assert.notNull(f3, "f3 must be not null");
        Assert.notNull(f4, "f4 must be not null");
        Assert.notNull(f5, "f5 must be not null");
        Assert.notNull(f6, "f6 must be not null");
        Assert.notNull(f7, "f7 must be not null");
        return of(
                f1.apply(_1),
                f2.apply(_2),
                f3.apply(_3),
                f4.apply(_4),
                f5.apply(_5),
                f6.apply(_6),
                f7.apply(_7)
        );
    }


    /**
     * Maps the 1st component of this {@link Tuple7} to a new value.
     *
     * @param mapper
     *    A mapping {@link Function}
     *
     * @return a new {@link Tuple7} based on this one and substituted 1st component
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null}
     */
    public <U> Tuple7<U, T2, T3, T4, T5, T6, T7> map1(final Function<? super T1, ? extends U> mapper) {
        Assert.notNull(mapper, "mapper must be not null");
        final U u = mapper.apply(_1);
        return of(u, _2, _3, _4, _5, _6, _7);
    }


    /**
     * Maps the 2nd component of this {@link Tuple7} to a new value.
     *
     * @param mapper
     *    A mapping {@link Function}
     *
     * @return a new {@link Tuple7} based on this one and substituted 2nd component
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null}
     */
    public <U> Tuple7<T1, U, T3, T4, T5, T6, T7> map2(final Function<? super T2, ? extends U> mapper) {
        Assert.notNull(mapper, "mapper must be not null");
        final U u = mapper.apply(_2);
        return of(_1, u, _3, _4, _5, _6, _7);
    }


    /**
     * Maps the 3rd component of this {@link Tuple7} to a new value.
     *
     * @param mapper
     *    A mapping {@link Function}
     *
     * @return a new {@link Tuple7} based on this one and substituted 3rd component
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null}
     */
    public <U> Tuple7<T1, T2, U, T4, T5, T6, T7> map3(final Function<? super T3, ? extends U> mapper) {
        Assert.notNull(mapper, "mapper must be not null");
        final U u = mapper.apply(_3);
        return of(_1, _2, u, _4, _5, _6, _7);
    }


    /**
     * Maps the 4th component of this {@link Tuple7} to a new value.
     *
     * @param mapper
     *    A mapping {@link Function}
     *
     * @return a new {@link Tuple7} based on this one and substituted 4th component
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null}
     */
    public <U> Tuple7<T1, T2, T3, U, T5, T6, T7> map4(final Function<? super T4, ? extends U> mapper) {
        Assert.notNull(mapper, "mapper must be not null");
        final U u = mapper.apply(_4);
        return of(_1, _2, _3, u, _5, _6, _7);
    }


    /**
     * Maps the 5th component of this {@link Tuple7} to a new value.
     *
     * @param mapper
     *    A mapping {@link Function}
     *
     * @return a new {@link Tuple7} based on this one and substituted 5th component
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null}
     */
    public <U> Tuple7<T1, T2, T3, T4, U, T6, T7> map5(final Function<? super T5, ? extends U> mapper) {
        Assert.notNull(mapper, "mapper must be not null");
        final U u = mapper.apply(_5);
        return of(_1, _2, _3, _4, u, _6, _7);
    }


    /**
     * Maps the 6th component of this {@link Tuple7} to a new value.
     *
     * @param mapper
     *    A mapping {@link Function}
     *
     * @return a new {@link Tuple7} based on this one and substituted 6th component
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null}
     */
    public <U> Tuple7<T1, T2, T3, T4, T5, U, T7> map6(final Function<? super T6, ? extends U> mapper) {
        Assert.notNull(mapper, "mapper must be not null");
        final U u = mapper.apply(_6);
        return of(_1, _2, _3, _4, _5, u, _7);
    }


    /**
     * Maps the 7th component of this {@link Tuple7} to a new value.
     *
     * @param mapper
     *    A mapping {@link Function}
     *
     * @return a new {@link Tuple7} based on this one and substituted 7th component
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null}
     */
    public <U> Tuple7<T1, T2, T3, T4, T5, T6, U> map7(final Function<? super T7, ? extends U> mapper) {
        Assert.notNull(mapper, "mapper must be not null");
        final U u = mapper.apply(_7);
        return of(_1, _2, _3, _4, _5, _6, u);
    }


    /**
     * Transforms this {@link Tuple7} to an object of type U.
     *
     * @param f
     *    Transformation {@link HeptaFunction} which creates a new object of type U based on this tuple's contents.
     *
     * @return An object of type U
     *
     * @throws IllegalArgumentException if {@code f} is {@code null}
     */
    public <U> U apply(final HeptaFunction<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? extends U> f) {
        Assert.notNull(f, "f must be not null");
        return f.apply(_1, _2, _3, _4, _5, _6, _7);
    }

}
