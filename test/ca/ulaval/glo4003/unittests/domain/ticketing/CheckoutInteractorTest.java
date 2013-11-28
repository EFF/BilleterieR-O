package ca.ulaval.glo4003.unittests.domain.ticketing;

import ca.ulaval.glo4003.domain.RecordNotFoundException;
import ca.ulaval.glo4003.domain.ticketing.*;
import ca.ulaval.glo4003.domain.user.User;
import ca.ulaval.glo4003.domain.EmailService;
import com.google.inject.Inject;
import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InOrder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(JukitoRunner.class)
public class CheckoutInteractorTest {

    private static final long TICKET_ID_1 = 1;
    private static final long TICKET_ID_2 = 123;
    private static final String AN_EMAIL = "test@example.com";
    @Rule
    public ExpectedException exception;
    @Inject
    private CheckoutInteractor checkoutInteractor;

    @Test
    public void executeTransactionWithExistingUserAndExistingTickets(TicketsInteractor mockedTicketsInteractor,
                                                                     TransactionFactory mockedTransactionFactory,
                                                                     EmailService mockedEmailService)
            throws RecordNotFoundException, UpdateTicketStateUnauthorizedException {
        User mockedBuyer = mock(User.class);
        List<Long> ticketIds = new ArrayList<>();
        ticketIds.add(TICKET_ID_1);
        ticketIds.add(TICKET_ID_2);
        Transaction mockedTransaction = mock(Transaction.class);
        when(mockedTransactionFactory.createTransaction(mockedBuyer)).thenReturn(mockedTransaction);
        when(mockedTransaction.getUser()).thenReturn(mockedBuyer);
        when(mockedBuyer.getEmail()).thenReturn(AN_EMAIL);

        Transaction transaction = checkoutInteractor.executeTransaction(mockedBuyer, ticketIds);

        assertEquals(mockedTransaction, transaction);
        InOrder inOrder = inOrder(mockedTicketsInteractor, mockedTransaction, mockedEmailService,
                mockedTransactionFactory);
        inOrder.verify(mockedTransactionFactory).createTransaction(mockedBuyer);
        inOrder.verify(mockedTicketsInteractor).buyATicket(TICKET_ID_1);
        inOrder.verify(mockedTicketsInteractor).buyATicket(TICKET_ID_2);
        inOrder.verify(mockedTransaction).fulfill();
        inOrder.verify(mockedEmailService).sendSystemEmail(eq(AN_EMAIL), anyString());
    }

    @Test
    public void executeTransactionFailIfOneTicketIsNotFound(TicketsInteractor mockedTicketsInteractor,
                                                            TransactionFactory mockedTransactionFactory,
                                                            EmailService mockedEmailService)
            throws RecordNotFoundException, UpdateTicketStateUnauthorizedException {
        User mockedBuyer = mock(User.class);
        List<Long> ticketIds = new ArrayList<>();
        ticketIds.add(TICKET_ID_1);
        ticketIds.add(TICKET_ID_2);
        Transaction mockedTransaction = mock(Transaction.class);
        when(mockedTransactionFactory.createTransaction(mockedBuyer)).thenReturn(mockedTransaction);
        doThrow(new RecordNotFoundException()).when(mockedTicketsInteractor).buyATicket(TICKET_ID_1);

        Transaction transaction = checkoutInteractor.executeTransaction(mockedBuyer, ticketIds);

        assertEquals(mockedTransaction, transaction);
        InOrder inOrder = inOrder(mockedTicketsInteractor, mockedTransaction, mockedEmailService,
                mockedTransactionFactory);
        inOrder.verify(mockedTransactionFactory).createTransaction(mockedBuyer);
        inOrder.verify(mockedTicketsInteractor).buyATicket(TICKET_ID_1);
        inOrder.verify(mockedTransaction).fail();
        verify(mockedTicketsInteractor, never()).buyATicket(TICKET_ID_2);
        verify(mockedEmailService, never()).sendSystemEmail(anyString(), anyString());
    }

    public static class TestModule extends JukitoModule {

        @Override
        protected void configureTest() {
            forceMock(TicketsInteractor.class);
            forceMock(TransactionFactory.class);
        }
    }
}
