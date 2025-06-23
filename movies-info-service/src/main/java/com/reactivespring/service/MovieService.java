package com.reactivespring.service;

import com.reactivespring.domain.MovieInfo;
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

    public Mono<MovieInfo> addMovie(MovieInfo movie) {
        return movieRepository.save(movie);
    }

    public Flux<MovieInfo> getAllMovies() {
        return movieRepository.findAll();
    }

    public Mono<MovieInfo> getMovieById(String id) {
        return movieRepository.findById(id);
    }

    public Mono<MovieInfo> updateMovie(String id, MovieInfo newMovie) {
        return movieRepository.findById(id)
                .flatMap(
                        movie -> {
                            movie.setName(newMovie.getName());
                            movie.setDescription(newMovie.getDescription());
                            movie.setCast(newMovie.getCast());
                            movie.setReleaseDate(newMovie.getReleaseDate());
                            movie.setYear(newMovie.getYear());
                            return movieRepository.save(movie);
                        }
                );
    }

    public Mono<Void> deleteMovie(String id) {
        return movieRepository.deleteById(id);
    }

    public Flux<MovieInfo> getMoviesByYear(Integer year) {
        return movieRepository.findByYear(year);
    }

    public Flux<MovieInfo> getMoviesByName(String name) {
        return  movieRepository.findByName(name);
    }
}
