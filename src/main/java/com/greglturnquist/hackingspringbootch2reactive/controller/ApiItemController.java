package com.greglturnquist.hackingspringbootch2reactive.controller;

import com.greglturnquist.hackingspringbootch2reactive.entity.Item;
import com.greglturnquist.hackingspringbootch2reactive.repository.ItemRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
public class ApiItemController {
    private final ItemRepository itemRepository;

    public ApiItemController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @GetMapping("/api/items")
    public Flux<Item> findAll(){
        return this.itemRepository.findAll();
    }

    @GetMapping("/api/items/{id}")
    public Mono<Item> findOne(@PathVariable String id){
        return this.itemRepository.findById(id);
    }

    @PostMapping("/api/items")
    public Mono<ResponseEntity<?>> addNewItem(@RequestBody Mono<Item> item){
        return item.flatMap(this.itemRepository::save)
                .map(saveItem -> ResponseEntity.created(URI.create("/api/items/" + saveItem.getId()))
                        .body(saveItem));
    }

    @PutMapping("/api/items/{id}")
    public Mono<ResponseEntity<?>> updateItems(
            @RequestBody Mono<Item> item,
            @PathVariable String id
    ){
        return item.map(content -> new Item(id, content.getName(), content.getDescription(), content.getPrice()))
                .flatMap(this.itemRepository::save)
                .map(ResponseEntity::ok);
    }

}
