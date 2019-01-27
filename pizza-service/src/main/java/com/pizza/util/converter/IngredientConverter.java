package com.pizza.util.converter;

import com.pizza.dto.IngredientDto;
import com.pizza.model.Ingredient;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;

import javax.swing.text.html.parser.Entity;
import java.util.Collection;
import java.util.Optional;

@Mapper(nullValueMappingStrategy=NullValueMappingStrategy.RETURN_DEFAULT)
public interface IngredientConverter {

    /**
     * Create a new {@link Ingredient} which properties match with the given {@link IngredientDto}
     *
     * @param ingredientDto
     *    {@link IngredientDto} with the "source information"
     *
     * @return {@link Ingredient}
     */
    Ingredient fromDtoToEntity(IngredientDto ingredientDto);

    /**
     * Create a new {@link Ingredient} which properties match with the given {@link IngredientDto}
     *
     * @param ingredientDto
     *    {@link IngredientDto} with the "source information"
     *
     * @return {@link Optional} of {@link Ingredient}
     */
    default Optional<Ingredient> fromDtoToOptionalEntity(IngredientDto ingredientDto) {
        return Optional.ofNullable(ingredientDto)
                       .map(this::fromDtoToEntity);
    }

    /**
     *    Return a new {@link Collection} of {@link Ingredient} with the information contains in the given
     * {@link Collection} of {@link IngredientDto}
     *
     * @param ingredientDtos
     *    {@link Collection} of {@link IngredientDto} with the "source information"
     *
     * @return {@link Collection} of {@link Ingredient}
     */
    Collection<Ingredient> fromDtosToEntities(Collection<IngredientDto> ingredientDtos);

    /**
     * Create a new {@link IngredientDto} which properties match with the given {@link Ingredient}
     *
     * @param ingredient
     *    {@link Ingredient} with the "source information"
     *
     * @return {@link IngredientDto}
     */
    IngredientDto fromEntityToDto(Ingredient ingredient);

    /**
     * Create a new {@link IngredientDto} which properties match with the given {@link Ingredient}
     *
     * @param ingredient
     *    {@link Ingredient} with the "source information"
     *
     * @return {@link Optional} of {@link IngredientDto}
     */
    default Optional<IngredientDto> fromEntityToOptionalDto(Ingredient ingredient) {
        return Optional.ofNullable(ingredient)
                       .map(this::fromEntityToDto);
    }

    /**
     *    Return a new {@link Collection} of {@link IngredientDto} with the information contains in the given
     * {@link Collection} of {@link Ingredient}
     *
     * @param ingredients
     *    {@link Collection} of {@link Ingredient} with the "source information"
     *
     * @return {@link Collection} of {@link IngredientDto}
     */
    Collection<IngredientDto> fromEntitiesToDtos(Collection<Ingredient> ingredients);

}
