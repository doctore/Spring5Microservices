package com.security.jwt.application.spring5microservices.service;

import com.security.jwt.dto.RawAuthenticationInformationDto;
import com.security.jwt.application.spring5microservices.enums.RoleEnum;
import com.security.jwt.application.spring5microservices.model.Role;
import com.security.jwt.application.spring5microservices.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;

import java.util.HashSet;
import java.util.Optional;

import static com.security.jwt.enums.TokenKeyEnum.AUTHORITIES;
import static com.security.jwt.enums.TokenKeyEnum.NAME;
import static com.security.jwt.enums.TokenKeyEnum.USERNAME;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = AuthenticationGenerator.class)
public class AuthenticationGeneratorTest {

    @Autowired
    private AuthenticationGenerator authenticationGenerator;


    @Test
    @DisplayName("getRawAuthenticationInformation: when not existing user is given then Optional empty is returned")
    public void getRawAuthenticationInformation_whenNotExistingUserIsGiven_thenOptionalEmptyIsReturned() {
        Optional<RawAuthenticationInformationDto> rawTokenInformation = authenticationGenerator.getRawAuthenticationInformation(null);
        assertNotNull(rawTokenInformation);
        assertFalse(rawTokenInformation.isPresent());
    }

    @Test
    @DisplayName("getRawAuthenticationInformation: when an existing user is given then related information is returned")
    public void getRawAuthenticationInformation_whenAnExistingUserIsGiven_thenRelatedInformationIsReturned() {
        // Given
        Role role = Role.builder().name(RoleEnum.ADMIN).build();
        User user = User.builder().username("test username").name("test name").roles(new HashSet<>(asList(role))).build();

        // When
        Optional<RawAuthenticationInformationDto> rawTokenInformation = authenticationGenerator.getRawAuthenticationInformation(user);

        // Then
        assertTrue(rawTokenInformation.isPresent());
        checkTokenInformation(rawTokenInformation.get(), user);
    }

    private void checkTokenInformation(RawAuthenticationInformationDto rawTokenInformation,
                                       User user) {
        assertNotNull(rawTokenInformation);
        assertNotNull(rawTokenInformation.getAccessTokenInformation());
        assertNotNull(rawTokenInformation.getRefreshTokenInformation());
        assertNotNull(rawTokenInformation.getAdditionalTokenInformation());

        assertEquals(user.getUsername(), rawTokenInformation.getAccessTokenInformation().get(USERNAME.getKey()));
        assertEquals(user.getName(), rawTokenInformation.getAccessTokenInformation().get(NAME.getKey()));
        assertEquals(user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(toList()),
                     rawTokenInformation.getAccessTokenInformation().get(AUTHORITIES.getKey()));

        assertEquals(user.getUsername(), rawTokenInformation.getRefreshTokenInformation().get(USERNAME.getKey()));

        assertEquals(user.getUsername(), rawTokenInformation.getAdditionalTokenInformation().get(USERNAME.getKey()));
        assertEquals(user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(toList()),
                     rawTokenInformation.getAdditionalTokenInformation().get(AUTHORITIES.getKey()));
    }

}
