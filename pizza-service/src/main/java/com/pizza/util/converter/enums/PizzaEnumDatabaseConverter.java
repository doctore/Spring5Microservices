package com.pizza.util.converter.enums;

import com.pizza.enums.PizzaEnum;
import com.pizza.model.Pizza;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Optional;

/**
 *    Class used to convert from/to the database value stored in the column {@code name} of the {@code pizza}
 * table from/to {@link Pizza#getName()}
 */
@Converter(autoApply = true)
public class PizzaEnumDatabaseConverter implements AttributeConverter<PizzaEnum, String> {

    @Override
    public String convertToDatabaseColumn(PizzaEnum pizzaEnum) {
        return Optional.ofNullable(pizzaEnum)
                       .map(PizzaEnum::getInternalPropertyValue)
                       .orElse(null);
    }

    @Override
    public PizzaEnum convertToEntityAttribute(String databaseValue) {
        return PizzaEnum.getFromDatabaseValue(databaseValue)
                        .orElse(null);
    }
}
