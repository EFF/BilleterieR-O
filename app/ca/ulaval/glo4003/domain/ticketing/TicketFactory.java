package ca.ulaval.glo4003.domain.ticketing;

public class TicketFactory {

    public static Ticket createAvailableGeneralAdmissionTicket(long eventId, long categoryId) {
        return new Ticket(eventId, categoryId, TicketState.AVAILABLE, "", TicketingConstantsManager.TICKET_INVALID_SEAT_NUMBER);
    }

    public static Ticket createAvailableSeatTicket(long eventId, long categoryId, String section, int seat) {
        return new Ticket(eventId, categoryId, TicketState.AVAILABLE, section, seat);
    }
}
