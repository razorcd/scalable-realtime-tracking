package com.takeaway.tracking;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Location {

//    @Id
    private String orderId;
    private final double lng;
    private final double lat;

    private final String createdByTheDriver1;
    private Long receivedFromKafka2;
    private Long savedInMongo3;
    private Long publishingToFE4;
    private Boolean first;
    private Boolean last;

    private Long counter;

    public boolean isFirst() {
        return first == true;
    }

    public boolean isLast() {
        return last == true;
    }
}
