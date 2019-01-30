package com.order.util.converter;

import com.order.dto.OrderLineDto;
import com.order.model.OrderLine;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Optional;

@Mapper(nullValueMappingStrategy=NullValueMappingStrategy.RETURN_DEFAULT)
@DecoratedWith(OrderLineConverterDecorator.class)
public interface OrderLineConverter {

    /**
     * Create a new {@link OrderLine} which properties match with the given {@link OrderLineDto}
     *
     * @param orderLineDto
     *    {@link OrderLineDto} with the "source information"
     *
     * @return {@link OrderLine}
     */
    OrderLine fromDtoToModel(OrderLineDto orderLineDto);

    /**
     * Create a new {@link OrderLine} which properties match with the given {@link OrderLineDto}
     *
     * @param orderLineDto
     *    {@link OrderLineDto} with the "source information"
     *
     * @return {@link Optional} of {@link OrderLine}
     */
    default Optional<OrderLine> fromDtoToOptionalModel(OrderLineDto orderLineDto) {
        return Optional.ofNullable(orderLineDto)
                       .map(this::fromDtoToModel);
    }

    /**
     *    Return a new {@link Collection} of {@link OrderLine} with the information contains in the given
     * {@link Collection} of {@link OrderLineDto}
     *
     * @param orderLineDtos
     *    {@link Collection} of {@link OrderLineDto} with the "source information"
     *
     * @return {@link Collection} of {@link OrderLine}
     */
    Collection<OrderLine> fromDtosToModels(Collection<OrderLineDto> orderLineDtos);

}


/**
 * Overwrite default converter methods included in {@link OrderLineConverter}
 */
abstract class OrderLineConverterDecorator implements OrderLineConverter {

    @Autowired
    private OrderLineConverter orderLineConverter;

    /**
     *    Create a new {@link OrderLine} which properties match with the given {@link OrderLineDto},
     * including {@link OrderLine#pizzaId} information in the final result
     *
     * @param orderLineDto
     *    {@link OrderLineDto} with the "source information"
     *
     * @return {@link OrderLine}
     */
    @Override
    public OrderLine fromDtoToModel(OrderLineDto orderLineDto) {
        return Optional.ofNullable(orderLineDto)
                       .map(orderLineConverter::fromDtoToModel)
                       .map(model -> {
                           model.setPizzaId(orderLineDto.getPizza().getId());
                           return model;
                        })
                       .orElse(new OrderLine());
    }

}

