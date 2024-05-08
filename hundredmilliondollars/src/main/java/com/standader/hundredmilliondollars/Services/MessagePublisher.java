package com.standader.hundredmilliondollars.Services;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
public class MessagePublisher {
    private final Sinks.Many<String> sink;

    public MessagePublisher() {
        this.sink = Sinks.many().multicast().onBackpressureBuffer();
    }

    public void publish(String message) {
        System.out.println("messagePublished!!" + message);
        sink.emitNext(message, Sinks.EmitFailureHandler.FAIL_FAST);
    }

    public Flux<String> getMessages() {
        return sink.asFlux();
    }
}

