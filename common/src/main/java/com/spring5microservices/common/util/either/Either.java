package com.spring5microservices.common.util.either;

import com.spring5microservices.common.util.validation.Validation;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;

/**
 *    Represents a value of one of two possible types (a disjoint union). An instance of Either is an instance of
 * {@link Left} or {@link Right}.
 *
 *    A common use of Either is as an alternative to {@link Optional} for dealing with possibly missing values. In this
 * usage, {@link Optional#empty()} is replaced with a {@link Left} which can contain useful information. {@link Right}
 * takes the place of {@link Optional#get()}. Convention dictates that {@link Left} is used for failure and {@link Right}
 * is used for success.
 *
 *    For example, you could use Either[String, Integer] to indicate whether a received input is a {@link String} or an
 * {@link Integer}.
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
     * Gets the value of this {@link Either} if is a {@link Right} or throws if this is an {@link Left}.
     *
     * @return the {@link Right} value
     *
     * @throws NoSuchElementException if this is an {@link Left}
     */
    public abstract R get();


    /**
     * Gets the value of this {@link Either} if is a {@link Left} or throws if this is an {@link Right}.
     *
     * @return the {@link Left} value
     *
     * @throws NoSuchElementException if this is an {@link Right}
     */
    public abstract L getLeft();


    /**
     * Creates a {@link Right} that contains the given {@code value}.
     *
     * @param value
     *    The value to store in the returned {@link Right}
     *
     * @return {@code Right(value)}
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
     * @return {@code Left(value)}
     */
    public static <L, R> Either<L, R> left(final L value) {
        return Left.ofNullable(value);
    }


    /**
     * Returns {@code true} if this is a {@link Right} and its value is equal to {@code value}, {@code false} otherwise.
     *
     * @param value
     *    Element to test
     *
     * @return {@code true} if this is a {@link Right} and its value is equal to {@code value}
     */
    public final boolean contains(final R value) {
        if (isRight()) {
            return (
                    Objects.isNull(value) &&
                            Objects.isNull(get())
                   ) ||
                   (
                    Objects.nonNull(value) &&
                            Objects.nonNull(get()) &&
                            get().equals(value)
                   );
        }
        return false;
    }


    /**
     * Returns {@code Optional.of(this)} if:
     *
     *   1. Current instance is {@link Left}
     *   2. Current instance is {@link Right} and stored value verifies given {@link Predicate} (or {@code predicate} is {@code null})
     *
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
        return Objects.isNull(predicate) || predicate.test(get())
                ? of(this)
                : empty();
    }


    /**
     *    Applies a {@link Function} {@code mapper} to the stored value of this {@link Either} if this is a {@link Right}.
     * Otherwise does nothing if this is a {@link Left}.
     *
     * @param mapper
     *    The mapping function to apply to a value of a {@link Right} instance.
     *
     * @return new {@link Either}
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null} and the current instance is a {@link Right} one
     */
    public final <U> Either<L, U> map(final Function<? super R, ? extends U> mapper) {
        if (isRight()) {
            Assert.notNull(mapper, "mapper must be not null");
            return Either.right(
                    mapper.apply(
                            get()
                    )
            );
        } else {
            return Either.left(getLeft());
        }
    }


    /**
     *    Applies a {@link Function} {@code mapper} to the stored value of this {@link Either} if this is a {@link Left}.
     * Otherwise does nothing if this is a {@link Right}.
     *
     * @param mapper
     *    The mapping function to apply to a value of a {@link Left} instance.
     *
     * @return new {@link Either}
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null} and the current instance is a {@link Left} one
     */
    public final <U> Either<U, R> mapLeft(final Function<? super L, ? extends U> mapper) {
        if (!isRight()) {
            Assert.notNull(mapper, "mapper must be not null");
            return Either.left(
                    mapper.apply(
                            getLeft()
                    )
            );
        } else {
            return Either.right(get());
        }
    }


    /**
     *    Whereas {@code mapperRight} only performs a mapping on a {@link Right} {@link Either}, and {@code mapperLeft}
     * performs a mapping on an {@link Left} {@link Either}, {@code bimap} allows you to provide mapping actions for
     * both, and will give you the result based on what type of {@link Either} this is. Without this, you would have
     * to do something like:
     *
     * Example:
     *   either.map(...).mapLeft(...);
     *
     * @param mapperLeft
     *    {@link Function} with the invalid mapping operation
     * @param mapperRight
     *    {@link Function} with the valid mapping operation
     *
     * @return {@link Either}
     *
     * @throws IllegalArgumentException if {@code mapperRight} is {@code null} and the current instance is a {@link Right} one
     *                                  or {@code mapperLeft} is {@code null} and the current instance is a {@link Left} one
     */
    public final <L2, R2> Either<L2, R2> map(final Function<? super L, ? extends L2> mapperLeft,
                                             final Function<? super R, ? extends R2> mapperRight) {
        if (isRight()) {
            Assert.notNull(mapperRight, "mapperRight must be not null");
            return Either.right(
                    mapperRight.apply(
                            get()
                    )
            );
        } else {
            Assert.notNull(mapperLeft, "mapperLeft must be not null");
            return Either.left(
                    mapperLeft.apply(
                            getLeft()
                    )
            );
        }
    }


    /**
     *    If the current {@link Either} is a {@link Right} instance, returns the result of applying the given
     * {@link Either}-bearing mapping function to the value. Otherwise does nothing if this is a {@link Left}.
     *
     * @param mapper
     *    The mapping {@link Function} to apply the value of a {@link Right} instance
     *
     * @return new {@link Either}
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null} and the current instance is a {@link Right} one
     */
    @SuppressWarnings("unchecked")
    public final <U> Either<L, U> flatMap(final Function<? super R, ? extends Either<L, ? extends U>> mapper) {
        if (isRight()) {
            Assert.notNull(mapper, "mapper must be not null");
            return (Either<L, U>) mapper.apply(get());
        }
        return (Either<L, U>) this;
    }


    /**
     * Merge given {@code either} with the current one, managing the following use cases:
     *
     *   1. this = {@link Right}, validation = {@link Right}  =>  return a {@link Right} instance applying {@code mapperRight}
     *   2. this = {@link Right}, validation = {@link Left}   =>  return the {@link Left}
     *   3. this = {@link Left},  validation = {@link Right}  =>  return the {@link Left}
     *   4. this = {@link Left},  validation = {@link Left}   =>  return a {@link Left} instance applying {@code mapperLeft}
     *
     * If provided {@code either} is {@code null}, the current instance will be returned.
     *
     * @param either
     *    New {@link Either} to merge with the current one
     *
     * @return {@link Either}
     *
     * @throws IllegalArgumentException if {@code mapperRight} is {@code null} and the current instance and {@code either} are {@link Right}
     *                                  or {@code mapperLeft} is {@code null} and the current instance and {@code either} are {@link Left}
     */
    public final Either<L, R> ap(final Either<? extends L, ? extends R> either,
                                 final BiFunction<? super L, ? super L, ? extends L> mapperLeft,
                                 final BiFunction<? super R, ? super R, ? extends R> mapperRight) {
        if (Objects.isNull(either)) {
            return this;
        }
        // This is a Right instance
        if (isRight()) {
            // Current and given validation are Right, a new merged Right instance will be returned
            if (either.isRight()) {
                Assert.notNull(mapperRight, "mapperRight must be not null");
                return right(
                        mapperRight.apply(
                                get(),
                                either.get()
                        )
                );
            // This is Right but either is Left
            } else {
                return left(either.getLeft());
            }
        } else {
            // Due to only this is Left, returns this
            if (either.isRight()) {
                return left(getLeft());

            // Current and given validation are Left, a new merged Left instance will be returned
            } else {
                Assert.notNull(mapperLeft, "mapperLeft must be not null");
                return left(
                        mapperLeft.apply(
                                getLeft(),
                                either.getLeft()
                        )
                );
            }
        }
    }


    /**
     *    Applies {@code mapperRight} if current {@link Either} is a {@link Right} instance, {@code mapperLeft}
     * if it is an {@link Left}, transforming internal values into another one.
     *
     * Example:
     *
     *   Either<String, Integer> either = ...;
     *   int i = either.fold(String::length, Function.identity());
     *
     * @param mapperLeft
     *    The mapping {@link Function} to apply the value of a {@link Left} instance
     * @param mapperRight
     *    The mapping {@link Function} to apply the value of a {@link Right} instance
     *
     * @return the result of applying the right {@link Function}
     *
     * @throws IllegalArgumentException if {@code mapperRight} is {@code null} and the current instance is a {@link Right} one
     *                                  or {@code mapperLeft} is {@code null} and the current instance is a {@link Left} one
     */
    public final <U> U fold(final Function<? super L, ? extends U> mapperLeft,
                            final Function<? super R, ? extends U> mapperRight) {
        if (isRight()) {
            Assert.notNull(mapperRight, "mapperRight must be not null");
            return mapperRight.apply(get());
        }
        else {
            Assert.notNull(mapperLeft, "mapperLeft must be not null");
            return mapperLeft.apply(getLeft());
        }
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
        if (isRight() && Objects.nonNull(action)) {
            action.accept(get());
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
        if (!isRight() && Objects.nonNull(action)) {
            action.accept(getLeft());
        }
        return this;
    }


    /**
     *    Performs the given {@code actionValid} to the stored value if the current {@link Either} is a {@link Right}
     * one. If the current instance is a {@link Left}, performs {@code actionInvalid}.
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
        if (isRight() && Objects.nonNull(actionRight)) {
            actionRight.accept(get());
        }
        if (!isRight() && Objects.nonNull(actionLeft)) {
            actionLeft.accept(getLeft());
        }
        return this;
    }


    /**
     * Returns the stored value if the underline instance is {@link Right}, otherwise returns {@code other}.
     *
     * @param other
     *    Returned value if current instance is an {@link Left} one
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
     * Returns the stored value if the underline instance is {@link Right}, otherwise throws {@code exceptionSupplier.get()}.
     *
     * @param exceptionSupplier
     *    An exception supplier
     *
     * @return {@code R} value stored in {@link Right} instance, throws {@code X} otherwise
     *
     * @throws IllegalArgumentException if {@code exceptionSupplier} is {@code null} and the current instance is a {@link Left} one
     * @throws X if is an {@link Left}
     */
    public final <X extends Throwable> R getOrElseThrow(final Supplier<X> exceptionSupplier) throws X {
        if (isRight()) {
            return get();
        }
        Assert.notNull(exceptionSupplier, "exceptionSupplier must be not null");
        throw exceptionSupplier.get();
    }


    /**
     * Returns this {@link Either} if it is {@link Right}, otherwise return the alternative.
     *
     * @param other
     *    An alternative {@link Either}
     *
     * @return {@link Either}
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
     * @return {@link Either}
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
     * If this is a {@link Left}, then return the left value in {@link} or vice versa.
     *
     * @return new {@link Either}
     */
    public final Either<R, L> swap() {
        if (isRight()) {
            return Either.left(get());
        } else {
            return Either.right(getLeft());
        }
    }


    /**
     * Verifies in the current instance has no value, that is:
     *
     *    1. Is a {@link Left} one.
     *    2. Is an empty {@link Right} instance.
     *
     * @return {@code true} is the current instance is empty, {@code false} otherwise
     */
    public final boolean isEmpty() {
        return !isRight() || Objects.isNull(get());
    }


    /**
     *    If the current {@link Either} is an instance of {@link Right} wraps the stored value into an {@link Optional} object.
     * Otherwise return {@link Optional#empty()}
     *
     * @return {@link Optional}
     */
    public final Optional<R> toOptional() {
        return isEmpty()
                ? empty()
                : of(get());
    }


    /**
     * Transforms current {@link Either} into a {@link Validation}.
     *
     * @return {@code Validation.valid(get())} if this is {@link Right},
     *         otherwise {@code Validation.invalid(getLeft())}.
     */
    public final Validation<L, R> toValidation() {
        return isRight()
                ? Validation.valid(get())
                : Validation.invalid(
                        Objects.isNull(getLeft())
                                ? new ArrayList<>()
                                : asList(getLeft())
                  );
    }

}
