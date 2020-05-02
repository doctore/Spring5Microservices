package com.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class OrderServiceApplication {

    @Bean
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);

        /*
        ConfigurableApplicationContext context = SpringApplication.run(OrderServiceApplication.class, args);
        OrderDao orderDao = context.getBean(OrderDao.class);
        Optional<OrderDto> order = Optional.empty();
        try {
            Set<OrderDto> orders = orderDao.fetchPageToOrderDtoByIdWithOrderLineDto(0, 2);
        } catch (Exception e) {}
        */
    }

}
