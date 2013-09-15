package factories;

import models.HelloWorld;

public class HelloWorldFactory implements HelloWorldFactoryInterface {

	@Override
	public HelloWorld createHelloWorld(String name) {
		HelloWorld helloWorld = new HelloWorld();
		helloWorld.message = String.format("Hello %s!", name);
		return helloWorld;
	}

}
