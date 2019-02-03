package com.pizza.controller;

import com.pizza.configuration.rest.RestRoutes;
import com.pizza.dto.PizzaDto;
import com.pizza.model.Ingredient;
import com.pizza.model.Pizza;
import com.pizza.service.PizzaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * Rest services to work with {@link Pizza}
 */
@RestController
@RequestMapping(RestRoutes.PIZZA.ROOT)
@CrossOrigin(origins="*")
public class PizzaController {

    private PizzaService pizzaService;

    @Autowired
    public PizzaController(@Lazy PizzaService pizzaService) {
        this.pizzaService = pizzaService;
    }


    /**
     * Used to create a new {@link PizzaDto}
     *
     * @param pizzaDto
     *    {@link PizzaDto} to create
     *
     * @return if pizzaDto is not {@code Null}: {@link HttpStatus#CREATED} and created {@link Pizza}
     *         if pizzaDto is {@code Null}: {@link HttpStatus#UNPROCESSABLE_ENTITY} and {@code Null}
     */
    @PostMapping
    public Mono<ResponseEntity<PizzaDto>> create(@RequestBody PizzaDto pizzaDto) {
        return Mono.just(pizzaService.save(pizzaDto)
                                     .map(p -> new ResponseEntity(p, HttpStatus.CREATED))
                                     .orElse(new ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY)));
    }


    /**
     * Returns the {@link Pizza} which name matches with the given one.
     *
     * @param name
     *    Name to search in the current {@link Pizza#name}s
     *
     * @return if name was found: {@link HttpStatus#OK} and {@link PizzaDto} that matches
     *         if name was not found: {@link HttpStatus#NOT_FOUND}
     */
    @GetMapping("/{name}")
    public Mono<ResponseEntity<PizzaDto>> findByName(@PathVariable String name) {
        return Mono.just(pizzaService.findByName(name)
                                     .map(p -> new ResponseEntity(p, HttpStatus.OK))
                                     .orElse(new ResponseEntity(HttpStatus.NOT_FOUND)));
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
    @GetMapping(RestRoutes.PIZZA.PAGE_WITH_INGREDIENTS)
    public Mono<Page<PizzaDto>> findPageWithIngredients(@RequestParam(value = "page") int page,
                                                        @RequestParam(value = "size") int size) {
        return Mono.just(pizzaService.findPageWithIngredients(page, size, null));
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
    @PutMapping
    public Mono<ResponseEntity<PizzaDto>> update(@RequestBody PizzaDto pizzaDto) {
        return Mono.just(pizzaService.save(pizzaDto)
                   .map(p -> new ResponseEntity(p, HttpStatus.OK))
                   .orElse(new ResponseEntity(HttpStatus.NOT_FOUND)));
    }

}
