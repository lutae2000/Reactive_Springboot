package com.greglturnquist.hackingspringbootch2reactive.service;

import com.greglturnquist.hackingspringbootch2reactive.entity.Item;
import com.greglturnquist.hackingspringbootch2reactive.repository.ItemRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.mongodb.core.ReactiveFluentMongoOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class InventoryService {

    private ItemRepository repository;
    private ReactiveFluentMongoOperations reactiveFluentMongoOperations;

    public InventoryService(ItemRepository repository, ReactiveFluentMongoOperations reactiveFluentMongoOperations) {
        this.repository = repository;
        this.reactiveFluentMongoOperations = reactiveFluentMongoOperations;
    }



    public Flux<Item> searchByExample(String name, String description, boolean useAnd) {
        Item item = new Item(name, description, 0.0);

        ExampleMatcher matcher = (useAnd ? ExampleMatcher.matchingAll() : ExampleMatcher.matchingAny())
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase()
                .withIgnorePaths("price");

        Example<Item> probe = Example.of(item, matcher);
        return repository.findAll(probe);
    }
}
