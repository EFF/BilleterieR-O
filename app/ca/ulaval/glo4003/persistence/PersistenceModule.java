package ca.ulaval.glo4003.persistence;

import ca.ulaval.glo4003.domain.event.EventDao;
import ca.ulaval.glo4003.domain.event.SportDao;
import ca.ulaval.glo4003.domain.event.TeamDao;
import ca.ulaval.glo4003.domain.ticketing.TicketDao;
import ca.ulaval.glo4003.domain.ticketing.TransactionDao;
import ca.ulaval.glo4003.domain.user.UserDao;
import ca.ulaval.glo4003.persistence.event.PersistedEventDao;
import ca.ulaval.glo4003.persistence.event.PersistedSportDao;
import ca.ulaval.glo4003.persistence.event.PersistedTeamDao;
import ca.ulaval.glo4003.persistence.ticketing.PersistedTicketDao;
import ca.ulaval.glo4003.persistence.ticketing.PersistedTransactionDao;
import ca.ulaval.glo4003.persistence.user.PersistedUserDao;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class PersistenceModule extends AbstractModule {

    private final DaoPersistenceService persistenceService;

    public PersistenceModule(DaoPersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    @Override
    protected void configure() {
        bind(EventDao.class).to(PersistedEventDao.class).asEagerSingleton();
        bind(SportDao.class).to(PersistedSportDao.class).asEagerSingleton();
        bind(TeamDao.class).to(PersistedTeamDao.class).asEagerSingleton();
        bind(TicketDao.class).to(PersistedTicketDao.class).asEagerSingleton();
        bind(TransactionDao.class).to(PersistedTransactionDao.class).asEagerSingleton();
        bind(UserDao.class).to(PersistedUserDao.class).asEagerSingleton();
    }

    @Provides
    private DaoPersistenceService providesDaoPersistenceService() {
        return persistenceService;
    }

}
