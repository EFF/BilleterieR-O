package ca.ulaval.glo4003;

import ca.ulaval.glo4003.domain.EmailService;
import ca.ulaval.glo4003.domain.boostrap.BootstrapperInteractor;
import ca.ulaval.glo4003.domain.boostrap.StagingBootstrapperInteractor;
import ca.ulaval.glo4003.email.SMTPEmailService;
import ca.ulaval.glo4003.persistence.FileBasedDaoPersistenceService;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.typesafe.config.ConfigFactory;

public class StagingModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new ApplicationModule(new FileBasedDaoPersistenceService("Staging")));

        bind(String.class)
            .annotatedWith(Names.named("SmtpHost"))
            .toInstance(ConfigFactory.load().getString("smtp.host"));

        bind(String.class)
            .annotatedWith(Names.named("SmtpPort"))
            .toInstance(ConfigFactory.load().getString("smtp.port"));

        bind(EmailService.class).to(SMTPEmailService.class);
        bind(BootstrapperInteractor.class).to(StagingBootstrapperInteractor.class);
    }
}
