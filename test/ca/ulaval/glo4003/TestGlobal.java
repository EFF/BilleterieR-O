package ca.ulaval.glo4003;

import ca.ulaval.glo4003.modules.TestModule;
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

        Bootstrapper bootstrapper = INJECTOR.getInstance(Bootstrapper.class);
        bootstrapper.initData();
    }

    @Override
    public void onStop(Application application) {
        super.onStop(application);

        Bootstrapper bootstrapper = INJECTOR.getInstance(Bootstrapper.class);
        bootstrapper.deleteAll();
    }

    private Injector createInjector() {
        return Guice.createInjector(new TestModule());
    }
}
