package com.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"code"})
@Data
@NoArgsConstructor
public class OrderDto {

    private Integer id;

    @NotNull
    @Size(min=1, max=64)
    private String code;

    @NotNull
    private Date created;

    @Valid
    List<OrderLineDto> orderLines;

}
