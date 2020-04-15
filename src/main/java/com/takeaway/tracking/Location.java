package com.takeaway.tracking;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@AllArgsConstructor
public class Location {

    public static final int DB_PARTITIONS_COUNT = 50;

    @Id
    private String id;

    @Indexed
    private byte dbPartition;

    @Indexed
    private String orderId;
    private final double lng;
    private final double lat;

    @Indexed
    private final Long createdByTheDriver1;

    private Long receivedFromKafka2;
    private Long savedInMongo3;
    private Long publishingToFE4;
    private Boolean first;
    private Boolean last;

    public boolean isFirst() {
        return first == true;
    }

    public boolean isLast() {
        return last == true;
    }
}
