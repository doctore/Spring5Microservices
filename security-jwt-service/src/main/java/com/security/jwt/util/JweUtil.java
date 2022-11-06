package com.security.jwt.util;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.Header;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObject;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.KeyException;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.security.jwt.exception.TokenInvalidException;
import com.spring5microservices.common.exception.TokenExpiredException;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.text.ParseException;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;

@AllArgsConstructor
@Component
@Log4j2
public class JweUtil {

    // JWE encryption options
    private final JWEAlgorithm jweAlgorithm = JWEAlgorithm.DIR;
    private final EncryptionMethod encryptionMethod = EncryptionMethod.A128CBC_HS256;

    @Lazy
    private final JwsUtil jwsUtil;


    /**
     *    Using the given {@code informationToInclude} generates a valid nested JWS and JWE token (signed + encrypted JWT),
     * signed with the selected {@link JWSAlgorithm} and {@code signatureSecret}.
     *
     *    For the encryption process, the algorithm is AES using a direct encryption with a shared symmetric key provided
     * in {@code encryptionSecret}.
     *
     * @param informationToInclude
     *    {@link Map} with the information to include in the returned JWS token
     * @param signatureAlgorithm
     *    {@link JWSAlgorithm} used to sign the JWS token
     * @param signatureSecret
     *    {@link String} used to sign the JWS token
     * @param encryptionSecret
     *    {@link String} used to encrypt the JWS token
     * @param expirationTimeInSeconds
     *    How many seconds the JWS toke will be valid
     *
     * @return {@link String} with the JWS
     *
     * @throws IllegalArgumentException if {@code signatureAlgorithm}, {@code jwtSignatureSecret} or {@code encryptionSecret}
     *                                  are {@code null}
     */
    public String generateToken(final Map<String, Object> informationToInclude,
                                final JWSAlgorithm signatureAlgorithm,
                                final String signatureSecret,
                                final String encryptionSecret,
                                final long expirationTimeInSeconds) {
        Assert.hasText(encryptionSecret, "encryptionSecret cannot be null or empty");
        String jwsToken = jwsUtil.generateToken(informationToInclude, signatureAlgorithm, signatureSecret, expirationTimeInSeconds);
        return encryptJwsToken(jwsToken, encryptionSecret);
    }


    /**
     * Get the information included in the given JWE {@code jweToken} that match with the given {@code keysToInclude}.
     *
     * @param jweToken
     *    JWE token to extract the required information
     * @param signatureSecret
     *    {@link String} used to sign the JWS token
     * @param encryptionSecret
     *    {@link String} used to encrypt the JWS token
     * @param keysToInclude
     *    {@link Set} of {@link String} with the {@code key}s to extract from JWS token
     *
     * @return {@link Map} of {@link String} - {@link Object} with the requested information
     *
     * @throws IllegalArgumentException if {@code jweToken}, {@code signatureSecret} or {@code encryptionSecret} are {@code null} or empty
     * @throws TokenInvalidException if {@code token} is not a JWS one or was not signed using {@code signatureSecret}
     * @throws TokenExpiredException if {@code token} has expired
     */
    public Map<String, Object> getPayloadKeys(final String jweToken,
                                              final String signatureSecret,
                                              final String encryptionSecret,
                                              final Set<String> keysToInclude) {
        Assert.hasText(encryptionSecret, "encryptionSecret cannot be null or empty");
        String jwsToken = decryptJweToken(jweToken, encryptionSecret);
        return jwsUtil.getPayloadKeys(jwsToken, signatureSecret, keysToInclude);
    }


    /**
     * Get the information included in the given JWE {@code jweToken} except the given {@code keysToExclude}.
     *
     * @param jweToken
     *    JWE token to extract the required information
     * @param signatureSecret
     *    {@link String} used to sign the JWS token
     * @param encryptionSecret
     *    {@link String} used to encrypt the JWS token
     * @param keysToExclude
     *    {@link Set} of {@link String} with the {@code key}s to exclude from JWS token
     *
     * @return {@link Map} of {@link String} - {@link Object} with the remaining information
     *
     * @throws IllegalArgumentException if {@code jweToken}, {@code signatureSecret} or {@code encryptionSecret} are {@code null} or empty
     * @throws TokenInvalidException if {@code token} is not a JWS one or was not signed using {@code signatureSecret}
     * @throws TokenExpiredException if {@code token} has expired
     */
    public Map<String, Object> getPayloadExceptGivenKeys(final String jweToken,
                                                         final String signatureSecret,
                                                         final String encryptionSecret,
                                                         final Set<String> keysToExclude) {
        Assert.hasText(encryptionSecret, "encryptionSecret cannot be null or empty");
        String jwsToken = decryptJweToken(jweToken, encryptionSecret);
        return jwsUtil.getPayloadExceptGivenKeys(jwsToken, signatureSecret, keysToExclude);
    }


