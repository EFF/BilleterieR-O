package ca.ulaval.glo4003.persistence;

import ca.ulaval.glo4003.domain.Dao;
import ca.ulaval.glo4003.domain.Record;
import ca.ulaval.glo4003.persistence.DaoPersistenceService;

import java.util.ArrayList;
import java.util.List;

public class InMemoryDaoPersistenceService implements DaoPersistenceService {

    @Override
    public <T extends Dao> void persist(T dao) {
        // Already in memory.. unless you are on swap or use some black magic
    }

    @Override
    public <T extends Record, Y extends Dao> List<T> restore(Y dao) {
        return new ArrayList<>();
    }
}
