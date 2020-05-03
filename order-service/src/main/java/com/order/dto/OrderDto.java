package com.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "Internal unique identifier", required = true)
    private Integer id;

    @Schema(description = "Unique identifier of the order", required = true)
    @NotNull
    @Size(min=1, max=64)
    private String code;

    @Schema(description = "When was created", required = true)
    @NotNull
    private Date created;

    @Schema(description = "List of order lines")
    @Valid
    List<OrderLineDto> orderLines;

}
