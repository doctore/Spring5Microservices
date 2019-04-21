package com.pizza;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class PizzaServiceApplication {

    @Bean
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(PizzaServiceApplication.class, args);

        /*
        ConfigurableApplicationContext context = SpringApplication.run(PizzaServiceApplication.class, args);
        PizzaRepository pizzaRepository = context.getBean(PizzaRepository.class);
        Optional<Pizza> pizzaOptional = pizzaRepository.findWithIngredientsByName("Carbonara");
        */
    }

}

