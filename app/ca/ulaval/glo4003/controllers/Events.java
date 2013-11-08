package ca.ulaval.glo4003.controllers;

import ca.ulaval.glo4003.ConstantsManager;
import ca.ulaval.glo4003.Secured;
import ca.ulaval.glo4003.dataaccessobjects.EventDao;
import ca.ulaval.glo4003.dataaccessobjects.TicketDao;
import ca.ulaval.glo4003.exceptions.MaximumExceededException;
import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.models.Event;
import ca.ulaval.glo4003.models.EventSearchCriteria;
import ca.ulaval.glo4003.models.Gender;
import ca.ulaval.glo4003.models.Ticket;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.inject.Inject;
import org.codehaus.jackson.JsonNode;
import org.joda.time.LocalDateTime;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Events extends Controller {

    private final EventDao eventDao;
    private final TicketDao ticketDao;

    @Inject
    public Events(EventDao eventDao, TicketDao ticketDao) {
        this.eventDao = eventDao;
        this.ticketDao = ticketDao;
    }

    public Result index() {
        final String sport = request().getQueryString(ConstantsManager.QUERY_STRING_SPORT_PARAM_NAME);
        final String dateStart = request().getQueryString(ConstantsManager.QUERY_STRING_DATE_START_PARAM_NAME);
        final String dateEnd = request().getQueryString(ConstantsManager.QUERY_STRING_DATE_END_PARAM_NAME);
        final String team = request().getQueryString(ConstantsManager.QUERY_STRING_TEAM_PARAM_NAME);
        final String genderString = request().getQueryString(ConstantsManager.QUERY_STRING_GENDER_PARAM_NAME);

        LocalDateTime start = dateStart == null ? null : LocalDateTime.parse(dateStart);
        LocalDateTime end = dateEnd == null ? null : LocalDateTime.parse(dateEnd);
        Gender gender = genderString == null ? null : Gender.valueOf(genderString);

        EventSearchCriteria eventSearchCriteria = new EventSearchCriteria();
        eventSearchCriteria.setSportName(sport);
        eventSearchCriteria.setTeamName(team);
        eventSearchCriteria.setDateEnd(end);
        eventSearchCriteria.setDateStart(start);
        eventSearchCriteria.setGender(gender);

        try {
            return ok(Json.toJson(eventDao.search(eventSearchCriteria)));
        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    public Result show(long id) {
        try {
            Event event = eventDao.read(id);
            return ok(Json.toJson(event));
        } catch (RecordNotFoundException e) {
            return notFound();
        }
    }

    public Result showEventTicketsByCategories(long eventId) {
        List<Ticket> tickets = ticketDao.searchByEvent(eventId);
        ListMultimap<Long, Ticket> result = ArrayListMultimap.create();
        for (Ticket ticket : tickets) {
            result.put(ticket.getCategoryId(), ticket);
        }
        return ok(Json.toJson(result.asMap()));
    }

    public Result showEventTicketSections(long eventId){
        List<Ticket> tickets = ticketDao.searchByEvent(eventId);
        List<String> sections = new ArrayList<>();

        for(Ticket ticket : tickets){
             if(!sections.contains(ticket.getSection())){
                 sections.add(ticket.getSection());
             }
        }
        return ok(Json.toJson(sections));
    }

    @Security.Authenticated(Secured.class)
    public Result decrementCategoryCounter() {
        JsonNode items = request().body().asJson();
        Iterator<JsonNode> jsonNodeIterator = items.iterator();

        while (jsonNodeIterator.hasNext()) {
            JsonNode item = jsonNodeIterator.next();

            Long eventId = item.get(ConstantsManager.EVENT_ID_FIELD_NAME).asLong();
            Long categoryId = item.get(ConstantsManager.CATEGORY_ID_FIELD_NAME).asLong();
            int quantity = item.get(ConstantsManager.QUANTITY_FIELD_NAME).asInt();

            try {
                eventDao.decrementEventCategoryNumberOfTickets(eventId, categoryId, quantity);
            } catch (RecordNotFoundException e) {
                return notFound();
            } catch (MaximumExceededException e) {
                return badRequest("Il n'y a pas assez de billets disponibles dans la catégorie" + categoryId.toString());
            }
        }

        return ok();
    }
}
