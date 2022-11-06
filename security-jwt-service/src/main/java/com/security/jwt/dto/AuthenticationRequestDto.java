package com.security.jwt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@Data
@EqualsAndHashCode(of = { "username" })
@NoArgsConstructor
@Schema(description = "Required data to authenticate a user")
public class AuthenticationRequestDto {

    @Schema(required = true)
    @NotNull
    @Size(min = 1, max = 64)
    private String username;

    @Schema(required = true)
    @NotNull
    @Size(min = 1, max = 128)
    private String password;

}
