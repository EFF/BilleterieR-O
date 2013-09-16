package factories;

import models.HelloWorld;

public interface IHelloWorldFactory {
	public HelloWorld createHelloWorld(String name);
}
