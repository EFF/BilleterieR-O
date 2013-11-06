package ca.ulaval.glo4003.models;


import java.io.Serializable;

public class Ticket extends Record implements Serializable {
    private long categoryId;
    private long eventId;
    private String section;
    private long seat;

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

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public long getSeat() {
        return seat;
    }

    public void setSeat(long seat) {
        this.seat = seat;
    }
}
