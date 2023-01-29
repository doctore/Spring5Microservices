package com.spring5microservices.common.util;

import com.fasterxml.jackson.databind.JavaType;
import com.spring5microservices.common.exception.JsonException;
import lombok.experimental.UtilityClass;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
                                           final Class<? extends T> clazzToConvert) {
        return fromJson(
                sourceJson,
                clazzToConvert,
                DEFAULT_OBJECT_MAPPER
        );
    }


    /**
     *    Transforms the given JSON-formatted {@link String} into an instance of a given {@link Class}, using provided
     * {@link ObjectMapper}.
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
                                           final Class<? extends T> clazzToConvert,
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
     *    Transforms the given JSON-formatted {@link String} that contains an array of elements into a {@link List}
     * of instances of a given {@code clazzOfElements}.
     *
     * @param sourceJson
     *    JSON-formatted {@link String} to transform
     * @param clazzOfElements
     *    {@link Class} of the elements included in the returned {@link List}
     *
     * @return {@link List} of {@code clazzOfElements} instances
     *
     * @throws JsonException if there was a problem trying to generate the result object or
     *                       given {@code sourceJson} has no text and there was an error creating an empty {@link List}
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> fromJsonCollection(final String sourceJson,
                                                 final Class<? extends T> clazzOfElements) {
        return (List<T>) fromJsonCollection(
                sourceJson,
                clazzOfElements,
                ArrayList.class,
                DEFAULT_OBJECT_MAPPER
        );
    }


    /**
     *    Transforms the given JSON-formatted {@link String} that contains an array of elements into a {@link Collection}
     * of instances of a given {@code clazzOfElements}.
     *
     * @param sourceJson
     *    JSON-formatted {@link String} to transform
     * @param clazzOfElements
     *    {@link Class} of the elements included in the returned {@link Collection}
     * @param clazzOfCollection
     *    {@link Class} of the returned {@link Collection}. Provided {@link Class} must have a valid empty constructor.
     *
     * @return {@link Collection} of {@code clazzOfElements} instances
     *
     * @throws JsonException if there was a problem trying to generate the result object or
     *                       given {@code sourceJson} has no text and there was an error creating an empty {@link Collection}
     */
    public static <T> Collection<T> fromJsonCollection(final String sourceJson,
                                                       final Class<? extends T> clazzOfElements,
                                                       final Class<? extends Collection> clazzOfCollection) {
        return fromJsonCollection(
                sourceJson,
                clazzOfElements,
                clazzOfCollection,
                DEFAULT_OBJECT_MAPPER
        );
    }


    /**
     *    Transforms the given JSON-formatted {@link String} that contains an array of elements into a {@link Collection}
     * of instances of a given {@code clazzOfElements}, using provided {@link ObjectMapper}.
     *
     * @param sourceJson
     *    JSON-formatted {@link String} to transform
     * @param clazzOfElements
     *    {@link Class} of the elements included in the returned {@link Collection}
     * @param clazzOfCollection
     *    {@link Class} of the returned {@link Collection}. Provided {@link Class} must have a valid empty constructor.
     * @param objectMapper
     *    {@link ObjectMapper} used to convert given {@code sourceObject}
      *
     * @return {@link Collection} of {@code clazzOfElements} instances
     *
     * @throws JsonException if there was a problem trying to generate the result object or
     *                       given {@code sourceJson} has no text and there was an error creating an empty {@link Collection}
     */
    @SuppressWarnings("unchecked")
    public static <T> Collection<T> fromJsonCollection(final String sourceJson,
                                                       final Class<? extends T> clazzOfElements,
                                                       final Class<? extends Collection> clazzOfCollection,
                                                       final ObjectMapper objectMapper) {
        final Class<? extends Collection> finalClazzOfCollection = getOrElse(
                clazzOfCollection,
                ArrayList.class
        );
        return (Collection<T>) ofNullable(sourceJson)
                .filter(StringUtils::isNotBlank)
                .map(json -> {
                    try {
                        final ObjectMapper finalObjectMapper = getOrElse(
                                objectMapper,
                                DEFAULT_OBJECT_MAPPER
                        );
                        JavaType type = finalObjectMapper
                                .getTypeFactory()
                                .constructCollectionType(
                                        finalClazzOfCollection,
                                        clazzOfElements
                                );
                        return finalObjectMapper.readValue(
                                json,
                                type
                        );
                    } catch (Exception e) {
                        throw new JsonException(
                                format("There was an error trying to convert the given JSON-formatted string: %s into a "
                                     + "collection class: %s filled with instances of the class: %s",
                                        sourceJson,
                                        ofNullable(finalClazzOfCollection).map(Class::getName).orElse("null"),
                                        ofNullable(clazzOfElements).map(Class::getName).orElse("null")
                                ),
                                e
                        );
                    }
                })
                .orElseGet(() -> {
                    try {
                        return finalClazzOfCollection.getDeclaredConstructor().newInstance();

                    } catch (Exception e) {
                        throw new JsonException(
                                format("Due to the given JSON-formatted string: %s is null or empty, it was required to create "
                                     + "an empty instance of the class: %s. However, there was an error trying to do it",
                                        sourceJson,
                                        ofNullable(finalClazzOfCollection).map(Class::getName).orElse("null")
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
