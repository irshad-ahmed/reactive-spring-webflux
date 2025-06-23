package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MovieService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/movies")
@Slf4j
public class MoviesController {

    MovieService movieService;

    public MoviesController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovie(@RequestBody @Valid MovieInfo movie) {
        return movieService.addMovie(movie).log();
    }

    @GetMapping
    public Flux<MovieInfo> getAllMovies(@RequestParam(value = "year", required = false) Integer year,
                                        @RequestParam(value = "name", required = false) String name) {
        log.info("Year is : {}", year);
        if (name != null) {
            return movieService.getMoviesByName(name);
        }
        if (year != null) {
            return movieService.getMoviesByYear(year);
        }
        return movieService.getAllMovies().log();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<MovieInfo>> getMovieById(@PathVariable String id) {
        return movieService.getMovieById(id)
                .map(movie -> {
                    return ResponseEntity.ok().body(movie);
                })
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .log();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<MovieInfo>> updateMovieById(@PathVariable String id, @RequestBody MovieInfo newMovie) {
        return movieService.updateMovie(id, newMovie)
                .map(movie -> {
                    return ResponseEntity.ok().body(movie);
                })
                .switchIfEmpty(Mono.just((ResponseEntity.notFound().build())))
                .log();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteMovieById(@PathVariable String id) {
        return movieService.deleteMovie(id);

    }
}
