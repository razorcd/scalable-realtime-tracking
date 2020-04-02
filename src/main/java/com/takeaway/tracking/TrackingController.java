package com.takeaway.tracking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.Instant;

@CrossOrigin
@RestController
@RequiredArgsConstructor
public class TrackingController {

    private final LocationsRepository locationsRepository;

    @GetMapping(value = "location/{orderId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<Location> create(@PathVariable String orderId) {
//        return Flux.fromIterable(Arrays.asList(1,2,3,4,5,6,7,8,2,3,4,6,7,5,3,2,5,5))
//                .delayElements(Duration.ofSeconds(2));

        return locationsRepository.findByOrderId(orderId)
                .map(ev -> {ev.setPublishing(Instant.now().toEpochMilli()); return ev;});
    }

}
