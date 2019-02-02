package com.order.service;

import com.order.dao.OrderDao;
import com.order.dto.OrderDto;
import com.order.dto.OrderLineDto;
import com.order.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrderService {

    private OrderDao orderDao;


    @Autowired
    public OrderService(@Lazy OrderDao orderDao) {
        this.orderDao = orderDao;
    }


    /**
     * Return the {@link OrderDto} and its {@link OrderLineDto} information of the given {@link OrderDto#id}
     *
     * @param id
     *    {@link Order#id} to find
     *
     * @return {@link Optional} with the {@link OrderDto} which identifier matches with the given one.
     *         {@link Optional#empty()} otherwise
     */
    public Optional<OrderDto> findByIdWithOrderLines(Integer id) {
        return orderDao.fetchToOrderDtoByIdWithOrderLineDto(id);
    }

}
