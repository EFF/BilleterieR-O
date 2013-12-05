package ca.ulaval.glo4003.unittests.domain.ticketing;

import ca.ulaval.glo4003.domain.RecordNotFoundException;
import ca.ulaval.glo4003.domain.event.Category;
import ca.ulaval.glo4003.domain.event.Event;
import ca.ulaval.glo4003.domain.event.EventsInteractor;
import ca.ulaval.glo4003.domain.ticketing.*;
import com.google.inject.Inject;
import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(JukitoRunner.class)
public class TicketValidatorTest {
    private static final long AN_EVENT_ID = 12L;
    private static final long A_CATEGORY_ID = 123L;
    private static final int A_SEAT = 2;
    private static final String A_SECTION = "section";

    @Inject
    private TicketValidator ticketValidator;

    @Test(expected = RecordNotFoundException.class)
    public void validateTicketWhenNoEventShouldThrow(EventsInteractor mockedEventInteractor, TicketsInteractor mockedTicketInteractor)
            throws RecordNotFoundException, NoSuchCategoryException, NoSuchTicketSectionException, AlreadyAssignedSeatException {

        doThrow(new RecordNotFoundException()).when(mockedEventInteractor).getById(anyLong());

        ticketValidator.validate(AN_EVENT_ID, A_CATEGORY_ID, A_SECTION, A_SEAT);
    }

    @Test
    public void validateTicketShouldSucceed(EventsInteractor mockedEventInteractor, TicketsInteractor mockedTicketInteractor) throws RecordNotFoundException, AlreadyAssignedSeatException, NoSuchCategoryException, NoSuchTicketSectionException {
        Event mockedEvent = mock(Event.class);
        Category mockedCategory = mock(Category.class);
        Ticket mockedTicket = mock(Ticket.class);
        List<Category> mockedCategoryList = new ArrayList<>();
        mockedCategoryList.add(mockedCategory);
        List<Ticket> mockedTicketList = new ArrayList<>();
        mockedTicketList.add(mockedTicket);

        when(mockedTicket.getSeat()).thenReturn(A_SEAT);
        when(mockedEvent.getCategories()).thenReturn(mockedCategoryList);
        when(mockedTicketInteractor.search(any(TicketSearchCriteria.class))).thenReturn(mockedTicketList);
        when(mockedEventInteractor.getById(AN_EVENT_ID)).thenReturn(mockedEvent);
        when(mockedCategory.getId()).thenReturn(A_CATEGORY_ID);

        ticketValidator.validate(AN_EVENT_ID, A_CATEGORY_ID, A_SECTION, A_SEAT);
        verify(mockedCategory).getId();
    }

    @Test(expected = NoSuchCategoryException.class)
    public void validateTicketWhenNoCategoryShouldThrow(EventsInteractor mockedEventInteractor) throws RecordNotFoundException, AlreadyAssignedSeatException, NoSuchCategoryException, NoSuchTicketSectionException {
        Event mockedEvent = mock(Event.class);
        when(mockedEventInteractor.getById(AN_EVENT_ID)).thenReturn(mockedEvent);
        ticketValidator.validate(AN_EVENT_ID, A_CATEGORY_ID, A_SECTION, A_SEAT);
    }

    public static class TestModule extends JukitoModule {

        @Override
        protected void configureTest() {
            forceMock(EventsInteractor.class);
            forceMock(TicketsInteractor.class);
        }
    }

}
