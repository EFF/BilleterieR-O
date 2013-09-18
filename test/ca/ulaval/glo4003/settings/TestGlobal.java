package ca.ulaval.glo4003.settings;

import ca.ulaval.glo4003.modules.TestModule;
import play.GlobalSettings;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class TestGlobal extends GlobalSettings {

	private static final Injector INJECTOR = createInjector();

	@Override
	public <A> A getControllerInstance(Class<A> controllerClass)
			throws Exception {
		return INJECTOR.getInstance(controllerClass);
	}

	private static Injector createInjector() {
		return Guice.createInjector(new TestModule());
	}
}
