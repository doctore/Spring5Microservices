package com.order.util.converter;

import com.order.dto.PizzaDto;
import com.order.model.Pizza;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;

import java.util.Collection;
import java.util.Optional;

@Mapper(nullValueMappingStrategy=NullValueMappingStrategy.RETURN_DEFAULT)
public interface PizzaConverter {

    /**
     * Create a new {@link Pizza} which properties match with the given {@link PizzaDto}
     *
     * @param pizzaDto
     *    {@link PizzaDto} with the "source information"
     *
     * @return {@link Pizza}
     */
    Pizza fromDtoToModel(PizzaDto pizzaDto);

    /**
     * Create a new {@link Pizza} which properties match with the given {@link PizzaDto}
     *
     * @param pizzaDto
     *    {@link PizzaDto} with the "source information"
     *
     * @return {@link Optional} of {@link Pizza}
     */
    default Optional<Pizza> fromDtoToOptionalModel(PizzaDto pizzaDto) {
        return Optional.ofNullable(pizzaDto)
                       .map(this::fromDtoToModel);
    }

    /**
     *    Return a new {@link Collection} of {@link Pizza} with the information contains in the given
     * {@link Collection} of {@link PizzaDto}
     *
     * @param pizzaDtos
     *    {@link Collection} of {@link PizzaDto} with the "source information"
     *
     * @return {@link Collection} of {@link Pizza}
     */
    Collection<Pizza> fromDtosToModels(Collection<PizzaDto> pizzaDtos);

}
