package ca.ulaval.glo4003.unittests.controllers;

import ca.ulaval.glo4003.ConstantsManager;
import ca.ulaval.glo4003.api.event.EventsController;
import ca.ulaval.glo4003.domain.RecordNotFoundException;
import ca.ulaval.glo4003.domain.event.Event;
import ca.ulaval.glo4003.domain.event.EventSearchCriteria;
import ca.ulaval.glo4003.domain.event.EventsInteractor;
import ca.ulaval.glo4003.domain.event.Gender;
import ca.ulaval.glo4003.domain.ticketing.MaximumExceededException;
import ca.ulaval.glo4003.unittests.helpers.EventsTestHelper;
import com.google.inject.Inject;
import org.codehaus.jackson.JsonNode;
import org.joda.time.LocalDateTime;
import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import play.libs.Json;
import play.mvc.Result;
import play.test.Helpers;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JukitoRunner.class)
public class EventsControllerTest extends BaseControllerTest {

    private static final int FIRST_EVENT_ID = 1;
    private static final int SECOND_EVENT_ID = 2;

    @Inject
    private EventsController eventsController;
    private EventSearchCriteria eventSearchCriteria;
    private List<Event> tempEventsList;
    private Event firstEvent;

    @Before
    public void setup(EventsInteractor mockedEventsInteractor) throws RecordNotFoundException,
            MaximumExceededException {
        firstEvent = EventsTestHelper.createRandomEventWithCategoryGivenSport(EventsTestHelper.FIRST_RANDOM_SPORT);
        Event secondEvent = EventsTestHelper.createRandomEventWithCategoryGivenSport(EventsTestHelper.SECOND_RANDOM_SPORT);
        firstEvent.setId(FIRST_EVENT_ID);
        secondEvent.setId(SECOND_EVENT_ID);
        tempEventsList = Arrays.asList(firstEvent, secondEvent);

        eventSearchCriteria = new EventSearchCriteria();
        when(mockedEventsInteractor.search(refEq(eventSearchCriteria))).thenReturn(tempEventsList);
    }

    @Test
    public void indexReturnsAllEventsWhenNoParameters(EventsInteractor mockedEventsInteractor) {
        Result result = eventsController.index();

        assertEquals(Helpers.OK, Helpers.status(result));
        assertEquals("application/json", Helpers.contentType(result));

        String json = Helpers.contentAsString(result);
        JsonNode jsonNode = Json.parse(json);

        assertTrue(jsonNode.isArray());
        JsonNode eventDto1 = jsonNode.get(0);
        JsonNode eventDto2 = jsonNode.get(1);
        JsonNode event1 = eventDto1.get("event");
        JsonNode event2 = eventDto2.get("event");

        assertEquals(tempEventsList.size(), jsonNode.size());

        assertEquals(FIRST_EVENT_ID, event1.get("id").asLong());
        assertEquals(EventsTestHelper.FIRST_RANDOM_SPORT, event1.get("sport").get("name").asText());

        assertEquals(SECOND_EVENT_ID, event2.get("id").asLong());
        assertEquals(EventsTestHelper.SECOND_RANDOM_SPORT, event2.get("sport").get("name").asText());

        verify(mockedEventsInteractor).search(refEq(eventSearchCriteria));
    }

