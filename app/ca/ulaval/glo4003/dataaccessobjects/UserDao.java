package ca.ulaval.glo4003.dataaccessobjects;

import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.models.User;
import ca.ulaval.glo4003.services.DaoPersistenceService;
import com.google.inject.Inject;

public class UserDao extends PersistedDao<User> implements DataAccessObject<User> {

    @Inject
    public UserDao(DaoPersistenceService persistenceService, UniqueConstraintValidator<User>
            uniqueConstraintValidator) {
        super(persistenceService, uniqueConstraintValidator);
    }

    public User findByEmail(final String email) throws RecordNotFoundException {
        for(User user: list()) {
            if(user.getEmail().toLowerCase().equals(email.toLowerCase())) {
                return user;
            }
        }

        throw new RecordNotFoundException();
    }
}
