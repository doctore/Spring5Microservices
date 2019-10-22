package com.security.jwt.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(SpringExtension.class)
public class SecurityServiceTest {

    /*
    @Mock
    private ApplicationContext mockApplicationContext;

    @Mock
    private JwtClientDetailsService mockJwtClientDetailsService;

    @Mock
    private JwtUtil mockJwtUtil;

    @Mock
    private TextEncryptor mockEncryptor;

    private SecurityService securityService;


    @BeforeEach
    public void init() {
        securityService = new SecurityService(mockApplicationContext, mockJwtClientDetailsService, mockJwtUtil, mockEncryptor);
    }


    static Stream<Arguments> loginTestCases() {
        String clientId = AuthenticationGeneratorEnum.SPRING5_MICROSERVICES.getClientId();
        String username = "username value";
        String password = "password value";
        Spring5MicroserviceAuthenticationGenerator authenticationGenerator = Mockito.mock(Spring5MicroserviceAuthenticationGenerator.class);
        JwtClientDetails clientDetails = buildDefaultClientDetails(clientId);
        Optional<RawAuthenticationInformationDto> rawAuthenticationInformation = of(buildDefaultRawAuthenticationInformation());
        return Stream.of(
                //@formatter:off
                //            clientId,   username,   password,   authenticationGenerator,   clientDetailsResult,   rawAuthenticationInformation,   isResultEmpty
                Arguments.of( null,       null,       null,       null,                      clientDetails,         empty(),                        true ),
                Arguments.of( clientId,   null,       null,       null,                      clientDetails,         empty(),                        true ),
                Arguments.of( clientId,   username,   null,       null,                      clientDetails,         empty(),                        true ),
                Arguments.of( clientId,   username,   password,   authenticationGenerator,   clientDetails,         empty(),                        true ),
                Arguments.of( clientId,   username,   password,   authenticationGenerator,   clientDetails,         rawAuthenticationInformation,   false )
        ); //@formatter:on
    }

    @ParameterizedTest
    @MethodSource("loginTestCases")
    @DisplayName("login: test cases")
    public void login_testCases(String clientId, String username, String password, Spring5MicroserviceAuthenticationGenerator authenticationGenerator,
                                JwtClientDetails clientDetailsResult, Optional<RawAuthenticationInformationDto> rawAuthenticationInformation,
                                boolean isResultEmpty) {
        Optional<String> jwtToken = of("JWT token");
        String jwtSecret = "secretKey";

        when(mockApplicationContext.getBean(Spring5MicroserviceAuthenticationGenerator.class)).thenReturn(authenticationGenerator);
        when(mockJwtClientDetailsService.findByClientId(eq(clientId))).thenReturn(clientDetailsResult);
        when(mockEncryptor.decrypt(anyString())).thenReturn(jwtSecret);

        if (null != authenticationGenerator){
            when(authenticationGenerator.getRawAuthenticationInformation(username, password)).thenReturn(rawAuthenticationInformation);
        }
        if (null != clientDetailsResult) {
            when(mockJwtUtil.generateJwtToken(anyMap(), eq(clientDetailsResult.getJwtAlgorithm()), anyString(), anyInt())).thenReturn(jwtToken);
        }
        Optional<AuthenticationInformationDto> result = securityService.login(clientId, username, password);
        verifyloginResult(clientDetailsResult, rawAuthenticationInformation, result, isResultEmpty);
    }


    private void verifyloginResult(JwtClientDetails clientDetailsResult, Optional<RawAuthenticationInformationDto> rawAuthenticationInformation,
                                   Optional<AuthenticationInformationDto> result, boolean isResultEmpty) {
        if (isResultEmpty)
            assertFalse(result.isPresent());
        else {
            assertTrue(result.isPresent());
            assertNotNull(result.get().getAccessToken());
            assertNotNull(result.get().getRefreshToken());
            assertEquals(clientDetailsResult.getAccessTokenValidity(), result.get().getExpiresIn());
            assertEquals(clientDetailsResult.getTokenType(), result.get().getTokenType());
            assertNull(result.get().getScope());
            assertFalse(result.get().getJwtId().isEmpty());
            if (!rawAuthenticationInformation.isPresent()) {
                assertNull(result.get().getAdditionalInfo());
            }
            else {
                assertEquals(rawAuthenticationInformation.get().getAdditionalTokenInformation(), result.get().getAdditionalInfo());
            }
        }
    }

    private static JwtClientDetails buildDefaultClientDetails(String clientId) {
        return JwtClientDetails.builder()
                .clientId(clientId)
                .jwtAlgorithm(SignatureAlgorithm.HS256)
                .jwtSecret(Constants.JWT_SECRET_PREFIX + "secretKey")
                .accessTokenValidity(100)
                .refreshTokenValidity(150)
                .tokenType("Bearer")
                .build();
    }

    private static RawAuthenticationInformationDto buildDefaultRawAuthenticationInformation() {
        return RawAuthenticationInformationDto.builder()
                .accessTokenInformation(new HashMap<String, Object>() {{
                    put(USERNAME.getKey(), "username value");
                    put(AUTHORITIES.getKey(), asList("admin"));
                }})
                .refreshTokenInformation(new HashMap<String, Object>() {{
                    put(USERNAME.getKey(), "username value");
                }})
                .additionalTokenInformation(new HashMap<String, Object>() {{
                    put(NAME.getKey(), "name value");
                }})
                .build();
    }

     */

}
