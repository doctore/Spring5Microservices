package com.security.jwt.controller;

import com.security.jwt.configuration.rest.GlobalErrorWebExceptionHandler;
import com.security.jwt.service.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(SpringExtension.class)
public class SecurityControllerTest {

    @Mock
    private SecurityService mockSecurityService;

    MockMvc mockMvc;

    private SecurityController securityController;

    @BeforeEach
    public void init() {
        securityController = new SecurityController(mockSecurityService);
        mockMvc = standaloneSetup(securityController)
                     .setControllerAdvice(GlobalErrorWebExceptionHandler.class)
                     .build();
    }


}
