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

    @Inject
    private UsersInteractor usersInteractor;

    @Test(expected = RecordNotFoundException.class)
    public void updateThrowsRecordNotFoundWhenUserDoesNotExists(UserDao mockedUserDao) throws RecordNotFoundException {
        String actualEmail = "user@example.com";
        String newEmail = "new-email@example.com";
        doThrow(new RecordNotFoundException()).when(mockedUserDao).findByEmail(actualEmail);

        usersInteractor.updateEmail(actualEmail, newEmail);
    }

    @Test(expected = UniqueValidationException.class)
    public void updateThrowsUniqueValidationExceptionWhenTheNewEmailAlreadyExists(UserDao mockedUserDao) throws
            RecordNotFoundException {
        String actualEmail = "user@example.com";
        String newEmail = "new-email@example.com";
        User mockedUser = mock(User.class);
        when(mockedUserDao.findByEmail(actualEmail)).thenReturn(mockedUser);
        doThrow(new UniqueValidationException()).when(mockedUserDao).update(mockedUser);

        usersInteractor.updateEmail(actualEmail, newEmail);
    }

    @Test(expected = ConstraintViolationException.class)
    public void updateThrowsConstraintViolationExceptionWhenTheNewEmailIsNotValid(UserDao mockedUserDao) throws
            RecordNotFoundException {
        String actualEmail = "user@example.com";
        String newEmail = "new-email@example.com";
        User mockedUser = mock(User.class);
        when(mockedUserDao.findByEmail(actualEmail)).thenReturn(mockedUser);
        doThrow(new ConstraintViolationException(new HashSet<ConstraintViolation<?>>())).when(mockedUserDao).update(mockedUser);

        usersInteractor.updateEmail(actualEmail, newEmail);
    }

    @Test()
    public void updateUserWithAValidUniqueEmail(UserDao mockedUserDao) throws
            RecordNotFoundException {
        String actualEmail = "user@example.com";
        String newEmail = "new-email@example.com";
        User mockedUser = mock(User.class);
        when(mockedUserDao.findByEmail(actualEmail)).thenReturn(mockedUser);

        usersInteractor.updateEmail(actualEmail, newEmail);

        verify(mockedUserDao).findByEmail(actualEmail);
        InOrder inOrder = inOrder(mockedUser, mockedUserDao);
        inOrder.verify(mockedUser).setEmail(newEmail);
        inOrder.verify(mockedUserDao).update(mockedUser);
    }

    @Test
    public void updatePasswordWithValidEmailAndActualPassword(UserDao mockedUserDao) throws RecordNotFoundException, InvalidActualPasswordException {
        String email = "user@example.com";
        String actualPassword = "secret";
        String newPassword = "newPassword";
        User mockedUser = mock(User.class);
        when(mockedUser.getPassword()).thenReturn(actualPassword);
        when(mockedUserDao.findByEmail(email)).thenReturn(mockedUser);

        usersInteractor.updatePassword(email, actualPassword, newPassword);

        verify(mockedUserDao).findByEmail(email);
        InOrder inOrder = inOrder(mockedUser, mockedUserDao);
        inOrder.verify(mockedUser).setPassword(newPassword);
        inOrder.verify(mockedUserDao).update(mockedUser);
    }

    @Test(expected = RecordNotFoundException.class)
    public void updatePasswordWithWrongEmail(UserDao mockedUserDao) throws RecordNotFoundException,
            InvalidActualPasswordException {
        String email = "user@example.com";
        String actualPassword = "secret";
        String newPassword = "newPassword";
        doThrow(new RecordNotFoundException()).when(mockedUserDao).findByEmail(email);

        usersInteractor.updatePassword(email, actualPassword, newPassword);
    }

    @Test(expected = InvalidActualPasswordException.class)
    public void updatePasswordWithWrongActualPassword(UserDao mockedUserDao) throws RecordNotFoundException,
            InvalidActualPasswordException {
        String email = "user@example.com";
        String actualPassword = "secret";
        String wrongPassword = "wrong";
        String newPassword = "newPassword";
        User mockedUser = mock(User.class);
        when(mockedUser.getPassword()).thenReturn(actualPassword);
        when(mockedUserDao.findByEmail(email)).thenReturn(mockedUser);

        usersInteractor.updatePassword(email, wrongPassword, newPassword);
    }

    public static class TestModule extends JukitoModule {

        @Override
        protected void configureTest() {
            forceMock(UserDao.class);
        }
    }
}
