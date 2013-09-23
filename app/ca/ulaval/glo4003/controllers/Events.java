package ca.ulaval.glo4003.controllers;

import ca.ulaval.glo4003.models.Event;
import ca.ulaval.glo4003.models.EventSearchCriteria;
import ca.ulaval.glo4003.dataaccessobjects.EventDao;
import com.google.inject.Inject;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class Events extends Controller {

    private final EventDao eventDao;

    @Inject
    public Events(EventDao eventDao) {
        this.eventDao = eventDao;
    }

    public Result index() {
        final String sport = request().getQueryString("sport");
        final String dateStart = request().getQueryString("dateStart");
        final String dateEnd = request().getQueryString("dateEnd");
        final String team = request().getQueryString("team");

        LocalDateTime start = dateStart == null ? null : LocalDateTime.parse(dateStart);
        LocalDateTime end = dateEnd == null ? null : LocalDateTime.parse(dateEnd);

        EventSearchCriteria eventSearchCriteria = new EventSearchCriteria();
        eventSearchCriteria.setSportName(sport);
        eventSearchCriteria.setTeamName(team);
        eventSearchCriteria.setDateEnd(start);
        eventSearchCriteria.setDateStart(end);

        try {
            return ok(Json.toJson(eventDao.search(eventSearchCriteria)));
        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }
    }

    public Result show(long id) {
        Event event = eventDao.read(id);
        if (event == null) {
            return notFound();
        } else {
            return ok(Json.toJson(event));
        }
    }
}
