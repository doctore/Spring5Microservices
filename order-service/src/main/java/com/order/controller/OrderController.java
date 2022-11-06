package com.order.controller;

import com.order.annotation.RoleAdmin;
import com.order.annotation.RoleAdminOrUser;
import com.order.configuration.rest.RestRoutes;
import com.order.dto.OrderDto;
import com.order.dto.OrderLineDto;
import com.order.model.Order;
import com.order.service.OrderService;
import com.spring5microservices.common.dto.ErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
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
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Rest services to work with {@link Order}
 */
@AllArgsConstructor
@RestController
@RequestMapping(RestRoutes.ORDER.ROOT)
@Validated
public class OrderController {

    @Lazy
    private final OrderService orderService;


    /**
     * Used to create a new {@link OrderDto}
     *
     * @param orderDto
     *    {@link OrderDto} to create
     *
     * @return if orderDto is not {@code Null}: {@link HttpStatus#CREATED} and created {@link OrderDto}
     *         if orderDto is {@code Null}: {@link HttpStatus#UNPROCESSABLE_ENTITY} and {@code Null}
     */
    @Operation(
            summary = "Create an order",
            description = "Create an order (only allowed to user with role admin)"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "The given order was successfully created",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = OrderDto.class)
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
                            description = "The order could not be created"
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
    public ResponseEntity<OrderDto> create(@RequestBody @Valid final OrderDto orderDto) {
        return orderService.save(orderDto)
                   .map(p -> new ResponseEntity<>(p, CREATED))
                   .orElseGet(() -> new ResponseEntity<>(UNPROCESSABLE_ENTITY));
    }


    /**
     * Return the {@link OrderDto} and its {@link OrderLineDto} information of the given {@link OrderDto#getId()}}.
     *
     * @param id
     *    {@link Order#getId()}  to find
     *
     * @return if id was found: {@link HttpStatus#OK} and {@link OrderDto} that matches
     *         if id was not found: {@link HttpStatus#NOT_FOUND}
     */
    @Operation(
            summary = "Find order information matches given id",
            description = "Find order information matches given id (only allowed to user with role admin/user)"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "There is an order with the given id",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = OrderDto.class)
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
                            description = "There is no an order with the given id"
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
    @GetMapping("/{id}" + RestRoutes.ORDER.WITH_ORDERLINES)
    @RoleAdminOrUser
    public ResponseEntity<OrderDto> findByIdWithOrderLines(@PathVariable @Positive final Integer id) {
        return orderService.findByIdWithOrderLines(id)
                   .map(p -> new ResponseEntity<>(p, OK))
                   .orElseGet(() -> new ResponseEntity<>(NOT_FOUND));
    }


    /**
     * Used to update an existing {@link OrderDto}
     *
     * @param orderDto
     *    {@link OrderDto} to update
     *
     * @return if orderDto is not {@code Null} and exists: {@link HttpStatus#OK} and updated {@link OrderDto}
     *         if orderDto is {@code Null} or not exists: {@link HttpStatus#NOT_FOUND} and {@code Null}
     */
    @Operation(
            summary = "Update an order",
            description = "Update an order (only allowed to user with role admin)"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "The given order was successfully update",
                            content = @Content(
                                    mediaType = APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = OrderDto.class)
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
                            description = "There is no a order matches with provided information"
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
    public ResponseEntity<OrderDto> update(@RequestBody @Valid final OrderDto orderDto) {
        return orderService.save(orderDto)
                   .map(p -> new ResponseEntity<>(p, OK))
                   .orElseGet(() -> new ResponseEntity<>(NOT_FOUND));
    }

}
