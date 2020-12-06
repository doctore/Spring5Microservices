package com.pizza.util.converter.enums;

import com.pizza.enums.PizzaEnum;
import com.spring5microservices.common.converter.enums.BaseEnumConverter;
import org.mapstruct.Mapper;

import static java.util.Optional.ofNullable;

/**
 * Utility class to convert from {@link PizzaEnum} to "equivalent" {@link String} and vice versa.
 */
@Mapper
public class PizzaEnumConverter implements BaseEnumConverter<PizzaEnum, String> {

    @Override
    public PizzaEnum fromValueToEnum(String value) {
        return PizzaEnum.getFromDatabaseValue(value)
                        .orElse(null);
    }

    @Override
    public String fromEnumToValue(PizzaEnum enumValue) {
        return ofNullable(enumValue)
                .map(PizzaEnum::getInternalPropertyValue)
                .orElse(null);
    }
}
