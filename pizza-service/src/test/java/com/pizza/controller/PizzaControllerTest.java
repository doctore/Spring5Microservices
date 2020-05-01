package com.pizza.controller;

import com.pizza.PizzaServiceApplication;
import com.pizza.configuration.Constants;
import com.pizza.configuration.rest.RestRoutes;
import com.pizza.dto.IngredientDto;
import com.pizza.dto.PizzaDto;
import com.pizza.enums.PizzaEnum;
import com.pizza.service.PizzaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.pizza.TestDataFactory.buildIngredientDto;
import static com.pizza.TestDataFactory.buildPizzaDto;
import static com.pizza.enums.PizzaEnum.CARBONARA;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = PizzaServiceApplication.class)
public class PizzaControllerTest {

    @Autowired
    ApplicationContext context;

    private WebTestClient webTestClient;

    @MockBean
    private PizzaService mockPizzaService;

    @BeforeEach
    public void init() {
        this.webTestClient = WebTestClient.bindToApplicationContext(this.context).configureClient().build();
    }


    @Test
    public void create_whenNoLoggedUserIsGiven_thenUnauthorizedHttpCodeIsReturned() {
        // When/Then
        webTestClient.post()
                     .uri(RestRoutes.PIZZA.ROOT)
                     .body(Mono.just(new PizzaDto()), PizzaDto.class)
                     .exchange()
                     .expectStatus().isUnauthorized();
    }


    @Test
    @WithMockUser(authorities = {Constants.ROLE_USER})
    public void create_whenNotValidAuthorityIsGiven_thenForbiddenHttpCodeIsReturned() {
        // Given
        PizzaDto pizzaDto = buildPizzaDto(1, CARBONARA.getInternalPropertyValue(), 7D, Set.of());

        // When/Then
        webTestClient.post()
                     .uri(RestRoutes.PIZZA.ROOT)
                     .body(Mono.just(pizzaDto), PizzaDto.class)
                     .exchange()
                     .expectStatus().isForbidden();
    }


    @Test
    @WithMockUser(authorities = {Constants.ROLE_ADMIN})
    public void create_whenGivenDtoDoesNotVerifyTheValidations_thenBadRequestHttpCodeAndValidationErrorsAreReturned() {
        // Given
        PizzaDto pizzaDto = buildPizzaDto(1, "Not existing", 7D, Set.of());
        String validPizzaNames = Arrays.stream(PizzaEnum.values())
                                       .map(PizzaEnum::getInternalPropertyValue)
                                       .collect(Collectors.joining(", "));
        // When/Then
        webTestClient.post()
                     .uri(RestRoutes.PIZZA.ROOT)
                     .body(Mono.just(pizzaDto), PizzaDto.class)
                     .exchange()
                     .expectStatus().isBadRequest()
                     .expectBody(String.class)
                     .isEqualTo("Error in the given parameters: [Field error in object 'pizzaDto' on field 'name' due to: "
                              + "must be one of the values included in [" + validPizzaNames + "]]");
    }


    @Test
    @WithMockUser(authorities = {Constants.ROLE_ADMIN})
    public void create_whenSaveDoesNotReturnAnEntity_thenUnprocessableEntityHttpCodeAndEmptyBodyAreReturned() {
        // Given
        PizzaDto pizzaDto = buildPizzaDto(1, CARBONARA.getInternalPropertyValue(), 7D, Set.of());

        // When
        when(mockPizzaService.save(any())).thenReturn(Optional.empty());

        // Then
        webTestClient.post()
                     .uri(RestRoutes.PIZZA.ROOT)
                     .body(Mono.just(pizzaDto), PizzaDto.class)
                     .exchange()
                     .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                     .expectBody().isEmpty();
    }


