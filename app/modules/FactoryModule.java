package modules;

import com.google.inject.AbstractModule;

import factories.HelloWorldFactory;
import factories.HelloWorldFactoryInterface;

public class FactoryModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(HelloWorldFactoryInterface.class).to(HelloWorldFactory.class);
	}

}
