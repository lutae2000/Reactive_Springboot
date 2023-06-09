package com.greglturnquist.hackingspringbootch2reactive.config;

import com.greglturnquist.hackingspringbootch2reactive.entity.Item;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

@Component
public class TemplateDatabaseLoader {

    @Bean
    CommandLineRunner initialize(MongoOperations mongo) {
        return args -> {
            mongo.save(new Item("Alf alarm clock", "kid clock", 19.99));
            mongo.save(new Item("Smurf TV tray", "kid TV", 24.99));
        };
    }

}
