package com.pizza.util.converter.enums;

/**
 * Parent interface of the all converters from Enum to "equivalent" {@code value} and vice versa.
 *
 * @param <E>
 *    Type of the {@link Enum} to manage
 * @param <V>
 *    Type of the "equivalent" {@code value} to manage
 */
public interface BaseEnumConverter<E extends Enum, V> {

    /**
     * Create a new {@link Enum} using the given {@code value}.
     *
     * @param value
     *    Value used to get the equivalent {@link Enum}
     *
     * @return equivalent {@link Enum}
     */
    E fromValueToEnum(final V value);

    /**
     * Create a new "equivalent" {@code value} using the given {@link Enum}.
     *
     * @param enumValue
     *    {@link Enum} that matches with returned {@code value}
     *
     * @return equivalent {@code value}
     */
    V fromEnumToValue(final E enumValue);

}
