package com.order.util.converter;

import com.order.model.IModel;
import org.mapstruct.IterableMapping;
import org.mapstruct.NullValueMappingStrategy;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Parent interface of the all converters from Dto to Model and vice versa.
 *
 * @param <M>
 *    Type of the Model to manage
 * @param <D>
 *    Type of the Dto to manage
 */
public interface BaseConverter<M extends IModel, D> {

    /**
     * Create a new model which properties match with the given dto.
     *
     * @param dto
     *    Dto with the source information
     *
     * @return equivalent model
     */
    M fromDtoToModel(final D dto);

    /**
     * Create an {@link Optional} with a new model which properties match with the given dto.
     *
     * @param dto
     *    Dto with the source information
     *
     * @return {@link Optional} of the equivalent model if dto is not null,
     *         {@link Optional#empty()} otherwise.
     */
    default Optional<M> fromDtoToOptionalModel(D dto) {
        return Optional.ofNullable(this.fromDtoToModel(dto));
    }

    /**
     *    Return a new {@link List} of models with the information contained in the given
     * {@link Collection} of dtos.
     *
     * @param dtos
     *    {@link Collection} of dtos with the source informacion
     *
     * @return {@link List} of equivalent models
     */
    @IterableMapping(nullValueMappingStrategy= NullValueMappingStrategy.RETURN_DEFAULT)
    List<M> fromDtosToModels(Collection<D> dtos);

    /**
     * Create a new dto which properties match with the given model.
     *
     * @param model
     *    Model with the source information
     *
     * @return equivalent dto
     */
    D fromModelToDto(M model);

    /**
     * Create an {@link Optional} with a new dto which properties match with the given model.
     *
     * @param model
     *    Model with the source information
     *
     * @return {@link Optional} of the equivalent dto if model is not null,
     *         {@link Optional#empty()} otherwise.
     */
    default Optional<D> fromModelToOptionalDto(M model) {
        return Optional.ofNullable(this.fromModelToDto(model));
    }

    /**
     *    Return a new {@link List} of dtos with the information contained in the given
     * {@link Collection} of models.
     *
     * @param models
     *    {@link Collection} of models with the source information
     *
     * @return {@link List} of equivalent dtos
     */
    @IterableMapping(nullValueMappingStrategy=NullValueMappingStrategy.RETURN_DEFAULT)
    List<D> fromModelsToDtos(Collection<M> models);

}
