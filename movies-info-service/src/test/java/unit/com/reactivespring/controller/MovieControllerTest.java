package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
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
                        new MovieInfo("1", "Batman", "Description 1", List.of("Christian Bale", "Actor 2"), LocalDate.of(2018, 10, 1), 2018),
                        new MovieInfo("2", "Mission Impossible", "Description 1", List.of("Actor 1", "Actor 2"), LocalDate.of(2025, 10, 1), 2025),
                        new MovieInfo("3", "Harry Potter", "Description 1", List.of("Actor 1", "Actor 2"), LocalDate.of(2008, 10, 1), 2008)
                ));

        webTestClient.get().uri(MOVIES_URI)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);
    }

    @Test
    void getMovieById() {

        String movieId = "1";
        when(movieServiceMock.getMovieById(isA(String.class)))
                .thenReturn(Mono.just(
                        new MovieInfo("1", "Batman", "Description 1", List.of("Christian Bale", "Actor 2"), LocalDate.of(2018, 10, 1), 2018)
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
        var movie = new MovieInfo(null, "Batman Begins", "Description 2", List.of("Christian Bale", "Actor 2"), LocalDate.of(2018, 10, 1), 2018);
        when(movieServiceMock.addMovie(isA(MovieInfo.class)))
                .thenReturn(Mono.just(
                        new MovieInfo("123", "Batman Begins", "Description 2", List.of("Christian Bale", "Actor 2"), LocalDate.of(2018, 10, 1), 2018)
                ));

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
    }

    @Test
    public void updateMovie() {
        //given
        var movie = new MovieInfo(null, "Batman Begins", "Description 2", List.of("Christian Bale", "Actor 2"), LocalDate.of(2018, 10, 1), 2018);
        when(movieServiceMock.updateMovie(isA(String.class), isA(MovieInfo.class)))
                .thenReturn(Mono.just(
                        new MovieInfo("123", "Batman Begins", "Description 2", List.of("Christian Bale", "Actor 2"), LocalDate.of(2018, 10, 1), 2018)
                ));
        //when
        String movieId="123";
        webTestClient.put().uri(MOVIES_URI+"/{id}",movieId)
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
    public void deleteMovie() {
        String movieId="123";
        when(movieServiceMock.deleteMovie(isA(String.class)))
                .thenReturn(Mono.empty());
        webTestClient.delete().uri(MOVIES_URI+"/{id}",movieId)
                .exchange()
                .expectStatus()
                .isNoContent();

    }

    @Test
    public void whenAddingMovieMandatoryFieldsNeedsToBePresent() {
        //given
        var movie = new MovieInfo(null, "", "Description 2", List.of(""), LocalDate.of(2018, 10, 1), -2018);
        //when
        webTestClient.post().uri(MOVIES_URI)
                .bodyValue(movie)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    System.out.println(responseBody);
                    assert responseBody != null;
                    assertEquals(responseBody,"Movie name must be provided,Year must be a positive number,cast must be present");
                });
    }

}
