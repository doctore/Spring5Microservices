package com.order.service;

import com.order.dao.OrderLineDao;
import com.order.dto.OrderLineDto;
import com.order.dto.PizzaDto;
import com.order.model.Order;
import com.order.model.OrderLine;
import com.order.util.converter.OrderLineConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrderLineService {

    private OrderLineDao orderLineDao;
    private OrderLineConverter orderLineConverter;

    @Autowired
    public OrderLineService(@Lazy OrderLineDao orderLineDao, @Lazy OrderLineConverter orderLineConverter) {
        this.orderLineDao = orderLineDao;
        this.orderLineConverter = orderLineConverter;
    }


    /**
     * Persist the information included in the given {@link Collection} of {@link OrderLineDto}s
     *
     * @param orderLineDtos
     *    {@link Collection} of {@link OrderLineDto}s to save
     * @param orderId
     *    {@link Order#id} of the given dtos
     *
     * @return {@link Collection} of {@link OrderLineDto}s with its "final information" after this action
     */
    public List<OrderLineDto> saveAll(Collection<OrderLineDto> orderLineDtos, Integer orderId) {
        return Optional.ofNullable(orderLineDtos)
                       .map(dtos -> {
                           Assert.notNull(orderId, "OrderId cannot be null");

                           Collection<OrderLine> orderLines = orderLineConverter.fromDtosToModels(orderLineDtos, orderId);
                           orderLineDao.saveAll(orderLines);

                           List<OrderLineDto> orderLineDtosPersisted = orderLineConverter.fromModelsToDtos(orderLines);
                           mergePizzaInformation(orderLineDtos, orderLineDtosPersisted);

                           return  orderLineDtosPersisted;
                       })
                       .orElse(new ArrayList<>());
    }


    /**
     *    When there is a conversion from {@link OrderLine} to {@link OrderLineDto}, only the {@link OrderLine#pizzaId}
     * can be included in the result Dto. For that reason, we use this method avoiding a new query to database.
     *
     * @param dtosWithPizzaInformation
     *    {@link Collection} of {@link OrderLineDto} with "complete {@link PizzaDto} objects"
     * @param dtosWithoutPizzaInformation
     *    {@link Collection} of {@link OrderLineDto} with "{@link PizzaDto} that contains only {@link PizzaDto#id}
     */
    private void mergePizzaInformation(Collection<OrderLineDto> dtosWithPizzaInformation,
                                       Collection<OrderLineDto> dtosWithoutPizzaInformation) {

        Map<Short, PizzaDto> pizzaDtoMap = dtosWithPizzaInformation.stream()
                                                                   .map(OrderLineDto::getPizza)
                                                                   .collect(Collectors.toMap(PizzaDto::getId, Function.identity()
                                                                                            ,(id1, id2) -> id1));
        dtosWithoutPizzaInformation.forEach(dto -> {
            if (null != dto.getPizza())
                dto.setPizza(pizzaDtoMap.get(dto.getPizza().getId()));
        });
    }

}
