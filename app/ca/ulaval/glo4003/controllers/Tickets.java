package ca.ulaval.glo4003.controllers;


import ca.ulaval.glo4003.Secured;
import ca.ulaval.glo4003.dataaccessobjects.EventDao;
import ca.ulaval.glo4003.dataaccessobjects.TicketDao;
import ca.ulaval.glo4003.exceptions.MaximumExceededException;
import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.models.Ticket;
import ca.ulaval.glo4003.models.TicketSearchCriteria;
import ca.ulaval.glo4003.models.TicketState;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tickets extends Controller {
    private final EventDao eventDao;
    private final TicketDao ticketDao;

    @Inject
    public Tickets(EventDao eventDao, TicketDao ticketDao) {
        this.ticketDao = ticketDao;
        this.eventDao = eventDao;
    }

    public Result index() {
        final String eventId = request().getQueryString("eventId");
        final String sectionName = request().getQueryString("sectionName");
        final String categoryId = request().getQueryString("categoryId");
        final String stringStates = request().getQueryString("states");
        final String strQuantity = request().getQueryString("quantity");

        TicketSearchCriteria ticketSearchCriteria = new TicketSearchCriteria();
        if (eventId != null) {
            ticketSearchCriteria.setEventId(Long.parseLong(eventId));
        }
        if (categoryId != null) {
            ticketSearchCriteria.setCategoryId(Long.parseLong(categoryId));
        }
        if (strQuantity != null) {
            ticketSearchCriteria.setQuantity(Integer.parseInt(strQuantity));
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

    @Security.Authenticated(Secured.class)
    public Result checkout(String strIds) {
        List<Long> ids = convertStringIdsToArrayLong(strIds);
        Result recordsExist = checkIfRecordsExist(ids);
        if (recordsExist == notFound()) {
            return notFound();
        }
        return updateTicketsState(ids, TicketState.SOLD);
    }

    public Result free(String strIds) {
        List<Long> ids = convertStringIdsToArrayLong(strIds);
        Result recordsExist = checkIfRecordsExist(ids, true);
        if (recordsExist == notFound()) {
            return notFound();
        }

        try {
            incrementCategories(ids);
        } catch (RecordNotFoundException e) {
            return notFound();
        }
        return updateTicketsState(ids, TicketState.AVAILABLE);
    }

    public Result reserve(String strIds) {
        List<Long> ids = convertStringIdsToArrayLong(strIds);
        Result recordsExist = checkIfRecordsExist(ids, true);
        if (recordsExist == notFound()) {
            return notFound();
        }

        try {
            decrementCategories(ids);
        } catch (RecordNotFoundException e) {
            return notFound();
        } catch (MaximumExceededException e) {
            return badRequest("Il n'y a pas assez de billets disponibles dans l'une des catégories.");
        }
        return updateTicketsState(ids, TicketState.RESERVED);
    }

    public Result show(long id) {
        try {
            Ticket ticket = ticketDao.read(id);
            return ok(Json.toJson(ticket));
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

    private void decrementCategories(List<Long> ids) throws RecordNotFoundException, MaximumExceededException {
        Multimap<String, Long> idsByEventDotCategory = regroupByEventAndCategory(ids);
        for (String key : idsByEventDotCategory.keys()) {
            String splittedKey[] = key.split("\\.");
            eventDao.decrementEventCategoryNumberOfTickets(Long.parseLong(splittedKey[0]), Long.parseLong(splittedKey[1]), idsByEventDotCategory.get(key).size());
        }
    }

    private void incrementCategories(List<Long> ids) throws RecordNotFoundException {
        Multimap<String, Long> idsByEventDotCategory = regroupByEventAndCategory(ids);
        for (String key : idsByEventDotCategory.keys()) {
            String splittedKey[] = key.split("\\.");
            eventDao.incrementEventCategoryNumberOfTickets(Long.parseLong(splittedKey[0]), Long.parseLong(splittedKey[1]), idsByEventDotCategory.get(key).size());
        }
    }

    private Multimap<String, Long> regroupByEventAndCategory(List<Long> ids) {
        Multimap<String, Long> idsByEventDotCategory = ArrayListMultimap.create();
        for (Long id : ids) {
            try {
                Ticket ticket = ticketDao.read(id);
                idsByEventDotCategory.put(String.valueOf(ticket.getEventId()) + "." + String.valueOf(ticket.getCategoryId()), id);
            } catch (RecordNotFoundException e) {}
        }
        return idsByEventDotCategory;
    }

    private List<Long> convertStringIdsToArrayLong(String strIds) {
        List<Long> ids = new ArrayList<>();
        for (String strId : strIds.split(",")) {
            ids.add(Long.valueOf(strId).longValue());
        }
        return ids;
    }

    private Result updateTicketsState(List<Long> ids, TicketState state) {
        try {
            for (Long id : ids) {
                Ticket ticket = ticketDao.read(id);
                ticket.setState(state);
                ticketDao.update(ticket);
            }
            return ok();
        } catch (RecordNotFoundException e) {
            return notFound();
        }
    }

    private Result checkIfRecordsExist(List<Long> ids) {
        return checkIfRecordsExist(ids, false);
    }


    private Result checkIfRecordsExist(List<Long> ids, boolean checkInEventDao) {
        try {
            for (Long id : ids) {
                Ticket ticket = ticketDao.read(id);
                if (checkInEventDao) {
                    eventDao.findCategory(ticket.getEventId(), ticket.getCategoryId());
                }
            }
            return ok();
        } catch (RecordNotFoundException e) {
            return notFound();
        }
    }
}
