package ca.ulaval.glo4003.controllers;

import ca.ulaval.glo4003.exceptions.InvalidJsonException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import static ca.ulaval.glo4003.helpers.JsonExpectationsHelper.expectFilledString;

public class Authentication extends Controller {

    public Result index() {
        ObjectNode result = Json.newObject();

        result.put("authenticated", session().get("email") != null);
        result.put("username", session().get("email"));

        return ok(result);
    }

    public Result login() {
        JsonNode json = request().body().asJson();

        try {
            expectFilledString(json, "username", "password");
        } catch (InvalidJsonException e) {
            return badRequest("Expected username and password in Json.");
        }

        String username = json.findPath("username").getTextValue();
        String password = json.findPath("password").getTextValue();

        if (username.equals("user") && password.equals("password")) {
            session().clear();
            session().put("email", username);

            return ok("Authenticated");

        } else {
            return unauthorized("Bad username/password.");
        }

    }

    public Result logout() {
        session().clear();
        return ok();
    }
}
