package ca.ulaval.glo4003.controllers;


import ca.ulaval.glo4003.dataaccessobjects.TicketDao;
import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.models.Ticket;
import ca.ulaval.glo4003.models.TicketSearchCriteria;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.inject.Inject;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.Collections;
import java.util.List;

public class Tickets extends Controller {
    private final TicketDao ticketDao;

    @Inject
    public Tickets(TicketDao ticketDao) {
        this.ticketDao = ticketDao;
    }

    public Result index() {
        final Long eventId = Long.parseLong(request().getQueryString("eventId"));
        final String sectionName = request().getQueryString("sectionName");
        final Long categoryId = Long.parseLong(request().getQueryString("categoryId"));

        TicketSearchCriteria ticketSearchCriteria = new TicketSearchCriteria();
        ticketSearchCriteria.setEventId(eventId);
        ticketSearchCriteria.setSectionName(sectionName);
        ticketSearchCriteria.setCategoryId(categoryId);

        try {
            return ok(Json.toJson(ticketDao.search(ticketSearchCriteria)));
        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    public Result show(long id) {
        try {
            Ticket ticket = ticketDao.read(id);
            return ok(Json.toJson(ticket));
        } catch (RecordNotFoundException e) {
            return notFound();
        }
    }

    public Result showEventTicketSections(long eventId, long categoryId){
        TicketSearchCriteria ticketSearchCriteria = new TicketSearchCriteria();
        ticketSearchCriteria.setEventId(eventId);
        List<Ticket> tickets = ticketDao.search(ticketSearchCriteria);

        ListMultimap<Long, String> sections = ArrayListMultimap.create();
        for (Ticket ticket : tickets) {
            if (!sections.get(ticket.getCategoryId()).contains(ticket.getSection()))
                sections.put(ticket.getCategoryId(), ticket.getSection());
        }

        for (Long i : sections.keys()) {
            Collections.sort(sections.get(i));
        }
        return ok(Json.toJson(sections.asMap()));
    }
}
