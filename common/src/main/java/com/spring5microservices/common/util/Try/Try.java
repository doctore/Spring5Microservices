package com.spring5microservices.common.util.Try;

import com.spring5microservices.common.util.ObjectUtil;
import com.spring5microservices.common.util.either.Either;
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

/**
 *    Represents a computation that may either result in an exception, or return a successfully computed value. It's
 * similar to, but semantically different from the {@link Either} type.
 * <p>
 *  Instances of {@link Try}, are either an instance of {@link Success} or {@link Failure}.
 * <p>
 *    For example, {@link Try} can be used to perform division on a user-defined input, without the need to do explicit
 * exception-handling in all the places that an exception might occur.
 *
 * @param <T>
 *    Value type in the case of {@link Success}
 */
public abstract class Try<T> implements Serializable {

    private static final long serialVersionUID = 8552575458527833793L;


    /**
     * Returns {@code true} is this is a {@link Success}, {@code false} otherwise.
     */
    public abstract boolean isSuccess();


    /**
     * Gets the value of this {@link Try} if is a {@link Success} or throws if this is an {@link Failure}.
     *
     * @return the {@link Success} value
     *
     * @throws {@code Failure#exception} if this is an {@link Failure}
     */
    public abstract T get();


    /**
     *    Gets the exception of this {@link Try} if is a {@link Failure} or throws {@link NoSuchElementException} if this
     * is an {@link Success}.
     *
     * @return the {@link Failure} exception
     *
     * @throws NoSuchElementException if this is an {@link Success}
     */
    public abstract Throwable getException();


    /**
     * Creates a {@link Success} that contains the given {@code value}.
     *
     * @param supplier
     *    {@link Supplier} used to get the value to store in the returned {@link Success}
     *
     * @return {@link Success}
     */
    public static <T> Try<T> of(final Supplier<T> supplier) {
        try {
            return success(supplier.get());
        } catch (Throwable t) {
            return failure(t);
        }
    }


    /**
     * Creates a {@link Success} that contains the given {@code value}.
     *
     * @param value
     *    The value to store in the returned {@link Success}
     *
     * @return {@link Success}
     */
    public static <T> Try<T> success(final T value) {
        return Success.ofNullable(value);
    }


    /**
     * Creates a {@link Failure} describing the given non-{@code null} value.
     *
     * @param exception
     *    {@link Throwable} to store, which must be non-{@code null}
     *
     * @return {@link Failure}
     *
     * @throws IllegalArgumentException if {@code exception} is {@code null}
     * @throws Throwable if the given {@code exception} is fatal, i.e. non-recoverable
     */
    public static <T> Try<T> failure(final Throwable exception) {
        return Failure.of(exception);
    }


    /**
     * Merges the given {@code tries} in a one result that will be:
     * <p>
     *   1. {@link Success} instance if all given {@code tries} are {@link Success} ones or such parameters is {@code null}
     *      or empty. Using provided {@link BiFunction} {@code mapperSuccess} to get the final value added into the
     *      returned {@link Success}.
     * <p>
     *   2. {@link Failure} instance if there is at least one {@link Failure} in the given {@code tries}. Using provided
     *      {@link BiFunction} {@code mapperFailure} to get the final value added into the returned {@link Success}.
     *
     * <pre>
     * Examples:
     *
     *   mapperFailure = (f1, f2) -> f2;
     *   mapperSuccess = (s1, s2) -> s2;
     *
     *   combine(mapperFailure, mapperSuccess, Try.success(11), Try.success(7));                                                 // Success(7)
     *   combine(mapperFailure, mapperSuccess, Try.success(13), Try.failure(new Exception()));                                   // Failure(new Exception())
     *   combine(mapperFailure, mapperSuccess, Try.success(10), Try.failure(new Exception()), Try.failure(new IOException()));   // Failure(new IOException())
     * </pre>
     *
     * @param mapperFailure
     *    {@link BiFunction} used to calculate the new {@link Failure} based on two provided ones
     * @param mapperSuccess
     *    {@link BiFunction} used to calculate the new {@link Success} based on two provided ones
     * @param tries
     *    {@link Try} instances to combine
     *
     * @return {@link Try}
     *
     * @throws IllegalArgumentException if {@code mapperFailure} or {@code mapperSuccess} is {@code null} and {@code tries}
     *                                  has elements.
     */
    @SafeVarargs
    public static <T> Try<T> combine(final BiFunction<? super Throwable, ? super Throwable, ? extends Throwable> mapperFailure,
                                     final BiFunction<? super T, ? super T, ? extends T> mapperSuccess,
                                     final Try<T>... tries) {
        if (ObjectUtil.isEmpty(tries)) {
            return Success.empty();
        }
        Assert.notNull(mapperFailure, "mapperFailure must be not null");
        Assert.notNull(mapperSuccess, "mapperSuccess must be not null");
        Try<T> result = tries[0];
        for (int i = 1; i < tries.length; i++) {
            result = result.ap(
                    tries[i],
                    mapperFailure,
                    mapperSuccess
            );
        }
        return result;
    }


