package com.greglturnquist.hackingspringbootch2reactive.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.data.annotation.Id;
import org.springframework.data.geo.Point;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Description(value = "Item")
public class Item {

    private @Id String id;
    private String name;
    private String description;
    private double price;
    private String distributorRegion;
    private Date releaseDate;
    private int availableUnits;
    private Point location;
    private boolean active;

    public Item(String name, String description, double price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public Item(String id, String name, String description, double price) {
        this(name, description, price);
        this.id = id;
    }

}
