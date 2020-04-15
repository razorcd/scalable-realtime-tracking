package com.takeaway.tracking;


import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface LocationsReactiveRepository extends ReactiveCrudRepository<Location,String> {

    Mono<Location> save(Location localtion);

    Flux<Location> findByOrderId(String orderId);

    @Tailable
    Flux<Location> findByDbPartitionAndCreatedByTheDriver1IsGreaterThan(byte dbPartition, Long createdByTheDriver);
}
