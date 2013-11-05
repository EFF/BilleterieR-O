package ca.ulaval.glo4003.modules;

import ca.ulaval.glo4003.dataaccessobjects.EventDao;
import ca.ulaval.glo4003.dataaccessobjects.SportDao;
import ca.ulaval.glo4003.dataaccessobjects.TicketDao;
import ca.ulaval.glo4003.dataaccessobjects.UserDao;
import ca.ulaval.glo4003.services.DaoPersistenceService;
import com.google.inject.AbstractModule;

public class ApplicationModule extends AbstractModule {

    private DaoPersistenceService persistenceService;

    public ApplicationModule(DaoPersistenceService persistenceService) {

        this.persistenceService = persistenceService;
    }

    @Override
    protected void configure() {
        EventDao eventDao = new EventDao(this.persistenceService);
        SportDao sportDao = new SportDao(this.persistenceService);
        UserDao userDao = new UserDao(this.persistenceService);
        TicketDao ticketDao = new TicketDao(this.persistenceService);

        bind(EventDao.class).toInstance(eventDao);
        bind(SportDao.class).toInstance(sportDao);
        bind(UserDao.class).toInstance(userDao);
        bind(TicketDao.class).toInstance(ticketDao);
    }
}
