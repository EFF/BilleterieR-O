package ca.ulaval.glo4003.unittests.controllers;

import ca.ulaval.glo4003.controllers.Authentication;
import ca.ulaval.glo4003.dataaccessobjects.UserDao;
import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.models.User;
import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(JukitoRunner.class)
public class AuthenticationTest {
    @Inject
    Authentication authentication;

    UserDao mockedUserDao;
    String username = "username";
    String password = "password";

    @Before
    public void setupMocks() throws RecordNotFoundException {
        mockedUserDao = mock(UserDao.class);
        User mockedUser = mock(User.class);
        when(mockedUser.getEmail()).thenReturn("email@test.com");

        when(mockedUserDao.findByEmailAndPassword(anyString(), anyString())).thenReturn(mockedUser);
    }

    @Test
    public void dummyTest() throws RecordNotFoundException {
        authentication.test();
        assertEquals(true, true);

        verify(mockedUserDao).findByEmailAndPassword(anyString(), anyString());
    }
}
