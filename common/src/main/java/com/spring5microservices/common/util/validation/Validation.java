package com.spring5microservices.common.util.validation;

import com.spring5microservices.common.util.either.Either;
import com.spring5microservices.common.util.either.Left;
import com.spring5microservices.common.util.either.Right;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;

/**
 * Class used to validate the given instance, defining 2 different status to manage the result:
 *
 *    {@link Valid} the instance has verified all provided validations
 *    {@link Invalid} with the {@link List} of validations the given instance does not verify
 *
 * @param <T>
 *    Type of the {@link Valid} value of an {@link Validation}
 * @param <E>
 *    Type of the {@link Invalid} value of an {@link Validation}
 */
public abstract class Validation<E, T> implements Serializable {

    private static final long serialVersionUID = 5251027277325308220L;


    /**
     * Checks whether this is of type {@link Valid}.
     *
     * @return {@code true} if is a {@link Valid}, {@code false} if is an {@link Invalid}
     */
    public abstract boolean isValid();


    /**
     * Gets the value of this {@link Validation} if is a {@link Valid} or throws if this is an {@link Invalid}.
     *
     * @return the {@link Valid} value
     *
     * @throws NoSuchElementException if this is an {@link Invalid}
     */
    public abstract T get();


    /**
     * Gets the {@code error} of this {@link Validation} if it is an {@link Invalid} or throws if this is a {@link Valid}.
     *
     * @return the {@link Invalid} {@link Collection}
     *
     * @throws NoSuchElementException if this is a {@link Valid}
     */
    public abstract Collection<E> getErrors();


    /**
     * Creates a {@link Valid} that contains the given {@code value}.
     *
     * @param value
     *    The value to store in the returned {@link Valid}
     *
     * @return {@code Valid(value)}
     */
    public static <E, T> Validation<E, T> valid(final T value) {
        return Valid.ofNullable(value);
    }


    /**
     * Creates an {@link Invalid} that contains the given {@code errors}.
     *
     * @param errors
     *    {@link Collection} of errors to include in the returned {@link Invalid}
     *
     * @return {@code Invalid(error)}
     */
    public static <E, T> Validation<E, T> invalid(final Collection<E> errors) {
        return Invalid.ofNullable(errors);
    }


    /**
     * Creates a {@link Validation} using the given {@link Either}, following the rules:
     *
     *  - If {@link Right} then new {@link Validation} instance will be {@link Valid}
     *  - If {@link Left} then new {@link Validation} instance will be {@link Invalid}
     *
     * @param either
     *    {@link Either} used as source
     *
     * @return {@code Valid(either.get())} if {@link Either} is a {@link Right},
     *         otherwise {@code Invalid(either.getLeft())}
     *
     * @throws IllegalArgumentException if {@code either} is {@code null}
     */
    public static <E, T> Validation<E, T> fromEither(final Either<? extends E, ? extends T> either) {
        Assert.notNull(either, "either must be not null");
        return either.isRight()
                ? valid(either.get())
                : invalid(
                        Objects.isNull(either.getLeft())
                                ? new ArrayList<>()
                                : asList(either.getLeft())
                  );
    }


    /**
     * Merges the given {@link Validation} in a result one that will be:
     *
     *   1. {@link Valid} instance with all given {@code validations} are {@link Valid} ones or such parameters is {@code null} or empty.
     *   2. {@link Invalid} instance if there is at least one {@link Invalid} in the given {@code validations}. In this case, errors of
     *      all provided {@link Invalid}s will be included in the result.
     *
     * @param validations
     *    {@link Validation} instances to combine
     *
     * @return {@link Validation}
     */
    @SafeVarargs
    public static <E, T> Validation<E, T> combine(final Validation<E, T>... validations) {
        Validation<E, T> result = Valid.empty();
        if (!ObjectUtils.isEmpty(validations)) {
            for (Validation<E, T> validation : validations) {
                result = result.ap(validation);
            }
        }
        return result;
    }


    /**
     *    Checks the given {@link Supplier} of {@link Validation}, returning a {@link Valid} instance if no {@link Invalid}
     * {@link Supplier} was given or the first {@link Invalid} one.
     *
     * @param suppliers
     *    {@link Supplier} of {@link Validation} instances to verify
     *
     * @return {@link Validation}
     */
    @SafeVarargs
    public static <E, T> Validation<E, T> getFirstInvalid(final Supplier<Validation<E, T>>... suppliers) {
        Validation<E, T> result = Valid.empty();
        if (!ObjectUtils.isEmpty(suppliers)) {
            for (Supplier<Validation<E, T>> supplier : suppliers) {
                result = result.ap(supplier.get());
                if (!result.isValid()) {
                    return result;
                }
            }
        }
        return result;
    }


