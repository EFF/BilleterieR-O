package ca.ulaval.glo4003.unittests.dataaccessobjects;

import ca.ulaval.glo4003.dataaccessobjects.PersistedDao;
import ca.ulaval.glo4003.dataaccessobjects.UniqueConstraintValidator;
import ca.ulaval.glo4003.dataaccessobjects.UniqueValidationException;
import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.models.Record;
import ca.ulaval.glo4003.services.DaoPersistenceService;
import org.junit.Before;
import org.junit.Test;
import play.data.validation.Constraints;

import javax.persistence.Column;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class PersistedDaoTest {

    private static final int UNEXISTING_ID = 10;
    private static final int A_VALUE = 50;
    private static final int ANOTHER_VALUE = 100;
    private static final int YET_ANOTHER_VALUE = 150;
    private static final int INVALID_VALUE = -1;
    private static final int MIN_VALUE_CONSTRAINT = 0;

    private PersistedDao<TestRecord> dao;
    private DaoPersistenceService mockedPersistenceService;

    @Before
    public void setUp() {
        mockedPersistenceService = mock(DaoPersistenceService.class);
        dao = new PersistedDao<TestRecord>(mockedPersistenceService,
                new UniqueConstraintValidator<TestRecord>()) {};
    }

    @Test
    public void createOneElement() {
        TestRecord record = new TestRecord(A_VALUE);
        dao.create(record);

        List<TestRecord> results = dao.list();

        assertEquals(1, dao.count());
        assertEquals(1, results.get(0).getId());
    }

    @Test
    public void createManyElements() {
        TestRecord record1 = new TestRecord(A_VALUE);
        TestRecord record2 = new TestRecord(ANOTHER_VALUE);
        TestRecord record3 = new TestRecord(YET_ANOTHER_VALUE);
        dao.create(record1);
        dao.create(record2);
        dao.create(record3);

        int count = dao.count();

        assertEquals(3, count);
        assertEquals(1, dao.list().get(0).getId());
        assertEquals(2, dao.list().get(1).getId());
        assertEquals(3, dao.list().get(2).getId());
    }

    @Test(expected = UniqueValidationException.class)
    public void createTwoElementsWithNonUniqueValueShouldThrowAnException() {
        TestRecord record1 = new TestRecord(A_VALUE);
        TestRecord record2 = new TestRecord(A_VALUE);

        dao.create(record1);
        dao.create(record2);
    }

    @Test(expected = ConstraintViolationException.class)
    public void createElementWithInvalidValueShouldThrowAnException() {
        TestRecord record = new TestRecord(INVALID_VALUE);

        dao.create(record);
    }

    @Test
    public void changesToARecordWithoutSavingItDoesntAlterTheDb() throws RecordNotFoundException {
        TestRecord record = new TestRecord(A_VALUE);
        dao.create(record);

        record.setUniqueValue(ANOTHER_VALUE);

        assertEquals(A_VALUE, dao.read(1).getUniqueValue());
    }

    @Test
    public void readAnExistingRecordReturnThisRecord() throws RecordNotFoundException {
        TestRecord record = new TestRecord(A_VALUE);
        dao.create(record);

        Record result = dao.read(1);

        assertEquals(record.getId(), result.getId());
    }

    @Test(expected = RecordNotFoundException.class)
    public void readUnexistingRecordThrowAnException() throws RecordNotFoundException {
        dao.read(UNEXISTING_ID);
    }

    @Test
    public void deleteAllRecords() {
        TestRecord record1 = new TestRecord(A_VALUE);
        TestRecord record2 = new TestRecord(ANOTHER_VALUE);
        dao.create(record1);
        dao.create(record2);

        dao.deleteAll();

        assertEquals(0, dao.count());
    }

    @Test
    public void deleteOneRecord() throws RecordNotFoundException {
        TestRecord record1 = new TestRecord(A_VALUE);
        TestRecord record2 = new TestRecord(ANOTHER_VALUE);
        dao.create(record1);
        dao.create(record2);

        dao.delete(1);
        dao.read(2);

        assertEquals(1, dao.count());
    }

    @Test(expected = RecordNotFoundException.class)
    public void deleteUnexistingRecord() throws RecordNotFoundException {
        TestRecord record = new TestRecord(A_VALUE);
        dao.create(record);

        dao.delete(UNEXISTING_ID);
    }

    @Test
    public void updateRecord() throws RecordNotFoundException {
        TestRecord record1 = new TestRecord(A_VALUE);
        TestRecord record2 = new TestRecord(ANOTHER_VALUE);
        dao.create(record1);
        dao.create(record2);

        record1.setUniqueValue(YET_ANOTHER_VALUE);
        dao.update(record1);

        assertEquals(2, dao.count());
        assertEquals(YET_ANOTHER_VALUE, dao.read(record1.getId()).getUniqueValue());
    }

    @Test(expected = RecordNotFoundException.class)
    public void updateUnsavedRecordThrowAnException() throws RecordNotFoundException {
        TestRecord unsavedRecord = new TestRecord(A_VALUE);

        dao.update(unsavedRecord);
    }

    @Test(expected = RecordNotFoundException.class)
    public void updatePreviouslyDeletedRecordThrowAnException() throws RecordNotFoundException {
        TestRecord record = new TestRecord(A_VALUE);
        dao.create(record);
        dao.delete(record.getId());

        dao.update(record);
    }

    @Test
    public void persistsOnAdd() throws IOException {
        dao.create(new TestRecord(A_VALUE));

        verify(mockedPersistenceService, times(1)).persist(dao);
    }

    @Test
    public void persistsOnUpdate() throws IOException, RecordNotFoundException {
        TestRecord record = new TestRecord(A_VALUE);

        dao.create(record);
        record.setUniqueValue(2);
        dao.update(record);

        verify(mockedPersistenceService, times(2)).persist(dao);
    }

    @Test
    public void persistsOnEveryUpdates() throws IOException, RecordNotFoundException {
        TestRecord record = new TestRecord(A_VALUE);

        dao.create(record);
        record.setUniqueValue(2);
        dao.update(record);
        record.setUniqueValue(3);
        dao.update(record);
        record.setUniqueValue(5);
        dao.update(record);

        verify(mockedPersistenceService, times(4)).persist(dao);
    }

    @Test
    public void restoreOnDaoCreation() throws IOException, ClassNotFoundException {
        verify(mockedPersistenceService, times(1)).restore(dao);
    }

    public static class TestRecord extends Record {

        @Column(unique = true)
        @Constraints.Min(MIN_VALUE_CONSTRAINT)
        private int uniqueValue;

        public TestRecord(int uniqueValue) {
            this.uniqueValue = uniqueValue;
        }

        public int getUniqueValue() {
            return uniqueValue;
        }

        public void setUniqueValue(int uniqueValue) {
            this.uniqueValue = uniqueValue;
        }
    }
}
