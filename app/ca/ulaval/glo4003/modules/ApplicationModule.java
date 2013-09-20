package ca.ulaval.glo4003.modules;

import ca.ulaval.glo4003.dataaccessobjects.EventDao;
import ca.ulaval.glo4003.dataaccessobjects.EventDaoInMemory;
import com.google.inject.AbstractModule;

public class ApplicationModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(EventDao.class).to(EventDaoInMemory.class).asEagerSingleton();
    }
}
