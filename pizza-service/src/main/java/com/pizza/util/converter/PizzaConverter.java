package com.pizza.util.converter;

import com.pizza.dto.PizzaDto;
import com.pizza.model.Pizza;
import org.mapstruct.Mapper;

@Mapper(uses={IngredientConverter.class})
public interface PizzaConverter extends BaseConverter<Pizza, PizzaDto> {}
