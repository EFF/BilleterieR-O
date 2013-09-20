package ca.ulaval.glo4003.controllers;

import ca.ulaval.glo4003.dataaccessobjects.EventDao;
import ca.ulaval.glo4003.models.Event;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.inject.Inject;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import javax.annotation.Nullable;
import java.util.ArrayList;
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

        FluentIterable<Event> results = FluentIterable.from(eventDao.list());

        if (sport != null) {
            results = results.filter(new Predicate<Event>() {
                @Override
                public boolean apply(@Nullable Event event) {
                    return event.getSport().getName().toLowerCase() == sport.toLowerCase();
                }
            });
        }

        return ok(Json.toJson(results.toList()));
    }
}
