package com.gatewayserver.configuration.documentation;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to configure the different Swagger documentation of the existing microservices
 */
@RequiredArgsConstructor
@Configuration
@Primary
public class DocumentationConfiguration implements SwaggerResourcesProvider {

    private final String ALLOW_ALL_ENDPOINTS = "/**";
    private final String INTERNAL_PATH_KEY_OF_GATEWAY_FILTER = "_genkey_0";

    @Value("${springfox.documentation.swagger.v3.path}")
    private String documentationPath;

    @Value("#{'${springfox.documentation.swagger.documentedServices}'.split(',')}")
    private List<String> documentedRestApis;

    @Value("${springfox.documentation.swagger.apiVersion}")
    private String apiVersion;

    private final RouteDefinitionLocator routeDefinitionLocator;


    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> resources = new ArrayList<>();
        routeDefinitionLocator.getRouteDefinitions().subscribe(
                routeDefinition -> {
                    String resourceName = routeDefinition.getId();
                    if (documentedRestApis.contains(resourceName)) {
                        String location =
                                routeDefinition
                                        .getPredicates()
                                        .get(0)
                                        .getArgs()
                                        .get(INTERNAL_PATH_KEY_OF_GATEWAY_FILTER)
                                        .replace(ALLOW_ALL_ENDPOINTS, documentationPath);
                        resources.add(
                                swaggerResource(
                                        resourceName,
                                        location,
                                        apiVersion
                                )
                        );
                    }
                }
        );
        return resources;
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
    private SwaggerResource swaggerResource(final String name,
                                            final String url,
                                            final String version) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setUrl(url);
        swaggerResource.setSwaggerVersion(version);
        return swaggerResource;
    }

}
