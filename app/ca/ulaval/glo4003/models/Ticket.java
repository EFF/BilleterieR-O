package ca.ulaval.glo4003.models;


import java.io.Serializable;

public class Ticket extends Record implements Serializable {
    private long categoryId;
    private long eventId;
    private String section;
    private int seat;
    private TicketState state;

    public Ticket(long eventId, long categoryId, TicketState state, String section, int seat) {
        this.categoryId = categoryId;
        this.eventId = eventId;
        this.section = section;
        this.seat = seat;
        this.state = state;
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

    public int getSeat() {
        return seat;
    }

    public TicketState getState() {
        return state;
    }

    public void setState(TicketState state) {
        this.state = state;
    }
}
