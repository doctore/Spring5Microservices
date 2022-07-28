package com.spring5microservices.common.util;

import lombok.experimental.UtilityClass;

import java.util.function.Function;

import static java.util.Optional.ofNullable;

@UtilityClass
public class ObjectsUtil {

    /**
     * Return the given {@code sourceInstance} if is not {@code null}. Otherwise returns {@code defaultValue}.
     *
     * @param sourceInstance
     *    Object returned only if is not {@code null}
     * @param defaultValue
     *    Returned value if {@code sourceInstance} is {@code null}
     *
     * @return {@code sourceInstance} if is not {@code null}, {@code defaultValue} otherwise
     */
    public static <T> T getOrElse(final T sourceInstance,
                                  final T defaultValue) {
        return ofNullable(sourceInstance)
                .orElse(defaultValue);
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
    public static <T, E> E getOrElse(final T sourceInstance,
                                     final Function<? super T, ? extends E> mapper,
                                     final E defaultValue) {
        return ofNullable(sourceInstance)
                .map(si ->
                        null == mapper
                                ? defaultValue
                                : mapper.apply(sourceInstance))
                .orElse(defaultValue);
    }


    /**
     *    Return the {@link String} representation of the given {@code sourceInstance} if is not {@code null}.
     * Otherwise returns {@code defaultValue}.
     *
     * @param sourceInstance
     *    Object returned only if is not {@code null}
     * @param defaultValue
     *    Returned value if {@code sourceInstance} is {@code null}
     *
     * @return {@link String} representation of {@code sourceInstance} if is not {@code null},
     *         {@code defaultValue} otherwise
     */
    public static <T> String getOrElse(final T sourceInstance,
                                       final String defaultValue) {
        return ofNullable(sourceInstance)
                .map(Object::toString)
                .orElse(defaultValue);
    }


    /**
     *    Using the provided {@link Function} {@code mapper}, transform/extract from the given {@code sourceInstance}
     * the related value, returning its {@link String} representation. Otherwise returns {@code defaultValue}.
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
    public static <T, E> String getOrElse(final T sourceInstance,
                                          final Function<? super T, ? extends E> mapper,
                                          final String defaultValue) {
        return ofNullable(sourceInstance)
                .map(si ->
                        null == mapper
                                ? defaultValue
                                : mapper.apply(sourceInstance))
                .map(Object::toString)
                .orElse(defaultValue);
    }

}
