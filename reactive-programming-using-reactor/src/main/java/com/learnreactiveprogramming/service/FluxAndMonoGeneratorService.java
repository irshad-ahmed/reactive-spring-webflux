package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class FluxAndMonoGeneratorService {
    public Flux<String> namesFlux() {
        return Flux.fromIterable(List.of("alex", "ben", "chloe")).log();
    }

    public Mono<String> nameMono() {
        return Mono.just("alex").log();
    }

    public Mono<String> namesMono_map_filter(int strLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > strLength)
                .map(s -> s.length() + "-" + s)
                .log();
    }

    public Mono<List<String>> namesMono_flatmap_filter(int strLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > strLength)
                .flatMap(this::splitStringMono)
                .log();
    }

    public Flux<String> namesMono_flatMapMany(int strLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > strLength)
                .flatMapMany(this::splitString)
                .log();
    }



    private Mono<List<String>> splitStringMono(String s) {
        return Mono.just(List.of(s.split(""))) ;
    }

    public Flux<String> namesFlux_map(int strLength) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(s-> s.length() > strLength)
                .map(s->s.length()+"-"+s)
                .log();
    }

    public Flux<String> namesFlux_immutibility() {
        var namesFlux = Flux.fromIterable(List.of("alex", "ben", "chloe"));
        namesFlux.map(String::toUpperCase); // This does not change the original Flux
        return namesFlux;
    }

    public Flux<String> namesFlux_flatmap(int strLength) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(s-> s.length() > strLength)
                .flatMap(this::splitString)
                .log();
    }

    public Flux<String> namesFlux_map_async(int strLength) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(s-> s.length() > strLength)
                .flatMap(this::splitStringWithDelay)
                .log();
    }

    public Flux<String> namesFlux_map_concatasync(int strLength) {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(s-> s.length() > strLength)
                .concatMap(this::splitStringWithDelay)
                .log();
    }

    public Flux<String> splitString(String str) {
        return Flux.fromArray(str.split(""));
    }

    public Flux<String> splitStringWithDelay(String str) {
        return Flux.fromArray(str.split("")).delayElements(Duration.ofMillis(500));//new Random().nextInt(1000)));
    }

    public Flux<String> namesFlux_transform(int strLength) {
        Function<Flux<String>,Flux<String>> filterMapFunction = flux -> flux
                .map(String::toUpperCase)
                .filter(s -> s.length() > strLength)
                .map(s -> s.length() + "-" + s);


        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .transform(filterMapFunction)
                .flatMap(this::splitString)
                .defaultIfEmpty("default")
                .log();
    }

    public Flux<String> namesFlux_transform_switchIfEmpty(int strLength) {
        Function<Flux<String>,Flux<String>> filterMapFunction = flux -> flux
                .map(String::toUpperCase)
                .filter(s -> s.length() > strLength)
                .map(s -> s.length() + "-" + s);

        var defaultFlux = Flux.just("default")
                .transform(filterMapFunction);


        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .transform(filterMapFunction)
                .flatMap(this::splitString)
                .switchIfEmpty(defaultFlux)
                .log();
    }

    public Flux<String> exploreConcat(){
        var abcFlux = Flux.just("A","B","C");
        var ddFlux = Flux.just("D","D","F");
        return Flux.concat(abcFlux, ddFlux)
                .log();

    }

    public Flux<String> exploreConcatWith(){
        var abcFlux = Flux.just("A","B","C");
        var ddFlux = Flux.just("D","D","F");
        return abcFlux.concatWith(ddFlux)
                .log();
    }

    public Flux<String> exploreMonoConcatWith(){
        var aMono = Mono.just("A");
        var bMono = Mono.just("D");
        return aMono.concatWith(bMono)
                .log();
    }

    public Flux<String> exploreMerge() {
        var firstFlux = Flux.just("A", "B", "C").delayElements(Duration.ofMillis(100));
        var secondFlux = Flux.just("D", "E", "F").delayElements(Duration.ofMillis(125));
        return Flux.merge(firstFlux, secondFlux)
                .log();
    }

    public Flux<String> exploreMergeWith() {
        var abcFlux = Flux.just("A", "B", "C").delayElements(Duration.ofMillis(100));
        var defFlux = Flux.just("D", "E", "F").delayElements(Duration.ofMillis(125));
        return abcFlux.mergeWith(defFlux)
                .log();
    }

    public Flux<String> exploreMergeWithMono() {
        var aMono = Mono.just("A");
        var bMono = Flux.just("B");
        return aMono.mergeWith(bMono)
                .log();
    }

    public Flux<String> exploreMergeSequential() {
        var firstFlux = Flux.just("A", "B", "C").delayElements(Duration.ofMillis(100));
        var secondFlux = Flux.just("D", "E", "F").delayElements(Duration.ofMillis(125));
        return Flux.mergeSequential(firstFlux, secondFlux)
                .log();
    }

    public Flux<String> exploreZip() {
        var firstFlux = Flux.just("A", "B", "C");
        var secondFlux = Flux.just("D", "E", "F");
        return Flux.zip(firstFlux, secondFlux, (first, second) -> first + second)
                .log();
    }

    public Flux<String> exploreZipWithMultipleFlux() {
        var firstFlux = Flux.just("A", "B", "C");
        var secondFlux = Flux.just("D", "E", "F");
        var thirdFlux = Flux.just("1", "2", "3");
        var forthFlux = Flux.just("4", "5", "6");
        return Flux.zip(firstFlux, secondFlux, thirdFlux,forthFlux)
                .map(tuple -> tuple.getT1() + tuple.getT2() + tuple.getT3() + tuple.getT4())
                .log();
    }

    public Flux<String> exploreZipWith() {
        var firstFlux = Flux.just("A", "B", "C");
        var secondFlux = Flux.just("D", "E", "F");
        return firstFlux.zipWith(secondFlux, (first, second) -> first + second)
                .log();
    }

    public Mono<String> exploreMonoZipWith(){
        var aMono = Mono.just("A");
        var bMono = Mono.just("D");
        return aMono.zipWith(bMono).map(tuple -> tuple.getT1() + tuple.getT2())
                .log();
    }


    public static void main(String[] args) {
        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();
        fluxAndMonoGeneratorService.namesFlux().subscribe(name -> {
            System.out.println("Name is: " + name);
        });
        fluxAndMonoGeneratorService.nameMono().subscribe(name -> {
            System.out.println("Mono Name is: " + name);
        });
    }
}

