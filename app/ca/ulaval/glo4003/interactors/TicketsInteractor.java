package ca.ulaval.glo4003.interactors;

import ca.ulaval.glo4003.dataaccessobjects.TicketDao;
import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.models.Ticket;
import ca.ulaval.glo4003.models.TicketSearchCriteria;
import ca.ulaval.glo4003.models.TicketState;
import com.google.inject.Inject;

import java.util.List;

public class TicketsInteractor {

    private final TicketDao ticketDao;

    @Inject
    public TicketsInteractor(TicketDao ticketDao) {
        this.ticketDao = ticketDao;
    }

    public List<Ticket> search(TicketSearchCriteria ticketSearchCriteria) {
        return ticketDao.search(ticketSearchCriteria);
    }

    public Ticket getById(long id) throws RecordNotFoundException {
        return ticketDao.read(id);
    }

    public void reserveATicket(long ticketId) throws RecordNotFoundException {
        setNewTicketState(ticketId, TicketState.RESERVED);
    }

    public void freeATicket(long ticketId) throws RecordNotFoundException {
        setNewTicketState(ticketId, TicketState.AVAILABLE);
    }

    public void buyATicket(long ticketId) throws RecordNotFoundException {
        setNewTicketState(ticketId, TicketState.SOLD);
    }

    public int numberOfTicketAvailable(long eventId) {
        TicketSearchCriteria ticketSearchCriterias = new TicketSearchCriteria();
        ticketSearchCriterias.setEventId(eventId);
        ticketSearchCriterias.addState(TicketState.AVAILABLE);
        return search(ticketSearchCriterias).size();
    }

    public int numberOfTicketAvailable(long eventId, long categoryId) {
        TicketSearchCriteria ticketSearchCriterias = new TicketSearchCriteria();
        ticketSearchCriterias.setEventId(eventId);
        ticketSearchCriterias.setCategoryId(categoryId);
        ticketSearchCriterias.addState(TicketState.AVAILABLE);
        return search(ticketSearchCriterias).size();
    }

    private void setNewTicketState(long ticketId, TicketState newTicketState) throws RecordNotFoundException {
        Ticket ticket = ticketDao.read(ticketId);
        ticket.setState(newTicketState);
        ticketDao.update(ticket);
    }
}
