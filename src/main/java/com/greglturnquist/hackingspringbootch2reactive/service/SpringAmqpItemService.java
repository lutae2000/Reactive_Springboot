package com.greglturnquist.hackingspringbootch2reactive.service;

import com.greglturnquist.hackingspringbootch2reactive.entity.Item;
import com.greglturnquist.hackingspringbootch2reactive.repository.ItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class SpringAmqpItemService {
    private final ItemRepository itemRepository;

    public SpringAmqpItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @RabbitListener(
            ackMode = "MANUAL",
            bindings = @QueueBinding(
                    value = @Queue,
                    exchange = @Exchange("hacking-spring-boot"),
                    key = "new-items-spring-amqp"))
    public Mono<Void> processNewItemsViaSpringAmqp(Item item){
        log.debug("consuming =>" + item);
        return this.itemRepository.save(item).then();
    }

}
