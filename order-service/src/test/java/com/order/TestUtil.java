package com.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.spring5microservices.common.exception.JsonException;
import com.spring5microservices.common.util.JsonUtil;
import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;

import java.util.Collection;

@UtilityClass
public class TestUtil {

    /**
     * Convert the incoming object into a JSON-formatted string.
     *
     * @param sourceObject
     *    Object to map into string
     *
     * @return {@link String} with JSON-formatted given object properties
     *
     * @throws JsonException
     */
    public static <T> String toJson(final T sourceObject) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        return JsonUtil.toJson(
                sourceObject,
                mapper
        ).orElse(null);
    }


    /**
     * Transform the given JSON-formatted into an instance of a given {@link Class}.
     *
     * @param sourceJson
     *    Json string to transform
     * @param clazz
     *    {@link Class} of the returned object
     *
     * @return an instance of given {@link Class}
     *
     * @throws JsonException
     */
    public static <T> T fromJson(final String sourceJson,
                                 final Class<T> clazz) {
        if (!StringUtils.hasText(sourceJson)) {
            return null;
        }
        return JsonUtil.fromJson(
                sourceJson,
                clazz
        )
        .orElse(null);
    }


    /**
     * Transform the given JSON-formatted array into a {@link Collection} of {@code clazzOfElements} instances.
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
     * @throws JsonException
     */
    public static <T> Collection<T> fromJsonCollection(final String sourceJson,
                                                       final Class<? extends T> clazzOfElements,
                                                       final Class<? extends Collection> clazzOfCollection) {
        if (!StringUtils.hasText(sourceJson)) {
            return null;
        }
        return JsonUtil.fromJsonCollection(
                sourceJson,
                clazzOfElements,
                clazzOfCollection
        );
    }

}
