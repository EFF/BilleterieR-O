package ca.ulaval.glo4003.unittests.interactors;

import ca.ulaval.glo4003.dataaccessobjects.UserDao;
import ca.ulaval.glo4003.exceptions.AuthenticationException;
import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.interactors.AuthenticationInteractor;
import ca.ulaval.glo4003.models.Credentials;
import ca.ulaval.glo4003.models.User;
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

    @Inject
    private AuthenticationInteractor authenticationInteractor;

    @Test
    public void authenticateUser(UserDao mockedUserDao) throws AuthenticationException, RecordNotFoundException {
        String email = "user@example.com";
        String password = "secret";
        Credentials credentials = createCredentials(email, password);
        User fakeUser = new User();
        fakeUser.setEmail(email);
        fakeUser.setPassword(password);
        when(mockedUserDao.findByEmail(email)).thenReturn(fakeUser);

        User user = authenticationInteractor.authenticate(credentials);

        assertEquals(fakeUser.getEmail(), user.getEmail());
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
        String email = "user@example.com";
        String password = "secret";
        User mockedUser = mock(User.class);
        when(mockedUser.getEmail()).thenReturn(email);
        when(mockedUser.getPassword()).thenReturn(password);
        when(mockedUserDao.findByEmail(email)).thenReturn(mockedUser);
        Credentials credentials = createCredentials(email, "wrong");

        authenticationInteractor.authenticate(credentials);
    }

    private Credentials createCredentials(String email, String password) {
        Credentials credentials = new Credentials();
        credentials.setEmail(email);
        credentials.setPassword(password);
        return credentials;
    }

    public static class TestModule extends JukitoModule {

        @Override
        protected void configureTest() {
            forceMock(UserDao.class);
        }
    }
}
