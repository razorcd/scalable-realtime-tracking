package com.takeaway.tracking;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.stream.StreamReceiver;

import javax.annotation.Priority;

@Priority(0)
@Configuration
@EnableBinding(LocationSink.class)
public class Config {

    @Autowired
    private ReactiveRedisTemplate<String, String> template;
    @Autowired
    private ObjectMapper mapper;

//    @Bean(name = "LocationRepository1")
//    public LocationRepository locationRepository1Bean() {
//        return new LocationRepository(template, mapper).setup();
//    }

//    @Bean(name = "LocationRepository2")
//    public LocationRepository locationRepository2Bean() {
//        return new LocationRepository(template, mapper).setup();
//    }
//
//    @Bean(name = "LocationRepository3")
//    public LocationRepository locationRepository3Bean() {
//        return new LocationRepository(template, mapper).setup();
//    }
//
//    @Bean(name = "LocationRepository4")
//    public LocationRepository locationRepository4Bean() {
//        return new LocationRepository(template, mapper).setup();
//    }
//
//    @Bean(name = "LocationRepository5")
//    public LocationRepository locationRepository5Bean() {
//        return new LocationRepository(template, mapper).setup();
//    }

//    @Bean
//    public StreamReceiver<String, ObjectRecord<String, String>> streamReceiver(ReactiveRedisConnectionFactory factory) {
//        return StreamReceiver.create(factory, StreamReceiver.StreamReceiverOptions.builder().targetType(String.class).build());
//    }
//
////    @Bean
////    public PollStatsUpdater updater(StreamReceiver<String, ObjectRecord<String, VoteMessage>> streamReceiver,
////                                    RedisReactiveCommands<String, String> commands) {
////        return new PollStatsUpdater(streamReceiver, commands);
////    }
//
//    @Bean(destroyMethod = "close")
//    public StatefulRedisConnection<String, String> connection(ReactiveRedisConnectionFactory factory) {
//
//        DirectFieldAccessor accessor = new DirectFieldAccessor(factory);
//
//        RedisClient client = (RedisClient) accessor.getPropertyValue("client");
//
//        return client.connect();
//    }
//
//    @Bean
//    public RedisReactiveCommands<String, String> commands(StatefulRedisConnection<String, String> connection) {
//        return connection.reactive();
//    }

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

            //    @Bean
            //    ReactiveRedisTemplate<String, String> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
            //
            //
            //
            //        return new ReactiveRedisTemplate<>(factory, RedisSerializationContext.string());
            //    }

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