package ca.ulaval.glo4003.unittests.persistence;

import ca.ulaval.glo4003.domain.RecordNotFoundException;
import ca.ulaval.glo4003.persistence.FileBasedDaoPersistenceService;
import ca.ulaval.glo4003.persistence.PersistedDao;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FileBasedDaoPersistenceServiceTest {

    private static final String PROFILE = "UTests";
    private static final int A_VALUE = 50;
    private static final int AN_ID = 666;

    @Mock
    private PersistedDao<PersistedDaoTest.TestRecord> mockedDao;
    private FileBasedDaoPersistenceService persistenceService;

    @Before
    public void setUp() throws IOException {
        persistenceService = new FileBasedDaoPersistenceService(PROFILE);
        deleteAllData();
    }

    @After
    public void tearDown() throws IOException {
        deleteAllData();
    }

    @Test(expected = IOException.class)
    public void restoreThrowsWhenPersistenceFilesNotFound() throws IOException, ClassNotFoundException {
        persistenceService.restore(mockedDao);
    }

    @Test
    public void restoreDoesNotThrowWhenPersistenceFilesFound() throws IOException, ClassNotFoundException {
        System.out.println(getPersistenceFilesDirectory().getAbsolutePath());
        persistenceService.persist(mockedDao);

        assertThat(getPersistenceFilesDirectory().exists()).isTrue();
        persistenceService.restore(mockedDao);
    }

    @Test
    public void persistCreatesDestinationDirectoryIfDoesNotExist() throws IOException, ClassNotFoundException {
        persistenceService.persist(mockedDao);

        assertThat(getPersistenceFilesDirectory().exists()).isTrue();
    }

    @Test
    public void persistAndRestoreWorks() throws IOException, ClassNotFoundException, RecordNotFoundException {
        PersistedDaoTest.TestRecord record = new PersistedDaoTest.TestRecord(A_VALUE);
        record.setId(AN_ID);
        when(mockedDao.list()).thenReturn(Arrays.asList(record));
        persistenceService.persist(mockedDao);

        List<PersistedDaoTest.TestRecord> restoredRecord = persistenceService.restore(mockedDao);

        assertThat(restoredRecord.size()).isEqualTo(1);
        assertThat(restoredRecord.get(0).getUniqueValue()).isEqualTo(record.getUniqueValue());
    }

    private void deleteAllData() throws IOException {
        FileUtils.deleteDirectory(getPersistenceFilesDirectory());
    }

    private File getPersistenceFilesDirectory() {
        return persistenceService.getDaoPersistencePath(mockedDao).getParentFile();
    }
}
