package controllers;

import com.google.inject.Inject;

import factories.IHelloWorldFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class Application extends Controller {  
	@Inject
	private IHelloWorldFactory helloWorldFactory;
	
    public Result index() {
    	String name = request().getQueryString("name");
    	if(name == null || name.isEmpty()){
    		name = "world";
    	}
		return ok(Json.toJson(helloWorldFactory.createHelloWorld(name)));
    }
}
