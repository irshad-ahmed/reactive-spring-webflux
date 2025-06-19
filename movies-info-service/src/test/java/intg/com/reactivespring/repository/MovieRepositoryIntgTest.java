package com.reactivespring.repository;

import com.reactivespring.domain.Movie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataMongoTest
@ActiveProfiles("test")
class MovieRepositoryIntgTest {

    @Autowired
    MovieRepository movieRepository;

    @BeforeEach
    void setUp() {
        var movieList = List.of(
                new Movie(null, "Batman", "Description 1", List.of("Christian Bale", "Actor 2"), LocalDate.of(2018, 10, 1), 2018),
                new Movie(null, "Mission Impossible", "Description 1", List.of("Actor 1", "Actor 2"), LocalDate.of(2025, 10, 1), 2025),
                new Movie("123", "Harry Potter", "Description 1", List.of("Actor 1", "Actor 2"), LocalDate.of(2008, 10, 1), 2008)
        );
        movieRepository.saveAll(movieList).blockLast();
    }

    @AfterEach
    void tearDown() {
        movieRepository.deleteAll().block();
    }

    @Test
    void findAllMovies() {
        //given
        //when
        var allMovies = movieRepository.findAll().log();
        //then
        StepVerifier.create(allMovies)
                .expectNextCount(3)
                .verifyComplete();

    }

    @Test
    void findMoviesById() {
        //given
        //when
        var movieMono = movieRepository.findById("123").log();
        //then
        StepVerifier.create(movieMono)
                .assertNext(movie -> {
                    assert movie.getMovieId().equals("123");
                    assertEquals(movie.getName(),"Harry Potter");
                    assert movie.getDescription().equals("Description 1");
                    assert movie.getCast().contains("Actor 1");
                    assert movie.getReleaseDate().equals(LocalDate.of(2008, 10, 1));
                    assertEquals(movie.getYear(), 2008);
                })
                .verifyComplete();

    }

    @Test
    void saveMovie() {
        //given
        //when
        var movieMono = movieRepository.save(new Movie(null, "Batman", "Description 1", List.of("Christian Bale", "Actor 2"), LocalDate.of(2018, 10, 1), 2018)).log();
        //then
        StepVerifier.create(movieMono)
                .assertNext(movie -> {
                    assertNotNull(movie.getMovieId());
                    assertEquals(movie.getName(),"Batman");
                    assert movie.getDescription().equals("Description 1");
                    assert movie.getCast().contains("Actor 2");
                    assert movie.getReleaseDate().equals(LocalDate.of(2018, 10, 1));
                    assertEquals(movie.getYear(), 2018);
                })
                .verifyComplete();

    }

    @Test
    void updateMovie() {
        //given
        var movie = movieRepository.findById("123")
                .block();

        movie.setName("Harry Potter and the Philosopher's Stone");
        //when
        var movieMono = movieRepository.save(movie).log();
        //then
        StepVerifier.create(movieMono)
                .assertNext(tempMovie -> {
                    assertNotNull(tempMovie.getMovieId());
                    assertEquals(tempMovie.getName(),"Harry Potter and the Philosopher's Stone");
                })
                .verifyComplete();

    }

    @Test
    void deleteMovie() {
        //given

        //when
        movieRepository.deleteById("123")
                .block();

        var allMovies = movieRepository.findAll().log();
        //then
        StepVerifier.create(allMovies)
                .expectNextCount(2)
                .verifyComplete();

    }

    @Test
    void findByYear() {
        //given

        //when
        var allMoviesIn2025 = movieRepository.findByYear(2025).log();

       // var allMovies = movieRepository.findAll().log();
        //then
        StepVerifier.create(allMoviesIn2025)
                .expectNextCount(1)
                .verifyComplete();

    }

    @Test
    void findByName() {
        //given

        //when
        var allMoviesIn2025 = movieRepository.findByName("Mission Impossible").log();

        // var allMovies = movieRepository.findAll().log();
        //then
        StepVerifier.create(allMoviesIn2025)
                .expectNextCount(1)
                .verifyComplete();

    }

}