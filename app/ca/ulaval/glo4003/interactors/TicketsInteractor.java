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

    public Ticket getById() {
        return null;
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

    private void setNewTicketState(long ticketId, TicketState newTicketState) throws RecordNotFoundException {
        Ticket ticket = ticketDao.read(ticketId);
        ticket.setState(newTicketState);
        ticketDao.update(ticket);
    }
}
