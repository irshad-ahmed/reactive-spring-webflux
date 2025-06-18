package com.reactivespring.controller;

import com.reactivespring.domain.Movie;
import com.reactivespring.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/movies")
public class MoviesController {
    @Autowired
    MovieService movieService;

    public MoviesController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Movie> addMovie(@RequestBody @Valid Movie movie) {
        return movieService.addMovie(movie).log();
    }

    @GetMapping
    public Flux<Movie> getAllMovies() {
        return movieService.getAllMovies().log();
    }

    @GetMapping("/{id}")
    public Mono<Movie> getMovieById(@PathVariable String id) {
        return movieService.getMovieById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Movie> updateMovieById(@PathVariable String id, @RequestBody Movie newMovie) {
        return movieService.updateMovie(id,newMovie);

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteMovieById(@PathVariable String id) {
        return movieService.deleteMovie(id);

    }
}
