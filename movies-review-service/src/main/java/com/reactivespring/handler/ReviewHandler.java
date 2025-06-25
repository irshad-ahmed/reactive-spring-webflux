package com.reactivespring.handler;

import com.reactivespring.domain.Review;
import com.reactivespring.exception.ReviewDataException;
import com.reactivespring.exception.ReviewNotFoundException;
import com.reactivespring.repository.ReviewReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ReviewHandler {

    private ReviewReactiveRepository reviewReactiveRepository;
    private Validator validator;
    private Sinks.Many<Review> reviewSink = Sinks.many().replay().latest();


    public ReviewHandler(ReviewReactiveRepository reviewReactiveRepository, Validator validator) {
        this.reviewReactiveRepository = reviewReactiveRepository;
        this.validator = validator;
    }


    public Mono<ServerResponse> addReview(ServerRequest request) {
        return request.bodyToMono(Review.class)
                .doOnNext(this::validate)
                .flatMap(reviewReactiveRepository::save)
                .doOnNext(review -> {
                    reviewSink.tryEmitNext(review);
                })
                .flatMap(savedReview -> ServerResponse.status(HttpStatus.CREATED).bodyValue(savedReview));
    }

    private void validate(Review review) {
        var constraintValidations = validator.validate(review);
        log.info("Constraint Validations: {}", constraintValidations);
        if (constraintValidations.size() > 0) {
            var errorMessage = constraintValidations.stream().map(ConstraintViolation::getMessage).sorted().collect(Collectors.joining(", "));
            throw new ReviewDataException(errorMessage);
        }
    }

    public Mono<ServerResponse> getAllReviews(ServerRequest request) {
        Optional<String> movieId = request.queryParam("movieId");
        if (movieId.isPresent()) {
            var reviewsFlux = reviewReactiveRepository.findReviewsByMovieId(movieId.get());
            return ServerResponse
                    .ok()
                    .body(reviewsFlux, Review.class)
                    .switchIfEmpty(ServerResponse.notFound().build());
        } else {
            return ServerResponse
                    .ok()
                    .body(reviewReactiveRepository.findAll(), Review.class)
                    .switchIfEmpty(ServerResponse.notFound().build());
        }

    }

    public Mono<ServerResponse> updateReview(ServerRequest request) {
        var id = request.pathVariable("id");
        var existingReview = reviewReactiveRepository.findById(id);
                //.switchIfEmpty(Mono.error(new ReviewNotFoundException("Review not found with id: " + id)));
        return existingReview
                .flatMap(review -> request.bodyToMono(Review.class)
                        .map(newReview -> {
                            review.setComment(newReview.getComment());
                            review.setRating(newReview.getRating());
                            return review;
                        }))
                .flatMap(reviewReactiveRepository::save)
                .flatMap(updatedReview -> ServerResponse.ok().bodyValue(updatedReview))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> deleteReview(ServerRequest request) {
        var id = request.pathVariable("id");
        var existingReview = reviewReactiveRepository.findById(id);

        return existingReview
                .flatMap(review -> reviewReactiveRepository.deleteById(id)
                        .then(ServerResponse.noContent().build()))
                .switchIfEmpty(ServerResponse.notFound().build());

    }

    public Mono<ServerResponse> getReviewsStream(ServerRequest request) {
        return ServerResponse.ok().contentType(MediaType.APPLICATION_NDJSON)
                .body(reviewSink.asFlux(),Review.class)
                .log();
    }
}
