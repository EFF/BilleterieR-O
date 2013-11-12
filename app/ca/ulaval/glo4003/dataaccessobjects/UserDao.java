package ca.ulaval.glo4003.dataaccessobjects;

import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.models.User;
import ca.ulaval.glo4003.services.DaoPersistenceService;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.inject.Inject;

public class UserDao extends PersistedDao<User> implements DataAccessObject<User> {

    @Inject
    public UserDao(DaoPersistenceService persistenceService, UniqueConstraintValidator<User>
            uniqueConstraintValidator) {
        super(persistenceService, uniqueConstraintValidator);
    }

    public User findByEmailAndPassword(final String email, final String password) throws RecordNotFoundException {
        FluentIterable<User> users = FluentIterable.from(this.list());

        Optional<User> userOptional = users.firstMatch(new Predicate<User>() {
            @Override
            public boolean apply(User input) {
                return input.getEmail().toLowerCase().equals(email.toLowerCase())
                        && input.getPassword().equals(password);
            }
        });

        if (userOptional.isPresent()) {
            return userOptional.get();
        }

        throw new RecordNotFoundException();
    }
}
