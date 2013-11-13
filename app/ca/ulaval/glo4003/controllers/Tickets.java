package ca.ulaval.glo4003.controllers;


import ca.ulaval.glo4003.dataaccessobjects.TicketDao;
import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.models.Ticket;
import ca.ulaval.glo4003.models.TicketSearchCriteria;
import ca.ulaval.glo4003.models.TicketState;
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
        final String eventId = request().getQueryString("eventId");
        final String sectionName = request().getQueryString("sectionName");
        final String categoryId = request().getQueryString("categoryId");
        final String stringStates = request().getQueryString("states");

        TicketSearchCriteria ticketSearchCriteria = new TicketSearchCriteria();
        if (eventId != null) {
            ticketSearchCriteria.setEventId(Long.parseLong(eventId));
        }
        if (categoryId != null) {
            ticketSearchCriteria.setCategoryId(Long.parseLong(categoryId));
        }
        ticketSearchCriteria.setSectionName(sectionName);
        String states[] = stringStates.split(",");
        for (String stringState : states) {
            TicketState state = TicketState.valueOf(stringState);
            if (state != null) {
                ticketSearchCriteria.addState(state);
            }
        }

        try {
            return ok(Json.toJson(ticketDao.search(ticketSearchCriteria)));
        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    public Result reserve(long id) {
        try {
            Ticket ticket = ticketDao.read(id);
            ticket.setState(TicketState.RESERVED);
            ticketDao.update(ticket);
            return ok();
        } catch (RecordNotFoundException e) {
            return notFound();
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

    public Result free(String strIds) {
        try {
            for (String strId : strIds.split(",")) {
                Long id = Long.valueOf(strId).longValue();
                Ticket ticket = ticketDao.read(id);
                ticket.setState(TicketState.AVAILABLE);
                ticketDao.update(ticket);
            }
            return ok();
        } catch (RecordNotFoundException e) {
            return notFound();
        }
    }

    public Result showEventTicketSections(long eventId){
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
