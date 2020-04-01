package com.takeaway.tracking;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;

@Repository
public interface LocationsRepository extends ReactiveCrudRepository<Location,String> {

//    Flux<Location> findByOrderId(String orderId);

//    Flux<Location> findWithTailabeAll();

    @Tailable
    Flux<Location> findByOrderId(String orderId);
}
