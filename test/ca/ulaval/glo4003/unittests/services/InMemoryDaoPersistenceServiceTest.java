package ca.ulaval.glo4003.unittests.services;

import ca.ulaval.glo4003.dataaccessobjects.PersistedDao;
import ca.ulaval.glo4003.services.InMemoryDaoPersistenceService;
import ca.ulaval.glo4003.unittests.dataaccessobjects.PersistedDaoTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InMemoryDaoPersistenceServiceTest {

    private static final int A_VALUE = 50;
    private static final int AN_ID = 666;

    @Mock
    private PersistedDao<PersistedDaoTest.TestRecord> mockedDao;
    private InMemoryDaoPersistenceService persistenceService;

    @Before
    public void setUp() {
        persistenceService = new InMemoryDaoPersistenceService();
    }

    @Test
    public void restoreAlwaysReturnsAnEmptyArray() {
        /* The expected behaviour of a memory persistence service
        is to always return nothing because InMemory is volatile
        persistency by definition.*/
        // Arrange
        PersistedDaoTest.TestRecord record = new PersistedDaoTest.TestRecord(A_VALUE);
        record.setId(AN_ID);
        when(mockedDao.list()).thenReturn(Arrays.asList(record));

        // Act & Assert
        assertThat(persistenceService.restore(mockedDao)).isEmpty();
    }

}
