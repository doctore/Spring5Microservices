package com.pizza.service;

import com.pizza.dto.PizzaDto;
import com.pizza.enums.PizzaEnum;
import com.pizza.model.Ingredient;
import com.pizza.model.Pizza;
import com.pizza.repository.IngredientRepository;
import com.pizza.repository.PizzaRepository;
import com.pizza.util.PageUtil;
import com.pizza.util.converter.PizzaConverter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@AllArgsConstructor
@Service
public class PizzaService {

    @Lazy
    private final IngredientRepository ingredientRepository;

    @Lazy
    private final PizzaConverter pizzaConverter;

    @Lazy
    private final PizzaRepository pizzaRepository;


    /**
     * Returns the {@link PizzaDto} which name matches with the given one.
     *
     * @param name
     *    Name to search in the current {@link Pizza#name}s
     *
     * @return {@link Optional} of {@link PizzaDto}
     */
    public Optional<PizzaDto> findByName(String name) {
        return ofNullable(name)
                .flatMap(PizzaEnum::getFromDatabaseValue)
                .flatMap(pizzaRepository::findWithIngredientsByName)
                .flatMap(pizzaConverter::fromModelToOptionalDto);
    }


    /**
     * Returns the required page information about {@link Pizza}s with their {@link Ingredient}s
     *
     * @param page
     *    Number of page to get
     * @param size
     *    Number of elements in every page
     * @param sort
     *    {@link Sort} with how we want to sort the returned results
     *
     * @return {@link Page} of {@link Pizza}
     */
    public Page<PizzaDto> findPageWithIngredients(int page, int size, Sort sort) {
        Page<Pizza> pizzaPage = pizzaRepository.findPageWithIngredientsWithoutInMemoryPagination(
                                                   PageUtil.buildPageRequest(page,size,sort));
        return ofNullable(pizzaPage)
                .map(p -> new PageImpl(pizzaConverter.fromModelsToDtos(p.getContent())
                        ,pizzaPage.getPageable()
                        ,pizzaPage.getTotalElements()))
                .orElseGet(() -> new PageImpl<PizzaDto>(new ArrayList()));
    }


    /**
     * Persist the information included in the given {@link PizzaDto}
     *
     * @param pizzaDto
     *    {@link PizzaDto} to save
     *
     * @return {@link Optional} of {@link Pizza} with its "final information" after this action
     */
    public Optional<PizzaDto> save(PizzaDto pizzaDto) {
        return ofNullable(pizzaDto)
                .flatMap(pizzaConverter::fromDtoToOptionalModel)
                .map(p -> {
                    if (null != p.getIngredients()) {
                        ingredientRepository.saveAll(p.getIngredients());
                    }
                    return pizzaRepository.save(p);
                })
                .flatMap(pizzaConverter::fromModelToOptionalDto);
    }

}