    /**
     * Get the information included in the given JWE {@code jweToken} WITHOUT ANY VERIFICATION.
     *
     * @param jweToken
     *    JWE token to extract the required information
     * @param encryptionSecret
     *    {@link String} used to encrypt the JWS token
     *
     * @return {@link Map} of {@link String} - {@link Object}
     *
     * @throws IllegalArgumentException if {@code jweToken} or {@code encryptionSecret} are {@code null} or empty
     */
    public Map<String, Object> getRawPayload(final String jweToken,
                                             final String encryptionSecret) {
        Assert.hasText(jweToken, "encryptionSecret cannot be null or empty");
        String jwsToken = decryptJweToken(jweToken, encryptionSecret);
        return jwsUtil.getRawPayload(jwsToken);
    }


    /**
     * Return if the given {@code token} is a JWE one.
     *
     * @param token
     *    {@link String} with the {@code token} to check
     *
     * @return {@code true} if the {@code token} is an JWE one, {@code false} otherwise
     *
     * @throws IllegalArgumentException if {@code token} is {@code null} or empty or there was a problem checking it
     */
    public boolean isJweToken(final String token) {
        Assert.hasText(token, "token cannot be null or empty");
        try {
            Base64URL[] parts = JOSEObject.split(token);
            Map<String, Object> jsonObjectProperties = JSONObjectUtils.parse(parts[0].decodeToString());
            Algorithm alg = Header.parseAlgorithm(jsonObjectProperties);
            return (alg instanceof JWEAlgorithm);

        } catch (ParseException e) {
            throw new IllegalArgumentException(
                    format("The was a problem trying to figure out the type of token: %s", token),
                    e
            );
        }
    }


    /**
     * Encrypt the given JWS token using algorithms and encryption method defined by default.
     *
     * @param jwsToken
     *    {@link String} with the JWS token to encrypt
     * @param encryptionSecret
     *    {@link String} used to encrypt the JWS token
     *
     * @return {@link String} with the JWE token
     *
     * @throws IllegalArgumentException it there was a problem encrypting the JWS token
     */
    private String encryptJwsToken(final String jwsToken,
                                   final String encryptionSecret) {
        if (!jwsUtil.isJwsToken(jwsToken)) {
            throw new TokenInvalidException(format("The token: %s is not a JWS one", jwsToken));
        }
        try {
            JWEObject jweObject = new JWEObject(
                    new JWEHeader.Builder(jweAlgorithm, encryptionMethod)
                            .contentType("JWT")   // Required to indicate nested JWT
                            .build(),
                    new Payload(jwsToken));
            jweObject.encrypt(new DirectEncrypter(encryptionSecret.getBytes()));
            return jweObject.serialize();

        } catch (JOSEException e) {
            throw new IllegalArgumentException(
                    format("The was a problem trying to encrypt the JWS token: %s", jwsToken),
                    e
            );
        }
    }

    /**
     * Decrypt the given JWE token returning the nested JWS one.
     *
     * @param jweToken
     *    {@link String} with the JWE token to decrypt
     * @param encryptionSecret
     *    {@link String} used to encrypt the JWS token
     *
     * @return {@link String} with the JWS nested token
     *
     * @throws TokenInvalidException if the {@code token} is not a JWE one or there was a problem decrypting it
     */
    private String decryptJweToken(final String jweToken,
                                   final String encryptionSecret) {
        if (!isJweToken(jweToken))
            throw new TokenInvalidException(format("The token: %s is not a JWE one", jweToken));
        try {
            JWEObject jweObject = JWEObject.parse(jweToken);
            jweObject.decrypt(new DirectDecrypter(encryptionSecret.getBytes()));
            return jweObject.getPayload().toSignedJWT().serialize();

        } catch (JOSEException | ParseException e) {
            if (e instanceof KeyException) {
                throw new IllegalArgumentException(
                        "The was a problem with the given encryptionSecret",
                        e
                );
            }
            throw new TokenInvalidException(
                    format("The was a problem trying to decrypt the JWE token: %s", jweToken),
                    e
            );
        }
    }

}
