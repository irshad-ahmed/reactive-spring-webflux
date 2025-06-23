package com.reactivespring.controller;

import com.reactivespring.domain.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Objects;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port=0)
@TestPropertySource(
        properties = {
                "restClient.moviesInfoServiceUrl=http://localhost:${wiremock.server.port}/v1/movies/{movieId}",
                "restClient.moviesReviewUrl=http://localhost:${wiremock.server.port}/v1/reviews"
        }
)
public class MoviesControllerIntgTest {

        @Autowired
        WebTestClient webTestClient;

        @Test
        void retrieveMovieById() {
                //given
                stubFor(get(urlEqualTo("/v1/movies/123"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBodyFile("movieinfo.json")));

                stubFor(get(urlPathEqualTo("/v1/reviews"))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBodyFile("reviews.json")));
                //when
                webTestClient.get()
                        .uri("/v1/movies/{movieId}", "123")
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody(Movie.class)
                        .consumeWith(movieEntityExchangeResult -> {
                                var movie = movieEntityExchangeResult.getResponseBody();
                                assert Objects.requireNonNull(movie).getReviewList().size()==2;
                                assertEquals("Batman Begins", movie.getMovieInfo().getName());
                        });

        }
}
