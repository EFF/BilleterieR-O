package ca.ulaval.glo4003.modules;

import ca.ulaval.glo4003.dataaccessobjects.EventDao;
import ca.ulaval.glo4003.dataaccessobjects.TestEventDaoInMemory;
import com.google.inject.AbstractModule;

public class TestModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(EventDao.class).to(TestEventDaoInMemory.class);
    }
}