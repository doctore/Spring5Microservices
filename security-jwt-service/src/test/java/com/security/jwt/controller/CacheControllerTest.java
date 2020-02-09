package com.security.jwt.controller;

import com.security.jwt.configuration.rest.RestRoutes;
import com.security.jwt.service.JwtClientDetailsService;
import com.security.jwt.service.cache.JwtClientDetailsCacheService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = CacheController.class)
public class CacheControllerTest {

    @MockBean
    private JwtClientDetailsCacheService mockJwtClientDetailsCacheService;

    @MockBean
    private PasswordEncoder mockPasswordEncoder;

    @MockBean
    private JwtClientDetailsService mockJwtClientDetailsService;

    @Autowired
    private MockMvc mockMvc;


    @Test
    @SneakyThrows
    @DisplayName("clear: when no basic authentication is provided then unauthorized code is returned")
    public void clear_whenNoBasicAuthIsProvided_thenUnauthorizedHttpCodeIsReturned() {
        mockMvc.perform(put(RestRoutes.CACHE.ROOT + RestRoutes.CACHE.CLEAR))
                .andExpect(status().isUnauthorized());

        verifyZeroInteractions(mockJwtClientDetailsCacheService);
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

        mockMvc.perform(put(RestRoutes.CACHE.ROOT + RestRoutes.CACHE.CLEAR))
                .andExpect(status().is(expectedResultHttpCode.value()));

        verify(mockJwtClientDetailsCacheService, times(1)).clear();
    }

}