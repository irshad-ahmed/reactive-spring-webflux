package com.reactivespring.service;

import com.reactivespring.domain.Movie;
import com.reactivespring.repository.MovieRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MovieService {

    private MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public Mono<Movie> addMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    public Flux<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Mono<Movie> getMovieById(String id) {
        return movieRepository.findById(id);
    }

    public Mono<Movie> updateMovie(String id, Movie newMovie) {
        return movieRepository.findById(id)
                .flatMap(
                        movie -> {
                            movie.setName(newMovie.getName());
                            movie.setDescription(newMovie.getDescription());
                            movie.setCast(newMovie.getCast());
                            movie.setReleaseDate(newMovie.getReleaseDate());
                            return movieRepository.save(movie);
                        }
                );
    }

    public Mono<Void> deleteMovie(String id) {
        return movieRepository.deleteById(id);
    }
}
