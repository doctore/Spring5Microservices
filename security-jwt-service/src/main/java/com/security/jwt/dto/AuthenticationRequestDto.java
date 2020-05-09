package com.security.jwt.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(of = {"username"})
@NoArgsConstructor
@ApiModel(description="Required data to authenticate a user")
@Wither
public class AuthenticationRequestDto {

    @ApiModelProperty(required = true)
    @NotNull
    @Size(min=1, max=64)
    private String username;

    @ApiModelProperty(position = 1, required = true)
    @NotNull
    @Size(min=1, max=128)
    private String password;

}
