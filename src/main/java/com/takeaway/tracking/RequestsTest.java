package com.takeaway.tracking;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;



public class RequestsTest {
//    private static WebClient webClient;

    public static void main(String[] args) throws Exception {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);


        startCallsInThread(10000);
        startCallsInThread(10000);
//        startCallsInThread(10000);
//        startCallsInThread(10000);
//        startCallsInThread(10000);

        Thread.sleep(5000L);

        WebClient webClient = WebClient.builder().build();
        testEventStreamApiAsync(webClient, true, "localhost:8080/simple/location/{orderId}", "1");
//        testEventStreamApiAsync(webClient, true, "https://tracker-api.dev.scoober.com/v1/trackings/{id}", "5f61ff2a5bedab9e3c3b4da94efddf31eaa0d548b413b2c5ce866e33f259fa9f");

        Thread.sleep(10000000L);
    }

    private static void startCallsInThread(int requestCount) {
        WebClient webClient = WebClient.builder().build();


        new Thread(() -> {
            for (int i = 0; i < requestCount; i++) {
//                System.out.print(".");
                testEventStreamApiAsync(webClient, false, "localhost:8080/simple/location/{orderId}", "1");
//                testEventStreamApiAsync(webClient, false, "https://tracker-api.dev.scoober.com/v1/trackings/{id}", "5f61ff2a5bedab9e3c3b4da94efddf31eaa0d548b413b2c5ce866e33f259fa9f");
            }

            try {
                Thread.sleep(10000000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Creates tests for a reactive API
     * @return
     */
    private static void testEventStreamApiAsync(WebClient webClient, Boolean logEnabled, String trackingRefUri, Object... params) {
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
                                .doOnNext(b -> {
                                    if (logEnabled) System.out.println(Thread.currentThread().getId() + " - Received event: " + b);
                                })
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

