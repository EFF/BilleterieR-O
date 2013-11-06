package ca.ulaval.glo4003.dataaccessobjects;


import ca.ulaval.glo4003.models.Ticket;
import ca.ulaval.glo4003.services.DaoPersistenceService;

import java.util.ArrayList;
import java.util.List;

public class TicketDao extends PersistedDao<Ticket> implements DataAccessObject<Ticket> {
    public TicketDao(DaoPersistenceService persistenceService) {
        super(persistenceService);
    }

    public List<Ticket> readForEvent(long eventId) {
        List<Ticket> tickets = new ArrayList<>();

        for(Ticket ticket :super.list()){
            if(ticket.getEventId() == eventId){
                tickets.add(ticket);
            }
        }

        return tickets;
    }
}
