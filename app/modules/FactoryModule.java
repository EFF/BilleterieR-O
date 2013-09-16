package modules;

import com.google.inject.AbstractModule;

import factories.HelloWorldFactory;
import factories.IHelloWorldFactory;

public class FactoryModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(IHelloWorldFactory.class).to(HelloWorldFactory.class);
	}

}
