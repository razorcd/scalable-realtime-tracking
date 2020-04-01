package com.takeaway.tracking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@EnableBinding
@EnableReactiveMongoRepositories
public class TrackingApplication {
    public static void main(String[] args) {
        SpringApplication.run(TrackingApplication.class, args);
    }
}




