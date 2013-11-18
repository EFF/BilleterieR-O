package ca.ulaval.glo4003.controllers;

import ca.ulaval.glo4003.ConstantsManager;
import ca.ulaval.glo4003.dataaccessobjects.SportDao;
import ca.ulaval.glo4003.interactors.FacetsInteractor;
import ca.ulaval.glo4003.models.Gender;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FacetsController extends Controller {

    private final FacetsInteractor facetsInteractor;

    @Inject
    public FacetsController(FacetsInteractor facetsInteractor) {
        this.facetsInteractor = facetsInteractor;
    }

    public Result index() {
        Map<String, List> facets = new HashMap<>();

        facets.put(ConstantsManager.FACET_SPORT, facetsInteractor.sports());
        facets.put(ConstantsManager.FACET_GENDER, facetsInteractor.genders());

        return ok(Json.toJson(facets));
    }
}
