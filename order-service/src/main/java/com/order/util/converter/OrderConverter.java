package com.order.util.converter;

import com.order.dto.OrderDto;
import com.order.model.Order;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Mapper
public interface OrderConverter {

    /**
     * Create a new {@link Order} which properties match with the given {@link OrderDto}
     *
     * @param orderDto
     *    {@link OrderDto} with the "source information"
     *
     * @return {@link Order}
     */
    Order fromDtoToModel(OrderDto orderDto);

    /**
     * Create a new {@link Order} which properties match with the given {@link OrderDto}
     *
     * @param orderDto
     *    {@link OrderDto} with the "source information"
     *
     * @return {@link Optional} of {@link Order}
     */
    default Optional<Order> fromDtoToOptionalModel(OrderDto orderDto) {
        return Optional.ofNullable(this.fromDtoToModel(orderDto));
    }

    /**
     *    Return a new {@link Collection} of {@link Order} with the information contains in the given
     * {@link Collection} of {@link OrderDto}
     *
     * @param orderDtos
     *    {@link Collection} of {@link OrderDto} with the "source information"
     *
     * @return {@link List} of {@link Order}
     */
    @IterableMapping(nullValueMappingStrategy=NullValueMappingStrategy.RETURN_DEFAULT)
    List<Order> fromDtosToModels(Collection<OrderDto> orderDtos);

    /**
     * Create a new {@link OrderDto} which properties match with the given {@link Order}
     *
     * @param order
     *    {@link Order} with the "source information"
     *
     * @return {@link OrderDto}
     */
    OrderDto fromModelToDto(Order order);

    /**
     * Create a new {@link OrderDto} which properties match with the given {@link Order}
     *
     * @param order
     *    {@link Order} with the "source information"
     *
     * @return {@link Optional} of {@link OrderDto}
     */
    default Optional<OrderDto> fromModelToOptionalDto(Order order) {
        return Optional.ofNullable(this.fromModelToDto(order));
    }

    /**
     *    Return a new {@link Collection} of {@link OrderDto} with the information contains in the given
     * {@link Collection} of {@link Order}
     *
     * @param orders
     *    {@link Collection} of {@link Order} with the "source information"
     *
     * @return {@link List} of {@link OrderDto}
     */
    @IterableMapping(nullValueMappingStrategy=NullValueMappingStrategy.RETURN_DEFAULT)
    List<OrderDto> fromModelsToDtos(Collection<Order> orders);

}
