package com.security.oauth.configuration.rest;

import org.springframework.data.util.Pair;

public final class RestRoutes {

    public static final class SECURITY_OAUTH {
        public static final String ROOT = "/security/oauth";
    }

    /**
     * Includes all equivalences between Oauth2 default Urls and new ones in this project
     */
    public final static Pair<String, String> ACCESS_TOKEN_URI = Pair.of("/oauth/token", SECURITY_OAUTH.ROOT + "/token");
    public final static Pair<String, String> CHECK_TOKEN_URI = Pair.of("/oauth/check_token", SECURITY_OAUTH.ROOT + "/check_token");
    public final static Pair<String, String> USER_AUTHORIZATION_URI = Pair.of("/oauth/authorize", SECURITY_OAUTH.ROOT + "/authorize");

}
