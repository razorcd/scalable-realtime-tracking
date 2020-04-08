package com.takeaway.tracking;

import io.lettuce.core.pubsub.api.reactive.ChannelMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
@EnableBinding
public class TrackingApplication implements CommandLineRunner {

    @Autowired
    private ReactiveRedisTemplate<String, String> template;

    public static Flux<Location> locations;

    public static void main(String[] args) {
        SpringApplication.run(TrackingApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
//        template.opsForList().leftPush("1", "test1");
        template.opsForList().rightPush("5", "test5").block();
        template.opsForList().rightPop("5")
                .doOnNext(e -> System.out.println(e))
                .subscribe();
        System.out.println();

        ReactiveRedisMessageListenerContainer container = new ReactiveRedisMessageListenerContainer(template.getConnectionFactory());

        Flux<ReactiveSubscription.Message<String, String>> messageFlux = container.receive(ChannelTopic.of("channel1"));
        template.convertAndSend("channel1", "message1").block();

        messageFlux.doOnNext(msg -> {
            System.out.println(msg);
        }).subscribe();


        template.listenToChannel("channel1").doOnNext(msg -> {
            System.out.println(msg);
        }).subscribe();

        template.convertAndSend("channel1", "messag1").block();

        System.out.println(".");
    }
}
