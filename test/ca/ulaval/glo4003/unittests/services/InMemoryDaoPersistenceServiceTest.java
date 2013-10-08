package ca.ulaval.glo4003.unittests.services;

import ca.ulaval.glo4003.dataaccessobjects.PersistedDao;
import ca.ulaval.glo4003.services.InMemoryDaoPersistenceService;
import ca.ulaval.glo4003.unittests.dataaccessobjects.PersistedDaoTest;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class InMemoryDaoPersistenceServiceTest {

    InMemoryDaoPersistenceService persistenceService;
    private PersistedDao<PersistedDaoTest.TestRecord> dao;

    @Before
    public void setUp() {

        this.persistenceService = new InMemoryDaoPersistenceService();
        this.dao = new PersistedDao<PersistedDaoTest.TestRecord>(persistenceService) {};
    }

    @Test
    public void restoreAlwaysReturnsAnEmptyArray() {
        /* The expected behaviour of a memory persistence service
        is to always return nothing because InMemory is volatile
        persistency by definition.*/

        // Act & Assert
        assertThat(this.persistenceService.restore(this.dao)).isEmpty();
    }

}
