package ca.ulaval.glo4003.unittests.controllers;

import ca.ulaval.glo4003.controllers.Authentication;
import ca.ulaval.glo4003.dataaccessobjects.UserDao;
import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.models.User;
import com.google.inject.Inject;
import org.codehaus.jackson.node.ObjectNode;
import org.jukito.JukitoRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static play.test.Helpers.status;

@RunWith(JukitoRunner.class)
public class AuthenticationTest extends BaseControllerTest {
    @Inject
    private Authentication authentication;

    private User mockedUser;

    private String username = "username";
    private String password = "password";
    private String email = "email@test.com";

    @Before
    public void setup() {
        mockedUser = mock(User.class);
        when(mockedUser.getEmail()).thenReturn(email);

        ObjectNode json = Json.newObject();
        json.put("username", username);
        json.put("password", password);

        when(mockedBody.asJson()).thenReturn(json);
    }

    @Test
    public void index() {
        Result result = authentication.index();

        verify(mockedSession, times(2)).get(Authentication.emailTag);

        assertEquals(Http.Status.OK, status(result));
    }

    @Test
    public void logout() {
        Result result = authentication.logout();

        verify(mockedSession).clear();

        assertEquals(Http.Status.OK, status(result));
    }

    @Test
    public void loginWithRegisteredUser(UserDao mockedUserDao) throws RecordNotFoundException {
        when(mockedUserDao.findByEmailAndPassword(anyString(), anyString()))
                .thenReturn(mockedUser);

        Result result = authentication.login();

        verify(mockedBody).asJson();
        verify(mockedRequest).body();
        verify(mockedUserDao).findByEmailAndPassword(anyString(), anyString());
        verify(mockedUser).getEmail();

        InOrder inOrder = inOrder(mockedSession);
        inOrder.verify(mockedSession).clear();
        inOrder.verify(mockedSession).put("email", email);

        assertEquals(Http.Status.OK, status(result));
    }

    @Test
    public void loginWithUnregisteredUser(UserDao mockedUserDao) throws RecordNotFoundException {
        when(mockedUserDao.findByEmailAndPassword(anyString(), anyString()))
                .thenThrow(new RecordNotFoundException());

        Result result = authentication.login();

        verify(mockedBody).asJson();
        verify(mockedRequest).body();
        verify(mockedUserDao).findByEmailAndPassword(anyString(), anyString());
        verify(mockedUser, never()).getEmail();

        verify(mockedSession, never()).clear();
        verify(mockedSession, never()).put("email", email);

        assertEquals(Http.Status.UNAUTHORIZED, status(result));
    }

    @Test
    public void loginWithEmptyBody(UserDao mockedUserDao) throws RecordNotFoundException {
        when(mockedBody.asJson()).thenReturn(Json.newObject());

        Result result = authentication.login();

        verify(mockedBody).asJson();
        verify(mockedRequest).body();
        verify(mockedUserDao, never()).findByEmailAndPassword(anyString(), anyString());
        verify(mockedUser, never()).getEmail();

        verify(mockedSession, never()).clear();
        verify(mockedSession, never()).put("email", email);

        assertEquals(Http.Status.BAD_REQUEST, status(result));
    }
}
