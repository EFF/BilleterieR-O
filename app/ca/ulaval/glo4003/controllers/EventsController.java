package ca.ulaval.glo4003.controllers;

import ca.ulaval.glo4003.ConstantsManager;
import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.interactors.EventsInteractor;
import ca.ulaval.glo4003.models.Event;
import ca.ulaval.glo4003.models.EventSearchCriteria;
import ca.ulaval.glo4003.models.Gender;
import com.google.inject.Inject;
import org.joda.time.LocalDateTime;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.security.InvalidParameterException;
import java.util.List;

public class EventsController extends Controller {

    private final EventsInteractor eventsInteractor;

    @Inject
    public EventsController(EventsInteractor eventsInteractor) {
        this.eventsInteractor = eventsInteractor;
    }

    public Result index() {
        EventSearchCriteria eventSearchCriteria = extractEventSearchCriteriaFromRequest();
        try {
            List<Event> searchResults = eventsInteractor.search(eventSearchCriteria);
            return ok(Json.toJson(searchResults));
        } catch (InvalidParameterException ignored) {
            return internalServerError();
        }
    }

    public Result show(long id) {
        try {
            Event event = eventsInteractor.getById(id);
            return ok(Json.toJson(event));
        } catch (RecordNotFoundException e) {
            return notFound();
        }
    }

    private EventSearchCriteria extractEventSearchCriteriaFromRequest() {
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
        return eventSearchCriteria;
    }
}