    /**
     *    Checks the given {@link Supplier} of {@link Try}, returning a {@link Success} instance if no {@link Failure}
     * {@link Supplier} was given or the first {@link Failure} one.
     *
     * <pre>
     * Examples:
     *
     *   mapperRight = (r1, r2) -> r2;
     *
     *   combineGetFirstFailure(mapperSuccess, Try.success(11), Try.success(7));                                                 // Success(7)
     *   combineGetFirstFailure(mapperSuccess, Try.success(13), Try.failure(new Exception()));                                   // Failure(new Exception())
     *   combineGetFirstFailure(mapperSuccess, Try.success(10), Try.failure(new Exception()), Try.failure(new IOException()));   // Failure(new IOException())
     * </pre>
     *
     * @param mapperSuccess
     *    {@link BiFunction} used to calculate the new {@link Success} based on two provided ones
     * @param suppliers
     *    {@link Supplier} of {@link Try} instances to verify
     *
     * @return {@link Try}
     *
     * @throws IllegalArgumentException if {@code mapperSuccess} is {@code null} and {@code suppliers} has elements.
     */
    @SafeVarargs
    public static  <T> Try<T> combineGetFirstFailure(final BiFunction<? super T, ? super T, ? extends T> mapperSuccess,
                                                     final Supplier<Try<T>>... suppliers) {
        if (ObjectUtil.isEmpty(suppliers)) {
            return Success.empty();
        }
        Assert.notNull(mapperSuccess, "mapperSuccess must be not null");
        Try<T> result = suppliers[0].get();
        for (int i = 1; i < suppliers.length; i++) {
            result = result.ap(
                    suppliers[i].get(),
                    (l1, l2) -> l1,
                    mapperSuccess
            );
            if (!result.isSuccess()) {
                return result;
            }
        }
        return result;
    }


    /**
     *    Inverts this {@link Try}. If this is a {@link Failure}, returns its exception wrapped in a {@link Success}.
     * If this is a {@link Success}, returns a {@link Failure} containing an {@link UnsupportedOperationException}.
     *
     * @return {@link Try}
     */
    public final Try<Throwable> failed() {
        return isSuccess()
                ? failure(new UnsupportedOperationException("failed() cannot be invoked from a 'success' Try"))
                : success(getException());
    }


    /**
     * Returns {@code this} if:
     * <p>
     *   1. Current instance is {@link Failure}
     *   2. Current instance is {@link Success} and stored value verifies given {@link Predicate} (or {@code predicate} is {@code null})
     * <p>
     *    Otherwise, returns new {@link Failure} wrapping a {@link NoSuchElementException} instance. If given {@code mapper}
     * invocation returns an {@link Exception} is thrown then returned {@link Try} will a {@link Failure} one too.
     *
     * @param predicate
     *    {@link Predicate} to apply the stored value if the current instance is a {@link Success} one
     *
     * @return {@link Try}
     */
    public final Try<T> filter(final Predicate<? super T> predicate) {
        if (!isSuccess()) {
            return this;
        } else {
            return filterTry(
                    predicate,
                    () -> new NoSuchElementException("Predicate does not hold for " + get())
            );
        }
    }


