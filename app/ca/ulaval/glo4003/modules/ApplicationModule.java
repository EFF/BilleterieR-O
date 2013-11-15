package ca.ulaval.glo4003.modules;

import ca.ulaval.glo4003.dataaccessobjects.*;
import ca.ulaval.glo4003.services.DaoPersistenceService;
import ca.ulaval.glo4003.services.*;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class ApplicationModule extends AbstractModule {

    private DaoPersistenceService persistenceService;

    public ApplicationModule(DaoPersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    @Override
    protected void configure() {
        bind(EmailService.class).to(LocalSMTPEmailService.class);
        bind(CheckoutService.class).to(ConcreteCheckoutService.class);

        bind(EventDao.class).asEagerSingleton();
        bind(SportDao.class).asEagerSingleton();
        bind(UserDao.class).asEagerSingleton();
        bind(TicketDao.class).asEagerSingleton();
        bind(TeamDao.class).asEagerSingleton();
        bind(TransactionDao.class).asEagerSingleton();
    }

    @Provides
    private DaoPersistenceService provideDaoPersistenceService() {
        return persistenceService;
    }
}
