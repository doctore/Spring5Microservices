package com.spring5microservices.common.converter;

import org.mapstruct.IterableMapping;
import org.mapstruct.NullValueMappingStrategy;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

/**
 * Parent interface of the all converters from Dto to Model and vice versa.
 *
 * @param <M>
 *    Type of the Model to manage
 * @param <D>
 *    Type of the Dto to manage
 */
public interface BaseConverter<M, D> {

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
    default Optional<M> fromDtoToOptionalModel(final D dto) {
        return ofNullable(this.fromDtoToModel(dto));
    }

    /**
     *    Return a new {@link List} of models with the information contained in the given
     * {@link Collection} of dtos.
     *
     * @param dtos
     *    {@link Collection} of dtos with the source information
     *
     * @return {@link List} of equivalent models
     */
    @IterableMapping(nullValueMappingStrategy=NullValueMappingStrategy.RETURN_DEFAULT)
    List<M> fromDtosToModels(final Collection<D> dtos);

    /**
     * Create a new dto which properties match with the given model.
     *
     * @param model
     *    Model with the source information
     *
     * @return equivalent dto
     */
    D fromModelToDto(final M model);

    /**
     * Create an {@link Optional} with a new dto which properties match with the given model.
     *
     * @param model
     *    Model with the source information
     *
     * @return {@link Optional} of the equivalent dto if model is not null,
     *         {@link Optional#empty()} otherwise.
     */
    default Optional<D> fromModelToOptionalDto(final M model) {
        return ofNullable(this.fromModelToDto(model));
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
    List<D> fromModelsToDtos(final Collection<M> models);

}
