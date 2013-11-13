package ca.ulaval.glo4003.modules;

import ca.ulaval.glo4003.dataaccessobjects.EventDao;
import ca.ulaval.glo4003.dataaccessobjects.SportDao;
import ca.ulaval.glo4003.dataaccessobjects.TransactionDao;
import ca.ulaval.glo4003.dataaccessobjects.UserDao;
import ca.ulaval.glo4003.services.*;
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
        TransactionDao transactionDao = new TransactionDao(this.persistenceService);

        bind(EventDao.class).toInstance(eventDao);
        bind(SportDao.class).toInstance(sportDao);
        bind(UserDao.class).toInstance(userDao);
        bind(UserDao.class).toInstance(userDao);
        bind(TransactionDao.class).toInstance(transactionDao);

        bind(EmailService.class).to(ConcreteEmailService.class);
        bind(CheckoutService.class).to(ConcreteCheckoutService.class);
    }
}
