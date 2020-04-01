package com.takeaway.tracking;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
//@Configuration
public class LocationListener {
//    @Autowired
    private final LocationsRepository locationsRepository;

    @StreamListener("location-events")
//    void listener(Flux<String> evetsStream) {
    void listener(Location event) {
//        evetsStream.doOnNext(ev -> {
            locationsRepository
                    .save(new Location(null, event.getOrderId(), event.getLng(), event.getLat(), event.getCreation(), null, Instant.now().toEpochMilli()))
                    .log()
                    .block()
                    ;
//        });
    }


//    @Bean
//    public Function<Flux<String>, Flux<String>> aggregate() {
//        return inbound -> inbound.
//                log()
////                .window(Duration.ofSeconds(30), Duration.ofSeconds(5))
////                .flatMap(w -> locationsRepository.save(Location.random()))
//                .log();
//    }
//
//    private Mono<String> calculateAverage(GroupedFlux<String, String> group) {
//        return group);
//    }

}
