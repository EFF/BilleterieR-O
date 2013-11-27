package ca.ulaval.glo4003.domain.user;

import ca.ulaval.glo4003.persistence.UniqueValidationException;
import ca.ulaval.glo4003.domain.RecordNotFoundException;
import com.google.inject.Inject;

import javax.validation.ConstraintViolationException;

public class UsersInteractor {

    private final UserDao userDao;

    @Inject
    public UsersInteractor(UserDao userDao) {
        this.userDao = userDao;
    }

    public void updateEmail(final String actualEmail, final String newEmail) throws RecordNotFoundException,
            UniqueValidationException, ConstraintViolationException {
        User user = userDao.findByEmail(actualEmail);
        user.setEmail(newEmail);
        userDao.update(user);
    }

    public void updatePassword(final String email, final String actualPassword, final String newPassword)
            throws RecordNotFoundException, InvalidActualPasswordException {
        User user = userDao.findByEmail(email);

        if (!user.getPassword().equals(actualPassword)) {
            throw new InvalidActualPasswordException();
        }

        user.setPassword(newPassword);
        userDao.update(user);
    }

    public User getByEmail(String email) throws RecordNotFoundException {
        return userDao.findByEmail(email);
    }
}
