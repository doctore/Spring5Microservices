package com.order.service;

import com.order.dao.OrderDao;
import com.order.dto.OrderDto;
import com.order.dto.OrderLineDto;
import com.order.model.Order;
import com.order.util.converter.OrderConverter;
import lombok.AllArgsConstructor;
import org.jooq.exception.DataAccessException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
@Service
public class OrderService {

    @Lazy
    private OrderDao orderDao;

    @Lazy
    private OrderConverter orderConverter;

    @Lazy
    private OrderLineService orderLineService;


    /**
     * Return the {@link OrderDto} and its {@link OrderLineDto} information of the given {@link OrderDto#id}
     *
     * @param id
     *    {@link Order#id} to find
     *
     * @return {@link Optional} with the {@link OrderDto} which identifier matches with the given one.
     *         {@link Optional#empty()} otherwise
     *
     * @throws DataAccessException if there is an error executing the query
     */
    public Optional<OrderDto> findByIdWithOrderLines(Integer id) {
        return orderDao.fetchToOrderDtoByIdWithOrderLineDto(id);
    }


    /**
     *    Return a "page of {@link OrderDto}" and its {@link OrderLineDto} information, ordered by
     * {@link Order#created} desc.
     *
     * @param page
     *    Desired page to get (taking into account the value of the given size)
     * @param size
     *    Number of {@link OrderDto}s included in every page
     *
     * @return {@link List} of {@link OrderDto} ordered by {@link Order#created} desc
     *
     * @throws DataAccessException if there is an error executing the query
     */
    public Set<OrderDto> findPageOrderedByCreatedWithOrderLines(int page, int size) {
        return orderDao.fetchPageToOrderDtoByIdWithOrderLineDto(page, size);
    }


    /**
     * Persist the information included in the given {@link OrderDto}
     *
     * @param orderDto
     *    {@link OrderDto} to save
     *
     * @return {@link Optional} of {@link OrderDto} with its "final information" after this action
     */
    public Optional<OrderDto> save(OrderDto orderDto) {
        return Optional.ofNullable(orderDto)
                       .flatMap(orderConverter::fromDtoToOptionalModel)
                       .map(order -> {
                           orderDao.save(order);
                           List<OrderLineDto> orderLineDtos = orderLineService.saveAll(orderDto.getOrderLines(), order.getId());

                           Optional<OrderDto> orderDtoPersisted = orderConverter.fromModelToOptionalDto(order);
                           orderDtoPersisted.ifPresent(dto -> dto.setOrderLines(orderLineDtos));
                           return orderDtoPersisted;
                       })
                       .orElse(Optional.empty());
    }

}
