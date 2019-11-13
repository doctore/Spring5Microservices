package com.pizza.configuration.rest;

/**
 * Used to define the REST API routes included in the project
 */
public final class RestRoutes {

    public static final class PIZZA {
        public static final String ROOT = "/pizza";
        public static final String PAGE_WITH_INGREDIENTS = "/pageWithIngredients";
    }

    public static final class USER {
        public static final String ROOT = "/user";
        public static final String BLACKLIST = "/blacklist";
    }

}
