package ca.ulaval.glo4003.interactors;

import ca.ulaval.glo4003.dataaccessobjects.UserDao;
import ca.ulaval.glo4003.exceptions.AuthenticationException;
import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.models.Credentials;
import ca.ulaval.glo4003.models.User;
import com.google.inject.Inject;

public class AuthenticationInteractor {

    private final UserDao userDao;

    @Inject
    public AuthenticationInteractor(UserDao userDao) {
        this.userDao = userDao;
    }

    public User authenticate(Credentials credentials) throws AuthenticationException {
        try {
            User user = userDao.findByEmail(credentials.getEmail());
            if (user.getPassword().equals(credentials.getPassword())) {
                return user;
            }
        } catch (RecordNotFoundException ignored) {
        }
        throw new AuthenticationException();
    }
}
