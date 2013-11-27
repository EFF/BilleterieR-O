package ca.ulaval.glo4003.domain.user;

import ca.ulaval.glo4003.domain.RecordNotFoundException;
import com.google.inject.Inject;

public class AuthenticationInteractor {

    private final UserDao userDao;

    @Inject
    public AuthenticationInteractor(UserDao userDao) {
        this.userDao = userDao;
    }

    public User authenticate(final Credentials credentials) throws AuthenticationException {
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
