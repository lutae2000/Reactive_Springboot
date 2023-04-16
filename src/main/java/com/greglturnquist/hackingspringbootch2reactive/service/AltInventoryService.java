package com.greglturnquist.hackingspringbootch2reactive.service;

import com.greglturnquist.hackingspringbootch2reactive.entity.Cart;
import com.greglturnquist.hackingspringbootch2reactive.entity.CartItem;
import com.greglturnquist.hackingspringbootch2reactive.entity.Item;
import com.greglturnquist.hackingspringbootch2reactive.repository.CartRepository;
import com.greglturnquist.hackingspringbootch2reactive.repository.ItemRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AltInventoryService {
    private ItemRepository itemRepository;
    private CartRepository cartRepository;

    public AltInventoryService(ItemRepository itemRepository, CartRepository cartRepository) {
        this.itemRepository = itemRepository;
        this.cartRepository = cartRepository;
    }

    public Mono<Cart> getCart(String cartId) {
        return this.cartRepository.findById(cartId);
    }

    public Flux<Item> getInventory() {
        return this.itemRepository.findAll();
    }

    Mono<Item> saveItem(Item newItem) {
        return this.itemRepository.save(newItem);
    }

    Mono<Void> deleteItem(String id) {
        return this.itemRepository.deleteById(id);
    }

    public Mono<Cart> addItemToCart(String cartId, String itemId){
        Cart myCart = this.cartRepository.findById(cartId)
                .defaultIfEmpty(new Cart(cartId))
                .block();

        return myCart.getCartItems().stream()
                .filter(cartItem -> cartItem.getItem().getId().equals(itemId))
                .findAny()
                .map(cartItem -> {
                    cartItem.increment();
                    return Mono.just(myCart);
                })
                .orElseGet(() -> this.itemRepository.findById(itemId)
                        .map(item -> new CartItem(item))
                        .map(cartItem -> {
                            myCart.getCartItems().add(cartItem);
                            return myCart;
                        })
                )
                .flatMap(cart -> this.cartRepository.save(cart));
    }
}
