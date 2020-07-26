package com.security.jwt.service;

import com.security.jwt.exception.ClientNotFoundException;
import com.security.jwt.model.JwtClientDetails;
import com.security.jwt.repository.JwtClientDetailsRepository;
import com.security.jwt.service.cache.JwtClientDetailsCacheService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = JwtClientDetailsService.class)
public class JwtClientDetailsServiceTest {

    @MockBean
    private JwtClientDetailsCacheService mockJwtClientDetailsCacheService;

    @MockBean
    private JwtClientDetailsRepository mockJwtClientDetailsRepository;

    @Autowired
    private JwtClientDetailsService jwtClientDetailsService;


    static Stream<Arguments> findByClientIdTestCases() {
        JwtClientDetails jwtClientDetails = JwtClientDetails.builder().clientId("ItDoesNotCare").build();
        return Stream.of(
                //@formatter:off
                //            clientId,                repositoryResult,       cacheServiceResult,   expectedException,               expectedResult
                Arguments.of( null,                    empty(),                null,                 ClientNotFoundException.class,   null ),
                Arguments.of( "NotFound",              empty(),                null,                 ClientNotFoundException.class,   null ),
                Arguments.of( "FoundOnlyInDatabase",   of(jwtClientDetails),   null,                 null,                            jwtClientDetails ),
                Arguments.of( "FoundInCache",          empty(),                jwtClientDetails,     null,                            jwtClientDetails )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findByClientIdTestCases")
    @DisplayName("findByClientId: test cases")
    public void findByClientId_testCases(String clientId, Optional<JwtClientDetails> repositoryResult, JwtClientDetails cacheServiceResult,
                                         Class<? extends Exception> expectedException, JwtClientDetails expectedResult) {

        when(mockJwtClientDetailsRepository.findByClientId(clientId)).thenReturn(repositoryResult);
        when(mockJwtClientDetailsCacheService.get(eq(clientId))).thenReturn(ofNullable(cacheServiceResult));
        if (null != expectedException) {
            assertThrows(expectedException, () -> jwtClientDetailsService.findByClientId(clientId));
        }
        else {
            assertEquals(expectedResult, jwtClientDetailsService.findByClientId(clientId));
        }
        findByClientId_verifyInvocations(clientId, repositoryResult, cacheServiceResult);
    }

    private void findByClientId_verifyInvocations(String clientId, Optional<JwtClientDetails> repositoryResult, JwtClientDetails cacheServiceResult) {
        // Found jwtClientDetails only in database
        if (repositoryResult.isPresent() && null == cacheServiceResult) {
            verify(mockJwtClientDetailsRepository, times(1)).findByClientId(eq(clientId));
            verify(mockJwtClientDetailsCacheService, times(1)).get(eq(clientId));
            verify(mockJwtClientDetailsCacheService, times(1)).put(eq(clientId), eq(repositoryResult.get()));
        }
        // Found jwtClientDetails in cache
        if (null != cacheServiceResult) {
            verify(mockJwtClientDetailsRepository, times(0)).findByClientId(eq(clientId));
            verify(mockJwtClientDetailsCacheService, times(1)).get(eq(clientId));
            verify(mockJwtClientDetailsCacheService, times(0)).put(any(), any());
        }
        // Not found jwtClientDetails neither in cache nor database
        if (!repositoryResult.isPresent() && null == cacheServiceResult) {
            verify(mockJwtClientDetailsRepository, times(1)).findByClientId(eq(clientId));
            verify(mockJwtClientDetailsCacheService, times(1)).get(eq(clientId));
            verify(mockJwtClientDetailsCacheService, times(0)).put(any(), any());
        }
    }


    static Stream<Arguments> findByUsernameTestCases() {
        JwtClientDetails jwtClientDetails = JwtClientDetails.builder().clientId("ItDoesNotCare").build();
        return Stream.of(
                //@formatter:off
                //            clientId,                repositoryResult,       expectedException,               expectedResult
                Arguments.of( null,                    empty(),                ClientNotFoundException.class,   null ),
                Arguments.of( "NotFound",              empty(),                ClientNotFoundException.class,   null ),
                Arguments.of( "Found",                 of(jwtClientDetails),   null,                            jwtClientDetails )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findByUsernameTestCases")
    @DisplayName("findByUsername: test cases")
    public void findByUsername_testCases(String clientId, Optional<JwtClientDetails> repositoryResult,
                                         Class<? extends Exception> expectedException, JwtClientDetails expectedResult) {
        when(mockJwtClientDetailsRepository.findByClientId(clientId)).thenReturn(repositoryResult);

        if (null != expectedException) {
            assertThrows(expectedException, () -> jwtClientDetailsService.findByUsername(clientId));
        }
        else {
            Mono<UserDetails> result = jwtClientDetailsService.findByUsername(clientId);
            StepVerifier.create(result)
                    .expectNextMatches(userDetails -> {
                        assertEquals(expectedResult, userDetails);
                        verify(mockJwtClientDetailsRepository, times(1)).findByClientId(eq(clientId));
                        verify(mockJwtClientDetailsCacheService, times(1)).get(eq(clientId));
                        return true;
                    })
                    .verifyComplete();
        }
    }

}
