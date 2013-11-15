package ca.ulaval.glo4003.unittests.services;

import ca.ulaval.glo4003.dataaccessobjects.TransactionDao;
import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.models.Transaction;
import ca.ulaval.glo4003.models.User;
import ca.ulaval.glo4003.services.CheckoutService;
import ca.ulaval.glo4003.services.ConcreteCheckoutService;
import ca.ulaval.glo4003.services.EmailService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

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
        User user = new User();
        user.setEmail("me@random.com");
        Long transactionId = 75686735L;
        when(transaction.getUser()).thenReturn(user);
        when(transaction.getId()).thenReturn(transactionId);

        checkoutService.fulfillTransaction(transaction);

        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailService, times(1)).sendEmail(emailCaptor.capture(), any(String.class), messageCaptor.capture());
        assert(emailCaptor.getValue()).contains(user.getEmail());
        assert(messageCaptor.getValue()).contains(String.valueOf(transactionId));
    }
}
