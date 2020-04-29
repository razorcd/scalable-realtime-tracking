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

import javax.annotation.PostConstruct;
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

import static com.takeaway.tracking.LocationRepository.STREAM_PREFIX;
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

    private final AtomicLong receivedEventsCount = new AtomicLong();

    @Before
    public void setUp() throws Exception {
        redisTemplate.delete(redisTemplate.keys(STREAM_PREFIX+"*"));
        System.out.println("Keys in the DB: "+redisTemplate.keys(STREAM_PREFIX+"*").size());
//        kafkaLocationIncomingMessags = collector.forChannel(locationSink.events());
    }

    @Before
    public void setup() {
        new Thread(() -> {
            while(true) {
                long current = receivedEventsCount.get();
                try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
                System.out.println("Incoming events from Repository: " +(receivedEventsCount.get()-current) + " events/sec");
            }
        })
        .start();
    }

//    @Ignore
    @Test
    public void givenAlistOfLocations_whenOpeningAllTheStreams_andEmitAllLocations_itShouldReceiveAllLocations() throws Exception {
        //setup events:
        List<Location> inputLocationsPartUnique = IntStream.rangeClosed(1,2000).boxed()
                .map(i -> new Location(String.valueOf(800000+i), 1.1d, 1.2d, "1", 1L, 1L, 1L, 1L, false, false, 1L))
                .collect(Collectors.toList());
        List<Location> inputLocations = new ArrayList<>();
        inputLocations.addAll(inputLocationsPartUnique);
        inputLocations.addAll(inputLocationsPartUnique);
        inputLocations.addAll(inputLocationsPartUnique);
        inputLocations.addAll(inputLocationsPartUnique);
        inputLocations.addAll(inputLocationsPartUnique);
        inputLocations.addAll(inputLocationsPartUnique);
        inputLocations.addAll(inputLocationsPartUnique);
        inputLocations.addAll(inputLocationsPartUnique);
        inputLocations.addAll(inputLocationsPartUnique);
        inputLocations.addAll(inputLocationsPartUnique);
        inputLocations.add(new Location("700000", 1.1d, 1.2d, "1", 1L, 1L, 1L, 1L, false, true, 1L));
        final long EXPECTED_RETURN_MESSAGES = inputLocationsPartUnique.size()*10+1;
        Set<String> connectionsOrderList = inputLocationsPartUnique.stream().map(e -> e.getOrderId()).collect(Collectors.toSet());
        connectionsOrderList.add("700000");

        //setup Kafka event publisher:
        Thread kafkaEmitterThread1 = getKafkaEmitterThread(inputLocations);
//        Thread kafkaEmitterThread2 = getKafkaEmitterThread(inputLocations);
//        Thread kafkaEmitterThread3 = getKafkaEmitterThread(inputLocations);
//        Thread kafkaEmitterThread4 = getKafkaEmitterThread(inputLocations);
//        Thread kafkaEmitterThread5 = getKafkaEmitterThread(inputLocations);
//        Thread kafkaEmitterThread6 = getKafkaEmitterThread(inputLocations);
//        Thread kafkaEmitterThread7 = getKafkaEmitterThread(inputLocations);
//        Thread kafkaEmitterThread8 = getKafkaEmitterThread(inputLocations);
//        Thread kafkaEmitterThread9 = getKafkaEmitterThread(inputLocations);
//        Thread kafkaEmitterThread10 = getKafkaEmitterThread(inputLocations);

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
        List<Flux<Location>> openFluxes = connectionsOrderList.stream()
                .map(connectionOrderId -> locationRepository.getFluxByOrderId(connectionOrderId))
                .collect(Collectors.toList());
        System.out.println("Created "+openFluxes.size()+" openFluxes in " +(Instant.now().toEpochMilli()-start)+ " ms." );

        //collect Repository response events
//        Map<String, Location> map = new ConcurrentHashMap<>();
//        Map<String, Location> receivedEvents = new ConcurrentHashMap<>();
        openFluxes.forEach(openFlux -> openFlux.doOnNext((c) -> receivedEventsCount.incrementAndGet()).subscribe());

        //Publish all events to Repository async
//        emitterThread.start();
        Thread.sleep(1000);
        kafkaEmitterThread1.start();
//        kafkaEmitterThread2.start();
//        kafkaEmitterThread3.start();
//        kafkaEmitterThread4.start();
//        kafkaEmitterThread5.start();
//        kafkaEmitterThread6.start();
//        kafkaEmitterThread7.start();
//        kafkaEmitterThread8.start();
//        kafkaEmitterThread9.start();
//        kafkaEmitterThread10.start();

        //await receiving all events
        while (receivedEventsCount.get() != EXPECTED_RETURN_MESSAGES) {
            Thread.sleep(1000);
            System.out.println("Total events received from repository: "+receivedEventsCount.get());
        }
        long end = Instant.now().toEpochMilli();

        //benchmark
        System.out.println("\nSize: "+receivedEventsCount.get());
        System.out.println("\nBenchmark result: Processing time to open "+connectionsOrderList.size()+" connections and send "+inputLocations.size()+" events and receive "+EXPECTED_RETURN_MESSAGES+" events in total, is: "+(end-start)+" ms.\n");

        assertThat(receivedEventsCount.get()).isEqualTo(EXPECTED_RETURN_MESSAGES);

//        Thread.sleep(99999999999L);
    }

    private Thread getKafkaEmitterThread(List<Location> inputLocations) {
        return new Thread(() -> {
            System.out.println("Start Kafkaemitting thread");
//            try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
            System.out.println("Publishing "+inputLocations.size()+" events to Kafka stream.");
            ForkJoinPool customThreadPool = new ForkJoinPool(100);
            try {
                customThreadPool.submit(() ->
                        inputLocations.parallelStream().forEach(location -> publishToKafka(location))
//                        Flux.fromIterable(inputLocations)
////                                .delayElements(Duration.ofNanos(1000))      // Set publishing rate. 1ms => 1000 events/sec
//                                .parallel(100)
//                                .runOn(Schedulers.newParallel("a", 100))
//                                .doOnNext(location -> publishToKafka(location))
//                                .subscribe()
                ).get();
            } catch (Exception e) {e.printStackTrace();}
            System.out.println("End Kafka emitting thread");
        });
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