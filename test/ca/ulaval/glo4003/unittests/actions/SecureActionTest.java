package ca.ulaval.glo4003.unittests.actions;

import ca.ulaval.glo4003.ConstantsManager;
import ca.ulaval.glo4003.actions.SecureAction;
import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static play.test.Helpers.status;

public class SecureActionTest {

    @Test
    public void getUsernameShouldGetTheUsernameFromSession() {
        String username = "username";
        Http.Context mockedHttpContext = mock(Http.Context.class);
        Http.Session mockedHttpSession = mock(Http.Session.class);
        when(mockedHttpSession.get(ConstantsManager.COOKIE_SESSION_FIELD_NAME)).thenReturn(username);
        when(mockedHttpContext.session()).thenReturn(mockedHttpSession);
        SecureAction secureAction = new SecureAction();

        String result = secureAction.getUsername(mockedHttpContext);

        assertEquals(username, result);
    }

    @Test
    public void onUnauthorizedReturnHttpResultWith401HttpCode() {
        Http.Context mockedHttpContext = mock(Http.Context.class);
        SecureAction secureAction = new SecureAction();

        Result result = secureAction.onUnauthorized(mockedHttpContext);

        assertEquals(Http.Status.UNAUTHORIZED, status(result));
    }
}
