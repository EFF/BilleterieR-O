package ca.ulaval.glo4003.api.bootstrap;

import ca.ulaval.glo4003.domain.boostrap.BootstrapperInteractor;
import com.google.inject.Inject;
import play.mvc.Controller;
import play.mvc.Result;

public class BootstrapController extends Controller {

    private final BootstrapperInteractor bootstrapperInteractor;

    @Inject
    public BootstrapController(BootstrapperInteractor bootstrapperInteractor) {
        this.bootstrapperInteractor = bootstrapperInteractor;
    }

    public Result index() {
        bootstrapperInteractor.deleteAll();
        bootstrapperInteractor.initData();

        return ok("Bootstrapped!");
    }
}
