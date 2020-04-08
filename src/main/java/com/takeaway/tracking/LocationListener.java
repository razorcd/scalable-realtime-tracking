package com.takeaway.tracking;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.Publisher;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Consumer;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
//@Configuration
@Slf4j
public class LocationListener {
//    @Autowired
//    private final LocationsRepository locationsRepository;

//    @ConditionalOnProperty(name="listener.enabled")
//    @StreamListener("location-events")
////    void listener(Flux<String> evetsStream) {
//    void listener(Location event) {
////        Instant startTime = Instant.now();
//
////        evetsStream.doOnNext(ev -> {
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
//
////            log.info("Async save call completed in {} ms: {}", Instant.now().minusMillis(startTime.toEpochMilli()).toEpochMilli(), event);
//
////        });
//    }

    @ConditionalOnProperty(name="listener.enabled")
//    @StreamListener("location-events")
    public void kafkaConsumer(Flux<Location> events) {
//        events.log().subscribe();
//        Flux<Location> newFlux = events
//                .log()
//                ;

//        locationsRepository.saveAll(events.map(this::buildLocation)).subscribe();
//        this.driverLocationEvents = events
//                .doOnNext(driverLocationChangedEventCloudEvent -> log.debug("Received DriverLocationChangedEvent: {}", driverLocationChangedEventCloudEvent))
//                .map(event -> event.getData().get())
//                .cache(EVENTS_CACHE_SIZE, Duration.ofMinutes(EVENTS_CACHE_DURATION))
//        ;
//        this.driverLocationEvents.retry().subscribe();
    }


////    TODO: update to spring cloud reactive. Update spring cloud too
//    @Bean
//    public Consumer<Flux<Location>> locationConsumer() {
//        return inbound -> locationsRepository.saveAll(inbound.log());
//
////                .log()
////                .window(Duration.ofSeconds(30), Duration.ofSeconds(5))
////                .flatMap(w -> locationsRepository.save(Location.random()))
////                .log();
//    }

    private Location buildLocation(Location event) {
        return new Location(event.getOrderId(),
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
