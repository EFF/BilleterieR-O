package ca.ulaval.glo4003.controllers;

import ca.ulaval.glo4003.ConstantsManager;
import ca.ulaval.glo4003.dataaccessobjects.UserDao;
import ca.ulaval.glo4003.exceptions.AuthenticationException;
import ca.ulaval.glo4003.interactors.AuthenticationInteractor;
import ca.ulaval.glo4003.models.Credentials;
import ca.ulaval.glo4003.models.User;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class AuthenticationController extends Controller {

    public final static String BAD_CREDENTIALS_MESSAGE = "Mauvaise combinaison email/mot de passe";
    public final static String AUTHENTICATION_SUCCESS_MESSAGE = "Authentification réussitte";
    public final static String WRONG_AUTHENTIFICATION_PARAMETERS = "Expected username and password";
    private final AuthenticationInteractor authenticationInteractor;

    @Inject
    public AuthenticationController(AuthenticationInteractor authenticationInteractor) {
        this.authenticationInteractor = authenticationInteractor;
    }

    public Result index() {
        ObjectNode result = Json.newObject();

        result.put(ConstantsManager.USER_AUTHENTICATED_FIELD_NAME, session().get(ConstantsManager
                .COOKIE_SESSION_FIELD_NAME) != null);
        result.put(ConstantsManager.USERNAME_FIELD_NAME, session().get(ConstantsManager.COOKIE_SESSION_FIELD_NAME));

        return ok(result);
    }

    public Result login() {
        JsonNode jsonBody = request().body().asJson();

        if (jsonBody != null && !validateLoginParameters(jsonBody)) {
            return badRequest(WRONG_AUTHENTIFICATION_PARAMETERS);
        }

        Credentials credentials = extractCredentialsFromRequest(jsonBody);

        try {
            User user = authenticationInteractor.authenticate(credentials);
            session().clear();
            session().put(ConstantsManager.COOKIE_SESSION_FIELD_NAME, user.getEmail());
            return ok(AUTHENTICATION_SUCCESS_MESSAGE);
        } catch (AuthenticationException ignored) {
            return unauthorized(BAD_CREDENTIALS_MESSAGE);
        }
    }

    private Credentials extractCredentialsFromRequest(JsonNode jsonBody) {
        String username = jsonBody.get(ConstantsManager.USERNAME_FIELD_NAME).asText();
        String password = jsonBody.get(ConstantsManager.PASSWORD_FIELD_NAME).asText();

        Credentials credentials = new Credentials();
        credentials.setEmail(username);
        credentials.setPassword(password);
        return credentials;
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
