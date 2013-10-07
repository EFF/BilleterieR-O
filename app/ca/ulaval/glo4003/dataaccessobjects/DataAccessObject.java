package ca.ulaval.glo4003.dataaccessobjects;

import ca.ulaval.glo4003.exceptions.RecordNotFoundException;

import java.util.List;

public interface DataAccessObject<T> {

    public List<T> list();

    public T create(T event);

    public T read(long id) throws RecordNotFoundException;

    public void update(T event) throws RecordNotFoundException;

    public void delete(long id) throws RecordNotFoundException;

    public void deleteAll();

    public int count();
}
