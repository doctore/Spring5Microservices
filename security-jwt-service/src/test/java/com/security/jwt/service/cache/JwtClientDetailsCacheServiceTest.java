package com.security.jwt.service.cache;

import com.security.jwt.configuration.cache.CacheConfiguration;
import com.security.jwt.model.JwtClientDetails;
import com.spring5microservices.common.service.CacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static com.security.jwt.ObjectGeneratorForTest.buildDefaultJwtClientDetails;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class JwtClientDetailsCacheServiceTest {

    @Mock
    private CacheConfiguration mockCacheConfiguration;

    @Mock
    private CacheService mockCacheService;

    private JwtClientDetailsCacheService jwtClientDetailsCacheService;

    @BeforeEach
    public void init() {
        jwtClientDetailsCacheService = new JwtClientDetailsCacheService(mockCacheConfiguration, mockCacheService);
        when(mockCacheConfiguration.getJwtConfigurationCacheName()).thenReturn("TestCache");
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
        boolean operationResult = jwtClientDetailsCacheService.contains(clientId);

        // Then
        assertEquals(expectedResult, operationResult);
    }


    static Stream<Arguments> getTestCases() {
        String clientId = "clientId";
        Optional<JwtClientDetails> cacheServiceResult = of(buildDefaultJwtClientDetails(clientId));
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
    public void get_testCases(String clientId, Optional<JwtClientDetails> cacheServiceResult, Optional<JwtClientDetails> expectedResult) {
        // When
        when(mockCacheService.get(anyString(), eq(clientId))).thenReturn((Optional)cacheServiceResult);
        Optional<JwtClientDetails> operationResult = jwtClientDetailsCacheService.get(clientId);

        // Then
        assertEquals(expectedResult, operationResult);
    }


    static Stream<Arguments> putTestCases() {
        String clientId = "clientId";
        JwtClientDetails jwtClientDetails = buildDefaultJwtClientDetails(clientId);
        return Stream.of(
                //@formatter:off
                //            clientId,   jwtClientDetails,   cacheServiceResult,   expectedResult
                Arguments.of( null,       null,               false,                false ),
                Arguments.of( clientId,   null,               false,                false ),
                Arguments.of( clientId,   null,               true,                 true ),
                Arguments.of( clientId,   jwtClientDetails,   true,                 true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("putTestCases")
    @DisplayName("put: test cases")
    public void put_testCases(String clientId, JwtClientDetails jwtClientDetails, boolean cacheServiceResult, boolean expectedResult) {
        // When
        when(mockCacheService.put(anyString(), eq(clientId), eq(jwtClientDetails))).thenReturn(cacheServiceResult);
        boolean operationResult = jwtClientDetailsCacheService.put(clientId, jwtClientDetails);

        // Then
        assertEquals(expectedResult, operationResult);
    }

}
