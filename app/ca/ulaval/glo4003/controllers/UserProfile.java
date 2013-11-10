package ca.ulaval.glo4003.controllers;

import ca.ulaval.glo4003.ConstantsManager;
import ca.ulaval.glo4003.Secured;
import ca.ulaval.glo4003.dataaccessobjects.UserDao;
import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.models.User;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

public class UserProfile extends Controller {

    private final UserDao userDao;

    @Inject
    public UserProfile(UserDao userDao) {
        this.userDao = userDao;
    }

    @Security.Authenticated(Secured.class)
    public Result updateEmail() {
        JsonNode json = request().body().asJson();

        if (!validateUpdateEmailParameters(json)) {
            return badRequest("Email expected");
        }

        String actualEmail = session().get(ConstantsManager.COOKIE_SESSION_FIELD_NAME);
        String newEmail = json.get(ConstantsManager.USERNAME_FIELD_NAME).asText();

        try {
            User user = userDao.findByEmail(actualEmail);
            user.setEmail(newEmail);
            // TODO: Email should be unique
            userDao.update(user);
            session().put(ConstantsManager.COOKIE_SESSION_FIELD_NAME, newEmail);
            return ok();
        } catch (RecordNotFoundException e) {
            return unauthorized();
        }
    }

    @Security.Authenticated(Secured.class)
    public Result updatePassword() {
        JsonNode json = request().body().asJson();

        if (!validateUpdatePasswordParameters(json)) {
            return badRequest("Actual and new password expected");
        }

        String email = session().get(ConstantsManager.COOKIE_SESSION_FIELD_NAME);
        String actualPassword = json.get(ConstantsManager.ACTUAL_PASSWORD_FIELD_NAME).asText();
        String newPassword = json.get(ConstantsManager.PASSWORD_FIELD_NAME).asText();
        String confirmationPassword = json.get(ConstantsManager.PASSWORD_CONFIRMATION_FIELD_NAME).asText();

        if(!newPassword.equals(confirmationPassword)) {
            // TODO: Is it the best HTTP error
            return unauthorized();
        }

        try {
            User user = userDao.findByEmail(email);

            if(!user.getPassword().equals(actualPassword)) {
                return unauthorized();
            }

            user.setPassword(newPassword);
            userDao.update(user);
            return ok();
        } catch (RecordNotFoundException e) {
            return unauthorized();
        }

    }

    private boolean validateUpdateEmailParameters(JsonNode json) {
        return isNotBlank(json, ConstantsManager.USERNAME_FIELD_NAME);
    }

    private boolean validateUpdatePasswordParameters(JsonNode json) {
        return isNotBlank(json, ConstantsManager.ACTUAL_PASSWORD_FIELD_NAME) && isNotBlank(json,
                ConstantsManager.PASSWORD_FIELD_NAME) && isNotBlank(json,
                ConstantsManager.PASSWORD_CONFIRMATION_FIELD_NAME);
    }

    private boolean isNotBlank(JsonNode json, String fieldName) {
        return json != null && json.has(fieldName) && StringUtils.isNotBlank(json.get(fieldName).asText());
    }
}
