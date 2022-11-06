package com.security.jwt.util;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.Header;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObject;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.security.jwt.exception.TokenInvalidException;
import com.spring5microservices.common.exception.TokenExpiredException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;
import static java.util.stream.Collectors.toMap;

@Component
@Log4j2
public class JwsUtil {

    private final static List<JWSAlgorithm> ALLOWED_JWS_ALGORITHMS = List.of(JWSAlgorithm.HS256, JWSAlgorithm.HS384, JWSAlgorithm.HS512);


    /**
     *    Using the given {@code informationToInclude} generates a valid JWS token (signed JWT) signed with the selected
     * {@link JWSAlgorithm} and {@code signatureSecret}.
     *
     * @param informationToInclude
     *    {@link Map} with the information to include in the returned JWS token
     * @param signatureAlgorithm
     *    {@link JWSAlgorithm} used to sign the JWS token
     * @param signatureSecret
     *    {@link String} used to sign the JWS token
     * @param expirationTimeInSeconds
     *    How many seconds the JWS toke will be valid
     *
     * @return {@link String} with the JWS
     *
     * @throws IllegalArgumentException if {@code signatureAlgorithm} or {@code jwtSignatureSecret} are {@code null}
     */
    public String generateToken(final Map<String, Object> informationToInclude,
                                final JWSAlgorithm signatureAlgorithm,
                                final String signatureSecret,
                                final long expirationTimeInSeconds) {
        Assert.notNull(signatureAlgorithm, "signatureAlgorithm cannot be null");
        Assert.hasText(signatureSecret, "signatureSecret cannot be null or empty");
        JWTClaimsSet claimsSet = addClaims(informationToInclude, expirationTimeInSeconds);
        SignedJWT signedJWT = getSignedJWT(signatureAlgorithm, signatureSecret, claimsSet);
        return signedJWT.serialize();
    }


