package com.greglturnquist.hackingspringbootch2reactive;

import com.greglturnquist.hackingspringbootch2reactive.entity.Cart;
import com.greglturnquist.hackingspringbootch2reactive.entity.CartItem;
import com.greglturnquist.hackingspringbootch2reactive.entity.Item;
import com.greglturnquist.hackingspringbootch2reactive.repository.CartRepository;
import com.greglturnquist.hackingspringbootch2reactive.repository.ItemRepository;
import com.greglturnquist.hackingspringbootch2reactive.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static reactor.core.publisher.Mono.when;

@ExtendWith(SpringExtension.class)
public class InventoryServiceUnitTest {

    InventoryService inventoryService;

    @MockBean
    private ItemRepository itemRepository;

    @MockBean
    private CartRepository cartRepository;

    @BeforeEach
    void setUp(){
        Item sampleItem = new Item("item1", "TV tray", "Alf TV tray", 19.99);
        CartItem sampleCartItem = new CartItem(sampleItem);
        Cart sampleCart = new Cart("My Cart", Collections.singletonList(sampleCartItem));

        when(cartRepository.findById(anyString())).thenReturn(Mono.empty());
        when(cartRepository.findById(anyString())).thenReturn(Mono.just(sampleItem));
        when(cartRepository.findById(anyString())).thenReturn(Mono.just(sampleCart));

        inventoryService = new InventoryService(itemRepository, cartRepository);
    }

    @Test
    void addItemToEmptyCartShouldProduceOneCartItem(){
        inventoryService.addItemToCart("My Cart", "item1")
                .as(StepVerifier::create)
                .expectNextMatches(cart -> {
                    assertThat(cart.getCartItems()).extracting(CartItem::getQuantity)
                            .containsExactlyInAnyOrder(1);

                    assertThat(cart.getCartItems()).extracting(CartItem::getItem)
                            .containsExactlyInAnyOrder(new  Item("item1", "TV tray", "Alf TV tray", 19.99));
                    return true;
                })
                .verifyComplete();
    }
}
