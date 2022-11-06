package com.pizza.util.converter.enums;

import com.pizza.enums.PizzaEnum;
import com.pizza.model.Pizza;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import static java.util.Optional.ofNullable;

/**
 *    Class used to convert from/to the database value stored in the column {@code name} of the {@code pizza}
 * table from/to {@link Pizza#getName()}
 */
@Converter
public class PizzaEnumDatabaseConverter implements AttributeConverter<PizzaEnum, String> {

    @Override
    public String convertToDatabaseColumn(final PizzaEnum pizzaEnum) {
        return ofNullable(pizzaEnum)
                .map(PizzaEnum::getInternalPropertyValue)
                .orElse(null);
    }

    @Override
    public PizzaEnum convertToEntityAttribute(final String databaseValue) {
        return PizzaEnum.getFromDatabaseValue(databaseValue)
                        .orElse(null);
    }
}
