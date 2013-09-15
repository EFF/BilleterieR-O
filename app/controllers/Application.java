package controllers;

import com.google.inject.Inject;

import factories.HelloWorldFactoryInterface;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class Application extends Controller {  
	@Inject
	private HelloWorldFactoryInterface helloWorldFactory;
	
    public Result index() {
    	String name = request().getQueryString("name");
    	if(name == null || name.isEmpty()){
    		name = "world";
    	}
		return ok(Json.toJson(helloWorldFactory.createHelloWorld(name)));
    }
}
