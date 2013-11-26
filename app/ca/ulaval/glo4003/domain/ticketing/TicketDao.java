package ca.ulaval.glo4003.domain.ticketing;

import ca.ulaval.glo4003.domain.Dao;

import java.util.List;

public interface TicketDao extends Dao<Ticket> {

    List<Ticket> search(TicketSearchCriteria criteria);
}
