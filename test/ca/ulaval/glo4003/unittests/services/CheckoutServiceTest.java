package ca.ulaval.glo4003.unittests.services;

import ca.ulaval.glo4003.dataaccessobjects.TransactionDao;
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
    private TransactionDao transactionDao;

    private static long TRANSACTION_USER_ID = 666;

    @Before
    public void setup() {
        this.transactionDao = mock(TransactionDao.class);
        this.checkoutService = new ConcreteCheckoutService(transactionDao);
    }

    @Test
    public void startingANewTransaction() {
        Transaction transaction = this.checkoutService.startNewTransaction(TRANSACTION_USER_ID);
        assertNotNull(transaction);
    }

    @Test
    public void startingANewTransaction_InsertsInDao() {
        Transaction transaction = this.checkoutService.startNewTransaction(TRANSACTION_USER_ID);

        verify(transactionDao, times(1)).create(transaction);
    }

    @Test
    public void startingANewTransaction_TransactionHasProvidedUserId() {
        Transaction transaction = this.checkoutService.startNewTransaction(TRANSACTION_USER_ID);

        assertEquals(TRANSACTION_USER_ID, transaction.getId());
    }

    @Test
    public void fulfillTransactionFulfillsTheTransaction() {
        Transaction transaction = mock(Transaction.class);

        checkoutService.fulfillTransaction(transaction);

        verify(transaction, times(1)).fulfill();
    }
}
