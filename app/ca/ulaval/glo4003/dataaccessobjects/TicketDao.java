package ca.ulaval.glo4003.dataaccessobjects;

import ca.ulaval.glo4003.models.Ticket;
import ca.ulaval.glo4003.models.TicketSearchCriteria;

import java.util.List;

public interface TicketDao extends DataAccessObject<Ticket> {

    List<Ticket> search(TicketSearchCriteria criteria);
}
