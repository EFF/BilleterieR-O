package ca.ulaval.glo4003.modules;

import ca.ulaval.glo4003.dataaccessobjects.EventDao;
import ca.ulaval.glo4003.dataaccessobjects.SportDao;
import ca.ulaval.glo4003.services.InMemoryDaoPersistenceService;
import com.google.inject.AbstractModule;

public class ApplicationModule extends AbstractModule {

    @Override
    protected void configure() {

        InMemoryDaoPersistenceService persistenceSvc = new InMemoryDaoPersistenceService();
        EventDao eventDao = new EventDao();
        SportDao sportDao = new SportDao();

        persistenceSvc.restore(eventDao);
        persistenceSvc.restore(sportDao);

        bind(EventDao.class).toInstance(eventDao);
        bind(SportDao.class).toInstance(sportDao);
    }
}
