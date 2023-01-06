package com.pizza.controller;

import com.pizza.PizzaServiceApplication;
import com.pizza.configuration.Constants;
import com.pizza.configuration.rest.RestRoutes;
import com.pizza.dto.IngredientDto;
import com.pizza.dto.PizzaDto;
import com.pizza.enums.PizzaEnum;
import com.pizza.model.Ingredient;
import com.pizza.model.Pizza;
import com.pizza.service.PizzaService;
import com.spring5microservices.common.dto.ErrorResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.pizza.TestDataFactory.buildIngredient;
import static com.pizza.TestDataFactory.buildIngredientDto;
import static com.pizza.TestDataFactory.buildPizza;
import static com.pizza.TestDataFactory.buildPizzaDto;
import static com.pizza.enums.PizzaEnum.CARBONARA;
import static com.spring5microservices.common.enums.RestApiErrorCode.VALIDATION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static java.util.Optional.of;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@SpringBootTest(classes = PizzaServiceApplication.class)
public class PizzaControllerTest extends BaseControllerTest {

    @MockBean
    private PizzaService mockPizzaService;

    private WebTestClient webTestClient;


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


    static Stream<Arguments> createWhenDtoDoesNotVerifyValidationsTestCases() {
        String validPizzaNames = Arrays.stream(PizzaEnum.values())
                .map(PizzaEnum::getInternalPropertyValue)
                .collect(Collectors.joining(", "));
        PizzaDto dto1 = buildPizzaDto(1, "Not existing", 7D, Set.of());
        PizzaDto dto2 = buildPizzaDto(null, CARBONARA.getInternalPropertyValue(), null, Set.of());

        ErrorResponseDto response1 = new ErrorResponseDto(VALIDATION,
                List.of("Field error in object 'pizzaDto' on field 'name' due to: must be one of the "
                      + "values included in [" + validPizzaNames + "]"));
        ErrorResponseDto response2 = new ErrorResponseDto(VALIDATION,
                List.of("Field error in object 'pizzaDto' on field 'cost' due to: must not be null"));
        return Stream.of(
                //@formatter:off
                //            dtoToCreate,   expectedResponse
                Arguments.of( dto1,          response1 ),
                Arguments.of( dto2,          response2 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("createWhenDtoDoesNotVerifyValidationsTestCases")
    @DisplayName("create: when dto does not verify validations")
    @WithMockUser(authorities = {Constants.ROLE_ADMIN})
    public void create_whenGivenDtoDoesNotVerifyTheValidations_thenBadRequestHttpCodeAndValidationErrorsAreReturned(PizzaDto dtoToCreate,
                                                                                                                    ErrorResponseDto expectedResponse) {

        webTestClient.post()
                .uri(RestRoutes.PIZZA.ROOT)
                .body(Mono.just(dtoToCreate), PizzaDto.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponseDto.class)
                .isEqualTo(expectedResponse);
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
                .expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
                .expectBody().isEmpty();
    }


    @Test
    @WithMockUser(authorities = {Constants.ROLE_ADMIN})
    public void create_whenNotEmptyDtoIsGiven_thenCreatedHttpCodeAndPizzaDtoAreReturned() {
        // Given
        PizzaEnum pizzaName = CARBONARA;
        IngredientDto beforeIngredientDto = buildIngredientDto(null, "Cheese");
        Ingredient beforeIngredient = buildIngredient(beforeIngredientDto.getId(), beforeIngredientDto.getName());
        IngredientDto afterIngredientDto = buildIngredientDto(1, beforeIngredientDto.getName());
        Ingredient afterIngredient = buildIngredient(afterIngredientDto.getId(), afterIngredientDto.getName());

        PizzaDto beforePizzaDto = buildPizzaDto(null, pizzaName.getInternalPropertyValue(), 7D, Set.of(beforeIngredientDto));
        Pizza beforePizza = buildPizza(beforePizzaDto.getId(), pizzaName, 7D, Set.of(beforeIngredient));
        PizzaDto afterPizzaDto = buildPizzaDto(1, beforePizzaDto.getName(), beforePizzaDto.getCost(), Set.of(afterIngredientDto));
        Pizza afterPizza = buildPizza(afterPizzaDto.getId(), pizzaName, afterPizzaDto.getCost(), Set.of(afterIngredient));

        // When
        when(mockPizzaService.save(beforePizza)).thenReturn(Optional.of(afterPizza));

        // Then
        webTestClient.post()
                .uri(RestRoutes.PIZZA.ROOT)
                .body(Mono.just(beforePizzaDto), PizzaDto.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
                .expectBody(PizzaDto.class)
                .isEqualTo(afterPizzaDto);

        verify(mockPizzaService, times(1)).save(beforePizza);
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
        ErrorResponseDto expectedResponse = new ErrorResponseDto(VALIDATION, List.of("name: size must be between 1 and 64"));

        // When/Then
        webTestClient.get()
                .uri(RestRoutes.PIZZA.ROOT + "/" + notValidPizzaName)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponseDto.class)
                .isEqualTo(expectedResponse);
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
        PizzaEnum pizzaName = CARBONARA;
        IngredientDto ingredientDto = buildIngredientDto(null, "Bacon");
        Ingredient ingredient = buildIngredient(ingredientDto.getId(), ingredientDto.getName());
        PizzaDto pizzaDto = buildPizzaDto(1, pizzaName.getInternalPropertyValue(), 7D, Set.of(ingredientDto));
        Pizza pizza = buildPizza(pizzaDto.getId(), pizzaName, pizzaDto.getCost(), Set.of(ingredient));

        // When
        when(mockPizzaService.findByName(anyString())).thenReturn(of(pizza));

        // Then
        webTestClient.get()
                .uri(RestRoutes.PIZZA.ROOT + "/" + pizzaDto.getName())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
                .expectBody(PizzaDto.class)
                .isEqualTo(pizzaDto);
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


    static Stream<Arguments> findPageWithIngredientsWhenParametersNotVerifyValidationsTestCases() {
        ErrorResponseDto response1 = new ErrorResponseDto(VALIDATION, List.of("page: must be greater than or equal to 0"));
        ErrorResponseDto response2 = new ErrorResponseDto(VALIDATION, List.of("size: must be greater than 0"));
        return Stream.of(
                //@formatter:off
                //            page,   size,   expectedResponse
                Arguments.of( -1,     1,      response1 ),
                Arguments.of( 0,      0,      response2 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("findPageWithIngredientsWhenParametersNotVerifyValidationsTestCases")
    @DisplayName("findPageWithIngredients: when parameters do not verify validations")
    @WithMockUser(authorities = {Constants.ROLE_ADMIN})
    public void findPageWithIngredients_whenGivenParametersDoNotVerifyTheValidations_thenBadRequestHttpCodeAndValidationErrorsAreReturned(int page,
                                                                                                                                          int size,
                                                                                                                                          ErrorResponseDto expectedResponse) {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(RestRoutes.PIZZA.ROOT + RestRoutes.PIZZA.PAGE_WITH_INGREDIENTS)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponseDto.class)
                .isEqualTo(expectedResponse);
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
                .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
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
        PizzaEnum pizzaName = CARBONARA;
        IngredientDto ingredientDto = buildIngredientDto(null, "Bacon");
        Ingredient ingredient = buildIngredient(ingredientDto.getId(), ingredientDto.getName());
        PizzaDto pizzaDto = buildPizzaDto(1, pizzaName.getInternalPropertyValue(), 7D, Set.of(ingredientDto));
        Pizza pizza = buildPizza(pizzaDto.getId(), pizzaName, pizzaDto.getCost(), Set.of(ingredient));

        // When
        when(mockPizzaService.findPageWithIngredients(anyInt(), anyInt(), any())).thenReturn(new PageImpl<>(List.of(pizza)));

        // Then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(RestRoutes.PIZZA.ROOT + RestRoutes.PIZZA.PAGE_WITH_INGREDIENTS)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
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


    static Stream<Arguments> updateWhenDtoDoesNotVerifyValidationsTestCases() {
        String validPizzaNames = Arrays.stream(PizzaEnum.values())
                .map(PizzaEnum::getInternalPropertyValue)
                .collect(Collectors.joining(", "));
        PizzaDto dto1 = buildPizzaDto(1, "Not existing", 7D, Set.of());
        PizzaDto dto2 = buildPizzaDto(null, CARBONARA.getInternalPropertyValue(), null, Set.of());
        ErrorResponseDto response1 = new ErrorResponseDto(VALIDATION,
                List.of("Field error in object 'pizzaDto' on field 'name' due to: must be one of the "
                      + "values included in [" + validPizzaNames + "]"));
        ErrorResponseDto response2 = new ErrorResponseDto(VALIDATION,
                List.of("Field error in object 'pizzaDto' on field 'cost' due to: must not be null"));
        return Stream.of(
                //@formatter:off
                //            dtoToUpdate,   expectedResponse
                Arguments.of( dto1,          response1 ),
                Arguments.of( dto2,          response2 )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("updateWhenDtoDoesNotVerifyValidationsTestCases")
    @DisplayName("update: when dto does not verify validations")
    @WithMockUser(authorities = {Constants.ROLE_ADMIN})
    public void update_whenGivenDtoDoesNotVerifyTheValidations_thenBadRequestHttpCodeAndValidationErrorsAreReturned(PizzaDto dtoToUpdate,
                                                                                                                    ErrorResponseDto expectedResponse) {
        webTestClient.put()
                .uri(RestRoutes.PIZZA.ROOT)
                .body(Mono.just(dtoToUpdate), PizzaDto.class)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponseDto.class)
                .isEqualTo(expectedResponse);
    }


    @Test
    @WithMockUser(authorities = {Constants.ROLE_ADMIN})
    public void update_whenSaveDoesNotReturnAnEntity_thenNotFoundHttpCodeAndValidationErrorsAreReturned() {
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
        PizzaEnum pizzaName = CARBONARA;
        IngredientDto beforeIngredientDto = buildIngredientDto(null, "Cheese");
        Ingredient beforeIngredient = buildIngredient(beforeIngredientDto.getId(), beforeIngredientDto.getName());
        IngredientDto afterIngredientDto = buildIngredientDto(1, beforeIngredientDto.getName());
        Ingredient afterIngredient = buildIngredient(afterIngredientDto.getId(), afterIngredientDto.getName());

        PizzaDto beforePizzaDto = buildPizzaDto(null, pizzaName.getInternalPropertyValue(), 7D, Set.of(beforeIngredientDto));
        Pizza beforePizza = buildPizza(beforePizzaDto.getId(), pizzaName, 7D, Set.of(beforeIngredient));
        PizzaDto afterPizzaDto = buildPizzaDto(1, beforePizzaDto.getName(), beforePizzaDto.getCost(), Set.of(afterIngredientDto));
        Pizza afterPizza = buildPizza(afterPizzaDto.getId(), pizzaName, afterPizzaDto.getCost(), Set.of(afterIngredient));

        // When
        when(mockPizzaService.save(beforePizza)).thenReturn(of(afterPizza));

        // Then
        webTestClient.put()
                .uri(RestRoutes.PIZZA.ROOT)
                .body(Mono.just(beforePizzaDto), PizzaDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_VALUE)
                .expectBody(PizzaDto.class)
                .isEqualTo(afterPizzaDto);

        verify(mockPizzaService, times(1)).save(beforePizza);
    }

}
