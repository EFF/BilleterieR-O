import ca.ulaval.glo4003.Bootstrap;
import ca.ulaval.glo4003.modules.StagingModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import play.Application;
import play.GlobalSettings;

public class Global extends GlobalSettings {

    private static final Injector INJECTOR = createInjector();

    @Override
    public <A> A getControllerInstance(Class<A> controllerClass)
            throws Exception {
        return INJECTOR.getInstance(controllerClass);
    }

    @Override
    public void onStart(Application application) {
        super.onStart(application);

        if (play.Play.isDev()) {
            Bootstrap bootstrap = INJECTOR.getInstance(Bootstrap.class);
            bootstrap.initData();
        }
    }

    private static Injector createInjector() {
        return Guice.createInjector(new StagingModule());
    }
}
