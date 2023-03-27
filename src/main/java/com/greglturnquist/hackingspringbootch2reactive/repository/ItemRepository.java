package com.greglturnquist.hackingspringbootch2reactive.repository;

import com.greglturnquist.hackingspringbootch2reactive.entity.Item;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ItemRepository extends ReactiveCrudRepository<Item, String>, ReactiveQueryByExampleExecutor<Item> {

    //name 검색
    Flux<Item> findByNameContaining(String partialName);
    Flux<Item> findByNameContainingAndDescriptionContainingAllIgnoreCase(String partialName, String partialDesc);
    Flux<Item> findByDescriptionContainingIgnoreCase(String partiaName);
    Flux<Item> findByNameContainingOrDescriptionContainingAllIgnoreCase(String partialName, String partialDesc);

}
