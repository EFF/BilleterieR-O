package ca.ulaval.glo4003.unittests.interactors;

import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.exceptions.UpdateTicketStateUnauthorizedException;
import ca.ulaval.glo4003.interactors.CheckoutInteractor;
import ca.ulaval.glo4003.interactors.TicketsInteractor;
import ca.ulaval.glo4003.models.Transaction;
import ca.ulaval.glo4003.models.User;
import ca.ulaval.glo4003.services.CheckoutService;
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

    @Rule
    public ExpectedException exception = ExpectedException.none();
    @Inject
    private CheckoutInteractor checkoutInteractor;

    @Test
    public void executeTransactionWithExistingUserAndExistingTickets(CheckoutService mockedCheckoutService,
                                                                     TicketsInteractor mockedTicketsInteractor) throws RecordNotFoundException, UpdateTicketStateUnauthorizedException {
        long fakeId1 = 12;
        long fakeId2 = 23;
        User mockedBuyer = mock(User.class);
        List<Long> ticketIds = new ArrayList<>();
        ticketIds.add(fakeId1);
        ticketIds.add(fakeId2);
        Transaction mockedTransaction = mock(Transaction.class);
        when(mockedCheckoutService.startNewTransaction(mockedBuyer)).thenReturn(mockedTransaction);

        Transaction transaction = checkoutInteractor.executeTransaction(mockedBuyer, ticketIds);

        assertEquals(mockedTransaction, transaction);
        InOrder inOrder = inOrder(mockedCheckoutService, mockedTicketsInteractor);
        inOrder.verify(mockedCheckoutService).startNewTransaction(mockedBuyer);
        inOrder.verify(mockedTicketsInteractor).buyATicket(fakeId1);
        inOrder.verify(mockedTicketsInteractor).buyATicket(fakeId2);
    }

    @Test
    public void executeTransactionFailIfOneTicketIsNotFound(CheckoutService mockedCheckoutService,
                                                            TicketsInteractor mockedTicketsInteractor) throws RecordNotFoundException, UpdateTicketStateUnauthorizedException {
        long fakeId1 = 12;
        long fakeId2 = 23;
        User mockedBuyer = mock(User.class);
        List<Long> ticketIds = new ArrayList<>();
        ticketIds.add(fakeId1);
        ticketIds.add(fakeId2);
        Transaction mockedTransaction = mock(Transaction.class);
        when(mockedCheckoutService.startNewTransaction(mockedBuyer)).thenReturn(mockedTransaction);
        doThrow(new RecordNotFoundException()).when(mockedTicketsInteractor).buyATicket(fakeId1);

        Transaction transaction = checkoutInteractor.executeTransaction(mockedBuyer, ticketIds);

        assertEquals(mockedTransaction, transaction);
        InOrder inOrder = inOrder(mockedCheckoutService, mockedTicketsInteractor, mockedTransaction);
        inOrder.verify(mockedCheckoutService).startNewTransaction(mockedBuyer);
        inOrder.verify(mockedTicketsInteractor).buyATicket(fakeId1);
        inOrder.verify(mockedTransaction).fail();
        verify(mockedTicketsInteractor, never()).buyATicket(fakeId2);
        verify(mockedCheckoutService, never()).fulfillTransaction(any(Transaction.class));
    }

    public static class TestModule extends JukitoModule {

        @Override
        protected void configureTest() {
            forceMock(TicketsInteractor.class);
        }
    }
}
