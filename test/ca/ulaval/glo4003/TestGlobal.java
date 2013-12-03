package ca.ulaval.glo4003;

import ca.ulaval.glo4003.domain.boostrap.BootstrapperInteractor;
import com.google.inject.Guice;
import com.google.inject.Injector;
import play.Application;
import play.GlobalSettings;

public class TestGlobal extends GlobalSettings {

    public final Injector INJECTOR = createInjector();

    @Override
    public <A> A getControllerInstance(Class<A> controllerClass)
            throws Exception {
        return INJECTOR.getInstance(controllerClass);
    }

    @Override
    public void onStart(Application application) {
        super.onStart(application);

        BootstrapperInteractor bootstrapper = INJECTOR.getInstance(BootstrapperInteractor.class);
        bootstrapper.initData();
    }

    @Override
    public void onStop(Application application) {
        super.onStop(application);

        BootstrapperInteractor bootstrapper = INJECTOR.getInstance(BootstrapperInteractor.class);
        bootstrapper.deleteAll();
    }

    private Injector createInjector() {
        return Guice.createInjector(new TestModule());
    }
}
