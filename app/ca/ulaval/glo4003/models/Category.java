package ca.ulaval.glo4003.models;

import ca.ulaval.glo4003.exceptions.MaximumExceededException;

import java.io.Serializable;

public class Category implements Serializable {

    private long id;
    private double price;
    private int numberOfTickets;
    private CategoryType type;

    public Category(double price, int numberOfTickets, long id, CategoryType type) {
        this.price = price;
        this.id = id;
        this.numberOfTickets = numberOfTickets;
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public int getNumberOfTickets() {
        return numberOfTickets;
    }

    public synchronized void decrementNumberOfTickets(int decrementNumber) throws MaximumExceededException {
        if (numberOfTickets < decrementNumber) throw new MaximumExceededException();
        numberOfTickets -= decrementNumber;
    }

    public long getId() {
        return id;
    }

    public CategoryType getType() {
        return type;
    }
}
