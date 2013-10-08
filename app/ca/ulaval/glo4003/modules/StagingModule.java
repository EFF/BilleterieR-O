package ca.ulaval.glo4003.modules;

import ca.ulaval.glo4003.Bootstrapper;
import ca.ulaval.glo4003.StagingBootstrapper;
import com.google.inject.AbstractModule;

public class StagingModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new ApplicationModule());

        bind(Bootstrapper.class).to(StagingBootstrapper.class);
    }
}
