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

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = { "code" })
@Data
@NoArgsConstructor
@Schema(description = "Information related with an order")
public class OrderDto {

    @Schema(description = "Internal unique identifier", requiredMode = RequiredMode.REQUIRED)
    private Integer id;

    @Schema(description = "Unique identifier of the order", requiredMode = RequiredMode.REQUIRED)
    @NotNull
    @Size(min = 1, max = 64)
    private String code;

    @Schema(description = "When the order was created", requiredMode = RequiredMode.REQUIRED)
    @NotNull
    private Date created;

    @Schema(description = "List of order lines", requiredMode = RequiredMode.REQUIRED)
    @Valid
    List<OrderLineDto> orderLines;

}
