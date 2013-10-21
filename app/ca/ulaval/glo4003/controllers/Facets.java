package ca.ulaval.glo4003.controllers;

import ca.ulaval.glo4003.ConstantsManager;
import ca.ulaval.glo4003.dataaccessobjects.SportDao;
import ca.ulaval.glo4003.models.Gender;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Facets extends Controller {

    private final SportDao sportDao;

    @Inject
    public Facets(SportDao sportDao) {
        this.sportDao = sportDao;
    }

    public Result index() {
        Map<String, List> facets = new HashMap<>();

        facets.put(ConstantsManager.FACET_SPORT, sportDao.list());
        facets.put(ConstantsManager.FACET_GENDER, Arrays.asList(Gender.values()));

        return ok(Json.toJson(facets));
    }
}
