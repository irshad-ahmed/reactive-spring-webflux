package com.reactivespring.repository;

import com.reactivespring.domain.Movie;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface MovieRepository extends ReactiveMongoRepository<Movie,String> {
}
