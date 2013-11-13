package ca.ulaval.glo4003.unittests.services;

import ca.ulaval.glo4003.dataaccessobjects.TransactionDao;
import ca.ulaval.glo4003.dataaccessobjects.UserDao;
import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.models.Transaction;
import ca.ulaval.glo4003.models.TransactionState;
import ca.ulaval.glo4003.models.User;
import ca.ulaval.glo4003.services.CheckoutService;
import ca.ulaval.glo4003.services.ConcreteCheckoutService;
import ca.ulaval.glo4003.services.EmailService;
import org.fest.assertions.Assertions;
import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.atLeastOnce;

public class CheckoutServiceTest {

    private CheckoutService checkoutService;
    private TransactionDao transactionDao;
    private EmailService emailService;

    @Before
    public void setup() {
        this.transactionDao = mock(TransactionDao.class);
        this.emailService = mock(EmailService.class);
        this.checkoutService = new ConcreteCheckoutService(transactionDao, emailService);
    }

    @Test
    public void startingANewTransaction() {
        Transaction transaction = this.checkoutService.startNewTransaction(new User());
        assertNotNull(transaction);
    }

    @Test
    public void startingANewTransaction_InsertsInDao() {
        Transaction transaction = this.checkoutService.startNewTransaction(new User());

        verify(transactionDao, times(1)).create(transaction);
    }

    @Test
    public void startingANewTransaction_TransactionHasProvidedUser() {
        User user = new User();
        user.setId(300);

        Transaction transaction = this.checkoutService.startNewTransaction(user);

        assertEquals(user.getId(), transaction.getUser().getId());
    }

    @Test
    public void fulfillTransaction_FulfillsTheTransaction() throws RecordNotFoundException {
        Transaction transaction = mock(Transaction.class);
        when(transaction.getUser()).thenReturn(new User());

        checkoutService.fulfillTransaction(transaction);

        verify(transaction, times(1)).fulfill();
    }

    @Test
    public void fulfillTransaction_SendsConfirmationEmail() {
        Transaction transaction = mock(Transaction.class);
        when(transaction.getUser()).thenReturn(new User());

        checkoutService.fulfillTransaction(transaction);

        verify(emailService, times(1)).SendEmail(any(String.class), any(String.class), any(String.class));
    }
}
