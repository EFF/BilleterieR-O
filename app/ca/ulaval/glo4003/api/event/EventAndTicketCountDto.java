package ca.ulaval.glo4003.api.event;

import ca.ulaval.glo4003.domain.event.Event;

import java.io.Serializable;

public class EventAndTicketCountDto implements Serializable {

    private Event event;
    private int ticketCount;

    public EventAndTicketCountDto(Event event, int ticketCount) {
        this.event = event;
        this.ticketCount = ticketCount;
    }

    public Event getEvent() {
        return event;
    }

    public int getTicketCount() {
        return ticketCount;
    }

}
