package com.authenticationservice.service;

import com.authenticationservice.configuration.security.JwtConfiguration;
import com.authenticationservice.util.JwtUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityNotFoundException;

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


    @Test(expected = EntityNotFoundException.class)
    public void generateJwtToken_whenGivenAuthenticationRequestDtoIsNull_thenEntityNotFoundExceptionIsThrown() {
        // When/Then
        authenticationService.generateJwtToken(null);
    }

}
