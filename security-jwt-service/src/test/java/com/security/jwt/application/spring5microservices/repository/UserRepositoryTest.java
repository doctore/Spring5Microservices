package com.security.jwt.application.spring5microservices.repository;

import com.security.jwt.application.spring5microservices.enums.RoleEnum;
import com.security.jwt.application.spring5microservices.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
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
        String notExistUsername = "12345abcd";

        // When
        Optional<User> user = userRepository.findByUsername(notExistUsername);

        // Then
        assertNotNull(user);
        assertFalse(user.isPresent());
    }


    @Test
    @DisplayName("findByUsername: when an existent username is given then optional with related entity is returned")
    public void findByUsername_whenAnExistentUsernameIsGiven_thenOptionalWithRelatedEntityIsReturned() {
        // Given
        User existingUser = User.builder()
                .id(1l)
                .username("user")
                .name("Normal user")
                .password("{bcrypt}$2a$10$i7LFiCo1JRm87ERePQOS3OkZ3Srgub8F7GyoWu6NmUuCLDTPq8zMW")
                .active(true)
                .roles(new HashSet<>(asList(RoleEnum.USER))).build();

        // When
        Optional<User> user = userRepository.findByUsername(existingUser.getUsername());

        // Then
        assertNotNull(user);
        assertTrue(user.isPresent());
        assertThat(user.get(), samePropertyValuesAs(existingUser));
    }

}
