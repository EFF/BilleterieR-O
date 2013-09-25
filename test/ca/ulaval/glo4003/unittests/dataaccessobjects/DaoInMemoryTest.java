package ca.ulaval.glo4003.unittests.dataaccessobjects;

import ca.ulaval.glo4003.dataaccessobjects.DaoInMemory;
import ca.ulaval.glo4003.dataaccessobjects.RecordNotFoundException;
import ca.ulaval.glo4003.models.Record;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class DaoInMemoryTest {

    private DaoInMemory<Record> dao;

    @Before
    public void setUp() {
        dao = new DaoInMemory<Record>() {};
    }

    @Test
    public void createOneElement() {
        // Arrange
        Record record = new Record() {};
        dao.create(record);

        // Act
        List<Record> results = dao.list();

        // Assert
        assertEquals(1, dao.count());
        assertEquals(1, results.get(0).getId());
    }

    @Test
    public void createManyElements() {
        // Arrange
        Record record1 = new Record() {};
        Record record2 = new Record() {};
        Record record3 = new Record() {};
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
    public void readAnExistingRecordReturnThisRecord() throws RecordNotFoundException {
        // Arrange
        Record record = new Record() {};
        dao.create(record);

        // Act
        Record result = dao.read(1);

        // Assert
        assertEquals(record.getId(), result.getId());
    }

    @Test(expected = RecordNotFoundException.class)
    public void readUnexistingRecordThrowAnException() throws RecordNotFoundException {
        // Act
        dao.read(1);
    }

    @Test
    public void deleteAllRecords() {
        // Arrange
        Record record1 = new Record() {};
        Record record2 = new Record() {};
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
        Record record1 = new Record() {};
        Record record2 = new Record() {};
        dao.create(record1);
        dao.create(record2);

        // Act
        dao.delete(1);
        Record record = dao.read(2);

        // Assert
        assertEquals(1, dao.count());
    }
}
