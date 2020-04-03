package com.takeaway.tracking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Instant;

@CrossOrigin
@RestController
@RequiredArgsConstructor
public class TrackingController {

    private final LocationsRepository locationsRepository;

    @GetMapping(value = "location/{orderId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<Location> create(@PathVariable String orderId) {

        return locationsRepository.findByOrderId(orderId)
                .map(ev -> {
                    ev.setPublishingToFE4(Instant.now().toEpochMilli());
                    return ev;
                });
    }

}
