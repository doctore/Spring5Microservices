package com.pizza.enums;

/**
 * Used in the entitie's properties mapped as {@link Enum}s in database.
 */
public interface IEnumInDatabase {

    /**
     * Gets the value stored in database of every {@link Enum}.
     */
    public String getDatabaseValue();
}