    /**
     *    Applies a {@link Function} {@code mapper} to the stored value of this {@link Try} if this is a {@link Success}.
     * Otherwise, does nothing if this is a {@link Failure}.
     * <p>
     * If given {@code mapper} invocation returns an {@link Exception} is thrown then returned {@link Try} will {@link Failure}.
     *
     * @param mapper
     *    The mapping function to apply to a value of a {@link Success} instance.
     *
     * @return new {@link Try}
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null} and the current instance is a {@link Success} one
     */
    public final <U> Try<U> map(final Function<? super T, ? extends U> mapper) {
        if (isSuccess()) {
            Assert.notNull(mapper, "mapper must be not null");
            return mapTry(mapper);
        } else {
            return failure(getException());
        }
    }


    /**
     *    Applies a {@link Function} {@code mapper} to the stored value of this {@link Try} if this is a {@link Failure}.
     * Otherwise, does nothing if this is a {@link Success}.
     * <p>
     * If given {@code mapper} invocation returns an {@link Exception} is thrown then returned {@link Try} will {@link Failure}.
     *
     * @param mapper
     *    The mapping function to apply to a value of a {@link Failure} instance.
     *
     * @return new {@link Try}
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null} and the current instance is a {@link Failure} one
     */
    public final Try<T> mapFailure(final Function<? super Throwable, ? extends Throwable> mapper) {
        if (!isSuccess()) {
            Assert.notNull(mapper, "mapper must be not null");
            return mapFailureTry(mapper);
        } else {
            return success(get());
        }
    }


    /**
     *    Whereas {@code map} with {@code mapper} argument only performs a mapping on a {@link Success} {@link Try},
     * and {@code mapFailure} performs a mapping on an {@link Failure} {@link Try}, {@code map} with two {@link Function}
     * mappers as arguments, allows you to provide mapping actions for both, and will give you the result based on what
     * type of {@link Try} this is. Without this, you would have to do something like:
     *
     * <pre>
     * Example:
     *
     *   t.map(...).mapFailure(...);
     * </pre>
     *
     *    If invoking given {@code mapperFailure} or {@code mapperSuccess} an {@link Exception} is thrown then returned
     * {@link Try} will {@link Failure}.
     *
     * @param mapperFailure
     *    {@link Function} with the failure mapping operation
     * @param mapperSuccess
     *    {@link Function} with the success mapping operation
     *
     * @return {@link Try}
     *
     * @throws IllegalArgumentException if {@code mapperFailure} is {@code null} and the current instance is a {@link Success} one
     *                                  or {@code mapperSuccess} is {@code null} and the current instance is a {@link Failure} one
     */
    @SuppressWarnings("unchecked")
    public final <U> Try<U> map(final Function<? super Throwable, ? extends Throwable> mapperFailure,
                                final Function<? super T, ? extends U> mapperSuccess) {
        if (isSuccess()) {
            Assert.notNull(mapperSuccess, "mapperSuccess must be not null");
            return mapTry(mapperSuccess);
        } else {
            Assert.notNull(mapperFailure, "mapperFailure must be not null");
            return (Try<U>) mapFailureTry(mapperFailure);
        }
    }


    /**
     *    If the current {@link Try} is a {@link Success} instance, returns the result of applying the given
     * {@link Try}-bearing mapping function to the value. Otherwise, does nothing if this is a {@link Failure}.
     * <p>
     * If given {@code mapper} invocation returns an {@link Exception} is thrown then returned {@link Try} will {@link Failure}.
     *
     * @param mapper
     *    The mapping {@link Function} to apply the value of a {@link Success} instance
     *
     * @return new {@link Try}
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null} and the current instance is a {@link Success} one
     */
    public final <U> Try<U> flatMap(final Function<? super T, ? extends Try<? extends U>> mapper) {
        if (isSuccess()) {
            Assert.notNull(mapper, "mapper must be not null");
            return flatmapTry(mapper);
        }
        return failure(getException());
    }


