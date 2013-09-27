package ca.ulaval.glo4003.modules;

import ca.ulaval.glo4003.Bootstrap;
import ca.ulaval.glo4003.TestBootstrap;
import com.google.inject.AbstractModule;

public class TestModule extends AbstractModule {

    @Override
    protected void configure() {
        install(new ApplicationModule());

        bind(Bootstrap.class).to(TestBootstrap.class);
    }
}
