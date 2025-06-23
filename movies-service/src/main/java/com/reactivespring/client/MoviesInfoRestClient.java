package com.reactivespring.client;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.domain.Review;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class MoviesInfoRestClient {
    private WebClient webClient;

    @Value("${restClient.moviesInfoServiceUrl}")
    private String moviesInfoServiceUrl;


    public MoviesInfoRestClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<MovieInfo> retrieveMovieInfo(String movieId) {
        String url = UriComponentsBuilder.fromHttpUrl(moviesInfoServiceUrl)
                .buildAndExpand(movieId)
                .toUriString();

        // var url = moviesInfoServiceUrl + "/" + movieId;
        log.info("URL for retrieving movie info: {}", url);

        return webClient.get()
                .uri(url, movieId)
                .retrieve()
                .bodyToMono(MovieInfo.class)
                .log();

    }

}
