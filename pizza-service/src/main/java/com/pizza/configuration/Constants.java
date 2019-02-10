package com.pizza.configuration;

/**
 * Global values used in different part of the application
 */
public class Constants {

    // Database schema on which the entities have been included
    public static final String DATABASE_SCHEMA = "eat";

    // Path of the folders in the application
    public static final class PATH {
        public static final String REPOSITORY = "com.pizza.repository";
    }

    // Mapping used to match the result of some custom queries
    public static final class SQL_RESULT_MAPPING {
        public static final String PIZZA_INGREDIENTS = "PizzaIngredientsMapping";
    }

}
