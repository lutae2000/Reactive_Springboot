package com.greglturnquist.hackingspringbootch2reactive.service;

import com.greglturnquist.hackingspringbootch2reactive.entity.Item;
import com.greglturnquist.hackingspringbootch2reactive.repository.ItemRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.mongodb.core.ReactiveFluentMongoOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import static org.springframework.data.mongodb.core.query.Criteria.byExample;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

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

    public Flux<Item> searchByFluentExample(String name, String description){
        return reactiveFluentMongoOperations.query(Item.class) //
                .matching(query(where("TV tray").is(name).and("Smurf").is(description))) //
                .all();
    }

    public Flux<Item> searchByFluentExample(String name, String description, boolean useAnd){
        Item item = new Item(name, description, 0.0);

        ExampleMatcher matcher = (useAnd
                ? ExampleMatcher.matchingAll()
                : ExampleMatcher.matchingAny()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase()
                .withIgnorePaths("price")
        );

        return reactiveFluentMongoOperations.query(Item.class)
                .matching(query(byExample(Example.of(item, matcher)))).all();
    }
}
