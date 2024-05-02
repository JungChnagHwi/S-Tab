package com.sixb.note.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories
public class RedisConfig {
    @Value("${jwt.redis.host}")
    private String redisHost;

    @Value("${jwt.redis.port}")
    private int redisPort;

//    @Value("${jwt.redis.password}")
//    private String redisPassword;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        redisConfiguration.setHostName(redisHost);
//        redisConfiguration.setPassword(redisPassword);
        redisConfiguration.setPort(redisPort);

        return new LettuceConnectionFactory(redisConfiguration);
    }
}
