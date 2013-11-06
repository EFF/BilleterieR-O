package ca.ulaval.glo4003.models;


import java.io.Serializable;

public class Ticket extends Record implements Serializable {
    private long categoryId;
    private long eventId;

    public Ticket(long eventId, long categoryId) {
        this.categoryId = categoryId;
        this.eventId = eventId;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public long getEventId() {
        return eventId;
    }
}
