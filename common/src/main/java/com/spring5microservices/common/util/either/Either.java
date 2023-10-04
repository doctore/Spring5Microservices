package com.spring5microservices.common.util.either;

import com.spring5microservices.common.util.ObjectUtil;
import com.spring5microservices.common.util.Try.Failure;
import com.spring5microservices.common.util.Try.Success;
import com.spring5microservices.common.util.Try.Try;
import com.spring5microservices.common.util.validation.Invalid;
import com.spring5microservices.common.util.validation.Valid;
import com.spring5microservices.common.util.validation.Validation;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.empty;
import static java.util.Optional.of;

/**
 *    Represents a value of one of two possible types (a disjoint union). An instance of Either is an instance of
 * {@link Left} or {@link Right}.
 * <p>
 *    A common use of Either is as an alternative to {@link Optional} for dealing with possibly missing values. In this
 * usage, {@link Optional#empty()} is replaced with a {@link Left} which can contain useful information. {@link Right}
 * takes the place of {@link Optional#get()}. Convention dictates that {@link Left} is used for failure and {@link Right}
 * is used for success.
 * <p>
 *    For example, you could use {@link Either}<{@link String}, {@link Integer}> to indicate whether a received input
 * is a {@link String} or an {@link Integer}.
 *
 * @param <L>
 *    Type of the {@link Left} value of an {@link Either}
 * @param <R>
 *    Type of the {@link Right} value of an {@link Either}
 */
public abstract class Either<L, R> implements Serializable {

    private static final long serialVersionUID = 4271021485928210833L;


    /**
     * Returns {@code true} is this is a {@link Right}, {@code false} otherwise.
     */
    public abstract boolean isRight();


    /**
     *    Gets the value of this {@link Either} if is a {@link Right} or throws a {@link NoSuchElementException} if this
     * is a {@link Left}.
     *
     * @return the {@link Right} value
     *
     * @throws NoSuchElementException if this is a {@link Left}
     */
    public abstract R get();


    /**
     *    Gets the value of this {@link Either} if is a {@link Left} or throws {@link NoSuchElementException} if this
     * is a {@link Right}.
     *
     * @return the {@link Left} value
     *
     * @throws NoSuchElementException if this is a {@link Right}
     */
    public abstract L getLeft();


    /**
     * Creates a {@link Right} that contains the given {@code value}.
     *
     * @param value
     *    The value to store in the returned {@link Right}
     *
     * @return {@link Right}
     */
    public static <L, R> Either<L, R> right(final R value) {
        return Right.ofNullable(value);
    }


    /**
     * Creates a {@link Left} that contains the given {@code value}.
     *
     * @param value
     *    The value to store in the returned {@link Left}
     *
     * @return {@link Left}
     */
    public static <L, R> Either<L, R> left(final L value) {
        return Left.ofNullable(value);
    }


    /**
     * Merges the given {@link Either}s in a one result that will be:
     * <p>
     *   1. {@link Right} instance if all given {@code eithers} are {@link Right} ones or such parameters is {@code null}
     *      or empty. Using provided {@link BiFunction} {@code mapperRight} to get the final value added into the
     *      returned {@link Right}.
     * <p>
     *   2. {@link Left} instance if there is at least one {@link Left} in the given {@code eithers}. Using provided
     *      {@link BiFunction} {@code mapperLeft} to get the final value added into the returned {@link Left}.
     *
     * <pre>
     * Examples:
     *
     *   mapperLeft = (l1, l2) -> l2;
     *   mapperRight = (r1, r2) -> r2;
     *
     *   combine(mapperLeft, mapperRight, Either.right(11), Either.right(7));                      // Right(7)
     *   combine(mapperLeft, mapperRight, Either.right(13), Either.left("A"));                     // Left("A")
     *   combine(mapperLeft, mapperRight, Either.right(10), Either.left("A"), Either.left("B"));   // Left("B")
     * </pre>
     *
     * @param mapperLeft
     *    {@link BiFunction} used to calculate the new {@link Left} based on two provided ones
     * @param mapperRight
     *    {@link BiFunction} used to calculate the new {@link Right} based on two provided ones
     * @param eithers
     *    {@link Either} instances to combine
     *
     * @return {@link Either}
     *
     * @throws IllegalArgumentException if {@code mapperLeft} or {@code mapperRight} is {@code null} and {@code eithers}
     *                                  has elements.
     */
    @SafeVarargs
    public static <L, R> Either<L, R> combine(final BiFunction<? super L, ? super L, ? extends L> mapperLeft,
                                              final BiFunction<? super R, ? super R, ? extends R> mapperRight,
                                              final Either<L, R>... eithers) {
        if (ObjectUtil.isEmpty(eithers)) {
            return Right.empty();
        }
        Assert.notNull(mapperLeft, "mapperLeft must be not null");
        Assert.notNull(mapperRight, "mapperRight must be not null");
        Either<L, R> result = eithers[0];
        for (int i = 1; i < eithers.length; i++) {
            result = result.ap(
                    eithers[i],
                    mapperLeft,
                    mapperRight
            );
        }
        return result;
    }


