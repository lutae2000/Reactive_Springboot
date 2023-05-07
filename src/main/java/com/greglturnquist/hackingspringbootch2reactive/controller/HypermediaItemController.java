package com.greglturnquist.hackingspringbootch2reactive.controller;

import com.greglturnquist.hackingspringbootch2reactive.entity.Item;
import com.greglturnquist.hackingspringbootch2reactive.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.*;

@RestController
public class HypermediaItemController {

    private final ItemRepository itemRepository;

    public HypermediaItemController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @GetMapping("/hypermedia/items/{id}")
    public Mono<EntityModel<Item>> findOne(@PathVariable String id) {
        HypermediaItemController controller = methodOn(HypermediaItemController.class); // <1>

        Mono<Link> selfLink = linkTo(controller.findOne(id)).withSelfRel().toMono(); // <2>

        Mono<Link> aggregateLink = linkTo(controller.findAll()) //
                .withRel(IanaLinkRelations.ITEM).toMono(); // <3>

        return Mono.zip(itemRepository.findById(id), selfLink, aggregateLink) // <4>
                .map(o -> EntityModel.of(o.getT1(), Links.of(o.getT2(), o.getT3()))); // <5>
    }

    @GetMapping("/hypermedia/items")
    Mono<CollectionModel<EntityModel<Item>>> findAll() {
        return this.itemRepository.findAll()
                .flatMap(item -> findOne(item.getId()))
                .collectList()
                .flatMap(entityModels -> linkTo(methodOn(HypermediaItemController.class)
                        .findAll()).withSelfRel()
                        .toMono()
                        .map(selfLink -> CollectionModel.of(entityModels, selfLink)));
    }
}
