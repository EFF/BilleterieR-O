package ca.ulaval.glo4003.unittests.dataaccessobjects;

import ca.ulaval.glo4003.dataaccessobjects.UniqueConstraintValidator;
import ca.ulaval.glo4003.models.Record;
import org.junit.Test;

import javax.persistence.Column;
import javax.validation.ValidationException;
import java.util.Arrays;
import java.util.List;

public class UniqueConstraintValidatorTest {

    private static final int AN_ID = 1;
    private static final int ANOTHER_ID = 2;
    private static final String A_VALUE = "A";
    private static final String ANOTHER_VALUE = "B";

    @Test
    public void validateWithUniqueValuesShouldNotThrow() {
        UniqueConstraintValidator<TestRecord> uniqueConstraintValidator = new UniqueConstraintValidator<>();
        TestRecord record1 = new TestRecord(AN_ID, A_VALUE);
        TestRecord record2 = new TestRecord(ANOTHER_ID, ANOTHER_VALUE);
        List<TestRecord> records = Arrays.asList(record1, record2);

        uniqueConstraintValidator.validate(records, record2);
    }

    @Test
    public void validateWithNonUniqueValuesWithTheSameIdShouldNotThrow() {
        UniqueConstraintValidator<TestRecord> uniqueConstraintValidator = new UniqueConstraintValidator<>();
        TestRecord record1 = new TestRecord(AN_ID, A_VALUE);
        TestRecord record2 = new TestRecord(AN_ID, A_VALUE);
        List<TestRecord> records = Arrays.asList(record1, record2);

        uniqueConstraintValidator.validate(records, record2);
    }

    @Test(expected = ValidationException.class)
    public void validateWithNonUniqueValuesWithDifferentIdShouldThrow() {
        UniqueConstraintValidator<TestRecord> uniqueConstraintValidator = new UniqueConstraintValidator<>();
        TestRecord record1 = new TestRecord(AN_ID, A_VALUE);
        TestRecord record2 = new TestRecord(ANOTHER_ID, A_VALUE);
        List<TestRecord> records = Arrays.asList(record1, record2);

        uniqueConstraintValidator.validate(records, record2);
    }

    public static class TestRecord extends Record {

        @Column(unique = true)
        private String uniqueField;

        private TestRecord(long id, String uniqueField) {
            setId(id);
            this.uniqueField = uniqueField;
        }

        public String getUniqueField() {
            return uniqueField;
        }
    }
}
