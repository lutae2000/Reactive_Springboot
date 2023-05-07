package com.greglturnquist.hackingspringbootch2reactive;

import com.greglturnquist.hackingspringbootch2reactive.controller.AffordancesItemController;
import com.greglturnquist.hackingspringbootch2reactive.entity.Item;
import com.greglturnquist.hackingspringbootch2reactive.repository.ItemRepository;
import com.greglturnquist.hackingspringbootch2reactive.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

@WebFluxTest(controllers = AffordancesItemController.class)
@AutoConfigureRestDocs
public class AffordancesItemControllerDocumentationTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private InventoryService inventoryService;

    @MockBean
    private ItemRepository itemRepository;

    @Test
    void findSingleItemAffordances(){
        when(itemRepository.findById("item-1")).thenReturn(Mono.just(
                new Item("item-1", "Alf alarm clock", "nothing I really need", 19.99)
        ));

        this.webTestClient.get()
                .uri("/affordances/items/item-1")
                .accept(MediaTypes.HAL_FORMS_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(document("single-item-affordances", preprocessResponse(prettyPrint())));
    }
}
