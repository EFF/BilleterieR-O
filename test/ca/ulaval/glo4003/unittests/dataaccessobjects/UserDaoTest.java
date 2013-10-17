package ca.ulaval.glo4003.unittests.dataaccessobjects;

import ca.ulaval.glo4003.dataaccessobjects.UserDao;
import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.models.User;
import ca.ulaval.glo4003.services.DaoPersistenceService;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class UserDaoTest {

    private DaoPersistenceService daoPersistenceService;

    public void setUp() {
        daoPersistenceService = mock(DaoPersistenceService.class);
    }

    @Test
    public void findUserByEmailAndPasswordReturnsTheUser() throws RecordNotFoundException {
        //Arrange
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("secret");

        UserDao userDao = new UserDao(daoPersistenceService);
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

        UserDao userDao = new UserDao(daoPersistenceService);
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

        UserDao userDao = new UserDao(daoPersistenceService);
        userDao.create(user);

        //Act
        userDao.findByEmailAndPassword(user.getEmail(), "wrong");
    }
}
