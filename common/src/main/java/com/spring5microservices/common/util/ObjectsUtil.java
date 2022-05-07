package com.spring5microservices.common.util;

import lombok.experimental.UtilityClass;

import java.util.function.Function;

import static java.util.Optional.ofNullable;

@UtilityClass
public class ObjectsUtil {

    /**
     *    Using the provided {@link Function} {@code mapper}, transform/extract from the given {@code sourceInstance}
     * the related value. Otherwise returns {@code defaultValue}.
     *
     * @param sourceInstance
     *    Object used to transform/extract required information.
     * @param mapper
     *    A mapping {@link Function} to use required information from {@code sourceInstance}
     * @param defaultValue
     *    Returned value if applying {@code mapper} no value is obtained.
     *
     * @return {@code mapper} {@code apply} method if not {@code null} is returned,
     *         {@code defaultValue} otherwise.
     */
    public static <T, E> E getOrElse(final T sourceInstance,
                                     final Function<? super T, ? extends E> mapper,
                                     final E defaultValue) {
        return ofNullable(sourceInstance)
                .map(si ->
                        null == mapper
                                ? defaultValue
                                : mapper.apply(sourceInstance))
                .orElseGet(() -> defaultValue);
    }


    /**
     *    Using the provided {@link Function} {@code mapper}, transform/extract from the given {@code sourceInstance}
     * the related value. Otherwise returns {@code null} as {@link String}.
     *
     * @param sourceInstance
     *    Object used to transform/extract required information.
     * @param mapper
     *    A mapping {@link Function} to use required information from {@code sourceInstance}
     *
     * @return {@code mapper} {@code apply} method if not {@code null} is returned,
     *         {@code null} as {@link String} otherwise.
     */
    public static <T, E> String getOrElseString(final T sourceInstance,
                                                final Function<? super T, ? extends E> mapper) {
        return getOrElseString(sourceInstance, mapper, "null");
    }


    /**
     *    Using the provided {@link Function} {@code mapper}, transform/extract from the given {@code sourceInstance}
     * the related value. Otherwise returns {@code defaultValue}.
     *
     * @param sourceInstance
     *    Object used to transform/extract required information.
     * @param mapper
     *    A mapping {@link Function} to use required information from {@code sourceInstance}
     * @param defaultValue
     *    Returned value if applying {@code mapper} no value is obtained.
     *
     * @return {@code mapper} {@code apply} method if not {@code null} is returned,
     *         {@code defaultValue} otherwise.
     */
    public static <T, E> String getOrElseString(final T sourceInstance,
                                                final Function<? super T, ? extends E> mapper,
                                                final String defaultValue) {
        return ofNullable(sourceInstance)
                .map(si ->
                        null == mapper
                                ? defaultValue
                                : mapper.apply(sourceInstance))
                .map(Object::toString)
                .orElseGet(() -> defaultValue);
    }

}
