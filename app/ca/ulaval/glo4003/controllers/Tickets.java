package ca.ulaval.glo4003.controllers;


import ca.ulaval.glo4003.dataaccessobjects.TicketDao;
import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.models.Ticket;
import com.google.inject.Inject;
import play.libs.Json;
import play.mvc.Result;

import static play.mvc.Results.notFound;
import static play.mvc.Results.ok;

public class Tickets {
    private final TicketDao ticketDao;

    @Inject
    public Tickets(TicketDao ticketDao) {
        this.ticketDao = ticketDao;
    }

    public Result show(long id) {
        try {
            Ticket ticket = ticketDao.read(id);
            return ok(Json.toJson(ticket));
        } catch (RecordNotFoundException e) {
            return notFound();
        }
    }
}
