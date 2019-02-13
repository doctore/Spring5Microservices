package com.pizza.util.converter;

import com.pizza.dto.PizzaDto;
import com.pizza.model.Pizza;
import org.mapstruct.Mapper;

/**
 * Utility class to convert from {@link Pizza} to {@link PizzaDto} and vice versa.
 */
@Mapper(uses={IngredientConverter.class})
public interface PizzaConverter extends BaseConverter<Pizza, PizzaDto> {}
