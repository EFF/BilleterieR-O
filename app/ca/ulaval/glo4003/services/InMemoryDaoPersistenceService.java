package ca.ulaval.glo4003.services;

import ca.ulaval.glo4003.dataaccessobjects.DataAccessObject;
import ca.ulaval.glo4003.models.Record;

import java.util.ArrayList;
import java.util.List;

public class InMemoryDaoPersistenceService implements DaoPersistenceService {

    @Override
    public <T extends DataAccessObject> void persist(T dao) {
        // Already in memory.. unless you are on swap or use some black magic
    }

    @Override
    public <T extends Record, Y extends DataAccessObject> List<T> restore(Y dao) {
        return new ArrayList();
    }
}