    /**
     * Returns {@code Optional.of(this)} if:
     *
     *   1. Current instance is {@link Invalid}
     *   2. Current instance is {@link Valid} and stored value verifies given {@link Predicate} (or {@code predicate} is {@code null})
     *
     * {@link Optional#empty()} otherwise.
     *
     * @param predicate
     *    {@link Predicate} to apply the stored value if the current instance is a {@link Valid} one
     *
     * @return {@link Optional} of {@link Validation}
     */
    public final Optional<Validation<E, T>> filter(final Predicate<? super T> predicate) {
        if (!isValid()) {
            return of(this);
        }
        return Objects.isNull(predicate) || predicate.test(get())
                ? of(this)
                : empty();
    }


    /**
     *    Applies a {@link Function} {@code mapper} to the stored value of this {@link Validation} if this is a {@link Valid}.
     * Otherwise does nothing if this is a {@link Invalid}.
     *
     * @param mapper
     *    The mapping function to apply to a value of a {@link Valid} instance.
     *
     * @return new {@link Validation}
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null} and the current instance is a {@link Valid} one
     */
    public final <U> Validation<E, U> map(final Function<? super T, ? extends U> mapper) {
        if (isValid()) {
            Assert.notNull(mapper, "mapper must be not null");
            return Validation.valid(
                    mapper.apply(
                            get()
                    )
            );
        } else {
            return Validation.invalid(getErrors());
        }
    }


    /**
     *    Applies a {@link Function} {@code mapper} to the errors of this {@link Validation} if this is an {@link Invalid}.
     *  Otherwise does nothing if this is a {@link Valid}.
     *
     * @param mapper
     *    A {@link Function} that maps the errors in this {@link Invalid}
     *
     * @return new {@link Validation}
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null} and the current instance is a {@link Invalid} one
     */
    public final <U> Validation<U, T> mapError(final Function<Collection<? super E>, Collection<U>> mapper) {
        if (!isValid()) {
            Assert.notNull(mapper, "mapper must be not null");
            return Validation.invalid(
                    mapper.apply(
                            getErrors()
                    )
            );
        } else {
            return Validation.valid(get());
        }
    }


    /**
     *    Whereas {@code mapperValid} only performs a mapping on a {@link Valid} {@link Validation}, and {@code mapperInvalid}
     * performs a mapping on an {@link Invalid} {@link Validation}, {@code bimap} allows you to provide mapping actions for
     * both, and will give you the result based on what type of {@link Validation} this is. Without this, you would have to do
     * something like:
     *
     * Example:
     *   validation.map(...).mapError(...);
     *
     * @param mapperInvalid
     *    {@link Function} with the invalid mapping operation
     * @param mapperValid
     *    {@link Function} with the valid mapping operation
     *
     * @return {@link Validation}
     *
     * @throws IllegalArgumentException if {@code mapperValid} is {@code null} and the current instance is a {@link Valid} one
     *                                  or {@code mapperInvalid} is {@code null} and the current instance is a {@link Invalid} one
     */
    public final <E2, T2> Validation<E2, T2> map(final Function<Collection<? super E>, Collection<E2>> mapperInvalid,
                                                 final Function<? super T, ? extends T2> mapperValid) {
        if (isValid()) {
            Assert.notNull(mapperValid, "mapperValid must be not null");
            return Validation.valid(
                    mapperValid.apply(
                            get()
                    )
            );
        } else {
            Assert.notNull(mapperInvalid, "mapperInvalid must be not null");
            return Validation.invalid(
                    mapperInvalid.apply(
                            getErrors()
                    )
            );
        }
    }


    /**
     *    If the current {@link Validation} is a {@link Valid} instance, returns the result of applying the given
     * {@link Validation}-bearing mapping function to the value. Otherwise does nothing if this is a {@link Invalid}.
     *
     * @param mapper
     *    The mapping {@link Function} to apply the value of a {@link Valid} instance
     *
     * @return new {@link Validation}
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null} and the current instance is a {@link Valid} one
     */
    @SuppressWarnings("unchecked")
    public final <U> Validation<E, U> flatMap(final Function<? super T, ? extends Validation<E, ? extends U>> mapper) {
        if (isValid()) {
            Assert.notNull(mapper, "mapper must be not null");
            return (Validation<E, U>) mapper.apply(get());
        }
        return (Validation<E, U>) this;
    }


