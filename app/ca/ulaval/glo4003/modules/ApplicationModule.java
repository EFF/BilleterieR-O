package ca.ulaval.glo4003.modules;

import ca.ulaval.glo4003.dataaccessobjects.EventDao;
import ca.ulaval.glo4003.dataaccessobjects.SportDao;
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

        bind(EventDao.class).toInstance(eventDao);
        bind(SportDao.class).toInstance(sportDao);
    }
}
