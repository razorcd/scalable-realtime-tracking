package com.takeaway.tracking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import scala.collection.immutable.Stream;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@SpringBootTest
@Import({TrackingApplication.class})
@AutoConfigureWebTestClient(timeout = "60000000")
public class RequestsTestE2E {

//    @Configuration
//    static class Config {
//        @Bean
//        @Primary
//        public Clock clockFixed() {
//            return Clock.fixed(Instant.ofEpochMilli(1000), ZoneId.of("UTC"));
//        }
//    }

    @Autowired
    private WebTestClient webTestClient;

//    @Autowired
//    JobSink jobSink;

//    @Autowired
//    MessageCollector collector;
//    BlockingQueue<Message<?>> kafkaPublishedMessags;
//    BlockingQueue<Message<?>> rabbitPublishedMessags;
//
//    @Autowired
//    ObjectMapper mapper;
//
//    CountDownLatch countDownLatch = new CountDownLatch(2);

    @Test(timeout = 3000000)
    public void test() throws Exception {
        //given OrderCreatedEvent
//        Message<String> orderCreatedEventMessage = MessageBuilder
//                .withPayload(readEvent("e2e/TrackerE2ETest_A/input/order_created_event_asap_A01.json"))
//                .build();
//
//        //when
//        taOrderSink.orderEvent().send(orderCreatedEventMessage);

        //API
        for (int i = 0; i < 25000; i++) {
//            System.out.print(".");
            testEventStreamApiAsync(false, "/global/location/{orderId}", "1").start();
        }
//        System.out.println();
        testEventStreamApiAsync(true, "/global/location/{orderId}", "1").run();
        Thread.sleep(1000);
    }

    /**
     * Creates tests for a reactive API
     * @return
     */
    private Thread testEventStreamApiAsync(Boolean logEnabled, String trackingRefUri, Object... params) {
        return new Thread(() -> {
            FluxExchangeResult<String> result = webTestClient
                    .get()
                    .uri(trackingRefUri, params)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .exchange()
                    .expectStatus()
                    .isOk()
                    .returnResult(new ParameterizedTypeReference<String>() {})
                    ;

            result
                    .getResponseBody()
                    .doOnNext(e -> {if (logEnabled) System.out.println(Thread.currentThread().getId() + " - Received event: "+e);})
                    .take(1000)
                    .blockLast();

//            countDownLatch.countDown();
        });
    }

}

