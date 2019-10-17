package com.security.jwt.service.jwt.generator;

import com.security.jwt.dto.RawTokenInformationDto;
import com.security.jwt.enums.RoleEnum;
import com.security.jwt.model.Role;
import com.security.jwt.model.User;
import com.security.jwt.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;

import static com.security.jwt.enums.TokenKeyEnum.AUTHORITIES;
import static com.security.jwt.enums.TokenKeyEnum.NAME;
import static com.security.jwt.enums.TokenKeyEnum.USERNAME;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class Spring5MicroserviceJwtGeneratorTest {

    @Mock
    private UserService mockUserService;

    private Spring5MicroserviceJwtGenerator spring5MicroserviceJwtGenerator;


    @BeforeEach
    public void init() {
        spring5MicroserviceJwtGenerator = new Spring5MicroserviceJwtGenerator(mockUserService);
    }


    @Test
    @DisplayName("getTokenInformation: when an existent username is given then related information is returned")
    public void getTokenInformation_whenAnExistentUsernameIsGiven_thenRelatedInformationIsReturned() {
        // Given
        Role role = Role.builder().name(RoleEnum.ADMIN).build();
        User user = User.builder().username("test username").name("test name").roles(new HashSet<>(asList(role))).build();

        // When
        when(mockUserService.loadUserByUsername(user.getUsername())).thenReturn(user);
        RawTokenInformationDto rawTokenInformation = spring5MicroserviceJwtGenerator.getTokenInformation(user.getUsername());

        // Then
        checkTokenInformation(rawTokenInformation, user);
    }

    private void checkTokenInformation(RawTokenInformationDto rawTokenInformation, User user) {
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
