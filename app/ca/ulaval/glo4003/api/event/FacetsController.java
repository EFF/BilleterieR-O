package ca.ulaval.glo4003.api.event;

import ca.ulaval.glo4003.domain.event.FacetsInteractor;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
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

        facets.put(ApiEventConstantsManager.FACET_SPORT, facetsInteractor.sports());
        facets.put(ApiEventConstantsManager.FACET_GENDER, facetsInteractor.genders());

        return ok(Json.toJson(facets));
    }
}
