package com.reactivespring.controller;

import com.reactivespring.domain.Movie;
import com.reactivespring.service.MovieService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static com.reactivespring.controller.MovieControllerIntgTest.MOVIES_URI;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = MoviesController.class)
@AutoConfigureWebTestClient
public class MovieControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private MovieService movieServiceMock;

    @Test
    void getAllMovies() {

        when(movieServiceMock.getAllMovies())
                .thenReturn(Flux.just(
                        new Movie("1", "Batman", "Description 1", List.of("Christian Bale", "Actor 2"), LocalDate.of(2018, 10, 1)),
                        new Movie("2", "Mission Impossible", "Description 1", List.of("Actor 1", "Actor 2"), LocalDate.of(2025, 10, 1)),
                        new Movie("3", "Harry Potter", "Description 1", List.of("Actor 1", "Actor 2"), LocalDate.of(2008, 10, 1))
                ));

        webTestClient.get().uri(MOVIES_URI)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Movie.class)
                .hasSize(3);
    }

    @Test
    void getMovieById() {
        String movieId = "1";
        when(movieServiceMock.getMovieById(isA(String.class)))
                .thenReturn(Mono.just(
                        new Movie("1", "Batman", "Description 1", List.of("Christian Bale", "Actor 2"), LocalDate.of(2018, 10, 1))
                ));
        webTestClient.get().uri(MOVIES_URI+"/{id}",movieId)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.movieId").isEqualTo(movieId);

    }

    @Test
    public void addMovie() {
        //given
        var movie = new Movie(null, "Batman Begins", "Description 2", List.of("Christian Bale", "Actor 2"), LocalDate.of(2018, 10, 1));
        when(movieServiceMock.addMovie(isA(Movie.class)))
                .thenReturn(Mono.just(
                        new Movie("123", "Batman Begins", "Description 2", List.of("Christian Bale", "Actor 2"), LocalDate.of(2018, 10, 1))
                ));

        //when
        webTestClient.post().uri(MOVIES_URI)
                .bodyValue(movie)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Movie.class)
                .consumeWith(response -> {
                    var savedMovie = response.getResponseBody();
                    assert savedMovie != null;
                    assert savedMovie.getMovieId() != null;
                    assert savedMovie.getName().equals("Batman Begins");
                    assert savedMovie.getDescription().equals("Description 2");
                    assert savedMovie.getCast().contains("Christian Bale");
                });
        //then
    }

    @Test
    public void updateMovie() {
        //given
        var movie = new Movie(null, "Batman Begins", "Description 2", List.of("Christian Bale", "Actor 2"), LocalDate.of(2018, 10, 1));
        when(movieServiceMock.updateMovie(isA(String.class), isA(Movie.class)))
                .thenReturn(Mono.just(
                        new Movie("123", "Batman Begins", "Description 2", List.of("Christian Bale", "Actor 2"), LocalDate.of(2018, 10, 1))
                ));
        //when
        String movieId="123";
        webTestClient.put().uri(MOVIES_URI+"/{id}",movieId)
                .bodyValue(movie)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(Movie.class)
                .consumeWith(response -> {
                    var savedMovie = response.getResponseBody();
                    assert savedMovie != null;
                    assert savedMovie.getMovieId() != null;
                    assert savedMovie.getName().equals("Batman Begins");
                    assert savedMovie.getDescription().equals("Description 2");
                    assert savedMovie.getCast().contains("Christian Bale");
                });
        //then
    }

    @Test
    public void deleteMovie() {
        String movieId="123";
        when(movieServiceMock.deleteMovie(isA(String.class)))
                .thenReturn(Mono.empty());
        webTestClient.delete().uri(MOVIES_URI+"/{id}",movieId)
                .exchange()
                .expectStatus()
                .isNoContent();

    }

}
