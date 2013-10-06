package ca.ulaval.glo4003.services;

import ca.ulaval.glo4003.dataaccessobjects.DataAccessObject;

public interface DaoPersistenceService {

    public <T extends DataAccessObject> void persist(T dao);

    public <T extends DataAccessObject> void restore(T dao);

}
