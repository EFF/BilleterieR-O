package ca.ulaval.glo4003.unittests.dataaccessobjects;

import ca.ulaval.glo4003.dataaccessobjects.UniqueConstraintValidator;
import ca.ulaval.glo4003.dataaccessobjects.UserDao;
import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.models.User;
import ca.ulaval.glo4003.services.DaoPersistenceService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class UserDaoTest {

    @Mock
    private UniqueConstraintValidator<User> uniqueConstraintValidator;
    private UserDao userDao;

    @Before
    public void setUp() {
        userDao = new UserDao(mock(DaoPersistenceService.class), uniqueConstraintValidator);
    }

    @Test
    public void findUserByEmailAndPasswordReturnsTheUser() throws RecordNotFoundException {
        //Arrange
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("secret");

        userDao.create(user);

        //Act
        User result = userDao.findByEmailAndPassword(user.getEmail(), user.getPassword());

        assertNotNull(result);
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test(expected = RecordNotFoundException.class)
    public void findUserByEmailAndPasswordThrowsRecordNotFoundIfTheEmailIsWrong() throws RecordNotFoundException {
        //Arrange
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("secret");

        userDao.create(user);

        //Act
        userDao.findByEmailAndPassword("wrong@email.com", user.getPassword());
    }

    @Test(expected = RecordNotFoundException.class)
    public void findUserByEmailAndPasswordThrowsRecordNotFoundIfThePasswordIsWrong() throws RecordNotFoundException {
        //Arrange
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("secret");

        userDao.create(user);

        //Act
        userDao.findByEmailAndPassword(user.getEmail(), "wrong");
    }
}
