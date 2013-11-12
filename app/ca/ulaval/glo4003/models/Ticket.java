package ca.ulaval.glo4003.models;


import java.io.Serializable;

public class Ticket extends Record implements Serializable {
    private long categoryId;
    private long eventId;
    private String section;
    private long seat;

    public Ticket(long eventId, long categoryId, String section, long seat) {
        this.categoryId = categoryId;
        this.eventId = eventId;
        this.section = section;
        this.seat = seat;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public long getEventId() {
        return eventId;
    }

    public String getSection() {
        return section;
    }

    public long getSeat() {
        return seat;
    }
}
