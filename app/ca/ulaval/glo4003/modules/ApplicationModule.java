package ca.ulaval.glo4003.modules;

import ca.ulaval.glo4003.dataaccessobjects.EventDao;
import ca.ulaval.glo4003.dataaccessobjects.SportDao;
import ca.ulaval.glo4003.models.Event;
import ca.ulaval.glo4003.services.DaoPersistenceService;
import ca.ulaval.glo4003.services.FileBasedDaoPersistenceService;
import ca.ulaval.glo4003.services.InMemoryDaoPersistenceService;
import com.google.inject.AbstractModule;

import java.util.List;

public class ApplicationModule extends AbstractModule {

    @Override
    protected void configure() {

        DaoPersistenceService persistenceSvc = new FileBasedDaoPersistenceService("Production");
        EventDao eventDao = new EventDao(persistenceSvc);
        SportDao sportDao = new SportDao(persistenceSvc);

        bind(EventDao.class).toInstance(eventDao);
        bind(SportDao.class).toInstance(sportDao);
    }
}
