package com.spring5microservices.common.util;

import com.spring5microservices.common.collection.tuple.Tuple;
import com.spring5microservices.common.collection.tuple.Tuple2;
import lombok.experimental.UtilityClass;

import java.util.Base64;

import static com.spring5microservices.common.util.StringUtil.getOrEmpty;
import static java.lang.String.format;

@UtilityClass
public class HttpUtil {

    public static final String BASIC_AUTHORIZATION_HEADER = "Basic ";
    public static final String BASIC_AUTHORIZATION_HEADER_SEPARATOR = ":";


    /**
     * Build the required Basic Authentication header value.
     *
     * @param username
     *    User's identifier
     * @param password
     *    User's password
     *
     * @return {@link String}
     *
     * @throws IllegalArgumentException if given {@code username} contains the character {@link HttpUtil#BASIC_AUTHORIZATION_HEADER_SEPARATOR}
     */
    public static String encodeBasicAuthentication(final String username,
                                                   final String password) {
        final String finalUsername = getOrEmpty(username);
        final String finalPassword = getOrEmpty(password);
        if (finalUsername.contains(BASIC_AUTHORIZATION_HEADER_SEPARATOR)) {
            throw new IllegalArgumentException(
                    format("Given username: %s must not contain the character: '%s'",
                            username, BASIC_AUTHORIZATION_HEADER_SEPARATOR)
            );
        }
        final String auth =
                finalUsername +
                        BASIC_AUTHORIZATION_HEADER_SEPARATOR +
                        finalPassword;

        byte[] encodedAuth = Base64.getEncoder()
                .encode(auth.getBytes());

        return BASIC_AUTHORIZATION_HEADER +
                new String(encodedAuth);
    }


    /**
     * Decode provided encoded Basic Authentication header value.
     *
     * @param encodeBasicAuth
     *    {@link String} with an encoded Basic Authentication header value
     *
     * @return {@link Tuple2} with username as first value and password as second one
     *
     * @throws IllegalArgumentException if given {@code encodeBasicAuth} does not start with {@link HttpUtil#BASIC_AUTHORIZATION_HEADER} or
     *                                  if given {@code encodeBasicAuth} without {@link HttpUtil#BASIC_AUTHORIZATION_HEADER} is not in valid Base64 scheme or
     *                                  if after decoding {@code encodeBasicAuth}, it does not contain {@link HttpUtil#BASIC_AUTHORIZATION_HEADER_SEPARATOR}
     */
    public static Tuple2<String, String> decodeBasicAuthentication(final String encodeBasicAuth) {
        final String finalEncodeBasicAuth = getOrEmpty(encodeBasicAuth);
        if (!finalEncodeBasicAuth.startsWith(BASIC_AUTHORIZATION_HEADER)) {
            throw new IllegalArgumentException(
                    format("Given encode basic authentication: %s must start with: '%s'",
                            encodeBasicAuth, BASIC_AUTHORIZATION_HEADER)
            );
        }
        final String base64Credentials = finalEncodeBasicAuth.substring(
                BASIC_AUTHORIZATION_HEADER.length()
        ).trim();

        final String rawBasicAuth = new String(
                Base64.getDecoder()
                        .decode(base64Credentials.getBytes())
        );
        int delim = rawBasicAuth.indexOf(BASIC_AUTHORIZATION_HEADER_SEPARATOR);
        if (-1 == delim) {
            throw new IllegalArgumentException(
                    format("Using the given encode basic authentication: %s after removing basic authentication header: %s"
                                    + "and decode it: %s, was not possible to find the expected username and password separator: '%s'",
                            encodeBasicAuth,
                            BASIC_AUTHORIZATION_HEADER,
                            rawBasicAuth,
                            BASIC_AUTHORIZATION_HEADER_SEPARATOR
                    )
            );
        }
        return Tuple.of(
                rawBasicAuth.substring(0, delim),
                rawBasicAuth.substring(delim + 1)
        );
    }

}
