package ca.ulaval.glo4003.unittests.services;

import ca.ulaval.glo4003.ConstantsManager;
import ca.ulaval.glo4003.models.Transaction;
import ca.ulaval.glo4003.models.TransactionState;
import org.fest.assertions.Assertions;
import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class TransactionTest {
    @Test
    public void ctor_setsTime() {
        Transaction transaction = new Transaction();

        assertAboutSameDateTime(new LocalDateTime(), transaction.getStartedOn());
    }

    @Test
    public void ctor_isUnfulfilled() {
        Transaction transaction = new Transaction();

        assertEquals(TransactionState.Unfulfilled, transaction.getState());
    }

    @Test
    public void ctor_assignsRandomUUID() {
        Transaction transaction1 = new Transaction();
        Transaction transaction2 = new Transaction();
        Transaction transaction3 = new Transaction();

        Assertions.assertThat(transaction1.getId()).isNotEqualTo(transaction2.getId());
        Assertions.assertThat(transaction1.getId()).isNotEqualTo(transaction3.getId());
        Assertions.assertThat(transaction2.getId()).isNotEqualTo(transaction3.getId());
    }

    private void assertAboutSameDateTime(LocalDateTime dt1, LocalDateTime dt2) {
        Period diff = Period.fieldDifference(dt1, dt2);
        Assertions.assertThat(diff.getMillis()).isLessThan(ConstantsManager.SERVICE_OPERATION_TIMEOUT);
    }
}