    /**
     * Merge given {@code t} with the current one, managing the following use cases:
     * <p>
     *   1. this = {@link Success}, t = {@link Success}  =>  return a {@link Success} instance applying {@code mapperSuccess}
     *   2. this = {@link Success}, t = {@link Failure}  =>  return the {@link Failure}
     *   3. this = {@link Failure}, t = {@link Success}  =>  return the {@link Failure}
     *   4. this = {@link Failure}, t = {@link Failure}  =>  return a {@link Failure} instance applying {@code mapperLeft}
     *
     * If provided {@code t} is {@code null}, the current instance will be returned.
     *
     * @param t
     *    New {@link Try} to merge with the current one
     * @param mapperFailure
     *    {@link BiFunction} used to map current {@link Try} and given {@code t}, both {@link Failure}
     * @param mapperSuccess
     *    {@link BiFunction} used to map current {@link Try} and given {@code t}, both {@link Success}
     *
     * @return {@link Try}
     *
     * @throws IllegalArgumentException if {@code mapperSuccess} is {@code null} and the current instance and {@code t} are {@link Success}
     *                                  or {@code mapperFailure} is {@code null} and the current instance and {@code t} are {@link Failure}
     */
    public final Try<T> ap(final Try<? extends T> t,
                           final BiFunction<? super Throwable, ? super Throwable, ? extends Throwable> mapperFailure,
                           final BiFunction<? super T, ? super T, ? extends T> mapperSuccess) {
        if (isNull(t)) {
            return this;
        }
        // This is a Success instance
        if (isSuccess()) {
            // Current and given t are Success, a new merged Success instance will be returned
            if (t.isSuccess()) {
                Assert.notNull(mapperSuccess, "mapperSuccess must be not null");
                return mapTry(
                        t,
                        mapperSuccess
                );
            }
            // This is Success but t is Failure
            else {
                return failure(t.getException());
            }
        } else {
            // Due to only this is Failure, returns this
            if (t.isSuccess()) {
                return failure(getException());
            }
            // Current and given t are Failure, a new merged Failure instance will be returned
            else {
                Assert.notNull(mapperFailure, "mapperFailure must be not null");
                return mapFailureTry(
                        t,
                        mapperFailure
                );
            }
        }
    }


    /**
     *    Applies {@code mapperSuccess} if current {@link Try} is a {@link Success} instance, {@code mapperFailure} if
     * it is an {@link Failure}, transforming internal values into another one. If {@code mapperSuccess} is initially
     * applied and throws an {@link Exception}, then {@code mapperFailure} is applied with this {@link Exception}.
     *
     * <pre>
     * Example:
     *
     *   Try<Integer> t = ...
     *   String s = t.fold(Throwable::getMessage, Object::toString);
     * </pre>
     *
     * @param mapperFailure
     *    The mapping {@link Function} to apply the value of a {@link Failure} instance
     * @param mapperSuccess
     *    The mapping {@link Function} to apply the value of a {@link Success} instance
     *
     * @return the result of applying the right {@link Function}
     *
     * @throws IllegalArgumentException if {@code mapperSuccess} is {@code null} and the current instance is a {@link Success} one
     *                                  or {@code mapperFailure} is {@code null} and the current instance is a {@link Failure} one
     */
    public final <U> U fold(final Function<? super Throwable, ? extends U> mapperFailure,
                            final Function<? super T, ? extends U> mapperSuccess) {
        if (isSuccess()) {
            Assert.notNull(mapperSuccess, "mapperSuccess must be not null");
            try {
                return mapperSuccess.apply(get());
            } catch (Throwable t) {
                return mapperFailure.apply(t);
            }
        } else {
            Assert.notNull(mapperFailure, "mapperFailure must be not null");
            return mapperFailure.apply(getException());
        }
    }


