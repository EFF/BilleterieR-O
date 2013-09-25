package ca.ulaval.glo4003.models;

public class Category {

    private double price;
    private int numberOfTickets;

    public Category(double price, int numberOfTickets) {
        this.price = price;
        this.numberOfTickets = numberOfTickets;
    }

    public double getPrice() {
        return price;
    }

    public int getNumberOfTickets() {
        return numberOfTickets;
    }
}
