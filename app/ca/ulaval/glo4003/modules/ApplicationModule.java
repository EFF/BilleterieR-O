package ca.ulaval.glo4003.modules;

import ca.ulaval.glo4003.dataaccessobjects.*;
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
        // TODO: Bind to FakeEmailService in test and staging. Bind to LocalSMTPEmailService in prod
        bind(EmailService.class).to(FakeEmailService.class);
        bind(CheckoutService.class).to(ConcreteCheckoutService.class);

        bind(EventDao.class).to(PersistedEventDao.class).asEagerSingleton();
        bind(SportDao.class).to(PersistedSportDao.class).asEagerSingleton();
        bind(TeamDao.class).to(PersistedTeamDao.class).asEagerSingleton();
        bind(TicketDao.class).to(PersistedTicketDao.class).asEagerSingleton();
        bind(TransactionDao.class).to(PersistedTransactionDao.class).asEagerSingleton();
        bind(UserDao.class).asEagerSingleton();
    }

    @Provides
    private DaoPersistenceService providesDaoPersistenceService() {
        return persistenceService;
    }
}
