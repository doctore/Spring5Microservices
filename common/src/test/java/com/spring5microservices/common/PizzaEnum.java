package com.spring5microservices.common;

import com.spring5microservices.common.util.validator.enums.IEnumInternalPropertyValue;

public enum PizzaEnum implements IEnumInternalPropertyValue<String> {
    MARGUERITA("Margherita"),
    CARBONARA("Carbonara");

    private String databaseValue;

    PizzaEnum(String databaseValue) {
        this.databaseValue = databaseValue;
    }

    @Override
    public String getInternalPropertyValue() {
        return this.databaseValue;
    }
}
