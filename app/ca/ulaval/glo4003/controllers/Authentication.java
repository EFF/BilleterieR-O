package ca.ulaval.glo4003.controllers;

import ca.ulaval.glo4003.dataaccessobjects.UserDao;
import ca.ulaval.glo4003.exceptions.InvalidJsonException;
import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.models.User;
import com.google.inject.Inject;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import static ca.ulaval.glo4003.helpers.JsonExpectationsHelper.expectFilledString;

public class Authentication extends Controller {

    private final UserDao userDao;

    public static final String emailTag = "email";

    @Inject
    public Authentication(UserDao userDao) {
        this.userDao = userDao;
    }

    public Result index() {
        ObjectNode result = Json.newObject();

        result.put("authenticated", session().get(emailTag) != null);
        result.put("username", session().get(emailTag));

        return ok(result);
    }

    public Result login() {
        JsonNode json = request().body().asJson();

        try {
            expectFilledString(json, "username", "password");
        } catch (InvalidJsonException e) {
            return badRequest("Expected username and password in Json.");
        }

        String username = json.get("username").asText();
        String password = json.get("password").asText();

        try {
            User user = userDao.findByEmailAndPassword(username, password);
            session().clear();
            session().put(emailTag, user.getEmail());

            return ok("Authenticated");
        } catch (RecordNotFoundException e) {
            return unauthorized("Bad username/password.");
        }
    }

    public Result logout() {
        session().clear();
        return ok();
    }
}
