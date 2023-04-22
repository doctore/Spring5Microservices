package com.spring5microservices.common.interfaces.functional;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.spring5microservices.common.util.ObjectUtil.getOrElse;
import static com.spring5microservices.common.util.PredicateUtil.alwaysTrue;
import static com.spring5microservices.common.util.PredicateUtil.biAlwaysTrue;
import static java.util.Objects.isNull;

/**
 *    Unary function where the domain does not necessarily include all values of type T. The method {@link PartialFunction#isDefinedAt(Object)}
 * allows to test dynamically if a value is in the domain of the function, it is the responsibility of the caller to call it
 * before {@link PartialFunction#apply(Object)}, because if {@link PartialFunction#isDefinedAt(Object)} is {@code false},
 * it is not guaranteed {@link PartialFunction#apply(Object)} will throw an exception to indicate an error condition. If an
 * exception is not thrown, evaluation may result in an arbitrary value.
 * <p>
 *    The usual way to respect this contract is to call {@link PartialFunction#applyOrElse(Object, Function)}, which is expected
 * to be more efficient than calling both {@link PartialFunction#isDefinedAt(Object)} and {@link PartialFunction#apply(Object)}.
 *
 * <pre>
 * Example:
 *    PartialFunction<Integer, String> toStringIfEven = new PartialFunction<Integer, String> {
 *
 *       isDefinedAt(Integer i) {
 *          return null != i &&
 *                 i % 2 == 0;
 *       }
 *
 *       String apply(Integer i) {
 *           return i.toString();
 *       }
 *    }
 *
 *    String fromIntToString1 = toStringIfEven.applyOrElse(
 *       null,
 *       i -> ""
 *    );   // Will return empty string
 *
 *    String fromIntToString2 = toStringIfEven.applyOrElse(
 *       10,
 *       i -> ""
 *    );   // Will return "10"
 * </pre>
 *
 * @param <T>
 *    The type of the function input
 * @param <R>
 *    Type of the result of the function
 */
public interface PartialFunction<T, R> extends Function<T, R> {

    /**
     * Applies this {@link PartialFunction} to the given argument and returns the result.
     *
     * @param t
     *    The function argument
     *
     * @return the {@link PartialFunction} result
     */
    R apply(final T t);


    /**
     * Tests if the provided {@code t} is contained in the {@link PartialFunction}'s domain.
     *
     * @param t
     *    A potential function argument
     *
     * @return {@code true} if the given value is contained in the {@link PartialFunction}'s domain,
     *         {@code false} otherwise
     */
    boolean isDefinedAt(final T t);


    /**
     * Returns a {@link PartialFunction} with:
     * <p>
     *  - {@link PartialFunction#isDefinedAt(Object)} always returns {@code true}
     *  - {@link PartialFunction#apply(Object)} always returns its input argument
     *
     * @return {@link PartialFunction} that always returns its input argument
     */
    static <T> PartialFunction<T, T> identity() {
        return new PartialFunction<>() {
            @Override
            public T apply(final T t) {
                return t;
            }

            @Override
            public boolean isDefinedAt(final T t) {
                return true;
            }
        };
    }


    /**
     *    Returns a new {@link PartialFunction} based on provided {@link Predicate} {@code filterPredicate} and
     * {@link Function} {@code mapFunction}
     *
     * @param filterPredicate
     *    {@link Predicate} used to know new {@link PartialFunction}'s domain
     * @param mapFunction
     *    {@link Function} required for {@link PartialFunction#apply(Object)}
     *
     * @return {@link PartialFunction}
     *
     * @throws NullPointerException if {@code mapFunction} is {@code null}
     */
    static <T,R> PartialFunction<T, R> of(final Predicate<? super T> filterPredicate,
                                          final Function<? super T, ? extends R> mapFunction) {
        Objects.requireNonNull(mapFunction, "mapFunction must be not null");
        final Predicate<? super T> finalFilterPredicate = getOrElse(
                filterPredicate,
                alwaysTrue()
        );
        return new PartialFunction<>() {

            @Override
            public R apply(final T t) {
                return mapFunction.apply(t);
            }

            @Override
            public boolean isDefinedAt(final T t) {
                return finalFilterPredicate.test(t);
            }
        };
    }


