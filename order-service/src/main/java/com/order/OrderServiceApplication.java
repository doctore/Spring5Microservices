package com.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);

        /*
        ConfigurableApplicationContext context = SpringApplication.run(OrderServiceApplication.class, args);
        OrderDao orderDao = context.getBean(OrderDao.class);
        Optional<OrderDto> order = Optional.empty();
        try {
            order = orderDao.fetchToOrderDtoByIdWithOrderLineDto(3);
        } catch (Exception e) {}
        */
    }

}

