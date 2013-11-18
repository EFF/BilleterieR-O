package ca.ulaval.glo4003.unittests.interactors;

import ca.ulaval.glo4003.dataaccessobjects.EventDao;
import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.interactors.EventsInteractor;
import ca.ulaval.glo4003.models.Event;
import ca.ulaval.glo4003.models.EventSearchCriteria;
import com.google.inject.Inject;
import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(JukitoRunner.class)
public class EventsInteractorTest {

    @Inject
    private EventsInteractor eventsInteractor;

    @Test
    public void readExistingIdReturnsTheEvent(EventDao mockedEventDao) throws RecordNotFoundException {
        long eventId = 1;
        Event mockedEvent = mock(Event.class);
        when(mockedEventDao.read(eventId)).thenReturn(mockedEvent);

        Event result = eventsInteractor.getById(eventId);

        assertEquals(mockedEvent, result);
        verify(mockedEventDao, times(1)).read(eventId);
    }

    @Test(expected = RecordNotFoundException.class)
    public void readNonExistingIdThrowsRecordNotFoundException(EventDao mockedEventDao) throws RecordNotFoundException {
        long eventId = 1;
        doThrow(new RecordNotFoundException()).when(mockedEventDao).read(eventId);

        eventsInteractor.getById(eventId);
    }

    @Test
    public void returnsAListOfEvents(EventDao mockedEventDao) {
        EventSearchCriteria mockedEventSearchCriteria = mock(EventSearchCriteria.class);

        eventsInteractor.search(mockedEventSearchCriteria);

        verify(mockedEventDao, times(1)).search(mockedEventSearchCriteria);

    }

    public static class TestModule extends JukitoModule {

        @Override
        protected void configureTest() {
            forceMock(EventDao.class);
        }
    }
}
