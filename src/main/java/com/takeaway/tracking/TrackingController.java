package com.takeaway.tracking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
public class TrackingController {

    private final LocationsRepository locationsRepository;

    @GetMapping(value = "location", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<Location> globalLocationByOrderId() {
//        return Flux.fromStream(Stream.generate(() -> new Random().nextInt(9999))).delayElements(Duration.ofSeconds(2));
        return TrackingApplication.locations
                .map(ev -> {
                    ev.setPublishingToFE4(Instant.now().toEpochMilli());
                    return ev;
                });
    }

    @GetMapping(value = "simple/location/{orderId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<Location> simpleLocationByOrderId(@PathVariable String orderId) {
//        return locationsRepository.findByOrderId(orderId)
        return TrackingApplication.locations
                    .filter(l -> l.getOrderId().equals(orderId))
                    .map(ev -> {
                        ev.setPublishingToFE4(Instant.now().toEpochMilli());
                        return ev;
                    })
//                    .onBackpressureLatest()
//                    .takeUntil(Location::isLast)
//                    .doOnError((e) -> log.error("Error: {}", e.getMessage()))
                    ;
    }

    @GetMapping(value = "location/{orderId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<Location> locationByOrderId(@PathVariable String orderId) {

        //consume without sending to FE (avoids backpressure)
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

        AtomicLong startRequestTime = new AtomicLong(Instant.now().toEpochMilli());

        //consume by sending to FE
        return locationsRepository.findByOrderId(orderId)
                .doOnNext((ev) -> { if (ev.isFirst()) startRequestTime.set(Instant.now().toEpochMilli()); })
                .map(ev -> {
                    ev.setPublishingToFE4(Instant.now().toEpochMilli());
                    return ev;
                })
//                .onBackpressureLatest()
                .takeUntil(Location::isLast)
                .doOnTerminate(() -> {
                    long processTime = Instant.now().minusMillis(startRequestTime.get()).toEpochMilli();
                    log.info("\n\n! Processed SSE request (orderId: {}) is {} ms. Sent {} locations in 1 connection.\n",
                            orderId, processTime, count(orderId).block());
                })
                .doOnError((e) -> log.error("Error {}", e.getMessage()))
                ;
    }


    @GetMapping(value = "count/{orderId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Mono<Long> count(@PathVariable String orderId) {
        return locationsRepository.findAllByOrderId(orderId).count();
    }

    @GetMapping(value = "count", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Mono<Long> countAll() {
        return locationsRepository.findAll().count();
    }

}



