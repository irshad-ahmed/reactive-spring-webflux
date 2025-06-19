package com.reactivespring.handler;

import com.reactivespring.domain.Review;
import com.reactivespring.repository.ReviewReactiveRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class ReviewHandler {


    private ReviewReactiveRepository reviewReactiveRepository;

    public ReviewHandler(ReviewReactiveRepository reviewReactiveRepository) {
        this.reviewReactiveRepository = reviewReactiveRepository;
    }

    public Mono<ServerResponse> addReview(ServerRequest request) {
        return request.bodyToMono(Review.class)
                .flatMap(reviewReactiveRepository::save)
                .flatMap(savedReview -> ServerResponse.status(HttpStatus.CREATED).bodyValue(savedReview));
    }

    public Mono<ServerResponse> getAllReviews(ServerRequest request) {
        Optional<String> movieId = request.queryParam("movieId");
        if(movieId.isPresent()){
            var reviewsFlux = reviewReactiveRepository.findReviewsByMovieId(movieId.get());
            return ServerResponse
                    .ok()
                    .body(reviewsFlux, Review.class)
                    .switchIfEmpty(ServerResponse.notFound().build());
        }else{
            return ServerResponse
                    .ok()
                    .body(reviewReactiveRepository.findAll(), Review.class)
                    .switchIfEmpty(ServerResponse.notFound().build());
        }

    }

    public Mono<ServerResponse> updateReview(ServerRequest request) {
        var id = request.pathVariable("id");
        var existingReview = reviewReactiveRepository.findById(id);
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
}
