package com.greglturnquist.hackingspringbootch2reactive.controller;

import com.greglturnquist.hackingspringbootch2reactive.entity.Cart;
import com.greglturnquist.hackingspringbootch2reactive.entity.CartItem;
import com.greglturnquist.hackingspringbootch2reactive.repository.CartRepository;
import com.greglturnquist.hackingspringbootch2reactive.repository.ItemRepository;
import com.greglturnquist.hackingspringbootch2reactive.service.CartService;
import com.greglturnquist.hackingspringbootch2reactive.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@Controller
public class HomeController {


    private ItemRepository itemRepository;
    private InventoryService inventoryService;

    public HomeController(ItemRepository itemRepository, // <2>
                          InventoryService inventoryService) {
        this.itemRepository = itemRepository;
        this.inventoryService = inventoryService;
    }
    // end::1[]

    // tag::2[]
    @GetMapping
    Mono<Rendering> home() { // <1>
        return Mono.just(Rendering.view("home.html")
                .modelAttribute("items", this.inventoryService.getInventory())
                .modelAttribute("cart", this.inventoryService.getCart("My Cart")
                        .defaultIfEmpty(new Cart("My Cart")))
                .build());
    }

    @PostMapping("/add/{id}") // <1>
    Mono<String> addToCart(@PathVariable String id) { // <2>
        return this.inventoryService.addItemToCart("My Cart", id)
                .thenReturn("redirect:/");
    }

    @DeleteMapping("/delete/{id}")
    public Mono<String> deleteCartItem(@PathVariable String id) {
        return this.inventoryService.removeOneFromCart("My Cart",id)
                .thenReturn("redirect:/");
    }


}
