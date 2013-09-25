package ca.ulaval.glo4003.dataaccessobjects;

import ca.ulaval.glo4003.models.Record;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

public abstract class DaoInMemory<T extends Record> implements DataAccessObject<T> {

    private long lastId = 1;
    protected List<T> list = new ArrayList<>();

    public void create(T element) {
        element.setId(lastId);
        list.add(element);
        lastId++;
    }

    public T read(long id) throws RecordNotFoundException {
        for (T element : list) {
            if (element.getId() == id) {
                return element;
            }
        }
        throw new RecordNotFoundException();
    }

    public void update(T element) {
        // TODO
        throw new NotImplementedException();
    }

    public void delete(long id) throws RecordNotFoundException {
        T element = read(id);
        list.remove(element);
    }

    public void deleteAll() {
        list.clear();
        lastId = 1;
    }

    public List<T> list() {
        return new ArrayList<>(list);
    }

    public int count() {
        return list.size();
    }
}
