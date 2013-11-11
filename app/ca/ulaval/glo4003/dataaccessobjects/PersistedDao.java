package ca.ulaval.glo4003.dataaccessobjects;

import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.exceptions.UniqueConstraintException;
import ca.ulaval.glo4003.models.Record;
import ca.ulaval.glo4003.services.DaoPersistenceService;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class PersistedDao<T extends Record> implements DataAccessObject<T>, Serializable {

    protected List<T> list = new ArrayList<>();
    protected DaoPersistenceService persistenceService;
    private long lastId = 1;
    private UniqueConstraintValidator uniqueConstraintValidator;

    public PersistedDao(DaoPersistenceService persistenceService, UniqueConstraintValidator uniqueConstraintValidator) {
        this.persistenceService = persistenceService;
        this.uniqueConstraintValidator = uniqueConstraintValidator;

        try {
            this.list = this.persistenceService.restore(this);
            System.out.println("Successfully restored DAO [" + this.getClass().getSimpleName() + "] with a total of " +
                    this.list.size() + " items.");
        } catch (Exception e) {
            System.out.println("Warning: Could not restore DAO [" + this.getClass().getSimpleName() + "]: " + e.getMessage());
        }
    }

    public T create(T element) throws UniqueConstraintException {
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

    public void update(T element) throws RecordNotFoundException, UniqueConstraintException {
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

    protected void persist(T element) throws UniqueConstraintException {
        uniqueConstraintValidator.validate((List<Record>) list(), element);

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
            System.err.println("Error persisting DAO [" + this.getClass().getSimpleName() + "] : " + e.getMessage());
        }
    }
}
