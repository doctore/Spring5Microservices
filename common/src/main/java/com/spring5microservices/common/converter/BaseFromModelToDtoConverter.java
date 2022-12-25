package com.spring5microservices.common.converter;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Parent interface of the all converters that allow ONLY from Model to Dto conversion.
 *
 * @param <M>
 *    Type of the Model to manage
 * @param <D>
 *    Type of the Dto to manage
 */
public interface BaseFromModelToDtoConverter<M, D> extends BaseConverter<M, D> {

    String errorMessage = "Operation not allowed in a BaseFromModelToDtoConverter converter";


    @Override
    default M fromDtoToModel(final D dto) {
        throw new UnsupportedOperationException(errorMessage);
    }


    @Override
    default Optional<M> fromDtoToOptionalModel(final D dto) {
        throw new UnsupportedOperationException(errorMessage);
    }


    @Override
    default List<M> fromDtosToModels(final Collection<D> dtos) {
        throw new UnsupportedOperationException(errorMessage);
    }

}
