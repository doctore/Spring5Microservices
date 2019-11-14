package com.pizza.service.cache;

import com.pizza.configuration.cache.CacheConfiguration;
import com.spring5microservices.common.service.CacheService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class UserBlacklistCacheServiceTest {

    @Mock
    private CacheConfiguration mockCacheConfiguration;

    @Mock
    private CacheService mockCacheService;

    private UserBlacklistCacheService userBlacklistCacheService;

    @Before
    public void init() {
        userBlacklistCacheService = new UserBlacklistCacheService(mockCacheConfiguration, mockCacheService);
        when(mockCacheConfiguration.getUserBlacklistCacheName()).thenReturn("TestCache");
    }


    private List<Object[]> containsTestCases() {
        String username = "username";
        return asList(
                //             username,   cacheServiceResult,   expectedResult
                new Object[] { null,       false,                false },
                new Object[] { username,   false,                false },
                new Object[] { username,   true,                 true });
    }

    @Test
    public void contains_testCases() {
        for (Object[] parameters: containsTestCases()) {
            when(mockCacheService.contains(anyString(), eq((String)parameters[0]))).thenReturn((boolean)parameters[1]);
            boolean operationResult = userBlacklistCacheService.contains((String)parameters[0]);
            assertEquals((boolean)parameters[2], operationResult);
        }
    }


    private List<Object[]> putTestCases() {
        String username = "username";
        return asList(
                //             username,   cacheServiceResult,   expectedResult
                new Object[] { null,       false,                false },
                new Object[] { username,   false,                false },
                new Object[] { username,   true,                 true });
    }

    @Test
    public void put_testCases() {
        for (Object[] parameters: putTestCases()) {
            when(mockCacheService.put(anyString(), eq((String)parameters[0]), anyBoolean())).thenReturn((boolean)parameters[1]);
            boolean operationResult = userBlacklistCacheService.put((String)parameters[0]);
            assertEquals((boolean)parameters[2], operationResult);
        }
    }


    private List<Object[]> removeTestCases() {
        String username = "username";
        return asList(
                //             username,   cacheServiceResult,   expectedResult
                new Object[] { null,       false,                false },
                new Object[] { username,   false,                false },
                new Object[] { username,   true,                 true });
    }

    @Test
    public void remove_testCases() {
        for (Object[] parameters: removeTestCases()) {
            when(mockCacheService.remove(anyString(), eq((String)parameters[0]))).thenReturn((boolean)parameters[1]);
            boolean operationResult = userBlacklistCacheService.remove((String)parameters[0]);
            assertEquals((boolean)parameters[2], operationResult);
        }
    }

}
