package com.greglturnquist.hackingspringbootch2reactive.repository;

import com.greglturnquist.hackingspringbootch2reactive.entity.Item;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;

public interface ItemByExampleRepository extends ReactiveQueryByExampleExecutor<Item> {
}