    @Test
    @WithMockUser(authorities = {Constants.ROLE_ADMIN})
    public void create_whenNotEmptyDtoIsGiven_thenCreatedHttpCodeAndPizzaDtoAreReturned() {
        // Given
        IngredientDto beforeIngredientDto = buildIngredientDto(null, "Cheese");
        IngredientDto afterIngredientDto = buildIngredientDto(1, beforeIngredientDto.getName());
        PizzaDto beforePizzaDto = buildPizzaDto(null, CARBONARA.getInternalPropertyValue(), 7D, Set.of(beforeIngredientDto));
        PizzaDto afterPizzaDto = buildPizzaDto(1, beforePizzaDto.getName(), beforePizzaDto.getCost(), Set.of(afterIngredientDto));

        // When
        when(mockPizzaService.save(beforePizzaDto)).thenReturn(Optional.of(afterPizzaDto));

        // Then
        webTestClient.post()
                     .uri(RestRoutes.PIZZA.ROOT)
                     .body(Mono.just(beforePizzaDto), PizzaDto.class)
                     .exchange()
                     .expectStatus().isCreated()
                     .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                     .expectBody()
                     .jsonPath("$.id").isEqualTo(afterPizzaDto.getId())
                     .jsonPath("$.name").isEqualTo(afterPizzaDto.getName())
                     .jsonPath("$.cost").isEqualTo(afterPizzaDto.getCost())
                     .jsonPath("$.ingredients.[0].id").isEqualTo(afterIngredientDto.getId())
                     .jsonPath("$.ingredients.[0].name").isEqualTo(afterIngredientDto.getName());

        verify(mockPizzaService, times(1)).save(beforePizzaDto);
    }


    @Test
    public void findByName_whenNoLoggedUserIsGiven_thenUnauthorizedHttpCodeIsReturned() {
        // Given
        String validPizzaName = "pizzaName";

        // When/Then
        webTestClient.get()
                     .uri(RestRoutes.PIZZA.ROOT + "/" + validPizzaName)
                     .exchange()
                     .expectStatus().isUnauthorized();
    }


    @Test
    @WithMockUser(authorities = {"NOT_EXISTING"})
    public void findByName_whenNotValidAuthorityIsGiven_thenForbiddenHttpCodeIsReturned() {
        // Given
        String validPizzaName = "pizzaName";

        // When/Then
        webTestClient.get()
                     .uri(RestRoutes.PIZZA.ROOT + "/" + validPizzaName)
                     .exchange()
                     .expectStatus().isForbidden();
    }


    @Test
    @WithMockUser(authorities = {Constants.ROLE_USER})
    public void findByName_whenTheNameDoesNotVerifyTheValidations_thenBadRequestHttpCodeAndAndValidationErrorsAreReturned() {
        // Given
        String notValidPizzaName = "pizzaName1pizzaName2pizzaName3pizzaName4pizzaName5pizzaName6pizzaName7";

        // When/Then
        webTestClient.get()
                     .uri(RestRoutes.PIZZA.ROOT + "/" + notValidPizzaName)
                     .exchange()
                     .expectStatus().isBadRequest()
                     .expectBody(String.class)
                     .isEqualTo("The following constraints have failed: findByName.name: size must be between 1 and 64");
    }


    @Test
    @WithMockUser(authorities = {Constants.ROLE_USER})
    public void findByName_whenTheNameDoesNotExist_thenNotFoundHttpCodeAndEmptyBodyAreReturned() {
        // When
        when(mockPizzaService.findByName(anyString())).thenReturn(Optional.empty());

        // Then
        webTestClient.get()
                     .uri(RestRoutes.PIZZA.ROOT + "/carbonara")
                     .exchange()
                     .expectStatus().isNotFound()
                     .expectBody().isEmpty();
    }


    @Test
    @WithMockUser(authorities = {Constants.ROLE_USER})
    public void findByName_whenTheNameExists_thenOkHttpCodeAndPizzaDtoAreReturned() {
        // Given
        IngredientDto ingredientDto = buildIngredientDto(null, "Bacon");
        PizzaDto pizzaDto = buildPizzaDto(1, CARBONARA.getInternalPropertyValue(), 7D, Set.of(ingredientDto));

        // When
        when(mockPizzaService.findByName(anyString())).thenReturn(Optional.of(pizzaDto));

        // Then
        webTestClient.get()
                     .uri(RestRoutes.PIZZA.ROOT + "/" + pizzaDto.getName())
                     .exchange()
                     .expectStatus().isOk()
                     .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                     .expectBody()
                     .jsonPath("$.id").isEqualTo(pizzaDto.getId())
                     .jsonPath("$.name").isEqualTo(pizzaDto.getName())
                     .jsonPath("$.cost").isEqualTo(pizzaDto.getCost())
                     .jsonPath("$.ingredients.[0].id").isEqualTo(ingredientDto.getId())
                     .jsonPath("$.ingredients.[0].name").isEqualTo(ingredientDto.getName());
    }


