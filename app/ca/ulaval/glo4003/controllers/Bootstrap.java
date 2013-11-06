package ca.ulaval.glo4003.controllers;

import ca.ulaval.glo4003.Bootstrapper;
import com.google.inject.Inject;
import play.mvc.Controller;
import play.mvc.Result;

public class Bootstrap extends Controller {

    private final Bootstrapper bootstrapper;

    @Inject
    public Bootstrap(Bootstrapper bootstrapper) {
        this.bootstrapper = bootstrapper;
    }

    public Result index() {
        if (!play.Play.isDev()) {
            return unauthorized();
        }

        try {
            bootstrapper.deleteAll();
            bootstrapper.initData();
        } catch (Exception e) {
            return internalServerError(e.getMessage());
        }

        return ok("Bootstrapped!");
    }
}
