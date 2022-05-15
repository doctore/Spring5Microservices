package com.spring5microservices.common.validation;

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

import static java.util.Optional.empty;
import static java.util.Optional.of;

/**
 * Class used to validate the given instance, defining 2 different status to manage the result:
 *
 *    {@link Valid} the instance has verified all provided validations
 *    {@link Invalid} with the {@link List} of validations the given instance does not verify
 *
 * @param <T>
 *    Value type in the case of {@link Valid}
 * @param <E>
 *    Value type in the case of {@link Invalid}
 */
public abstract class Validation<E, T> implements Serializable {

    /**
     * Creates a {@link Valid} that contains the given {@code value}.
     *
     * @param value
     *    The value to store in the returned {@link Valid}
     *
     * @return {@code Valid(value)}
     */
    public static <E, T> Validation<E, T> valid(T value) {
        return Valid.of(value);
    }

    /**
     * Creates an {@link Invalid} that contains the given {@code errors}.
     *
     * @param errors
     *    {@link Collection} of errors to include in the returned {@link Invalid}
     *
     * @return {@code Invalid(error)}
     */
    public static <E, T> Validation<E, T> invalid(Collection<E> errors) {
        return Invalid.of(errors);
    }


    /**
     *    Applies a {@link Function} {@code mapper} to the stored value of this {@code Validation} if this is an {@link Valid}.
     * Otherwise does nothing if this is a {@link Invalid}.
     *
     * @param mapper
     *    The mapping function to apply to a value of a {@link Valid} instance.
     *
     * @return A new value
     *
     * @throws NullPointerException if {@code mapper} is {@code null} and the current instance is a {@link Valid} one
     */
    public final <U> Validation<E, U> map(final Function<? super T, ? extends U> mapper) {
        if (isValid()) {
            Objects.requireNonNull(mapper, "mapper must be not null");
            final T value = get();
            return Validation.valid(mapper.apply(value));
        } else {
            return Validation.invalid(getErrors());
        }
    }


    /**
     *    Applies a {@link Function} {@code mapper} to the errors of this {@code Validation} if this is an {@link Invalid}.
     *  Otherwise does nothing if this is a {@link Valid}.
     *
     * @param mapper
     *    A {@link Function} that maps the errors in this {@link Invalid}
     *
     * @return {@code Validation}
     *
     * @throws NullPointerException if {@code mapper} is {@code null} and the current instance is a {@link Invalid} one
     */
    public final <U> Validation<U, T> mapError(final Function<Collection<? super E>, Collection<U>> mapper) {
        if (!isValid()) {
            Objects.requireNonNull(mapper, "mapper must be not null");
            final Collection<E> errors = getErrors();
            return Validation.invalid(mapper.apply(errors));
        } else {
            return Validation.valid(get());
        }
    }


    /**
     *    Whereas {@code map} only performs a mapping on a {@link Valid} {@code Validation}, and {@code mapError} performs a mapping
     * on an {@link Invalid} {@code Validation}, {@code bimap} allows you to provide mapping actions for both, and will give you the
     * result based on what type of {@code Validation} this is. Without this, you would have to do something like:
     *
     *  <pre>
     *     validation.map(...).mapError(...);
     *  </pre>
     *
     * @param mapperValid
     *    The valid mapping operation
     * @param mapperInvalid
     *    The invalid mapping operation
     *
     * @return {@code Validation}
     *
     * @throws NullPointerException if {@code mapperValid} is {@code null} and the current instance is a {@link Valid} one
     *                              or {@code mapperInvalid} is {@code null} and the current instance is a {@link Invalid} one
     */
    public final <E2, T2> Validation<E2, T2> bimap(final Function<? super T, ? extends T2> mapperValid,
                                                   final Function<Collection<? super E>, Collection<E2>> mapperInvalid) {
        if (isValid()) {
            Objects.requireNonNull(mapperValid, "mapperValid must be not null");
            final T value = get();
            return Validation.valid(mapperValid.apply(value));
        } else {
            Objects.requireNonNull(mapperInvalid, "mapperInvalid must be not null");
            final Collection<E> errors = getErrors();
            return Validation.invalid(mapperInvalid.apply(errors));
        }
    }


    /**
     *    If the current {@code Validation} if this is an {@link Valid} instance, returns the result of applying the given
     * {@code Validation}-bearing mapping function to the value. Otherwise does nothing if this is a {@link Invalid}.
     *
     * @param mapper
     *    The mapping function to apply to a value of a {@link Valid} instance
     *
     * @return {@code Validation}
     *
     * @throws NullPointerException if {@code mapper} is {@code null} and the current instance is a {@link Valid} one
     */
    public final <U> Validation<E, U> flatMap(final Function<? super T, ? extends Validation<E, ? extends U>> mapper) {
        if (isValid()) {
            Objects.requireNonNull(mapper, "mapper must be not null");
            return (Validation<E, U>) mapper.apply(get());
        }
        return (Validation<E, U>) this;
    }


    /**
     * Performs the given {@code action} to the stored value if the current {@code Validation} is a {@link Valid} one.
     *
     * @param action
     *    {@link Consumer} invoked for the stored value of the current {@link Valid} instance.
     *
     * @return {@code Validation}
     *
     * @throws NullPointerException if {@code action} is {@code null} and the current instance is a {@link Valid} one
     */
    public final Validation<E, T> peek(final Consumer<? super T> action) {
        if (isValid()) {
            Objects.requireNonNull(action, "action must be not null");
            action.accept(get());
        }
        return this;
    }


