package com.takeaway.tracking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableBinding
@Slf4j
public class TrackingApplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(TrackingApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
    }

}