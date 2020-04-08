package com.takeaway.tracking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@SpringBootApplication
@EnableBinding
@EnableReactiveMongoRepositories
public class TrackingApplication implements CommandLineRunner {

    public static Flux<Location> locations;

    @Autowired
    LocationsRepository locationsRepository;

    public static void main(String[] args) {
        SpringApplication.run(TrackingApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
//        Scheduler s = Schedulers.newParallel("parallel-scheduler", 20);

        locations = Flux.defer(() -> locationsRepository.findAll())
        .parallel(20)
        .runOn(Schedulers.parallel())
        .;
//        Flux<Location> all = locationsRepository
//                .findAll()
//                .parallel(20)
//                .runOn(Schedulers.parallel())
//                .publishOn(s);
//
//        locations = all;


////        all.subscribe((e) -> System.out.println(e));
    }
}
