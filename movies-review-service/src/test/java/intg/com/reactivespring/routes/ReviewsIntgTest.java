package com.reactivespring.routes;

import com.reactivespring.domain.Review;
import com.reactivespring.repository.ReviewReactiveRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
public class ReviewsIntgTest {

    public static final String REVIEWS_URI = "/v1/reviews";
    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ReviewReactiveRepository reviewReactiveRepository;

    @BeforeEach
    void setUp() {
        var reviewList = List.of(
                new Review(null, "123", "Great movie!",5.0),
                new Review(null, "123", "Must watch!", 4.5),
                new Review("456", "789", "Not bad", 3.0));
        reviewReactiveRepository.saveAll(reviewList).blockLast();
    }

    @AfterEach
    void tearDown() {
        reviewReactiveRepository.deleteAll().block();
    }

    @Test
    void addReview() {
        //given
        var review = new Review(null, "123", "Amazing!", 4.8);

        //when
        webTestClient
                .post()
                .uri(REVIEWS_URI)
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
    void getAllReviews() {
        //when
        webTestClient
                .get()
                .uri(REVIEWS_URI)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .hasSize(3)
                .consumeWith(reviewEntityExchangeResult -> {
                    var reviews = reviewEntityExchangeResult.getResponseBody();
                    assert reviews != null;
                    assert reviews.stream().anyMatch(review -> review.getMovieId().equals("123") && review.getComment().equals("Great movie!"));
                    assert reviews.stream().anyMatch(review -> review.getMovieId().equals("123") && review.getComment().equals("Must watch!"));
                    assert reviews.stream().anyMatch(review -> review.getReviewId().equals("456") && review.getComment().equals("Not bad"));
                });
    }

    @Test
    void updateReview() {
        //given
        var updatedReview = new Review("456", "789", "Updated comment", 4.0);

        //when
        webTestClient
                .put()
                .uri(REVIEWS_URI + "/456")
                .bodyValue(updatedReview)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Review.class)
                .consumeWith(reviewEntityExchangeResult -> {
                    var review = reviewEntityExchangeResult.getResponseBody();
                    assert review != null;
                    assert review.getReviewId().equals("456");
                    assert review.getMovieId().equals("789");
                    assert review.getComment().equals("Updated comment");
                    assert review.getRating() == 4.0;
                });
    }

    @Test
    void deleteReview() {
        //given
        String reviewId = "456";
        //when
        webTestClient
                .delete()
                .uri(REVIEWS_URI + "/{id}", reviewId)
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody()
                .isEmpty();
        //then
        webTestClient
                .get()
                .uri(REVIEWS_URI + "/{id}", reviewId)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void getReviewsByMovieId() {
        //given
        String movieId = "123";
        //when
        webTestClient
                .get()
                .uri(REVIEWS_URI + "?movieId={movieId}", movieId)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .hasSize(2)
                .consumeWith(reviewEntityExchangeResult -> {
                    var reviews = reviewEntityExchangeResult.getResponseBody();
                    assert reviews != null;
                    assert reviews.stream().allMatch(review -> review.getMovieId().equals(movieId));
                });
    }

}
