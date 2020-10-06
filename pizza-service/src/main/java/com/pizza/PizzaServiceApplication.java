package com.pizza;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PizzaServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PizzaServiceApplication.class, args);

        /*
        ConfigurableApplicationContext context = SpringApplication.run(PizzaServiceApplication.class, args);
        PizzaRepository pizzaRepository = context.getBean(PizzaRepository.class);
        Optional<Pizza> pizzaOptional = pizzaRepository.findWithIngredientsByName("Carbonara");
        */
    }

}

