package controllers;

import play.mvc.*;
import play.libs.Json;
import models.HelloWorld;

public class Application extends Controller {  
    public static Result index() {
    	HelloWorld helloWorld = new HelloWorld();
    	helloWorld.message = "Hello World";
        return ok(Json.toJson(helloWorld));
    }
}
