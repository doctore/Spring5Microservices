package com.order.util.converter;

import com.order.dto.PizzaDto;
import com.order.model.Pizza;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;

import java.util.Collection;
import java.util.Optional;

@Mapper
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
    @IterableMapping(nullValueMappingStrategy=NullValueMappingStrategy.RETURN_DEFAULT)
    Collection<Pizza> fromDtosToModels(Collection<PizzaDto> pizzaDtos);

    /**
     * Create a new {@link PizzaDto} which properties match with the given {@link Pizza}
     *
     * @param pizza
     *    {@link Pizza} with the "source information"
     *
     * @return {@link PizzaDto}
     */
    PizzaDto fromModelToDto(Pizza pizza);

    /**
     * Create a new {@link PizzaDto} which properties match with the given {@link Pizza}
     *
     * @param pizza
     *    {@link Pizza} with the "source information"
     *
     * @return {@link Optional} of {@link PizzaDto}
     */
    default Optional<PizzaDto> fromModelToOptionalDto(Pizza pizza) {
        return Optional.ofNullable(pizza)
                .map(this::fromModelToDto);
    }

    /**
     *    Return a new {@link Collection} of {@link PizzaDto} with the information contains in the given
     * {@link Collection} of {@link Pizza}
     *
     * @param pizzas
     *    {@link Collection} of {@link Pizza} with the "source information"
     *
     * @return {@link Collection} of {@link PizzaDto}
     */
    @IterableMapping(nullValueMappingStrategy=NullValueMappingStrategy.RETURN_DEFAULT)
    Collection<PizzaDto> fromModelsToDtos(Collection<Pizza> pizzas);

}
