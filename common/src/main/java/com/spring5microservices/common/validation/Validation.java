package com.spring5microservices.common.validation;

import java.io.Serializable;
import java.util.ArrayList;
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
 */
public abstract class Validation<T> implements Serializable {

    /**
     * Creates a {@link Valid} that contains the given {@code value}.
     *
     * @param value
     *    The value to store in the returned {@link Valid}
     *
     * @return {@code Valid(value)}
     */
    public static <T> Validation<T> valid(T value) {
        return Valid.of(value);
    }

    /**
     * Creates an {@link Invalid} that contains the given {@code errors}.
     *
     * @param errors
     *    {@link List} of errors to include in the returned {@link Invalid}
     *
     * @return {@code Invalid(error)}
     */
    public static <T> Validation<T> invalid(List<String> errors) {
        return Invalid.of(errors);
    }


    /**
     *    Applies a {@link Function} {@code mapper} to the stored value of this {@code Validation} if this is an {@link Valid}.
     * Otherwise does nothing if this is a {@link Invalid}.
     *
     * @param mapper
     *    The mapping function to apply to a value
     *
     * @return A new value
     *
     * @throws NullPointerException if {@code mapper} is {@code null} and the current instance is a {@link Valid} one
     */
    public final <U> Validation<U> map(Function<? super T, ? extends U> mapper) {
        if (isValid()) {
            Objects.requireNonNull(mapper, "mapper is null");
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
    public final Validation<T> mapError(Function<List<String>, List<String>> mapper) {
        if (!isValid()) {
            Objects.requireNonNull(mapper, "mapper is null");
            final List<String> errors = getErrors();
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
    public final <U> Validation<U> bimap(Function<? super T, ? extends U> mapperValid, Function<List<String>, List<String>> mapperInvalid) {
        if (isValid()) {
            Objects.requireNonNull(mapperValid, "mapperValid is null");
            final T value = get();
            return Validation.valid(mapperValid.apply(value));
        } else {
            Objects.requireNonNull(mapperInvalid, "mapperInvalid is null");
            final List<String> errors = getErrors();
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
     * @return {@code Validationulln}
     *
     * @throws NullPointerException if {@code mapper} is {@code null} and the current instance is a {@link Valid} one
     */
    public final <U> Validation<U> flatMap(Function<? super T, ? extends Validation<? extends U>> mapper) {
        if (isValid()) {
            Objects.requireNonNull(mapper, "mapper is null");
            return (Validation<U>) mapper.apply(get());
        }
        return (Validation<U>) this;
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
    public final Validation<T> peek(Consumer<? super T> action) {
        if (isValid()) {
            Objects.requireNonNull(action, "action is null");
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
    public final Validation<T> peekError(Consumer<List<String>> action) {
        if (!isValid()) {
            Objects.requireNonNull(action, "action is null");
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
    public final Validation<T> bipeek(Consumer<? super T> actionValid, Consumer<List<String>> actionInvalid) {
        if (isValid()) {
            Objects.requireNonNull(actionValid, "actionValid is null");
            actionValid.accept(get());
        } else {
            Objects.requireNonNull(actionInvalid, "actionInvalid is null");
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
    public final Optional<Validation<T>> filter(Predicate<? super T> predicate) {
        if (!isValid()) {
            return of(this);
        }
        Objects.requireNonNull(predicate, "predicate is null");
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
    public final <T> Validation<T> ap(Validation<? extends T> validation) {
        if (Objects.isNull(validation)) {
            return (Validation<T>) this;
        }
        // this is a Valid instance
        if (isValid()) {
            // Only if current and given validation are Valid, a Valid instance will be returned
            if (validation.isValid()) {
                return valid(validation.get());

            // this is Valid but validation is Invalid
            } else {
                final List<String> errors = validation.getErrors();
                return invalid(errors);
            }
        } else {
            // Due only this is Invalid, return only its errors
            if (validation.isValid()) {
                final List<String> errors = this.getErrors();
                return invalid(errors);

            // Add both errors of this and validation
            } else {
                final List<String> errors = new ArrayList<>(this.getErrors());
                errors.addAll(validation.getErrors());
                return invalid(errors);
            }
        }
    }


    /**
     *    If the current {@code Validation} is an instance of {@Valid} wraps the stored value into an {@link Optional} object.
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
    public final Validation<T> orElse(Validation<? extends T> other) {
        return isValid()
                ? this
                : (Validation<T>) other;
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
    public final Validation<T> orElse(Supplier<Validation<? extends T>> supplier) {
        if (isValid()) {
            return this;
        }
        Objects.requireNonNull(supplier, "supplier is null");
        return (Validation<T>) supplier.get();
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
    public final <X extends Throwable> T getOrElseThrow(Supplier<X> exceptionSupplier) throws X {
        if (isValid()) {
            return get();
        }
        Objects.requireNonNull(exceptionSupplier, "exceptionSupplier is null");
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
     * Gets the {@link List} of errors of this {@code Validation} if it is an {@link Invalid} or throws if this is a {@link Valid}.
     *
     * @return the {@link List} of errors, if present
     *
     * @throws NoSuchElementException if this is a {@link Valid}
     */
    public abstract List<String> getErrors();

}