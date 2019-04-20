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
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

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
    public void isTokenValid_whenValidTokenIsGiven_thenFalseIsReturned() {
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

}
