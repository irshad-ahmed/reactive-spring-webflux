package com.reactivespring.repository;

import com.reactivespring.domain.Movie;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovieRepository extends ReactiveMongoRepository<Movie,String> {

    Flux<Movie> findByYear(Integer year);
    Flux<Movie> findByName(String name);
}
