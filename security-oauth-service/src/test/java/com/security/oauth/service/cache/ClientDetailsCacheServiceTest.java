package com.security.oauth.service.cache;

import com.security.oauth.configuration.cache.CacheConfiguration;
import com.spring5microservices.common.service.CacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;

import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ClientDetailsCacheService.class)
public class ClientDetailsCacheServiceTest {

    @MockBean
    private CacheConfiguration mockCacheConfiguration;

    @MockBean
    private CacheService mockCacheService;

    @Autowired
    private ClientDetailsCacheService clientDetailsCacheService;

    @BeforeEach
    public void init() {
        when(mockCacheConfiguration.getOauthClientCacheName()).thenReturn("TestCache");
    }


    static Stream<Arguments> containsTestCases() {
        String clientId = "clientId";
        return Stream.of(
                //@formatter:off
                //            clientId,   cacheServiceResult,   expectedResult
                Arguments.of( null,       false,                false ),
                Arguments.of( clientId,   false,                false ),
                Arguments.of( clientId,   true,                 true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("containsTestCases")
    @DisplayName("contains: test cases")
    public void contains_testCases(String clientId, boolean cacheServiceResult, boolean expectedResult) {
        // When
        when(mockCacheService.contains(anyString(), eq(clientId))).thenReturn(cacheServiceResult);
        boolean operationResult = clientDetailsCacheService.contains(clientId);

        // Then
        assertEquals(expectedResult, operationResult);
    }


    static Stream<Arguments> getTestCases() {
        String clientId = "clientId";
        Optional<ClientDetails> cacheServiceResult = of(new BaseClientDetails());
        return Stream.of(
                //@formatter:off
                //            clientId,   cacheServiceResult,   expectedResult
                Arguments.of( null,       empty(),              empty() ),
                Arguments.of( clientId,   empty(),              empty() ),
                Arguments.of( clientId,   cacheServiceResult,   cacheServiceResult )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getTestCases")
    @DisplayName("get: test cases")
    public void get_testCases(String clientId, Optional<ClientDetails> cacheServiceResult, Optional<ClientDetails> expectedResult) {
        // When
        when(mockCacheService.get(anyString(), eq(clientId))).thenReturn((Optional)cacheServiceResult);
        Optional<ClientDetails> operationResult = clientDetailsCacheService.get(clientId);

        // Then
        assertEquals(expectedResult, operationResult);
    }


    static Stream<Arguments> putTestCases() {
        String clientId = "clientId";
        ClientDetails clientDetails = new BaseClientDetails();
        return Stream.of(
                //@formatter:off
                //            clientId,   clientDetails,   cacheServiceResult,   expectedResult
                Arguments.of( null,       null,            false,                false ),
                Arguments.of( clientId,   null,            false,                false ),
                Arguments.of( clientId,   null,            true,                 true ),
                Arguments.of( clientId,   clientDetails,   true,                 true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("putTestCases")
    @DisplayName("put: test cases")
    public void put_testCases(String clientId, ClientDetails clientDetails, boolean cacheServiceResult, boolean expectedResult) {
        // When
        when(mockCacheService.put(anyString(), eq(clientId), eq(clientDetails))).thenReturn(cacheServiceResult);
        boolean operationResult = clientDetailsCacheService.put(clientId, clientDetails);

        // Then
        assertEquals(expectedResult, operationResult);
    }

}
