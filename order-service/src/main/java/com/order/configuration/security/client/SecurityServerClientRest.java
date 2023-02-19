package com.order.configuration.security.client;

import com.order.dto.UsernameAuthoritiesDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST endpoint definitions used to connect to the security microservice.
 */
@FeignClient(value = "securityServer", url = "${security.restApi.authenticationInformation}")
public interface SecurityServerClientRest {

    @PostMapping(value = "/check_token", produces = APPLICATION_JSON_VALUE)
    UsernameAuthoritiesDto checkToken(@RequestParam(value="token") String token);

}
