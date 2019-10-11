package com.security.jwt.repository;

import com.security.jwt.model.JwtClientDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
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
        List<JwtClientDetails> jwtClientDetailsList = jwtClientDetailsRepository.findAll();

        // When
        Optional<JwtClientDetails> jwtClientDetails = jwtClientDetailsRepository.findByClientId(jwtClientDetailsList.get(0).getClientId() + "@V2@_2");

        // Then
        assertNotNull(jwtClientDetails);
        assertFalse(jwtClientDetails.isPresent());
    }


    @Test
    @DisplayName("findByClientId: when an existent clientId is given then optional with related entity is returned")
    public void findByUsername_whenAnExistentUsernameIsGiven_thenOptionalWithRelatedEntityIsReturned() {
        // Given
        List<JwtClientDetails> jwtClientDetailsList = jwtClientDetailsRepository.findAll();

        // When
        Optional<JwtClientDetails> jwtClientDetails = jwtClientDetailsRepository.findByClientId(jwtClientDetailsList.get(0).getClientId());

        // Then
        assertNotNull(jwtClientDetails);
        assertTrue(jwtClientDetails.isPresent());
        assertThat(jwtClientDetails.get(), samePropertyValuesAs(jwtClientDetailsList.get(0)));
    }

}
