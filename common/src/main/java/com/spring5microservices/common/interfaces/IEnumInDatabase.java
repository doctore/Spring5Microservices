package com.spring5microservices.common.interfaces;

/**
 * Used in the entitie's properties mapped as {@link Enum}s in database.
 */
public interface IEnumInDatabase<T> {

    /**
     * Gets the value stored in database of every {@link Enum}.
     */
    T getDatabaseValue();
}
