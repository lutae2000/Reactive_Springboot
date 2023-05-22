package com.greglturnquist.hackingspringbootch2reactive.repository;

import com.greglturnquist.hackingspringbootch2reactive.entity.User;
import org.springframework.data.repository.CrudRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends CrudRepository<User, String> {
    Mono<User> findByName(String name);
}