    @Test
    public void findPageWithIngredients_whenNoLoggedUserIsGiven_thenUnauthorizedHttpCodeIsReturned() {
        // Given
        int notValidPage = -1;
        int size = 1;

        // When/Then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder.path(RestRoutes.PIZZA.ROOT + RestRoutes.PIZZA.PAGE_WITH_INGREDIENTS)
                        .queryParam("page", notValidPage)
                        .queryParam("size", size)
                        .build())
                     .exchange()
                     .expectStatus().isUnauthorized();
    }


    @Test
    @WithMockUser(authorities = {"NOT_EXISTING"})
    public void findPageWithIngredients_whenNotValidAuthorityIsGiven_thenForbiddenHttpCodeIsReturned() {
        // Given
        int notValidPage = -1;
        int size = 1;

        // When/Then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder.path(RestRoutes.PIZZA.ROOT + RestRoutes.PIZZA.PAGE_WITH_INGREDIENTS)
                                                  .queryParam("page", notValidPage)
                                                  .queryParam("size", size)
                                                  .build())
                     .exchange()
                     .expectStatus().isForbidden();
    }


    @Test
    @WithMockUser(authorities = {Constants.ROLE_USER})
    public void findPageWithIngredients_whenThePageDoesNotVerifyTheValidations_thenBadRequestHttpCodeAndAndValidationErrorsAreReturned() {
        // Given
        int notValidPage = -1;
        int size = 1;

        // When/Then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder.path(RestRoutes.PIZZA.ROOT + RestRoutes.PIZZA.PAGE_WITH_INGREDIENTS)
                                                  .queryParam("page", notValidPage)
                                                  .queryParam("size", size)
                                                  .build())
                     .exchange()
                     .expectStatus().isBadRequest()
                     .expectBody(String.class)
                     .isEqualTo("The following constraints have failed: findPageWithIngredients.page: must be greater than or equal to 0");
    }


    @Test
    @WithMockUser(authorities = {Constants.ROLE_USER})
    public void findPageWithIngredients_whenTheSizeDoesNotVerifyTheValidations_thenBadRequestHttpCodeAndAndValidationErrorsAreReturned() {
        // Given
        int page = 0;
        int notValidSize = 0;

        // When/Then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder.path(RestRoutes.PIZZA.ROOT + RestRoutes.PIZZA.PAGE_WITH_INGREDIENTS)
                                                  .queryParam("page", page)
                                                  .queryParam("size", notValidSize)
                                                  .build())
                     .exchange()
                     .expectStatus().isBadRequest()
                     .expectBody(String.class)
                     .isEqualTo("The following constraints have failed: findPageWithIngredients.size: must be greater than 0");
    }


    @Test
    @WithMockUser(authorities = {Constants.ROLE_USER})
    public void findPageWithIngredients_whenNoResultsAreFound_thenEmptyPageIsReturned() {
        // Given
        int page = 0;
        int size = 1;

        // When
        when(mockPizzaService.findPageWithIngredients(anyInt(), anyInt(), any())).thenReturn(Page.empty());

        // Then
        webTestClient.get()
                     .uri(uriBuilder -> uriBuilder.path(RestRoutes.PIZZA.ROOT + RestRoutes.PIZZA.PAGE_WITH_INGREDIENTS)
                                                  .queryParam("page", page)
                                                  .queryParam("size", size)
                                                  .build())
                     .exchange()
                     .expectStatus().isOk()
                     .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                     .expectBody()
                     .jsonPath("$.content").isEqualTo(new ArrayList<>())
                     .jsonPath("$.numberOfElements").isEqualTo(0)
                     .jsonPath("$.totalPages").isEqualTo(1)
                     .jsonPath("$.totalElements").isEqualTo(0);
    }


    @Test
    @WithMockUser(authorities = {Constants.ROLE_USER})
    public void findPageWithIngredients_whenResultsAreFound_thenExpectedPageIsReturned() {
        // Given
        int page = 0;
        int size = 1;
        IngredientDto ingredientDto = buildIngredientDto(null, "Bacon");
        PizzaDto pizzaDto = buildPizzaDto(1, CARBONARA.getInternalPropertyValue(), 7D, Set.of(ingredientDto));

        // When
        when(mockPizzaService.findPageWithIngredients(anyInt(), anyInt(), any())).thenReturn(new PageImpl<>(Arrays.asList(pizzaDto)));

        // Then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(RestRoutes.PIZZA.ROOT + RestRoutes.PIZZA.PAGE_WITH_INGREDIENTS)
                                             .queryParam("page", page)
                                             .queryParam("size", size)
                                             .build())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("$.content.[0].id").isEqualTo(pizzaDto.getId())
                .jsonPath("$.content.[0].name").isEqualTo(pizzaDto.getName())
                .jsonPath("$.content.[0].cost").isEqualTo(pizzaDto.getCost())
                .jsonPath("$.content.[0].ingredients.[0].id").isEqualTo(ingredientDto.getId())
                .jsonPath("$.content.[0].ingredients.[0].name").isEqualTo(ingredientDto.getName())
                .jsonPath("$.numberOfElements").isEqualTo(1)
                .jsonPath("$.totalPages").isEqualTo(1)
                .jsonPath("$.totalElements").isEqualTo(1);
    }


    @Test
    public void update_whenNoLoggedUserIsGiven_thenUnauthorizedHttpCodeIsReturned() {
        // When/Then
        webTestClient.put()
                     .uri(RestRoutes.PIZZA.ROOT)
                     .body(Mono.just(new PizzaDto()), PizzaDto.class)
                     .exchange()
                     .expectStatus().isUnauthorized();
    }


    @Test
    @WithMockUser(authorities = {Constants.ROLE_USER})
    public void update_whenNotValidAuthorityIsGiven_thenForbiddenHttpCodeIsReturned() {
        // Given
        PizzaDto pizzaDto = buildPizzaDto(1, CARBONARA.getInternalPropertyValue(), 7D, Set.of());

        // When/Then
        webTestClient.put()
                     .uri(RestRoutes.PIZZA.ROOT)
                     .body(Mono.just(pizzaDto), PizzaDto.class)
                     .exchange()
                     .expectStatus().isForbidden();
    }


    @Test
    @WithMockUser(authorities = {Constants.ROLE_ADMIN})
    public void update_whenGivenDtoDoesNotVerifyTheValidations_thenNotFoundHttpCodeAndEmptyBodyAreReturned() {
        // Given
        PizzaDto pizzaDto = buildPizzaDto(1, CARBONARA.getInternalPropertyValue(), null, Set.of());

        // When/Then
        webTestClient.put()
                     .uri(RestRoutes.PIZZA.ROOT)
                     .body(Mono.just(pizzaDto), PizzaDto.class)
                     .exchange()
                     .expectStatus().isBadRequest()
                     .expectBody(String.class)
                     .isEqualTo("Error in the given parameters: [Field error in object 'pizzaDto' on field 'cost' due to: must not be null]");
    }


    @Test
    @WithMockUser(authorities = {Constants.ROLE_ADMIN})
    public void update_whenSaveDoesNotReturnAnEntity_thenUnprocessableEntityHttpCodeAndValidationErrorsAreReturned() {
        // Given
        PizzaDto pizzaDto = buildPizzaDto(1, CARBONARA.getInternalPropertyValue(), 7D, Set.of());

        // When
        when(mockPizzaService.save(any())).thenReturn(Optional.empty());

        // Then
        webTestClient.put()
                     .uri(RestRoutes.PIZZA.ROOT)
                     .body(Mono.just(pizzaDto), PizzaDto.class)
                     .exchange()
                     .expectStatus().isNotFound()
                     .expectBody().isEmpty();
    }


    @Test
    @WithMockUser(authorities = {Constants.ROLE_ADMIN})
    public void update_whenNotEmptyDtoIsGiven_thenOkHttpCodeAndPizzaDtoAreReturned() {
        // Given
        IngredientDto beforeIngredientDto = buildIngredientDto(null, "Cheese");
        IngredientDto afterIngredientDto = buildIngredientDto(1, beforeIngredientDto.getName());
        PizzaDto beforePizzaDto = buildPizzaDto(null, CARBONARA.getInternalPropertyValue(), 7D, Set.of(beforeIngredientDto));
        PizzaDto afterPizzaDto = buildPizzaDto(1, beforePizzaDto.getName(), beforePizzaDto.getCost(), Set.of(afterIngredientDto));

        // When
        when(mockPizzaService.save(beforePizzaDto)).thenReturn(Optional.of(afterPizzaDto));

        // Then
        webTestClient.put()
                     .uri(RestRoutes.PIZZA.ROOT)
                     .body(Mono.just(beforePizzaDto), PizzaDto.class)
                     .exchange()
                     .expectStatus().isOk()
                     .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                     .expectBody()
                     .jsonPath("$.id").isEqualTo(afterPizzaDto.getId())
                     .jsonPath("$.name").isEqualTo(afterPizzaDto.getName())
                     .jsonPath("$.cost").isEqualTo(afterPizzaDto.getCost())
                     .jsonPath("$.ingredients.[0].id").isEqualTo(afterIngredientDto.getId())
                     .jsonPath("$.ingredients.[0].name").isEqualTo(afterIngredientDto.getName());

        verify(mockPizzaService, times(1)).save(beforePizzaDto);
    }

}
