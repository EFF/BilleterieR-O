package ca.ulaval.glo4003.controllers;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class Authentication extends Controller {

    public Result index() {
        ObjectNode result = Json.newObject();

        result.put("authenticated", session().get("email") != null);
        result.put("username", session().get("email"));

        return ok(result);
    }

    public Result login() {
        JsonNode json = request().body().asJson();

        String username = json.findPath("username").getTextValue();
        String password = json.findPath("password").getTextValue();

        if (username.equals("user") && password.equals("password")) {
            session().clear();
            session().put("email", username);

            return ok("SUCCESS");

        } else {
            return internalServerError("Bad email / password." + username + "/" + password);
        }

    }

    public Result logout() {
        session().clear();
        return ok();
    }
}
