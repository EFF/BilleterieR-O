package ca.ulaval.glo4003.unittests.services;

import ca.ulaval.glo4003.dataaccessobjects.PersistedDao;
import ca.ulaval.glo4003.services.FileBasedDaoPersistenceService;
import ca.ulaval.glo4003.unittests.dataaccessobjects.PersistedDaoTest;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;

public class FileBasedDaoPersistenceServiceTest {

    FileBasedDaoPersistenceService persistenceService;
    private PersistedDao<PersistedDaoTest.TestRecord> dao;

    private static final String PROFILE = "UTests";

    @Before
    public void setUp() throws IOException {
        this.persistenceService = new FileBasedDaoPersistenceService(PROFILE);
        this.dao = new PersistedDao<PersistedDaoTest.TestRecord>(persistenceService) {};
        deleteAllData();
    }

    @After
    public void tearDown() throws IOException {
        deleteAllData();
    }

    @Test(expected = IOException.class)
    public void restoreThrowsWhenPersistenceFilesNotFound() throws IOException, ClassNotFoundException {
        // Act & Assert
        this.persistenceService.restore(this.dao);
    }

    @Test
    public void restoreDoesNotThrowWhenPersistenceFilesFound() throws IOException, ClassNotFoundException {
        // Arrange
        System.out.println(getPersistenceFilesDirectory().getAbsolutePath());
        this.persistenceService.persist(this.dao);

        // Act & Assert
        assertThat(getPersistenceFilesDirectory().exists()).isTrue();
        this.persistenceService.restore(this.dao);
    }

    @Test
    public void persistCreatesDestinationDirectoryIfDoesNotExist() throws IOException, ClassNotFoundException {
        // Arrange
        this.persistenceService.persist(this.dao);

        // Act & Assert
        assertThat(getPersistenceFilesDirectory().exists()).isTrue();
    }

    private void deleteAllData() throws IOException {
        FileUtils.deleteDirectory(this.getPersistenceFilesDirectory());
    }

    private File getPersistenceFilesDirectory() {
        return persistenceService.getDaoPersistencePath(this.dao).getParentFile();
    }

}
