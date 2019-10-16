package com.authenticationservice.util;

import com.authenticationservice.configuration.Constants;
import com.authenticationservice.enums.RoleEnum;
import com.authenticationservice.model.Role;
import com.authenticationservice.model.User;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;


    @Test
    public void generateJwtToken_whenNullUserDetailsIsGiven_thenOptionalEmptyIsReturned() {
        // When
        Optional<String> jwtToken = jwtUtil.generateJwtToken(null, Constants.JWT.SIGNATURE_ALGORITHM,
                                                             "secretKey", 10);
        // Then
        assertNotNull(jwtToken);
        assertFalse(jwtToken.isPresent());
    }


    @Test(expected = IllegalArgumentException.class)
    public void generateJwtToken_whenNullSignatureAlgorithmIsGiven_thenIllegalArgumentExceptionIsThrown() {
        // When/Then
        jwtUtil.generateJwtToken(new User(), null, "secretKey", 10);
    }


    @Test(expected = IllegalArgumentException.class)
    public void generateJwtToken_whenNullSecretKeyIsGiven_thenIllegalArgumentExceptionIsThrown() {
        // When/Then
        jwtUtil.generateJwtToken(new User(), Constants.JWT.SIGNATURE_ALGORITHM, null, 10);
    }


    @Test
    public void generateJwtToken_whenAllRequiredInformationIsGiven_thenValidJwtTokenIsReturned() {
        // Given
        Role role = Role.builder().name(RoleEnum.USER).build();
        User user = User.builder().username("username").roles(new HashSet<>(Arrays.asList(role))).build();

        // When
        Optional<String> jwtToken = jwtUtil.generateJwtToken(user, SignatureAlgorithm.HS512, "secretKey", 100);

        // Then
        assertNotNull(jwtToken);
        assertTrue(jwtToken.isPresent());
    }


    @Test(expected = IllegalArgumentException.class)
    public void isTokenValid_whenNullTokenIsGiven_thenIllegalArgumentExceptionIsThrown() {
        // When/Then
        jwtUtil.isTokenValid(null, "secretKey");
    }


    @Test(expected = IllegalArgumentException.class)
    public void isTokenValid_whenNullSecretKeyIsGiven_thenIllegalArgumentExceptionIsThrown() {
        // When/Then
        jwtUtil.isTokenValid("token JWT", null);
    }


    @Test
    public void isTokenValid_whenNotValidTokenIsGiven_thenFalseIsReturned() {
        // When
        boolean isValid = jwtUtil.isTokenValid("token JWT", "secretKey");

        // Then
        assertFalse(isValid);
    }


    @Test
    public void isTokenValid_whenNotSameSecretKeyIsGiven_thenFalseIsReturned() {
        // Given
        String expiredJwtToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VybmFtZSIsInJvbGVzIjpbeyJhdXRob3JpdHkiOiJVU0VSIn1dL"
                               + "CJpYXQiOjE1NTU2NzQ2NjksImV4cCI6MTU1NTY3NDY2OX0.y8FX6KYOK8OfCD_ZMbMhYa55QRLQ7-za-WVnUr_"
                               + "MEZ0ka4ZR4_4SkKuy3v_FFTEXyRPBtWSqYnB83-tW9BoQ7g";

        String jwtSecretKeyUsed = "secretKey";

        // When
        boolean isValid = jwtUtil.isTokenValid(expiredJwtToken, jwtSecretKeyUsed+jwtSecretKeyUsed);

        // Then
        assertFalse(isValid);
    }


    @Test
    public void isTokenValid_whenExpiredTokenIsGiven_thenFalseIsReturned() {
        // Given
        String expiredJwtToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VybmFtZSIsInJvbGVzIjpbeyJhdXRob3JpdHkiOiJVU0VSIn1dL"
                               + "CJpYXQiOjE1NTU2NzQ2NjksImV4cCI6MTU1NTY3NDY2OX0.y8FX6KYOK8OfCD_ZMbMhYa55QRLQ7-za-WVnUr_"
                               + "MEZ0ka4ZR4_4SkKuy3v_FFTEXyRPBtWSqYnB83-tW9BoQ7g";
        // When
        boolean isValid = jwtUtil.isTokenValid(expiredJwtToken, "secretKey");

        // Then
        assertFalse(isValid);
    }


    @Test
    public void isTokenValid_whenValidTokenIsGiven_thenTrueIsReturned() {
        // Given
        Role role = Role.builder().name(RoleEnum.USER).build();
        User user = User.builder().username("username").roles(new HashSet<>(Arrays.asList(role))).build();
        String jwtSecretKey = "secretKey";
        long expirationTimeInMilliseconds = 180000;

        Optional<String> jwtToken = jwtUtil.generateJwtToken(user, SignatureAlgorithm.HS512, jwtSecretKey, expirationTimeInMilliseconds);

        // When
        boolean isValid = jwtUtil.isTokenValid(jwtToken.get(), jwtSecretKey);

        // Then
        assertTrue(isValid);
    }


    @Test(expected = IllegalArgumentException.class)
    public void getUsernameFromToken_whenNullTokenIsGiven_thenIllegalArgumentExceptionIsThrown() {
        // When/Then
        jwtUtil.getUsernameFromToken(null, "secretKey");
    }


    @Test(expected = IllegalArgumentException.class)
    public void getUsernameFromToken_whenNullSecretKeyIsGiven_thenIllegalArgumentExceptionIsThrown() {
        // When/Then
        jwtUtil.getUsernameFromToken("token JWT", null);
    }


    @Test
    public void getUsernameFromToken_whenNotValidTokenIsGiven_thenOptionalEmptyIsReturned() {
        // When
        Optional<String> username = jwtUtil.getUsernameFromToken("token JWT", "secretKey");

        // Then
        assertNotNull(username);
        assertFalse(username.isPresent());
    }


    @Test
    public void getUsernameFromToken_whenNotSameSecretKeyIsGiven_thenOptionalEmptyIsReturned() {
        // Given
        String expiredJwtToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VybmFtZSIsInJvbGVzIjpbeyJhdXRob3JpdHkiOiJVU0VSIn1dL"
                               + "CJpYXQiOjE1NTU2NzQ2NjksImV4cCI6MTU1NTY3NDY2OX0.y8FX6KYOK8OfCD_ZMbMhYa55QRLQ7-za-WVnUr_"
                               + "MEZ0ka4ZR4_4SkKuy3v_FFTEXyRPBtWSqYnB83-tW9BoQ7g";

        String jwtSecretKeyUsed = "secretKey";

        // When
        Optional<String> username = jwtUtil.getUsernameFromToken(expiredJwtToken, jwtSecretKeyUsed+jwtSecretKeyUsed);

        // Then
        assertNotNull(username);
        assertFalse(username.isPresent());
    }


    @Test
    public void getUsernameFromToken_whenExpiredTokenIsGiven_thenOptionalEmptyIsReturned() {
        // Given
        String expiredJwtToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VybmFtZSIsInJvbGVzIjpbeyJhdXRob3JpdHkiOiJVU0VSIn1dL"
                               + "CJpYXQiOjE1NTU2NzQ2NjksImV4cCI6MTU1NTY3NDY2OX0.y8FX6KYOK8OfCD_ZMbMhYa55QRLQ7-za-WVnUr_"
                               + "MEZ0ka4ZR4_4SkKuy3v_FFTEXyRPBtWSqYnB83-tW9BoQ7g";
        // When
        Optional<String> username = jwtUtil.getUsernameFromToken(expiredJwtToken, "secretKey");

        // Then
        assertNotNull(username);
        assertFalse(username.isPresent());
    }


    @Test
    public void getUsernameFromToken_whenValidTokenIsGiven_thenOptionalWithExpectedUsernameIsReturned() {
        // Given
        Role role = Role.builder().name(RoleEnum.USER).build();
        User user = User.builder().username("username").roles(new HashSet<>(Arrays.asList(role))).build();
        String jwtSecretKey = "secretKey";
        long expirationTimeInMilliseconds = 180000;

        Optional<String> jwtToken = jwtUtil.generateJwtToken(user, SignatureAlgorithm.HS512, jwtSecretKey, expirationTimeInMilliseconds);

        // When
        Optional<String> username = jwtUtil.getUsernameFromToken(jwtToken.get(), jwtSecretKey);

        // Then
        assertTrue(username.isPresent());
        assertEquals(user.getUsername(), username.get());
    }


    @Test(expected = IllegalArgumentException.class)
    public void getExpirationDateFromToken_whenNullTokenIsGiven_thenIllegalArgumentExceptionIsThrown() {
        // When/Then
        jwtUtil.getExpirationDateFromToken(null, "secretKey");
    }


    @Test(expected = IllegalArgumentException.class)
    public void getExpirationDateFromToken_whenNullSecretKeyIsGiven_thenIllegalArgumentExceptionIsThrown() {
        // When/Then
        jwtUtil.getExpirationDateFromToken("token JWT", null);
    }


    @Test
    public void getExpirationDateFromToken_whenNotValidTokenIsGiven_thenOptionalEmptyIsReturned() {
        // When
        Optional<Date> expirationDate = jwtUtil.getExpirationDateFromToken("token JWT", "secretKey");

        // Then
        assertNotNull(expirationDate);
        assertFalse(expirationDate.isPresent());
    }


    @Test
    public void getExpirationDateFromToken_whenNotSameSecretKeyIsGiven_thenOptionalEmptyIsReturned() {
        // Given
        String expiredJwtToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VybmFtZSIsInJvbGVzIjpbeyJhdXRob3JpdHkiOiJVU0VSIn1dL"
                               + "CJpYXQiOjE1NTU2NzQ2NjksImV4cCI6MTU1NTY3NDY2OX0.y8FX6KYOK8OfCD_ZMbMhYa55QRLQ7-za-WVnUr_"
                               + "MEZ0ka4ZR4_4SkKuy3v_FFTEXyRPBtWSqYnB83-tW9BoQ7g";

        String jwtSecretKeyUsed = "secretKey";

        // When
        Optional<Date> expirationDate = jwtUtil.getExpirationDateFromToken(expiredJwtToken, jwtSecretKeyUsed+jwtSecretKeyUsed);

        // Then
        assertNotNull(expirationDate);
        assertFalse(expirationDate.isPresent());
    }


    @Test
    public void getExpirationDateFromToken_whenExpiredTokenIsGiven_thenOptionalEmptyIsReturned() {
        // Given
        String expiredJwtToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VybmFtZSIsInJvbGVzIjpbeyJhdXRob3JpdHkiOiJVU0VSIn1dL"
                               + "CJpYXQiOjE1NTU2NzQ2NjksImV4cCI6MTU1NTY3NDY2OX0.y8FX6KYOK8OfCD_ZMbMhYa55QRLQ7-za-WVnUr_"
                               + "MEZ0ka4ZR4_4SkKuy3v_FFTEXyRPBtWSqYnB83-tW9BoQ7g";
        // When
        Optional<Date> expirationDate = jwtUtil.getExpirationDateFromToken(expiredJwtToken, "secretKey");

        // Then
        assertNotNull(expirationDate);
        assertFalse(expirationDate.isPresent());
    }


    @Test
    public void getExpirationDateFromToken_whenValidTokenIsGiven_thenOptionalWithExpectedExpirationDateIsReturned() {
        // Given
        Role role = Role.builder().name(RoleEnum.USER).build();
        User user = User.builder().username("username").roles(new HashSet<>(Arrays.asList(role))).build();
        String jwtSecretKey = "secretKey";
        long expirationTimeInMilliseconds = 180000;

        Optional<String> jwtToken = jwtUtil.generateJwtToken(user, SignatureAlgorithm.HS512, jwtSecretKey, expirationTimeInMilliseconds);

        // When
        Optional<Date> expirationDate = jwtUtil.getExpirationDateFromToken(jwtToken.get(), jwtSecretKey);

        // Then
        assertTrue(expirationDate.isPresent());
        assertTrue(expirationDate.get().after(new Date()));
    }


    @Test(expected = IllegalArgumentException.class)
    public void getRolesFromToken_whenNullTokenIsGiven_thenIllegalArgumentExceptionIsThrown() {
        // When/Then
        jwtUtil.getRolesFromToken(null, "secretKey");
    }


    @Test(expected = IllegalArgumentException.class)
    public void getRolesFromToken_whenNullSecretKeyIsGiven_thenIllegalArgumentExceptionIsThrown() {
        // When/Then
        jwtUtil.getRolesFromToken("token JWT", null);
    }


    @Test
    public void getRolesFromToken_whenNotValidTokenIsGiven_thenEmptyCollectionIsReturned() {
        // When
        Collection<? extends GrantedAuthority> roles = jwtUtil.getRolesFromToken("token JWT", "secretKey");

        // Then
        assertNotNull(roles);
        assertTrue(roles.isEmpty());
    }


    @Test
    public void getRolesFromToken_whenNotSameSecretKeyIsGiven_thenEmptyCollectionIsReturned() {
        // Given
        String expiredJwtToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VybmFtZSIsInJvbGVzIjpbeyJhdXRob3JpdHkiOiJVU0VSIn1dL"
                               + "CJpYXQiOjE1NTU2NzQ2NjksImV4cCI6MTU1NTY3NDY2OX0.y8FX6KYOK8OfCD_ZMbMhYa55QRLQ7-za-WVnUr_"
                               + "MEZ0ka4ZR4_4SkKuy3v_FFTEXyRPBtWSqYnB83-tW9BoQ7g";

        String jwtSecretKeyUsed = "secretKey";

        // When
        Collection<? extends GrantedAuthority> roles = jwtUtil.getRolesFromToken(expiredJwtToken, jwtSecretKeyUsed+jwtSecretKeyUsed);

        // Then
        assertNotNull(roles);
        assertTrue(roles.isEmpty());
    }


    @Test
    public void getRolesFromToken_whenExpiredTokenIsGiven_thenEmptyCollectionIsReturned() {
        // Given
        String expiredJwtToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VybmFtZSIsInJvbGVzIjpbeyJhdXRob3JpdHkiOiJVU0VSIn1dL"
                               + "CJpYXQiOjE1NTU2NzQ2NjksImV4cCI6MTU1NTY3NDY2OX0.y8FX6KYOK8OfCD_ZMbMhYa55QRLQ7-za-WVnUr_"
                               + "MEZ0ka4ZR4_4SkKuy3v_FFTEXyRPBtWSqYnB83-tW9BoQ7g";
        // When
        Collection<? extends GrantedAuthority> roles = jwtUtil.getRolesFromToken(expiredJwtToken, "secretKey");

        // Then
        assertNotNull(roles);
        assertTrue(roles.isEmpty());
    }


    @Test
    public void getExpirationDateFromToken_whenValidTokenIsGiven_thenCollectionWithExpectedRolesIsReturned() {
        // Given
        Role role = Role.builder().name(RoleEnum.USER).build();
        User user = User.builder().username("username").roles(new HashSet<>(Arrays.asList(role))).build();
        String jwtSecretKey = "secretKey";
        long expirationTimeInMilliseconds = 180000;

        Optional<String> jwtToken = jwtUtil.generateJwtToken(user, SignatureAlgorithm.HS512, jwtSecretKey, expirationTimeInMilliseconds);

        // When
        Collection<? extends GrantedAuthority> roles = jwtUtil.getRolesFromToken(jwtToken.get(), jwtSecretKey);

        // Then
        assertNotNull(roles);
        assertEquals(user.getAuthorities().size(), roles.size());

        for (GrantedAuthority auth: user.getAuthorities())
            assertTrue(auth.getAuthority().equals(role.getName().name()));
    }

}
