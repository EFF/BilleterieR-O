package ca.ulaval.glo4003.unittests.services;

import ca.ulaval.glo4003.dataaccessobjects.PersistedDao;
import ca.ulaval.glo4003.services.FileBasedDaoPersistenceService;
import ca.ulaval.glo4003.unittests.dataaccessobjects.PersistedDaoTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class FileBasedDaoPersistenceServiceTest {

    FileBasedDaoPersistenceService persistenceService;
    private PersistedDao<PersistedDaoTest.TestRecord> dao;

    private static final String PROFILE = "UTests";

    @Before
    public void setUp() {
        deleteAllData();
        this.persistenceService = new FileBasedDaoPersistenceService(PROFILE);
        this.dao = new PersistedDao<PersistedDaoTest.TestRecord>(persistenceService) {};
    }

    @After
    public void tearDown() {
        deleteAllData();
    }

    @Test(expected = IOException.class)
    public void restoreThrowsWhenNoPersistenceFilesFound() throws IOException, ClassNotFoundException {
        // Act & Assert
        this.persistenceService.restore(this.dao);
    }

    private void deleteAllData() {
        try {
            this.recursiveDelete(this.getPersistenceFilesDirectory());
        }
        catch(Exception e) {
        }
    }

    private File getPersistenceFilesDirectory() {
        return new File(new File(new File(""), "data"), PROFILE);
    }

    public static void recursiveDelete(File file) throws IOException {
        if (file.isDirectory()) {
            if (file.list().length == 0) {
                file.delete();
            } else {
                String files[] = file.list();

                for (String temp : files) {
                    File fileDelete = new File(file, temp);
                    recursiveDelete(fileDelete);
                }

                if (file.list().length == 0) {
                    file.delete();
                }
            }

        } else {
            file.delete();
        }
    }

}
