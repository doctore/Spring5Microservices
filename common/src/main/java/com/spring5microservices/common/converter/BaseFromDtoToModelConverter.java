package com.spring5microservices.common.converter;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Parent interface of the all converters that allow ONLY from Dto to Model conversion.
 *
 * @param <M>
 *    Type of the Model to manage
 * @param <D>
 *    Type of the Dto to manage
 */
public interface BaseFromDtoToModelConverter<M, D> extends BaseConverter<M, D> {

    String errorMessage = "Operation not allowed in a BaseFromDtoToModelConverter converter";


    @Override
    default D fromModelToDto(final M model) {
        throw new UnsupportedOperationException(errorMessage);
    }


    @Override
    default Optional<D> fromModelToOptionalDto(final M model) {
        throw new UnsupportedOperationException(errorMessage);
    }


    @Override
    default List<D> fromModelsToDtos(final Collection<M> models) {
        throw new UnsupportedOperationException(errorMessage);
    }

}
