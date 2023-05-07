package com.greglturnquist.hackingspringbootch2reactive.controller;

import com.greglturnquist.hackingspringbootch2reactive.entity.Item;
import com.greglturnquist.hackingspringbootch2reactive.repository.ItemRepository;
import org.springframework.hateoas.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;

@RestController
public class AffordancesItemController {

    private final ItemRepository repository;

    public AffordancesItemController(ItemRepository repository) {
        this.repository = repository;
    }
    @PutMapping("/affordances/items/{id}")
    public Mono<ResponseEntity<?>> updateItem(@RequestBody Mono<EntityModel<Item>> item, @PathVariable String id){
        return item.map(EntityModel::getContent)
                .map(content -> new Item(id, content.getName(), content.getDescription(), content.getPrice()))
                .flatMap(this.repository::save)
                .then(findOne(id))
                .map(model -> ResponseEntity.noContent()
                        .location(model.getRequiredLink(IanaLinkRelations.SELF).toUri()).build());
    }

    @GetMapping("/affordances/items/{id}")
    public Mono<EntityModel<Item>> findOne(@PathVariable String id){
        AffordancesItemController controller = methodOn(AffordancesItemController.class);

        Mono<Link> selfLink = linkTo(controller.findOne(id)) //
                .withSelfRel() //
                .andAffordance(controller.updateItem(null, id)) // <3>
                .toMono();

        Mono<Link> aggregateLink = linkTo(controller.findAll()) //
                .withRel(IanaLinkRelations.ITEM) //
                .toMono();

        return Mono.zip(repository.findById(id), selfLink, aggregateLink)
                .map(o -> EntityModel.of(o.getT1(), Links.of(o.getT2(), o.getT3())));
    }

    @GetMapping("/affordances/items")
    public Mono<CollectionModel<EntityModel<Item>>> findAll(){
        AffordancesItemController controller = methodOn(AffordancesItemController.class);

        Mono<Link> aggregateRoot = linkTo(controller.findAll())
                .withSelfRel()
                .andAffordance(controller.addNewItem(null))
                .toMono();

        return this.repository.findAll()
                .flatMap(item -> findOne(item.getId()))
                .collectList()
                .flatMap(models -> aggregateRoot.map(selfLink -> CollectionModel.of(models, selfLink)));
    }

    @PostMapping("/affordances/items")
    public Mono<ResponseEntity<?>> addNewItem(@RequestBody Mono<EntityModel<Item>> item){
        return item.map(EntityModel::getContent)
                .flatMap(this.repository::save)
                .map(Item::getId)
                .flatMap(this::findOne)
                .map(newModel -> ResponseEntity.created(newModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                        .body(newModel.getContent()));
    }
}
