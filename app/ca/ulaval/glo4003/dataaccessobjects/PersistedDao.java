package ca.ulaval.glo4003.dataaccessobjects;

import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.models.Record;
import ca.ulaval.glo4003.services.DaoPersistenceService;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.*;

public abstract class PersistedDao<T extends Record> implements DataAccessObject<T>, Serializable {

    private long lastId = 1;
    private List<T> list = new ArrayList<>();
    private DaoPersistenceService persistenceService;
    private UniqueConstraintValidator<T> uniqueConstraintValidator;
    Comparator<T> comparator = new Comparator<T>() {
        public int compare(T c1, T c2) {
            Long id1 = c1.getId();
            Long id2 = c2.getId();
            return id2.compareTo(id1);
        }
    };

    public PersistedDao(DaoPersistenceService persistenceService, UniqueConstraintValidator<T>
            uniqueConstraintValidator) {
        this.persistenceService = persistenceService;
        this.uniqueConstraintValidator = uniqueConstraintValidator;

        try {
            this.list = this.persistenceService.restore(this);
            System.out.println("Successfully restored DAO [" + this.getClass().getSimpleName() + "] with a total of " +
                    this.list.size() + " items.");
        } catch (Exception e) {
            System.out.println("Warning: Could not restore DAO [" + this.getClass().getSimpleName() + "]: " +
                    e.getMessage());
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
        Collections.sort(list, comparator);
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
        uniqueConstraintValidator.validate(list(), element);

        // We clone the object before saving it in the DB
        // Otherwise, a change on a record outside this
        // dao would reflect in the DB without calling
        // the update method
        T elementCopy = SerializationUtils.clone(element);
        list.add(elementCopy);
        try {
            this.persistenceService.persist(this);
        } catch (Exception e) {
            System.err.println("Error persisting DAO [" + this.getClass().getSimpleName() + "] : " + e.getMessage());
        }
    }
}
