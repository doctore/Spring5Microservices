package com.spring5microservices.common.util.validation;

import com.spring5microservices.common.util.ObjectUtil;
import com.spring5microservices.common.util.Try.Failure;
import com.spring5microservices.common.util.Try.Success;
import com.spring5microservices.common.util.Try.Try;
import com.spring5microservices.common.util.either.Either;
import com.spring5microservices.common.util.either.Left;
import com.spring5microservices.common.util.either.Right;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
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
 * Class used to validate the given instance, defining 2 different status to manage the result:
 * <p>
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
     *    Gets the {@code error} of this {@link Validation} if it is an {@link Invalid} or throws {@link NoSuchElementException}
     * if this is a {@link Valid}.
     *
     * @return the {@link Invalid} {@link Collection} of errors
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
     * @return {@link Valid}
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
     * @return {@link Invalid}
     */
    public static <E, T> Validation<E, T> invalid(final Collection<E> errors) {
        return Invalid.ofNullable(errors);
    }


    /**
     * Creates a {@link Validation} using the given {@link Either}, following the rules:
     * <p>
     *  - If {@link Right} then new {@link Validation} instance will be {@link Valid}
     *  - If {@link Left} or {@code null} then new {@link Validation} instance will be {@link Invalid}
     *
     * @param either
     *    {@link Either} used as source
     *
     * @return {@code Valid(either.get())} if {@link Either} is a {@link Right},
     *         otherwise {@code Invalid(either.getLeft())}
     */
    public static <E, T> Validation<E, T> fromEither(final Either<? extends E, ? extends T> either) {
        if (nonNull(either) && either.isRight()) {
            return valid(either.get());
        }
        List<E> errors = isNull(either) || isNull(either.getLeft())
                ? new ArrayList<>()
                : asList(either.getLeft());
        return invalid(errors);
    }


    /**
     * Creates a {@link Validation} using the given {@link Try}, following the rules:
     * <p>
     *  - If {@link Success} then new {@link Validation} instance will be {@link Valid}
     *  - If {@link Failure} or {@code null} then new {@link Validation} instance will be {@link Invalid}
     *
     * @param t
     *    {@link Try} used as source
     *
     * @return {@code Valid(t.get())} if {@link Try} is a {@link Success},
     *         otherwise {@code Invalid(t.getException())}
     */
    public static <T> Validation<Throwable, T> fromTry(final Try<? extends T> t) {
        if (nonNull(t) && t.isSuccess()) {
            return valid(t.get());
        }
        List<Throwable> errors = isNull(t) || isNull(t.getException())
                ? new ArrayList<>()
                : asList(t.getException());
        return invalid(errors);
    }


    /**
     * Merges the given {@code validations} in a one result that will be:
     * <p>
     *   1. {@link Valid} instance if all given {@code validations} are {@link Valid} ones or such parameters is {@code null} or empty.
     * <p>
     *   2. {@link Invalid} instance if there is at least one {@link Invalid} in the given {@code validations}. In this case, errors of
     *      all provided {@link Invalid}s will be included in the result.
     *
     * <pre>
     * Examples:
     *
     *   combine(Validation.valid(11), Validation.valid(7));                                                // Valid(7)
     *   combine(Validation.valid(13), Validation.invalid(asList("A")));                                    // Invalid(List("A"))
     *   combine(Validation.valid(10), Validation.invalid(asList("A")), Validation.invalid(asList("B")));   // Invalid(List("A", "B"))
     * </pre>
     *
     * @param validations
     *    {@link Validation} instances to combine
     *
     * @return {@link Validation}
     */
    @SafeVarargs
    public static <E, T> Validation<E, T> combine(final Validation<E, T>... validations) {
        Validation<E, T> result = Valid.empty();
        if (!ObjectUtil.isEmpty(validations)) {
            for (Validation<E, T> validation : validations) {
                result = result.ap(validation);
            }
        }
        return result;
    }


    /**
     *    Checks the given {@code suppliers}, returning a {@link Valid} instance if no {@link Invalid} {@link Supplier}
     * was given or the first {@link Invalid} one.
     *
     * <pre>
     * Examples:
     *
     *   combineGetFirstInvalid(() -> Validation.valid(1), () -> Validation.valid(7));                                                      // Valid(7)
     *   combineGetFirstInvalid(() -> Validation.valid(3), () -> Validation.invalid(asList("A")));                                          // Invalid(List("A"))
     *   combineGetFirstInvalid(() -> Validation.valid(2), () -> Validation.invalid(asList("A")), () -> Validation.invalid(asList("B")));   // Invalid(List("A"))
     * </pre>
     *
     * @param suppliers
     *    {@link Supplier} of {@link Validation} instances to verify
     *
     * @return {@link Validation}
     */
    @SafeVarargs
    public static <E, T> Validation<E, T> combineGetFirstInvalid(final Supplier<Validation<E, T>>... suppliers) {
        Validation<E, T> result = Valid.empty();
        if (!ObjectUtil.isEmpty(suppliers)) {
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
     * Filters the current {@link Validation} returning {@code Optional.of(this)} if:
     * <p>
     *   1. Current instance is {@link Invalid}
     *   2. Current instance is {@link Valid} and stored value verifies given {@link Predicate} (or {@code predicate} is {@code null})
     *      {@link Optional#empty()} otherwise.
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
        return isNull(predicate) || predicate.test(get())
                ? of(this)
                : empty();
    }


    /**
     * Filters the current {@link Validation} returning:
     * <p>
     *   1. {@link Valid} if this is a {@link Valid} and its value matches given {@link Predicate} (or {@code predicate} is {@code null})
     *   2. {@link Invalid} applying {@code zero} if this is {@link Valid} but its value does not match given {@link Predicate}
     *   3. {@link Invalid} with the existing value if this is a {@link Invalid}
     *
     * <pre>
     * Examples:
     *
     *   Validation.valid(11).filterOrElse(i -> i > 10, "error");                    // Valid(11)
     *   Validation.valid(7).filterOrElse(i -> i > 10, "error");                     // Invalid(List("error"))
     *   Validation.invalid(asList("warning")).filterOrElse(i -> i > 10, "error");   // Invalid(List("warning"))
     * </pre>
     *
     * @param predicate
     *    {@link Predicate} to apply the stored value if the current instance is a {@link Valid} one
     * @param zero
     *    {@link Function} that turns a {@link Valid} value into a {@link Invalid} one if this is {@link Valid}
     *    but its value does not match given {@link Predicate}
     *
     * @throws IllegalArgumentException if {@code zero} is {@code null}, this is a {@link Valid} but does not match given {@link Predicate}
     *
     * @return {@link Validation}
     */
    public final Validation<E, T> filterOrElse(final Predicate<? super T> predicate,
                                               final Function<? super T, ? extends E> zero) {
        if (!isValid()) {
            return this;
        }
        if (isNull(predicate) || predicate.test(get())) {
            return this;
        }
        Assert.notNull(zero, "zero must be not null");
        return invalid(
                asList(
                        zero.apply(
                                get()
                        )
                )
        );
    }


    /**
     *    Applies a {@link Function} {@code mapper} to the stored value of this {@link Validation} if this is a {@link Valid}.
     * Otherwise, does nothing if this is a {@link Invalid}.
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
            return valid(
                    mapper.apply(
                            get()
                    )
            );
        }
        return invalid(getErrors());
    }


    /**
     *    Whereas {@code map} with {@code mapper} argument only performs a mapping on a {@link Valid} {@link Validation},
     * and {@code mapError} performs a mapping on an {@link Invalid} {@link Validation}, {@code map} with two {@link Function}
     * mappers as arguments, allows you to provide mapping actions for both, and will give you the result based on what
     * type of {@link Validation} this is. Without this, you would have to do something like:
     *
     * <pre>
     * Example:
     *
     *   validation.map(...).mapError(...);
     * </pre>
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
            return valid(
                    mapperValid.apply(
                            get()
                    )
            );
        }
        Assert.notNull(mapperInvalid, "mapperInvalid must be not null");
        return invalid(
                mapperInvalid.apply(
                        getErrors()
                )
        );
    }


    /**
     *    Applies a {@link Function} {@code mapper} to the errors of this {@link Validation} if this is an {@link Invalid}.
     *  Otherwise, does nothing if this is a {@link Valid}.
     *
     * @param mapper
     *    A {@link Function} that maps the errors in this {@link Invalid}
     *
     * @return new {@link Validation}
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null} and the current instance is a {@link Invalid} one
     */
    public final <U> Validation<U, T> mapInvalid(final Function<Collection<? super E>, Collection<U>> mapper) {
        if (!isValid()) {
            Assert.notNull(mapper, "mapper must be not null");
            return invalid(
                    mapper.apply(
                            getErrors()
                    )
            );
        }
        return valid(
                get()
        );
    }


    /**
     *    If the current {@link Validation} is a {@link Valid} instance, returns the result of applying the given
     * {@link Validation}-bearing mapping function to the value. Otherwise, does nothing if this is a {@link Invalid}.
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
        return invalid(getErrors());
    }


    /**
     * Merge given {@code validation} with the current one, managing the following use cases:
     * <p>
     *   1. this = {@link Valid},   validation = {@link Valid}    =>  return a {@link Valid} instance with the value of {@code validation}
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
        if (isNull(validation)) {
            return this;
        }
        // This is a Valid instance
        if (isValid()) {
            // Only if current and given validation are Valid, a Valid instance will be returned
            if (validation.isValid()) {
                return valid(validation.get());
            }
            // This is Valid but validation is Invalid
            final Collection<E> errors = validation.getErrors();
            return invalid(errors);

        // This is an Invalid instance
        } else {
            // Due to only this is Invalid, return only its errors
            if (validation.isValid()) {
                final Collection<E> errors = this.getErrors();
                return invalid(errors);
            }
            // Add both errors of this and validation
            final Collection<E> errors = new ArrayList<>(this.getErrors());
            errors.addAll(validation.getErrors());
            return invalid(errors);
        }
    }


    /**
     * Applies {@code mapper} to the current {@link Validation}, transforming internal values into another one.
     *
     * <pre>
     * Example:
     *
     *   Validation<String, String> valid = ...
     *   int i = valid.fold(
     *              v ->
     *                 v.isValid()
     *                         ? ofNullable(v.get()).orElse(defaultValue)
     *                         : v.getErrors().size()
     *           );
     * </pre>
     *
     * @param mapper
     *    The mapping {@link Function} to apply to the current {@link Validation}
     *
     * @return the result of applying provided {@link Function}
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null}
     */
    public final <U> U fold(final Function<? super Validation<E, T>, ? extends U> mapper) {
        Assert.notNull(mapper, "mapper must be not null");
        return mapper.apply(this);
    }


    /**
     *    Applies {@code mapperValid} if current {@link Validation} is a {@link Valid} instance, {@code mapperInvalid}
     * if it is an {@link Invalid}, transforming internal values into another one.
     *
     * <pre>
     * Example:
     *
     *   Validation<String, String> valid = ...
     *   int i = valid.fold(
     *              String::length,
     *              List::length
     *           );
     * </pre>
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
            return mapperValid.apply(
                    get()
            );
        } else {
            Assert.notNull(mapperInvalid, "mapperInvalid must be not null");
            return mapperInvalid.apply(
                    getErrors()
            );
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
        if (isValid() && nonNull(action)) {
            action.accept(get());
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
        if (isValid() && nonNull(actionValid)) {
            actionValid.accept(
                    get()
            );
        }
        if (!isValid() && nonNull(actionInvalid)) {
            actionInvalid.accept(
                    getErrors()
            );
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
    public final Validation<E, T> peekInvalid(final Consumer<Collection<? super E>> action) {
        if (!isValid() && nonNull(action)) {
            action.accept(
                    getErrors()
            );
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
        return isValid()
                ? get()
                : other;
    }


    /**
     *    Returns the stored value if the underline instance is {@link Valid}, otherwise throws an {@link Exception} using
     * provided {@link Supplier}.
     *
     * @param exceptionSupplier
     *    An {@link Exception} {@link Supplier}
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
     *    Returns the stored value if the underline instance is {@link Valid}, otherwise throws an {@link Exception} using
     * provided {@link Function} with the current {@link Invalid} instance value as parameter.
     *
     * @param exceptionFunction
     *    A {@link Function} which creates an {@link Exception} based on an {@link Invalid} value
     *
     * @return {@code T} value stored in {@link Valid} instance, throws {@code X} otherwise
     *
     * @throws IllegalArgumentException if {@code exceptionFunction} is {@code null} and the current instance is a {@link Invalid} one
     * @throws X if is an {@link Invalid}
     */
    public final <X extends Throwable> T getOrElseThrow(final Function<Collection<? super E>, X> exceptionFunction) throws X {
        if (isValid()) {
            return get();
        }
        Assert.notNull(exceptionFunction, "exceptionFunction must be not null");
        throw exceptionFunction.apply(
                getErrors()
        );
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
     * <p>
     *    1. Is a {@link Invalid} one.
     *    2. Is an empty {@link Valid} instance.
     *
     * @return {@code true} is the current instance is empty, {@code false} otherwise
     */
    public final boolean isEmpty() {
        return !isValid() || isNull(get());
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


    /**
     * Converts current {@link Validation} to an {@link Either}.
     *
     * <pre>
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
     * </pre>
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
     *    Transforms this {@link Validation} into a {@link Try} instance. If the current {@link Validation} is an instance
     * of {@link Valid} wraps the stored value into a {@link Success} one, {@link Failure} otherwise.
     *
     * @param mapperInvalid
     *   {@link Function} that maps the {@link Left} value to a {@link Throwable} instance
     *
     * @return {@link Try}
     *
     * @throws IllegalArgumentException if {@code mapperInvalid} is {@code null} and the current instance is an {@link Invalid} one
     */
    public final Try<T> toTry(final Function<Collection<? super E>, ? extends Throwable> mapperInvalid) {
        if (!isValid()) {
            Assert.notNull(mapperInvalid, "mapperInvalid must be not null");
            return Try.failure(
                    mapperInvalid.apply(
                            getErrors()
                    )
            );
        } else {
            return Try.success(
                    get()
            );
        }
    }

}