    /**
     *    Checks the given {@link Supplier}s of {@link Either}, returning a {@link Right} instance if no {@link Left}
     * {@link Supplier} was given or the first {@link Left} one.
     *
     * <pre>
     * Examples:
     *
     *   mapperRight = (r1, r2) -> r2;
     *
     *   combineGetFirstLeft(mapperRight, () -> Either.right(1), () -> Either.right(7));                            // Right(7)
     *   combineGetFirstLeft(mapperRight, () -> Either.right(3), () -> Either.left("A"));                           // Left("A")
     *   combineGetFirstLeft(mapperRight, () -> Either.right(2), () -> Either.left("A"), () -> Either.left("B"));   // Left("B")
     * </pre>
     *
     * @param mapperRight
     *    {@link BiFunction} used to calculate the new {@link Right} based on two provided ones
     * @param suppliers
     *    {@link Supplier} of {@link Either} instances to verify
     *
     * @return {@link Either}
     *
     * @throws IllegalArgumentException if {@code mapperRight} is {@code null} and {@code suppliers} has elements.
     */
    @SafeVarargs
    public static  <L, R> Either<L, R> combineGetFirstLeft(final BiFunction<? super R, ? super R, ? extends R> mapperRight,
                                                           final Supplier<Either<L, R>>... suppliers) {
        if (ObjectUtil.isEmpty(suppliers)) {
            return Right.empty();
        }
        Assert.notNull(mapperRight, "mapperRight must be not null");
        Either<L, R> result = suppliers[0].get();
        for (int i = 1; i < suppliers.length; i++) {
            result = result.ap(
                    suppliers[i].get(),
                    (l1, l2) -> l1,
                    mapperRight
            );
            if (!result.isRight()) {
                return result;
            }
        }
        return result;
    }


    /**
     * Verifies if the current {@link Either} is a {@link Right} instance containing given {@code value}.
     *
     * @param value
     *    Element to test
     *
     * @return {@code true} if this is a {@link Right} and its value is equal to {@code value}
     */
    public final boolean contain(final R value) {
        if (isRight()) {
            return (
                    isNull(value) &&
                            isNull(get())
            ) ||
            (
                    nonNull(value) &&
                            nonNull(get()) &&
                            get().equals(value)
            );
        }
        return false;
    }


    /**
     * Filters the current {@link Either} returning {@link Optional#of(Object)} of this if:
     * <p>
     *   1. Current instance is {@link Left}
     *   2. Current instance is {@link Right} and stored value verifies given {@link Predicate} (or it is {@code null})
     * <p>
     * {@link Optional#empty()} otherwise.
     *
     * @param predicate
     *    {@link Predicate} to apply the stored value if the current instance is a {@link Right} one
     *
     * @return {@link Optional} of {@link Either}
     */
    public final Optional<Either<L, R>> filter(final Predicate<? super R> predicate) {
        if (!isRight()) {
            return of(this);
        }
        return isNull(predicate) || predicate.test(get())
                ? of(this)
                : empty();
    }


