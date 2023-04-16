package com.greglturnquist.hackingspringbootch2reactive;

import com.greglturnquist.hackingspringbootch2reactive.controller.ApiItemController;
import com.greglturnquist.hackingspringbootch2reactive.entity.Item;
import com.greglturnquist.hackingspringbootch2reactive.repository.ItemRepository;
import com.greglturnquist.hackingspringbootch2reactive.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

@AutoConfigureRestDocs
@WebFluxTest(controllers = ApiItemController.class)
public class ApiItemControllerDocumentationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private InventoryService inventoryService;

    @MockBean
    private ItemRepository itemRepository;

    @Test
    void findingAllItems(){
        when(itemRepository.findAll()).thenReturn(
                Flux.just(new Item("item-1", "Alf alram clock", "nothing I really need", 19.99)));
        this.webTestClient.get().uri("/api/items")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(document("findAll", preprocessResponse(prettyPrint())));
    }

    @Test
    void postNewItem(){
        when(itemRepository.save(any())).thenReturn(
                Mono.just(new Item("1", "Alf alarm clock", "nothing important", 19.99, null, null, 0, null, false))
        );

        this.webTestClient.post().uri("/api/items")
                .bodyValue(new Item("Alf alarm clock", "nothing important", 19.99))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .consumeWith(document("post-new-item", preprocessResponse(prettyPrint())));
    }
}
