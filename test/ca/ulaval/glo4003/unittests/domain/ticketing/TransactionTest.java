package ca.ulaval.glo4003.unittests.domain.ticketing;

import ca.ulaval.glo4003.ConstantsManager;
import ca.ulaval.glo4003.domain.ticketing.Transaction;
import ca.ulaval.glo4003.domain.ticketing.TransactionState;
import ca.ulaval.glo4003.domain.user.User;
import org.fest.assertions.Assertions;
import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class TransactionTest {

    private static final String AN_EMAIL = "user@example.com";
    private static final String A_PASSWORD = "secret";

    @Test
    public void setsTimeOnInitialization() {
        Transaction transaction = new Transaction(createUser());

        assertAboutSameDateTime(new LocalDateTime(), transaction.getStartedOn());
    }

    @Test
    public void setsUserOnInitialization() {
        User user = createUser();

        Transaction transaction = new Transaction(user);

        assertEquals(user, transaction.getUser());
    }

    @Test
    public void isUnfulfilledOnInitialization() {
        Transaction transaction = new Transaction(createUser());

        assertEquals(TransactionState.Unfulfilled, transaction.getState());
    }

    @Test
    public void fulfillChangesStateToFulfilled() {
        Transaction transaction = new Transaction(createUser());

        transaction.fulfill();

        assertEquals(TransactionState.Fulfilled, transaction.getState());
    }

    @Test
    public void failChangesStateToFailed() {
        Transaction transaction = new Transaction(createUser());

        transaction.fail();

        assertEquals(TransactionState.Failed, transaction.getState());
    }

    @Test
    public void fulfillUpdatesDate() {
        Transaction transaction = new Transaction(createUser());

        transaction.fulfill();

        assertAboutSameDateTime(new LocalDateTime(), transaction.getEndedOn());
    }

    @Test
    public void failUpdatesDate() {
        Transaction transaction = new Transaction(createUser());

        transaction.fail();

        assertAboutSameDateTime(new LocalDateTime(), transaction.getEndedOn());
    }

    private void assertAboutSameDateTime(LocalDateTime dt1, LocalDateTime dt2) {
        Period diff = Period.fieldDifference(dt1, dt2);
        Assertions.assertThat(diff.getMillis()).isLessThan(ConstantsManager.SERVICE_OPERATION_TIMEOUT);
    }

    private User createUser() {
        return new User(AN_EMAIL, A_PASSWORD, false);
    }
}
