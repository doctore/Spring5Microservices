package com.spring5microservices.common.util;

import org.springframework.util.Assert;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;

/**
 * Used to manage a lazy evaluated value, useful when getting it has an important performance cost.
 *
 *    Internally, use a cached value so when the provided {@link Supplier} is invoked its result will
 * be cached. There are 2 different methods to consider if we want to reuse or not the cached value:
 *
 *   1. {@code get} use cached value.
 *
 *   2. {@code getNoCached} does not use it and invokes again the provided {@link Supplier}.
 *
 * There are several use cases for using {@link Lazy}, but probably the main one is when we need:
 *
 *   Replace using a {@link Supplier} as method parameter because we do not just want to prevent automatic
 *   invocation, but even manage a cache to reuse the result in an easy way.
 *
 * Example:
 *
 *      <T> T exampleMethod(Supplier<? extends T> supplier)
 *
 *  Advantages:
 *
 *     1. We can manage when the {@link Supplier} is invoked internally, avoiding automatic invocation of a functionality
 *       with a maybe not good performance.
 *
 *  Disadvantages:
 *
 *     1. If we need to result of {@link Supplier} if different parts of the code, it should be managed in a manual way
 *        (for example, using a variable as cache)
 *
 *     2. If we used a variable because of point 1. how can we distinguish if {@link Supplier} was invoked or if it
 *        was but the result was {@code null} (probably the initial value used in our internal cache variable).
 *
 * {@link Lazy} solves the above problems keeping the described advantages of using {@link Supplier}.
 */
public final class Lazy<T> implements Supplier<T> {

    private final Supplier<? extends T> supplier;
    private T cachedValue;


    /**
     * Construct a {@code Lazy}
     *
     * @param supplier
     *    {@link Supplier} used to get the value in a lazy way
     */
    private Lazy(Supplier<? extends T> supplier) {
        this.supplier = supplier;
    }


    /**
     * Creates a {@code Lazy} that requests its value from a given {@code Supplier}.
     *
     * @param supplier
     *    {@link Supplier} used to get the value in a lazy way
     *
     * @return {@code Lazy}
     *
     * @throws IllegalArgumentException if {@code supplier} is {@code null}
     */
    @SuppressWarnings("unchecked")
    public static <T> Lazy<T> of(final Supplier<? extends T> supplier) {
        Assert.notNull(supplier, "supplier must be not null");
        if (supplier instanceof Lazy) {
            return (Lazy<T>) supplier;
        } else {
            return new Lazy<>(supplier);
        }
    }


    /**
     *    Returns the internal value using the provided {@link Supplier}. If this one was previously invoked, the cached
     * value will be returned.
     *
     * @return cached value if provided {@link Supplier} has been invoked previously. {@link Supplier#get()} otherwise.
     */
    @Override
    public T get() {
        return computeValue(true);
    }


    /**
     * Invokes the provided {@link Supplier} to get the related value.
     *
     * @return result of invoking provided {@link Supplier#get()}
     */
    public T getNoCached() {
        return computeValue(false);
    }


    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Lazy)) {
            return false;
        } else {
            final Lazy<?> that = (Lazy<?>) o;

            // Equals does not invoke internal supplier
            if (!isEvaluated() || !that.isEvaluated()) {
                return false;
            }
            return Objects.equals(get(), that.get());
        }
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(supplier);
    }


    /**
     *    Using the provided {@link Predicate} return the lazy internal value in an {@link Optional} if satisfies
     * {@code predicate} or given {@link Predicate} is {@code null}. {@link Optional#empty()} otherwise.
     *
     * @param predicate
     *    {@link Predicate} to filter the lazy internal value
     *
     * @return {@link Optional} with internal value: cached or the result of provided {@link Supplier} if satisfies
     *         {@code predicate} or given {@link Predicate} is {@code null}. {@link Optional#empty()} otherwise.
     */
    public Optional<T> filter(final Predicate<? super T> predicate) {
        final T v = get();
        return Objects.isNull(predicate) || predicate.test(v)
                ? Optional.of(v)
                : empty();
    }


    /**
     * Check if current lazy value has been evaluated.
     *
     * @return {@code true} if the provided supplier value was evaluated, {@code false} otherwise.
     */
    public boolean isEvaluated() {
        return Objects.nonNull(cachedValue);
    }


    /**
     *    Transform the cached value (or the one returned by provided {@link Supplier}) in a new one using the provided
     * {@code mapper}.
     *
     * @param mapper
     *    {@link Function} used to convert internal value
     *
     * @return {@link Lazy}
     *
     * @throws IllegalArgumentException if {@code mapper} is {@code null}
     */
    public <U> Lazy<U> map(final Function<? super T, ? extends U> mapper) {
        Assert.notNull(mapper, "mapper must be not null");
        return Lazy.of(() -> mapper.apply(get()));
    }


    /**
     * Performs the given {@code action} to the cached value (or the one returned by provided {@link Supplier})
     *
     * @param action
     *    {@link Consumer} invoked for the internal value of the current {@link Lazy} instance.
     *
     * @return {@code Lazy}
     */
    public Lazy<T> peek(final Consumer<? super T> action) {
        if (Objects.nonNull(action)) {
            action.accept(get());
        }
        return this;
    }


    /**
     * Wrap the result of provided {@link Supplier} into an {@link Optional}.
     *
     * @return {@link Optional}
     */
    public Optional<T> toOptional() {
        return ofNullable(get());
    }


    /**
     *    Return the cached value if not {@code null} and {@code useCachedValue} is {@code true}. Otherwise invokes
     * provided {@link Supplier}.
     *
     * @param useCachedValue
     *    If {@code true} tries to reuse internal cached value.
     *
     * @return internal value
     */
    private T computeValue(boolean useCachedValue) {
        if (useCachedValue) {
            cachedValue = Objects.isNull(cachedValue)
                    ? supplier.get()
                    : cachedValue;
        }
        else {
            cachedValue = supplier.get();
        }
        return cachedValue;
    }

}
