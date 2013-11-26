package ca.ulaval.glo4003.unittests.interactors;

import ca.ulaval.glo4003.domain.user.UserDao;
import ca.ulaval.glo4003.domain.user.AuthenticationException;
import ca.ulaval.glo4003.domain.RecordNotFoundException;
import ca.ulaval.glo4003.domain.user.AuthenticationInteractor;
import ca.ulaval.glo4003.domain.user.Credentials;
import ca.ulaval.glo4003.domain.user.User;
import com.google.inject.Inject;
import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(JukitoRunner.class)
public class AuthenticationInteractorTest {

    private static final String AN_EMAIL = "user@example.com";
    private static final String A_PASSWORD = "secret";
    private static final String A_WRONG_PASSWORD = "wrong";
    @Inject
    private AuthenticationInteractor authenticationInteractor;

    @Test
    public void authenticateUser(UserDao mockedUserDao) throws AuthenticationException, RecordNotFoundException {
        Credentials credentials = createMockedCredentials(AN_EMAIL, A_PASSWORD);
        User mockedUser = mock(User.class);
        when(mockedUser.getEmail()).thenReturn(AN_EMAIL);
        when(mockedUser.getPassword()).thenReturn(A_PASSWORD);
        when(mockedUserDao.findByEmail(AN_EMAIL)).thenReturn(mockedUser);

        User user = authenticationInteractor.authenticate(credentials);

        assertEquals(mockedUser.getEmail(), user.getEmail());
    }

    @Test(expected = AuthenticationException.class)
    public void authenticateThrowsIfUserNotFound(UserDao mockedUserDao) throws RecordNotFoundException, AuthenticationException {
        Credentials credentials = mock(Credentials.class);
        doThrow(new RecordNotFoundException()).when(mockedUserDao).findByEmail(anyString());

        authenticationInteractor.authenticate(credentials);
    }

    @Test(expected = AuthenticationException.class)
    public void authenticateThrowsIfWrongPassword(UserDao mockedUserDao) throws AuthenticationException,
            RecordNotFoundException {
        User mockedUser = mock(User.class);
        when(mockedUser.getEmail()).thenReturn(AN_EMAIL);
        when(mockedUser.getPassword()).thenReturn(A_PASSWORD);
        when(mockedUserDao.findByEmail(AN_EMAIL)).thenReturn(mockedUser);
        Credentials credentials = createMockedCredentials(AN_EMAIL, A_WRONG_PASSWORD);

        authenticationInteractor.authenticate(credentials);
    }

    private Credentials createMockedCredentials(String email, String password) {
        Credentials mockedCredentials = mock(Credentials.class);
        when(mockedCredentials.getEmail()).thenReturn(email);
        when(mockedCredentials.getPassword()).thenReturn(password);
        return mockedCredentials;
    }

    public static class TestModule extends JukitoModule {

        @Override
        protected void configureTest() {
            forceMock(UserDao.class);
        }
    }
}
