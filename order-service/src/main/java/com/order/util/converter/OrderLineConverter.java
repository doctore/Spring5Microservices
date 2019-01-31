package com.order.util.converter;

import com.order.dto.OrderLineDto;
import com.order.model.Order;
import com.order.model.OrderLine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Mapper
public interface OrderLineConverter {

    /**
     * Create a new {@link OrderLine} which properties match with the given {@link OrderLineDto}
     *
     * @param orderLineDto
     *    {@link OrderLineDto} with the "source information"
     * @param orderId
     *    {@link Order#id} of the given dto
     *
     * @return {@link OrderLine}
     */
    @Mappings(@Mapping(source = "orderLineDto.pizza.id", target = "pizzaId"))
    OrderLine fromDtoToModel(OrderLineDto orderLineDto, Integer orderId);

    /**
     * Create a new {@link OrderLine} which properties match with the given {@link OrderLineDto}
     *
     * @param orderLineDto
     *    {@link OrderLineDto} with the "source information"
     *
     * @return {@link Optional} of {@link OrderLine}
     */
    default Optional<OrderLine> fromDtoToOptionalModel(OrderLineDto orderLineDto, Integer orderId) {
        return Optional.ofNullable(orderLineDto)
                       .map(dto -> this.fromDtoToModel(dto, orderId));
    }

    /**
     *    Return a new {@link Collection} of {@link OrderLine} with the information contains in the given
     * {@link Collection} of {@link OrderLineDto}
     *
     * @param orderLineDtos
     *    {@link Collection} of {@link OrderLineDto} with the "source information"
     * @param orderId
     *    {@link Order#id} of the given dtos
     *
     * @return {@link Collection} of {@link OrderLine}
     */
    default Collection<OrderLine> fromDtosToModels(Collection<OrderLineDto> orderLineDtos, Integer orderId) {
        return Optional.ofNullable(orderLineDtos)
                       .map(dtos -> {
                           List<OrderLine> orderLines = new ArrayList<>();
                           dtos.forEach(dto -> orderLines.add(this.fromDtoToModel(dto, orderId)));
                           return orderLines;
                       })
                       .orElse(new ArrayList<>());
    }

}
