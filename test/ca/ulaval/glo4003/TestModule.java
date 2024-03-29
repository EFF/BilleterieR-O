package ca.ulaval.glo4003;

import ca.ulaval.glo4003.ApplicationModule;
import ca.ulaval.glo4003.TestBootstrapperInteractor;
import ca.ulaval.glo4003.domain.EmailService;
import ca.ulaval.glo4003.domain.boostrap.BootstrapperInteractor;
import ca.ulaval.glo4003.email.FakeEmailService;
import ca.ulaval.glo4003.persistence.InMemoryDaoPersistenceService;
import com.google.inject.AbstractModule;

public class TestModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new ApplicationModule(new InMemoryDaoPersistenceService()));

        bind(BootstrapperInteractor.class).to(TestBootstrapperInteractor.class);
        bind(EmailService.class).to(FakeEmailService.class);
    }
}