    /**
     * Filters the current {@link Either} returning:
     * <p>
     *   1. {@link Right} if this is a {@link Right} and its value matches given {@link Predicate} (or {@code predicate} is {@code null})
     *   2. {@link Left} applying {@code zero} if this is {@link Right} but its value does not match given {@link Predicate}
     *   3. {@link Left} with the existing value if this is a {@link Left}
     *
     * <pre>
     * Examples:
     *
     *   Either.right(11).filterOrElse(i -> i > 10, () -> "error");           // Right(11)
     *   Either.right(7).filterOrElse(i -> i > 10, () -> "error");            // Left("error")
     *   Either.left("warning").filterOrElse(i -> i > 10, () -> "error");     // Left("warning")
     * </pre>
     *
     * @param predicate
     *    {@link Predicate} to apply the stored value if the current instance is a {@link Right} one
     * @param zero
     *    {@link Supplier} that turns a {@link Right} value into a {@link Left} one if this is {@link Right}
     *    but its value does not match given {@link Predicate}
     *
     * @throws IllegalArgumentException if {@code zero} is {@code null}, this is a {@link Right} but does not match given {@link Predicate}
     *
     * @return {@link Right} if this is {@link Right} and {@code predicate} matches,
     *         {@link Left} applying {@code zero} otherwise.
     */
    public final Either<L, R> filterOrElse(final Predicate<? super R> predicate,
                                           final Supplier<? extends L> zero) {
        if (!isRight()) {
            return this;
        }
        if (isNull(predicate) || predicate.test(get())) {
            return this;
        }
        Assert.notNull(zero, "zero must be not null");
        return left(
                zero.get()
        );
    }


    /**
     *    Applies a {@link Function} {@code mapper} to the stored value of this {@link Either} if this is a {@link Right}.
     * Otherwise, does nothing if this is a {@link Left}.
     *
     * @param mapper
     *    The mapping function to apply to a value of a {@link Right} instance.
     *
     * @return new {@link Right} applying {@code mapper} if current is {@link Right},
     *         current {@link Left} otherwise.
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null} and the current instance is a {@link Right} one
     */
    public final <U> Either<L, U> map(final Function<? super R, ? extends U> mapper) {
        if (isRight()) {
            Assert.notNull(mapper, "mapper must be not null");
            return right(
                    mapper.apply(
                            get()
                    )
            );
        }
        return left(
                getLeft()
        );
    }


    /**
     *    Whereas {@link Either#map(Function)} with {@code mapper} argument only performs a mapping on a {@link Right}
     * {@link Either}, and {@link Either#mapLeft(Function)} performs a mapping on a {@link Left} {@link Either},
     * this function allows you to provide mapping actions for both, and will give you the result based on what type
     * of {@link Either} this is.
     * <p>
     * Without this, you would have to do something like:
     *
     * <pre>
     * Example:
     *
     *   either.map(...).mapLeft(...);
     * </pre>
     *
     * @param mapperLeft
     *    {@link Function} with the left mapping operation
     * @param mapperRight
     *    {@link Function} with the right mapping operation
     *
     * @return {@link Right} applying {@code mapperRight} if current is {@link Right},
     *         {@link Left} applying {@code mapperLeft} otherwise.
     *
     * @throws IllegalArgumentException if {@code mapperRight} is {@code null} and the current instance is a {@link Right} one
     *                                  or {@code mapperLeft} is {@code null} and the current instance is a {@link Left} one
     */
    public final <L2, R2> Either<L2, R2> map(final Function<? super L, ? extends L2> mapperLeft,
                                             final Function<? super R, ? extends R2> mapperRight) {
        if (isRight()) {
            Assert.notNull(mapperRight, "mapperRight must be not null");
            return right(
                    mapperRight.apply(
                            get()
                    )
            );
        }
        Assert.notNull(mapperLeft, "mapperLeft must be not null");
        return left(
                mapperLeft.apply(
                        getLeft()
                )
        );
    }


    /**
     *    Applies a {@link Function} {@code mapper} to the stored value of this {@link Either} if this is a {@link Left}.
     * Otherwise, does nothing if this is a {@link Right}.
     *
     * @param mapper
     *    The mapping function to apply to a value of a {@link Left} instance.
     *
     * @return {@link Left} applying {@code mapper} if current is {@link Left},
     *         current {@link Right} otherwise.
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null} and the current instance is a {@link Left} one
     */
    public final <U> Either<U, R> mapLeft(final Function<? super L, ? extends U> mapper) {
        if (!isRight()) {
            Assert.notNull(mapper, "mapper must be not null");
            return left(
                    mapper.apply(
                            getLeft()
                    )
            );
        }
        return right(
                get()
        );
    }


