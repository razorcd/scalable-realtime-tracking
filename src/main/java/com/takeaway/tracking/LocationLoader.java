//package com.takeaway.tracking;
//
//
//import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
//import org.springframework.data.redis.core.ReactiveRedisOperations;
//import org.springframework.stereotype.Component;
//import reactor.core.publisher.Flux;
//
//import javax.annotation.PostConstruct;
//import java.util.UUID;
//
//@Component
//public class LocationLoader {
//    private final ReactiveRedisConnectionFactory factory;
//    private final ReactiveRedisOperations<String, Location> locationOps;
//
//    public LocationLoader(ReactiveRedisConnectionFactory factory, ReactiveRedisOperations<String, Location> locationOps) {
//        this.factory = factory;
//        this.locationOps = locationOps;
//    }
//
//    @PostConstruct
//    public void loadData() {
//        factory.getReactiveConnection().serverCommands().flushAll().thenMany(
//                Flux.just(new Location("1", 3.2,2.2,null,null,null, null, true, false))
////                        .map(name -> new Location(UUID.randomUUID().toString(), name))
//                        .flatMap(location -> {
//                            return locationOps.opsForValue().set(location.getOrderId(), location);
//                        }))
//                .thenMany(locationOps.keys("*")
//                        .flatMap(locationOps.opsForValue()::get))
//                .subscribe((e) -> {
//                        System.out.println(e);
//                });
//    }
//}