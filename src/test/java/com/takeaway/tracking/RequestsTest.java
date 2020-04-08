package com.takeaway.tracking;

import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;



public class RequestsTest {

//    @Configuration
//    static class Config {
//        @Bean
//        @Primary
//        public Clock clockFixed() {
//            return Clock.fixed(Instant.ofEpochMilli(1000), ZoneId.of("UTC"));
//        }
//    }

    private WebClient webClient;


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

//    @Before
//    public void setUp() throws Exception {
//        final Logger logger = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
//        logger.setLevel(Level.INFO);
//    }
    @Test
    public void test00a()
    {
    }

    @Test(timeout = 3000000)
    public void test() throws Exception {


        //given OrderCreatedEvent
//        Message<String> orderCreatedEventMessage = MessageBuilder
//                .withPayload(readEvent("e2e/TrackerE2ETest_A/input/order_created_event_asap_A01.json"))
//                .build();
//
//        //when
//        taOrderSink.orderEvent().send(orderCreatedEventMessage);

        webClient = WebClient.builder().build();

//        //API
        for (int i = 0; i < 2500; i++) {
//            System.out.print(".");
        testEventStreamApiAsync(false, "localhost:8080/global/location/{orderId}", "1");

        }
//        System.out.println();

        testEventStreamApiAsync(true, "localhost:8080/global/location/{orderId}", "1");
//        testEventStreamApiAsync(true, "https://tracker-api.dev.scoober.com/v1/trackings/{id}", "5f61ff2a5bedab9e3c3b4da94efddf31eaa0d548b413b2c5ce866e33f259fa9f");

        Thread.sleep(100000);
    }

    /**
     * Creates tests for a reactive API
     * @return
     */
    private void testEventStreamApiAsync(Boolean logEnabled, String trackingRefUri, Object... params) {
//        return new Thread(() -> {
            Mono<ClientResponse> result = webClient
                    .get()
                    .uri(trackingRefUri, params)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .exchange()
//                    .expectStatus()
//                    .isOk()
//                    .returnResult(new ParameterizedTypeReference<String>() {})
                    ;

            result
                    .doOnNext(response -> {
                        response
                                .bodyToFlux(String.class)
//                                .doOnNext(b -> {
////                                    if (logEnabled) System.out.println(Thread.currentThread().getId() + " - Received event: " + b);
//                                })
                                .subscribe()
                                ;
                    })
                    .subscribe();
//                    .take(1000)
//                    .blockLast();

//            countDownLatch.countDown();
//        });
    }

}