    /**
     * Performs the given {@code action} to the stored value if the current {@link Try} is a {@link Success} one.
     *
     * @param action
     *    {@link Consumer} invoked for the stored value of the current {@link Success} instance.
     *
     * @return {@link Try}
     */
    public final Try<T> peek(final Consumer<? super T> action) {
        if (isSuccess() && nonNull(action)) {
            action.accept(get());
        }
        return this;
    }


    /**
     * Performs the given {@code action} to the stored value if the current {@link Try} is a {@link Failure} one.
     *
     * @param action
     *    {@link Consumer} invoked for the stored value of the current {@link Failure} instance.
     *
     * @return {@link Try}
     */
    public final Try<T> peekFailure(final Consumer<? super Throwable> action) {
        if (!isSuccess() && nonNull(action)) {
            action.accept(getException());
        }
        return this;
    }


    /**
     *    Performs the given {@code actionSuccess} to the stored value if the current {@link Try} is a {@link Success}
     * one. If the current instance is a {@link Failure}, performs {@code actionFailure}.
     *
     * @param actionFailure
     *    The {@link Failure} {@link Consumer} operation
     * @param actionSuccess
     *    The {@link Success} {@link Consumer} operation
     *
     * @return {@link Try}
     */
    public final Try<T> peek(final Consumer<? super Throwable> actionFailure,
                             final Consumer<? super T> actionSuccess) {
        if (isSuccess() && nonNull(actionSuccess)) {
            actionSuccess.accept(get());
        }
        if (!isSuccess() && nonNull(actionFailure)) {
            actionFailure.accept(getException());
        }
        return this;
    }


    /**
     *    Returns the stored value if the underline instance is {@link Success}, otherwise returns {@code other}. This
     * will throw an {@link Exception} if it is not a {@link Success} and {@code other} throws an {@link Exception}.
     *
     * @param other
     *    Returned value if current instance is an {@link Failure} one
     *
     * @return {@code T} value stored in {@link Success} instance, {@code other} otherwise
     */
    public final T getOrElse(final T other) {
        if (isSuccess()) {
            return get();
        }
        return other;
    }


    /**
     * Returns this {@link Try} if it is {@link Success}, otherwise return the alternative.
     *
     * @param other
     *    An alternative {@link Try}
     *
     * @return {@link Try}
     */
    @SuppressWarnings("unchecked")
    public final Try<T> orElse(final Try<? extends T> other) {
        return isSuccess()
                ? this
                : (Try<T>) other;
    }


    /**
     * Returns this {@link Try} if it is {@link Success}, otherwise return the result of evaluating {@link Supplier}.
     *
     * @param supplier
     *    An alternative {@link Try} supplier
     *
     * @return {@link Try}
     *
     * @throws IllegalArgumentException if {@code supplier} is {@code null} and the current instance is a {@link Failure} one
     */
    public final Try<T> orElse(final Supplier<Try<? extends T>> supplier) {
        if (isSuccess()) {
            return this;
        }
        Assert.notNull(supplier, "supplier must be not null");
        return getWithSupplierTry(supplier);
    }


    /**
     *    Returns this {@link Try} if it is {@link Success}, otherwise tries to recover the {@link Throwable} of the
     * {@link Failure} applying {@code mapperFailure}.
     *
     * <pre>
     * Example:
     *
     *   Try.of(() -> 12 / 0).recover(t -> Integer.MAX_VALUE);
     * </pre>
     *
     * @param mapperFailure
     *    Recovery {@link Function} taking a {@link Throwable}
     *
     * @return {@link Try}
     *
     * @throws IllegalArgumentException if {@code mapperFailure} is {@code null} and the current instance is a {@link Failure} one
     */
    public final Try<T> recover(final Function<? super Throwable, ? extends T> mapperFailure) {
        if (isSuccess()) {
            return this;
        }
        Assert.notNull(mapperFailure, "mapperFailure must be not null");
        return recoverTry(mapperFailure);
    }


