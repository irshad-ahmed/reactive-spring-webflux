package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
public class MovieControllerIntgTest {

    public static final String MOVIES_URI = "/v1/movies";
    @Autowired
    MovieRepository movieRepository;

    @Autowired
    WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        var movieList = List.of(
                new MovieInfo(null, "Batman", "Description 1", List.of("Christian Bale", "Actor 2"), LocalDate.of(2018, 10, 1), 2018),
                new MovieInfo(null, "Mission Impossible", "Description 1", List.of("Actor 1", "Actor 2"), LocalDate.of(2025, 10, 1), 2025),
                new MovieInfo("123", "Harry Potter", "Description 1", List.of("Actor 1", "Actor 2"), LocalDate.of(2008, 10, 1), 2008)
        );
        movieRepository.saveAll(movieList).blockLast();
    }

    @AfterEach
    void tearDown() {
        movieRepository.deleteAll().block();
    }

    @Test
    public void addMovie() {
        //given
        var movie = new MovieInfo(null, "Batman Begins", "Description 2", List.of("Christian Bale", "Actor 2"), LocalDate.of(2018, 10, 1), 2018);

        //when
        webTestClient.post().uri(MOVIES_URI)
                .bodyValue(movie)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(response -> {
                    var savedMovie = response.getResponseBody();
                    assert savedMovie != null;
                    assert savedMovie.getMovieId() != null;
                    assert savedMovie.getName().equals("Batman Begins");
                    assert savedMovie.getDescription().equals("Description 2");
                    assert savedMovie.getCast().contains("Christian Bale");
                    assert savedMovie.getYear().equals(2018);
                });
        //then
    }

    @Test
    public void getAllMovie() {
        //given
        //var movie = new Movie(null, "Batman Begins", "Description 2", List.of("Christian Bale", "Actor 2"), LocalDate.of(2018, 10, 1));

        //when
        webTestClient.get().uri(MOVIES_URI)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);
        //then
    }

    @Test
    public void getAllMovieByYear() {
        //given
        //var movie = new Movie(null, "Batman Begins", "Description 2", List.of("Christian Bale", "Actor 2"), LocalDate.of(2018, 10, 1));
        var uri = UriComponentsBuilder.fromUriString(MOVIES_URI)
                .queryParam("year", 2018)
                .buildAndExpand().toUri();
        //when
        webTestClient.get().uri(uri)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(1);
        //then
    }

    @Test
    public void getAllMovieByName() {
        //given
        //var movie = new Movie(null, "Batman Begins", "Description 2", List.of("Christian Bale", "Actor 2"), LocalDate.of(2018, 10, 1));
        var uri = UriComponentsBuilder.fromUriString(MOVIES_URI)
                .queryParam("name", "Harry Potter")
                .buildAndExpand().toUri();
        //when
        webTestClient.get().uri(uri)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(1);
        //then
    }

    @Test
    void getMovieById() {
        String movieId = "123";
        webTestClient.get().uri(MOVIES_URI + "/{id}", movieId)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.movieId").isEqualTo(movieId);
                /*.expectBody(Movie.class)
                .consumeWith(response -> {
                    var movie = response.getResponseBody();
                    assert movie != null;
                    assert movie.getMovieId().equals(movieId);
                    assert movie.getName().equals("Harry Potter");
                    assert movie.getDescription().equals("Description 1");
                    assert movie.getCast().contains("Actor 1");
                    assert movie.getReleaseDate().equals(LocalDate.of(2008, 10, 1));
                });*/
    }

    @Test
    void findMoviesByIdNotFound() {
        String movieId = "1";

        webTestClient.get().uri(MOVIES_URI + "/{id}", movieId)
                .exchange()
                .expectStatus()
                .isNotFound();

    }

    @Test
    public void updateMovie() {
        //given
        var movie = new MovieInfo(null, "Batman Begins", "Description 2", List.of("Christian Bale", "Actor 2"), LocalDate.of(2018, 10, 1), 2018);

        //when
        String movieId = "123";
        webTestClient.put().uri(MOVIES_URI + "/{id}", movieId)
                .bodyValue(movie)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(response -> {
                    var savedMovie = response.getResponseBody();
                    assert savedMovie != null;
                    assert savedMovie.getMovieId() != null;
                    assert savedMovie.getName().equals("Batman Begins");
                    assert savedMovie.getDescription().equals("Description 2");
                    assert savedMovie.getCast().contains("Christian Bale");
                    assert savedMovie.getYear().equals(2018);
                });
    }

    @Test
    public void whenUpdateMovieWhichIsNotPresent() {
        //given
        var movie = new MovieInfo(null, "Batman Begins", "Description 2", List.of("Christian Bale", "Actor 2"), LocalDate.of(2018, 10, 1), 2018);

        //when
        String movieId = "abc";
        webTestClient.put().uri(MOVIES_URI + "/{id}", movieId)
                .bodyValue(movie)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    public void deleteMovie() {
        String movieId = "123";
        webTestClient.delete().uri(MOVIES_URI + "/{id}", movieId)
                .exchange()
                .expectStatus()
                .isNoContent();

    }


}
