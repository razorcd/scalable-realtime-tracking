package com.takeaway.tracking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class LocationRepositoryTest {

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    LocationSink locationSink;

    @Autowired
    MessageCollector collector;
    BlockingQueue<Message<?>> kafkaLocationIncomingMessags;

    @Autowired
    ObjectMapper mapper;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Before
    public void setUp() throws Exception {
        redisTemplate.delete(redisTemplate.keys("*"));
        System.out.println("Keys in the DB: "+redisTemplate.keys("*").size());
//        kafkaLocationIncomingMessags = collector.forChannel(locationSink.events());
    }

//    @Ignore
    @Test
    public void givenAlistOfLocations_whenOpeningAllTheStreams_andEmitAllLocations_itShouldReceiveAllLocations() throws Exception {
        //setup events:
        List<Location> inputLocationsPart1 = IntStream.rangeClosed(1,1000).boxed()
                .map(i -> new Location(String.valueOf(800000+i), 1.1d, 1.2d, "1", 1L, 1L, 1L, 1L, false, false, 1L))
                .collect(Collectors.toList());
        List<Location> inputLocations = new ArrayList<>();
        inputLocations.addAll(inputLocationsPart1);
        inputLocations.addAll(inputLocationsPart1);
        inputLocations.addAll(inputLocationsPart1);
        inputLocations.add(new Location("700000", 1.1d, 1.2d, "1", 1L, 1L, 1L, 1L, false, true, 1L));
        final long EXPECTED_RETURN_MESSAGES = inputLocationsPart1.size()*3*3+1;
        inputLocationsPart1 = null; // cleanup mem

        //setup Kafka event publisher:
        Thread kafkaEmitterThread = new Thread(() -> {
            System.out.println("Start Kafkaemitting thread");
//            try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
            System.out.println("Publishing "+inputLocations.size()+" events to Kafka stream.");
            ForkJoinPool customThreadPool = new ForkJoinPool(100);
            try {
                customThreadPool.submit(() ->
                        inputLocations.parallelStream().forEach(location -> publishToKafka(location))
                ).get();
            } catch (Exception e) {e.printStackTrace();}
            System.out.println("End Kafka emitting thread");
        });

//        //setup event publisher:
//        Thread emitterThread = new Thread(() -> {
//            System.out.println("Start emitting thread");
////            try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
//            System.out.println("Publishing "+inputLocations.size()+" events to locationRepository.");
//            ForkJoinPool customThreadPool = new ForkJoinPool(100);
//            try {
//                customThreadPool.submit(() ->
//                        inputLocations.parallelStream().forEach(location -> locationRepository.emit(location))
//                ).get();
//            } catch (Exception e) {e.printStackTrace();}
//            System.out.println("End emitting thread");
//        });


        long start = Instant.now().toEpochMilli();

        //setup Repository consumer:
        List<Flux<Location>> openFluxes = inputLocations.stream()
                .map(location -> locationRepository.getFluxByOrderId(location.getOrderId()))
                .collect(Collectors.toList());
        System.out.println("Created "+openFluxes.size()+" openFluxes in " +(Instant.now().toEpochMilli()-start)+ " ms." );

        //collect Repository response events
//        Map<String, Location> map = new ConcurrentHashMap<>();
//        Map<String, Location> receivedEvents = new ConcurrentHashMap<>();
        AtomicLong receivedEventsCount = new AtomicLong();
        openFluxes.forEach(openFlux -> openFlux.doOnNext((c) -> receivedEventsCount.incrementAndGet()).subscribe());

        //Publish all events to Repository async
//        emitterThread.start();
        kafkaEmitterThread.start();

        //await receiving all events
        while (receivedEventsCount.get() != EXPECTED_RETURN_MESSAGES) {
            Thread.sleep(1000);
            System.out.println("Events received: "+receivedEventsCount.get());
        }
        long end = Instant.now().toEpochMilli();

        //benchmark
        System.out.println("\nSize: "+receivedEventsCount.get());
        System.out.println("\nBenchmark result: Processing time to open "+inputLocations.size()+" connections and send "+inputLocations.size()+" events and receive "+EXPECTED_RETURN_MESSAGES+" events in total, is: "+(end-start)+" ms.\n");

        assertThat(receivedEventsCount.get()).isEqualTo(EXPECTED_RETURN_MESSAGES);
    }

    private void publishToKafka(Location location) {
        Message<String> inputLocationMessage = null;
        try {
            inputLocationMessage = MessageBuilder
                    .withPayload(mapper.writeValueAsString(location))
                    .build();
        } catch (JsonProcessingException e) {throw new RuntimeException(e);}

        locationSink.events().send(inputLocationMessage);
    }
}