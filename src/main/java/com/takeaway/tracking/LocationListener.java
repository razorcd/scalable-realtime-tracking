package com.takeaway.tracking;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationListener {

    private final LocationsRepository locationsRepository;

    private ConnectableFlux<Location> locationFlux;

    @ConditionalOnProperty(name="listener.enabled")
    @StreamListener("location-events")
//    void listener(Flux<String> evetsStream) {
    public void listener(Flux<Location> event) {

//        Send directly to API flux
//        locationFlux = event
//                .map(this::buildLocation)
//                .cache(Duration.ofSeconds(60*15))
//                .publish()
//                ;
//        locationFlux.connect();

        locationsRepository.saveAll(event.log().map(this::buildLocation)).subscribe();
    }

    public ConnectableFlux<Location> getLocationFlux() {
        return locationFlux;
    }

    private Location buildLocation(Location event) {
        return new Location(
                            null,
                            dbPartitionFromOrderId(event.getOrderId()),
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

    public static byte dbPartitionFromOrderId(String orderId) {
        return ((Long)(Long.parseLong(orderId) % Location.DB_PARTITIONS_COUNT)).byteValue();
    }

}
