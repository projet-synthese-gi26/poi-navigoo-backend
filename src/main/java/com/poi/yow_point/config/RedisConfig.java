package com.poi.yow_point.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poi.yow_point.presentation.dto.PointOfInterestDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisTemplate<String, PointOfInterestDTO> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory factory, ObjectMapper objectMapper) {

        Jackson2JsonRedisSerializer<PointOfInterestDTO> serializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, PointOfInterestDTO.class);

        RedisSerializationContext.RedisSerializationContextBuilder<String, PointOfInterestDTO> builder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());

        RedisSerializationContext<String, PointOfInterestDTO> context =
                builder.value(serializer).build();

        return new ReactiveRedisTemplate<>(factory, context);
    }
}
