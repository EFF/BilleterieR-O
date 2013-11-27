package ca.ulaval.glo4003.modules;

import ca.ulaval.glo4003.ApplicationModule;
import ca.ulaval.glo4003.TestBootstrapperInteractor;
import ca.ulaval.glo4003.domain.boostrap.BootstrapperInteractor;
import ca.ulaval.glo4003.persistence.InMemoryDaoPersistenceService;
import com.google.inject.AbstractModule;

public class TestModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new ApplicationModule(new InMemoryDaoPersistenceService()));

        bind(BootstrapperInteractor.class).to(TestBootstrapperInteractor.class);
    }
}
