package com.pizza.controller;

import com.pizza.annotation.RoleAdmin;
import com.pizza.annotation.RoleAdminOrUser;
import com.pizza.configuration.rest.RestRoutes;
import com.pizza.dto.PizzaDto;
import com.pizza.model.Ingredient;
import com.pizza.model.Pizza;
import com.pizza.service.PizzaService;
import lombok.AllArgsConstructor;
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

/**
 * Rest services to work with {@link Pizza}
 */
@AllArgsConstructor
@RestController
@RequestMapping(RestRoutes.PIZZA.ROOT)
@Validated
public class PizzaController {

    @Lazy
    private final PizzaService pizzaService;


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
    @Transactional(rollbackFor = Exception.class)
    @RoleAdmin
    public Mono<ResponseEntity<PizzaDto>> create(@RequestBody @Valid PizzaDto pizzaDto) {
        return Mono.just(pizzaService.save(pizzaDto)
                                     .map(p -> new ResponseEntity(p, HttpStatus.CREATED))
                                     .orElse(new ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY)));
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
    @GetMapping("/{name}")
    @RoleAdminOrUser
    public Mono<ResponseEntity<PizzaDto>> findByName(@PathVariable @Size(min=1, max=64) String name) {
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
    @RoleAdminOrUser
    public Mono<Page<PizzaDto>> findPageWithIngredients(@RequestParam(value = "page") @PositiveOrZero int page,
                                                        @RequestParam(value = "size") @Positive int size) {
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
    @Transactional(rollbackFor = Exception.class)
    @RoleAdmin
    public Mono<ResponseEntity<PizzaDto>> update(@RequestBody @Valid PizzaDto pizzaDto) {
        return Mono.just(pizzaService.save(pizzaDto)
                   .map(p -> new ResponseEntity(p, HttpStatus.OK))
                   .orElse(new ResponseEntity(HttpStatus.NOT_FOUND)));
    }

}
