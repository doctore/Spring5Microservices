package com.spring5microservices.common.service;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = EncryptorService.class)
public class EncryptorServiceTest {

    @Autowired
    private EncryptorService service;


    static Stream<Arguments> encryptTestCases() {
        String toEncrypt = "Raw information to encrypt";
        String password = "23Rhf(@_2-Poas";
        return Stream.of(
                //@formatter:off
                //            toEncrypt,   password,   expectedException
                Arguments.of( null,        null,       IllegalArgumentException.class ),
                Arguments.of( toEncrypt,   null,       IllegalArgumentException.class ),
                Arguments.of( toEncrypt,   password,   null )
        ); //@formatter:on
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("encryptTestCases")
    @DisplayName("encrypt: test cases")
    public void encrypt_testCases(String toEncrypt, String password, Class<? extends Exception> expectedException) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> service.encrypt(toEncrypt, password));
        }
        else {
            assertNotNull(service.encrypt(toEncrypt, password));
        }
    }


    static Stream<Arguments> decryptTestCases() {
        String toDecrypt = "SGB2pnNj1NUZ7IKV6RpEes/cv76rwV/0fNopGgnIuVeyuy1wdOomylv4i0geFJBHQe0B4VAyjcWGD6gpCEtszrGwcDiu1w==";
        String password = "23Rhf(@_2-Poas";
        String decrypted = "Raw information to encrypt";
        return Stream.of(
                //@formatter:off
                //            toDecrypt,   password,   expectedException,                expectedResult
                Arguments.of( null,        null,       IllegalArgumentException.class,   null ),
                Arguments.of( toDecrypt,   null,       IllegalArgumentException.class,   null ),
                Arguments.of( toDecrypt,   password,   null,                             decrypted )
        ); //@formatter:on
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("decryptTestCases")
    @DisplayName("decrypt: test cases")
    public void decrypt_testCases(String toDecrypt, String password, Class<? extends Exception> expectedException,
                                  String expectedResult) {
        if (null != expectedException) {
            assertThrows(expectedException, () -> service.decrypt(toDecrypt, password));
        }
        else {
            assertEquals(expectedResult, service.decrypt(toDecrypt, password));
        }
    }

}
