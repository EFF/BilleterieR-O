package ca.ulaval.glo4003.api.event;

import ca.ulaval.glo4003.domain.RecordNotFoundException;
import ca.ulaval.glo4003.domain.event.Event;
import ca.ulaval.glo4003.domain.event.EventSearchCriteria;
import ca.ulaval.glo4003.domain.event.EventsInteractor;
import ca.ulaval.glo4003.domain.event.Gender;
import ca.ulaval.glo4003.domain.ticketing.TicketsInteractor;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDateTime;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class EventsController extends Controller {

    private final EventsInteractor eventsInteractor;
    private final TicketsInteractor ticketsInteractor;

    @Inject
    public EventsController(EventsInteractor eventsInteractor, TicketsInteractor ticketsInteractor) {
        this.eventsInteractor = eventsInteractor;
        this.ticketsInteractor = ticketsInteractor;
    }

    public Result index() {
        EventSearchCriteria eventSearchCriteria = extractEventSearchCriteriaFromRequest();
        try {
            List<EventAndTicketCountDto> eventsDto = new ArrayList<>();
            List<Event> searchResults = eventsInteractor.search(eventSearchCriteria);

            for (Event event : searchResults) {
                int ticketCount = ticketsInteractor.numberOfTicketAvailable(event.getId());
                EventAndTicketCountDto eventDto = new EventAndTicketCountDto(event, ticketCount);
                eventsDto.add(eventDto);
            }

            return ok(Json.toJson(eventsDto));
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
        final String sport = request().getQueryString(ApiEventConstantsManager.QUERY_STRING_SPORT_PARAM_NAME);
        final String dateStart = request().getQueryString(ApiEventConstantsManager.QUERY_STRING_DATE_START_PARAM_NAME);
        final String dateEnd = request().getQueryString(ApiEventConstantsManager.QUERY_STRING_DATE_END_PARAM_NAME);
        final String team = request().getQueryString(ApiEventConstantsManager.QUERY_STRING_TEAM_PARAM_NAME);
        final String genderString = request().getQueryString(ApiEventConstantsManager.QUERY_STRING_GENDER_PARAM_NAME);

        LocalDateTime start = StringUtils.isNotBlank(dateStart) ? LocalDateTime.parse(dateStart): null;
        LocalDateTime end = StringUtils.isNotBlank(dateEnd) ? LocalDateTime.parse(dateEnd): null;
        Gender gender = StringUtils.isNotBlank(genderString) ? Gender.valueOf(genderString) : null;

        EventSearchCriteria eventSearchCriteria = new EventSearchCriteria();
        eventSearchCriteria.setSportName(sport);
        eventSearchCriteria.setTeamName(team);
        eventSearchCriteria.setDateEnd(end);
        eventSearchCriteria.setDateStart(start);
        eventSearchCriteria.setGender(gender);
        return eventSearchCriteria;
    }
}
