package ca.ulaval.glo4003.unittests.interactors;

import ca.ulaval.glo4003.dataaccessobjects.UniqueValidationException;
import ca.ulaval.glo4003.dataaccessobjects.UserDao;
import ca.ulaval.glo4003.exceptions.InvalidActualPasswordException;
import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.interactors.UsersInteractor;
import ca.ulaval.glo4003.models.User;
import com.google.inject.Inject;
import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashSet;

import static org.mockito.Mockito.*;

@RunWith(JukitoRunner.class)
public class UsersInteractorTest {

    private static final String AN_EMAIL = "user@example.com";
    private static final String A_NEW_EMAIL = "new-email@example.com";
    private static final String A_PASSWORD = "secret";
    private static final String A_NEW_PASSWORD = "new-secret";
    private static final String A_WRONG_PASSWORD = "wrong-secret";
    @Inject
    private UsersInteractor usersInteractor;

    @Test(expected = RecordNotFoundException.class)
    public void updateThrowsRecordNotFoundWhenUserDoesNotExists(UserDao mockedUserDao) throws RecordNotFoundException {
        doThrow(new RecordNotFoundException()).when(mockedUserDao).findByEmail(AN_EMAIL);

        usersInteractor.updateEmail(AN_EMAIL, A_NEW_EMAIL);
    }

    @Test(expected = UniqueValidationException.class)
    public void updateThrowsUniqueValidationExceptionWhenTheNewEmaiAlreadyExists(UserDao mockedUserDao) throws
            RecordNotFoundException {
        User mockedUser = mock(User.class);
        when(mockedUserDao.findByEmail(AN_EMAIL)).thenReturn(mockedUser);
        doThrow(new UniqueValidationException()).when(mockedUserDao).update(mockedUser);

        usersInteractor.updateEmail(AN_EMAIL, A_NEW_EMAIL);
    }

    @Test(expected = ConstraintViolationException.class)
    public void updateThrowsConstraintViolationExceptionWhenTheNewEmailIsNotValid(UserDao mockedUserDao) throws
            RecordNotFoundException {
        User mockedUser = mock(User.class);
        when(mockedUserDao.findByEmail(AN_EMAIL)).thenReturn(mockedUser);
        doThrow(new ConstraintViolationException(new HashSet<ConstraintViolation<?>>())).when(mockedUserDao).update(mockedUser);

        usersInteractor.updateEmail(AN_EMAIL, A_NEW_EMAIL);
    }

    @Test()
    public void updateUserWithAValidUniqueEmail(UserDao mockedUserDao) throws
            RecordNotFoundException {
        User mockedUser = mock(User.class);
        when(mockedUserDao.findByEmail(AN_EMAIL)).thenReturn(mockedUser);

        usersInteractor.updateEmail(AN_EMAIL, A_NEW_EMAIL);

        verify(mockedUserDao).findByEmail(AN_EMAIL);
        InOrder inOrder = inOrder(mockedUser, mockedUserDao);
        inOrder.verify(mockedUser).setEmail(A_NEW_EMAIL);
        inOrder.verify(mockedUserDao).update(mockedUser);
    }

    @Test
    public void updatePasswordWithValidEmailAndActualPassword(UserDao mockedUserDao) throws RecordNotFoundException, InvalidActualPasswordException {
        User mockedUser = mock(User.class);
        when(mockedUser.getPassword()).thenReturn(A_PASSWORD);
        when(mockedUserDao.findByEmail(AN_EMAIL)).thenReturn(mockedUser);

        usersInteractor.updatePassword(AN_EMAIL, A_PASSWORD, A_NEW_PASSWORD);

        verify(mockedUserDao).findByEmail(AN_EMAIL);
        InOrder inOrder = inOrder(mockedUser, mockedUserDao);
        inOrder.verify(mockedUser).setPassword(A_NEW_PASSWORD);
        inOrder.verify(mockedUserDao).update(mockedUser);
    }

    @Test(expected = RecordNotFoundException.class)
    public void updatePasswordWithWrongEmail(UserDao mockedUserDao) throws RecordNotFoundException,
            InvalidActualPasswordException {
        doThrow(new RecordNotFoundException()).when(mockedUserDao).findByEmail(AN_EMAIL);

        usersInteractor.updatePassword(AN_EMAIL, A_PASSWORD, A_NEW_PASSWORD);
    }

    @Test(expected = InvalidActualPasswordException.class)
    public void updatePasswordWithWrongActualPassword(UserDao mockedUserDao) throws RecordNotFoundException,
            InvalidActualPasswordException {
        User mockedUser = mock(User.class);
        when(mockedUser.getPassword()).thenReturn(A_PASSWORD);
        when(mockedUserDao.findByEmail(AN_EMAIL)).thenReturn(mockedUser);

        usersInteractor.updatePassword(AN_EMAIL, A_WRONG_PASSWORD, A_NEW_PASSWORD);
    }

    public static class TestModule extends JukitoModule {

        @Override
        protected void configureTest() {
            forceMock(UserDao.class);
        }
    }
}
