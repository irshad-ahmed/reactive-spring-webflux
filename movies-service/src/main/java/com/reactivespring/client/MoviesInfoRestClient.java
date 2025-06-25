package com.reactivespring.client;

import com.reactivespring.domain.Movie;
import com.reactivespring.domain.MovieInfo;
import com.reactivespring.domain.Review;
import com.reactivespring.exception.MoviesInfoClientException;
import com.reactivespring.exception.MoviesInfoServerException;
import com.reactivespring.util.RetryUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;

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
//        String url = UriComponentsBuilder.fromHttpUrl(moviesInfoServiceUrl)
//                .buildAndExpand(movieId)
//                .toUriString();

        var url = moviesInfoServiceUrl + "/" + movieId;
        log.info("URL for retrieving movie info: {}", url);

        return webClient.get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,
                        clientResponse -> {
                            log.info("Status code: {}", clientResponse.statusCode().value());
                            if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                                log.error("4xx error while retrieving movie info for id: {}", movieId);
                                return Mono.error(new MoviesInfoClientException("There is no movie info available for the passed in Id: " + movieId,
                                        clientResponse.statusCode().value()));
                            }
                            return clientResponse.bodyToMono(String.class).flatMap(responseMessage -> Mono.error(new MoviesInfoClientException(responseMessage, clientResponse.statusCode().value())));
                        })
                .onStatus(HttpStatus::is5xxServerError,
                        clientResponse -> {
                            log.info("Status code: {}", clientResponse.statusCode().value());
                            return clientResponse.bodyToMono(String.class).flatMap(responseMessage -> Mono.error(new MoviesInfoServerException("Server Exception in MoviesInfoService responseMessage " + responseMessage)));
                        })
                .bodyToMono(MovieInfo.class)
                //.retry(3)
                .retryWhen(RetryUtil.retrySpec())
                .log();

    }

    public Flux<MovieInfo> retrieveMovieInfoStream() {
        var url = moviesInfoServiceUrl.concat("/stream");
        log.info("URL for retrieving movie info stream: {}", url);
        return webClient.get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,
                        clientResponse -> {
                            log.info("Status code: {}", clientResponse.statusCode().value());
                            return clientResponse.bodyToMono(String.class).flatMap(responseMessage -> Mono.error(new MoviesInfoClientException(responseMessage, clientResponse.statusCode().value())));
                        })
                .onStatus(HttpStatus::is5xxServerError,
                        clientResponse -> {
                            log.info("Status code: {}", clientResponse.statusCode().value());
                            return clientResponse.bodyToMono(String.class).flatMap(responseMessage -> Mono.error(new MoviesInfoServerException("Server Exception in MoviesInfoService responseMessage " + responseMessage)));
                        })
                .bodyToFlux(MovieInfo.class)
                //.retry(3)
                .retryWhen(RetryUtil.retrySpec())
                .log();
    }
}
