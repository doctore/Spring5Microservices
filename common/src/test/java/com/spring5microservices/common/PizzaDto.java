package com.spring5microservices.common;

import com.spring5microservices.common.validator.annotation.EnumHasInternalStringValue;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Builder
@Data
@EqualsAndHashCode
public class PizzaDto {
    @EnumHasInternalStringValue(enumClass=PizzaEnum.class)
    private String name;

    private Double cost;
}


