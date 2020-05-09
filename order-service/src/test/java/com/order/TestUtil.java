package com.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;

import java.io.IOException;

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
        if (StringUtils.isEmpty(json))
            return null;

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, clazz);
    }

}
