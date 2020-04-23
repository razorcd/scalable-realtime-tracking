package com.takeaway.tracking;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.ReadFrom;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import io.lettuce.core.resource.ClientResources;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.convert.StringToRedisClientInfoConverter;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.stream.StreamReceiver;

import javax.annotation.Priority;
import java.nio.charset.Charset;
import java.time.Duration;

@Priority(0)
@Configuration
@EnableBinding(LocationSink.class)
public class Config {

//    @Bean
//    public LettuceConnectionFactory redisConnectionFactory() {
//        RedisStandaloneConfiguration server = new RedisStandaloneConfiguration("aws..", 6379);
////        RedisStandaloneConfiguration server = new RedisStandaloneConfiguration("master.....", 6379);
//        server.setPassword("xxxxx");
//        server.setDatabase(0);
//        return new LettuceConnectionFactory(server);
//    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .readFrom(ReadFrom.REPLICA)
                .useSsl().and()
                .clientResources(ClientResources.builder()
                        .computationThreadPoolSize(5)
                        .ioThreadPoolSize(5)
                        .build())
                .clientOptions(ClientOptions.builder().build())
                .commandTimeout(Duration.ofSeconds(30))
                .shutdownTimeout(Duration.ofSeconds(30))
                .build();

//        //Cluster config for sharding:
//        // At the moment throws:  ERR This instance has cluster support disabled
//        // https://redis.io/topics/cluster-tutorial
//        RedisClusterConfiguration serverConfig = new RedisClusterConfiguration();
//        serverConfig.addClusterNode(RedisNode.newRedisNode()
//                .listeningAt("master.....", 6379)
//                .withName("MASTER1")
//                .withId("M1")
//                .promotedAs(RedisNode.NodeType.MASTER)
//                .build());
//        serverConfig.addClusterNode(RedisNode.newRedisNode()
//                .listeningAt("replica......", 6379)
//                .withName("REPLICA1")
//                .withId("R1")
//                .replicaOf("M1")
//                .slaveOf("M1")
//                .promotedAs(RedisNode.NodeType.SLAVE)
//                .build());

        //Master / Replica config:
        // Throws: Connection type interface io.lettuce.core.api.StatefulConnection not supported
//        RedisStaticMasterReplicaConfiguration serverConfig = new RedisStaticMasterReplicaConfiguration("localhost", 6379);
//        serverConfig.addNode("localhost", 6380);

//        RedisStaticMasterReplicaConfiguration serverConfig = new RedisStaticMasterReplicaConfiguration("master....", 6379);
//        serverConfig.addNode("replica.....", 6379);

//        // Static config:
//        RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration("localhost", 6379);

//        RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration("master.....", 6379);
        RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration("aws...", 6379);
        serverConfig.setPassword("xxxxxxxxxx");

        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(serverConfig, clientConfig);
        return lettuceConnectionFactory;
    }
//
////    @Bean
//    public LettuceConnectionFactory redisReplicaConnectionFactory() {
//        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
//                .readFrom(ReadFrom.MASTER)
//                .useSsl().and()
//                .clientResources(ClientResources.builder()
//                        .computationThreadPoolSize(5)
//                        .ioThreadPoolSize(5)
//                        .build())
//                .clientOptions(ClientOptions.builder().build())
//                .commandTimeout(Duration.ofSeconds(30))
//                .shutdownTimeout(Duration.ofSeconds(30))
//                .build();
//
//        RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration("replica.xxxx", 6379);
//        serverConfig.setPassword("xxxxx");
//
//        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(serverConfig, clientConfig);
//        return lettuceConnectionFactory;
//    }


    @Bean
    StreamReceiver<String, MapRecord<String, String, String>> streamReceiver() {
        return StreamReceiver.create(redisConnectionFactory());
    }

