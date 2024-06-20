package com.order.service;

import com.order.dao.OrderDao;
import com.order.dto.OrderDto;
import com.order.dto.OrderLineDto;
import com.order.model.Order;
import com.order.util.converter.OrderConverter;
import org.jooq.exception.DataAccessException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Optional.ofNullable;

@Service
public class OrderService {

    private final OrderDao dao;

    private final OrderConverter converter;

    private final OrderLineService orderLineService;


    public OrderService(@Lazy final OrderDao dao,
                        @Lazy final OrderConverter converter,
                        @Lazy final OrderLineService orderLineService) {
        this.dao = dao;
        this.converter = converter;
        this.orderLineService = orderLineService;
    }


    /**
     * Return the {@link OrderDto} and its {@link OrderLineDto} information of the given {@link OrderDto#getId()}
     *
     * @param id
     *    {@link Order#getId()} to find
     *
     * @return {@link Optional} with the {@link OrderDto} which identifier matches with the given one.
     *         {@link Optional#empty()} otherwise
     *
     * @throws DataAccessException if there is an error executing the query
     */
    public Optional<OrderDto> findByIdWithOrderLines(final Integer id) {
        return ofNullable(id)
                .flatMap(dao::fetchToOrderDtoByIdWithOrderLineDto);
    }


    /**
     *    Return a "page of {@link OrderDto}" and its {@link OrderLineDto} information, ordered by
     * {@link Order#getCreated()} desc.
     *
     * @param page
     *    Desired page to get (taking into account the value of the given size)
     * @param size
     *    Number of {@link OrderDto}s included in every page
     *
     * @return {@link List} of {@link OrderDto} ordered by {@link Order#getCreated()} desc
     *
     * @throws DataAccessException if there is an error executing the query
     */
    public Set<OrderDto> findPageOrderedByCreatedWithOrderLines(final int page,
                                                                final int size) {
        return dao.fetchPageToOrderDtoByIdWithOrderLineDto(
                page,
                size
        );
    }


    /**
     * Persist the information included in the given {@link OrderDto}
     *
     * @param orderDto
     *    {@link OrderDto} to save
     *
     * @return {@link Optional} of {@link OrderDto} with its updated data
     */
    public Optional<OrderDto> save(final OrderDto orderDto) {
        return ofNullable(orderDto)
                .flatMap(converter::fromDtoToOptionalModel)
                .flatMap(order -> {
                    dao.save(order);
                    List<OrderLineDto> orderLineDtos = orderLineService.saveAll(
                            orderDto.getOrderLines(),
                            order.getId()
                    );
                    Optional<OrderDto> orderDtoPersisted = converter.fromModelToOptionalDto(order);
                    orderDtoPersisted.ifPresent(dto -> dto.setOrderLines(orderLineDtos));
                    return orderDtoPersisted;
                });
    }

}