    /**
     *    Returns a new {@link PartialFunction} based on provided {@link BiPredicate} {@code filterPredicate} and
     * {@link BiFunction} {@code mapFunction}
     *
     * @param filterPredicate
     *    {@link BiPredicate} used to know new {@link PartialFunction}'s domain
     * @param mapFunction
     *    {@link BiFunction} required for {@link PartialFunction#apply(Object)}
     *
     * @return {@link PartialFunction}
     *
     * @throws NullPointerException if {@code mapFunction} is {@code null}
     */
    static <K1, K2, V1, V2> PartialFunction<Map.Entry<K1, V1>, Map.Entry<K2, V2>> of(final BiPredicate<? super K1, ? super V1> filterPredicate,
                                                                                     final BiFunction<? super K1, ? super V1, ? extends Map.Entry<K2, V2>> mapFunction) {
        Objects.requireNonNull(mapFunction, "mapFunction must be not null");
        final BiPredicate<? super K1, ? super V1> finalFilterPredicate = getOrElse(
                filterPredicate,
                biAlwaysTrue()
        );
        return new PartialFunction<>() {

            @Override
            public Map.Entry<K2, V2> apply(final Map.Entry<K1, V1> entry) {
                return mapFunction.apply(
                        entry.getKey(),
                        entry.getValue()
                );
            }

            @Override
            public boolean isDefinedAt(final Map.Entry<K1, V1> entry) {
                return finalFilterPredicate.test(
                        entry.getKey(),
                        entry.getValue()
                );
            }
        };
    }


