package com.order.util.converter;

import com.order.dto.PizzaDto;
import com.order.model.Pizza;
import com.spring5microservices.common.converter.BaseConverter;
import org.mapstruct.Mapper;

/**
 * Utility class to convert from {@link Pizza} to {@link PizzaDto} and vice versa.
 */
@Mapper
public interface PizzaConverter extends BaseConverter<Pizza, PizzaDto> {}
