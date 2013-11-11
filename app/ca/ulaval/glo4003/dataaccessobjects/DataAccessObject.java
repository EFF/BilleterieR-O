package ca.ulaval.glo4003.dataaccessobjects;

import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.exceptions.UniqueConstraintException;

import java.util.List;

public interface DataAccessObject<T> {

    public List<T> list();

    public T create(T event) throws UniqueConstraintException;

    public T read(long id) throws RecordNotFoundException;

    public void update(T event) throws UniqueConstraintException, RecordNotFoundException;

    public void delete(long id) throws RecordNotFoundException;

    public void deleteAll();

    public int count();
}
