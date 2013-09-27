package ca.ulaval.glo4003.modules;

import ca.ulaval.glo4003.Bootstrap;
import ca.ulaval.glo4003.StagingBootstrap;
import com.google.inject.AbstractModule;

public class StagingModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new ApplicationModule());

        bind(Bootstrap.class).to(StagingBootstrap.class);
    }
}
