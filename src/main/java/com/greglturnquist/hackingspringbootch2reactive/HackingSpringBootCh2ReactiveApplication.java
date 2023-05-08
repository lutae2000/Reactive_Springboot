package com.greglturnquist.hackingspringbootch2reactive;

import com.greglturnquist.hackingspringbootch2reactive.entity.HttpTraceWrapper;
import com.greglturnquist.hackingspringbootch2reactive.repository.HttpTraceWrapperRepository;
import org.bson.Document;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.convert.NoOpDbRefResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.thymeleaf.TemplateEngine;
import reactor.blockhound.BlockHound;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

@SpringBootApplication
public class HackingSpringBootCh2ReactiveApplication {

    HttpTraceRepository traceRepository(){
        return new InMemoryHttpTraceRepository();
    }


    public static void main(String[] args) {
        BlockHound.builder()
                        .allowBlockingCallsInside(
                                TemplateEngine.class.getCanonicalName(), "process")
                        .install();

        SpringApplication.run(HackingSpringBootCh2ReactiveApplication.class, args);
    }

    /**
     * 몽고DB Document 를 HttpTraceWrapper 로 변환하는 컨버터
     */

    static Converter<Document, HttpTraceWrapper> CONVERTER = new Converter<Document, HttpTraceWrapper>() { //
        @Override
        public HttpTraceWrapper convert(Document document){
            Document httpTrace = document.get("httpTrace", Document.class);
            Document request = httpTrace.get("request", Document.class);
            Document response = httpTrace.get("response", Document.class);

            return new HttpTraceWrapper(new HttpTrace(
                    new HttpTrace.Request(
                            request.getString("method"),
                            URI.create(request.getString("uri")),
                            request.get("headers", Map.class),
                            null
                    ),
                    new HttpTrace.Response(
                            response.getInteger("status"),
                            response.get("headers", Map.class)
                    ), httpTrace.getDate("timestamp").toInstant(),
                    null,null,httpTrace.getLong("timeToken")
            ));
        }
    };

    @Bean
    Jackson2JsonMessageConverter jackson2JsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }

/*    @Bean
    public MappingMongoConverter mappingMongoConverter(MongoMappingContext context){
        MappingMongoConverter mappingMongoConverter = new MappingMongoConverter(NoOpDbRefResolver.INSTANCE, context);
        mappingMongoConverter.setCustomConversions(new MongoCustomConversions(Collections.singletonList(CONVERTER)));
        return mappingMongoConverter;
    }*/
}
