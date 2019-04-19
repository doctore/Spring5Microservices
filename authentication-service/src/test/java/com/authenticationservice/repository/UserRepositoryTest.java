package com.authenticationservice.repository;

import com.authenticationservice.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;


    @Test
    public void findByUsername_whenNullUsernameIsGiven_thenOptionalEmptyIsReturned() {
        // When
        Optional<User> user = userRepository.findByUsername(null);

        // Then
        assertNotNull(user);
        assertFalse(user.isPresent());
    }


    @Test
    public void findByUsername_whenANonExistentUsernameIsGiven_thenOptionalEmptyIsReturned() {
        // Given
        List<User> users = userRepository.findAll();

        // When
        Optional<User> user = userRepository.findByUsername(users.get(0).getUsername() + "V2");

        // Then
        assertNotNull(user);
        assertFalse(user.isPresent());
    }


    @Test
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
