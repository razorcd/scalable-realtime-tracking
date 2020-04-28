package com.takeaway.tracking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;


@Component
//@EnableBinding
@Slf4j
@RequiredArgsConstructor
public class LocationRepository {
    public static final String STREAM_PREFIX = "streamK_";
    public static final String DEBUG_STREAM = STREAM_PREFIX+"700000";

//    @Qualifier("template2")
    @Autowired
    private ReactiveRedisTemplate<String,String> template;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;
//    private final ReactiveStringRedisTemplate template;

//    @Qualifier("read")
//    @Autowired
//    ReactiveRedisTemplate<String,String> readTemplate = Config.getReactiveRedisTemplate();
//    private ReactiveStringRedisTemplate template;


    private final ObjectMapper mapper;

//    @Autowired
//    StreamReceiver<String, MapRecord<String, String, String>> streamReceiver;
//    @Autowired
//    RedisReactiveCommands<String, String> commands;

//    private final Map<String,StreamObject> streamObjects0 = new ConcurrentHashMap<>();
    private final Map<String,StreamObject> streamObjects1 = new ConcurrentHashMap<>();
//    private final Map<String,StreamObject> streamObjects2 = new ConcurrentHashMap<>();
//    private final Map<String,StreamObject> streamObjects3 = new ConcurrentHashMap<>();
//    private final Map<String,StreamObject> streamObjects4 = new ConcurrentHashMap<>();
//    private final Map<String,StreamObject> streamObjects5 = new ConcurrentHashMap<>();
//    private final Map<String,StreamObject> streamObjects6 = new ConcurrentHashMap<>();
//    private final Map<String,StreamObject> streamObjects7 = new ConcurrentHashMap<>();
//    private final Map<String,StreamObject> streamObjects8 = new ConcurrentHashMap<>();
//    private final Map<String,StreamObject> streamObjects9 = new ConcurrentHashMap<>();

//    private FluxSink<String> mainSink;
//    private Flux<String> liveLocations;
//    private CopyOnWriteArraySet<StreamName> streamNames = new CopyOnWriteArraySet<>();
//    private final Map<String, Set<StreamObject>> streamNames1 = new CopyOnWriteMap<>();
//    private Map<String, StreamName> streamNames2 = new HashMap<>();
//    private Map<String, StreamName> streamNames3 = new HashMap<>();
//    private Map<String, StreamName> streamNames4 = new HashMap<>();


    @PostConstruct
    public LocationRepository setup() throws Exception {
        log.info("SETUP REDIS POLLER");
//        streamNames2.put("_", new StreamName("_", Instant.now().toEpochMilli(), 0L));
//        streamNames3.put("_", new StreamName("_", Instant.now().toEpochMilli(), 0L));
//        streamNames4.put("_", new StreamName("_", Instant.now().toEpochMilli(), 0L));
//        for (int i = 1; i < 100000; i++) {
//            streamNames.add(""+(600000+i));
//        }
//        this.liveLocations = Flux.create(sink -> this.mainSink = sink);
//        subscribe = this.liveLocations.subscribe();

        //CONSUME with stream_name, on Terminate remove stream_name
//        Flux<String> objectFlux = Flux.create(sink -> this.mainSink = sink);
//        liveLocations = objectFlux
//                .onBackpressureBuffer(100000)
////                .doOnNext(it -> log.info("Processing: " + it))
//                .publish()
//                .autoConnect(0);
//                .subscribe();
//        subscribeRedisPoller1(streamNames1);
//        subscribeRedisPoller1(streamNames2);
//        subscribeRedisPoller1(streamNames3);
//        subscribeRedisPoller1(streamNames4);

//        new Thread(() -> { while (true) {staticRedisPoller1(this.streamObjects0);} }).start();
        new Thread(() -> { while (true) {staticRedisPoller1(this.streamObjects1);} }).start();
//        new Thread(() -> { while (true) {staticRedisPoller1(this.streamObjects2);} }).start();
//        new Thread(() -> { while (true) {staticRedisPoller1(this.streamObjects3);} }).start();
//        new Thread(() -> { while (true) {staticRedisPoller1(this.streamObjects4);} }).start();
//        new Thread(() -> { while (true) {staticRedisPoller1(this.streamObjects5);} }).start();
//        new Thread(() -> { while (true) {staticRedisPoller1(this.streamObjects6);} }).start();
//        new Thread(() -> { while (true) {staticRedisPoller1(this.streamObjects7);} }).start();
//        new Thread(() -> { while (true) {staticRedisPoller1(this.streamObjects8);} }).start();
//        new Thread(() -> { while (true) {staticRedisPoller1(this.streamObjects9);} }).start();
        return this;
    }

