package ca.ulaval.glo4003.unittests.services;

import ca.ulaval.glo4003.ConstantsManager;
import ca.ulaval.glo4003.models.Transaction;
import ca.ulaval.glo4003.models.TransactionState;
import ca.ulaval.glo4003.models.User;
import org.fest.assertions.Assertions;
import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class TransactionTest {

    @Test
    public void setsTimeOnInitialization() {
        Transaction transaction = new Transaction(new User());

        assertAboutSameDateTime(new LocalDateTime(), transaction.getStartedOn());
    }

    @Test
    public void setsUserOnInitialization() {
        User user = new User();

        Transaction transaction = new Transaction(user);

        assertEquals(user, transaction.getUser());
    }

    @Test
    public void isUnfulfilledOnInitialization() {
        Transaction transaction = new Transaction(new User());

        assertEquals(TransactionState.Unfulfilled, transaction.getState());
    }

    @Test
    public void fulfillChangesStateToFulfilled() {
        Transaction transaction = new Transaction(new User());

        transaction.fulfill();

        assertEquals(TransactionState.Fulfilled, transaction.getState());
    }

    @Test
    public void failChangesStateToFailed() {
        Transaction transaction = new Transaction(new User());

        transaction.fail();

        assertEquals(TransactionState.Failed, transaction.getState());
    }

    @Test
    public void fulfillUpdatesDate() {
        Transaction transaction = new Transaction(new User());

        transaction.fulfill();

        assertAboutSameDateTime(new LocalDateTime(), transaction.getEndedOn());
    }

    @Test
    public void failUpdatesDate() {
        Transaction transaction = new Transaction(new User());

        transaction.fail();

        assertAboutSameDateTime(new LocalDateTime(), transaction.getEndedOn());
    }

    private void assertAboutSameDateTime(LocalDateTime dt1, LocalDateTime dt2) {
        Period diff = Period.fieldDifference(dt1, dt2);
        Assertions.assertThat(diff.getMillis()).isLessThan(ConstantsManager.SERVICE_OPERATION_TIMEOUT);
    }
}
