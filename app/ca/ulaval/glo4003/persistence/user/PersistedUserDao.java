package ca.ulaval.glo4003.persistence.user;

import ca.ulaval.glo4003.persistence.PersistedDao;
import ca.ulaval.glo4003.persistence.UniqueConstraintValidator;
import ca.ulaval.glo4003.domain.user.UserDao;
import ca.ulaval.glo4003.domain.RecordNotFoundException;
import ca.ulaval.glo4003.domain.user.User;
import ca.ulaval.glo4003.persistence.DaoPersistenceService;
import com.google.inject.Inject;

public class PersistedUserDao extends PersistedDao<User> implements UserDao {

    @Inject
    public PersistedUserDao(DaoPersistenceService persistenceService, UniqueConstraintValidator<User>
            uniqueConstraintValidator) {
        super(persistenceService, uniqueConstraintValidator);
    }

    public User findByEmail(final String email) throws RecordNotFoundException {
        for (User user : list()) {
            if (user.getEmail().toLowerCase().equals(email.toLowerCase())) {
                return user;
            }
        }

        throw new RecordNotFoundException();
    }
}
