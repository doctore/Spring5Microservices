package com.security.jwt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;

@Component
@Log4j2
public class JwtUtil {

    /**
     *    Using the given {@code informationToInclude} generates a valid JWT token encrypted with the selected
     * {@link SignatureAlgorithm}
     *
     * @param informationToInclude
     *    {@link Map} with the information to include in the returned JWT token
     * @param signatureAlgorithm
     *    {@link SignatureAlgorithm} used to encrypt the JWT token
     * @param jwtSecretKey
     *    {@link String} used to encrypt the JWT token
     * @param expirationTimeInSeconds
     *    How many seconds the JWT toke will be valid
     *
     * @return {@link Optional} of {@link String} with the JWT
     *
     * @throws IllegalArgumentException if {@code signatureAlgorithm} or {@code jwtSecretKey} are {@code null}
     */
    public Optional<String> generateJwtToken(Map<String, Object> informationToInclude, SignatureAlgorithm signatureAlgorithm,
                                             String jwtSecretKey, long expirationTimeInSeconds) {
        Assert.notNull(signatureAlgorithm, "signatureAlgorithm cannot be null");
        Assert.notNull(jwtSecretKey, "jwtSecretKey cannot be null");
        return ofNullable(informationToInclude)
                .map(ud -> {
                    Date now = new Date();
                    Date expirationDate = new Date(now.getTime() + (expirationTimeInSeconds * 1000));
                    return Jwts.builder()
                            .setClaims(informationToInclude)
                            .setIssuedAt(now)
                            .setExpiration(expirationDate)
                            .signWith(getSigningKey(jwtSecretKey), signatureAlgorithm)
                            .compact();
                });
    }


    /**
     * Get the given {@code keyToSearch} included in the given JWT {@code token}.
     *
     * @param token
     *    JWT token to extract the required information
     * @param jwtSecretKey
     *    {@link String} used to encrypt the JWT token
     * @param keyToSearch
     *    Information to get from the given token
     * @param valueClazz
     *    {@link Class} of the returned data related with given {@code key}
     *
     * @return {@link Optional} if a value related with the given {@code key} exists, {@link Optional#empty()} otherwise
     *
     * @throws IllegalArgumentException if {@code token} or {@code jwtSecretKey} are null or empty
     * @throws JwtException if an error occurs parsing the given {@code token}
     */
    public <T> Optional<T> getKey(String token, String jwtSecretKey, String keyToSearch, Class<T> valueClazz) {
        return ofNullable(keyToSearch)
                .map(key -> getClaimFromToken(token, jwtSecretKey, (claims) -> claims.get(key, valueClazz)));
    }


    /**
     * Get the information included in the given JWT {@code token} that match with the given {@code keysToInclude}.
     *
     * @param token
     *    JWT token to extract the required information
     * @param jwtSecretKey
     *    {@link String} used to encrypt the JWT token
     * @param keysToInclude
     *    {@link Set} of {@link String} with the {@code key}s to extract from Jwt token
     *
     * @return {@link Map} of {@link String} - {@link Object} with the requested information
     *
     * @throws IllegalArgumentException if {@code token} or {@code jwtSecretKey} are null or empty
     * @throws JwtException if an error occurs parsing the given {@code token}
     */
    public Map<String, Object> getKeys(String token, String jwtSecretKey, Set<String> keysToInclude) {
        return ofNullable(keysToInclude)
                .map(toInclude -> getAllClaimsFromToken(token, jwtSecretKey)
                                     .entrySet().stream()
                                     .filter(e -> keysToInclude.contains(e.getKey()))
                                     .collect(toMap(Map.Entry::getKey, Map.Entry::getValue)))
                .orElse(new HashMap<>());
    }


    /**
     * Get the information included in the given JWT {@code token} except the given {@code keysToExclude}.
     *
     * @param token
     *    JWT token to extract the required information
     * @param jwtSecretKey
     *    {@link String} used to encrypt the JWT token
     * @param keysToExclude
     *    {@link Set} of {@link String} with the {@code key}s to exclude from Jwt token
     *
     * @return {@link Map} of {@link String} - {@link Object} with the remaining information
     *
     * @throws IllegalArgumentException if {@code token} or {@code jwtSecretKey} are null or empty
     * @throws JwtException if an error occurs parsing the given {@code token}
     */
    public Map<String, Object> getExceptGivenKeys(String token, String jwtSecretKey, Set<String> keysToExclude) {
        return ofNullable(keysToExclude)
                .map(toExclude -> getAllClaimsFromToken(token, jwtSecretKey)
                                     .entrySet().stream()
                                     .filter(e -> !toExclude.contains(e.getKey()))
                                     .collect(toMap(Map.Entry::getKey, Map.Entry::getValue)))
                .orElse(new HashMap<>());
    }


    /**
     * Get from the given token the required information from its payload
     *
     * @param token
     *    JWT token to extract the required information
     * @param jwtSecretKey
     *    String used to encrypt the JWT token
     * @param claimsResolver
     *    {@link Function} used to know how to get the information
     *
     * @return {@link T} with the wanted part of the payload
     *
     * @throws IllegalArgumentException if {@code token} or {@code jwtSecretKey} are null or empty
     * @throws JwtException if an error occurs parsing the given {@code token}
     */
    private <T> T getClaimFromToken(String token, String jwtSecretKey, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token, jwtSecretKey);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract from the given token all the information included in the payload
     *
     * @param token
     *    JWT token to extract the required information
     * @param jwtSecretKey
     *    {@link String} used to encrypt the JWT token
     *
     * @return {@link Claims}
     *
     * @throws IllegalArgumentException if {@code token} or {@code jwtSecretKey} are null or empty
     * @throws JwtException if an error occurs parsing the given {@code token}
     */
    private Claims getAllClaimsFromToken(String token, String jwtSecretKey) {
        Assert.hasText(token, "token cannot be null or empty");
        Assert.hasText(jwtSecretKey, "jwtSecretKey cannot be null or empty");
        return Jwts.parser()
                .setSigningKey(getSigningKey(jwtSecretKey))
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Generate the information required to encrypt/decrypt the Jwt token.
     *
     * @param jwtSecretKey
     *    {@link String} used to encrypt the JWT token
     *
     * @return {@link Key}
     */
    private Key getSigningKey(String jwtSecretKey) {
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes());
    }

}
