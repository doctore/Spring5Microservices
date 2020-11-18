package com.order.util.converter;

import com.order.dto.OrderLineDto;
import com.order.model.Order;
import com.order.model.OrderLine;
import com.spring5microservices.common.converter.BaseConverter;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Utility class to convert from {@link OrderLine} to {@link OrderLineDto} and vice versa.
 */
@Mapper
@DecoratedWith(OrderLineConverterDecorator.class)
public interface OrderLineConverter extends BaseConverter<OrderLine, OrderLineDto> {

    /**
     * Create a new {@link OrderLine} which properties match with the given {@link OrderLineDto}
     *
     * @param orderLineDto {@link OrderLineDto} with the "source information"
     * @param orderId      {@link Order#id} of the given dto
     * @return {@link OrderLine}
     */
    @Mappings({
            @Mapping(source = "orderLineDto.pizza.id", target = "pizzaId"),
            @Mapping(source = "orderId", target = "orderId")})
    OrderLine fromDtoToModel(final OrderLineDto orderLineDto, Integer orderId);

    /**
     * Create a new {@link OrderLine} which properties match with the given {@link OrderLineDto}
     *
     * @param orderLineDto {@link OrderLineDto} with the "source information"
     * @return {@link OrderLine}
     */
    @Override
    @Mappings(@Mapping(source = "orderLineDto.pizza.id", target = "pizzaId"))
    OrderLine fromDtoToModel(final OrderLineDto orderLineDto);

    /**
     * Create a new {@link OrderLine} which properties match with the given {@link OrderLineDto}
     *
     * @param orderLineDto {@link OrderLineDto} with the "source information"
     * @return {@link Optional} of {@link OrderLine}
     */
    default Optional<OrderLine> fromDtoToOptionalModel(final OrderLineDto orderLineDto, Integer orderId) {
        return Optional.ofNullable(orderLineDto)
                       .map(dto -> this.fromDtoToModel(dto, orderId));
    }

    /**
     * Return a new {@link Collection} of {@link OrderLine} with the information contains in the given
     * {@link Collection} of {@link OrderLineDto}
     *
     * @param orderLineDtos {@link Collection} of {@link OrderLineDto} with the "source information"
     * @param orderId       {@link Order#id} of the given dtos
     * @return {@link List} of {@link OrderLine}
     */
    default List<OrderLine> fromDtosToModels(final Collection<OrderLineDto> orderLineDtos, Integer orderId) {
        return Optional.ofNullable(orderLineDtos)
                       .map(dtos -> {
                           List<OrderLine> orderLines = new ArrayList<>();
                           dtos.forEach(dto -> orderLines.add(this.fromDtoToModel(dto, orderId)));
                           return orderLines;
                       })
                       .orElseGet(ArrayList::new);
    }

    /**
     * Create a new {@link OrderLineDto} which properties match with the given {@link OrderLine}
     *
     * @param orderLine {@link OrderLine} with the "source information"
     * @return {@link OrderLineDto}
     */
    @Override
    @Mappings(@Mapping(source = "pizzaId", target = "pizza.id"))
    OrderLineDto fromModelToDto(final OrderLine orderLine);

}


/**
 * Overwrite default converter methods included in {@link OrderLineConverter}
 */
abstract class OrderLineConverterDecorator implements OrderLineConverter {

    @Autowired
    private OrderLineConverter orderLineConverter;

    /**
     *    Create a new {@link OrderLine} which properties match with the given {@link OrderLineDto}. The difference
     * with the "default behaviour" is that only if the given {@link OrderLineDto} is not null, we will create a new one.
     *
     * @param orderLineDto
     *    {@link OrderLineDto} with the "source information"
     * @param orderId
     *    {@link Order#id} of the given dto
     *
     * @return {@link OrderLine}
     */
    @Override
    public OrderLine fromDtoToModel(final OrderLineDto orderLineDto, Integer orderId) {
        return Optional.ofNullable(orderLineDto)
                       .map(dto -> orderLineConverter.fromDtoToModel(dto, orderId))
                       .orElse(null);
    }

}
