package com.order.service;

import com.order.dao.OrderDao;
import com.order.dto.OrderDto;
import com.order.dto.OrderLineDto;
import com.order.model.Order;
import com.order.util.converter.OrderConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private OrderDao orderDao;
    private OrderConverter orderConverter;
    private OrderLineService orderLineService;


    @Autowired
    public OrderService(@Lazy OrderDao orderDao, @Lazy OrderConverter orderConverter,
                        @Lazy OrderLineService orderLineService) {
        this.orderDao = orderDao;
        this.orderConverter = orderConverter;
        this.orderLineService = orderLineService;
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


    /**
     * Persist the information included in the given {@link OrderDto}
     *
     * @param orderDto
     *    {@link OrderDto} to save
     *
     * @return {@link Optional} of {@link OrderDto} with its "final information" after this action
     */
    @Transactional
    public Optional<OrderDto> save(OrderDto orderDto) {
        return Optional.ofNullable(orderDto)
                       .flatMap(orderConverter::fromDtoToOptionalModel)
                       .map(order -> {
                           orderDao.save(order);

                           List<OrderLineDto> orderLineDtos = orderLineService.saveAll(orderDto.getOrderLines(), order.getId());
                           OrderDto orderDtoPersisted = orderConverter.fromModelToDto(order);
                           orderDtoPersisted.setOrderLines(orderLineDtos);

                           return Optional.of(orderDtoPersisted);
                       })
                       .orElse(null);
    }

}
