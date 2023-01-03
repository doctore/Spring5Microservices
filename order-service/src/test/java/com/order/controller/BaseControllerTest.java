package com.order.controller;

import com.order.configuration.documentation.DocumentationConfiguration;
import com.order.configuration.security.SecurityManager;
import com.order.grpc.client.GrpcClientRunner;
import com.spring5microservices.common.dto.ErrorResponseDto;
import lombok.SneakyThrows;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;

import static com.order.TestUtil.fromJson;
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
