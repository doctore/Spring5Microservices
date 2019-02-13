package com.pizza.util.converter;

import com.pizza.dto.IngredientDto;
import com.pizza.model.Ingredient;
import org.mapstruct.Mapper;

@Mapper
public interface IngredientConverter extends BaseConverter<Ingredient, IngredientDto> {}
