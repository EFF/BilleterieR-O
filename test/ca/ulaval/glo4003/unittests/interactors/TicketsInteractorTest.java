package ca.ulaval.glo4003.unittests.interactors;

import ca.ulaval.glo4003.dataaccessobjects.TicketDao;
import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.interactors.TicketsInteractor;
import ca.ulaval.glo4003.models.Ticket;
import ca.ulaval.glo4003.models.TicketSearchCriteria;
import ca.ulaval.glo4003.models.TicketState;
import com.google.inject.Inject;
import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(JukitoRunner.class)
public class TicketsInteractorTest {

    @Inject
    private TicketsInteractor ticketsInteractor;

    @Test
    public void searchReturnsAListOfTickets(TicketDao mockedTicketDao) {
        List<Ticket> fakeEmptyList = new ArrayList<>();
        TicketSearchCriteria mockedTicketSearchCriteria = mock(TicketSearchCriteria.class);
        when(mockedTicketDao.search(mockedTicketSearchCriteria)).thenReturn(fakeEmptyList);

        List<Ticket> tickets = ticketsInteractor.search(mockedTicketSearchCriteria);

        assertEquals(fakeEmptyList, tickets);
        verify(mockedTicketDao, times(1)).search(mockedTicketSearchCriteria);
    }

    @Test(expected = RecordNotFoundException.class)
    public void buyATicketThrowsRecordNotFoundExceptionIfTheTicketDoesNotExist(TicketDao mockedTicketDao) throws RecordNotFoundException {
        long ticketId = 1;
        doThrow(new RecordNotFoundException()).when(mockedTicketDao).read(ticketId);

        ticketsInteractor.buyATicket(ticketId);
    }

    @Test
    public void buyATicketUpdateTheTicketStateToSold(TicketDao mockedTicketDao) throws RecordNotFoundException {
        long ticketId = 1;
        Ticket mockedTicket = mock(Ticket.class);
        when(mockedTicketDao.read(ticketId)).thenReturn(mockedTicket);

        ticketsInteractor.buyATicket(ticketId);

        InOrder inOrder = inOrder(mockedTicket, mockedTicketDao);
        inOrder.verify(mockedTicket, times(1)).setState(TicketState.SOLD);
        inOrder.verify(mockedTicketDao, times(1)).update(mockedTicket);
    }

    @Test(expected = RecordNotFoundException.class)
    public void freeATicketThrowsRecordNotFoundExceptionIfTheTicketDoesNotExist(TicketDao mockedTicketDao) throws
            RecordNotFoundException {
        long ticketId = 1;
        doThrow(new RecordNotFoundException()).when(mockedTicketDao).read(ticketId);

        ticketsInteractor.freeATicket(ticketId);
    }

    @Test
    public void freeATicketUpdateTheTicketStateToAvailable(TicketDao mockedTicketDao) throws RecordNotFoundException {
        long ticketId = 1;
        Ticket mockedTicket = mock(Ticket.class);
        when(mockedTicketDao.read(ticketId)).thenReturn(mockedTicket);

        ticketsInteractor.freeATicket(ticketId);

        InOrder inOrder = inOrder(mockedTicket, mockedTicketDao);
        inOrder.verify(mockedTicket, times(1)).setState(TicketState.AVAILABLE);
        inOrder.verify(mockedTicketDao, times(1)).update(mockedTicket);
    }

    @Test(expected = RecordNotFoundException.class)
    public void reserveATicketThrowsRecordNotFoundExceptionIfTheTicketDoesNotExist(TicketDao mockedTicketDao) throws
            RecordNotFoundException {
        long ticketId = 1;
        doThrow(new RecordNotFoundException()).when(mockedTicketDao).read(ticketId);

        ticketsInteractor.reserveATicket(ticketId);
    }

    @Test
    public void reserveATicketUpdateTheTicketStateToReserved(TicketDao mockedTicketDao) throws
            RecordNotFoundException {
        long ticketId = 1;
        Ticket mockedTicket = mock(Ticket.class);
        when(mockedTicketDao.read(ticketId)).thenReturn(mockedTicket);

        ticketsInteractor.reserveATicket(ticketId);

        InOrder inOrder = inOrder(mockedTicket, mockedTicketDao);
        inOrder.verify(mockedTicket, times(1)).setState(TicketState.RESERVED);
        inOrder.verify(mockedTicketDao, times(1)).update(mockedTicket);
    }

    public static class TestModule extends JukitoModule {

        @Override
        protected void configureTest() {
            forceMock(TicketDao.class);
        }
    }

}
