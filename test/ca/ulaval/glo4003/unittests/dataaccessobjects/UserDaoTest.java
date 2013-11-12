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
    public void findUserByEmailReturnsTheUser() throws RecordNotFoundException {
        User user = new User();
        user.setEmail("user@example.com");
        userDao.create(user);

        User result = userDao.findByEmail(user.getEmail());

        assertNotNull(result);
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test(expected = RecordNotFoundException.class)
    public void findUserByEmailThrowsRecordNotFoundIfTheEmailIsWrong() throws RecordNotFoundException {
        User user = new User();
        user.setEmail("user@example.com");
        userDao.create(user);

        userDao.findByEmail("wrong@email.com");
    }
}
