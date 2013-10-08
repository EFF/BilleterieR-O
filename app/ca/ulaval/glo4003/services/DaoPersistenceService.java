package ca.ulaval.glo4003.services;

import ca.ulaval.glo4003.dataaccessobjects.DataAccessObject;
import ca.ulaval.glo4003.models.Record;

import java.io.IOException;
import java.util.List;

public interface DaoPersistenceService {

    public <T extends DataAccessObject> void persist(T dao) throws IOException;

    public <T extends Record, Y extends DataAccessObject> List<T> restore(Y dao) throws IOException, ClassNotFoundException;

}
