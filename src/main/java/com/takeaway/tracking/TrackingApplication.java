package com.takeaway.tracking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import reactor.core.publisher.Flux;

import java.util.function.Function;

@SpringBootApplication
@EnableBinding
@EnableReactiveMongoRepositories
public class TrackingApplication {
    public static void main(String[] args) {
        SpringApplication.run(TrackingApplication.class, args);
    }

//
//    @Bean
//    public Function<Flux<String>, Flux<String>> reactiveUpperCase() {
//        return flux -> flux.map(val -> {
//            System.out.println("\n\n"+val+"\n\n");
//            return val;
//        });
//    }

}




