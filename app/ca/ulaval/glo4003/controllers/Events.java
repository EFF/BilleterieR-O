package ca.ulaval.glo4003.controllers;

import ca.ulaval.glo4003.dataaccessobjects.EventDao;
import ca.ulaval.glo4003.models.Event;
import com.google.inject.Inject;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;


public class Events extends Controller {

    @Inject
    private EventDao eventDao;

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
}
