package ca.ulaval.glo4003.domain.ticketing;

import ca.ulaval.glo4003.ConstantsManager;

public class TicketFactory {

    public static Ticket createAvailableGeneralAdmissionTicket(long eventId, long categoryId) {
        return new Ticket(eventId, categoryId, TicketState.AVAILABLE, "", ConstantsManager.TICKET_INVALID_SEAT_NUMBER);
    }

    public static Ticket createAvailableSeatTicket(long eventId, long categoryId, String section, int seat) {
        return new Ticket(eventId, categoryId, TicketState.AVAILABLE, section, seat);
    }
}