    public Flux<Location> getFluxByOrderId(String orderId) {
        String thisStreamName = STREAM_PREFIX+orderId;
        StreamObject.FluxSinkObject thisFluxSinkObject = StreamObject.FluxSinkObject.empty();

        Flux<Object> liveLocations = Flux.create(sink -> thisFluxSinkObject.setSink(sink))
//            .onBackpressureBuffer(100000)
//            .doOnNext(it -> log.info("Processing: " + it))
            .publish()
            .autoConnect(0);

        //TESTING
        streamObjects1.putIfAbsent(thisStreamName, StreamObject.build(thisStreamName));
        StreamObject thisStreamObject = streamObjects1.get(thisStreamName);
        thisStreamObject.addFluxObject(thisFluxSinkObject);

//        if (thisStreamObject.getStreamName().equals(DEBUG_STREAM)) {
//            log.info("Registering stream: " + thisStreamObject.getStreamName());
//        }



        //select stream Set
//        int streamNr = new Random().nextInt(100) % 1;
//        System.out.println("Selecting random: "+streamNr);
//        Map<String, StreamName> currentStreamNames;
//        switch (streamNr) {
//            case 0:
//                currentStreamNames = streamNames1;
//                break;
//            case 1:
//                currentStreamNames = streamNames2;
//                break;
//            case 2:
//                currentStreamNames = streamNames3;
//                break;
//            case 3:
//                currentStreamNames = streamNames4;
//                break;
//            default:
//                throw new RuntimeException("Wrong random value: "+streamNr);
//        }



//        return readTemplate
//                .randomKey()
//                .log()
//                .map(it -> new Location(it, 0,0,null,null,null,null,null,null,null))
//                .flux()
//                ;

        return liveLocations
//                .log()
                .map(event -> deserializeLocation(event.toString()))
                .takeUntil(location -> location.isLast())
//                .filter(event -> event.getOrderId().equals(orderId))
                .doFinally(e -> {
                    thisStreamObject.removeFluxObject(thisFluxSinkObject);
                    if (thisStreamObject.getStreamName().equals(DEBUG_STREAM)) {
                        log.info("Finally triggered UNRegistering consumer: "+thisStreamObject.getStreamName()+". Cause: "+e);
//                        log.info("Remaining stream names: "+streamObjects.values());
                    }
                })
                ;
    }

    public void emit(Location location) {
        StreamObject streamObject = streamObjects1.get(STREAM_PREFIX + location.getOrderId());
        streamObject.getFluxSinkObjects().forEach(fluxSinkObject -> {
            fluxSinkObject.getSink().next(serializeLocation(location));
        });
    }

    private Location deserializeLocation(String event) {
        try {
            return mapper.readValue(event, Location.class);
        } catch (JsonProcessingException e) { throw new RuntimeException("Can not deserialize json "+event); }
    }

    private String serializeLocation(Location event) {
        try {
            return mapper.writeValueAsString(event);
        } catch (JsonProcessingException e) { throw new RuntimeException("Can not serialize json "+event); }
    }

//    private void subscribeRedisPoller1(Map<String, StreamObject> currentStreamNames) {
//        log.info("Number of streams per pulling: "+currentStreamNames.size());
////        System.out.println("Poller starting for: "+currentStreamNames.keySet());
////        AtomicInteger counter = new AtomicInteger();
//        //TODO: fix offset time/gap between recursive calls
//        Set<StreamOffset<String>> streamNamesCollection = currentStreamNames.values().stream()
//                .map(streamName -> StreamOffset.create(streamName.getStreamName(), ReadOffset.from("" + (streamName.getOffsetTimeMs()) + "-"+streamName.getOffsetCount())))
//                .collect(Collectors.toSet());
//
//        StreamOffset<String>[] streamNamesArray = new StreamOffset[streamNamesCollection.size()];
//        streamNamesCollection.toArray(streamNamesArray);
////        System.out.println(streamNamesCollection);
//        template.opsForStream()
//                .read(StreamReadOptions.empty().block(Duration.ofSeconds(2)).count(5000), streamNamesArray)
//                .doOnTerminate(() -> {
////                    log.info("Terminated.");
//                    subscribeRedisPoller1(currentStreamNames);
////                    System.out.println("Stream batch request: " + counter);
//                })
////                .parallel(10)
////                .runOn(Schedulers.newParallel("Pooler", 10))
////                .onBackpressureDrop()
//                .doOnNext(it -> {
////                    log.info("Fux element: "+it);
//                        sinks.get(it.getStream()).forEach(sink -> sink.next(
//                                it.getValue().get("payload").toString()
//                                .replace("10000000000001", it.getId().getTimestamp().toString())
//                                .replace("20000000000002", ""+Instant.now().toEpochMilli())
//                        ));
//                })
//                .doOnNext(it -> {
//                    if (it.getStream().equals(STREAM_PREFIX+"600001")) log.info("Now: {}. From Redis at {}. Event: {}", Instant.now().toEpochMilli(), it.getId().getTimestamp(), it.getValue());
//                })
//                .doOnNext(it -> {
//                    currentStreamNames.put(it.getStream(), new StreamObject(it.getStream(), it.getId().getTimestamp(),  (it.getId().getSequence()+1)));
////                    counter.incrementAndGet();
//                })
//                .doOnError((e) -> {
//                    log.info("Error: "+e);
//                })
//
////                .doAfterTerminate(() -> {
////                    log.info("After terminated.");
////                })
////                .doFinally((e) -> {
////                    log.info("Finally "+e);
////                })
//                .subscribe();
//    }




