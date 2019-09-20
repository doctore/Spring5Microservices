package com.pizza.enums;

import com.pizza.model.Pizza;
import com.spring5microservices.common.interfaces.IEnumInternalPropertyValue;

import java.util.Arrays;
import java.util.Optional;

/**
 * Allowed types of {@link Pizza}.
 */
public enum PizzaEnum implements IEnumInternalPropertyValue<String> {
    MARGUERITA("Margherita"),
    MARINARA("Marinara"),
    CARBONARA("Carbonara"),
    FRUTTI_DI_MARE("Frutti di Mare"),
    PUGLIESE("Pugliese"),
    HAWAIIAN("Hawaiian");

    private String databaseValue;

    PizzaEnum(String databaseValue) {
        this.databaseValue = databaseValue;
    }

    @Override
    public String getInternalPropertyValue() {
        return this.databaseValue;
    }

    /**
     * Using the given parameter returns the {@link PizzaEnum} with equal value in its {@code databaseValue} property.
     *
     * @param databaseValue
     *    Value to search
     *
     * @return {@link Optional} of {@link PizzaEnum} if given value exists.
     *         An empty {@link Optional} otherwise.
     */
    public static Optional<PizzaEnum> getFromDatabaseValue(String databaseValue) {
        return Optional.ofNullable(databaseValue)
                       .flatMap(dv -> Arrays.stream(PizzaEnum.values())
                                            .filter(ev -> dv.equals(ev.databaseValue))
                                            .findFirst());
    }

}

