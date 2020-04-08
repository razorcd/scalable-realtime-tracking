package com.takeaway.tracking;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
//@Configuration
@Slf4j
public class LocationListener {
//    @Autowired
    private final LocationsRepository locationsRepository;

    private ConnectableFlux<Location> locationFlux;

    @ConditionalOnProperty(name="listener.enabled")
    @StreamListener("location-events")
//    void listener(Flux<String> evetsStream) {
    public void listener(Flux<Location> event) {

        locationFlux = event
                .map(this::buildLocation)
                .cache(Duration.ofSeconds(60*15))
                .publish()
                ;
        locationFlux.connect();

//        Instant startTime = Instant.now();


        locationsRepository.saveAll(event.log().map(this::buildLocation)).subscribe();

//        evetsStream.doOnNext(ev -> {
//            locationsRepository
//                    .save(new Location(
//                            null,
//                            event.getOrderId(),
//                            event.getLng(),
//                            event.getLat(),
//                            event.getCreatedByTheDriver1(),
//                            Instant.now().toEpochMilli(),
//                            null,
//                            null,
//                            event.getFirst(),
//                            event.getLast())
//                    )
////                    .log()
//                    .subscribe(msg -> {
////                        log.info("Sync save call completed in {} ms: {}",Instant.now().minusMillis(startTime.toEpochMilli()).toEpochMilli(), msg);
//                    })
////                    .block()
//                    ;

//            log.info("Async save call completed in {} ms: {}", Instant.now().minusMillis(startTime.toEpochMilli()).toEpochMilli(), event);

//        });
    }

    public ConnectableFlux<Location> getLocationFlux() {
        return locationFlux;
    }

    //TODO: update to spring cloud reactive. Update spring cloud too
//    @Bean
//    public Function<Flux<Location>, Flux<Location>> kafkaListener() {
//        return inbound -> locationsRepository.saveAll(inbound);
//
////                .log()
////                .window(Duration.ofSeconds(30), Duration.ofSeconds(5))
////                .flatMap(w -> locationsRepository.save(Location.random()))
////                .log();
//    }

    private Location buildLocation(Location event) {
        return new Location(
                            null,
                            event.getOrderId(),
                            event.getLng(),
                            event.getLat(),
                            event.getCreatedByTheDriver1(),
                            Instant.now().toEpochMilli(),
                            null,
                            null,
                            event.getFirst(),
                            event.getLast());
    }

}
