package com.security.jwt.controller;

import com.security.jwt.SecurityJwtServiceApplication;
import com.security.jwt.configuration.rest.RestRoutes;
import com.security.jwt.service.cache.JwtClientDetailsCacheService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.stream.Stream;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(classes = SecurityJwtServiceApplication.class)
public class CacheControllerTest {

    @Autowired
    ApplicationContext context;

    @MockBean
    private JwtClientDetailsCacheService mockJwtClientDetailsCacheService;

    private WebTestClient webTestClient;

    @BeforeEach
    public void init() {
        this.webTestClient = WebTestClient.bindToApplicationContext(this.context).configureClient().build();
    }


    @Test
    @SneakyThrows
    @DisplayName("clear: when no basic authentication is provided then unauthorized code is returned")
    public void clear_whenNoBasicAuthIsProvided_thenUnauthorizedHttpCodeIsReturned() {
        webTestClient.put()
                .uri(RestRoutes.CACHE.ROOT + RestRoutes.CACHE.CLEAR)
                .exchange()
                .expectStatus().isUnauthorized();

        verifyNoInteractions(mockJwtClientDetailsCacheService);
    }


    static Stream<Arguments> clear_validTestCases() {
        return Stream.of(
                //@formatter:off
                //            JwtClientDetailsCacheServiceResult,   expectedResultHttpCode
                Arguments.of( false,                                NOT_FOUND ),
                Arguments.of( true,                                 OK )
        ); //@formatter:on
    }

    @ParameterizedTest
    @SneakyThrows
    @MethodSource("clear_validTestCases")
    @DisplayName("clear: when given basic authentication is given then the suitable Http code is returned")
    @WithMockUser
    public void clear_whenGivenBasicAuthIsGiven_thenSuitableHttpCodeIsReturned(boolean cacheServiceResult,
                                                                               HttpStatus expectedResultHttpCode) {
        when(mockJwtClientDetailsCacheService.clear()).thenReturn(cacheServiceResult);

        webTestClient.put()
                .uri(RestRoutes.CACHE.ROOT + RestRoutes.CACHE.CLEAR)
                .exchange()
                .expectStatus().isEqualTo(expectedResultHttpCode);

        verify(mockJwtClientDetailsCacheService, times(1)).clear();
    }

}
