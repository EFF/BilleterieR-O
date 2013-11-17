package ca.ulaval.glo4003.controllers;

import ca.ulaval.glo4003.Bootstrapper;
import com.google.inject.Inject;
import play.mvc.Controller;
import play.mvc.Result;

public class BootstrapController extends Controller {

    private final Bootstrapper bootstrapper;

    @Inject
    public BootstrapController(Bootstrapper bootstrapper) {
        this.bootstrapper = bootstrapper;
    }

    public Result index() {
        if (!play.Play.isDev()) {
            return unauthorized();
        }

        bootstrapper.deleteAll();
        bootstrapper.initData();

        return ok("Bootstrapped!");
    }
}