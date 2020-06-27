package com.security.jwt.repository;

import com.security.jwt.enums.AuthenticationConfigurationEnum;
import com.security.jwt.enums.SignatureAlgorithmEnum;
import com.security.jwt.model.JwtClientDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class JwtClientDetailsRepositoryTest {

    @Autowired
    private JwtClientDetailsRepository jwtClientDetailsRepository;


    @Test
    @DisplayName("findByClientId: when null clientId is given then optional empty is returned")
    public void findByClientId_whenNullClientIdIsGiven_thenOptionalEmptyIsReturned() {
        // When
        Optional<JwtClientDetails> jwtClientDetails = jwtClientDetailsRepository.findByClientId(null);

        // Then
        assertNotNull(jwtClientDetails);
        assertFalse(jwtClientDetails.isPresent());
    }


    @Test
    @DisplayName("findByClientId: when a non existent clientId is given then optional empty is returned")
    public void findByUsername_whenANonExistentUsernameIsGiven_thenOptionalEmptyIsReturned() {
        // Given
        String notExistClientId = "12345abcd";

        // When
        Optional<JwtClientDetails> jwtClientDetails = jwtClientDetailsRepository.findByClientId(notExistClientId);

        // Then
        assertNotNull(jwtClientDetails);
        assertFalse(jwtClientDetails.isPresent());
    }


    @Test
    @DisplayName("findByClientId: when an existent clientId is given then optional with related entity is returned")
    public void findByUsername_whenAnExistentUsernameIsGiven_thenOptionalWithRelatedEntityIsReturned() {
        // Given
        JwtClientDetails existingJwtClientDetails = JwtClientDetails.builder()
                .clientId("Spring5Microservices")
                .clientSecret("{bcrypt}$2a$10$NlKX/TyTk41qraDjxg98L.xFdu7IQYRoi3Z37PZmjekaQYAeaRZgO")
                .signatureSecret("{cipher}04f1b9a71d880569283849aa911e4f3f3373a2522cba355e25e17f7ac7e262cb63d41295ab8bca038823b884858f05457306159cdfe68eb11c616028d6213b719887c07750e8c4b60dfea4196b1ddaffdcd462180028abc1a2d1dda69b8ac4bf")
                .signatureAlgorithm(SignatureAlgorithmEnum.valueOf("HS512"))
                .authenticationGenerator(AuthenticationConfigurationEnum.valueOf("SPRING5_MICROSERVICES"))
                .tokenType("Bearer")
                .useJwe(true)
                .accessTokenValidity(900)
                .refreshTokenValidity(3600)
                .build();

        // When
        Optional<JwtClientDetails> jwtClientDetails = jwtClientDetailsRepository.findByClientId(existingJwtClientDetails.getClientId());

        // Then
        assertNotNull(jwtClientDetails);
        assertTrue(jwtClientDetails.isPresent());
        assertThat(jwtClientDetails.get(), samePropertyValuesAs(existingJwtClientDetails));
    }

}
