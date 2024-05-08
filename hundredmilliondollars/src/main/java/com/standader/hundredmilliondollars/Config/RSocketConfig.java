package com.standader.hundredmilliondollars.Config;

import org.springframework.boot.rsocket.server.RSocketServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.security.config.annotation.rsocket.RSocketSecurity;
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor;
import org.springframework.web.util.pattern.PathPatternRouteMatcher;

import com.standader.hundredmilliondollars.Functions.RedisStreamSubscriber;

import io.rsocket.plugins.SocketAcceptorInterceptor;

@Configuration
public class RSocketConfig {
    @Bean
    public RSocketMessageHandler rSocketMessageHandler() {
        RSocketMessageHandler handler = new RSocketMessageHandler();
        handler.setRSocketStrategies(rSocketStrategies());
        return handler;
    }

    @Bean
    public RSocketStrategies rSocketStrategies() {
        
        return RSocketStrategies.builder()
        
        .decoder(new Jackson2JsonDecoder())
        .encoder(new Jackson2JsonEncoder())
        .routeMatcher(new PathPatternRouteMatcher())
        .build();
    }

    @Bean
    @Primary
    RSocketServerCustomizer customSpringSecurityRSocketSecurity(SocketAcceptorInterceptor interceptor) {
        return (server) -> server.interceptors((registry) -> registry.forSocketAcceptor(interceptor));
    }

    @Bean
    @Primary
    PayloadSocketAcceptorInterceptor rsocketInterceptor(RSocketSecurity rsocket) {
        rsocket
            .authorizePayload(authorize ->
                authorize
                    .anyExchange().permitAll()
            );

        return rsocket.build();
    }
    
    // @Bean
    // public LettuceConnectionFactory redisConnectionFactory() {
    //     RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
    //     config.setHostName("localhost");
    //     config.setPort(6379);
    //     // config.setPassword(RedisPassword.of("yourRedisPassword")); // 비밀번호가 필요한 경우

    //     return new LettuceConnectionFactory(config);
    // }


    
}
