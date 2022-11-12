package com.spring5microservices.common.util;

import com.spring5microservices.common.exception.JsonException;
import lombok.experimental.UtilityClass;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@UtilityClass
public class JsonUtil {

    private static final ObjectMapper mapper;
    static {
        mapper = new ObjectMapper();
        mapper.configure(
                SerializationFeature.WRAP_ROOT_VALUE,
                false
        );
    }


    /**
     * Converts the incoming object into a JSON-formatted string.
     *
     * @param object
     *    Object to map into string
     *
     * @return {@link Optional} of {@link String} with JSON-formatted if the object could be converted,
     *         {@link Optional#empty()} otherwise
     *
     * @throws JsonException if there was a problem trying to generate the JSON-formatted string
     */
    public static <T> Optional<String> toJson(final T object) {
        return ofNullable(object)
                .map(o -> {
                    try {
                        return mapper.writeValueAsString(o);
                    } catch (JsonProcessingException e) {
                        throw new JsonException(e);
                    }
                });
    }


    /**
     * Transforms the given JSON-formatted into an instance of a given {@link Class}.
     *
     * @param json
     *    Json string to transform
     * @param clazz
     *    {@link Class} of the returned object
     *
     * @return an instance of given {@link Class}
     *
     * @throws JsonException if there was a problem trying to generate the result object
     */
    public static <T> Optional<T> fromJson(final String json,
                                           final Class<T> clazz) {
        return ofNullable(json)
                .filter(j -> !j.trim().isEmpty())
                .map(j -> {
                    try {
                        return mapper.readValue(
                                json,
                                clazz
                        );
                    } catch (IOException e) {
                        throw new JsonException(e);
                    }
                });
    }

}
