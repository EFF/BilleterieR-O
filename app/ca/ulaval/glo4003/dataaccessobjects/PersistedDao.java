package ca.ulaval.glo4003.dataaccessobjects;

import ca.ulaval.glo4003.models.Record;
import ca.ulaval.glo4003.services.DaoPersistenceService;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class PersistedDao<T extends Record> implements DataAccessObject<T>, Serializable {

    private long lastId = 1;
    protected List<T> list = new ArrayList<>();
    protected DaoPersistenceService persistenceService;

    public PersistedDao(DaoPersistenceService persistenceService) {
        this.persistenceService = persistenceService;

        try {
            this.list = this.persistenceService.restore(this);

            System.out.println("Restored the list with " + this.list.size() + " items.");
        } catch (Exception e) {
        }
    }

    public T create(T element) {
        element.setId(lastId);
        persist(element);
        lastId++;
        return element;
    }

    public T read(long id) throws RecordNotFoundException {
        for (T element : list) {
            if (element.getId() == id) {
                return element;
            }
        }
        throw new RecordNotFoundException();
    }

    public void update(T element) throws RecordNotFoundException {
        delete(element.getId());
        persist(element);
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

    protected void persist(T element) {
        // We clone the object before saving it in the DB
        // Otherwise, a change on a record outside this
        // dao would reflect in the DB without calling
        // the update method
        T elementCopy = SerializationUtils.clone(element);
        list.add(elementCopy);
        try {
            this.persistenceService.persist(this);
        }
        catch(Exception e) {

        }

    }
}
