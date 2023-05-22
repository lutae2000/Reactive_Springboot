package com.greglturnquist.hackingspringbootch2reactive.config;

import com.greglturnquist.hackingspringbootch2reactive.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    static final String USER = "USER";
    static final String INVENTORY = "INVENTORY";

    static String role(String auth){
        return "ROLE_" + auth;
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService(UserRepository repository){
        return username -> repository.findByName(username)
                .map(user -> User.withDefaultPasswordEncoder()
                        .username(user.getName())
                        .password(user.getPassword())
                        .authorities(user.getRoles().toArray(new String[0]))
                        .build()
                );
    }

    @Bean
    CommandLineRunner userLoader(MongoOperations operations){
        return args -> {
            operations.save(new com.greglturnquist.hackingspringbootch2reactive.entity.User(
                    "greg", "password", Arrays.asList(role(USER))
            ));
            operations.save(new com.greglturnquist.hackingspringbootch2reactive.entity.User(
                    "manager", "password", Arrays.asList(role(USER), role(INVENTORY))
            ));
        };
    }

    @Bean
    SecurityWebFilterChain myCustomSecurityPolicy(ServerHttpSecurity serverHttpSecurity){
        return serverHttpSecurity.authorizeExchange(exchange -> exchange.pathMatchers(HttpMethod.POST, "/").hasRole(INVENTORY)
                                                                        .pathMatchers(HttpMethod.DELETE, "/**").hasRole(INVENTORY)
                                                                        .anyExchange().authenticated()
                                                                        .and()
                                                                        .httpBasic()
                                                                        .and()
                                                                        .formLogin()
                                                    ).csrf().disable().build();
    }
}
