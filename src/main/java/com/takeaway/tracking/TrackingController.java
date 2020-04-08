package com.takeaway.tracking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.sql.Date;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
public class TrackingController {

    private final LocationListener locationListener;
    private final LocationsRepository locationsRepository;

    private ConnectableFlux<Location> dbFlux;
    private ConnectableFlux<Location> dbFlux2;

    @PostConstruct
    public void construct() {
        dbFlux = locationsRepository
                .findByCreatedByTheDriver1IsGreaterThan(Instant.now().toEpochMilli())
//                .findByOrderId("500001")
                .doOnError(e -> {
                    throw new RuntimeException("DB connection error. ", e);
                })
                .publish()
                ;
        dbFlux.connect();

        dbFlux2 = locationsRepository  //TODO: query by partition token
                .findByCreatedByTheDriver1IsGreaterThan(Instant.now().toEpochMilli())
//                .findByOrderId("500001")
                .doOnError(e -> {
                    throw new RuntimeException("DB connection error. ", e);
                })
                .publish()
        ;
        dbFlux2.connect();
    }



    @GetMapping(value = "location/{orderId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<Location> create(@PathVariable String orderId) {

        Flux<Location> oldEvents = locationsRepository.findByOrderId(orderId);

        Flux<Location> liveEvents = dbFlux
//        return locationListener.getLocationFlux()
                .filter(event -> event.getOrderId().equals(orderId))
                .map(ev -> {
                    ev.setPublishingToFE4(Instant.now().toEpochMilli());
                    return ev;
                })
                ;

        return oldEvents.concatWith(liveEvents);
//
//        //consume without sending to FE (avoids backpressure)
//        new Thread(() -> {
//            locationsRepository.findByOrderId(orderId)
//                    .map(ev -> {
//                        ev.setPublishingToFE4(Instant.now().toEpochMilli());
//                        return ev;
//                    })
////                    .log()
//                    .blockLast()
//                    ;
//            ;
//        }).start();
//
//        AtomicLong startRequestTime = new AtomicLong(Instant.now().toEpochMilli());
//
//        //consume by sending to FE
//        return locationsRepository.findByOrderId(orderId)
//                .doOnNext((ev) -> { if (ev.isFirst()) startRequestTime.set(Instant.now().toEpochMilli()); })
//                .map(ev -> {
//                    ev.setPublishingToFE4(Instant.now().toEpochMilli());
//                    return ev;
//                })
//                .onBackpressureLatest()
//                .takeUntil(Location::isLast)
//                .doOnTerminate(() -> {
//                    long processTime = Instant.now().minusMillis(startRequestTime.get()).toEpochMilli();
//                    log.info("\n\n! Processed SSE request (orderId: {}) is {} ms. Sent {} locations in 1 connection.\n",
//                            orderId, processTime, count(orderId).block());
//                })
//                .doOnError((e) -> log.error("Error {}", e.getMessage()))
//                ;
    }


    @GetMapping(value = "count/{orderId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Mono<Long> count(@PathVariable String orderId) {
        return locationsRepository.findByOrderId(orderId).take(Duration.ofSeconds(3)).count();
    }

    @GetMapping(value = "countafter", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Mono<Long> countAfter() {
        return locationsRepository.findByCreatedByTheDriver1IsGreaterThan(0L).count();
    }

}
