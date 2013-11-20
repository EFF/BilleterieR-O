package ca.ulaval.glo4003.controllers;


import ca.ulaval.glo4003.ConstantsManager;
import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.interactors.TicketsInteractor;
import ca.ulaval.glo4003.models.Ticket;
import ca.ulaval.glo4003.models.TicketSearchCriteria;
import ca.ulaval.glo4003.models.TicketState;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.google.inject.Inject;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.List;

public class TicketsController extends Controller {

    private final TicketsInteractor ticketsInteractor;

    @Inject
    public TicketsController(TicketsInteractor ticketsInteractor) {
        this.ticketsInteractor = ticketsInteractor;
    }

    public Result index() {
        try {
            TicketSearchCriteria ticketSearchCriteria = extractTicketSearchCriteriaFromRequest();
            List<Ticket> searchResults = ticketsInteractor.search(ticketSearchCriteria);
            return ok(Json.toJson(searchResults));
        } catch (InvalidParameterException ignored) {
            return badRequest();
        }
    }

    public Result free() {
        try {
            List<Long> ids = extractTicketsIdsFromRequest();
            for (Long ticketId : ids) {
                ticketsInteractor.freeATicket(ticketId);
            }
            return ok();
        } catch (IOException e) {
            return internalServerError();
        } catch (RecordNotFoundException re) {
            return notFound();
        }
    }

    public Result reserve() {
        try {
            List<Long> ids = extractTicketsIdsFromRequest();
            for (Long ticketId : ids) {
                ticketsInteractor.reserveATicket(ticketId);
            }
            return ok();
        } catch (IOException e) {
            return internalServerError();
        } catch (RecordNotFoundException re) {
            return notFound();
        }
    }

    public Result show(long id) {
        try {
            Ticket ticket = ticketsInteractor.getById(id);
            return ok(Json.toJson(ticket));
        } catch (RecordNotFoundException e) {
            return notFound();
        }
    }

    public Result numberOfTickets() {
        String strEventId = request().getQueryString("strEventId");
        String strCategoryId = request().getQueryString("strCategoryId");
        int numberOfTickets;
        Long eventId = null;

        try {
            Longs.tryParse(strEventId);
        } catch (NumberFormatException ignored) {
            return badRequest();
        }

        try {
            Long categoryId = Longs.tryParse(strCategoryId);
            numberOfTickets = ticketsInteractor.numberOfTicketAvailable(eventId, categoryId);
        } catch (NumberFormatException ignored) {
            numberOfTickets = ticketsInteractor.numberOfTicketAvailable(eventId);
        }

        return ok(Json.toJson(numberOfTickets));
    }

    //TODO should this be a facet?
    public Result showEventSections(long eventId) {
        TicketSearchCriteria ticketSearchCriteria = new TicketSearchCriteria();
        ticketSearchCriteria.setEventId(eventId);
        List<Ticket> tickets = ticketsInteractor.search(ticketSearchCriteria);

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

    private List<Long> extractTicketsIdsFromRequest() throws IOException {
        JsonNode json = request().body().asJson();
        JsonNode node = json.get(ConstantsManager.TICKET_IDS_FIELD_NAME);

        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<Long>> typeRef = new TypeReference<List<Long>>() {
        };

        return mapper.readValue(node.traverse(), typeRef);
    }

    private TicketSearchCriteria extractTicketSearchCriteriaFromRequest() {
        final String strEventId = request().getQueryString("eventId");
        final String sectionName = request().getQueryString("sectionName");
        final String strCategoryId = request().getQueryString("categoryId");
        final String stringStates = request().getQueryString("states");
        final String strQuantity = request().getQueryString("quantity");

        Long eventId = null;
        Long categoryId = null;
        Integer quantity = null;

        if (strEventId != null) {
            eventId = Longs.tryParse(strEventId);
        }
        if (strCategoryId != null) {
            categoryId = Longs.tryParse(strCategoryId);
        }
        if (strQuantity != null) {
            quantity = Ints.tryParse(strQuantity);
        }
        if ((strEventId != null && eventId == null)
                || (strCategoryId != null && categoryId == null)
                || (strQuantity != null && quantity == null)) {
            throw new InvalidParameterException();
        }

        TicketSearchCriteria ticketSearchCriteria = new TicketSearchCriteria();
        ticketSearchCriteria.setEventId(eventId);
        ticketSearchCriteria.setCategoryId(categoryId);
        ticketSearchCriteria.setQuantity(quantity);
        ticketSearchCriteria.setSectionName(sectionName);

        if (stringStates != null) {
            String states[] = stringStates.split(",");
            for (String stringState : states) {
                TicketState state = TicketState.valueOf(stringState);
                if (state != null) {
                    ticketSearchCriteria.addState(state);
                }
            }
        }
        return ticketSearchCriteria;
    }
}
