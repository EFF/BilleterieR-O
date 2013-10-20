package ca.ulaval.glo4003.unittests.controllers;

import ca.ulaval.glo4003.controllers.Events;
import ca.ulaval.glo4003.dataaccessobjects.EventDao;
import ca.ulaval.glo4003.exceptions.MaximumExceededException;
import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.models.Event;
import ca.ulaval.glo4003.models.EventSearchCriteria;
import ca.ulaval.glo4003.models.Gender;
import ca.ulaval.glo4003.unittests.helpers.EventsTestHelper;
import com.google.inject.Inject;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import org.joda.time.LocalDateTime;
import org.jukito.JukitoRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import play.libs.Json;
import play.mvc.Result;
import play.test.Helpers;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.*;

@RunWith(JukitoRunner.class)
public class EventsTest extends BaseControllerTest {

    @Inject
    private Events events;

    private Event firstEvent;
    private Event secondEvent;
    private EventSearchCriteria eventSearchCriteria;
    private List<Event> tempEventsList;

    @Before
    public void setup(EventDao mockedEventDao) throws RecordNotFoundException, MaximumExceededException {
        firstEvent = EventsTestHelper.createRandomEventtWithCategoryGivenSport(EventsTestHelper.FIRST_RANDOM_SPORT);
        firstEvent.setId(1);
        secondEvent = EventsTestHelper.createRandomEventtWithCategoryGivenSport(EventsTestHelper.SECOND_RANDOM_SPORT);
        secondEvent.setId(2);
        tempEventsList = new ArrayList<>();
        tempEventsList.add(firstEvent);
        tempEventsList.add(secondEvent);

        eventSearchCriteria = new EventSearchCriteria();
        when(mockedEventDao.search(refEq(eventSearchCriteria))).thenReturn(tempEventsList);
        doThrow(new RecordNotFoundException()).when(mockedEventDao).decrementEventCategoryNumberOfTickets(anyLong(), anyLong(), anyInt());
    }

    @Test
    public void indexReturnsAllEventsWhenNoParameters(EventDao mockedEventDao) {
        Result result = events.index();

        assertEquals(Helpers.OK, Helpers.status(result));
        assertEquals("application/json", Helpers.contentType(result));

        String json = Helpers.contentAsString(result);
        JsonNode jsonNode = Json.parse(json);

        assertTrue(jsonNode.isArray());
        JsonNode event1 = jsonNode.get(0);
        JsonNode event2 = jsonNode.get(1);

        assertEquals(tempEventsList.size(), jsonNode.size());

        assertEquals(firstEvent.getId(), event1.get("id").asLong());
        assertEquals(firstEvent.getSport().getName(), event1.get("sport").get("name").asText());

        assertEquals(secondEvent.getId(), event2.get("id").asLong());
        assertEquals(secondEvent.getSport().getName(), event2.get("sport").get("name").asText());

        verify(mockedEventDao).search(refEq(eventSearchCriteria));
    }

    @Test
    public void indexReturnsOnlyEventsCorrespondingToParameters(EventDao mockedEventDao) {
        String teamName = "Test team";
        LocalDateTime dateStart = new LocalDateTime(2013, 10, 19, 0, 0);
        LocalDateTime dateEnd = dateStart.plusMonths(1);
        List<Event> tempFilteredListEvent = new ArrayList<>();
        tempFilteredListEvent.add(firstEvent);
        eventSearchCriteria.setSportName(firstEvent.getSport().getName());
        eventSearchCriteria.setTeamName(teamName);
        eventSearchCriteria.setDateStart(dateStart);
        eventSearchCriteria.setDateEnd(dateEnd);
        eventSearchCriteria.setGender(Gender.MALE);
        when(mockedEventDao.search(refEq(eventSearchCriteria))).thenReturn(tempFilteredListEvent);

        when(mockedRequest.getQueryString("sport")).thenReturn(firstEvent.getSport().getName());
        when(mockedRequest.getQueryString("dateStart")).thenReturn(dateStart.toString());
        when(mockedRequest.getQueryString("dateEnd")).thenReturn(dateEnd.toString());
        when(mockedRequest.getQueryString("team")).thenReturn(teamName);
        when(mockedRequest.getQueryString("gender")).thenReturn(Gender.MALE.toString());

        Result result = events.index();

        assertEquals(Helpers.OK, Helpers.status(result));
        assertEquals("application/json", Helpers.contentType(result));

        String json = Helpers.contentAsString(result);
        JsonNode jsonNode = Json.parse(json);

        assertTrue(jsonNode.isArray());
        JsonNode event1 = jsonNode.get(0);

        assertEquals(tempFilteredListEvent.size(), jsonNode.size());

        assertEquals(firstEvent.getId(), event1.get("id").asLong());
        assertEquals(firstEvent.getSport().getName(), event1.get("sport").get("name").asText());

        verify(mockedEventDao).search(refEq(eventSearchCriteria));
    }

    @Test
    public void indexThrowExceptionWithInvalidParameters(EventDao mockedEventDao) {
        LocalDateTime dateEnd = new LocalDateTime(2013, 10, 19, 0, 0);
        LocalDateTime dateStart = dateEnd.plusDays(1);
        eventSearchCriteria.setDateEnd(dateEnd);
        eventSearchCriteria.setDateStart(dateStart);
        when(mockedEventDao.search(refEq(eventSearchCriteria))).thenThrow(new InvalidParameterException("Test"));

        when(mockedRequest.getQueryString("dateStart")).thenReturn(dateStart.toString());
        when(mockedRequest.getQueryString("dateEnd")).thenReturn(dateEnd.toString());

        Result result = events.index();

        assertEquals(Helpers.INTERNAL_SERVER_ERROR, Helpers.status(result));

        verify(mockedEventDao).search(refEq(eventSearchCriteria));
    }

