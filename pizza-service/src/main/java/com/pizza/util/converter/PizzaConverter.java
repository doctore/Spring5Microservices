package com.pizza.util.converter;

import com.pizza.dto.PizzaDto;
import com.pizza.model.Pizza;
import com.pizza.util.converter.enums.PizzaEnumConverter;
import com.spring5microservices.common.converter.BaseConverter;
import org.mapstruct.Mapper;

/**
 * Utility class to convert from {@link Pizza} to {@link PizzaDto} and vice versa.
 */
@Mapper(uses={IngredientConverter.class, PizzaEnumConverter.class})
public interface PizzaConverter extends BaseConverter<Pizza, PizzaDto> {}
