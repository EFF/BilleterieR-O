package modules;

import com.google.inject.AbstractModule;

import dataaccessobjects.EventDao;
import dataaccessobjects.EventDaoInMemory;
import factories.HelloWorldFactory;
import factories.IHelloWorldFactory;

public class ApplicationModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(IHelloWorldFactory.class).to(HelloWorldFactory.class);
		bind(EventDao.class).to(EventDaoInMemory.class).asEagerSingleton();
	}
}
