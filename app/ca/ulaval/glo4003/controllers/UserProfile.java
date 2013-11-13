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

import javax.validation.ValidationException;

public class UserProfile extends Controller {

    public static final String EMAIL_SHOULD_BE_UNIQUE = "Email should be unique";
    public static final String BAD_SESSION_WRONG_USERNAME = "Bad session, wrong username";
    public static final String EMAIL_EXPECTED = "Email expected";
    public static final String ACTUAL_AND_NEW_PASSWORD_EXPECTED = "Actual and new password expected";
    public static final String WRONG_ACTUAL_PASSWORD = "Wrong actual password";

    private final UserDao userDao;

    @Inject
    public UserProfile(UserDao userDao) {
        this.userDao = userDao;
    }

    @Security.Authenticated(Secured.class)
    public Result updateEmail() {
        JsonNode json = request().body().asJson();

        if (!validateUpdateEmailParameters(json)) {
            return badRequest(EMAIL_EXPECTED);
        }

        String actualEmail = session().get(ConstantsManager.COOKIE_SESSION_FIELD_NAME);
        String newEmail = json.get(ConstantsManager.USERNAME_FIELD_NAME).asText();

        try {
            User user = userDao.findByEmail(actualEmail);
            user.setEmail(newEmail);
            userDao.update(user);
            session().put(ConstantsManager.COOKIE_SESSION_FIELD_NAME, newEmail);
            return ok();
        } catch (RecordNotFoundException e) {
            return unauthorized(BAD_SESSION_WRONG_USERNAME);
        } catch (ValidationException e) {
            return unauthorized(EMAIL_SHOULD_BE_UNIQUE);
        }
    }

    @Security.Authenticated(Secured.class)
    public Result updatePassword() {
        JsonNode json = request().body().asJson();

        if (!validateUpdatePasswordParameters(json)) {
            return badRequest(ACTUAL_AND_NEW_PASSWORD_EXPECTED);
        }

        String email = session().get(ConstantsManager.COOKIE_SESSION_FIELD_NAME);
        String actualPassword = json.get(ConstantsManager.ACTUAL_PASSWORD_FIELD_NAME).asText();
        String newPassword = json.get(ConstantsManager.PASSWORD_FIELD_NAME).asText();

        try {
            User user = userDao.findByEmail(email);

            if(!user.getPassword().equals(actualPassword)) {
                return unauthorized(WRONG_ACTUAL_PASSWORD);
            }

            user.setPassword(newPassword);
            userDao.update(user);
            return ok();
        } catch (RecordNotFoundException e) {
            return unauthorized(BAD_SESSION_WRONG_USERNAME);
        }
    }

    private boolean validateUpdateEmailParameters(JsonNode json) {
        return isNotBlank(json, ConstantsManager.USERNAME_FIELD_NAME);
    }

    private boolean validateUpdatePasswordParameters(JsonNode json) {
        return isNotBlank(json, ConstantsManager.ACTUAL_PASSWORD_FIELD_NAME) && isNotBlank(json,
                ConstantsManager.PASSWORD_FIELD_NAME);
    }

    private boolean isNotBlank(JsonNode json, String fieldName) {
        return json != null && json.has(fieldName) && StringUtils.isNotBlank(json.get(fieldName).asText());
    }
}
