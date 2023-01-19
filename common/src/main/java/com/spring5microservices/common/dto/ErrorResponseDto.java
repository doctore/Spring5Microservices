package com.spring5microservices.common.dto;

import com.spring5microservices.common.enums.RestApiErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Information included in an API error response")
public class ErrorResponseDto {

    @Schema(required = true, description = "Code with the root cause of the error")
    private RestApiErrorCode code;

    @Schema(description = "Details about the error")
    private List<String> errors;

    public ErrorResponseDto(final RestApiErrorCode code) {
        this.code = code;
        this.errors = new ArrayList<>();
    }

}
