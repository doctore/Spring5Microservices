package com.spring5microservices.common;

import com.spring5microservices.common.validator.annotation.EnumHasValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@EqualsAndHashCode
@Data
@NoArgsConstructor
public class IngredientDto {

    private Integer id;

    @EnumHasValue(enumClass=IngredientEnum.class)
    private String name;

}
