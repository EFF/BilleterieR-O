package ca.ulaval.glo4003.persistence;

import ca.ulaval.glo4003.domain.Dao;
import ca.ulaval.glo4003.domain.Record;

import java.io.IOException;
import java.util.List;

public interface DaoPersistenceService {

    public <T extends Dao> void persist(T dao) throws IOException;

    public <T extends Record, Y extends Dao> List<T> restore(Y dao) throws IOException, ClassNotFoundException;

}
