package com.security.jwt.repository;

import com.security.jwt.model.User;
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
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;


    @Test
    @DisplayName("findByUsername: when null username is given then optional empty is returned")
    public void findByUsername_whenNullUsernameIsGiven_thenOptionalEmptyIsReturned() {
        // When
        Optional<User> user = userRepository.findByUsername(null);

        // Then
        assertNotNull(user);
        assertFalse(user.isPresent());
    }


    @Test
    @DisplayName("findByUsername: when a non existent username is given then optional empty is returned")
    public void findByUsername_whenANonExistentUsernameIsGiven_thenOptionalEmptyIsReturned() {
        // Given
        List<User> users = userRepository.findAll();

        // When
        Optional<User> user = userRepository.findByUsername(users.get(0).getUsername() + "@V2@_2");

        // Then
        assertNotNull(user);
        assertFalse(user.isPresent());
    }


    @Test
    @DisplayName("findByUsername: when an existent username is given then optional with related entity is returned")
    public void findByUsername_whenAnExistentUsernameIsGiven_thenOptionalWithRelatedEntityIsReturned() {
        // Given
        List<User> users = userRepository.findAll();

        // When
        Optional<User> user = userRepository.findByUsername(users.get(0).getUsername());

        // Then
        assertNotNull(user);
        assertTrue(user.isPresent());
        assertThat(user.get(), samePropertyValuesAs(users.get(0)));
    }

}
