package com.order.controller;

import com.order.configuration.rest.RestRoutes;
import com.order.dto.OrderDto;
import com.order.dto.OrderLineDto;
import com.order.model.Order;
import com.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

/**
 * Rest services to work with {@link Order}
 */
@RestController
@RequestMapping(RestRoutes.ORDER.ROOT)
@CrossOrigin(origins="*")
@Validated
public class OrderController {

    private OrderService orderService;


    @Autowired
    public OrderController (@Lazy OrderService orderService) {
        this.orderService = orderService;
    }


    /**
     * Used to create a new {@link OrderDto}
     *
     * @param orderDto
     *    {@link OrderDto} to create
     *
     * @return if orderDto is not {@code Null}: {@link HttpStatus#CREATED} and created {@link OrderDto}
     *         if orderDto is {@code Null}: {@link HttpStatus#UNPROCESSABLE_ENTITY} and {@code Null}
     */
    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public Mono<ResponseEntity<OrderDto>> create(@RequestBody @Valid OrderDto orderDto) {
        return Mono.just(orderService.save(orderDto)
                   .map(p -> new ResponseEntity(p, HttpStatus.CREATED))
                   .orElse(new ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY)));
    }


    /**
     * Return the {@link OrderDto} and its {@link OrderLineDto} information of the given {@link OrderDto#id}.
     *
     * @param id
     *    {@link Order#id} to find
     *
     * @return if id was found: {@link HttpStatus#OK} and {@link OrderDto} that matches
     *         if id was not found: {@link HttpStatus#NOT_FOUND}
     */
    @GetMapping("/{id}" + RestRoutes.ORDER.WITH_ORDERLINES)
    public Mono<ResponseEntity<OrderDto>> findByIdWithOrderLines(@PathVariable @Positive Integer id) {
        return Mono.just(orderService.findByIdWithOrderLines(id)
                   .map(p -> new ResponseEntity(p, HttpStatus.OK))
                   .orElse(new ResponseEntity(HttpStatus.NOT_FOUND)));
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
    @PutMapping
    @Transactional(rollbackFor = Exception.class)
    public Mono<ResponseEntity<OrderDto>> update(@RequestBody @Valid OrderDto orderDto) {
        return Mono.just(orderService.save(orderDto)
                   .map(p -> new ResponseEntity(p, HttpStatus.OK))
                   .orElse(new ResponseEntity(HttpStatus.NOT_FOUND)));
    }

}
