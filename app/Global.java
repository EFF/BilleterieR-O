import play.GlobalSettings;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import factories.HelloWorldFactory;
import factories.HelloWorldFactoryInterface;

public class Global extends GlobalSettings {

  private static final Injector INJECTOR = createInjector();
  
  @Override
  public <A> A getControllerInstance(Class<A> controllerClass) throws Exception {
    return INJECTOR.getInstance(controllerClass);
  }

  private static Injector createInjector() {
    return Guice.createInjector(new AbstractModule() {
        @Override
        protected void configure() {
            bind(HelloWorldFactoryInterface.class).to(HelloWorldFactory.class);
        }
    });
  }

}
