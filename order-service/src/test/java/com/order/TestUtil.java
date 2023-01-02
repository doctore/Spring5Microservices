package com.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@UtilityClass
public class TestUtil {

    /**
     * Convert the incoming object into a JSON-formatted string.
     *
     * @param object
     *    Object to map into string
     *
     * @return {@link String} with JSON-formatted given object properties
     *
     * @throws JsonProcessingException
     */
    public static <T> String toJson(T object) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(object);
    }


    /**
     * Transform the given JSON-formatted into an instance of a given {@link Class}.
     *
     * @param json
     *    Json string to transform
     * @param clazz
     *    {@link Class} of the returned object
     *
     * @return an instance of given {@link Class}
     *
     * @throws IOException
     */
    public static <T> T fromJson(String json, Class<T> clazz) throws IOException {
        if (!StringUtils.hasText(json)) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, clazz);
    }


    /**
     * Transform the given JSON-formatted array into a {@link List} of {@link Class} instances.
     *
     * @param json
     *    Json string to transform
     * @param clazz
     *    {@link Class} of the returned object
     *
     * @return {@link List} of {@link Class} instances
     *
     * @throws IOException
     */
    public static <T> List<T> fromJsonList(String json, Class<T> clazz) throws IOException {
        if (!StringUtils.hasText(json)) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        JavaType type = mapper.getTypeFactory().
                constructCollectionType(List.class, clazz);
        return mapper.readValue(json, type);
    }


    /**
     * Transform the given JSON-formatted array into a {@link Set} of {@link Class} instances.
     *
     * @param json
     *    Json string to transform
     * @param clazz
     *    {@link Class} of the returned object
     *
     * @return {@link Set} of {@link Class} instances
     *
     * @throws IOException
     */
    public static <T> Set<T> fromJsonSet(String json, Class<T> clazz) throws IOException {
        if (!StringUtils.hasText(json)) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        JavaType type = mapper.getTypeFactory().
                constructCollectionType(Set.class, clazz);
        return mapper.readValue(json, type);
    }

}
