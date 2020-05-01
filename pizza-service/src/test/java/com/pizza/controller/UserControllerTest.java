package com.pizza.controller;

import com.pizza.PizzaServiceApplication;
import com.pizza.configuration.Constants;
import com.pizza.configuration.rest.RestRoutes;
import com.pizza.service.cache.UserBlacklistCacheService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = PizzaServiceApplication.class)
public class UserControllerTest {

    @Autowired
    ApplicationContext context;

    private WebTestClient webTestClient;

    @MockBean
    private UserBlacklistCacheService mockUserBlacklistCacheService;

    @BeforeEach
    public void init() {
        this.webTestClient = WebTestClient.bindToApplicationContext(this.context).configureClient().build();
    }


    @Test
    public void addToBlacklist_whenNoLoggedUserIsGiven_thenUnauthorizedHttpCodeIsReturned() {
        // When/Then
        webTestClient.post()
                     .uri(RestRoutes.USER.ROOT + RestRoutes.USER.BLACKLIST + "/testUser")
                     .exchange()
                     .expectStatus().isUnauthorized();
    }


    @Test
    @WithMockUser(authorities = {Constants.ROLE_USER})
    public void addToBlacklist_whenNotValidAuthorityIsGiven_thenForbiddenHttpCodeIsReturned() {
        // When/Then
        webTestClient.post()
                     .uri(RestRoutes.USER.ROOT + RestRoutes.USER.BLACKLIST + "/testUser")
                     .exchange()
                     .expectStatus().isForbidden();
    }


    @Test
    @WithMockUser(authorities = {Constants.ROLE_ADMIN})
    public void addToBlacklist_whenPutInCacheReturnsFalse_thenUnprocessableEntityHttpCodeAndEmptyBodyAreReturned() {
        // When
        when(mockUserBlacklistCacheService.put(anyString())).thenReturn(false);

        // Then
        webTestClient.post()
                     .uri(RestRoutes.USER.ROOT + RestRoutes.USER.BLACKLIST + "/testUser")
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                     .expectBody().isEmpty();

        verify(mockUserBlacklistCacheService, times(1)).put(anyString());
    }


    @Test
    @WithMockUser(authorities = {Constants.ROLE_ADMIN})
    public void addToBlacklist_whenPutInCacheReturnsTrue_thenOkHttpCodeAndIncludedUsernameAreReturned() {
        // Given
        String username = "username";

        // When
        when(mockUserBlacklistCacheService.put(anyString())).thenReturn(true);

        // Then
        webTestClient.post()
                     .uri(RestRoutes.USER.ROOT + RestRoutes.USER.BLACKLIST + "/" + username)
                     .exchange()
                     .expectStatus().isOk()
                     .expectHeader().contentType(Constants.TEXT_PLAIN_UTF8_VALUE)
                     .expectBody()
                     .equals(username);

        verify(mockUserBlacklistCacheService, times(1)).put(anyString());
    }


    @Test
    public void removeFromBlacklist_whenNoLoggedUserIsGiven_thenUnauthorizedHttpCodeIsReturned() {
        // When/Then
        webTestClient.delete()
                     .uri(RestRoutes.USER.ROOT + RestRoutes.USER.BLACKLIST + "/testUser")
                     .exchange()
                     .expectStatus().isUnauthorized();
    }


    @Test
    @WithMockUser(authorities = {Constants.ROLE_USER})
    public void removeFromBlacklist_whenNotValidAuthorityIsGiven_thenForbiddenHttpCodeIsReturned() {
        // When/Then
        webTestClient.delete()
                     .uri(RestRoutes.USER.ROOT + RestRoutes.USER.BLACKLIST + "/testUser")
                     .exchange()
                     .expectStatus().isForbidden();
    }


    @Test
    @WithMockUser(authorities = {Constants.ROLE_ADMIN})
    public void removeFromBlacklist_whenRemoveInCacheReturnsFalse_thenUnprocessableEntityHttpCodeAndEmptyBodyAreReturned() {
        // When
        when(mockUserBlacklistCacheService.remove(anyString())).thenReturn(false);

        // Then
        webTestClient.delete()
                     .uri(RestRoutes.USER.ROOT + RestRoutes.USER.BLACKLIST + "/testUser")
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.NOT_FOUND)
                     .expectBody().isEmpty();

        verify(mockUserBlacklistCacheService, times(1)).remove(anyString());
    }


    @Test
    @WithMockUser(authorities = {Constants.ROLE_ADMIN})
    public void removeFromBlacklist_whenRemoveInCacheReturnsTrue_thenOkHttpCodeAndIncludedUsernameAreReturned() {
        // Given
        String username = "username";

        // When
        when(mockUserBlacklistCacheService.remove(anyString())).thenReturn(true);

        // Then
        webTestClient.delete()
                     .uri(RestRoutes.USER.ROOT + RestRoutes.USER.BLACKLIST + "/" + username)
                     .exchange()
                     .expectStatus().isOk()
                     .expectHeader().contentType(Constants.TEXT_PLAIN_UTF8_VALUE)
                     .expectBody()
                     .equals(username);

        verify(mockUserBlacklistCacheService, times(1)).remove(anyString());
    }

}
