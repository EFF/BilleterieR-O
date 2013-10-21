package ca.ulaval.glo4003.controllers;

import ca.ulaval.glo4003.ConstantsManager;
import ca.ulaval.glo4003.dataaccessobjects.UserDao;
import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.models.User;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class Authentication extends Controller {

    private final UserDao userDao;

    @Inject
    public Authentication(UserDao userDao) {
        this.userDao = userDao;
    }

    public Result index() {
        ObjectNode result = Json.newObject();

        result.put(ConstantsManager.USER_AUTHENTICATED_FIELD_NAME, session().get(ConstantsManager.COOKIE_SESSION_FIELD_NAME) != null);
        result.put(ConstantsManager.USERNAME_FIELD_NAME, session().get(ConstantsManager.COOKIE_SESSION_FIELD_NAME));

        return ok(result);
    }

    public Result login() {
        JsonNode json = request().body().asJson();

        if (!validateLoginParameters(json)) {
            return badRequest("Expected username and password");
        }

        String username = json.get(ConstantsManager.USERNAME_FIELD_NAME).asText();
        String password = json.get(ConstantsManager.PASSWORD_FIELD_NAME).asText();

        try {
            User user = userDao.findByEmailAndPassword(username, password);
            session().clear();
            session().put(ConstantsManager.COOKIE_SESSION_FIELD_NAME, user.getEmail());

            return ok("Authenticated");
        } catch (RecordNotFoundException e) {
            return unauthorized("Bad username/password.");
        }
    }

    public Result logout() {
        session().clear();
        return ok();
    }

    private boolean validateLoginParameters(JsonNode json) {
        return json.has(ConstantsManager.USERNAME_FIELD_NAME) &&
                StringUtils.isNotBlank(json.get(ConstantsManager.USERNAME_FIELD_NAME).asText()) &&
                json.has(ConstantsManager.PASSWORD_FIELD_NAME) &&
                StringUtils.isNotBlank(json.get(ConstantsManager.PASSWORD_FIELD_NAME).asText());
    }
}