    /**
     * Get the information included in the given JWS {@code token} that match with the given {@code keysToInclude}.
     *
     * @param jwsToken
     *    JWS token to extract the required information
     * @param signatureSecret
     *    {@link String} used to sign the JWS token
     * @param keysToInclude
     *    {@link Set} of {@link String} with the {@code key}s to extract from Jwt token
     *
     * @return {@link Map} of {@link String} - {@link Object} with the requested information
     *
     * @throws IllegalArgumentException if {@code jwsToken} or {@code signatureSecret} are {@code null} or empty
     * @throws TokenInvalidException if {@code token} is not a JWS one or was not signed using {@code signatureSecret}
     * @throws TokenExpiredException if {@code token} has expired
     */
    public Map<String, Object> getPayloadKeys(final String jwsToken,
                                              final String signatureSecret,
                                              final Set<String> keysToInclude) {
        if (null == keysToInclude) {
            return new HashMap<>();
        }
        return getAllClaimsFromToken(jwsToken, signatureSecret, true)
                .entrySet().stream()
                .filter(e -> keysToInclude.contains(e.getKey()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }


    /**
     * Get the information included in the given JWS {@code token} except the given {@code keysToExclude}.
     *
     * @param jwsToken
     *    JWS token to extract the required information
     * @param signatureSecret
     *    {@link String} used to sign the JWS token
     * @param keysToExclude
     *    {@link Set} of {@link String} with the {@code key}s to exclude from JWS token
     *
     * @return {@link Map} of {@link String} - {@link Object} with the remaining information
     *
     * @throws IllegalArgumentException if {@code token} or {@code signatureSecret} are {@code null} or empty
     * @throws TokenInvalidException if {@code jwsToken} is not a JWS one or was not signed using {@code signatureSecret}
     * @throws TokenExpiredException if {@code jwsToken} has expired
     */
    public Map<String, Object> getPayloadExceptGivenKeys(final String jwsToken,
                                                         final String signatureSecret,
                                                         final Set<String> keysToExclude) {
        if (null == keysToExclude) {
            return new HashMap<>();
        }
        return getAllClaimsFromToken(jwsToken, signatureSecret, true)
                .entrySet().stream()
                .filter(e -> !keysToExclude.contains(e.getKey()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }


    /**
     * Get the information included in the given JWS {@code jwsToken} WITHOUT ANY VERIFICATION.
     *
     * @param jwsToken
     *    JWS token to extract the required information
     *
     * @return {@link Map} of {@link String} - {@link Object}
     *
     * @throws IllegalArgumentException if {@code jwsToken} is {@code null} or empty
     */
    public Map<String, Object> getRawPayload(final String jwsToken) {
        return getAllClaimsFromToken(jwsToken, null, false);
    }


    /**
     * Return if the given {@code token} is a JWS one.
     *
     * @param token
     *    {@link String} with the {@code token} to check
     *
     * @return {@code true} if the {@code token} is an JWS one, {@code false} otherwise
     *
     * @throws IllegalArgumentException if {@code token} is {@code null} or empty or there was a problem checking it
     */
    public boolean isJwsToken(final String token) {
        Assert.hasText(token, "token cannot be null or empty");
        try {
            Base64URL[] parts = JOSEObject.split(token);
            Map<String, Object> jsonObjectProperties = JSONObjectUtils.parse(parts[0].decodeToString());
            Algorithm alg = Header.parseAlgorithm(jsonObjectProperties);
            return (alg instanceof JWSAlgorithm);

        } catch (ParseException e) {
            throw new IllegalArgumentException(
                    format("The was a problem trying to figure out the type of token: %s", token),
                    e
            );
        }
    }


    /**
     * Generate the information to include in the JWT token.
     *
     * @param informationToInclude
     *    {@link Map} with the information to include in the returned JWS token
     * @param expirationTimeInSeconds
     *    How many seconds the JWT toke will be valid
     *
     * @return {@link JWTClaimsSet}
     */
    private JWTClaimsSet addClaims(final Map<String, Object> informationToInclude,
                                   final long expirationTimeInSeconds) {
        JWTClaimsSet.Builder claimsSet = new JWTClaimsSet.Builder();
        if (null != informationToInclude) {
            informationToInclude.forEach(claimsSet::claim);
        }
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + (expirationTimeInSeconds * 1000));
        return claimsSet
                .issueTime(now)
                .expirationTime(expirationDate)
                .build();
    }

    /**
     * Generate the signed JWT token (a JWS one).
     *
     * @param signatureAlgorithm
     *    {@link JWSAlgorithm} used to sign the JWS token
     * @param signatureSecret
     *    {@link String} used to sign the JWS token
     * @param claimsSet
     *    {@link JWTClaimsSet} with the information to include
     *
     * @return {@link SignedJWT}
     *
     * @throws IllegalArgumentException it there was a problem creating the JWS token
     */
    private SignedJWT getSignedJWT(final JWSAlgorithm signatureAlgorithm,
                                   final String signatureSecret,
                                   final JWTClaimsSet claimsSet) {
        try {
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(signatureAlgorithm), claimsSet);
            signedJWT.sign(getSuitableSigner(signatureAlgorithm, signatureSecret));
            return signedJWT;

        } catch (JOSEException e) {
            throw new IllegalArgumentException("The was a problem trying to create a new JWS token", e);
        }
    }

    /**
     * Extract from the given token all the information included in the payload.
     *
     * @param jwsToken
     *    JWS token to extract the required information
     * @param signatureSecret
     *    {@link String} used to sign the JWS token
     * @param verifyToken
     *    If {@code true} the given token will be verified: signature and expiration time, {@code false} otherwise
     *
     * @return {@link Map} of {@link String}-{@link Object}
     *
     * @throws IllegalArgumentException if {@code jwsToken} is {@code null} or empty.
     *                                  when {@code verifyToken} is {@code true} => if {@code signatureSecret} are {@code null} or empty
     * @throws TokenInvalidException when {@code verifyToken} is {@code true} => if {@code token} is not a JWS one or
     *                               was not signed using {@code signatureSecret}
     * @throws TokenExpiredException when {@code verifyToken} is {@code true} => if {@code token} has expired
     */
    private Map<String, Object> getAllClaimsFromToken(final String jwsToken,
                                                      final String signatureSecret,
                                                      final boolean verifyToken) {
        Assert.hasText(jwsToken, "jwsToken cannot be null or empty");
        if (!isJwsToken(jwsToken)) {
            throw new TokenInvalidException(format("The token: %s is not a JWS one", jwsToken));
        }
        try {
            SignedJWT signedJWT = SignedJWT.parse(jwsToken);
            if (verifyToken) {
                Assert.hasText(signatureSecret, "signatureSecret cannot be null or empty");
                JWSVerifier verifier = getSuitableVerifier(signedJWT, signatureSecret);
                if (!signedJWT.verify(verifier)) {
                    throw new TokenInvalidException(format("The JWS token: %s does not match the provided signatureSecret", jwsToken));
                }
                Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
                if (null == expirationTime || expirationTime.before(new Date()))
                    throw new TokenExpiredException(format("The JWT token: %s has expired at %s", jwsToken, expirationTime));
            }
            return signedJWT.getJWTClaimsSet().getClaims();

        } catch (JOSEException | ParseException e) {
            throw new TokenInvalidException(
                    format("The was an error getting information included in JWS token: %s", jwsToken),
                    e
            );
        }
    }

    /**
     * Return the suitable {@link JWSSigner} taking into account the {@link JWSAlgorithm} used to sing the given JWS token.
     *
     * @param signatureAlgorithm
     *    {@link JWSAlgorithm} used to sign the JWS token
     * @param signatureSecret
     *    {@link String} used to sign the JWS token
     *
     * @return {@link JWSSigner}
     *
     * @throws KeyLengthException if {@code signatureSecret} has not enough length for the given {@code signatureAlgorithm}
     */
    private JWSSigner getSuitableSigner(final JWSAlgorithm signatureAlgorithm,
                                        final String signatureSecret) throws KeyLengthException {
        if (ALLOWED_JWS_ALGORITHMS.contains(signatureAlgorithm)) {
            return new MACSigner(signatureSecret);
        }
        throw new IllegalArgumentException(
                format("It was not possible to find a suitable signer for the signature algorithm: %s ", signatureAlgorithm)
        );
    }


    /**
     * Return the suitable {@link JWSVerifier} taking into account the {@link JWSAlgorithm} used to sing the given JWS token.
     *
     * @param signedJWT
     *    {@link SignedJWT} with JWS token
     * @param signatureSecret
     *    {@link String} used to sign the JWS token
     *
     * @return {@link JWSVerifier}
     *
     * @throws IllegalArgumentException if it was not possible to find a suitable {@link JWSVerifier}
     */
    private JWSVerifier getSuitableVerifier(final SignedJWT signedJWT,
                                            final String signatureSecret) throws JOSEException {
        JWSAlgorithm signatureAlgorithm = signedJWT.getHeader().getAlgorithm();
        if (ALLOWED_JWS_ALGORITHMS.contains(signatureAlgorithm)) {
            return new MACVerifier(signatureSecret);
        }
        throw new IllegalArgumentException(
                format("It was not possible to find a suitable verifier for the signature algorithm: %s ", signatureAlgorithm)
        );
    }

}