    @Test
    public void indexReturnsOnlyEventsCorrespondingToParameters(EventsInteractor mockedEventsInteractor) {
        String teamName = "Test team";
        LocalDateTime dateStart = new LocalDateTime(2013, 10, 19, 0, 0);
        LocalDateTime dateEnd = dateStart.plusMonths(1);
        List<Event> tempFilteredListEvent = Arrays.asList(firstEvent);
        eventSearchCriteria.setSportName(EventsTestHelper.FIRST_RANDOM_SPORT);
        eventSearchCriteria.setTeamName(teamName);
        eventSearchCriteria.setDateStart(dateStart);
        eventSearchCriteria.setDateEnd(dateEnd);
        eventSearchCriteria.setGender(Gender.MALE);
        when(mockedEventsInteractor.search(refEq(eventSearchCriteria))).thenReturn(tempFilteredListEvent);

        when(mockedRequest.getQueryString(ConstantsManager.QUERY_STRING_SPORT_PARAM_NAME)).thenReturn(EventsTestHelper.FIRST_RANDOM_SPORT);
        when(mockedRequest.getQueryString(ConstantsManager.QUERY_STRING_DATE_START_PARAM_NAME)).thenReturn(dateStart.toString());
        when(mockedRequest.getQueryString(ConstantsManager.QUERY_STRING_DATE_END_PARAM_NAME)).thenReturn(dateEnd.toString());
        when(mockedRequest.getQueryString(ConstantsManager.QUERY_STRING_TEAM_PARAM_NAME)).thenReturn(teamName);
        when(mockedRequest.getQueryString(ConstantsManager.QUERY_STRING_GENDER_PARAM_NAME)).thenReturn(Gender.MALE.toString());

        Result result = eventsController.index();

        assertEquals(Helpers.OK, Helpers.status(result));
        assertEquals("application/json", Helpers.contentType(result));

        String json = Helpers.contentAsString(result);
        JsonNode jsonNode = Json.parse(json);

        assertTrue(jsonNode.isArray());
        JsonNode eventDto1 = jsonNode.get(0);
        JsonNode event1 = eventDto1.get("event");

        assertEquals(tempFilteredListEvent.size(), jsonNode.size());

        assertEquals(FIRST_EVENT_ID, event1.get("id").asLong());
        assertEquals(EventsTestHelper.FIRST_RANDOM_SPORT, event1.get("sport").get("name").asText());

        verify(mockedEventsInteractor).search(refEq(eventSearchCriteria));
    }

    @Test
    public void indexThrowExceptionWithInvalidParameters(EventsInteractor mockedEventsInteractor) {
        LocalDateTime dateEnd = new LocalDateTime(2013, 10, 19, 0, 0);
        LocalDateTime dateStart = dateEnd.plusDays(1);
        eventSearchCriteria.setDateEnd(dateEnd);
        eventSearchCriteria.setDateStart(dateStart);
        when(mockedEventsInteractor.search(refEq(eventSearchCriteria))).thenThrow(new InvalidParameterException("Test"));

        when(mockedRequest.getQueryString(ConstantsManager.QUERY_STRING_DATE_START_PARAM_NAME)).thenReturn(dateStart.toString());
        when(mockedRequest.getQueryString(ConstantsManager.QUERY_STRING_DATE_END_PARAM_NAME)).thenReturn(dateEnd.toString());

        Result result = eventsController.index();

        assertEquals(Helpers.INTERNAL_SERVER_ERROR, Helpers.status(result));

        verify(mockedEventsInteractor).search(refEq(eventSearchCriteria));
    }

    @Test
    public void showReturnsAnEvent(EventsInteractor mockedEventsInteractor) throws RecordNotFoundException {
        when(mockedEventsInteractor.getById(firstEvent.getId())).thenReturn(firstEvent);

        Result result = eventsController.show(firstEvent.getId());
        assertEquals(Helpers.OK, Helpers.status(result));
        assertEquals("application/json", Helpers.contentType(result));

        String json = Helpers.contentAsString(result);
        JsonNode jsonNode = Json.parse(json);

        assertEquals(firstEvent.getId(), jsonNode.get("id").asLong());

        verify(mockedEventsInteractor).getById(firstEvent.getId());
    }

    @Test
    public void showReturnNotFoundWhenRecordNotFoundExceptionIsCatched(EventsInteractor mockedEventsInteractor) throws RecordNotFoundException {
        when(mockedEventsInteractor.getById(firstEvent.getId())).thenThrow(new RecordNotFoundException());

        Result result = eventsController.show(firstEvent.getId());
        assertEquals(Helpers.NOT_FOUND, Helpers.status(result));
        assertEquals(null, Helpers.contentType(result));

        verify(mockedEventsInteractor).getById(firstEvent.getId());
    }

    public static class TestModule extends JukitoModule {

        @Override
        protected void configureTest() {
            forceMock(EventsInteractor.class);
        }
    }
}
