package com.pizza.util.converter;

import com.pizza.dto.IngredientDto;
import com.pizza.model.Ingredient;
import com.spring5microservices.common.converter.BaseConverter;
import org.mapstruct.Mapper;

/**
 * Utility class to convert from {@link Ingredient} to {@link IngredientDto} and vice versa.
 */
@Mapper
public interface IngredientConverter extends BaseConverter<Ingredient, IngredientDto> {}
