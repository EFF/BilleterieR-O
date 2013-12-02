package ca.ulaval.glo4003;

import ca.ulaval.glo4003.domain.EmailService;
import ca.ulaval.glo4003.domain.boostrap.BootstrapperInteractor;
import ca.ulaval.glo4003.domain.boostrap.StagingBootstrapperInteractor;
import ca.ulaval.glo4003.email.SMTPEmailService;
import ca.ulaval.glo4003.persistence.FileBasedDaoPersistenceService;
import com.google.inject.AbstractModule;

public class StagingModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new ApplicationModule(new FileBasedDaoPersistenceService("Staging")));

        bind(EmailService.class).to(SMTPEmailService.class);
        bind(BootstrapperInteractor.class).to(StagingBootstrapperInteractor.class);
    }
}