    /**
     * Merge given {@code validation} with the current one, managing the following use cases:
     *
     *   1. this = {@link Valid},   validation = {@link Valid}    =>  return a {@link Valid} instance
     *   2. this = {@link Valid},   validation = {@link Invalid}  =>  return an {@link Invalid} instance with the errors of {@code validation}
     *   3. this = {@link Invalid}, validation = {@link Valid}    =>  return an {@link Invalid} instance with the errors of {@code this}
     *   4. this = {@link Invalid}, validation = {@link Invalid}  =>  return an {@link Invalid} instance with the errors of {@code this} and {@code validation}
     *
     * If provided {@code validation} is {@code null}, the current instance will be returned.
     *
     * @param validation
     *    New {@link Validation} to merge with the current one
     *
     * @return {@link Validation}
     */
    public final Validation<E, T> ap(final Validation<E, T> validation) {
        if (Objects.isNull(validation)) {
            return this;
        }
        // This is a Valid instance
        if (isValid()) {
            // Only if current and given validation are Valid, a Valid instance will be returned
            if (validation.isValid()) {
                return valid(validation.get());

            // This is Valid but validation is Invalid
            } else {
                final Collection<E> errors = validation.getErrors();
                return invalid(errors);
            }
        } else {
            // Due to only this is Invalid, return only its errors
            if (validation.isValid()) {
                final Collection<E> errors = this.getErrors();
                return invalid(errors);

            // Add both errors of this and validation
            } else {
                final Collection<E> errors = new ArrayList<>(this.getErrors());
                errors.addAll(validation.getErrors());
                return invalid(errors);
            }
        }
    }


    /**
     *    Applies {@code mapperValid} if current {@link Validation} is a {@link Valid} instance, {@code mapperInvalid}
     * if it is an {@link Invalid}, transforming internal values into another one.
     *
     * Example:
     *
     *   Validation<String, String> valid = ...;
     *   int i = valid.fold(String::length, List::length);
     *
     * @param mapperInvalid
     *    The mapping {@link Function} to apply the value of a {@link Invalid} instance
     * @param mapperValid
     *    The mapping {@link Function} to apply the value of a {@link Valid} instance
     *
     * @return the result of applying the right {@link Function}
     *
     * @throws IllegalArgumentException if {@code mapperValid} is {@code null} and the current instance is a {@link Valid} one
     *                                  or {@code mapperInvalid} is {@code null} and the current instance is a {@link Invalid} one
     */
    public final <U> U fold(final Function<Collection<? super E>, U> mapperInvalid,
                            final Function<? super T, ? extends U> mapperValid) {
        if (isValid()) {
            Assert.notNull(mapperValid, "mapperValid must be not null");
            return mapperValid.apply(get());
        }
        else {
            Assert.notNull(mapperInvalid, "mapperInvalid must be not null");
            return mapperInvalid.apply(getErrors());
        }
    }


    /**
     * Performs the given {@code action} to the stored value if the current {@link Validation} is a {@link Valid} one.
     *
     * @param action
     *    {@link Consumer} invoked for the stored value of the current {@link Valid} instance.
     *
     * @return {@link Validation}
     */
    public final Validation<E, T> peek(final Consumer<? super T> action) {
        if (isValid() && Objects.nonNull(action)) {
            action.accept(get());
        }
        return this;
    }


    /**
     * Performs the given {@code action} to the stored value if the current {@link Validation} is a {@link Invalid} one.
     *
     * @param action
     *    {@link Consumer} invoked for the stored value of the current {@link Invalid} instance.
     *
     * @return {@link Validation}
     */
    public final Validation<E, T> peekError(final Consumer<Collection<? super E>> action) {
        if (!isValid() && Objects.nonNull(action)) {
            action.accept(getErrors());
        }
        return this;
    }


