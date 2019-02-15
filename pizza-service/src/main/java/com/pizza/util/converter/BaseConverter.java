package com.pizza.util.converter;

import org.mapstruct.IterableMapping;
import org.mapstruct.NullValueMappingStrategy;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Parent interface of the all converters from Dto to Entity and vice versa.
 *
 * @param <E>
 *    Type of the Entity to manage
 * @param <D>
 *    Type of the Dto to manage
 */
public interface BaseConverter<E, D> {

    /**
     * Create a new entity which properties match with the given dto.
     *
     * @param dto
     *    Dto with the source information
     *
     * @return equivalent entity
     */
    E fromDtoToEntity(final D dto);

    /**
     * Create an {@link Optional} with a new entity which properties match with the given dto.
     *
     * @param dto
     *    Dto with the source information
     *
     * @return {@link Optional} of the equivalent entity if dto is not null,
     *         {@link Optional#empty()} otherwise.
     */
    default Optional<E> fromDtoToOptionalEntity(D dto) {
        return Optional.ofNullable(this.fromDtoToEntity(dto));
    }

    /**
     *    Return a new {@link List} of entities with the information contained in the given
     * {@link Collection} of dtos.
     *
     * @param dtos
     *    {@link Collection} of dtos with the source information
     *
     * @return {@link List} of equivalent entities
     */
    @IterableMapping(nullValueMappingStrategy= NullValueMappingStrategy.RETURN_DEFAULT)
    List<E> fromDtosToEntities(Collection<D> dtos);

    /**
     * Create a new dto which properties match with the given entity.
     *
     * @param entity
     *    Entity with the source information
     *
     * @return equivalent dto
     */
    D fromEntityToDto(E entity);

    /**
     * Create an {@link Optional} with a new dto which properties match with the given entity.
     *
     * @param entity
     *    Entity with the source information
     *
     * @return {@link Optional} of the equivalent dto if entity is not null,
     *         {@link Optional#empty()} otherwise.
     */
    default Optional<D> fromEntityToOptionalDto(E entity) {
        return Optional.ofNullable(this.fromEntityToDto(entity));
    }

    /**
     *    Return a new {@link List} of dtos with the information contained in the given
     * {@link Collection} of entities.
     *
     * @param entities
     *    {@link Collection} of entities with the source information
     *
     * @return {@link List} of equivalent dtos
     */
    @IterableMapping(nullValueMappingStrategy=NullValueMappingStrategy.RETURN_DEFAULT)
    List<D> fromEntitiesToDtos(Collection<E> entities);

}
