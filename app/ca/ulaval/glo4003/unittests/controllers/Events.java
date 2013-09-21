package ca.ulaval.glo4003.unittests.controllers;

import ca.ulaval.glo4003.unittests.dataaccessobjects.EventDao;
import ca.ulaval.glo4003.models.Event;
import ca.ulaval.glo4003.models.EventSearchCriteria;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.inject.Inject;
import org.joda.time.LocalDate;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import javax.annotation.Nullable;
import java.util.List;

public class Events extends Controller {

    private final EventDao eventDao;

    @Inject
    public Events(EventDao eventDao) {
        this.eventDao = eventDao;
    }

    @Inject
    public Result index() {
        return ok(Json.toJson(eventDao.list()));
    }

    public Result show(long id) {
        Event event = eventDao.read(id);
        if (event == null) {
            return notFound();
        } else {
            return ok(Json.toJson(event));
        }
    }

    public Result search() {
        final String sport = request().getQueryString("sport");
        final String dateStart = request().getQueryString("dateStart");
        final String dateEnd = request().getQueryString("dateEnd");
        final String team = request().getQueryString("team");

        LocalDate start = dateStart == null ? null : LocalDate.parse(dateStart);
        LocalDate end = dateEnd == null ? null : LocalDate.parse(dateEnd);

        EventSearchCriteria eventSearchCriteria = new EventSearchCriteria();
        eventSearchCriteria.setSportName(sport);
        eventSearchCriteria.setTeamName(team);
        eventSearchCriteria.setDateEnd(start);
        eventSearchCriteria.setDateStart(end);

        List<Event> searchResults = null;
        try {
            searchResults = eventDao.search(eventSearchCriteria);
        } catch (Exception e) {
            internalServerError(e.getMessage());
        }

        return ok(Json.toJson(searchResults));
    }

    private FluentIterable<Event> FilterBySports(final String sport, FluentIterable<Event> results) {
        if (sport != null) {
            results = results.filter(new Predicate<Event>() {
                @Override
                public boolean apply(@Nullable Event event) {
                    return event.getSport().getName().toLowerCase() == sport.toLowerCase();
                }
            });
        }
        return results;
    }
}
