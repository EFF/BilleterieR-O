package modules;

import com.google.inject.AbstractModule;

import dataaccessobjects.EventDao;
import dataaccessobjects.TestEventDaoInMemory;
import factories.HelloWorldFactory;
import factories.IHelloWorldFactory;

public class TestModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(IHelloWorldFactory.class).to(HelloWorldFactory.class);
		bind(EventDao.class).to(TestEventDaoInMemory.class);
	}
}
