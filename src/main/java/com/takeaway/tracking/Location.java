package com.takeaway.tracking;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;

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
    private Long receivedFromDb;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Double.compare(location.lng, lng) == 0 &&
                Double.compare(location.lat, lat) == 0 &&
                Objects.equals(orderId, location.orderId) &&
//                Objects.equals(createdByTheDriver1, location.createdByTheDriver1) &&
//                Objects.equals(receivedFromKafka2, location.receivedFromKafka2) &&
//                Objects.equals(savedInMongo3, location.savedInMongo3) &&
//                Objects.equals(receivedFromDb, location.receivedFromDb) &&
//                Objects.equals(publishingToFE4, location.publishingToFE4) &&
                Objects.equals(first, location.first) &&
                Objects.equals(last, location.last)
//                &&
//                Objects.equals(counter, location.counter)
                ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, lng, lat, first, last);
    }
}
