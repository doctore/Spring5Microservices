package com.pizza.configuration.rest;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.util.concurrent.TimeUnit;

@Configuration
public class RestConfiguration {

    @Value("${rest.connect.timeoutInMilliseconds}")
    private int connectTimeoutMillis;

    @Value("${rest.read.timeoutInMilliseconds}")
    private int readTimeoutMillis;

    @Value("${rest.write.timeoutInMilliseconds}")
    private int writeTimeoutMillis;

    @Bean
    public WebClient webClient() {
        TcpClient tcpClient = TcpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMillis)
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(readTimeoutMillis, TimeUnit.MILLISECONDS));
                    connection.addHandlerLast(new WriteTimeoutHandler(writeTimeoutMillis, TimeUnit.MILLISECONDS));
                });
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
                .build();
    }

}

