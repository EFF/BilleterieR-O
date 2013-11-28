package ca.ulaval.glo4003.domain.event;

import java.io.Serializable;

public class Category implements Serializable {

    private long id;
    private double price;
    private CategoryType type;

    public Category(double price, long id, CategoryType type) {
        this.price = price;
        this.id = id;
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public long getId() {
        return id;
    }

    public CategoryType getType() {
        return type;
    }
}
