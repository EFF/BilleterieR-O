package ca.ulaval.glo4003.unittests.domain.ticketing;

import ca.ulaval.glo4003.api.ticketing.ApiTicketingConstantsManager;
import ca.ulaval.glo4003.domain.RecordNotFoundException;
import ca.ulaval.glo4003.domain.ticketing.*;
import com.google.inject.Inject;
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

    private static final long A_TICKET_ID = 1;
    private static final Long AN_EVENT_ID = 12L;
    private static final Long A_CATEGORY_ID = 123L;
    private static final int A_SEAT = 2;
    private static final String A_SECTION = "section";
    private static final int A_QUANTITY = 7;
    @Inject
    private TicketsInteractor ticketsInteractor;

    @Test
    public void searchReturnsAListOfTickets(TicketDao mockedTicketDao) {
        List<Ticket> fakeEmptyList = new ArrayList<>();
        TicketSearchCriteria mockedTicketSearchCriteria = mock(TicketSearchCriteria.class);
        when(mockedTicketDao.search(mockedTicketSearchCriteria)).thenReturn(fakeEmptyList);

        List<Ticket> tickets = ticketsInteractor.search(mockedTicketSearchCriteria);

        assertEquals(fakeEmptyList, tickets);
        verify(mockedTicketDao).search(mockedTicketSearchCriteria);
    }

    @Test(expected = RecordNotFoundException.class)
    public void buyATicketThrowsRecordNotFoundExceptionIfTheTicketDoesNotExist(TicketDao mockedTicketDao) throws RecordNotFoundException, UpdateTicketStateUnauthorizedException {
        doThrow(new RecordNotFoundException()).when(mockedTicketDao).read(A_TICKET_ID);

        ticketsInteractor.buyATicket(A_TICKET_ID);
    }

    @Test(expected = UpdateTicketStateUnauthorizedException.class)
    public void buyATicketThrowsUpdateTIcketStateUnauthorizedExceptionIfTheTicketIsNotReseverd(TicketDao mockedTicketDao)
            throws RecordNotFoundException, UpdateTicketStateUnauthorizedException {
        Ticket mockedTicket = mock(Ticket.class);
        when(mockedTicket.getState()).thenReturn(TicketState.AVAILABLE);
        when(mockedTicketDao.read(A_TICKET_ID)).thenReturn(mockedTicket);

        ticketsInteractor.buyATicket(A_TICKET_ID);
    }

    @Test
    public void buyATicketUpdateTheTicketStateToSold(TicketDao mockedTicketDao) throws RecordNotFoundException, UpdateTicketStateUnauthorizedException {
        Ticket mockedTicket = mock(Ticket.class);
        when(mockedTicket.getState()).thenReturn(TicketState.RESERVED);
        when(mockedTicketDao.read(A_TICKET_ID)).thenReturn(mockedTicket);

        ticketsInteractor.buyATicket(A_TICKET_ID);

        InOrder inOrder = inOrder(mockedTicket, mockedTicketDao);
        inOrder.verify(mockedTicket, times(1)).setState(TicketState.SOLD);
        inOrder.verify(mockedTicketDao, times(1)).update(mockedTicket);
    }

    @Test(expected = RecordNotFoundException.class)
    public void freeATicketThrowsRecordNotFoundExceptionIfTheTicketDoesNotExist(TicketDao mockedTicketDao) throws
            RecordNotFoundException, UpdateTicketStateUnauthorizedException {
        doThrow(new RecordNotFoundException()).when(mockedTicketDao).read(A_TICKET_ID);

        ticketsInteractor.freeATicket(A_TICKET_ID);
    }

    @Test(expected = UpdateTicketStateUnauthorizedException.class)
    public void freeATicketThrowsUpdateTicketStateUnauthorizedExceptionIfTheTicketIsNotResevered(TicketDao mockedTicketDao)
            throws RecordNotFoundException, UpdateTicketStateUnauthorizedException {
        Ticket mockedTicket = mock(Ticket.class);
        when(mockedTicket.getState()).thenReturn(TicketState.AVAILABLE);
        when(mockedTicketDao.read(A_TICKET_ID)).thenReturn(mockedTicket);

        ticketsInteractor.freeATicket(A_TICKET_ID);
    }

    @Test
    public void freeATicketUpdateTheTicketStateToAvailable(TicketDao mockedTicketDao) throws RecordNotFoundException, UpdateTicketStateUnauthorizedException {
        Ticket mockedTicket = mock(Ticket.class);
        when(mockedTicket.getState()).thenReturn(TicketState.RESERVED);
        when(mockedTicketDao.read(A_TICKET_ID)).thenReturn(mockedTicket);

        ticketsInteractor.freeATicket(A_TICKET_ID);

        InOrder inOrder = inOrder(mockedTicket, mockedTicketDao);
        inOrder.verify(mockedTicket).setState(TicketState.AVAILABLE);
        inOrder.verify(mockedTicketDao).update(mockedTicket);
    }

    @Test(expected = RecordNotFoundException.class)
    public void reserveATicketThrowsRecordNotFoundExceptionIfTheTicketDoesNotExist(TicketDao mockedTicketDao) throws
            RecordNotFoundException, UpdateTicketStateUnauthorizedException {
        doThrow(new RecordNotFoundException()).when(mockedTicketDao).read(A_TICKET_ID);

        ticketsInteractor.reserveATicket(A_TICKET_ID);
    }

    @Test(expected = UpdateTicketStateUnauthorizedException.class)
    public void reserveATicketThrowsUpdateTicketStateUnauthorizedExceptionIfTheTicketIsNotFree(TicketDao mockedTicketDao)
            throws RecordNotFoundException, UpdateTicketStateUnauthorizedException {
        Ticket mockedTicket = mock(Ticket.class);
        when(mockedTicket.getState()).thenReturn(TicketState.RESERVED);
        when(mockedTicketDao.read(A_TICKET_ID)).thenReturn(mockedTicket);

        ticketsInteractor.reserveATicket(A_TICKET_ID);
    }

    @Test
    public void reserveATicketUpdateTheTicketStateToReserved(TicketDao mockedTicketDao) throws
            RecordNotFoundException, UpdateTicketStateUnauthorizedException {
        Ticket mockedTicket = mock(Ticket.class);
        when(mockedTicket.getState()).thenReturn(TicketState.AVAILABLE);
        when(mockedTicketDao.read(A_TICKET_ID)).thenReturn(mockedTicket);

        ticketsInteractor.reserveATicket(A_TICKET_ID);

        InOrder inOrder = inOrder(mockedTicket, mockedTicketDao);
        inOrder.verify(mockedTicket).setState(TicketState.RESERVED);
        inOrder.verify(mockedTicketDao).update(mockedTicket);
    }

    @Test(expected = RecordNotFoundException.class)
    public void getByIdThrowsRecordNotFoundIfTheTicketDoesNotExist(TicketDao mockedTicketDao) throws
            RecordNotFoundException {
        doThrow(new RecordNotFoundException()).when(mockedTicketDao).read(A_TICKET_ID);

        ticketsInteractor.getById(A_TICKET_ID);
    }

    @Test
    public void getByIdReturnsTheTicket(TicketDao mockedTicketDao) throws
            RecordNotFoundException {
        Ticket mockedTicket = mock(Ticket.class);
        when(mockedTicketDao.read(A_TICKET_ID)).thenReturn(mockedTicket);

        Ticket ticket = ticketsInteractor.getById(A_TICKET_ID);

        assertEquals(mockedTicket, ticket);
    }

    @Test
    public void numberOfTicketAvailableByEventId(TicketDao mockedTicketDao) {
        List<Ticket> mockedSearchResult = new ArrayList<>();
        mockedSearchResult.add(mock(Ticket.class));
        mockedSearchResult.add(mock(Ticket.class));
        when(mockedTicketDao.search(any(TicketSearchCriteria.class))).thenReturn(mockedSearchResult);

        int numberOfTickets = ticketsInteractor.numberOfTicketAvailable(AN_EVENT_ID);

        assertEquals(mockedSearchResult.size(), numberOfTickets);
        ArgumentCaptor<TicketSearchCriteria> argument = ArgumentCaptor.forClass(TicketSearchCriteria.class);
        verify(mockedTicketDao).search(argument.capture());
        assertEquals(AN_EVENT_ID, argument.getValue().getEventId());
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
        when(mockedTicketDao.search(any(TicketSearchCriteria.class))).thenReturn(mockedSearchResult);

        int numberOfTickets = ticketsInteractor.numberOfTicketAvailable(AN_EVENT_ID, A_CATEGORY_ID);

        assertEquals(mockedSearchResult.size(), numberOfTickets);
        ArgumentCaptor<TicketSearchCriteria> argument = ArgumentCaptor.forClass(TicketSearchCriteria.class);
        verify(mockedTicketDao).search(argument.capture());
        assertEquals(AN_EVENT_ID, argument.getValue().getEventId());
        assertEquals(A_CATEGORY_ID, argument.getValue().getCategoryId());
        assertNull(argument.getValue().getQuantity());
        assertNull(argument.getValue().getSectionName());
        assertEquals(1, argument.getValue().getStates().size());
        assertEquals(TicketState.AVAILABLE, argument.getValue().getStates().get(0));
    }

    @Test
    public void addGeneralAdmissionTicketShouldCreateATicket(TicketDao mockedTicketDao) {
        when(mockedTicketDao.create(any(Ticket.class))).thenReturn(mock(Ticket.class));

        ticketsInteractor.addGeneralAdmissionTickets(AN_EVENT_ID, A_CATEGORY_ID, A_QUANTITY);

        ArgumentCaptor<Ticket> argument = ArgumentCaptor.forClass(Ticket.class);
        verify(mockedTicketDao, times(A_QUANTITY)).create(argument.capture());

        assertEquals(AN_EVENT_ID.longValue(), argument.getValue().getEventId());
        assertEquals(A_CATEGORY_ID.longValue(), argument.getValue().getCategoryId());
        assertEquals("", argument.getValue().getSection());
        assertEquals(ApiTicketingConstantsManager.TICKET_INVALID_SEAT_NUMBER, argument.getValue().getSeat());
        assertEquals(TicketState.AVAILABLE, argument.getValue().getState());
    }

    @Test
    public void addManyGeneralAdmissionTicketShouldCreateManyTickets(TicketDao mockedTicketDao) throws Exception {
        when(mockedTicketDao.create(any(Ticket.class))).thenReturn(mock(Ticket.class));

        ticketsInteractor.addGeneralAdmissionTickets(AN_EVENT_ID, A_CATEGORY_ID, A_QUANTITY);

        ArgumentCaptor<Ticket> argument = ArgumentCaptor.forClass(Ticket.class);
        verify(mockedTicketDao, times(A_QUANTITY)).create(argument.capture());

        assertEquals(AN_EVENT_ID.longValue(), argument.getValue().getEventId());
        assertEquals(A_CATEGORY_ID.longValue(), argument.getValue().getCategoryId());
        assertEquals("", argument.getValue().getSection());
        assertEquals(ApiTicketingConstantsManager.TICKET_INVALID_SEAT_NUMBER, argument.getValue().getSeat());
        assertEquals(TicketState.AVAILABLE, argument.getValue().getState());
    }

    @Test
    public void addSingleSeatTicketShouldCreateATicketWithAValidSeat(TicketDao mockedTicketDao) throws Exception {
        when(mockedTicketDao.create(any(Ticket.class))).thenReturn(mock(Ticket.class));

        ticketsInteractor.addSingleSeatTicket(AN_EVENT_ID, A_CATEGORY_ID, A_SECTION, A_SEAT);

        ArgumentCaptor<Ticket> argument = ArgumentCaptor.forClass(Ticket.class);
        verify(mockedTicketDao).create(argument.capture());
        assertEquals(AN_EVENT_ID.longValue(), argument.getValue().getEventId());
        assertEquals(A_CATEGORY_ID.longValue(), argument.getValue().getCategoryId());
        assertEquals(A_SECTION, argument.getValue().getSection());
        assertEquals(A_SEAT, argument.getValue().getSeat());
        assertEquals(TicketState.AVAILABLE, argument.getValue().getState());
    }
}
