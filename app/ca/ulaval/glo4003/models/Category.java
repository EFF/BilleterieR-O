package ca.ulaval.glo4003.models;

public class Category {

    private long id;
    private double price;
    private int numberOfTickets;

    public Category(long id, double price, int numberOfTickets) {
        this.id = id;
        this.price = price;
        this.numberOfTickets = numberOfTickets;
    }

    public long getId() {
        return id;
    }

    public double getPrice() {
        return price;
    }

    public int getNumberOfTickets() {
        return numberOfTickets;
    }
}
