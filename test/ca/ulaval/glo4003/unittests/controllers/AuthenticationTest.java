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
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static play.test.Helpers.status;

@RunWith(JukitoRunner.class)
public class AuthenticationTest {
    @Inject
    Authentication authentication;

    Http.Request mockedRequest;

    String username = "username";
    String password = "password";

    @Before
    public void setupMocks(UserDao mockedUserDao) throws RecordNotFoundException {
        ObjectNode json = Json.newObject();
        json.put("username", username);
        json.put("password", password);

        User mockedUser = mock(User.class);
        when(mockedUser.getEmail()).thenReturn("email@test.com");

        Http.RequestBody mockedBody = mock(Http.RequestBody.class);
        when(mockedBody.asJson()).thenReturn(json);

        mockedRequest = mock(Http.Request.class);
        when(mockedRequest.body()).thenReturn(mockedBody);

        when(mockedUserDao.findByEmailAndPassword(anyString(), anyString())).thenReturn(mockedUser);

        Http.Context.current.set(new Http.Context((long) 1, mock(play.api.mvc.RequestHeader.class),
                mockedRequest, new HashMap<String, String>(), new HashMap<String, String>(),
                new HashMap<String, Object>()));
    }

    @Test
    public void loginTest(UserDao mockedUserDao) throws RecordNotFoundException {
        Result result = authentication.login();

        verify(mockedRequest).body();
        verify(mocked).cl();
        verify(mockedUserDao).findByEmailAndPassword(anyString(), anyString());
        assertEquals(Http.Status.OK, status(result));
    }
}
