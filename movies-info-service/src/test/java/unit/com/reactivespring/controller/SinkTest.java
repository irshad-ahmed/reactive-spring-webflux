package com.reactivespring.controller;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

public class SinkTest {

    @Test
    void testSink() {
        Sinks.Many<Integer> replaySink = Sinks.many().replay().all();
        replaySink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        replaySink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        Flux<Integer> integerFlux = replaySink.asFlux();
        integerFlux.subscribe((i)->{
            System.out.println("Subscriber 1 : " + i);
        });

        Flux<Integer> integerFlux2 = replaySink.asFlux();
        integerFlux2.subscribe((i)->{
            System.out.println("Subscriber 2 : " + i);
        });

        replaySink.tryEmitNext(3);

        Flux<Integer> integerFlux3 = replaySink.asFlux();
        integerFlux3.subscribe((i)->{
            System.out.println("Subscriber 3 : " + i);
        });

    }

    @Test
    void testMultiCastSink(){
        Sinks.Many<Integer> multicastSink = Sinks.many().multicast().onBackpressureBuffer();
        multicastSink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        multicastSink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        Flux<Integer> integerFlux = multicastSink.asFlux();
        integerFlux.subscribe((i)->{
            System.out.println("Subscriber 1 : " + i);
        });

        Flux<Integer> integerFlux2 = multicastSink.asFlux();
        integerFlux2.subscribe((i)->{
            System.out.println("Subscriber 2 : " + i);
        });

        multicastSink.tryEmitNext(3);

        Flux<Integer> integerFlux3 = multicastSink.asFlux();
        integerFlux3.subscribe((i)->{
            System.out.println("Subscriber 3 : " + i);
        });
    }

    @Test
    void testUnicastSink() {
        Sinks.Many<Integer> unicastSink = Sinks.many().unicast().onBackpressureBuffer();

        unicastSink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        unicastSink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        Flux<Integer> integerFlux = unicastSink.asFlux();
        integerFlux.subscribe((i)->{
            System.out.println("Subscriber 1 : " + i);
        });

        Flux<Integer> integerFlux2 = unicastSink.asFlux();
        integerFlux2.subscribe((i)->{
            System.out.println("Subscriber 2 : " + i);
        });

        unicastSink.tryEmitNext(3);

        Flux<Integer> integerFlux3 = unicastSink.asFlux();
        integerFlux3.subscribe((i)->{
            System.out.println("Subscriber 3 : " + i);
        });
    }

}
