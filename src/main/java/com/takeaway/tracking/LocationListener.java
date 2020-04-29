package com.takeaway.tracking;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import static com.takeaway.tracking.LocationRepository.STREAM_PREFIX;

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

    private final AtomicLong counter = new AtomicLong();

//    @Qualifier("template1")
    @Autowired
    private ReactiveRedisTemplate<String,String> template;
    @Autowired
    private RedisTemplate<String,String> staticTemplate;

    @Autowired
    ObjectMapper mapper;

    @PostConstruct
    public void setup() {
        new Thread(() -> {
            while(true) {
                long current = counter.get();
                try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
                System.out.println("Incoming events from Kafka: " +((counter.get()-current)) + " events/sec");
            }
        })
        .start();
    }


    @ConditionalOnProperty(name="listener.enabled")
    @StreamListener("location-events")
    public void kafkaConsumer(Location event) {

//        System.out.println("Received from Kafka " + event);
//        events.doOnNext(event ->
//        {
        staticTemplate.opsForStream().add(ObjectRecord.create(STREAM_PREFIX + event.getOrderId(), toJson(buildLocation(event))));
//        template.opsForStream().add(ObjectRecord.create(STREAM_PREFIX + event.getOrderId(), toJson(buildLocation(event)))).subscribe();
        counter.incrementAndGet();
//        })
//        .subscribe();


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

    private String toJson(Location event) {
        try {
            return mapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Parsing error: "+e);
        }
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
                            10000000000001L,
                            20000000000002L,
                            null,
                            event.getFirst(),
                            event.getLast(),
                            event.getCounter());
    }

}
