package ca.ulaval.glo4003.dataaccessobjects;

import ca.ulaval.glo4003.exceptions.UniqueConstraintException;
import ca.ulaval.glo4003.models.Record;
import org.junit.Test;

import javax.persistence.Column;
import java.util.ArrayList;
import java.util.List;

public class UniqueConstraintValidatorTest {

    // TODO: 3 tests, unique, same with same id, same with different id => throw
    @Test
    public void validateWithUniqueValues() throws UniqueConstraintException {
        UniqueConstraintValidator uniqueConstraintValidator = new UniqueConstraintValidator();
        TestRecord record1 = new TestRecord("A");
        TestRecord record2 = new TestRecord("B");
        List<Record> records = new ArrayList<>();
        records.add(record1);


        uniqueConstraintValidator.validate(records, record2);
    }

    private class TestRecord extends Record {
        @Column(unique = true)
        private String uniqueField;

        private TestRecord(String uniqueField) {
            this.uniqueField = uniqueField;
        }
    }
}
