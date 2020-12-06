package com.gatewayserver.configuration.documentation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

/**
 * Used to configure the different Swagger documentation of the existing microservices
 */
@Configuration
@EnableSwagger2
@Primary
public class DocumentationConfiguration implements SwaggerResourcesProvider {

    private static final String DOCUMENTATION_API_VERSION = "1.0";

    @Value("${springfox.documentation.swagger.v2.path}")
    private String documentationPath;

    @Value("#{'${restApi.documentedServices}'.split(',')}")
    private List<String> documentedRestApis;

    @Autowired
    private RouteLocator routeLocator;


    @Override
    public List<SwaggerResource> get() {
        return ofNullable(routeLocator)
                .map(rl -> routeLocator.getRoutes().stream()
                                .filter(route -> documentedRestApis.contains(route.getId()))
                                .map(r -> swaggerResource(r.getId(),
                                        r.getFullPath().replace("/**", documentationPath),
                                        DOCUMENTATION_API_VERSION))
                                .collect(Collectors.toList())
                )
                .orElseGet(ArrayList::new);
    }

    /**
     * Carry out the returned {@link SwaggerResource} with the given information.
     *
     * @param name
     *    Identifier of the resource
     * @param url
     *    Location of the resource
     * @param version
     *    Current version of the resource
     *
     * @return {@link SwaggerResource}
     */
    private SwaggerResource swaggerResource(String name, String url, String version) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setUrl(url);
        swaggerResource.setSwaggerVersion(version);
        return swaggerResource;
    }

}
