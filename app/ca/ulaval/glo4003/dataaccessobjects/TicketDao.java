package ca.ulaval.glo4003.dataaccessobjects;


import ca.ulaval.glo4003.models.Ticket;
import ca.ulaval.glo4003.services.DaoPersistenceService;

public class TicketDao extends PersistedDao<Ticket> implements DataAccessObject<Ticket> {
    public TicketDao(DaoPersistenceService persistenceService) {
        super(persistenceService);
    }
}
