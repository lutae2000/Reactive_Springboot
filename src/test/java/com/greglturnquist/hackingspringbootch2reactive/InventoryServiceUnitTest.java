package com.greglturnquist.hackingspringbootch2reactive;

import com.greglturnquist.hackingspringbootch2reactive.entity.Cart;
import com.greglturnquist.hackingspringbootch2reactive.entity.CartItem;
import com.greglturnquist.hackingspringbootch2reactive.entity.Item;
import com.greglturnquist.hackingspringbootch2reactive.repository.CartRepository;
import com.greglturnquist.hackingspringbootch2reactive.repository.ItemRepository;
import com.greglturnquist.hackingspringbootch2reactive.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static reactor.core.publisher.Mono.when;


@ExtendWith(SpringExtension.class)
public class InventoryServiceUnitTest {
    // tag::class-under-test[]
    InventoryService inventoryService; // <1>

    @MockBean private ItemRepository itemRepository; // <2>

    @MockBean private CartRepository cartRepository; // <2>
    // end::class-under-test[]

    // tag::before[]
    @BeforeEach // <1>
    void setUp() {
        // Define test data <2>
        Item sampleItem = new Item("item1", "TV tray", "Alf TV tray", 19.99);
        CartItem sampleCartItem = new CartItem(sampleItem);
        Cart sampleCart = new Cart("My Cart", Collections.singletonList(sampleCartItem));

        // Define mock interactions provided
        // by your collaborators <3>
        when(cartRepository.findById(anyString())).thenReturn(Mono.empty());
        when(itemRepository.findById(anyString())).thenReturn(Mono.just(sampleItem));
        when(cartRepository.save(any(Cart.class))).thenReturn(Mono.just(sampleCart));

        inventoryService = new InventoryService(itemRepository, cartRepository); // <4>
    }
    // end::before[]

    // tag::test[]
    @Test
    @Disabled
    void addItemToEmptyCartShouldProduceOneCartItem() { // <1>
        inventoryService.addItemToCart("My Cart", "item1") // <2>
                .as(StepVerifier::create) // <3>
                .expectNextMatches(cart -> { // <4>
                    assertThat(cart.getCartItems()).extracting(CartItem::getQuantity) //
                            .containsExactlyInAnyOrder(1); // <5>

                    assertThat(cart.getCartItems()).extracting(CartItem::getItem) //
                            .containsExactly(new Item("item1", "TV tray", "Alf TV tray", 19.99)); // <6>

                    return true; // <7>
                }) //
                .verifyComplete(); // <8>
    }
    // end::test[]

    // tag::test2[]
    @Test
    @Disabled
    void alternativeWayToTest() { // <1>
        StepVerifier.create( //
                        inventoryService.addItemToCart("My Cart", "item1")) //
                .expectNextMatches(cart -> { // <4>
                    assertThat(cart.getCartItems()).extracting(CartItem::getQuantity) //
                            .containsExactlyInAnyOrder(1); // <5>

                    assertThat(cart.getCartItems()).extracting(CartItem::getItem) //
                            .containsExactly(new Item("item1", "TV tray", "Alf TV tray", 19.99)); // <6>

                    return true; // <7>
                }) //
                .verifyComplete(); // <8>
    }
    // end::test2[]

}
