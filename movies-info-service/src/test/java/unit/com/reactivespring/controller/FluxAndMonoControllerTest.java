package com.reactivespring.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

@WebFluxTest(controllers = FluxAndMonoController.class)
@AutoConfigureWebTestClient
public class FluxAndMonoControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void flux() {
        //given

        //when
        webTestClient.get()
                .uri("/flux")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Integer.class)
                .hasSize(5);
        //then

    }

    @Test
    void fluxUsingConsumeWith() {
        //given

        //when
        webTestClient.get()
                .uri("/flux")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Integer.class)
                .consumeWith(listEntityExchangeResult -> {
                    var responseBody = listEntityExchangeResult.getResponseBody();
                    assert responseBody != null;
                    assert responseBody.size() == 5;
                });
        //then

    }

    @Test
    void helloWorld() {
        var mono = webTestClient.get()
                .uri("/mono")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .returnResult(String.class)
                .getResponseBody();

        StepVerifier.create(mono)
                .expectNext("Hello World")
                .verifyComplete();
    }

    @Test
    void helloWorldWithExpectBody() {
        var mono = webTestClient.get()
                .uri("/mono")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(String.class)
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assert responseBody != null;
                    assert responseBody.equals("Hello World");
                });
    }

    @Test
    void stream() {
        var flux = webTestClient.get()
                .uri("/stream")
                .accept(org.springframework.http.MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .returnResult(Long.class)
                .getResponseBody();
        StepVerifier.create(flux)
                .expectNext(0L,1L,2L) // Adjust the count based on your test needs
                .thenCancel()
                .verify();
    }
}