    public void staticRedisPoller1(Map<String, StreamObject> streamObjects) {
        Set<StreamOffset<String>> streamNamesCollection = streamObjects.values().stream()
                .filter(streamObject -> streamObject.hasFluxSinkObjects())
                .map(streamObject -> StreamOffset.create(streamObject.getStreamName(), ReadOffset.from("" + (streamObject.getOffsetTimeMs()) + "-"+streamObject.getOffsetCount())))
                .collect(Collectors.toSet());

        StreamOffset<String>[] streamNamesArray = new StreamOffset[streamNamesCollection.size()];
        streamNamesCollection.toArray(streamNamesArray);

        if (streamNamesCollection.size()>0) log.info("Redis query for {} streams.", streamNamesCollection.size());

        if (streamNamesCollection.isEmpty()) return;

        long startTime = Instant.now().toEpochMilli();
        List<MapRecord<String, Object, Object>> eventBatch = redisTemplate.opsForStream()
//                .read(StreamReadOptions.empty().block(Duration.ofSeconds(1)).count(10000), streamNamesArray)
                .read(streamNamesArray)
                ;
        long endTime = Instant.now().toEpochMilli();


        eventBatch.forEach(it -> {
            final String event = it.getValue().get("payload").toString()
                    .replace("10000000000001", it.getId().getTimestamp().toString())
                    .replace("20000000000002", ""+Instant.now().toEpochMilli());

            Set<StreamObject.FluxSinkObject> thisFluxSinkObjects = streamObjects.get(it.getStream()).getFluxSinkObjects();
            thisFluxSinkObjects.forEach(fluxSinkObject -> {
                fluxSinkObject.getSink().next(event);
                streamObjects.put(it.getStream(), new StreamObject(it.getStream(), it.getId().getTimestamp(), (it.getId().getSequence() + 1), thisFluxSinkObjects));
            });
        });

        //log
        Optional<MapRecord<String, Object, Object>> anyDebugEvent = eventBatch.stream().filter(it -> it.getStream().equals(DEBUG_STREAM)).findAny();
        anyDebugEvent.ifPresent(it -> {
            System.out.println();
            log.info("Now: {}. From Redis at {}. Event: {}", Instant.now().toEpochMilli(), it.getId().getTimestamp(), it.getValue());
            log.info("Network pulling size: {}, Pulling time ms: {}. ", eventBatch.size(), (endTime - startTime));
            if (streamNamesCollection.stream().anyMatch(streamName -> streamName.getKey().equals(DEBUG_STREAM))) {log.info("Active streams (count:{}): {}", streamNamesCollection.size(), "streamNamesCollection");} //log
            System.out.println();
        });
    }



//    private void withStreamReceiver() {
//        streamReceiver.receive(StreamOffset.create(DEBUG_STREAM, ReadOffset.lastConsumed()))
//                .doOnNext(it -> {
//                    log.info("Received: "+it);
//                })
//                .subscribe();
//    }

    @Value
    private static class StreamObject {
        private final String streamName;
        private final Long offsetTimeMs;
        private final Long offsetCount;
        private final Set<FluxSinkObject> fluxSinkObjects;

        public static StreamObject build(String newStreamName) {
            return new StreamObject(newStreamName, Instant.EPOCH.toEpochMilli(), 0L, new CopyOnWriteArraySet<>());
        }

        public void addFluxObject(FluxSinkObject newFluxSinkObject) {
            fluxSinkObjects.add(newFluxSinkObject);
        }
        public void removeFluxObject(FluxSinkObject newFluxSinkObject) {
            fluxSinkObjects.remove(newFluxSinkObject);
        }

        public boolean hasFluxSinkObjects() {
            return !fluxSinkObjects.isEmpty();
        }

//        @Value
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @EqualsAndHashCode
        @ToString
        public static class FluxSinkObject {
            private final String id = UUID.randomUUID().toString();
            private FluxSink<Object> sink;

            public static FluxSinkObject build(FluxSink<Object> newFluxSink) {
                return new FluxSinkObject(newFluxSink);
            }

            public static FluxSinkObject empty() {
                return new FluxSinkObject();
            }

            public void setSink(FluxSink<Object> newSink) {
                this.sink = newSink;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                FluxSinkObject that = (FluxSinkObject) o;
                return Objects.equals(id, that.id);
            }
        }
    }
}


