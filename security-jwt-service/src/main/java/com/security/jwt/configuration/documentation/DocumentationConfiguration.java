package com.security.jwt.configuration.documentation;

import com.security.jwt.configuration.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

/**
 * Used to configure the Swagger documentation of the current microservice
 */
@Configuration
@EnableSwagger2
public class DocumentationConfiguration {

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
                .apiInfo(apiInfo());
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

}
