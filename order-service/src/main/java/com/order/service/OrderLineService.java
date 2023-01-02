package com.order.service;

import com.order.dao.OrderLineDao;
import com.order.dto.OrderLineDto;
import com.order.dto.PizzaDto;
import com.order.model.Order;
import com.order.model.OrderLine;
import com.order.util.converter.OrderLineConverter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;

@AllArgsConstructor
@Service
public class OrderLineService {

    @Lazy
    private final OrderLineDao dao;

    @Lazy
    private final OrderLineConverter converter;


    /**
     * Persist the information included in the given {@link Collection} of {@link OrderLineDto}s
     *
     * @param orderLineDtos
     *    {@link Collection} of {@link OrderLineDto}s to save
     * @param orderId
     *    {@link Order#getId()} of the given dtos
     *
     * @return {@link Collection} of {@link OrderLineDto}s with its updated data
     *
     * @throws IllegalArgumentException if given orderLineDtos is not null but orderId is null
     */
    public List<OrderLineDto> saveAll(final Collection<OrderLineDto> orderLineDtos,
                                      final Integer orderId) {
        return ofNullable(orderLineDtos)
                .map(dtos -> {
                    Assert.notNull(orderId, "OrderId cannot be null");

                    Collection<OrderLine> orderLines = converter.fromDtosToModels(
                            dtos,
                            orderId
                    );
                    dao.saveAll(orderLines);

                    List<OrderLineDto> orderLineDtosPersisted = converter.fromModelsToDtos(orderLines);
                    mergePizzaInformation(
                            orderLineDtos,
                            orderLineDtosPersisted
                    );
                    return orderLineDtosPersisted;
                })
                .orElseGet(ArrayList::new);
    }


    /**
     *    When there is a conversion from {@link OrderLine} to {@link OrderLineDto}, only the {@link OrderLine#getPizzaId()}
     * can be included in the result Dto. For that reason, we use this method avoiding a new query to database.
     *
     * @param dtosWithPizzaInformation
     *    {@link Collection} of {@link OrderLineDto} with "complete {@link PizzaDto} objects"
     * @param dtosWithoutPizzaInformation
     *    {@link Collection} of {@link OrderLineDto} with "{@link PizzaDto} that contains only {@link PizzaDto#getId()}
     */
    private void mergePizzaInformation(final Collection<OrderLineDto> dtosWithPizzaInformation,
                                       final Collection<OrderLineDto> dtosWithoutPizzaInformation) {
        Map<Short, PizzaDto> pizzaDtoMap = dtosWithPizzaInformation.stream()
                .map(OrderLineDto::getPizza)
                .collect(
                        toMap(
                                PizzaDto::getId,
                                Function.identity(),
                                (id1, id2) -> id1
                        )
                );
        dtosWithoutPizzaInformation
                .forEach(dto -> {
                    if (null != dto.getPizza()) {
                        dto.setPizza(
                                pizzaDtoMap.get(
                                        dto.getPizza().getId()
                                )
                        );
                    }
                });
    }

}
