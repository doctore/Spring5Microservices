package com.pizza.service;

import com.pizza.dto.PizzaDto;
import com.pizza.model.Ingredient;
import com.pizza.model.Pizza;
import com.pizza.repository.IngredientRepository;
import com.pizza.repository.PizzaRepository;
import com.pizza.util.PageUtil;
import com.pizza.util.converter.PizzaConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PizzaService {

    private IngredientRepository ingredientRepository;
    private PageUtil pageUtil;
    private PizzaConverter pizzaConverter;
    private PizzaRepository pizzaRepository;


    @Autowired
    public PizzaService(@Lazy IngredientRepository ingredientRepository, @Lazy PageUtil pageUtil,
                        @Lazy PizzaConverter pizzaConverter, @Lazy PizzaRepository pizzaRepository) {
        this.ingredientRepository = ingredientRepository;
        this.pageUtil = pageUtil;
        this.pizzaConverter = pizzaConverter;
        this.pizzaRepository = pizzaRepository;
    }


    /**
     * Returns the {@link PizzaDto} which name matches with the given one.
     *
     * @param name
     *    Name to search in the current {@link Pizza#name}s
     *
     * @return {@link Optional} of {@link PizzaDto}
     */
    public Optional<PizzaDto> findByName(String name) {
        return Optional.ofNullable(name)
                       .flatMap(pizzaRepository::findWithIngredientsByName)
                       .flatMap(pizzaConverter::fromEntityToOptionalDto);
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
                                                   pageUtil.buildPageRequest(page,size,sort));
        return Optional.ofNullable(pizzaPage)
                       .map(p -> new PageImpl((List)pizzaConverter.fromEntitiesToDtos(p.getContent())
                                              ,pizzaPage.getPageable()
                                              ,pizzaPage.getTotalElements()))
                       .orElse(new PageImpl<PizzaDto>(new ArrayList()));
    }


    /**
     * Persist the information included in the given {@link PizzaDto}
     *
     * @param pizzaDto
     *    {@link PizzaDto} to save
     *
     * @return {@link Optional} of {@link Pizza} with its "final information" after this action
     */
    @Transactional
    public Optional<PizzaDto> save(PizzaDto pizzaDto) {
        return Optional.ofNullable(pizzaDto)
                       .flatMap(pizzaConverter::fromDtoToOptionalEntity)
                       .map(p -> {
                           ingredientRepository.saveAll(p.getIngredients());
                           return pizzaRepository.save(p);
                       })
                       .flatMap(pizzaConverter::fromEntityToOptionalDto);
    }

}
