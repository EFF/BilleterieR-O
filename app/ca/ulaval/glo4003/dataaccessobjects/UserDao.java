package ca.ulaval.glo4003.dataaccessobjects;

import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.models.User;

public interface UserDao extends DataAccessObject<User> {

    public User findByEmail(final String email) throws RecordNotFoundException;

}
