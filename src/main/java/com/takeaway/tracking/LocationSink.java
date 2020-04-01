package com.takeaway.tracking;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface LocationSink {
    @Input("location-events")
    SubscribableChannel events();
}
