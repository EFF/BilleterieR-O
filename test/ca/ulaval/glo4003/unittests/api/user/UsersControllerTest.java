package ca.ulaval.glo4003.unittests.api.user;

import ca.ulaval.glo4003.ConstantsManager;
import ca.ulaval.glo4003.api.user.UsersController;
import ca.ulaval.glo4003.persistence.UniqueValidationException;
import ca.ulaval.glo4003.domain.user.UserDao;
import ca.ulaval.glo4003.domain.user.InvalidActualPasswordException;
import ca.ulaval.glo4003.domain.RecordNotFoundException;
import ca.ulaval.glo4003.domain.user.UsersInteractor;
import ca.ulaval.glo4003.domain.user.User;
import ca.ulaval.glo4003.unittests.api.BaseControllerTest;
import com.google.inject.Inject;
import org.codehaus.jackson.node.ObjectNode;
import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.status;

@RunWith(JukitoRunner.class)
public class UsersControllerTest extends BaseControllerTest {

    private static final String A_PASSWORD = "secret";
    private static final String NEW_PASSWORD = "secret2";
    private static final String AN_EMAIL = "email@test.com";
    private static final String INVALID_EMAIL = "invalid";
    @Inject
    private UsersController usersController;
    @Inject
    private UserDao mockedUserDao;

    @Test
    public void updateEmailWithWrongUsernameSession(UsersInteractor mockedUsersInteractor) throws RecordNotFoundException {
        addEmailBody(AN_EMAIL);
        doThrow(new RecordNotFoundException()).when(mockedUsersInteractor).updateEmail(anyString(),
                anyString());

        Result result = usersController.updateEmail();

        assertEquals(Http.Status.UNAUTHORIZED, status(result));
        assertEquals(UsersController.BAD_SESSION_WRONG_USERNAME, contentAsString(result));
    }

    @Test
    public void updateEmailWithAUniqueEmail() throws RecordNotFoundException {
        addEmailBody(AN_EMAIL);

        Result result = usersController.updateEmail();

        assertEquals(Http.Status.OK, status(result));
    }

    @Test
    public void updateEmailWithANonUniqueEmail(UsersInteractor mockedUsersInteractor) throws RecordNotFoundException {
        addEmailBody(AN_EMAIL);
        doThrow(new UniqueValidationException()).when(mockedUsersInteractor).updateEmail(anyString(),
                anyString());

        Result result = usersController.updateEmail();

        assertEquals(Http.Status.UNAUTHORIZED, status(result));
        assertEquals(UsersController.EMAIL_SHOULD_BE_UNIQUE, contentAsString(result));
    }

    @Test
    public void updateEmailWithInvalidEmail(UsersInteractor mockedUsersInteractor) throws RecordNotFoundException {
        addEmailBody(INVALID_EMAIL);
        doThrow(new ConstraintViolationException(new HashSet<ConstraintViolation<?>>())).when(mockedUsersInteractor)
                .updateEmail(anyString(),
                        anyString());

        Result result = usersController.updateEmail();

        assertEquals(Http.Status.UNAUTHORIZED, status(result));
        assertEquals(UsersController.EMAIL_IS_INVALID, contentAsString(result));
    }

    @Test
    public void updateEmailWithWrongRequestParameters() throws RecordNotFoundException {
        when(mockedBody.asJson()).thenReturn(Json.newObject());

        Result result = usersController.updateEmail();

        assertEquals(Http.Status.BAD_REQUEST, status(result));
        assertEquals(UsersController.EMAIL_EXPECTED, contentAsString(result));
    }

    @Test
    public void updatePasswordWithWrongUsernameSession(UsersInteractor mockedUsersInteractor) throws
            RecordNotFoundException, InvalidActualPasswordException {
        addPasswordBody(A_PASSWORD, NEW_PASSWORD);
        doThrow(new RecordNotFoundException()).when(mockedUsersInteractor).updatePassword(anyString(), anyString(), anyString());

        Result result = usersController.updatePassword();

        assertEquals(Http.Status.UNAUTHORIZED, status(result));
        assertEquals(UsersController.BAD_SESSION_WRONG_USERNAME, contentAsString(result));
    }

    @Test
    public void updatePasswordWithValidActualPassword() throws RecordNotFoundException {
        addPasswordBody(A_PASSWORD, NEW_PASSWORD);

        Result result = usersController.updatePassword();

        assertEquals(Http.Status.OK, status(result));
    }

    @Test
    public void updatePasswordWithWrongActualPassword(UsersInteractor mockedUsersInteractor) throws
            RecordNotFoundException, InvalidActualPasswordException {
        addPasswordBody(A_PASSWORD, NEW_PASSWORD);

        doThrow(new InvalidActualPasswordException()).when(mockedUsersInteractor).updatePassword(anyString(), anyString(), anyString());

        Result result = usersController.updatePassword();

        assertEquals(Http.Status.UNAUTHORIZED, status(result));
        assertEquals(UsersController.WRONG_ACTUAL_PASSWORD, contentAsString(result));
    }

    @Test
    public void updatePasswordWithWrongRequestParameters() throws RecordNotFoundException {
        when(mockedBody.asJson()).thenReturn(Json.newObject());

        Result result = usersController.updatePassword();

        assertEquals(Http.Status.BAD_REQUEST, status(result));
        assertEquals(UsersController.ACTUAL_AND_NEW_PASSWORD_EXPECTED, contentAsString(result));
        verify(mockedUserDao, never()).update(any(User.class));
    }

    private void addPasswordBody(String actualPassword, String newPassword) {
        ObjectNode json = Json.newObject();
        json.put(ConstantsManager.ACTUAL_PASSWORD_FIELD_NAME, actualPassword);
        json.put(ConstantsManager.PASSWORD_FIELD_NAME, newPassword);
        when(mockedBody.asJson()).thenReturn(json);
    }

    private void addEmailBody(String email) {
        ObjectNode json = Json.newObject();
        json.put(ConstantsManager.USERNAME_FIELD_NAME, email);
        when(mockedBody.asJson()).thenReturn(json);
    }

    public static class TestModule extends JukitoModule {

        @Override
        protected void configureTest() {
            forceMock(UsersInteractor.class);
        }
    }
}
