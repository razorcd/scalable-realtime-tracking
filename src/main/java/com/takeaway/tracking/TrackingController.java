package com.takeaway.tracking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
public class TrackingController {

    private final LocationListener locationListener;
    private final LocationsReactiveRepository locationsReactiveRepository;
    private final MongoTemplate mongoTemplate;
    private final Map<Byte, ConnectableFlux<Location>> connectableFluxMap = new HashMap<>();


    @PostConstruct
    public void construct() {
        for (byte dbPartition = 0; dbPartition < Location.DB_PARTITIONS_COUNT; dbPartition++) {
            connectableFluxMap.put(dbPartition, openDbConnection(dbPartition));
        }
        connectableFluxMap.values().forEach(ConnectableFlux::connect);
    }

    private ConnectableFlux<Location> openDbConnection(byte dbPartition) {
        return locationsReactiveRepository
            .findByDbPartitionAndCreatedByTheDriver1IsGreaterThan(dbPartition, Instant.now().toEpochMilli())
            .doOnError(e -> {
                throw new RuntimeException("DB connection error. ", e);
            })
            .doOnTerminate(() -> {
                throw new RuntimeException("DB connection error termination. ");
            })
            .publish()
            ;
    }

    @GetMapping(value = "location/{orderId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<Location> locationByOrder(@PathVariable String orderId) {
        byte dbPartition = LocationListener.dbPartitionFromOrderId(orderId);

        Flux<Location> liveEvents = connectableFluxMap.get(dbPartition)
//        return locationListener.getLocationFlux()
                .filter(event -> event.getOrderId().equals(orderId))
                .map(ev -> {
                    ev.setPublishingToFE4(Instant.now().toEpochMilli());
                    return ev;
                })
                ;

        List<Location> oldEvents = findByOrderId(orderId);

        //TODO: check for possible lost events that were created btween nonReactiveQuery and reactiveQuery. Possible fix: filter nonReactive query by creationTime<now and filter reactive query by creationDate>=now
        return Flux.fromIterable(oldEvents).concatWith(liveEvents);
    }

    @GetMapping(value = "findby/{orderId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<Location> findByOrder(@PathVariable String orderId) {
        return locationsReactiveRepository.findByOrderId(orderId);
    }

    @GetMapping(value = "findall", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<Location> findAll() {
        return locationsReactiveRepository.findAll();
    }

    @GetMapping(value = "count/{orderId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Mono<Long> count(@PathVariable String orderId) {
        return locationsReactiveRepository.findByOrderId(orderId).take(Duration.ofSeconds(3)).count();
    }

    @GetMapping(value = "count", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Mono<Long> countAll() {
        return locationsReactiveRepository.findAll().count();
    }


    private List<Location> findByOrderId(String orderId) {
        Query query = new Query()
                .addCriteria(Criteria.where("orderId")
                        .is(orderId));

        return mongoTemplate.find(query, Location.class);
    }

}
