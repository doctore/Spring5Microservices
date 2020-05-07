package com.spring5microservices.common;

import com.spring5microservices.common.validator.annotation.EnumHasInternalStringValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@EqualsAndHashCode
@Data
@NoArgsConstructor
public class PizzaDto {

    @EnumHasInternalStringValue(enumClass=PizzaEnum.class)
    private String name;

    private Double cost;
}