    /**
     *    Returns a composed {@link PartialFunction} that first applies this {@link PartialFunction} to its input, and then
     * applies the after {@link Function} to the result. If evaluation of either {@link Function} throws an {@link Exception},
     * it is relayed to the caller of the composed {@link PartialFunction}.
     *
     * @param after
     *    The {@link Function} to apply after this {@link PartialFunction} is applied
     * @param <V>
     *    The type of the output of the {@code after} {@link Function}, and of the composed {@link PartialFunction}
     *
     * @return a composed {@link PartialFunction} that first applies this {@link PartialFunction} and then applies the
     *         {@code after} {@link Function}.
     *
     * @throws NullPointerException if {@code after} is {@code null}
     */
    @Override
    @SuppressWarnings("unchecked")
    default <V> PartialFunction<T, V> andThen(final Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after, "after must be not null");
        if (after instanceof PartialFunction) {
            return andThen((PartialFunction) after);
        }
        return new PartialFunction<>() {

            @Override
            public V apply(final T t) {
                return after.apply(
                        PartialFunction.this.apply(t)
                );
            }

            @Override
            public boolean isDefinedAt(final T t) {
                return PartialFunction.this.isDefinedAt(t);
            }
        };
    }


    /**
     *    Returns a composed {@link PartialFunction} that first applies this {@link PartialFunction} to its input, and then
     * applies the after {@link PartialFunction} to the result. If evaluation of either {@link Function} throws an {@link Exception},
     * it is relayed to the caller of the composed {@link PartialFunction}.
     *
     * @param after
     *    The {@link PartialFunction} to apply after this {@link PartialFunction} is applied
     * @param <V>
     *    The type of the output of the {@code after} {@link PartialFunction}, and of the composed {@link PartialFunction}
     *
     * @return a composed {@link PartialFunction} that first applies this {@link PartialFunction} and then applies the
     *         {@code after} {@link PartialFunction}.
     *
     * @throws NullPointerException if {@code after} is {@code null}
     */
    default <V> PartialFunction<T, V> andThen(final PartialFunction<? super R, ? extends V> after) {
        Objects.requireNonNull(after, "after must be not null");
        return new PartialFunction<>() {

            @Override
            public V apply(final T t) {
                return after.apply(
                        PartialFunction.this.apply(t)
                );
            }

            @Override
            public boolean isDefinedAt(final T t) {
                return PartialFunction.this.isDefinedAt(t)
                        ? after.isDefinedAt(
                                PartialFunction.this.apply(t)
                          )
                        : false;
            }
        };
    }


    /**
     *    Applies this {@link PartialFunction} to the given {@code t} when it is contained in the {@link PartialFunction}'s
     * domain. Otherwise, applies {@code defaultFunction}
     *
     * @param t
     *    The function argument
     * @param defaultFunction
     *    {@link Function} to apply if provided {@code t} is not contained in the {@link PartialFunction}'s domain
     *
     * @return the result of this {@link PartialFunction} is {@code t} belongs to the {@link PartialFunction}'s domain,
     *         {@code defaultFunction} application otherwise.
     *
     * @throws NullPointerException if {@code defaultFunction} is {@code null} and {@code t} is not contained in the
     *                              {@link PartialFunction}'s domain
     */
    default R applyOrElse(final T t,
                          final Function<? super T, ? extends R> defaultFunction) {
        if (isDefinedAt(t)) {
            return apply(t);
        }
        Objects.requireNonNull(defaultFunction, "defaultFunction must be not null");
        return defaultFunction.apply(t);
    }


    /**
     *    Returns a composed {@link PartialFunction} that first applies {@code before} {@link Function} to its input, and
     * then applies this {@link PartialFunction} to the result. If evaluation of either {@link Function} throws an {@link Exception},
     * it is relayed to the caller of the composed {@link PartialFunction}.
     *
     * @param before
     *    The {@link Function} to apply before this {@link PartialFunction} is applied
     * @param <V>
     *    The type of input to the {@code before} {@link Function}, and to the composed {@link PartialFunction}
     *
     * @return a composed {@link PartialFunction} that first applies {@code before} {@link Function} and then applies this
     *         {@link PartialFunction}.
     *
     * @throws NullPointerException if {@code before} is {@code null}
     */
    @Override
    @SuppressWarnings("unchecked")
    default <V> PartialFunction<V, R> compose(final Function<? super V, ? extends T> before) {
        Objects.requireNonNull(before, "before must be not null");
        if (before instanceof PartialFunction) {
            return compose((PartialFunction) before);
        }
        return new PartialFunction<>() {

            @Override
            public R apply(final V v) {
                return PartialFunction.this.apply(
                        before.apply(v)
                );
            }

            @Override
            public boolean isDefinedAt(final V v) {
                return PartialFunction.this.isDefinedAt(
                        before.apply(v)
                );
            }
        };
    }


    /**
     *    Returns a composed {@link PartialFunction} that first applies {@code before} {@link PartialFunction} to its input,
     * and then applies this {@link PartialFunction} to the result. If evaluation of either {@link Function} throws an
     * {@link Exception}, it is relayed to the caller of the composed {@link PartialFunction}.
     *
     * @param before
     *    The {@link PartialFunction} to apply before this {@link PartialFunction} is applied
     * @param <V>
     *    The type of input to the {@code before} {@link PartialFunction}, and to the composed {@link PartialFunction}
     *
     * @return a composed {@link PartialFunction} that first applies {@code before} {@link PartialFunction} and then applies
     *         this {@link PartialFunction}.
     *
     * @throws NullPointerException if {@code before} is {@code null}
     */
    default <V> PartialFunction<V, R> compose(final PartialFunction<? super V, ? extends T> before) {
        Objects.requireNonNull(before, "before must be not null");
        return new PartialFunction<>() {

            @Override
            public R apply(final V v) {
                return PartialFunction.this.apply(
                        before.apply(v)
                );
            }

            @Override
            public boolean isDefinedAt(final V v) {
                return before.isDefinedAt(v)
                        ? PartialFunction.this.isDefinedAt(
                                before.apply(v)
                          )
                        : false;
            }
        };
    }


    /**
     * Turns this {@link PartialFunction} into a {@link Function} returning an {@link Optional} result.
     *
     * @return {@link Function} that takes an argument {@code x} to {@link Optional} of {@link PartialFunction#apply(Object)} if {@code x}
     *         belongs to the {@link PartialFunction}'s domain, {@link Optional#empty()} otherwise.
     */
    default Function<T, Optional<R>> lift() {
        return t ->
                isDefinedAt(t)
                        ? Optional.ofNullable(
                                apply(t)
                          )
                        : Optional.empty();
    }


    /**
     *    Composes this {@link PartialFunction} with another one, which gets applied where this {@link PartialFunction}
     * is not defined.
     *
     * @param defaultPartialFunction
     *    {@link PartialFunction} to apply when current value is not contained in this {@link PartialFunction}'s domain
     *
     * @return {@link PartialFunction} which has as domain the union of the domains of this {@link PartialFunction} and
     *         {@code orElsePartialFunction}. The resulting {@link PartialFunction} takes {@code x} to {@code this(x)}
     *         where this is defined, and to {@code orElsePartialFunction(x)} where it is not.
     */
    default PartialFunction<T, R> orElse(final PartialFunction<? super T, ? extends R> defaultPartialFunction) {

        return new PartialFunction<>() {

            @Override
            public R apply(final T t) {
                return isNull(defaultPartialFunction)
                        ? PartialFunction.this.apply(t)
                        : PartialFunction.this.applyOrElse(
                                t,
                                defaultPartialFunction
                          );
            }

            @Override
            public boolean isDefinedAt(final T t) {
                return isNull(defaultPartialFunction)
                        ? PartialFunction.this.isDefinedAt(t)
                        : PartialFunction.this.isDefinedAt(t) ||
                          defaultPartialFunction.isDefinedAt(t);
            }
        };
    }

}
