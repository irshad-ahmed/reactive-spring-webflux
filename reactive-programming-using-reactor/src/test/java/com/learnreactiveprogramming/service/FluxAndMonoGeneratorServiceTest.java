package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FluxAndMonoGeneratorServiceTest {
    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    @Test
    void namesFlux() {
        // given
        // when
        var nameFlux = fluxAndMonoGeneratorService.namesFlux();
        // then
        StepVerifier.create(nameFlux).expectNext("alex", "ben", "chloe")
                .expectNextCount(0)
                .verifyComplete();

        StepVerifier.create(nameFlux).expectNextCount(3).verifyComplete();
    }

    @Test
    void namesFlux_map() {
        //given
        int strLength = 3;
        //when
        var nameFlux = fluxAndMonoGeneratorService.namesFlux_map(strLength);
        //then
        StepVerifier.create(nameFlux)
                .expectNext("4-ALEX", "5-CHLOE")
                .verifyComplete();
    }

    @Test
    void namesFlux_immutibility() {
        //given

        //when
        var nameFlux = fluxAndMonoGeneratorService.namesFlux_immutibility();
        //then
        StepVerifier.create(nameFlux)
                .expectNext("alex", "ben", "chloe")
                .verifyComplete();
    }

    @Test
    void namesFlux_flatmap() {
        //given
        int strLength = 3;
        //when
        var nameFlux = fluxAndMonoGeneratorService.namesFlux_flatmap(strLength);
        //then
        StepVerifier.create(nameFlux)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    void namesFlux_map_async() {
        //given
        int strLength = 3;
        //when
        var nameFlux = fluxAndMonoGeneratorService.namesFlux_map_async(strLength);
        //then
        StepVerifier.create(nameFlux)
                .expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void namesFlux_concatmap_async() {
        //given
        int strLength = 3;
        //when
        var nameFlux = fluxAndMonoGeneratorService.namesFlux_map_concatasync(strLength);
        //then
        StepVerifier.create(nameFlux)
                .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    void namesMono_flatmap_filter() {
        //given
        int strLength = 3;
        //when
        var nameMono = fluxAndMonoGeneratorService.namesMono_flatmap_filter(strLength);
        //then
        StepVerifier.create(nameMono)
                .expectNext(List.of("A", "L", "E", "X"))
                .verifyComplete();
    }

    @Test
    void namesMono_flatMapMany() {
        //given
        int strLength = 3;
        //when
        var nameFlux = fluxAndMonoGeneratorService.namesMono_flatMapMany(strLength);
        //then
        StepVerifier.create(nameFlux)
                .expectNext("A", "L", "E", "X")
                .verifyComplete();
    }

    @Test
    void namesFlux_transform() {
        //given
        int strLength = 3;
        //when
        var nameFlux = fluxAndMonoGeneratorService.namesFlux_transform(strLength);
        //then
        StepVerifier.create(nameFlux)
                .expectNext("4", "-", "A", "L", "E", "X", "5", "-", "C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    void namesFlux_transform_with6Elements() {
        //given
        int strLength = 6;
        //when
        var nameFlux = fluxAndMonoGeneratorService.namesFlux_transform(strLength);
        //then
        StepVerifier.create(nameFlux)
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    void namesFlux_transform_switchIfEmpty() {
        //given
        int strLength = 3;
        //when
        var nameFlux = fluxAndMonoGeneratorService.namesFlux_transform_switchIfEmpty(strLength);
        //then
        StepVerifier.create(nameFlux)
                .expectNext("4", "-", "A", "L", "E", "X", "5", "-", "C", "H", "L", "O", "E")
                .verifyComplete();
    }

    @Test
    void exploreConcat() {
        //given
        //when
        var nameFlux = fluxAndMonoGeneratorService.exploreConcat();
        //then
        StepVerifier.create(nameFlux)
                .expectNext("A", "B", "C", "D", "D", "F")
                .verifyComplete();
    }

    @Test
    void exploreConcatWith() {
        //given
        //when
        var nameFlux = fluxAndMonoGeneratorService.exploreConcatWith();
        //then
        StepVerifier.create(nameFlux)
                .expectNext("A", "B", "C", "D", "D", "F")
                .verifyComplete();
    }

    @Test
    void exploreMonoConcatWith() {
        //when
        var nameFlux = fluxAndMonoGeneratorService.exploreMonoConcatWith();
        //then
        StepVerifier.create(nameFlux)
                .expectNext("A", "D")
                .verifyComplete();
    }

    @Test
    void exploreMerge() {
        //given
        //when
        var nameFlux = fluxAndMonoGeneratorService.exploreMerge();
        //then
        StepVerifier.create(nameFlux)
                .expectNext("A", "D", "B", "E", "C", "F")
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void exploreMergeWith() {
        //given
        //when
        var nameFlux = fluxAndMonoGeneratorService.exploreMergeWith();
        //then
        StepVerifier.create(nameFlux)
                .expectNext("A", "D", "B", "E", "C", "F")
                .verifyComplete();
    }

    @Test
    void exploreMergeWithMono() {
        //given
        //when
        var nameFlux = fluxAndMonoGeneratorService.exploreMergeWithMono();
        //then
        StepVerifier.create(nameFlux)
                .expectNext("A", "B")
                .verifyComplete();

    }

    @Test
    void exploreMergeSequential() {
        //given
        //when
        var nameFlux = fluxAndMonoGeneratorService.exploreMergeSequential();
        //then
        StepVerifier.create(nameFlux)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void exploreZip() {
        //given
        //when
        var nameFlux = fluxAndMonoGeneratorService.exploreZip();
        //then
        StepVerifier.create(nameFlux)
                .expectNext("AD", "BE", "CF")
                .verifyComplete();
    }

    @Test
    void exploreZipWithMultipleFlux() {
        //given
        //when
        var nameFlux = fluxAndMonoGeneratorService.exploreZipWithMultipleFlux();
        //then
        StepVerifier.create(nameFlux)
                .expectNext("AD14", "BE25", "CF36")
                .verifyComplete();
    }

    @Test
    void exploreZipWith() {
        //given
        //when
        var nameFlux = fluxAndMonoGeneratorService.exploreZipWith();
        //then
        StepVerifier.create(nameFlux)
                .expectNext("AD", "BE", "CF")
                .verifyComplete();
    }

    @Test
    void exploreMonoZipWith() {
        //given
        //when
        var nameMono = fluxAndMonoGeneratorService.exploreMonoZipWith();
        //then
        StepVerifier.create(nameMono)
                .expectNext("AD")
                .verifyComplete();
    }

}