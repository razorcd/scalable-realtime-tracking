package com.takeaway.tracking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Random;

@Data
@Document
@AllArgsConstructor
public class Location {

    @Id
    private String id;

    @Indexed
    private String orderId;
    private final double lng;
    private final double lat;
    private final String creation;
    private Long publishing;

    private Long createdAtInMongo;

    public static Location random() {
        Random rnd = new Random();
        return new Location(null, Integer.toString(rnd.nextInt()), rnd.nextDouble(), rnd.nextDouble(), Instant.now().toString(), null, Instant.now().toEpochMilli());
    }
}
