package com.greglturnquist.hackingspringbootch2reactive.controller;

import com.greglturnquist.hackingspringbootch2reactive.entity.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URI;

@RestController
@Slf4j
public class SpringAmqpItemController {
    private final AmqpTemplate template;

    public SpringAmqpItemController(AmqpTemplate template) {
        this.template = template;
    }

    @PostMapping("/items")
    public Mono<ResponseEntity<?>> addNewItemUsingSpringAmqp(@RequestBody Mono<Item> item){
        return item.subscribeOn(Schedulers.boundedElastic())
                .flatMap(content -> {
                    return Mono.fromCallable(() -> {
                        this.template.convertAndSend(
                                "hacking-spring-boot", "new-items-spring-amqp", content);
                        return ResponseEntity.created(URI.create("/items")).build();
                    });
                });
    }
}