    @Test
    public void showReturnsAnEvent(EventDao mockedEventDao) throws RecordNotFoundException {
        when(mockedEventDao.read(firstEvent.getId())).thenReturn(firstEvent);

        Result result = events.show(firstEvent.getId());
        assertEquals(Helpers.OK, Helpers.status(result));
        assertEquals("application/json", Helpers.contentType(result));

        String json = Helpers.contentAsString(result);
        JsonNode jsonNode = Json.parse(json);

        assertEquals(firstEvent.getId(), jsonNode.get("id").asLong());

        verify(mockedEventDao).read(firstEvent.getId());
    }

    @Test
    public void showReturnNotFoundWhenRecordNotFoundExceptionIsCatched(EventDao mockedEventDao) throws RecordNotFoundException {
        when(mockedEventDao.read(firstEvent.getId())).thenThrow(new RecordNotFoundException());

        Result result = events.show(firstEvent.getId());
        assertEquals(Helpers.NOT_FOUND, Helpers.status(result));
        assertEquals(null, Helpers.contentType(result));

        verify(mockedEventDao).read(firstEvent.getId());
    }

    @Test
    public void decrementCategoryCounterDoNothingWhenThereIsNoItems(EventDao mockedEventDao) throws RecordNotFoundException, MaximumExceededException {
        Result result = events.decrementCategoryCounter();

        assertEquals(Helpers.OK, Helpers.status(result));
        verify(mockedEventDao, never()).decrementEventCategoryNumberOfTickets(anyLong(), anyLong(), anyInt());
    }

    @Test
    public void decrementCategoryCounterWithSeveralItems(EventDao mockedEventDao) throws RecordNotFoundException, MaximumExceededException {
        long eventId1 = 1;
        long categoryId1 = 1;
        int quantity1 = 10;
        long eventId2 = eventId1 + 1;
        long categoryId2 = categoryId1 + 1;
        int quantity2 = quantity1 * 2;

        ObjectNode jsonMaster = Json.newObject();
        ObjectNode json1 = Json.newObject();
        ObjectNode json2 = Json.newObject();
        json1.put("eventId", eventId1);
        json1.put("categoryId", categoryId1);
        json1.put("quantity", quantity1);
        jsonMaster.put("1", json1);
        doNothing().when(mockedEventDao).decrementEventCategoryNumberOfTickets(eventId1, categoryId1, quantity1);
        json2.put("eventId", eventId2);
        json2.put("categoryId", categoryId2);
        json2.put("quantity", quantity2);
        jsonMaster.put("2", json2);
        doNothing().when(mockedEventDao).decrementEventCategoryNumberOfTickets(eventId2, categoryId2, quantity2);

        when(mockedBody.asJson()).thenReturn(jsonMaster);

        Result result = events.decrementCategoryCounter();

        assertEquals(Helpers.OK, Helpers.status(result));
        verify(mockedEventDao, times(2)).decrementEventCategoryNumberOfTickets(anyLong(), anyLong(), anyInt());
        verify(mockedEventDao).decrementEventCategoryNumberOfTickets(eventId1, categoryId1, quantity1);
        verify(mockedEventDao).decrementEventCategoryNumberOfTickets(eventId2, categoryId2, quantity2);
    }

    @Test
    public void decrementCategoryCounterWhenMaximumIsExceeded(EventDao mockedEventDao) throws RecordNotFoundException, MaximumExceededException {
        long eventId1 = 1;
        long categoryId1 = 1;
        int quantity1 = 10;

        ObjectNode jsonMaster = Json.newObject();
        ObjectNode json1 = Json.newObject();
        json1.put("eventId", eventId1);
        json1.put("categoryId", categoryId1);
        json1.put("quantity", quantity1);
        jsonMaster.put("1", json1);
        doThrow(new MaximumExceededException()).when(mockedEventDao).decrementEventCategoryNumberOfTickets(eventId1, categoryId1, quantity1);

        when(mockedBody.asJson()).thenReturn(jsonMaster);

        Result result = events.decrementCategoryCounter();

        assertEquals(Helpers.INTERNAL_SERVER_ERROR, Helpers.status(result));
        verify(mockedEventDao).decrementEventCategoryNumberOfTickets(anyLong(), anyLong(), anyInt());
        verify(mockedEventDao).decrementEventCategoryNumberOfTickets(eventId1, categoryId1, quantity1);
    }

    @Test
    public void decrementCategoryCounterWhenRecordNotFoundExceptionIsThrown(EventDao mockedEventDao) throws RecordNotFoundException, MaximumExceededException {
        reset(mockedEventDao);
        long eventId1 = 1;
        long categoryId1 = 1;
        int quantity1 = 10;

        ObjectNode jsonMaster = Json.newObject();
        ObjectNode json1 = Json.newObject();
        json1.put("eventId", eventId1);
        json1.put("categoryId", categoryId1);
        json1.put("quantity", quantity1);
        jsonMaster.put("1", json1);
        doThrow(new RecordNotFoundException()).when(mockedEventDao).decrementEventCategoryNumberOfTickets(eventId1, categoryId1, quantity1);

        when(mockedBody.asJson()).thenReturn(jsonMaster);

        Result result = events.decrementCategoryCounter();

        assertEquals(Helpers.NOT_FOUND, Helpers.status(result));
        verify(mockedEventDao).decrementEventCategoryNumberOfTickets(anyLong(), anyLong(), anyInt());
        verify(mockedEventDao).decrementEventCategoryNumberOfTickets(eventId1, categoryId1, quantity1);
    }
}
