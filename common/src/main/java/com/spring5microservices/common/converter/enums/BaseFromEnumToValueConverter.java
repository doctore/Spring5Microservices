package com.spring5microservices.common.converter.enums;

/**
 * Parent interface of the all converters that allow ONLY from {@link Enum} to value conversion.
 *
 * @param <E>
 *    Type of the {@link Enum} to manage
 * @param <V>
 *    Type of the "equivalent" {@code value} to manage
 */
public interface BaseFromEnumToValueConverter<E extends Enum<?>, V> extends BaseEnumConverter<E, V> {

    String errorMessage = "Operation not allowed in a BaseFromEnumToValueConverter converter";


    @Override
    default E fromValueToEnum(final V value) {
        throw new UnsupportedOperationException(errorMessage);
    }

}