    /**
     *    Performs the given {@code actionValid} to the stored value if the current {@link Validation} is a {@link Valid}
     * one. If the current instance is a {@link Invalid}, performs {@code actionInvalid}.
     *
     * @param actionInvalid
     *    The {@link Invalid} {@link Consumer} operation
     * @param actionValid
     *    The {@link Valid} {@link Consumer} operation
     *
     * @return {@link Validation}
     */
    public final Validation<E, T> peek(final Consumer<Collection<? super E>> actionInvalid,
                                       final Consumer<? super T> actionValid) {
        if (isValid() && Objects.nonNull(actionValid)) {
            actionValid.accept(get());
        }
        if (!isValid() && Objects.nonNull(actionInvalid)) {
            actionInvalid.accept(getErrors());
        }
        return this;
    }


    /**
     * Returns the stored value if the underline instance is {@link Valid}, otherwise returns {@code other}.
     *
     * @param other
     *    Returned value if current instance is an {@link Invalid} one
     *
     * @return {@code T} value stored in {@link Valid} instance, {@code other} otherwise
     */
    public final T getOrElse(final T other) {
        if (isValid()) {
            return get();
        }
        return other;
    }


    /**
     * Returns the stored value if the underline instance is {@link Valid}, otherwise throws {@code exceptionSupplier.get()}.
     *
     * @param exceptionSupplier
     *    An exception supplier
     *
     * @return {@code T} value stored in {@link Valid} instance, throws {@code X} otherwise
     *
     * @throws IllegalArgumentException if {@code exceptionSupplier} is {@code null} and the current instance is a {@link Invalid} one
     * @throws X if is an {@link Invalid}
     */
    public final <X extends Throwable> T getOrElseThrow(final Supplier<X> exceptionSupplier) throws X {
        if (isValid()) {
            return get();
        }
        Assert.notNull(exceptionSupplier, "exceptionSupplier must be not null");
        throw exceptionSupplier.get();
    }


    /**
     * Returns this {@link Validation} if it is {@link Valid}, otherwise return the alternative.
     *
     * @param other
     *    An alternative {@link Validation}
     *
     * @return {@link Validation}
     */
    @SuppressWarnings("unchecked")
    public final Validation<E, T> orElse(final Validation<? extends E, ? extends T> other) {
        return isValid()
                ? this
                : (Validation<E, T>) other;
    }


    /**
     * Returns this {@link Validation} if it is {@link Valid}, otherwise return the result of evaluating {@link Supplier}.
     *
     * @param supplier
     *    An alternative {@link Validation} supplier
     *
     * @return {@link Validation}
     *
     * @throws IllegalArgumentException if {@code supplier} is {@code null} and the current instance is a {@link Invalid} one
     */
    @SuppressWarnings("unchecked")
    public final Validation<E, T> orElse(final Supplier<Validation<? extends E, ? extends T>> supplier) {
        if (isValid()) {
            return this;
        }
        Assert.notNull(supplier, "supplier must be not null");
        return (Validation<E, T>) supplier.get();
    }


    /**
     * Verifies in the current instance has no value, that is:
     *
     *    1. Is a {@link Invalid} one.
     *    2. Is an empty {@link Valid} instance.
     *
     * @return {@code true} is the current instance is empty, {@code false} otherwise
     */
    public final boolean isEmpty() {
        return !isValid() || Objects.isNull(get());
    }


    /**
     * Converts current {@link Validation} to an {@link Either}.
     *
     * Example:
     *
     *   {@link Validation} does not supply the error when {@code getOrElseThrow()} is used.
     *   You have switch to an {@link Either} first:
     *
     *      validateEmail("abc@def.gh")
     *         // we cannot access the error part
     *         .getOrElseThrow(() -> new RuntimeException("could not validate"));
     *
     *      validateEmail("abc@def.gh")
     *         .toEither()
     *         // here we can access the error part
     *         .getOrElseThrow(errors -> new RuntimeException(errors.toString()));
     *
     * @return {@code Either.right(get())} if current {@link Validation} is {@link Valid}
     *         {@code Either.left(getErrors())} if it is {@link Invalid}
     */
    public final Either<Collection<E>, T> toEither() {
        return isValid()
                ? Either.right(get())
                : Either.left(getErrors());
    }


    /**
     *    If the current {@link Validation} is an instance of {@link Valid} wraps the stored value into an {@link Optional} object.
     * Otherwise return {@link Optional#empty()}
     *
     * @return {@link Optional}
     */
    public final Optional<T> toOptional() {
        return isEmpty()
                ? empty()
                : of(get());
    }

}