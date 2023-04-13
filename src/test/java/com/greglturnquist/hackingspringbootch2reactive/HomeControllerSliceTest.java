package com.greglturnquist.hackingspringbootch2reactive;

import com.greglturnquist.hackingspringbootch2reactive.controller.HomeController;
import com.greglturnquist.hackingspringbootch2reactive.entity.Cart;
import com.greglturnquist.hackingspringbootch2reactive.entity.Item;
import com.greglturnquist.hackingspringbootch2reactive.service.InventoryService;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(HomeController.class) // <1>
public class HomeControllerSliceTest {

    @Autowired // <2>
    private WebTestClient client;

    @MockBean // <3>
    InventoryService inventoryService;

    @Test
    void homePage() {
        when(inventoryService.getInventory()).thenReturn(Flux.just( //
                new Item("id1", "name1", "desc1", 1.99), //
                new Item("id2", "name2", "desc2", 9.99) //
        ));
        when(inventoryService.getCart("My Cart")) //
                .thenReturn(Mono.just(new Cart("My Cart")));

        client.get().uri("/").exchange() //
                .expectStatus().isOk() //
                .expectBody(String.class) //
                .consumeWith(exchangeResult -> {
                    assertThat( //
                            exchangeResult.getResponseBody()).contains("action=\"/add/id1\"");
                    assertThat( //
                            exchangeResult.getResponseBody()).contains("action=\"/add/id2\"");
                });
    }
}