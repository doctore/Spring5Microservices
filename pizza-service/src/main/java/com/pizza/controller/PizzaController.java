package com.pizza.controller;

import com.pizza.annotation.RoleAdmin;
import com.pizza.annotation.RoleAdminOrUser;
import com.pizza.configuration.rest.RestRoutes;
import com.pizza.dto.PizzaDto;
import com.pizza.model.Ingredient;
import com.pizza.model.Pizza;
import com.pizza.service.PizzaService;
import com.pizza.util.converter.PizzaConverter;
import com.spring5microservices.common.dto.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Rest services to work with {@link Pizza}
 */
@AllArgsConstructor
@Log4j2
@RestController
@RequestMapping(RestRoutes.PIZZA.ROOT)
@Validated
@Tag(name = "PizzaController", description = "Endpoints to manage operations related with pizzas")
public class PizzaController {

    @Lazy
    private final PizzaService service;

    @Lazy
    private final PizzaConverter converter;


    /**
     * Used to create a new {@link PizzaDto}
     *
     * @param pizzaDto
     *    {@link PizzaDto} to create
     *
     * @return if pizzaDto is not {@code Null}: {@link HttpStatus#CREATED} and created {@link Pizza}
     *         if pizzaDto is {@code Null}: {@link HttpStatus#UNPROCESSABLE_ENTITY} and {@code Null}
     */
    @Operation(
            summary = "Create a pizza",
            description = "Create a pizza (only allowed to user with role admin)"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "The given pizza was successfully created",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = PizzaDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "There was a problem in the given request, the given parameters have not passed the required validations",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "The user has not authorization to execute this request or provided authorization has expired",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "The pizza could not be created"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "There was an internal problem in the server",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    )
            }
    )
    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    @RoleAdmin
    public Mono<ResponseEntity<PizzaDto>> create(@RequestBody @Valid final PizzaDto pizzaDto) {
        log.info(
                format("Creating the pizza: %s",
                        pizzaDto)
        );
        return Mono.just(
                service.save(
                            converter.fromDtoToModel(pizzaDto)
                        )
                        .map(converter::fromModelToDto)
                        .map(p ->
                                new ResponseEntity<>(
                                        p,
                                        CREATED
                                )
                        )
                        .orElseGet(() ->
                                new ResponseEntity<>(UNPROCESSABLE_ENTITY)
                        )
        );
    }


    /**
     * Returns the {@link Pizza} which name matches with the given one.
     *
     * @param name
     *    Name to search in the current {@link Pizza#getName()}s
     *
     * @return if name was found: {@link HttpStatus#OK} and {@link PizzaDto} that matches
     *         if name was not found: {@link HttpStatus#NOT_FOUND}
     */
    @Operation(
            summary = "Find pizza information matches given name",
            description = "Find pizza information matches given name (only allowed to user with role admin/user)"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "There is a pizza with the given name",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = PizzaDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "There was a problem in the given request, the given parameters have not passed the required validations",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "The user has not authorization to execute this request or provided authorization has expired",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "There is no a pizza with the given name"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "There was an internal problem in the server",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    )
            }
    )
    @GetMapping("/{name}")
    @RoleAdminOrUser
    public Mono<ResponseEntity<PizzaDto>> findByName(@PathVariable @Size(min=1, max=64) final String name) {
        log.info(
                format("Searching the pizza with name: %s",
                        name)
        );
        return Mono.just(
                service.findByName(name)
                        .map(converter::fromModelToDto)
                        .map(p ->
                                new ResponseEntity<>(
                                        p,
                                        OK
                                )
                )
                .orElseGet(() ->
                        new ResponseEntity<>(NOT_FOUND)
                )
        );
    }


    /**
     * Returns the required page information about {@link Pizza}s with their {@link Ingredient}s
     *
     * @param page
     *    Number of page to get
     * @param size
     *    Number of elements in every page
     *
     * @return {@link Page} of {@link PizzaDto}
     */
    @Operation(
            summary = "Get list of pizzas with their ingredients",
            description = "Get list of pizzas with their ingredients (only allowed to user with role admin/user)"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "The list of existing pizzas",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(type = "List", implementation = PizzaDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "There was a problem in the given request, the given parameters have not passed the required validations",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "The user has not authorization to execute this request or provided authorization has expired",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "There was an internal problem in the server",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    )
            }
    )
    @GetMapping(RestRoutes.PIZZA.PAGE_WITH_INGREDIENTS)
    @RoleAdminOrUser
    public Mono<Page<PizzaDto>> findPageWithIngredients(@RequestParam(value = "page") @PositiveOrZero final int page,
                                                        @RequestParam(value = "size") @Positive final int size) {
        log.info(
                format("Returning the page of pizzas related with page: %d and size: %d",
                        page, size)
        );
        return Mono.just(
                service.findPageWithIngredients(
                        page,
                        size,
                        null
                )
                .map(converter::fromModelToDto)
        );
    }


    /**
     * Used to update an existing {@link PizzaDto}
     *
     * @param pizzaDto
     *    {@link PizzaDto} to update
     *
     * @return if pizza is not {@code Null} and exists: {@link HttpStatus#OK} and updated {@link Pizza}
     *         if pizza is {@code Null} or not exists: {@link HttpStatus#NOT_FOUND} and {@code Null}
     */
    @Operation(
            summary = "Update a pizza",
            description = "Update a pizza (only allowed to user with role admin)"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "The given pizza was successfully update",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = PizzaDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "There was a problem in the given request, the given parameters have not passed the required validations",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "The user has not authorization to execute this request or provided authorization has expired",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "There is no a pizza matches with provided information"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "There was an internal problem in the server",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponseDto.class)
                            )
                    ),
            }
    )
    @PutMapping
    @Transactional(rollbackFor = Exception.class)
    @RoleAdmin
    public Mono<ResponseEntity<PizzaDto>> update(@RequestBody @Valid final PizzaDto pizzaDto) {
        log.info(
                format("Updating the pizza: %s",
                        pizzaDto)
        );
        return Mono.just(
                service.save(
                            converter.fromDtoToModel(pizzaDto)
                        )
                        .map(converter::fromModelToDto)
                        .map(p ->
                                new ResponseEntity<>(
                                        p,
                                        OK
                                )
                        )
                        .orElseGet(() ->
                                new ResponseEntity<>(NOT_FOUND)
                        )
        );
    }

}
