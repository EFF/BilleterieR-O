package ca.ulaval.glo4003.email;

import ca.ulaval.glo4003.domain.EmailService;
import com.google.inject.AbstractModule;

public class EmailModule extends AbstractModule {

    @Override
    protected void configure() {
        // TODO: Bind to FakeEmailService in test and staging. Bind to LocalSMTPEmailService in prod
        bind(EmailService.class).to(FakeEmailService.class);
    }
}
