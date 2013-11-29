package ca.ulaval.glo4003.api;

import ca.ulaval.glo4003.api.admin.SecureAction;
import ca.ulaval.glo4003.api.admin.SecureBlocker;
import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

public class ApiModule extends AbstractModule {

    @Override
    protected void configure() {
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(SecureAction.class), new SecureBlocker());
    }
}
