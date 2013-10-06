package ca.ulaval.glo4003.services;

import ca.ulaval.glo4003.dataaccessobjects.DataAccessObject;

public class InMemoryDaoPersistenceService implements DaoPersistenceService {

    @Override
    public <T extends DataAccessObject> void persist(T dao) {

    }

    @Override
    public <T extends DataAccessObject> void restore(T dao) {

    }
}
