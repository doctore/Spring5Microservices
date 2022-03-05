package com.spring5microservices.common.dto;

import com.spring5microservices.common.enums.RestApiErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDto {

    private RestApiErrorCode code;
    private List<String> errors;

    public ErrorResponseDto(RestApiErrorCode code) {
        this.code = code;
    }
}
