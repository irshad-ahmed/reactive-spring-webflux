package com.reactivespring.repository;

import com.reactivespring.domain.MovieInfo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface MovieRepository extends ReactiveMongoRepository<MovieInfo,String> {

    Flux<MovieInfo> findByYear(Integer year);
    Flux<MovieInfo> findByName(String name);
}
