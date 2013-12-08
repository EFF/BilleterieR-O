package ca.ulaval.glo4003.domain.user;

import ca.ulaval.glo4003.domain.Dao;
import ca.ulaval.glo4003.domain.RecordNotFoundException;

public interface UserDao extends Dao<User> {

    public User findByEmail(final String email) throws RecordNotFoundException;
}
