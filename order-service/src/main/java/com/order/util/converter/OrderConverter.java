package com.order.util.converter;

import com.order.dto.OrderDto;
import com.order.model.Order;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;

import java.util.Collection;
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
        return Optional.ofNullable(orderDto)
                       .map(this::fromDtoToModel);
    }

    /**
     *    Return a new {@link Collection} of {@link Order} with the information contains in the given
     * {@link Collection} of {@link OrderDto}
     *
     * @param orderDtos
     *    {@link Collection} of {@link OrderDto} with the "source information"
     *
     * @return {@link Collection} of {@link Order}
     */
    @IterableMapping(nullValueMappingStrategy=NullValueMappingStrategy.RETURN_DEFAULT)
    Collection<Order> fromDtosToModels(Collection<OrderDto> orderDtos);

}