//    @Bean
//    @Qualifier("template1")
////    @ConditionalOnMissingBean(name = "reactiveRedisTemplate")
//    @ConditionalOnBean(ReactiveRedisConnectionFactory.class)
//    public ReactiveRedisTemplate<String,String> reactiveRedisTemplate(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory, ResourceLoader resourceLoader) {
////        JdkSerializationRedisSerializer jdkSerializer = new JdkSerializationRedisSerializer(resourceLoader.getClassLoader());
//        StringRedisSerializer stringSerializer = StringRedisSerializer.UTF_8;
////        RedisSerializationContext<String,String> serializationContext = RedisSerializationContext
////                .newSerializationContext().key(jdkSerializer).value(jdkSerializer).hashKey(jdkSerializer)
////                .hashValue(jdkSerializer).build();
//        return new ReactiveRedisTemplate<>(redisConnectionFactory(), RedisSerializationContext.string());
//    }
//
//    @Bean
//    @Qualifier("template2")
////    @ConditionalOnMissingBean(name = "reactiveRedisTemplate")
//    @ConditionalOnBean(ReactiveRedisConnectionFactory.class)
//    public ReactiveRedisTemplate<String,String> reactiveRedisTemplate2(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory, ResourceLoader resourceLoader) {
////        JdkSerializationRedisSerializer jdkSerializer = new JdkSerializationRedisSerializer(resourceLoader.getClassLoader());
//        StringRedisSerializer stringSerializer = StringRedisSerializer.UTF_8;
////        RedisSerializationContext<String,String> serializationContext = RedisSerializationContext
////                .newSerializationContext().key(jdkSerializer).value(jdkSerializer).hashKey(jdkSerializer)
////                .hashValue(jdkSerializer).build();
//        return new ReactiveRedisTemplate<>(redisConnectionFactory(), RedisSerializationContext.string());
//    }

//    @Bean
//    @ConditionalOnBean(ReactiveRedisTemplate.class)
//    public ReactiveStringRedisTemplate reactiveStringRedisTemplate() {
//        return new ReactiveStringRedisTemplate(redisReplicaConnectionFactory());
//    }

//
//
//    @Bean
//    public LettuceConnectionFactory redisConnectionFactory2() {
//
//        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
//                .readFrom(ReadFrom.MASTER)
//                .useSsl().and()
//                .commandTimeout(Duration.ofSeconds(30))
//                .shutdownTimeout(Duration.ofSeconds(30))
//                .build();
//
////        RedisStaticMasterReplicaConfiguration serverConfig = new RedisStaticMasterReplicaConfiguration("master......", 6379);
////        serverConfig.addNode("replica....", 6379);
//
////        RedisStaticMasterReplicaConfiguration serverConfig = new RedisStaticMasterReplicaConfiguration("localhost", 6379);
////        serverConfig.addNode("localhost", 6380);
//
////        RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration("localhost", 6379);
//
//        RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration("master...........", 6379);
//        serverConfig.setPassword("xxxxxxxx");
//
//        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(serverConfig, clientConfig);
//        return lettuceConnectionFactory;
//    }

////    @Qualifier("read")
////    @Bean
//    public static ReactiveRedisTemplate<String,String> getReactiveRedisTemplate() {
//
//            LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
////                .readFrom(ReadFrom.MASTER)
////                .useSsl().and()
//                .commandTimeout(Duration.ofSeconds(30))
//                .shutdownTimeout(Duration.ofSeconds(30))
//                .build();
//
////        RedisStaticMasterReplicaConfiguration serverConfig = new RedisStaticMasterReplicaConfiguration("master........", 6379);
////        serverConfig.addNode("replica.....", 6379);
//
////        RedisStaticMasterReplicaConfiguration serverConfig = new RedisStaticMasterReplicaConfiguration("localhost", 6379);
////        serverConfig.addNode("localhost", 6380);
//
//        RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration("localhost", 6379);
//
//
////        RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration("master.........", 6379);
////        serverConfig.setPassword("xxxxxxxx");
//
//        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(serverConfig, clientConfig);
//
//
//        RedisSerializer<String> serializer = RedisSerializer.string();
//        RedisSerializationContext<String, String> serializationContext = RedisSerializationContext.string();
//        return new ReactiveRedisTemplate<>(lettuceConnectionFactory, serializationContext);
//    }

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