package ca.ulaval.glo4003.unittests.interactors;

import ca.ulaval.glo4003.dataaccessobjects.TicketDao;
import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.exceptions.UpdateTicketStateUnauthorizedException;
import ca.ulaval.glo4003.interactors.TicketsInteractor;
import ca.ulaval.glo4003.models.Ticket;
import ca.ulaval.glo4003.models.TicketSearchCriteria;
import ca.ulaval.glo4003.models.TicketState;
import com.google.inject.Inject;
import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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
    public void buyATicketThrowsRecordNotFoundExceptionIfTheTicketDoesNotExist(TicketDao mockedTicketDao) throws RecordNotFoundException, UpdateTicketStateUnauthorizedException {
        long ticketId = 1;
        doThrow(new RecordNotFoundException()).when(mockedTicketDao).read(ticketId);

        ticketsInteractor.buyATicket(ticketId);
    }

    @Test(expected = UpdateTicketStateUnauthorizedException.class)
    public void buyATicketThrowsUpdateTIcketStateUnauthorizedExceptionIfTheTicketIsNotReseverd(TicketDao mockedTicketDao)
            throws RecordNotFoundException, UpdateTicketStateUnauthorizedException {
        long ticketId = 1;
        Ticket mockedTicket = mock(Ticket.class);
        when(mockedTicket.getState()).thenReturn(TicketState.AVAILABLE);
        when(mockedTicketDao.read(ticketId)).thenReturn(mockedTicket);

        ticketsInteractor.buyATicket(ticketId);
    }

    @Test
    public void buyATicketUpdateTheTicketStateToSold(TicketDao mockedTicketDao) throws RecordNotFoundException, UpdateTicketStateUnauthorizedException {
        long ticketId = 1;
        Ticket mockedTicket = mock(Ticket.class);
        when(mockedTicket.getState()).thenReturn(TicketState.RESERVED);
        when(mockedTicketDao.read(ticketId)).thenReturn(mockedTicket);

        ticketsInteractor.buyATicket(ticketId);

        InOrder inOrder = inOrder(mockedTicket, mockedTicketDao);
        inOrder.verify(mockedTicket, times(1)).setState(TicketState.SOLD);
        inOrder.verify(mockedTicketDao, times(1)).update(mockedTicket);
    }

    @Test(expected = RecordNotFoundException.class)
    public void freeATicketThrowsRecordNotFoundExceptionIfTheTicketDoesNotExist(TicketDao mockedTicketDao) throws
            RecordNotFoundException, UpdateTicketStateUnauthorizedException {
        long ticketId = 1;
        doThrow(new RecordNotFoundException()).when(mockedTicketDao).read(ticketId);

        ticketsInteractor.freeATicket(ticketId);
    }

    @Test(expected = UpdateTicketStateUnauthorizedException.class)
    public void freeATicketThrowsUpdateTicketStateUnauthorizedExceptionIfTheTicketIsNotResevered(TicketDao mockedTicketDao)
            throws RecordNotFoundException, UpdateTicketStateUnauthorizedException {
        long ticketId = 1;
        Ticket mockedTicket = mock(Ticket.class);
        when(mockedTicket.getState()).thenReturn(TicketState.AVAILABLE);
        when(mockedTicketDao.read(ticketId)).thenReturn(mockedTicket);

        ticketsInteractor.freeATicket(ticketId);
    }

    @Test
    public void freeATicketUpdateTheTicketStateToAvailable(TicketDao mockedTicketDao) throws RecordNotFoundException, UpdateTicketStateUnauthorizedException {
        long ticketId = 1;
        Ticket mockedTicket = mock(Ticket.class);
        when(mockedTicket.getState()).thenReturn(TicketState.RESERVED);
        when(mockedTicketDao.read(ticketId)).thenReturn(mockedTicket);

        ticketsInteractor.freeATicket(ticketId);

        InOrder inOrder = inOrder(mockedTicket, mockedTicketDao);
        inOrder.verify(mockedTicket, times(1)).setState(TicketState.AVAILABLE);
        inOrder.verify(mockedTicketDao, times(1)).update(mockedTicket);
    }

    @Test(expected = RecordNotFoundException.class)
    public void reserveATicketThrowsRecordNotFoundExceptionIfTheTicketDoesNotExist(TicketDao mockedTicketDao) throws
            RecordNotFoundException, UpdateTicketStateUnauthorizedException {
        long ticketId = 1;
        doThrow(new RecordNotFoundException()).when(mockedTicketDao).read(ticketId);

        ticketsInteractor.reserveATicket(ticketId);
    }

    @Test(expected = UpdateTicketStateUnauthorizedException.class)
    public void reserveATicketThrowsUpdateTicketStateUnauthorizedExceptionIfTheTicketIsNotFree(TicketDao mockedTicketDao)
            throws RecordNotFoundException, UpdateTicketStateUnauthorizedException {
        long ticketId = 1;
        Ticket mockedTicket = mock(Ticket.class);
        when(mockedTicket.getState()).thenReturn(TicketState.RESERVED);
        when(mockedTicketDao.read(ticketId)).thenReturn(mockedTicket);

        ticketsInteractor.reserveATicket(ticketId);
    }

    @Test
    public void reserveATicketUpdateTheTicketStateToReserved(TicketDao mockedTicketDao) throws
            RecordNotFoundException, UpdateTicketStateUnauthorizedException {
        long ticketId = 1;
        Ticket mockedTicket = mock(Ticket.class);
        when(mockedTicket.getState()).thenReturn(TicketState.AVAILABLE);
        when(mockedTicketDao.read(ticketId)).thenReturn(mockedTicket);

        ticketsInteractor.reserveATicket(ticketId);

        InOrder inOrder = inOrder(mockedTicket, mockedTicketDao);
        inOrder.verify(mockedTicket, times(1)).setState(TicketState.RESERVED);
        inOrder.verify(mockedTicketDao, times(1)).update(mockedTicket);
    }

    @Test(expected = RecordNotFoundException.class)
    public void getByIdThrowsRecordNotFoundIfTheTicketDoesNotExist(TicketDao mockedTicketDao) throws
            RecordNotFoundException {
        long ticketId = 1;
        doThrow(new RecordNotFoundException()).when(mockedTicketDao).read(ticketId);

        ticketsInteractor.getById(ticketId);
    }

    @Test
    public void getByIdReturnsTheTicket(TicketDao mockedTicketDao) throws
            RecordNotFoundException {
        long ticketId = 1;
        Ticket mockedTicket = mock(Ticket.class);
        when(mockedTicketDao.read(ticketId)).thenReturn(mockedTicket);

        Ticket ticket = ticketsInteractor.getById(ticketId);

        assertEquals(mockedTicket, ticket);
    }

    @Test
    public void numberOfTicketAvailableByEventId(TicketDao mockedTicketDao) {
        List<Ticket> mockedSearchResult = new ArrayList<>();
        mockedSearchResult.add(mock(Ticket.class));
        mockedSearchResult.add(mock(Ticket.class));
        Long eventId = 1L;
        when(mockedTicketDao.search(any(TicketSearchCriteria.class))).thenReturn(mockedSearchResult);

        int numberOfTickets = ticketsInteractor.numberOfTicketAvailable(eventId);

        assertEquals(mockedSearchResult.size(), numberOfTickets);
        ArgumentCaptor<TicketSearchCriteria> argument = ArgumentCaptor.forClass(TicketSearchCriteria.class);
        verify(mockedTicketDao).search(argument.capture());
        assertEquals(eventId, argument.getValue().getEventId());
        assertNull(argument.getValue().getCategoryId());
        assertNull(argument.getValue().getQuantity());
        assertNull(argument.getValue().getSectionName());
        assertEquals(1, argument.getValue().getStates().size());
        assertEquals(TicketState.AVAILABLE, argument.getValue().getStates().get(0));
    }

    @Test
    public void numberOfTicketAvailableByEventIdAndCategoryId(TicketDao mockedTicketDao) {
        List<Ticket> mockedSearchResult = new ArrayList<>();
        mockedSearchResult.add(mock(Ticket.class));
        mockedSearchResult.add(mock(Ticket.class));
        Long eventId = 1L;
        Long categoryId = 2L;
        when(mockedTicketDao.search(any(TicketSearchCriteria.class))).thenReturn(mockedSearchResult);

        int numberOfTickets = ticketsInteractor.numberOfTicketAvailable(eventId, categoryId);

        assertEquals(mockedSearchResult.size(), numberOfTickets);
        ArgumentCaptor<TicketSearchCriteria> argument = ArgumentCaptor.forClass(TicketSearchCriteria.class);
        verify(mockedTicketDao).search(argument.capture());
        assertEquals(eventId, argument.getValue().getEventId());
        assertEquals(categoryId, argument.getValue().getCategoryId());
        assertNull(argument.getValue().getQuantity());
        assertNull(argument.getValue().getSectionName());
        assertEquals(1, argument.getValue().getStates().size());
        assertEquals(TicketState.AVAILABLE, argument.getValue().getStates().get(0));
    }

    public static class TestModule extends JukitoModule {

        @Override
        protected void configureTest() {
            forceMock(TicketDao.class);
        }
    }
}
