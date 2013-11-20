package ca.ulaval.glo4003.controllers;


import ca.ulaval.glo4003.ConstantsManager;
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
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.google.inject.Inject;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TicketsController extends Controller {
    private final EventDao eventDao;
    private final TicketDao ticketDao;

    @Inject
    public TicketsController(EventDao eventDao, TicketDao ticketDao) {
        this.ticketDao = ticketDao;
        this.eventDao = eventDao;
    }

    public Result index() {
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
            return badRequest();
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

        try {
            return ok(Json.toJson(ticketDao.search(ticketSearchCriteria)));
        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    @Security.Authenticated(Secured.class)
    public Result checkout() {
        try {
            List<Long> ids = getListTicketIds();
            boolean recordsExist = checkIfTicketsExist(ids);
            if (!recordsExist) {
                return notFound();
            }
            boolean ticketsAreReserved = checkIfTicketsAreReserved(ids);
            if (!ticketsAreReserved) {
                return internalServerError();
            }
            return updateTicketsState(ids, TicketState.SOLD);
        } catch (IOException e) {
            return internalServerError();
        }
    }

    private boolean checkIfTicketsAreReserved(List<Long> ids) {
        try {
            for (Long id : ids) {
                Ticket ticket = ticketDao.read(id);
                if (ticket.getState() != TicketState.RESERVED) {
                    return false;
                }
            }
            return true;
        } catch (RecordNotFoundException e) {
            return false;
        }
    }

    public Result free() {
        try {
            List<Long> ids = getListTicketIds();
            boolean recordsExist = checkIfTicketsExist(ids, true);
            if (!recordsExist) {
                return notFound();
            }

            incrementCategories(ids);
            return updateTicketsState(ids, TicketState.AVAILABLE);
        } catch (IOException e) {
            return internalServerError();
        } catch (RecordNotFoundException re) {
            return notFound();
        }
    }

    public Result reserve() {
        try {
            List<Long> ids = getListTicketIds();
            boolean recordsExist = checkIfTicketsExist(ids, true);
            if (!recordsExist) {
                return notFound();
            }

            decrementCategories(ids);
            return updateTicketsState(ids, TicketState.RESERVED);
        } catch (IOException e) {
            return internalServerError();
        } catch (RecordNotFoundException re) {
            return notFound();
        } catch (MaximumExceededException e) {
            return badRequest("Il n'y a pas assez de billets disponibles dans l'une des catégories.");
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

    public Result showEventSections(long eventId) {
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

    private List<Long> getListTicketIds() throws IOException {
        JsonNode json = request().body().asJson();
        JsonNode node = json.get(ConstantsManager.TICKET_IDS_FIELD_NAME);

        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<Long>> typeRef = new TypeReference<List<Long>>() {
        };

        return mapper.readValue(node.traverse(), typeRef);
    }

    private void decrementCategories(List<Long> ids) throws RecordNotFoundException, MaximumExceededException {
        Map<String, Collection<Long>> idsByEventDotCategory = regroupByEventAndCategory(ids);
        for (Map.Entry<String, Collection<Long>> entry : idsByEventDotCategory.entrySet()) {
            String splittedKey[] = entry.getKey().split("\\.");
            Long eventId = Long.parseLong(splittedKey[0]);
            Long categoryId = Long.parseLong(splittedKey[1]);
            eventDao.decrementEventCategoryNumberOfTickets(eventId, categoryId, entry.getValue().size());
        }
    }

    private void incrementCategories(List<Long> ids) throws RecordNotFoundException {
        Map<String, Collection<Long>> idsByEventDotCategory = regroupByEventAndCategory(ids);
        for (Map.Entry<String, Collection<Long>> entry : idsByEventDotCategory.entrySet()) {
            String splittedKey[] = entry.getKey().split("\\.");
            Long eventId = Long.parseLong(splittedKey[0]);
            Long categoryId = Long.parseLong(splittedKey[1]);
            eventDao.incrementEventCategoryNumberOfTickets(eventId, categoryId, entry.getValue().size());
        }
    }

    private Map<String, Collection<Long>> regroupByEventAndCategory(List<Long> ids) {
        Multimap<String, Long> idsByEventDotCategory = ArrayListMultimap.create();
        for (Long id : ids) {
            try {
                Ticket ticket = ticketDao.read(id);
                String key = String.valueOf(ticket.getEventId()) + "." + String.valueOf(ticket.getCategoryId());
                idsByEventDotCategory.put(key, id);
            } catch (RecordNotFoundException e) {
            }
        }
        return idsByEventDotCategory.asMap();
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

    private boolean checkIfTicketsExist(List<Long> ids) {
        return checkIfTicketsExist(ids, false);
    }

    private boolean checkIfTicketsExist(List<Long> ids, boolean checkIfTicketsEventIdsExist) {
        try {
            for (Long id : ids) {
                Ticket ticket = ticketDao.read(id);
                if (checkIfTicketsEventIdsExist) {
                    eventDao.findCategory(ticket.getEventId(), ticket.getCategoryId());
                }
            }
            return true;
        } catch (RecordNotFoundException e) {
            return false;
        }
    }
}
