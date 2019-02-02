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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Rest services to work with {@link Order}
 */
@RestController
@RequestMapping(RestRoutes.ORDER.ROOT)
@CrossOrigin(origins="*")
public class OrderController {

    private OrderService orderService;


    @Autowired
    public OrderController (@Lazy OrderService orderService) {
        this.orderService = orderService;
    }


    /**
     * Return the {@link OrderDto} and its {@link OrderLineDto} information of the given {@link OrderDto#id}.
     *
     * @param id
     *    {@link Order#id} to find
     *
     * @return if name was found: {@link HttpStatus#OK} and {@link OrderDto} that matches
     *         if name was not found: {@link HttpStatus#NOT_FOUND}
     */
    @GetMapping("/{id}" + RestRoutes.ORDER.WITH_ORDERLINES)
    public Mono<ResponseEntity<OrderDto>> findByIdWithOrderLines(@PathVariable Integer id) {
        return Mono.just(orderService.findByIdWithOrderLines(id)
                   .map(p -> new ResponseEntity(p, HttpStatus.OK))
                   .orElse(new ResponseEntity(HttpStatus.NOT_FOUND)));
    }

}
