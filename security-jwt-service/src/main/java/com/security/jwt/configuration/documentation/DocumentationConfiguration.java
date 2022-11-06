package com.security.jwt.configuration.documentation;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class DocumentationConfiguration {

    @Value("${springdoc.api-docs.path}")
    private String apiDocsPath;

    @Value("${springdoc.swagger-ui.path}")
    private String apiUiUrl;

    @Value("${springdoc.documentation.apiVersion}")
    private String apiVersion;

    @Value("${springdoc.documentation.description}")
    private String description;

    @Value("${springdoc.documentation.title}")
    private String title;

    @Value("${springdoc.security.authorization}")
    private String securityAuthorization;

    @Value("${springdoc.security.schema}")
    private String securitySchema;

    @Value("${springdoc.webjars.prefix}")
    private String webjarsUrl;


    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(
                        new SecurityRequirement()
                                .addList(securityAuthorization)
                )
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        securityAuthorization,
                                        securityScheme()
                                )
                )
                .info(apiInfo());
    }

    /**
     * Include more information related with the Rest Api documentation
     *
     * @return {@link Info}
     */
    private Info apiInfo() {
        return new Info()
                .title(title)
                .description(description)
                .version(apiVersion);
    }


    private SecurityScheme securityScheme() {
        return new SecurityScheme()
                .name(securityAuthorization)
                .type(SecurityScheme.Type.HTTP)
                .scheme(securitySchema);
    }

}
