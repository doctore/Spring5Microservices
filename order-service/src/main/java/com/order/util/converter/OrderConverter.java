package com.order.util.converter;

import com.order.dto.OrderDto;
import com.order.model.Order;
import com.spring5microservices.common.converter.BaseConverter;
import org.mapstruct.Mapper;

/**
 * Utility class to convert from {@link Order} to {@link OrderDto} and vice versa.
 */
@Mapper
public interface OrderConverter extends BaseConverter<Order, OrderDto> {}
