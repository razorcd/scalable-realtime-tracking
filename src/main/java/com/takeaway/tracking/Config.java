package com.takeaway.tracking;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.Priority;
import java.time.Duration;

@Priority(0)
@Configuration
//@EnableBinding(LocationSink.class)
public class Config {

//    @Bean
//    public ReactiveRedisConnectionFactory connectionFactory() {
//        return new LettuceConnectionFactory("localhost", 6379);
//    }

//    @Bean
//    public ReactiveRedisConnectionFactory lettuceConnectionFactory() {
//
//        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
//                .useSsl().and()
//                .commandTimeout(Duration.ofSeconds(2))
//                .shutdownTimeout(Duration.ZERO)
//                .build();
//
//        return new LettuceConnectionFactory(new RedisStandaloneConfiguration("localhost", 6379), clientConfig);
//    }

    @Bean
    ReactiveRedisTemplate<String, String> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {



        return new ReactiveRedisTemplate<>(factory, RedisSerializationContext.string());
    }

//    @Bean
//    ReactiveRedisOperations<String, Location> redisOperations(ReactiveRedisConnectionFactory factory) {
//        Jackson2JsonRedisSerializer<Location> serializer = new Jackson2JsonRedisSerializer<>(Location.class);
//
//        RedisSerializationContext.RedisSerializationContextBuilder<String, Location> builder =
//                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());
//
//        RedisSerializationContext<String, Location> context = builder.value(serializer).build();
//
//        return new ReactiveRedisTemplate<>(factory, context);
//    }

}