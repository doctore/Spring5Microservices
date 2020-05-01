package com.order;

import com.order.dto.OrderDto;
import com.order.dto.OrderLineDto;
import com.order.dto.PizzaDto;
import com.order.model.Order;
import com.order.model.OrderLine;
import com.order.model.Pizza;
import lombok.experimental.UtilityClass;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@UtilityClass
public class TestDataFactory {

    public static Order buildOrder(Integer id, String code, Timestamp created) {
        return new Order(id, code, created);
    }

    public static OrderLine buildOrderLine(Integer id, Integer orderId, Short pizzaId, Short amount, Double cost) {
        return new OrderLine(id, orderId, pizzaId, amount, cost);
    }

    public static Pizza buildPizza(Short id, String name, Double cost) {
        return new Pizza(id, name, cost);
    }

    public static OrderDto buildOrderDto(Integer id, String code, Date created, List<OrderLineDto> orderLines) {
        return new OrderDto(id, code, created, orderLines);
    }

    public static OrderLineDto buildOrderLineDto(Integer id, Integer orderId, PizzaDto pizza, Short amount, Double cost) {
        return new OrderLineDto(id, orderId, pizza, amount, cost);
    }

    public static PizzaDto buildPizzaDto(Short id, String name, Double cost) {
        return new PizzaDto(id, name, cost);
    }

}
