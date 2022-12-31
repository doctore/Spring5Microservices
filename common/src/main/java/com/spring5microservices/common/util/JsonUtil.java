package com.spring5microservices.common.util;

import com.spring5microservices.common.exception.JsonException;
import lombok.experimental.UtilityClass;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

import static com.spring5microservices.common.util.ObjectsUtil.getOrElse;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;

@UtilityClass
public class JsonUtil {

    private static final ObjectMapper DEFAULT_OBJECT_MAPPER;
    static {
        DEFAULT_OBJECT_MAPPER = new ObjectMapper();
        DEFAULT_OBJECT_MAPPER.configure(
                SerializationFeature.WRAP_ROOT_VALUE,
                false
        );
    }


    /**
     * Transforms the given JSON-formatted {@link String} into an instance of a given {@link Class}.
     *
     * @param sourceJson
     *    JSON-formatted {@link String} to transform
     * @param clazzToConvert
     *    {@link Class} of the returned object
     *
     * @return an instance of given {@link Class}
     *
     * @throws JsonException if there was a problem trying to generate the result object
     */
    public static <T> Optional<T> fromJson(final String sourceJson,
                                           final Class<T> clazzToConvert) {
        return fromJson(
                sourceJson,
                clazzToConvert,
                DEFAULT_OBJECT_MAPPER
        );
    }


    /**
     * Transforms the given JSON-formatted {@link String} into an instance of a given {@link Class}.
     *
     * @param sourceJson
     *    JSON-formatted {@link String} to transform
     * @param clazzToConvert
     *    {@link Class} of the returned object
     * @param objectMapper
     *    {@link ObjectMapper} used to convert given {@code sourceObject}
     *
     * @return an instance of given {@link Class}
     *
     * @throws JsonException if there was a problem trying to generate the result object
     */
    public static <T> Optional<T> fromJson(final String sourceJson,
                                           final Class<T> clazzToConvert,
                                           final ObjectMapper objectMapper) {
        return ofNullable(sourceJson)
                .filter(StringUtils::isNotBlank)
                .map(json -> {
                    try {
                        final ObjectMapper finalObjectMapper = getOrElse(
                                objectMapper,
                                DEFAULT_OBJECT_MAPPER
                        );
                        return finalObjectMapper.readValue(
                                json,
                                clazzToConvert
                        );
                    } catch (Exception e) {
                        throw new JsonException(
                                format("There was an error trying to convert the given JSON-formatted string: %s into an instance of the class: %s",
                                        sourceJson,
                                        ofNullable(clazzToConvert).map(Class::getName).orElse("null")
                                ),
                                e
                        );
                    }
                });
    }


    /**
     * Converts the incoming object into a JSON-formatted {@link String}.
     *
     * @param sourceObject
     *    Object to convert to a JSON-formatted {@link String}
     *
     * @return {@link Optional} of {@link String} with JSON-formatted if the object could be converted,
     *         {@link Optional#empty()} otherwise
     *
     * @throws JsonException if there was a problem trying to generate the JSON-formatted {@link String}
     */
    public static <T> Optional<String> toJson(final T sourceObject) {
        return toJson(
                sourceObject,
                DEFAULT_OBJECT_MAPPER
        );
    }


    /**
     * Converts the incoming object into a JSON-formatted {@link String}.
     *
     * @param sourceObject
     *    Object to convert to a JSON-formatted {@link String}
     * @param objectMapper
     *    {@link ObjectMapper} used to convert given {@code sourceObject}
     *
     * @return {@link Optional} of {@link String} with JSON-formatted if the object could be converted,
     *         {@link Optional#empty()} otherwise
     *
     * @throws JsonException if there was a problem trying to generate the JSON-formatted {@link String}
     */
    public static <T> Optional<String> toJson(final T sourceObject,
                                              final ObjectMapper objectMapper) {
        return ofNullable(sourceObject)
                .map(o -> {
                    try {
                        final ObjectMapper finalObjectMapper = getOrElse(
                                objectMapper,
                                DEFAULT_OBJECT_MAPPER
                        );
                        return finalObjectMapper.writeValueAsString(o);

                    } catch (Exception e) {
                        throw new JsonException(
                                format("There was an error trying to convert an object of the class: %s into a JSON-formatted string",
                                        sourceObject.getClass().getName()),
                                e
                        );
                    }
                });
    }

}
