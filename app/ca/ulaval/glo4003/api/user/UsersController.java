package ca.ulaval.glo4003.api.user;

import ca.ulaval.glo4003.api.SecureAction;
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

    private final UsersInteractor usersInteractor;

    @Inject
    public UsersController(UsersInteractor usersInteractor) {
        this.usersInteractor = usersInteractor;
    }

    @SecureAction
    public Result updateEmail() {
        JsonNode json = request().body().asJson();

        if (!validateUpdateEmailParameters(json)) {
            return badRequest(ApiUserConstantsManager.EMAIL_EXPECTED);
        }

        String actualEmail = request().username();
        String newEmail = json.get(ApiUserConstantsManager.USERNAME_FIELD_NAME).asText();

        try {
            usersInteractor.updateEmail(actualEmail, newEmail);
            session().put(ApiUserConstantsManager.COOKIE_EMAIL_FIELD_NAME, newEmail);
            return ok();
        } catch (RecordNotFoundException e) {
            return unauthorized(ApiUserConstantsManager.BAD_SESSION_WRONG_USERNAME);
        } catch (UniqueValidationException e) {
            return unauthorized(ApiUserConstantsManager.EMAIL_SHOULD_BE_UNIQUE);
        } catch (ConstraintViolationException e) {
            return unauthorized(ApiUserConstantsManager.EMAIL_IS_INVALID);
        }
    }

    @SecureAction
    public Result updatePassword() {
        JsonNode json = request().body().asJson();

        if (!validateUpdatePasswordParameters(json)) {
            return badRequest(ApiUserConstantsManager.ACTUAL_AND_NEW_PASSWORD_EXPECTED);
        }

        String email = request().username();
        String actualPassword = json.get(ApiUserConstantsManager.ACTUAL_PASSWORD_FIELD_NAME).asText();
        String newPassword = json.get(ApiUserConstantsManager.PASSWORD_FIELD_NAME).asText();

        try {
            usersInteractor.updatePassword(email, actualPassword, newPassword);
            return ok();
        } catch (RecordNotFoundException e) {
            return unauthorized(ApiUserConstantsManager.BAD_SESSION_WRONG_USERNAME);
        } catch (InvalidActualPasswordException e) {
            return unauthorized(ApiUserConstantsManager.WRONG_ACTUAL_PASSWORD);
        }
    }

    private boolean validateUpdateEmailParameters(JsonNode json) {
        return isNotBlank(json, ApiUserConstantsManager.USERNAME_FIELD_NAME);
    }

    private boolean validateUpdatePasswordParameters(JsonNode json) {
        return isNotBlank(json, ApiUserConstantsManager.ACTUAL_PASSWORD_FIELD_NAME) && isNotBlank(json,
                ApiUserConstantsManager.PASSWORD_FIELD_NAME);
    }

    private boolean isNotBlank(JsonNode json, String fieldName) {
        return json != null && json.has(fieldName) && StringUtils.isNotBlank(json.get(fieldName).asText());
    }
}
