package ca.ulaval.glo4003.models;

import ca.ulaval.glo4003.ConstantsManager;

public class TicketFactory {

    public static Ticket createGeneralAdmissionTicket(long eventId, long categoryId) {
        return new Ticket(eventId, categoryId, "", ConstantsManager.TICKET_INVALID_SEAT_NUMBER);
    }

    public static Ticket createSeatTicket(long eventId, long categoryId, String section, int seat) {
        return new Ticket(eventId, categoryId, section, seat);
    }
}
