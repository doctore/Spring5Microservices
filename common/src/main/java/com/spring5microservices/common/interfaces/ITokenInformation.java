package com.spring5microservices.common.interfaces;

import java.util.Map;

/**
 * Functionality related with tokens used mainly for authorization purpose.
 */
public interface ITokenInformation {

    /**
     * Return the data required for the token generation.
     *
     * @return {@link Map} with information to include
     */
    Map<String, Object> getTokenInformation();

}
