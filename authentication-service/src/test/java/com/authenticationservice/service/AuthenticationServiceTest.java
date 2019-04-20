package com.authenticationservice.service;

import com.authenticationservice.configuration.security.JwtConfiguration;
import com.authenticationservice.dto.AuthenticationRequestDto;
import com.authenticationservice.model.User;
import com.authenticationservice.util.JwtUtil;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthenticationServiceTest {

    @Mock
    private PasswordEncoder mockPasswordEncoder;

    @Mock
    private JwtConfiguration mockJwtConfiguration;

    @Mock
    private JwtUtil mockJwtUtil;

    @Mock
    private UserService mockUserService;

    private AuthenticationService authenticationService;


    @Before
    public void init() {
        authenticationService = new AuthenticationService(mockPasswordEncoder, mockJwtConfiguration,
                                                          mockJwtUtil, mockUserService);
    }


    @Test
    public void generateJwtToken_whenGivenAuthenticationRequestDtoIsNull_thenOptionalEmptyIsReturned() {
        // When
        Optional<String> jwtToken = authenticationService.generateJwtToken(null);

        // Then
        assertNotNull(jwtToken);
        assertFalse(jwtToken.isPresent());
    }


    @Test
    public void generateJwtToken_whenGivenUsernameDoesNotExistInDatabase_thenOptionalEmptyIsReturned() {
        // Given
        String nonExistentUsername = "nonExistentUsername";
        AuthenticationRequestDto authenticationRequest = AuthenticationRequestDto.builder().username(nonExistentUsername).build();

        // When
        when(mockUserService.loadUserByUsername(nonExistentUsername)).thenReturn(null);
        Optional<String> jwtToken = authenticationService.generateJwtToken(authenticationRequest);

        // Then
        verify(mockUserService, times(1)).loadUserByUsername(nonExistentUsername);

        assertNotNull(jwtToken);
        assertFalse(jwtToken.isPresent());
    }


    @Test
    public void generateJwtToken_whenGivenPasswordDoesNotMatchWithStoredOneInDatabase_thenOptionalEmptyIsReturned() {
        // Given
        String existentUsername = "existentUsername";
        String requestPassword = "requestPassword";
        String userPassword = "userPassword";

        AuthenticationRequestDto authenticationRequest = AuthenticationRequestDto.builder().username(existentUsername)
                                                                                           .password(requestPassword).build();

        User user = User.builder().username(existentUsername).password(userPassword).build();

        // When
        when(mockUserService.loadUserByUsername(existentUsername)).thenReturn(user);
        when(mockPasswordEncoder.matches(authenticationRequest.getPassword(), user.getPassword())).thenReturn(false);

        Optional<String> jwtToken = authenticationService.generateJwtToken(authenticationRequest);

        // Then
        verify(mockPasswordEncoder, times(1)).matches(authenticationRequest.getPassword(), user.getPassword());

        assertNotNull(jwtToken);
        assertFalse(jwtToken.isPresent());
    }


    @Test
    public void generateJwtToken_whenTheJwtCouldNotBeGenerated_thenOptionalEmptyIsReturned() {
        // Given
        String existentUsername = "existentUsername";
        String password = "password";

        AuthenticationRequestDto authenticationRequest = AuthenticationRequestDto.builder().username(existentUsername)
                                                                                           .password(password).build();
        User user = User.builder().username(existentUsername).password(password).build();

        // When
        when(mockUserService.loadUserByUsername(existentUsername)).thenReturn(user);
        when(mockPasswordEncoder.matches(authenticationRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(mockJwtConfiguration.getSecretKey()).thenReturn("secretKey");
        when(mockJwtConfiguration.getExpirationTimeInMilliseconds()).thenReturn(100L);
        when(mockJwtUtil.generateJwtToken(any(UserDetails.class), any(SignatureAlgorithm.class), anyString(), anyLong()))
                .thenReturn(Optional.empty());

        Optional<String> jwtToken = authenticationService.generateJwtToken(authenticationRequest);

        // Then
        verify(mockJwtUtil, times(1)).generateJwtToken(any(UserDetails.class), any(SignatureAlgorithm.class),
                                                                              anyString(), anyLong());
        assertNotNull(jwtToken);
        assertFalse(jwtToken.isPresent());
    }


    @Test
    public void generateJwtToken_whenTheJwtWasGeneratedSuccessfully_thenValidJwtTokenIsReturned() {
        // Given
        String existentUsername = "existentUsername";
        String password = "password";
        String expectedJwtToken = "expectedJwtToken";

        AuthenticationRequestDto authenticationRequest = AuthenticationRequestDto.builder().username(existentUsername)
                .password(password).build();
        User user = User.builder().username(existentUsername).password(password).build();

        // When
        when(mockUserService.loadUserByUsername(existentUsername)).thenReturn(user);
        when(mockPasswordEncoder.matches(authenticationRequest.getPassword(), user.getPassword())).thenReturn(true);
        when(mockJwtConfiguration.getSecretKey()).thenReturn("secretKey");
        when(mockJwtConfiguration.getExpirationTimeInMilliseconds()).thenReturn(100L);
        when(mockJwtUtil.generateJwtToken(any(UserDetails.class), any(SignatureAlgorithm.class), anyString(), anyLong()))
                .thenReturn(Optional.of(expectedJwtToken));

        Optional<String> jwtTokenGenerated = authenticationService.generateJwtToken(authenticationRequest);

        // Then
        verify(mockJwtUtil, times(1)).generateJwtToken(any(UserDetails.class), any(SignatureAlgorithm.class),
                                                                              anyString(), anyLong());
        assertTrue(jwtTokenGenerated.isPresent());
        assertEquals(expectedJwtToken, jwtTokenGenerated.get());
    }


    @Test
    public void isJwtTokenValid_whenGivenTokenIsNull_thenFalseIsReturned() {
        // When
        boolean isValid = authenticationService.isJwtTokenValid(null);

        // Then
        assertFalse(isValid);
    }


    @Test
    public void isJwtTokenValid_whenGivenTokenIsInvalid_thenFalseIsReturned() {
        // Given
        String jwtToken = "jwtToken";

        // When
        when(mockJwtConfiguration.getSecretKey()).thenReturn("secretKey");
        when(mockJwtUtil.isTokenValid(anyString(), anyString())).thenReturn(false);

        boolean isValid = authenticationService.isJwtTokenValid(jwtToken);

        // Then
        assertFalse(isValid);
    }


    @Test
    public void isJwtTokenValid_whenGivenTokenIsValid_thenTrueIsReturned() {
        // Given
        String jwtToken = "jwtToken";

        // When
        when(mockJwtConfiguration.getSecretKey()).thenReturn("secretKey");
        when(mockJwtUtil.isTokenValid(anyString(), anyString())).thenReturn(true);

        boolean isValid = authenticationService.isJwtTokenValid(jwtToken);

        // Then
        assertTrue(isValid);
    }

}
