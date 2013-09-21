package ca.ulaval.glo4003.controllers;

import ca.ulaval.glo4003.dataaccessobjects.EventDao;
import ca.ulaval.glo4003.models.Event;
import ca.ulaval.glo4003.models.SearchCriteria;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.inject.Inject;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import play.data.format.Formats;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Date;
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

        LocalDate start = LocalDate.parse(dateStart);
        LocalDate end = LocalDate.parse(dateEnd);

        SearchCriteria searchCriteria = new SearchCriteria();
        searchCriteria.setSport(sport);
        searchCriteria.setTeam(team);
        searchCriteria.setDateEnd(start);
        searchCriteria.setDateStart(end);

        List<Event> searchResults = eventDao.search(searchCriteria);

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