    /**
     *    If the current {@link Either} is a {@link Right} instance, returns the result of applying the given
     * {@link Either}-bearing mapping function to the value. Otherwise, does nothing if this is a {@link Left}.
     *
     * @param mapper
     *    The mapping {@link Function} to apply the value of a {@link Right} instance
     *
     * @return new {@link Right} applying {@code mapper} if current is {@link Right}, {@link Left} otherwise
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null} and the current instance is a {@link Right} one
     */
    @SuppressWarnings("unchecked")
    public final <U> Either<L, U> flatMap(final Function<? super R, ? extends Either<L, ? extends U>> mapper) {
        if (isRight()) {
            Assert.notNull(mapper, "mapper must be not null");
            return (Either<L, U>) mapper.apply(
                    get()
            );
        }
        return left(
                getLeft()
        );
    }


    /**
     * Merge given {@code either} with the current one, managing the following use cases:
     * <p>
     *   1. this = {@link Right}, either = {@link Right}  =>  return a {@link Right} instance applying {@code mapperRight}
     *   2. this = {@link Right}, either = {@link Left}   =>  return the {@link Left}
     *   3. this = {@link Left},  either = {@link Right}  =>  return the {@link Left}
     *   4. this = {@link Left},  either = {@link Left}   =>  return a {@link Left} instance applying {@code mapperLeft}
     *
     * If provided {@code either} is {@code null}, the current instance will be returned.
     *
     * @param either
     *    New {@link Either} to merge with the current one
     * @param mapperLeft
     *    {@link BiFunction} used to map current {@link Either} and given {@code either}, both {@link Left}
     * @param mapperRight
     *    {@link BiFunction} used to map current {@link Either} and given {@code either}, both {@link Right}
     *
     * @return {@link Either} merging {@code either} with current one
     *
     * @throws IllegalArgumentException if {@code mapperRight} is {@code null} and the current instance and {@code either} are {@link Right}
     *                                  or {@code mapperLeft} is {@code null} and the current instance and {@code either} are {@link Left}
     */
    public final Either<L, R> ap(final Either<? extends L, ? extends R> either,
                                 final BiFunction<? super L, ? super L, ? extends L> mapperLeft,
                                 final BiFunction<? super R, ? super R, ? extends R> mapperRight) {
        if (isNull(either)) {
            return this;
        }
        // This is a Right instance
        if (isRight()) {
            // Current and given either are Right, a new merged Right instance will be returned
            if (either.isRight()) {
                Assert.notNull(mapperRight, "mapperRight must be not null");
                return right(
                        mapperRight.apply(
                                get(),
                                either.get()
                        )
                );
            }
            // This is Right but either is Left
            return left(
                    either.getLeft()
            );

        // This is a Left instance
        } else {
            // Due to only this is Left, returns this
            if (either.isRight()) {
                return left(
                        getLeft()
                );
            }
            // Current and given either are Left, a new merged Left instance will be returned
            Assert.notNull(mapperLeft, "mapperLeft must be not null");
            return left(
                    mapperLeft.apply(
                            getLeft(),
                            either.getLeft()
                    )
            );
        }
    }


    /**
     * Applies {@code mapper} to the current {@link Either}, transforming internal values into another one.
     *
     * <pre>
     * Example:
     *
     *    Either<String, Integer> either = ...
     *    int i = either.fold(
     *               e ->
     *                  e.isRight()
     *                     ? ofNullable(e.get()).orElse(defaultValue)
     *                     : e.getLeft().length()
     *            );
     * </pre>
     *
     * @param mapper
     *    The mapping {@link Function} to apply to the current {@link Either}
     *
     * @return the result of applying provided {@link Function}
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null}
     */
    public final <U> U fold(final Function<? super Either<L, R>, ? extends U> mapper) {
        Assert.notNull(mapper, "mapper must be not null");
        return mapper.apply(this);
    }


