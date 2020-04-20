package com.takeaway.tracking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;


@Component
//@EnableBinding
@Slf4j
@RequiredArgsConstructor
public class LocationRepository {
    public static final String STREAM_PREFIX = "streamI_";

    private final ReactiveRedisTemplate<String, String> template;
//    private ReactiveStringRedisTemplate template;

    private final ObjectMapper mapper;
//    @Autowired
//    StreamReceiver<String,ObjectRecord<String,String>> streamReceiver;
//    @Autowired
//    RedisReactiveCommands<String, String> commands;

    private FluxSink<Map<Object,Object>> mainSink;
    private Flux<Map<Object,Object>> liveLocations;
    private CopyOnWriteArraySet<StreamName> streamNames = new CopyOnWriteArraySet<>();

    @PostConstruct
    public LocationRepository setup() {
        log.info("SETUP REDIS POLLER");
        streamNames.add(new StreamName("_"));
//        for (int i = 1; i < 100000; i++) {
//            streamNames.add(""+(600000+i));
//        }
        this.liveLocations = Flux.create(sink -> this.mainSink = sink);
//        subscribe = this.liveLocations.subscribe();

        //CONSUME with stream_name, on Terminate remove stream_name
        Flux<Map<Object,Object>> objectFlux = Flux.create(sink -> this.mainSink = sink);
        liveLocations = objectFlux
//                .doOnNext(it -> log.info("Processing: " + it))
                .publish()
                .autoConnect(0);
//                .subscribe();
        subscribeRedisPoller();
        return this;
    }

    public Flux<Location> getFluxByOrderId(String orderId) {
        log.info("Registering consumer: "+STREAM_PREFIX+orderId);
        final StreamName streamName = new StreamName(orderId);
        streamNames.add(streamName);

        return liveLocations
//                .log()
                .map(event -> deserializeLocation(event.get("payload").toString()))
                .filter(event -> event.getOrderId().equals(orderId))
                .doFinally(e -> {
                    streamNames.remove(streamName);
                    log.info("Finally triggered UNRegistering consumer: "+orderId+" Cause: "+e);
                })
                ;
    }

    private Location deserializeLocation(String event) {
        try {
            return mapper.readValue(event, Location.class);
        } catch (JsonProcessingException e) { throw new RuntimeException("Can not parse json "+event); }
    }

    private void subscribeRedisPoller() {
        //TODO: fix offset time/gap between recursive calls
        Set<StreamOffset<String>> streamNamesCollection = streamNames.stream()
                .map(streamName -> StreamOffset.create(streamName.getName(), ReadOffset.from("" + (streamName.getStartTime().toEpochMilli()) + "-0")))
                .collect(Collectors.toSet());

        StreamOffset<String>[] streamNamesArray = new StreamOffset[streamNamesCollection.size()];
        streamNamesCollection.toArray(streamNamesArray);
//        System.out.println(streamNamesCollection);
        template.opsForStream()
                .read(StreamReadOptions.empty().block(Duration.ofSeconds(5)), streamNamesArray)
                .doOnNext(it -> this.mainSink.next(it.getValue()))
                .doOnTerminate(() -> {
//                    log.info("Terminated.");
                    subscribeRedisPoller();
                })
                .subscribe();
    }

    @Value
    private class StreamName {
        private final String id = UUID.randomUUID().toString();
        private final String name;
        private final Instant startTime = Instant.now();

        public StreamName(String name) {
            this.name = STREAM_PREFIX+name;
        }
    }
}


