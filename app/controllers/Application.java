package controllers;

import com.google.inject.Inject;

import factories.HelloWorldFactoryInterface;
import play.mvc.*;
import play.libs.Json;

public class Application extends Controller {  
	@Inject HelloWorldFactoryInterface helloWorldFactory;
	
    public Result index() {
    	String name = request().getQueryString("name");
    	if(name == null || name.isEmpty())
    		name = "world";
        return ok(Json.toJson(helloWorldFactory.createHelloWorld(name)));
    }
}
