package ca.ulaval.glo4003.api.user;

import ca.ulaval.glo4003.ConstantsManager;
import ca.ulaval.glo4003.api.admin.SecureAction;
import ca.ulaval.glo4003.domain.RecordNotFoundException;
import ca.ulaval.glo4003.domain.user.InvalidActualPasswordException;
import ca.ulaval.glo4003.domain.user.UsersInteractor;
import ca.ulaval.glo4003.persistence.UniqueValidationException;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import play.mvc.Controller;
import play.mvc.Result;

import javax.validation.ConstraintViolationException;

public class UsersController extends Controller {

    public static final String EMAIL_SHOULD_BE_UNIQUE = "Email should be unique";
    public static final String BAD_SESSION_WRONG_USERNAME = "Bad session, wrong username";
    public static final String EMAIL_EXPECTED = "Email expected";
    public static final String ACTUAL_AND_NEW_PASSWORD_EXPECTED = "Actual and new password expected";
    public static final String WRONG_ACTUAL_PASSWORD = "Wrong actual password";
    public static final String EMAIL_IS_INVALID = "Invalid email";
    private final UsersInteractor usersInteractor;

    @Inject
    public UsersController(UsersInteractor usersInteractor) {
        this.usersInteractor = usersInteractor;
    }

    @SecureAction
    public Result updateEmail() {
        JsonNode json = request().body().asJson();

        if (!validateUpdateEmailParameters(json)) {
            return badRequest(EMAIL_EXPECTED);
        }

        String actualEmail = request().username();
        String newEmail = json.get(ConstantsManager.USERNAME_FIELD_NAME).asText();

        try {
            usersInteractor.updateEmail(actualEmail, newEmail);
            session().put(ConstantsManager.COOKIE_EMAIL_FIELD_NAME, newEmail);
            return ok();
        } catch (RecordNotFoundException e) {
            return unauthorized(BAD_SESSION_WRONG_USERNAME);
        } catch (UniqueValidationException e) {
            return unauthorized(EMAIL_SHOULD_BE_UNIQUE);
        } catch (ConstraintViolationException e) {
            return unauthorized(EMAIL_IS_INVALID);
        }
    }

    @SecureAction
    public Result updatePassword() {
        JsonNode json = request().body().asJson();

        if (!validateUpdatePasswordParameters(json)) {
            return badRequest(ACTUAL_AND_NEW_PASSWORD_EXPECTED);
        }

        String email = request().username();
        String actualPassword = json.get(ConstantsManager.ACTUAL_PASSWORD_FIELD_NAME).asText();
        String newPassword = json.get(ConstantsManager.PASSWORD_FIELD_NAME).asText();

        try {
            usersInteractor.updatePassword(email, actualPassword, newPassword);
            return ok();
        } catch (RecordNotFoundException e) {
            return unauthorized(BAD_SESSION_WRONG_USERNAME);
        } catch (InvalidActualPasswordException e) {
            return unauthorized(WRONG_ACTUAL_PASSWORD);
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
