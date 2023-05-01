package com.greglturnquist.hackingspringbootch2reactive.repository;

import com.greglturnquist.hackingspringbootch2reactive.entity.HttpTraceWrapper;
import org.springframework.data.repository.Repository;

import java.util.stream.Stream;

public interface HttpTraceWrapperRepository extends Repository<HttpTraceWrapper, String> {
    Stream<HttpTraceWrapper> findAll();
    void save(HttpTraceWrapper trace);
}
