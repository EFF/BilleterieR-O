package ca.ulaval.glo4003.unittests.dataaccessobjects;

import ca.ulaval.glo4003.dataaccessobjects.PersistedDao;
import ca.ulaval.glo4003.dataaccessobjects.RecordNotFoundException;
import ca.ulaval.glo4003.models.Record;
import ca.ulaval.glo4003.services.InMemoryDaoPersistenceService;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class PersistedDaoTest {

    private static final int UNEXISTING_ID = 10;
    private static final int A_VALUE = 50;
    private static final int ANOTHER_VALUE = 100;

    private PersistedDao<TestRecord> dao;

    @Before
    public void setUp() {
        dao = new PersistedDao<TestRecord>(new InMemoryDaoPersistenceService()) {};
    }

    @Test
    public void createOneElement() {
        // Arrange
        TestRecord record = new TestRecord();
        dao.create(record);

        // Act
        List<TestRecord> results = dao.list();

        // Assert
        assertEquals(1, dao.count());
        assertEquals(1, results.get(0).getId());
    }

    @Test
    public void createManyElements() {
        // Arrange
        TestRecord record1 = new TestRecord();
        TestRecord record2 = new TestRecord();
        TestRecord record3 = new TestRecord();
        dao.create(record1);
        dao.create(record2);
        dao.create(record3);

        // Act
        int count = dao.count();

        // Assert
        assertEquals(3, count);
        assertEquals(1, dao.list().get(0).getId());
        assertEquals(2, dao.list().get(1).getId());
        assertEquals(3, dao.list().get(2).getId());
    }

    @Test
    public void changesToARecordWithoutSavingItDoesntAlterTheDb() throws RecordNotFoundException {
        // Arrange
        TestRecord record = new TestRecord();
        record.setValue(A_VALUE);
        dao.create(record);

        // Act
        record.setValue(ANOTHER_VALUE);

        // Assert
        assertEquals(A_VALUE, dao.read(1).getValue());
    }

    @Test
    public void readAnExistingRecordReturnThisRecord() throws RecordNotFoundException {
        // Arrange
        TestRecord record = new TestRecord();
        dao.create(record);

        // Act
        Record result = dao.read(1);

        // Assert
        assertEquals(record.getId(), result.getId());
    }

    @Test(expected = RecordNotFoundException.class)
    public void readUnexistingRecordThrowAnException() throws RecordNotFoundException {
        // Act
        dao.read(UNEXISTING_ID);
    }

    @Test
    public void deleteAllRecords() {
        // Arrange
        TestRecord record1 = new TestRecord();
        TestRecord record2 = new TestRecord();

        dao.create(record1);
        dao.create(record2);

        // Act
        dao.deleteAll();

        // Assert
        assertEquals(0, dao.count());
    }

    @Test
    public void deleteOneRecord() throws RecordNotFoundException {
        // Arrange
        TestRecord record1 = new TestRecord();
        TestRecord record2 = new TestRecord();
        dao.create(record1);
        dao.create(record2);

        // Act
        dao.delete(1);
        dao.read(2);

        // Assert
        assertEquals(1, dao.count());
    }

    @Test(expected = RecordNotFoundException.class)
    public void deleteUnexistingRecord() throws RecordNotFoundException {
        // Arrange
        TestRecord record = new TestRecord();
        dao.create(record);

        // Act
        dao.delete(UNEXISTING_ID);
    }

    @Test
    public void updateRecord() throws RecordNotFoundException {
        // Arrange
        TestRecord record1 = new TestRecord();
        record1.setValue(A_VALUE);
        TestRecord record2 = new TestRecord();
        dao.create(record1);
        dao.create(record2);

        // Act
        record1.setValue(ANOTHER_VALUE);
        dao.update(record1);

        // Assert
        assertEquals(2, dao.count());
        assertEquals(ANOTHER_VALUE, dao.read(record1.getId()).getValue());
    }

    @Test(expected = RecordNotFoundException.class)
    public void updateUnsavedRecordThrowAnException() throws RecordNotFoundException {
        // Arrange
        TestRecord unsavedRecord = new TestRecord();

        // Act
        dao.update(unsavedRecord);
    }

    @Test(expected = RecordNotFoundException.class)
    public void updatePreviouslyDeletedRecordThrowAnException() throws RecordNotFoundException {
        // Arrange
        TestRecord record = new TestRecord();
        dao.create(record);
        dao.delete(record.getId());

        // Act
        dao.update(record);
    }

    public static class TestRecord extends Record {

        private int value;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}
