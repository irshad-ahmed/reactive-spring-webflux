package com.reactivespring.util;

import com.reactivespring.exception.MoviesInfoServerException;
import com.reactivespring.exception.ReviewsServerException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Exceptions;
import reactor.util.retry.Retry;

import java.time.Duration;

@Slf4j
public class RetryUtil {
    public static Retry retrySpec(){
        return Retry.fixedDelay(3, Duration.ofSeconds(1))
                .filter(ex -> {
                    return ex instanceof MoviesInfoServerException || ex instanceof ReviewsServerException;
                })
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                    return Exceptions.propagate(retrySignal.failure());
                });
    }
}
