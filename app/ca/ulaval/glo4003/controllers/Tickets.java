package ca.ulaval.glo4003.controllers;


import ca.ulaval.glo4003.dataaccessobjects.TicketDao;
import com.google.inject.Inject;
import play.libs.Json;
import play.mvc.Result;

import static play.mvc.Results.ok;

public class Tickets {
    private final TicketDao ticketDao;

    @Inject
    public Tickets(TicketDao ticketDao) {
        this.ticketDao = ticketDao;
    }

    public Result show(long id) {
        return ok(Json.toJson(ticketDao.list()));
    }
}
