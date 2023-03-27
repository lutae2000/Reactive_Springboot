package com.greglturnquist.hackingspringbootch2reactive.controller;

import com.greglturnquist.hackingspringbootch2reactive.entity.Cart;
import com.greglturnquist.hackingspringbootch2reactive.entity.CartItem;
import com.greglturnquist.hackingspringbootch2reactive.repository.CartRepository;
import com.greglturnquist.hackingspringbootch2reactive.repository.ItemRepository;
import com.greglturnquist.hackingspringbootch2reactive.service.CartService;
import com.greglturnquist.hackingspringbootch2reactive.service.InventoryService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@Controller
public class HomeController {
    private ItemRepository itemRepository;
    private CartRepository cartRepository;
    private CartService cartService;
    private InventoryService inventoryService;

    public HomeController(ItemRepository itemRepository, // <2>
                          CartRepository cartRepository) {
        this.itemRepository = itemRepository;
        this.cartRepository = cartRepository;
    }
    // end::1[]

    // tag::2[]
    @GetMapping
    Mono<Rendering> home() { // <1>
        return Mono.just(Rendering.view("home.html") // <2>
                .modelAttribute("items", //
                        this.itemRepository.findAll()) // <3>
                .modelAttribute("cart", //
                        this.cartRepository.findById("My Cart") // <4>
                                .defaultIfEmpty(new Cart("My Cart")))
                .build());
    }

    @PostMapping("/add/{id}") // <1>
    Mono<String> addToCart(@PathVariable String id) { // <2>
        return cartRepository.findById("My Cart")
                .defaultIfEmpty(new Cart("My Cart"))
                .flatMap(cart -> cart.getCartItems().stream()
                                    .filter(cartItem -> cartItem.getItem()
                                            .getId().equals(id))
                        .findAny()
                        .map(cartItem -> {cartItem.increment();
                            return Mono.just(cart);
                        }).orElseGet(() -> {
                           return this.itemRepository.findById(id)
                                   .map(item -> new CartItem(item))
                                   .map(cartItem -> {
                                       cart.getCartItems().add(cartItem);
                                       return cart;
                                   });
                        }))
                .flatMap(cart -> this.cartRepository.save(cart))
                .thenReturn("redirect:/");
    }

    @DeleteMapping("/delete/{id}")
    public Mono<String> deleteCartItem(@PathVariable String id) {
        return itemRepository.deleteById(id)
                .thenReturn("redirect:/");
    }

    @GetMapping("/search")
    public Mono<Rendering> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam boolean useAnd
            ) {
        return Mono.just(Rendering.view("home.html")
                .modelAttribute("items", inventoryService.searchByExample(name, description, useAnd))
                .modelAttribute("cart", cartRepository.findById("My Cart")
                        .defaultIfEmpty(new Cart("My Cart")))
                .build());

    }
}