    /**
     *    Returns this {@link Try} if it is {@link Success}, otherwise tries to recover the {@link Throwable} of the
     * {@link Failure} applying {@code mapperFailure}.
     *
     * <pre>
     * Example:
     *
     *   Try.of(() -> 12 / 0).recoverWith(t -> Try.success(Integer.MAX_VALUE));
     * </pre>
     *
     * @param mapperFailure
     *    Recovery {@link Function} taking a {@link Throwable}
     *
     * @return {@link Try}
     *
     * @throws IllegalArgumentException if {@code mapperFailure} is {@code null} and the current instance is a {@link Failure} one
     */
    public final Try<T> recoverWith(final Function<? super Throwable, ? extends Try<? extends T>> mapperFailure) {
        if (isSuccess()) {
            return this;
        }
        Assert.notNull(mapperFailure, "mapperFailure must be not null");
        return recoverWithTry(mapperFailure);
    }


    /**
     * Verifies in the current instance has no value, that is:
     * <p>
     *    1. Is a {@link Failure} one.
     *    2. Is an empty {@link Success} instance.
     *
     * @return {@code true} is the current instance is empty, {@code false} otherwise
     */
    public final boolean isEmpty() {
        return !isSuccess() || isNull(get());
    }


    /**
     *    If the current {@link Try} is an instance of {@link Success} wraps the stored value into an {@link Optional} object.
     * Otherwise return {@link Optional#empty()}
     *
     * @return {@link Optional}
     */
    public final Optional<T> toOptional() {
        return isEmpty()
                ? empty()
                : Optional.of(get());
    }


    /**
     * Converts current {@link Try} to an {@link Either}.
     *
     * @return {@code Either.right(get())} if current {@link Try} is {@link Success}
     *         {@code Either.left(getFailure())} if it is {@link Failure}
     */
    public final Either<Throwable, T> toEither() {
        return isSuccess()
                ? Either.right(get())
                : Either.left(getException());
    }


    /**
     * Transforms current {@link Try} into a {@link Validation}.
     *
     * @return {@code Validation.valid(get())} if this is {@link Success},
     *         otherwise {@code Validation.invalid(getException())}.
     */
    public final Validation<Throwable, T> toValidation() {
        return isSuccess()
                ? Validation.valid(get())
                : Validation.invalid(
                        isNull(getException())
                                ? new ArrayList<>()
                                : asList(getException())
                  );
    }


    /**
     * when current {@link Try} is a {@link Success} instance, manages in a safe way the {@link Predicate} invocation.
     *
     * @param predicate
     *    {@link Predicate} to apply the stored value if the current instance is a {@link Success} one
     * @param throwableSupplier
     *    {@link Supplier} with the content of returned {@link Failure} if {@link Success} does not verify it
     *
     * @return {@link Try}
     */
    private Try<T> filterTry(final Predicate<? super T> predicate,
                             final Supplier<? extends Throwable> throwableSupplier) {
        try {
            if (isNull(predicate) || predicate.test(get())) {
                return this;
            } else {
                return failure(throwableSupplier.get());
            }
        } catch (Throwable t) {
            return failure(t);
        }
    }


    /**
     * When current {@link Try} is a {@link Success} instance, manages in a safe way the {@link Function} invocation.
     *
     * @param mapper
     *    {@link Function} to apply the stored value if the current instance is a {@link Success} one
     *
     * @return {@link Try}
     */
    private <U> Try<U> mapTry(final Function<? super T, ? extends U> mapper) {
        try {
            return success(
                    mapper.apply(
                            get()
                    )
            );
        } catch (Throwable t) {
            return failure(t);
        }
    }


