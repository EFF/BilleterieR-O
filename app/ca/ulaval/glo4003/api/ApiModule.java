package ca.ulaval.glo4003.api;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

public class ApiModule extends AbstractModule {

    @Override
    protected void configure() {
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(SecureAction.class), new SecureBlocker());
    }
}
