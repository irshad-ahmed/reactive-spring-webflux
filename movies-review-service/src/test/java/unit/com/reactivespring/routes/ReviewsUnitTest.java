package com.reactivespring.routes;

import com.reactivespring.domain.Review;
import com.reactivespring.exceptionhandler.GlobalErrorHandler;
import com.reactivespring.handler.ReviewHandler;
import com.reactivespring.repository.ReviewReactiveRepository;
import com.reactivespring.router.ReviewRouter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = {ReviewRouter.class, ReviewHandler.class, GlobalErrorHandler.class})
@AutoConfigureWebTestClient
public class ReviewsUnitTest {

    public static final String REVIEW_URI = "/v1/reviews";
    @MockBean
    private ReviewReactiveRepository reviewReactiveRepository;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void addReview() {
        //given
        var review = new Review(null, "123", "Amazing!", 4.8);

        when(reviewReactiveRepository.save(isA(Review.class)))
                .thenReturn(Mono.just(new Review("789", "123", "Amazing!", 4.8)));

        //when
        webTestClient
                .post()
                .uri(REVIEW_URI)
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Review.class)
                .consumeWith(reviewEntityExchangeResult -> {
                    var savedReview = reviewEntityExchangeResult.getResponseBody();
                    assert savedReview.getReviewId() != null;
                    assert savedReview.getMovieId().equals("123");
                    assert savedReview.getComment().equals("Amazing!");
                    assert savedReview.getRating() == 4.8;
                });
    }

    @Test
    void addReviewWithValidationError() {
        //given
        var review = new Review(null, null, "", -9.0); // Empty comment to trigger validation error

        //when
        webTestClient
                .post()
                .uri(REVIEW_URI)
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .consumeWith(response -> {
                    String errorMessage = response.getResponseBody();
                    assert errorMessage.equals("movieId.blank : movieId must be provided, rating.negative : please pass a non-negative value");
                });
    }

    @Test
    void getAllReviews() {
        //given
        var reviewList = List.of(
                new Review("1", "123", "Great movie!", 5.0),
                new Review("2", "123", "Must watch!", 4.5),
                new Review("3", "456", "Not bad", 3.0)
        );

        when(reviewReactiveRepository.findAll())
                .thenReturn(Flux.fromIterable(reviewList));

        //when
        webTestClient.get()
                .uri(REVIEW_URI)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Review.class)
                .hasSize(3);
    }

    @Test
    void updateReview() {
        //given
        var reviewId = "123";
        var updatedReview = new Review(reviewId, "123", "Updated comment", 4.0);

        when(reviewReactiveRepository.findById(reviewId))
                .thenReturn(Mono.just(new Review(reviewId, "123", "Old comment", 3.5)));

        when(reviewReactiveRepository.save(isA(Review.class)))
                .thenReturn(Mono.just(updatedReview));
        //when
        webTestClient
                .put()
                .uri(REVIEW_URI + "/{id}", reviewId)
                .bodyValue(updatedReview)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Review.class)
                .consumeWith(reviewEntityExchangeResult -> {
                    var savedReview = reviewEntityExchangeResult.getResponseBody();
                    assert savedReview != null;
                    assert savedReview.getReviewId().equals(reviewId);
                    assert savedReview.getComment().equals("Updated comment");
                    assert savedReview.getRating() == 4.0;
                });
    }

    @Test
    void updateReviewNotFound() {
        //given
        String reviewId = "999";
        when(reviewReactiveRepository.findById(reviewId))
                .thenReturn(Mono.empty());

        //when
        webTestClient
                .put()
                .uri(REVIEW_URI + "/{id}", reviewId)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void deleteReview() {
        //given
        String reviewId = "456";

        when(reviewReactiveRepository.findById(reviewId))
                .thenReturn(Mono.just(new Review(reviewId, "123", "To be deleted", 2.0)));
        when(reviewReactiveRepository.deleteById(reviewId))
                .thenReturn(Mono.empty());

        //when
        webTestClient
                .delete()
                .uri(REVIEW_URI + "/{id}", reviewId)
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    void getReviewsByMovieId() {
        //given
        String movieId = "123";
        var reviewList = List.of(
                new Review("1", movieId, "Great movie!", 5.0),
                new Review("2", movieId, "Must watch!", 4.5)
        );

        when(reviewReactiveRepository.findReviewsByMovieId(movieId))
                .thenReturn(Flux.fromIterable(reviewList));

        //when
        webTestClient
                .get()
                .uri(REVIEW_URI + "?movieId={movieId}", movieId)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Review.class)
                .hasSize(2);
    }
}