    /**
     *    Applies {@code mapperRight} if current {@link Either} is a {@link Right} instance, {@code mapperLeft}
     * if it is a {@link Left}, transforming internal values into another one.
     *
     * <pre>
     * Example:
     *
     *   // Return 99
     *   Either.right(99)
     *         .fold(String::length, Function.identity());
     *
     *   // Return 3
     *   Either.left("abc")
     *         .fold(String::length, Function.identity());
     * </pre>
     *
     * @param mapperLeft
     *    The mapping {@link Function} to apply the value of a {@link Left} instance
     * @param mapperRight
     *    The mapping {@link Function} to apply the value of a {@link Right} instance
     *
     * @return the result of applying the suitable {@link Function}
     *
     * @throws IllegalArgumentException if {@code mapperRight} is {@code null} and the current instance is a {@link Right} one
     *                                  or {@code mapperLeft} is {@code null} and the current instance is a {@link Left} one
     */
    public final <U> U fold(final Function<? super L, ? extends U> mapperLeft,
                            final Function<? super R, ? extends U> mapperRight) {
        if (isRight()) {
            Assert.notNull(mapperRight, "mapperRight must be not null");
            return mapperRight.apply(
                    get()
            );
        }
        Assert.notNull(mapperLeft, "mapperLeft must be not null");
        return mapperLeft.apply(
                getLeft()
        );
    }


    /**
     * Performs the given {@code action} to the stored value if the current {@link Either} is a {@link Right} one.
     *
     * @param action
     *    {@link Consumer} invoked for the stored value of the current {@link Right} instance.
     *
     * @return {@link Either}
     */
    public final Either<L, R> peek(final Consumer<? super R> action) {
        if (isRight() && nonNull(action)) {
            action.accept(
                    get()
            );
        }
        return this;
    }


    /**
     *    Performs the given {@code actionRight} to the stored value if the current {@link Either} is a {@link Right}
     * one. If the current instance is a {@link Left}, performs {@code actionLeft}.
     *
     * @param actionLeft
     *    The {@link Left} {@link Consumer} operation
     * @param actionRight
     *    The {@link Right} {@link Consumer} operation
     *
     * @return {@link Either}
     */
    public final Either<L, R> peek(final Consumer<? super L> actionLeft,
                                   final Consumer<? super R> actionRight) {
        if (isRight() && nonNull(actionRight)) {
            actionRight.accept(
                    get()
            );
        }
        if (!isRight() && nonNull(actionLeft)) {
            actionLeft.accept(
                    getLeft()
            );
        }
        return this;
    }


    /**
     * Performs the given {@code action} to the stored value if the current {@link Either} is a {@link Left} one.
     *
     * @param action
     *    {@link Consumer} invoked for the stored value of the current {@link Left} instance.
     *
     * @return {@link Either}
     */
    public final Either<L, R> peekLeft(final Consumer<? super L> action) {
        if (!isRight() && nonNull(action)) {
            action.accept(
                    getLeft()
            );
        }
        return this;
    }


    /**
     * Returns the stored value if the underline instance is {@link Right}, otherwise returns {@code other}.
     *
     * @param other
     *    Returned value if current instance is a {@link Left} one
     *
     * @return {@code R} value stored in {@link Right} instance, {@code other} otherwise
     */
    public final R getOrElse(final R other) {
        if (isRight()) {
            return get();
        }
        return other;
    }


    /**
     *    Returns the stored value if the underline instance is {@link Right}, otherwise throws an {@link Exception} using
     * provided {@link Supplier}.
     *
     * @param exceptionSupplier
     *    An {@link Exception} {@link Supplier}
     *
     * @return {@code R} value stored in {@link Right} instance, throws {@code X} otherwise
     *
     * @throws IllegalArgumentException if {@code exceptionSupplier} is {@code null} and the current instance is a {@link Left} one
     * @throws X if is a {@link Left}
     */
    public final <X extends Throwable> R getOrElseThrow(final Supplier<X> exceptionSupplier) throws X {
        if (isRight()) {
            return get();
        }
        Assert.notNull(exceptionSupplier, "exceptionSupplier must be not null");
        throw exceptionSupplier.get();
    }


