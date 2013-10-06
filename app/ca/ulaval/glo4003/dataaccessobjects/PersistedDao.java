package ca.ulaval.glo4003.dataaccessobjects;

import ca.ulaval.glo4003.models.Record;

public abstract class PersistedDao<T extends Record> extends DaoInMemory<T> {

    public PersistedDao() {

    }

    @Override
    protected void persist(T element) {
        super.persist(element);

        // TODO : Save to file
    }

    private void persistToDisk() {

    }

}
