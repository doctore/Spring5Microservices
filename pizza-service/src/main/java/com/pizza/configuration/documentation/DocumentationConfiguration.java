package com.pizza.configuration.documentation;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DocumentationConfiguration {

    public static final String DOCUMENTATION_API_URL = "/swagger-ui.html";
    public static final String DOCUMENTATION_RESOURCE_URL = "/swagger-resources";
    public static final String DOCUMENTATION_WEBJARS = "/webjars";

    private final String BEARER_AUTHORIZATION = "Bearer Auth";
    private final String BEARER_SCHEMA = "bearer";
    private final String BEARER_FORMAT = "JWT";

    @Value("${springdoc.documentation.apiVersion}")
    private String apiVersion;

    @Value("${springdoc.documentation.title}")
    private String title;

    @Value("${springdoc.documentation.description}")
    private String description;


    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTHORIZATION))
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTHORIZATION, securityScheme())
                )
                .info(apiInfo());
    }

    /**
     * Include more information related with the Rest Api documentation
     *
     * @return {@link Info}
     */
    private Info apiInfo() {
        return new Info().title(title).description(description).version(apiVersion);
    }

    private SecurityScheme securityScheme() {
        return new SecurityScheme()
                .name(BEARER_AUTHORIZATION)
                .type(SecurityScheme.Type.HTTP)
                .scheme(BEARER_SCHEMA)
                .bearerFormat(BEARER_FORMAT);
    }

}