    /**
     *    Returns the stored value if the underline instance is {@link Right}, otherwise throws an {@link Exception} using
     * provided {@link Function} with the current {@link Left} instance value as parameter.
     *
     * @param exceptionFunction
     *    A {@link Function} which creates an {@link Exception} based on a {@link Left} value
     *
     * @return {@code T} value stored in {@link Right} instance, throws {@code X} otherwise
     *
     * @throws IllegalArgumentException if {@code exceptionFunction} is {@code null} and the current instance is a {@link Left} one
     * @throws X if is a {@link Left}
     */
    public final <X extends Throwable> R getOrElseThrow(final Function<? super L, X> exceptionFunction) throws X {
        if (isRight()) {
            return get();
        }
        Assert.notNull(exceptionFunction, "exceptionFunction must be not null");
        throw exceptionFunction.apply(
                getLeft()
        );
    }


    /**
     * Returns this {@link Either} if it is {@link Right}, otherwise returns {@code other}.
     *
     * @param other
     *    An alternative {@link Either}
     *
     * @return current {@link Either} if {@link Right}, {@code other} otherwise
     */
    @SuppressWarnings("unchecked")
    public final Either<L, R> orElse(final Either<? extends L, ? extends R> other) {
        return isRight()
                ? this
                : (Either<L, R>) other;
    }


    /**
     * Returns this {@link Either} if it is {@link Right}, otherwise return the result of evaluating {@link Supplier}.
     *
     * @param supplier
     *    An alternative {@link Either} supplier
     *
     * @return current {@link Either} if {@link Right}, {@code supplier} result otherwise.
     *
     * @throws IllegalArgumentException if {@code supplier} is {@code null} and the current instance is a {@link Left} one
     */
    @SuppressWarnings("unchecked")
    public final Either<L, R> orElse(final Supplier<Either<? extends L, ? extends R>> supplier) {
        if (isRight()) {
            return this;
        }
        Assert.notNull(supplier, "supplier must be not null");
        return (Either<L, R>) supplier.get();
    }


    /**
     * If this is a {@link Left}, then return the left value in a new {@link Right} instance or vice versa.
     *
     * @return new {@link Either}
     */
    public final Either<R, L> swap() {
        return isRight()
                ? left(get())
                : right(getLeft());
    }


    /**
     * Verifies if the current instance has no value, that is:
     * <p>
     *    1. Is a {@link Left} one.
     *    2. Is an empty {@link Right} instance.
     *
     * @return {@code true} is the current instance is empty, {@code false} otherwise
     */
    public final boolean isEmpty() {
        return !isRight() ||
               isNull(get());
    }


    /**
     *    Transforms this {@link Either} into a {@link Optional} instance. If the current {@link Either} is an instance
     * of {@link Right} wraps the stored value into an {@link Optional} object, {@link Optional#empty()} otherwise.
     *
     * @return {@link Optional} if is this {@link Either} is a {@link Right} and its value is non-`null`,
     *         {@link Optional#empty} if is this {@link Either} is a {@link Right} and its value is {@code null},
     *         {@link Optional#empty} if this is an {@link Left}
     */
    public final Optional<R> toOptional() {
        return isEmpty()
                ? empty()
                : of(get());
    }


    /**
     *    Transforms this {@link Either} into a {@link Try} instance. If the current {@link Either} is an instance of
     * {@link Right} wraps the stored value into a {@link Success} one, {@link Failure} otherwise.
     *
     * @param mapperLeft
     *   {@link Function} that maps the {@link Left} value to a {@link Throwable} instance
     *
     * @return {@link Success} if this is {@link Right}, {@link Failure} otherwise.
     *
     * @throws IllegalArgumentException if {@code mapperLeft} is {@code null} and the current instance is a {@link Left} one
     */
    public final Try<R> toTry(final Function<? super L, ? extends Throwable> mapperLeft) {
        return mapLeft(mapperLeft)
                .fold(
                        Try::failure,
                        Try::success
                );
    }


    /**
     *    Transforms current {@link Either} into a {@link Validation}. If the current {@link Either} is an instance of
     * {@link Right} wraps the stored value into a {@link Valid} one, {@link Invalid} otherwise.
     *
     * @return {@link Valid} instance if this is {@link Right}, {@link Invalid} otherwise.
     */
    public final Validation<L, R> toValidation() {
        return isRight()
                ? Validation.valid(get())
                : Validation.invalid(
                        isNull(getLeft())
                                ? new ArrayList<>()
                                : asList(getLeft())
                  );
    }

}
