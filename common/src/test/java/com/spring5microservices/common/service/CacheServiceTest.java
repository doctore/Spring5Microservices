package com.spring5microservices.common.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class CacheServiceTest {

    @Mock
    private CacheManager mockCacheManager;

    private CacheService cacheService;


    @BeforeEach
    public void init() {
        cacheService = new CacheService(mockCacheManager);
    }


    static Stream<Arguments> putTestCases() {
        Cache mockCache = Mockito.mock(Cache.class);
        return Stream.of(
                //@formatter:off
                //            cacheName,         key,               value,             cacheManagerResult,    expectedResult
                Arguments.of( null,              null,              null,              null,                  false ),
                Arguments.of( null,              "ItDoesNotCare",   "ItDoesNotCare",   mockCache,             false ),
                Arguments.of( "NotFoundCache",   "ItDoesNotCare",   "ItDoesNotCare",   null,                  false ),
                Arguments.of( "FoundCache",      "ValidKey",        "ValidValue",      mockCache,             true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("putTestCases")
    @DisplayName("put: test cases")
    public void put_testCases(String cacheName, String key, String value, Cache cacheManagerResult, boolean expectedResult) {
        // When
        when(mockCacheManager.getCache(cacheName)).thenReturn(cacheManagerResult);
        if (null != cacheManagerResult)
            doNothing().when(cacheManagerResult).put(key, value);

        boolean operationResult = cacheService.put(cacheName, key, value);

        // Then
        assertEquals(expectedResult, operationResult);
    }


    static Stream<Arguments> getTestCases() {
        Cache mockCache = Mockito.mock(Cache.class);
        SimpleValueWrapper returnedValue = new SimpleValueWrapper("FoundValue");
        return Stream.of(
                //@formatter:off
                //            cacheName,         key,               cacheManagerResult,   cacheResult,     expectedResult
                Arguments.of( null,              null,              null,                 null,            empty() ),
                Arguments.of( null,              "ItDoesNotCare",   mockCache,            returnedValue,   empty() ),
                Arguments.of( "NotFoundCache",   "ItDoesNotCare",   null,                 null,            empty() ),
                Arguments.of( "FoundCache",      "NotFoundKey",     mockCache,            null,            empty() ),
                Arguments.of( "FoundCache",      "FoundKey",        mockCache,            returnedValue,   of(returnedValue.get()) )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("getTestCases")
    @DisplayName("get: test cases")
    public void get_testCases(String cacheName, String key, Cache cacheManagerResult, SimpleValueWrapper cacheResult, Optional<String> expectedResult) {
        // When
        when(mockCacheManager.getCache(cacheName)).thenReturn(cacheManagerResult);
        if (null != cacheManagerResult)
           when(cacheManagerResult.get(key)).thenReturn(cacheResult);

        Optional<String> operationResult = cacheService.get(cacheName, key);

        // Then
        assertEquals(expectedResult, operationResult);
    }


    static Stream<Arguments> containsTestCases() {
        Cache mockCache = Mockito.mock(Cache.class);
        SimpleValueWrapper returnedValue = new SimpleValueWrapper("FoundValue");
        return Stream.of(
                //@formatter:off
                //            cacheName,         key,               cacheManagerResult,   cacheResult,     expectedResult
                Arguments.of( null,              null,              null,                 null,            false ),
                Arguments.of( null,              "ItDoesNotCare",   mockCache,            returnedValue,   false ),
                Arguments.of( "NotFoundCache",   "ItDoesNotCare",   null,                 null,            false ),
                Arguments.of( "FoundCache",      "NotFoundKey",     mockCache,            null,            false ),
                Arguments.of( "FoundCache",      "FoundKey",        mockCache,            returnedValue,   true )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("containsTestCases")
    @DisplayName("contains: test cases")
    public void contains_testCases(String cacheName, String key, Cache cacheManagerResult, SimpleValueWrapper cacheResult, boolean expectedResult) {
        // When
        when(mockCacheManager.getCache(cacheName)).thenReturn(cacheManagerResult);
        if (null != cacheManagerResult)
            when(cacheManagerResult.get(key)).thenReturn(cacheResult);

        boolean operationResult = cacheService.contains(cacheName, key);

        // Then
        assertEquals(expectedResult, operationResult);
    }

}
