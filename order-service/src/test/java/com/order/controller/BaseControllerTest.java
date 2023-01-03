package com.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.order.configuration.documentation.DocumentationConfiguration;
import com.order.configuration.security.SecurityManager;
import com.order.grpc.client.GrpcClientRunner;
import com.spring5microservices.common.dto.ErrorResponseDto;
import com.spring5microservices.common.exception.JsonException;
import com.spring5microservices.common.util.JsonUtil;
import lombok.SneakyThrows;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.StringUtils;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class BaseControllerTest {

    // To avoid an error when creating the bean
    @MockBean
    private SecurityManager mockSecurityManager;

    // To avoid an error when creating the bean
    @MockBean
    private DocumentationConfiguration documentationConfiguration;

    // To avoid gRPC client initialization
    @MockBean
    private GrpcClientRunner grpcClientRunner;


    /**
     * Convert the incoming object into a JSON-formatted string.
     *
     * @param sourceObject
     *    Object to map into string
     *
     * @return {@link String} with JSON-formatted given object properties
     *
     * @throws JsonException if there was an error creating the JSON representation of given {@code sourceObject}
     */
    protected <T> String toJson(final T sourceObject) {
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
     * @throws JsonException if there was an error creating the new instance using the given {@code sourceJson}
     */
    protected <T> T fromJson(final String sourceJson,
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
     * @throws JsonException if there was an error creating the collection using the given {@code sourceJson}
     */
    protected <T> Collection<T> fromJsonCollection(final String sourceJson,
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


    /**
     * Checks the expected result in Controller layer related tests when the endpoint should return an error.
     *
     * @param webResult
     *    {@link ResultActions} with the result of {@link MockMvc#perform(RequestBuilder)}
     * @param expectedHttpCode
     *    {@link HttpStatus} with expected returned Http code
     * @param errorResponse
     *    {@link ErrorResponseDto} with information about the problem
     */
    @SneakyThrows
    protected void thenHttpErrorIsReturned(final ResultActions webResult,
                                           final HttpStatus expectedHttpCode,
                                           final ErrorResponseDto errorResponse) {
        webResult.andExpect(
                status().is(expectedHttpCode.value())
        );
        assertEquals(
                errorResponse,
                fromJson(
                        webResult.andReturn().getResponse().getContentAsString(),
                        ErrorResponseDto.class
                )
        );
    }

}
