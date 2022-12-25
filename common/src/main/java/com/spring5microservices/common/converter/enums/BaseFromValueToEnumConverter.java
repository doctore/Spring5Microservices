package com.spring5microservices.common.converter.enums;

/**
 * Parent interface of the all converters that allow ONLY from value to {@link Enum} conversion.
 *
 * @param <E>
 *    Type of the {@link Enum} to manage
 * @param <V>
 *    Type of the "equivalent" {@code value} to manage
 */
public interface BaseFromValueToEnumConverter<E extends Enum<?>, V> extends BaseEnumConverter<E, V> {

    String errorMessage = "Operation not allowed in a BaseFromValueToEnumConverter converter";


    @Override
    default V fromEnumToValue(final E enumValue) {
        throw new UnsupportedOperationException(errorMessage);
    }

}
