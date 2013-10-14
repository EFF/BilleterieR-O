package ca.ulaval.glo4003.modules;

import ca.ulaval.glo4003.Bootstrapper;
import ca.ulaval.glo4003.TestBootstrapper;
import ca.ulaval.glo4003.services.InMemoryDaoPersistenceService;
import com.google.inject.AbstractModule;

public class TestModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new ApplicationModule(new InMemoryDaoPersistenceService()));

        bind(Bootstrapper.class).to(TestBootstrapper.class);
    }
}
