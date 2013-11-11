package ca.ulaval.glo4003.unittests.services;

import ca.ulaval.glo4003.models.Transaction;
import ca.ulaval.glo4003.models.TransactionState;
import ca.ulaval.glo4003.services.CheckoutService;
import ca.ulaval.glo4003.services.ConcreteCheckoutService;
import org.fest.assertions.Assertions;
import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.atLeastOnce;

public class CheckoutServiceTest {

    private CheckoutService checkoutService;

    @Before
    public void setup() {
        this.checkoutService = new ConcreteCheckoutService();
    }

    @Test
    public void startingANewTransaction() {
        Transaction transaction = this.checkoutService.startNewTransaction();
        assertNotNull(transaction);
    }

    @Test
    public void fulfillTransaction_fulfills_the_transaction() {
        Transaction transaction = mock(Transaction.class);

        checkoutService.fulfillTransaction(transaction);

        verify(transaction, times(1)).fulfill();
    }
}
