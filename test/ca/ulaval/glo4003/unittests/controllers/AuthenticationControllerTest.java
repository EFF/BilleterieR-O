package ca.ulaval.glo4003.unittests.controllers;

import ca.ulaval.glo4003.ConstantsManager;
import ca.ulaval.glo4003.api.user.AuthenticationController;
import ca.ulaval.glo4003.domain.user.UserDao;
import ca.ulaval.glo4003.domain.RecordNotFoundException;
import ca.ulaval.glo4003.domain.user.User;
import com.google.inject.Inject;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import org.jukito.JukitoRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static play.test.Helpers.status;

@RunWith(JukitoRunner.class)
public class AuthenticationControllerTest extends BaseControllerTest {
    @Inject
    private AuthenticationController authenticationController;
    private User mockedUser;
    private String email = "email@test.com";
    private String password = "password";

    @Before
    public void setup() {
        mockedUser = mock(User.class);
        when(mockedUser.getEmail()).thenReturn(email);
        when(mockedUser.getPassword()).thenReturn(password);

        ObjectNode json = Json.newObject();
        json.put(ConstantsManager.USERNAME_FIELD_NAME, email);
        json.put(ConstantsManager.PASSWORD_FIELD_NAME, password);

        when(mockedBody.asJson()).thenReturn(json);
    }

    @Test
    public void indexWhenAuthenticated() {
        when(mockedSession.get(ConstantsManager.COOKIE_SESSION_FIELD_NAME)).thenReturn(email);

        Result result = authenticationController.index();

        verify(mockedSession, atLeast(1)).get(ConstantsManager.COOKIE_SESSION_FIELD_NAME);

        assertEquals(Http.Status.OK, status(result));

        assertEquals("application/json", Helpers.contentType(result));

        String json = Helpers.contentAsString(result);
        JsonNode jsonNode = Json.parse(json);

        assertTrue(jsonNode.isContainerNode());
        JsonNode authenticatedValue = jsonNode.get("authenticated");
        JsonNode username1 = jsonNode.get("username");

        assertTrue(authenticatedValue.asBoolean());
        assertEquals(email, username1.asText());
    }

    @Test
    public void indexWhenNotAuthenticated() {
        when(mockedSession.get(ConstantsManager.COOKIE_SESSION_FIELD_NAME)).thenReturn(null);

        Result result = authenticationController.index();

        verify(mockedSession, atLeast(1)).get(ConstantsManager.COOKIE_SESSION_FIELD_NAME);

        assertEquals(Http.Status.OK, status(result));

        assertEquals("application/json", Helpers.contentType(result));

        String json = Helpers.contentAsString(result);
        JsonNode jsonNode = Json.parse(json);

        assertTrue(jsonNode.isContainerNode());
        JsonNode authenticatedValue = jsonNode.get(ConstantsManager.USER_AUTHENTICATED_FIELD_NAME);
        JsonNode username1 = jsonNode.get(ConstantsManager.USERNAME_FIELD_NAME);

        assertFalse(authenticatedValue.asBoolean());
        assertTrue(username1.isNull());
    }

    @Test
    public void logout() {
        Result result = authenticationController.logout();

        verify(mockedSession).clear();

        assertEquals(Http.Status.OK, status(result));
    }

    @Test
    public void loginWithRegisteredUser(UserDao mockedUserDao) throws RecordNotFoundException {
        when(mockedUserDao.findByEmail(email))
                .thenReturn(mockedUser);

        Result result = authenticationController.login();

        verify(mockedBody).asJson();
        verify(mockedRequest).body();
        verify(mockedUserDao).findByEmail(anyString());
        verify(mockedUser).getEmail();

        InOrder inOrder = inOrder(mockedSession);
        inOrder.verify(mockedSession).clear();
        inOrder.verify(mockedSession).put(ConstantsManager.COOKIE_SESSION_FIELD_NAME, email);

        assertEquals(Http.Status.OK, status(result));
    }

    @Test
    public void loginWithUnregisteredUser(UserDao mockedUserDao) throws RecordNotFoundException {
        when(mockedUserDao.findByEmail(anyString()))
                .thenThrow(new RecordNotFoundException());

        Result result = authenticationController.login();

        verify(mockedBody).asJson();
        verify(mockedRequest).body();
        verify(mockedUserDao).findByEmail(anyString());
        verify(mockedUser, never()).getEmail();

        verify(mockedSession, never()).clear();
        verify(mockedSession, never()).put(ConstantsManager.COOKIE_SESSION_FIELD_NAME, email);

        assertEquals(Http.Status.UNAUTHORIZED, status(result));
    }

    @Test
    public void loginWithEmptyBody(UserDao mockedUserDao) throws RecordNotFoundException {
        when(mockedBody.asJson()).thenReturn(Json.newObject());

        Result result = authenticationController.login();

        verify(mockedBody).asJson();
        verify(mockedRequest).body();
        verify(mockedUserDao, never()).findByEmail(anyString());
        verify(mockedUser, never()).getEmail();

        verify(mockedSession, never()).clear();
        verify(mockedSession, never()).put(ConstantsManager.COOKIE_SESSION_FIELD_NAME, email);

        assertEquals(Http.Status.BAD_REQUEST, status(result));
    }
}
