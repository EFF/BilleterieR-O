package ca.ulaval.glo4003.unittests.controllers;

import ca.ulaval.glo4003.ConstantsManager;
import ca.ulaval.glo4003.controllers.UserProfile;
import ca.ulaval.glo4003.dataaccessobjects.UserDao;
import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.models.User;
import com.google.inject.Inject;
import org.codehaus.jackson.node.ObjectNode;
import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;

import javax.validation.ValidationException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.status;

@RunWith(JukitoRunner.class)
public class UserProfileTest extends BaseControllerTest {

    private static final String A_PASSWORD = "secret";
    private static final String ANOTHER_PASSWORD = "secret2";
    private static final String NEW_PASSWORD = "secret2";
    private static final String AN_EMAIL = "email@test.com";

    @Inject
    private UserProfile userProfile;
    @Inject
    private UserDao mockedUserDao;

    @Test
    public void updateEmailWithWrongUsernameSession() throws RecordNotFoundException {
        addEmailBody();
        when(mockedUserDao.findByEmail(anyString())).thenThrow(RecordNotFoundException.class);

        Result result = userProfile.updateEmail();

        assertEquals(Http.Status.UNAUTHORIZED, status(result));
        assertEquals(UserProfile.BAD_SESSION_WRONG_USERNAME, contentAsString(result));
        verify(mockedUserDao, never()).update(any(User.class));
    }

    @Test
    public void updateEmailWithAUniqueEmail() throws RecordNotFoundException {
        User mockedUser = mock(User.class);
        addEmailBody();
        when(mockedUserDao.findByEmail(anyString())).thenReturn(mockedUser);

        Result result = userProfile.updateEmail();

        assertEquals(Http.Status.OK, status(result));
        verify(mockedUser, times(1)).setEmail(AN_EMAIL);
        verify(mockedUserDao, times(1)).update(mockedUser);
    }

    @Test
    public void updateEmailWithANonUniqueEmail() throws RecordNotFoundException {
        addEmailBody();
        when(mockedUserDao.findByEmail(anyString())).thenThrow(ValidationException.class);

        Result result = userProfile.updateEmail();

        assertEquals(Http.Status.UNAUTHORIZED, status(result));
        assertEquals(UserProfile.EMAIL_SHOULD_BE_UNIQUE, contentAsString(result));
        verify(mockedUserDao, never()).update(any(User.class));
    }

    @Test
    public void updateEmailWithWrongRequestParameters() throws RecordNotFoundException {
        when(mockedBody.asJson()).thenReturn(Json.newObject());

        Result result = userProfile.updateEmail();

        assertEquals(Http.Status.BAD_REQUEST, status(result));
        assertEquals(UserProfile.EMAIL_EXPECTED, contentAsString(result));
        verify(mockedUserDao, never()).update(any(User.class));
    }

    @Test
    public void updatePasswordWithWrongUsernameSession() throws RecordNotFoundException {
        addPasswordBody(A_PASSWORD, NEW_PASSWORD);
        when(mockedUserDao.findByEmail(anyString())).thenThrow(RecordNotFoundException.class);

        Result result = userProfile.updatePassword();

        assertEquals(Http.Status.UNAUTHORIZED, status(result));
        assertEquals(UserProfile.BAD_SESSION_WRONG_USERNAME, contentAsString(result));
        verify(mockedUserDao, never()).update(any(User.class));
    }

    @Test
    public void updatePasswordWithValidActualPassword() throws RecordNotFoundException {
        User mockedUser = mock(User.class);
        addPasswordBody(A_PASSWORD, NEW_PASSWORD);
        when(mockedUser.getPassword()).thenReturn(A_PASSWORD);
        when(mockedUserDao.findByEmail(anyString())).thenReturn(mockedUser);

        Result result = userProfile.updatePassword();

        assertEquals(Http.Status.OK, status(result));
        verify(mockedUserDao, times(1)).update(any(User.class));
    }

    @Test
    public void updatePasswordWithWrongActualPassword() throws RecordNotFoundException {
        User mockedUser = mock(User.class);
        addPasswordBody(A_PASSWORD, NEW_PASSWORD);

        when(mockedUser.getPassword()).thenReturn(ANOTHER_PASSWORD);
        when(mockedUserDao.findByEmail(anyString())).thenReturn(mockedUser);

        Result result = userProfile.updatePassword();

        assertEquals(Http.Status.UNAUTHORIZED, status(result));
        assertEquals(UserProfile.WRONG_ACTUAL_PASSWORD, contentAsString(result));
        verify(mockedUserDao, never()).update(any(User.class));
    }

    @Test
    public void updatePasswordWithWrongRequestParameters() throws RecordNotFoundException {
        when(mockedBody.asJson()).thenReturn(Json.newObject());

        Result result = userProfile.updatePassword();

        assertEquals(Http.Status.BAD_REQUEST, status(result));
        assertEquals(UserProfile.ACTUAL_AND_NEW_PASSWORD_EXPECTED, contentAsString(result));
        verify(mockedUserDao, never()).update(any(User.class));
    }

    private void addPasswordBody(String actualPassword, String newPassword) {
        ObjectNode json = Json.newObject();
        json.put(ConstantsManager.ACTUAL_PASSWORD_FIELD_NAME, actualPassword);
        json.put(ConstantsManager.PASSWORD_FIELD_NAME, newPassword);
        when(mockedBody.asJson()).thenReturn(json);
    }

    private void addEmailBody() {
        ObjectNode json = Json.newObject();
        json.put(ConstantsManager.USERNAME_FIELD_NAME, AN_EMAIL);
        when(mockedBody.asJson()).thenReturn(json);
    }

    public static class TestModule extends JukitoModule {

        @Override
        protected void configureTest() {
            forceMock(UserDao.class);
        }
    }
}
