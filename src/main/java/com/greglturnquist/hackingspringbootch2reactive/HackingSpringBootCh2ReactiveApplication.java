package com.greglturnquist.hackingspringbootch2reactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.thymeleaf.TemplateEngine;
import reactor.blockhound.BlockHound;

@SpringBootApplication
public class HackingSpringBootCh2ReactiveApplication {

    public static void main(String[] args) {
        BlockHound.builder()
                        .allowBlockingCallsInside(
                                TemplateEngine.class.getCanonicalName(), "process")
                        .install();

        SpringApplication.run(HackingSpringBootCh2ReactiveApplication.class, args);
    }

}
