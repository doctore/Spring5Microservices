package com.order.configuration.documentation;

import com.order.configuration.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Arrays;

import static java.util.Arrays.asList;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * Used to configure the Swagger documentation of the current microservice
 */
@Configuration
@EnableSwagger2
public class DocumentationConfiguration {

    private final String AUTHORIZATION_TOKEN_TEMPLATE = "%JwtToken";

    @Value("${springfox.documentation.apiVersion}")
    private String apiVersion;

    @Value("${springfox.documentation.title}")
    private String title;

    @Value("${springfox.documentation.description}")
    private String description;


    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .select()
                .apis(RequestHandlerSelectors.basePackage(Constants.PATH.CONTROLLER))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
                .securitySchemes(asList(apiKey()))
                .securityContexts(asList(securityContext()));
    }


    /**
     * Include more information related with the Rest Api documentation
     *
     * @return {@link ApiInfo}
     */
    private ApiInfo apiInfo() {
        return new ApiInfo(title, description, apiVersion,
                null, null, null, null,
                new ArrayList<>());
    }

    private ApiKey apiKey() {
        return new ApiKey(AUTHORIZATION_TOKEN_TEMPLATE, AUTHORIZATION, "Header");
    }

    private SecurityReference securityReference() {
        return SecurityReference.builder()
                .reference(AUTHORIZATION_TOKEN_TEMPLATE)
                .scopes(new AuthorizationScope[0])
                .build();
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(Arrays.asList(securityReference()))
                .build();
    }

}