    /**
     *    When current {@link Try} is a {@link Success} instance and given {@code t} too, manages in a safe way the
     * {@link BiFunction} invocation to map both values.
     *
     * @param t
     *    New {@link Try} to merge with the current one
     * @param mapper
     *    {@link BiFunction} to apply the stored value and the one related with {@code t}
     *
     * @return {@link Try}
     */
    private <U> Try<U> mapTry(final Try<? extends T> t,
                              final BiFunction<? super T, ? super T, ? extends U> mapper) {
        try {
            return success(
                    mapper.apply(
                            get(),
                            t.get()
                    )
            );
        } catch (Throwable thr) {
            return failure(thr);
        }
    }


    /**
     * When current {@link Try} is a {@link Failure} instance, manages in a safe way the {@link Function} invocation.
     *
     * @param mapper
     *    {@link Function} to apply the stored value if the current instance is a {@link Failure} one
     *
     * @return {@link Try}
     */
    private Try<T> mapFailureTry(final Function<? super Throwable, ? extends Throwable> mapper) {
        try {
            return failure(
                    mapper.apply(
                            getException()
                    )
            );
        } catch (Throwable t) {
            return failure(t);
        }
    }


    /**
     *    When current {@link Try} is a {@link Failure} instance and given {@code t} too, manages in a safe way the
     * {@link BiFunction} invocation to map both values.
     *
     * @param t
     *    New {@link Try} to merge with the current one
     * @param mapper
     *    {@link BiFunction} to apply the stored exception and the one related with {@code t}
     *
     * @return {@link Try}
     */
    private Try<T> mapFailureTry(final Try<? extends T> t,
                                 final BiFunction<? super Throwable, ? super Throwable, ? extends Throwable> mapper) {
        try {
            return failure(
                    mapper.apply(
                            getException(),
                            t.getException()
                    )
            );
        } catch (Throwable thr) {
            return failure(thr);
        }
    }


    /**
     * When current {@link Try} is a {@link Success} instance, manages in a safe way the {@link Function} invocation.
     *
     * @param mapper
     *    {@link Function} to apply the stored value if the current instance is a {@link Success} one
     *
     * @return {@link Try}
     */
    @SuppressWarnings("unchecked")
    private <U> Try<U> flatmapTry(final Function<? super T, ? extends Try<? extends U>> mapper) {
        try {
            return (Try<U>) mapper.apply(
                    get()
            );
        } catch (Throwable t) {
            return failure(t);
        }
    }


    /**
     * When current {@link Try} is a {@link Failure} instance, manages in a safe way the {@link Function} invocation.
     *
     * @param mapperFailure
     *    {@link Function} to apply the stored value if the current instance is a {@link Failure} one
     *
     * @return @return {@link Try}
     */
    private Try<T> recoverTry(final Function<? super Throwable, ? extends T> mapperFailure) {
        try {
            return success(
                    mapperFailure.apply(
                            getException()
                    )
            );
        } catch (Throwable t) {
            return failure(t);
        }
    }


    /**
     * When current {@link Try} is a {@link Failure} instance, manages in a safe way the {@link Function} invocation.
     *
     * @param mapperFailure
     *    {@link Function} to apply the stored value if the current instance is a {@link Failure} one
     *
     * @return @return {@link Try}
     */
    @SuppressWarnings("unchecked")
    private Try<T> recoverWithTry(final Function<? super Throwable, ? extends Try<? extends T>> mapperFailure) {
        try {
            return (Try<T>) mapperFailure.apply(
                            getException()
            );
        } catch (Throwable t) {
            return failure(t);
        }
    }


    /**
     * Manages in a safe way the {@link Supplier} invocation.
     *
     * @param supplier
     *    {@link Supplier} used to generate returned {@link Try}
     *
     * @return {@link Try}
     */
    @SuppressWarnings("unchecked")
    private Try<T> getWithSupplierTry(final Supplier<Try<? extends T>> supplier) {
        try {
            return (Try<T>) supplier.get();
        } catch (Throwable t) {
            return failure(t);
        }
    }

}
