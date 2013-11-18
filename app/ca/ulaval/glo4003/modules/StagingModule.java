package ca.ulaval.glo4003.modules;

import ca.ulaval.glo4003.interactors.StagingBootstrapperInteractor;
import ca.ulaval.glo4003.interactors.AuthenticationInteractor;
import ca.ulaval.glo4003.services.FileBasedDaoPersistenceService;
import com.google.inject.AbstractModule;

public class StagingModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new ApplicationModule(new FileBasedDaoPersistenceService("Staging")));

        bind(AuthenticationInteractor.Bootstrapper.class).to(StagingBootstrapperInteractor.class);
    }
}
