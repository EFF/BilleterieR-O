package ca.ulaval.glo4003.interactors;

import ca.ulaval.glo4003.dataaccessobjects.TicketDao;
import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.exceptions.UpdateTicketStateUnauthorizedException;
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

    public void reserveATicket(long ticketId) throws RecordNotFoundException, UpdateTicketStateUnauthorizedException {
        Ticket ticket = ticketDao.read(ticketId);
        if (ticket.getState() == TicketState.AVAILABLE) {
            setNewTicketState(ticket, TicketState.RESERVED);
        } else {
            throw new UpdateTicketStateUnauthorizedException();
        }
    }

    public void freeATicket(long ticketId) throws RecordNotFoundException, UpdateTicketStateUnauthorizedException {
        Ticket ticket = ticketDao.read(ticketId);
        if (ticket.getState() == TicketState.RESERVED) {
            setNewTicketState(ticket, TicketState.AVAILABLE);
        } else {
            throw new UpdateTicketStateUnauthorizedException();
        }
        setNewTicketState(ticket, TicketState.AVAILABLE);
    }

    public void buyATicket(long ticketId) throws RecordNotFoundException, UpdateTicketStateUnauthorizedException {
        Ticket ticket = ticketDao.read(ticketId);
        if (ticket.getState() == TicketState.RESERVED) {
            setNewTicketState(ticket, TicketState.SOLD);
        } else {
            throw new UpdateTicketStateUnauthorizedException();
        }
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

    private void setNewTicketState(Ticket ticket, TicketState newTicketState) throws RecordNotFoundException {
        ticket.setState(newTicketState);
        ticketDao.update(ticket);
    }
}
