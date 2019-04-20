package com.authenticationservice.service;

import com.authenticationservice.model.User;
import com.authenticationservice.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

    @Mock
    private UserRepository mockUserRepository;

    private UserService userService;


    @Before
    public void init() {
        userService = new UserService(mockUserRepository);
    }


    @Test(expected = UsernameNotFoundException.class)
    public void loadUserByUsername_whenGivenUsernameIsNull_thenUsernameNotFoundExceptionIsThrown() {
        // When/Then
        userService.loadUserByUsername(null);
    }


    @Test(expected = UsernameNotFoundException.class)
    public void loadUserByUsername_whenGivenUsernameDoesNotExistInDatabase_thenUsernameNotFoundExceptionIsThrown() {
        // Given
        String nonExistentUsername = "nonExistentUsername";

        // When
        when(mockUserRepository.findByUsername(nonExistentUsername)).thenReturn(Optional.empty());
        userService.loadUserByUsername(nonExistentUsername);
    }


    @Test(expected = LockedException.class)
    public void loadUserByUsername_whenGivenUsernameBelongsNotActiveUserInDatabase_thenLockedExceptionIsThrown() {
        // Given
        User user = User.builder().username("username").active(false).build();

        // When
        when(mockUserRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        userService.loadUserByUsername(user.getUsername());
    }


    @Test
    public void loadUserByUsername_whenAnExistingUsernameIsGiven_thenOptionalWithRelatedEntityIsReturned() {
        // Given
        User user = User.builder().username("username").password("password").active(true).roles(new HashSet<>()).build();

        // When
        when(mockUserRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        UserDetails userDetails = userService.loadUserByUsername(user.getUsername());

        // Then
        assertNotNull(userDetails);
        assertEquals(user.getUsername(), userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
        assertEquals(user.isActive(), userDetails.isEnabled());
    }

}