    /**
     * Performs the given {@code action} to the stored value if the current {@code Validation} is a {@link Invalid} one.
     *
     * @param action
     *    {@link Consumer} invoked for the stored value of the current {@link Invalid} instance.
     *
     * @return {@code Validation}
     *
     * @throws NullPointerException if {@code action} is {@code null} and the current instance is a {@link Invalid} one
     */
    public final Validation<E, T> peekError(final Consumer<Collection<? super E>> action) {
        if (!isValid()) {
            Objects.requireNonNull(action, "action must be not null");
            action.accept(getErrors());
        }
        return this;
    }


    /**
     *    Whereas {@code peek} only performs an action on a {@link Valid} {@code Validation}, and {@code peekError} performs an action
     * on an {@link Invalid} {@code Validation}, {@code bipeek} allows you to provide actions for both, and will give you the result
     * based on what type of {@code Validation} this is.
     *
     * @param actionValid
     *    The valid {@link Consumer} operation
     * @param actionInvalid
     *    The invalid {@link Consumer} operation
     *
     * @return {@code Validation}
     *
     * @throws NullPointerException if {@code actionValid} is {@code null} and the current instance is a {@link Valid} one
     *                              or {@code actionInvalid} is {@code null} and the current instance is a {@link Invalid} one
     */
    public final Validation<E, T> bipeek(final Consumer<? super T> actionValid,
                                         final Consumer<Collection<? super E>> actionInvalid) {
        if (isValid()) {
            Objects.requireNonNull(actionValid, "actionValid must be not null");
            actionValid.accept(get());
        } else {
            Objects.requireNonNull(actionInvalid, "actionInvalid must be not null");
            actionInvalid.accept(getErrors());
        }
        return this;
    }


    /**
     *    Verifies the given {@code predicate} with the stored value if the current {@code Validation} is a {@link Valid} one.
     * Otherwise return an empty {@link Optional}
     *
     * @param predicate
     *    {@link Predicate} to apply the stored value if the current instance is a {@link Valid} one
     *
     * @return {@link Optional} of {@link Validation}
     *
     * @throws NullPointerException if {@code predicate} is {@code null} and the current instance is a {@link Valid} one
     */
    public final Optional<Validation<E, T>> filter(final Predicate<? super T> predicate) {
        if (!isValid()) {
            return of(this);
        }
        Objects.requireNonNull(predicate, "predicate must be not null");
        return predicate.test(get())
                ? of(this)
                : empty();
    }


    /**
     * Merge given {@code validation} with the current one, managing the following use cases:
     *
     *   1. this = Valid, validation = Valid      =>  return a Valid instance
     *   2. this = Valid, validation = Invalid    =>  return an Invalid instance with the errors of {@code validation}
     *   3. this = Invalid, validation = Valid    =>  return an Invalid instance with the errors of {@code this}
     *   4. this = Invalid, validation = Invalid  =>  return an Invalid instance with the errors of {@code this} and {@code validation}
     *
     * If provided {@code validation} is {@code null}, the current instance will be returned.
     *
     * @param validation
     *    New {@code Validation} to merge with the current one
     *
     * @return {@code Validation}
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
            // Due only this is Invalid, return only its errors
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
     *    If the current {@code Validation} is an instance of {@link Valid} wraps the stored value into an {@link Optional} object.
     * Otherwise return {@code Optional.empty}
     *
     * @return {@link Optional}
     */
    public final Optional<T> toOptional() {
        return isEmpty()
                ? empty()
                : of(get());
    }


    /**
     * Returns this {@code Validation} if it is {@link Valid}, otherwise return the alternative.
     *
     * @param other
     *    An alternative {@code Validation}
     *
     * @return {@code Validation}
     */
    public final Validation<E, T> orElse(final Validation<? extends E, ? extends T> other) {
        return isValid()
                ? this
                : (Validation<E, T>) other;
    }


    /**
     * Returns this {@code Validation} if it is {@link Valid}, otherwise return the result of evaluating {@link Supplier}.
     *
     * @param supplier
     *    An alternative {@code Validation} supplier
     *
     * @return {@code Validation}
     *
     * @throws NullPointerException if {@code supplier} is {@code null} and the current instance is a {@link Invalid} one
     */
    public final Validation<E, T> orElse(final Supplier<Validation<? extends E, ? extends T>> supplier) {
        if (isValid()) {
            return this;
        }
        Objects.requireNonNull(supplier, "supplier must be not null");
        return (Validation<E, T>) supplier.get();
    }


    /**
     * Returns the stored value if the underline instance is {@link Valid}, otherwise throws {@code exceptionSupplier.get()}..
     *
     * @param exceptionSupplier
     *    An exception supplier
     *
     * @return {@code T} value stored in {@link Valid} instance, throws {@code X} otherwise
     *
     * @throws NullPointerException if {@code exceptionSupplier} is {@code null} and the current instance is a {@link Invalid} one
     * @throws X if is an {@link Invalid}
     */
    public final <X extends Throwable> T getOrElseThrow(final Supplier<X> exceptionSupplier) throws X {
        if (isValid()) {
            return get();
        }
        Objects.requireNonNull(exceptionSupplier, "exceptionSupplier must be not null");
        throw exceptionSupplier.get();
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
     * Checks whether this is of type {@link Valid}.
     *
     * @return {@code true} if is a {@link Valid}, {@code false} if is an {@link Invalid}
     */
    public abstract boolean isValid();


    /**
     * Gets the value of this {@code Validation} if is a {@link Valid} or throws if this is an {@link Invalid}.
     *
     * @return the value of this {@code Validation}
     *
     * @throws NoSuchElementException if this is an {@link Invalid}
     */
    public abstract T get();


    /**
     * Gets the {@code error} of this {@code Validation} if it is an {@link Invalid} or throws if this is a {@link Valid}.
     *
     * @return {@link Collection} of {@code error}, if present
     *
     * @throws NoSuchElementException if this is a {@link Valid}
     */
    public abstract Collection<E> getErrors();

}