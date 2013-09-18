package ca.ulaval.glo4003.modules;

import com.google.inject.AbstractModule;

import ca.ulaval.glo4003.dataaccessobjects.EventDao;
import ca.ulaval.glo4003.dataaccessobjects.TestEventDaoInMemory;

public class TestModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(EventDao.class).to(TestEventDaoInMemory.class);
	}
}
