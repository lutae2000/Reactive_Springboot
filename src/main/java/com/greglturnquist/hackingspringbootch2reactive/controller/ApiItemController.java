package com.greglturnquist.hackingspringbootch2reactive.controller;

import com.greglturnquist.hackingspringbootch2reactive.entity.Item;
import com.greglturnquist.hackingspringbootch2reactive.repository.ItemRepository;
import org.springframework.hateoas.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;



import static com.greglturnquist.hackingspringbootch2reactive.config.SecurityConfig.INVENTORY;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

@RestController
public class ApiItemController {
    private static final SimpleGrantedAuthority ROLE_INVENTORY =  new SimpleGrantedAuthority("ROLE_" + INVENTORY);
    private final ItemRepository itemRepository;

    public ApiItemController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @GetMapping("/api/items")
    Mono<CollectionModel<EntityModel<Item>>> findAll(Authentication auth) {
        ApiItemController controller = methodOn(ApiItemController.class);

        Mono<Link> selfLink = linkTo(controller.findAll(auth)).withSelfRel().toMono();

        Mono<Links> allLinks;

        if(auth.getAuthorities().contains(ROLE_INVENTORY)){
            Mono<Link> addNewLink = linkTo(controller.addNewItem(null, auth)).withRel("add").toMono();
            allLinks = Mono.zip(selfLink, addNewLink)
                    .map(links -> Links.of(links.getT1(), links.getT2()));
        } else {
            allLinks = selfLink.map(link -> Links.of(link));
        }
        return allLinks.flatMap(links -> this.itemRepository.findAll()
                .flatMap(item -> findOne(item.getId(), auth))
                .collectList()
                .map(entityModels -> CollectionModel.of(entityModels, links)));
    }

    @GetMapping("/api/items/{id}")
    public Mono<EntityModel<Item>> findOne(@PathVariable String id, Authentication auth){
        ApiItemController controller = methodOn(ApiItemController.class);

        Mono<Link> selfLink = linkTo(controller.findOne(id, auth)).withSelfRel().toMono();

        Mono<Link> aggregateLink = linkTo(controller.findAll(auth)).withRel(IanaLinkRelations.ITEM).toMono();

        Mono<Links> allLinks;

        if(auth.getAuthorities().contains(ROLE_INVENTORY)){
            Mono<Link> deleteLink = linkTo(controller.deleteItem(id)).withRel("delete").toMono();
            allLinks = Mono.zip(selfLink, aggregateLink, deleteLink)
                            .map(links -> Links.of(links.getT1(), links.getT2(), links.getT3()));
        } else {
            allLinks = Mono.zip(selfLink, aggregateLink)
                    .map(links -> Links.of(links.getT1(), links.getT2()));
        }
        return this.itemRepository.findById(id)
                .zipWith(allLinks)
                .map(o -> EntityModel.of(o.getT1(), o.getT2()));
    }

    @PreAuthorize("hasRole('"+ INVENTORY +"')")
    @PostMapping("/api/items/add")
    public Mono<ResponseEntity<?>> addNewItem(@RequestBody Item item, Authentication auth){
        return itemRepository.save(item)
                .map(Item::getId)
                .flatMap(id -> findOne(id, auth))
                .map(newModel -> ResponseEntity.created(newModel.getRequiredLink(IanaLinkRelations.SELF)
                                                                .toUri()).build());
    }

    @PutMapping("/api/items/{id}")
    public Mono<ResponseEntity<?>> updateItems(
            @RequestBody Mono<EntityModel<Item>> item,
            @PathVariable String id,
            Authentication auth
    ){
        return item.map(EntityModel::getContent)
                .map(content -> new Item(id, content.getName(), content.getDescription(), content.getPrice()))
                .flatMap(this.itemRepository::save)
                .then(findOne(id, auth))
                .map(model -> ResponseEntity.noContent()
                        .location(model.getRequiredLink(IanaLinkRelations.SELF).toUri()).build()
                );
    }

    @PreAuthorize("hasRole('" + INVENTORY + "')")
    @DeleteMapping("/api/items/delete/{id}")
    Mono<ResponseEntity<?>> deleteItem(@PathVariable String id) {
        return this.itemRepository.deleteById(id) //
                .thenReturn(ResponseEntity.noContent().build());
    }
}
