package com.takeaway.tracking;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class SseRequests {

    public static final AtomicLong requestsCount = new AtomicLong(0L);
    public static final String URL = "https://.../location/{orderId}";
    public static final long MIN_ORDER_ID = 600000;
    public static final long MAX_ORDER_ID = 609000;
    public static final long REQUESTS_MULTIPLIER = 10;

    public static void main(String[] args) throws Exception {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        System.out.println("Stating requests count: " + (MAX_ORDER_ID - MIN_ORDER_ID)*REQUESTS_MULTIPLIER);
        showNrOfRequests();

        for (long i = 0; i < REQUESTS_MULTIPLIER; i++) {
            startRequestsThread();
            Thread.sleep(1000);
        }

//        WebClient webClient = WebClient.builder().build();
//        testEventStreamApiAsync(webClient, true, SseRequests.URL, "600001");

        Thread.sleep(99999999999L);
    }

    private static void showNrOfRequests() {
        new Thread(() -> {
            for (int i = 0; i >= 0 ; i++) {
                System.out.println("Current requests count: " + requestsCount);
                try { Thread.sleep(5000);} catch (InterruptedException e) {}
            }
        }).start();
    }


    /**
     * Start paralel reacive requests
     */
    private static void startRequestsThread() {
        WebClient webClient = WebClient.builder().build();

        new Thread(() -> {
            for (long i = MIN_ORDER_ID; i < MAX_ORDER_ID; i++) {
                    testEventStreamApiAsync(webClient, false, SseRequests.URL, i);
                    try { Thread.sleep(500L);} catch (InterruptedException e) {}
                }
            try { Thread.sleep(99999999999L);} catch (InterruptedException e) {System.out.println(e);}
        }).start();
    }

    /**
     * Creates a reactive request
     */
    private static void testEventStreamApiAsync(WebClient webClient, Boolean logEnabled, String trackingRefUri, Long params) {
        requestsCount.incrementAndGet();
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
                .doOnError(e -> System.out.println("WebClient connection ERROR for params: "+params+ " RequestsCount: " + requestsCount.decrementAndGet()))
//                .doOnTerminate(() -> System.out.println("WebClient connection TERMINATED for params: "+ params+ " RequestsCount: " + requestsCount.decrementAndGet()))
                .doOnNext(response -> {
                    response
                            .bodyToFlux(String.class)
                            .doOnNext(b -> {
                                if (logEnabled) System.out.println(Thread.currentThread().getId() + " - Received event: " + b);
                            })
                            .doOnTerminate(() -> System.out.println("Open stream TERMINATED with params: " + params))
                            .subscribe()
                    ;
                })
                .subscribe();
//                    .take(1000)
//                    .blockLast();
    }
}

