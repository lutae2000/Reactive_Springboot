package com.greglturnquist.hackingspringbootch2reactive.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class User {
    private @Id String id;
    private String name;
    private String password;
    private List<String> roles;

    public User(String name, String password, List<String> roles){
        this.name = name;
        this.password = password;
        this.roles = roles;
    }
}
