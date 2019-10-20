package com.security.jwt.configuration.documentation;

import com.security.jwt.configuration.Constants;
import com.security.jwt.configuration.rest.RestRoutes;
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

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).useDefaultResponseMessages(false)
                                                      .select()
                                                      .apis(RequestHandlerSelectors.any())
                                                      .paths(PathSelectors.ant(RestRoutes.SECURITY.ROOT + "/**"))
                                                      .build()
                                                      .apiInfo(apiInfo());
    }


    /**
     * Include more information related with the Rest Api documentation
     *
     * @return {@link ApiInfo}
     */
    private ApiInfo apiInfo() {
        return new ApiInfo("Security JWT Rest Api",
                           "Services related with authentication/authorization functionality",
                           Constants.DOCUMENTATION_API_VERSION,
                           null, null, null, null,
                           new ArrayList<>());
    }

}
