package ca.ulaval.glo4003.modules;

import ca.ulaval.glo4003.TestBootstrapper;
import ca.ulaval.glo4003.interactors.AuthenticationInteractor;
import ca.ulaval.glo4003.interactors.BootstrapperInteractor;
import ca.ulaval.glo4003.services.InMemoryDaoPersistenceService;
import com.google.inject.AbstractModule;

public class TestModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new ApplicationModule(new InMemoryDaoPersistenceService()));

        bind(BootstrapperInteractor.class).to(TestBootstrapper.class);
    }
}
