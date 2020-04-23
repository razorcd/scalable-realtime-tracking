package com.takeaway.tracking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.ReadFrom;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.masterslave.MasterSlave;
import io.lettuce.core.masterslave.StatefulRedisMasterSlaveConnection;
import io.lettuce.core.protocol.Command;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.utils.CopyOnWriteMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.RedisCommand;
import org.springframework.data.redis.stream.StreamReceiver;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


@Component
//@EnableBinding
@Slf4j
@RequiredArgsConstructor
public class LocationRepository {
    public static final String STREAM_PREFIX = "streamK_";

//    @Qualifier("template2")
    @Autowired
    private ReactiveRedisTemplate<String,String> template;
//    private final ReactiveStringRedisTemplate template;

//    @Qualifier("read")
//    @Autowired
//    ReactiveRedisTemplate<String,String> readTemplate = Config.getReactiveRedisTemplate();
//    private ReactiveStringRedisTemplate template;

    private final ObjectMapper mapper;
    @Autowired
//    StreamReceiver<String, ObjectRecord<String,String>> streamReceiver;
    StreamReceiver<String, MapRecord<String, String, String>> streamReceiver;
//    @Autowired
//    RedisReactiveCommands<String, String> commands;

    private FluxSink<String> mainSink;
    private Flux<String> liveLocations;
//    private CopyOnWriteArraySet<StreamName> streamNames = new CopyOnWriteArraySet<>();
    private Map<String, StreamName> streamNames1 = new HashMap<>();
    private Map<String, StreamName> streamNames2 = new HashMap<>();
    private Map<String, StreamName> streamNames3 = new HashMap<>();
    private Map<String, StreamName> streamNames4 = new HashMap<>();


    @PostConstruct
    public LocationRepository setup() {
        log.info("SETUP REDIS POLLER");
        streamNames1.put("_", new StreamName("_", Instant.now().toEpochMilli(), 0L));
        streamNames2.put("_", new StreamName("_", Instant.now().toEpochMilli(), 0L));
        streamNames3.put("_", new StreamName("_", Instant.now().toEpochMilli(), 0L));
        streamNames4.put("_", new StreamName("_", Instant.now().toEpochMilli(), 0L));
//        for (int i = 1; i < 100000; i++) {
//            streamNames.add(""+(600000+i));
//        }
        this.liveLocations = Flux.create(sink -> this.mainSink = sink);
//        subscribe = this.liveLocations.subscribe();

        //CONSUME with stream_name, on Terminate remove stream_name
        Flux<String> objectFlux = Flux.create(sink -> this.mainSink = sink);
        liveLocations = objectFlux
//                .doOnNext(it -> log.info("Processing: " + it))
                .publish()
                .autoConnect(0);
//                .subscribe();
        subscribeRedisPoller1(streamNames1);
        subscribeRedisPoller1(streamNames2);
        subscribeRedisPoller1(streamNames3);
        subscribeRedisPoller1(streamNames4);
        return this;
    }

    public Flux<Location> getFluxByOrderId(String orderId) {
//        template.getConnectionFactory().getReactiveConnection()
//                .serverCommands().flushAll().subscribe();

//        subscribeRedisPoller();
//        withStreamReceiver();

        //select stream Set
        int streamNr = new Random().nextInt(100) % 4;
        System.out.println("Selecting random: "+streamNr);
        Map<String, StreamName> currentStreamNames;
        switch (streamNr) {
            case 0:
                currentStreamNames = streamNames1;
                break;
            case 1:
                currentStreamNames = streamNames2;
                break;
            case 2:
                currentStreamNames = streamNames3;
                break;
            case 3:
                currentStreamNames = streamNames4;
                break;
            default:
                throw new RuntimeException("Wrong random value: "+streamNr);
        }

        log.info("Registering consumer: "+STREAM_PREFIX+orderId);
        final StreamName streamName = new StreamName(STREAM_PREFIX+orderId, Instant.now().toEpochMilli(), 0L);
        currentStreamNames.put(STREAM_PREFIX+orderId, streamName);

//        return readTemplate
//                .randomKey()
//                .log()
//                .map(it -> new Location(it, 0,0,null,null,null,null,null,null,null))
//                .flux()
//                ;

        return liveLocations
//                .log()
                .map(event -> deserializeLocation(event))
                .filter(event -> event.getOrderId().equals(orderId))
                .doFinally(e -> {
                    currentStreamNames.remove(STREAM_PREFIX+orderId);
                    log.info("Finally triggered UNRegistering consumer: "+STREAM_PREFIX+orderId+" Cause: "+e);
                })
                ;
    }

    private Location deserializeLocation(String event) {
        try {
            return mapper.readValue(event, Location.class);
        } catch (JsonProcessingException e) { throw new RuntimeException("Can not parse json "+event); }
    }

    private void subscribeRedisPoller1(Map<String, StreamName> currentStreamNames) {
//        System.out.println("Poller starting for: "+currentStreamNames.keySet());
//        AtomicInteger counter = new AtomicInteger();
        //TODO: fix offset time/gap between recursive calls
        Set<StreamOffset<String>> streamNamesCollection = currentStreamNames.values().stream()
                .map(streamName -> StreamOffset.create(streamName.getStreamName(), ReadOffset.from("" + (streamName.getOffsetTimeMs()) + "-"+streamName.getOffsetCount())))
                .collect(Collectors.toSet());

        StreamOffset<String>[] streamNamesArray = new StreamOffset[streamNamesCollection.size()];
        streamNamesCollection.toArray(streamNamesArray);
//        System.out.println(streamNamesCollection);
        template.opsForStream()
                .read(StreamReadOptions.empty().block(Duration.ofSeconds(2)).count(5000), streamNamesArray)
                .doOnTerminate(() -> {
//                    log.info("Terminated.");
                    subscribeRedisPoller1(currentStreamNames);
//                    System.out.println("Stream batch request: " + counter);
                })
//                .parallel(10)
//                .runOn(Schedulers.newParallel("Pooler", 10))
//                .onBackpressureDrop()
                .doOnNext(it -> {
//                    log.info("Fux element: "+it);
                            this.mainSink.next(
                                    it.getValue().get("payload").toString()
                                    .replace("10000000000001", it.getId().getTimestamp().toString())
                                    .replace("20000000000002", ""+Instant.now().toEpochMilli())
                            );
                })
                .doOnNext(it -> {
                    if (it.getStream().equals(STREAM_PREFIX+"600001")) log.info("Now: {}. From Redis at {}. Event: {}", Instant.now().toEpochMilli(), it.getId().getTimestamp(), it.getValue());
                })
                .doOnNext(it -> {
                    currentStreamNames.put(it.getStream(), new StreamName(it.getStream(), it.getId().getTimestamp(),  (it.getId().getSequence()+1)));
//                    counter.incrementAndGet();
                })
                .doOnError((e) -> {
                    log.info("Error: "+e);
                })

//                .doAfterTerminate(() -> {
//                    log.info("After terminated.");
//                })
//                .doFinally((e) -> {
//                    log.info("Finally "+e);
//                })
                .subscribe();
    }



    private void withStreamReceiver() {
        streamReceiver.receive(StreamOffset.create(STREAM_PREFIX+"600001", ReadOffset.lastConsumed()))
                .doOnNext(it -> {
                    log.info("Received: "+it);
                })
                .subscribe();
    }

    @Value
    private class StreamName {
        //        private final String id = UUID.randomUUID().toString();
        private final String streamName;
        private final Long offsetTimeMs;
        private final Long offsetCount;
    }
}


