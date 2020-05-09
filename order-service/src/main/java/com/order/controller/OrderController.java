package com.order.controller;

import com.order.annotation.RoleAdmin;
import com.order.annotation.RoleAdminOrUser;
import com.order.configuration.rest.RestRoutes;
import com.order.dto.OrderDto;
import com.order.dto.OrderLineDto;
import com.order.model.Order;
import com.order.service.OrderService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
    @ApiOperation(value = "Create an order",
            notes = "Create an order (only allowed to user with role admin)",
            response = OrderDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The given order was successfully created", response = OrderDto.class),
            @ApiResponse(code = 400, message = "There was a problem in the given request, the given parameters have not passed the required validations"),
            @ApiResponse(code = 401, message = "The user has not authorization to execute this request"),
            @ApiResponse(code = 412, message = "The provided authorization information has expired"),
            @ApiResponse(code = 500, message = "There was an internal problem in the server")
    })
    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    @RoleAdmin
    public ResponseEntity<OrderDto> create(@RequestBody @Valid OrderDto orderDto) {
        return orderService.save(orderDto)
                   .map(p -> new ResponseEntity(p, CREATED))
                   .orElse(new ResponseEntity(UNPROCESSABLE_ENTITY));
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
    @ApiOperation(value = "Find order information matches given id",
            notes = "Find order information matches given id (only allowed to user with role admin/user)",
            response = OrderDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "There is an order with the given id", response = OrderDto.class),
            @ApiResponse(code = 400, message = "There was a problem in the given request, the given parameters have not passed the required validations"),
            @ApiResponse(code = 401, message = "The user has not authorization to execute this request"),
            @ApiResponse(code = 404, message = "There is no an order with the given id"),
            @ApiResponse(code = 412, message = "The provided authorization information has expired"),
            @ApiResponse(code = 500, message = "There was an internal problem in the server")
    })
    @GetMapping("/{id}" + RestRoutes.ORDER.WITH_ORDERLINES)
    @RoleAdminOrUser
    public ResponseEntity<OrderDto> findByIdWithOrderLines(@PathVariable @Positive Integer id) {
        return orderService.findByIdWithOrderLines(id)
                   .map(p -> new ResponseEntity(p, OK))
                   .orElse(new ResponseEntity(NOT_FOUND));
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
    @ApiOperation(value = "Update an order",
            notes = "Update an order (only allowed to user with role admin)",
            response = OrderDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "The given order was successfully update"),
            @ApiResponse(code = 400, message = "There was a problem in the given request, the given parameters have not passed the required validations"),
            @ApiResponse(code = 404, message = "There is no an order matches with provided information"),
            @ApiResponse(code = 401, message = "The user has not authorization to execute this request"),
            @ApiResponse(code = 412, message = "The provided authorization information has expired"),
            @ApiResponse(code = 500, message = "There was an internal problem in the server"),
    })
    @PutMapping
    @Transactional(rollbackFor = Exception.class)
    @RoleAdmin
    public ResponseEntity<OrderDto> update(@RequestBody @Valid OrderDto orderDto) {
        return orderService.save(orderDto)
                   .map(p -> new ResponseEntity(p, OK))
                   .orElse(new ResponseEntity(NOT_FOUND));
    }

}
