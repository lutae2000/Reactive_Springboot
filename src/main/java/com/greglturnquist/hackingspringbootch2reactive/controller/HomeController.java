package com.greglturnquist.hackingspringbootch2reactive.controller;

import com.greglturnquist.hackingspringbootch2reactive.entity.Cart;
import com.greglturnquist.hackingspringbootch2reactive.entity.CartItem;
import com.greglturnquist.hackingspringbootch2reactive.repository.CartRepository;
import com.greglturnquist.hackingspringbootch2reactive.repository.ItemRepository;
import com.greglturnquist.hackingspringbootch2reactive.service.CartService;
import com.greglturnquist.hackingspringbootch2reactive.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@Controller
public class HomeController {


    private ItemRepository itemRepository;
    private InventoryService inventoryService;

    public HomeController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }
    // end::1[]

    // tag::2[]
    @GetMapping
    Mono<Rendering> home(@RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient oAuth2AuthorizedClient,
                         @AuthenticationPrincipal OAuth2User oAuth2User) { // <1>
/*        return Mono.just(Rendering.view("home.html")
                .modelAttribute("items", this.inventoryService.getInventory())
                .modelAttribute("cart", this.inventoryService.getCart(cartName(auth))
                        .defaultIfEmpty(new Cart(cartName(auth))))
                .modelAttribute("auth", auth)
                .build());*/

        //OAuth로 변경
        return Mono.just(Rendering.view("home.html")
                .modelAttribute("items", this.inventoryService.getInventory())
                .modelAttribute("cart", this.inventoryService.getCart(cartName(oAuth2User))
                        .defaultIfEmpty(new Cart(cartName(oAuth2User))))
                .modelAttribute("userName", oAuth2User.getName())
                .modelAttribute("authorities", oAuth2User.getAuthorities())
                .modelAttribute("clientName", oAuth2AuthorizedClient.getClientRegistration().getClientName())
                .modelAttribute("userAttributes", oAuth2User.getAttributes())
                .build()
        );
    }

    @PostMapping("/add/{id}") // <1>
    Mono<String> addToCart(Authentication auth, OAuth2User oAuth2User, @PathVariable String id) { // <2>
        return this.inventoryService.addItemToCart(cartName(oAuth2User), id)
                .thenReturn("redirect:/");
    }

    @DeleteMapping("/delete/{id}")
    public Mono<String> deleteCartItem(Authentication auth, OAuth2User oAuth2User, @PathVariable String id) {
        return this.inventoryService.removeOneFromCart(cartName(oAuth2User),id)
                .thenReturn("redirect:/");
    }

    private static String cartName(OAuth2User oAuth2User){
        //return auth.getName() + "'s Cart";
        return oAuth2User.getName() + "'s Cart";
    }

}
