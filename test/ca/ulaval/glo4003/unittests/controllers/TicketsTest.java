package ca.ulaval.glo4003.unittests.controllers;

import ca.ulaval.glo4003.ConstantsManager;
import ca.ulaval.glo4003.controllers.Tickets;
import ca.ulaval.glo4003.dataaccessobjects.EventDao;
import ca.ulaval.glo4003.dataaccessobjects.TicketDao;
import ca.ulaval.glo4003.exceptions.MaximumExceededException;
import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.models.Event;
import ca.ulaval.glo4003.models.EventSearchCriteria;
import ca.ulaval.glo4003.models.Ticket;
import ca.ulaval.glo4003.unittests.helpers.EventsTestHelper;
import com.google.inject.Inject;
import org.codehaus.jackson.node.ObjectNode;
import org.jukito.JukitoRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import play.libs.Json;
import play.mvc.Result;
import play.test.Helpers;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.*;

@RunWith(JukitoRunner.class)
public class TicketsTest extends BaseControllerTest {
    private static final String A_SECTION = "section 100";
    private static final long A_SEAT = 55;
    private Ticket firstTicket;
    private Event firstEvent;

    @Inject
    private Tickets tickets;

    @Before
    public void setup(TicketDao mockedTicketDao) {
        firstEvent = EventsTestHelper.createRandomEventtWithCategoryGivenSport(EventsTestHelper.FIRST_RANDOM_SPORT);
        firstEvent.setId(1);
        firstTicket = new Ticket(firstEvent.getId(),firstEvent.getCategories().get(0).getId(),A_SECTION,A_SEAT);
    }

//    @Test
//    public void decrementCategoryCounterDoNothingWhenThereIsNoItems(EventDao mockedEventDao) throws RecordNotFoundException, MaximumExceededException {
//        doThrow(new RecordNotFoundException()).when(mockedEventDao).decrementEventCategoryNumberOfTickets(anyLong(), anyLong(), anyInt());
//
//        Result result = tickets.decrementCategory();
//
//        assertEquals(Helpers.OK, Helpers.status(result));
//        verify(mockedEventDao, never()).decrementEventCategoryNumberOfTickets(anyLong(), anyLong(), anyInt());
//    }

//@Test
//public void decrementCategoryCounterWithSeveralItems(EventDao mockedEventDao) throws RecordNotFoundException, MaximumExceededException {
//    long eventId1 = 1;
//    long categoryId1 = 1;
//    int quantity1 = 10;
//    long eventId2 = eventId1 + 1;
//    long categoryId2 = categoryId1 + 1;
//    int quantity2 = quantity1 * 2;
//
//    ObjectNode jsonMaster = Json.newObject();
//    ObjectNode json1 = Json.newObject();
//    ObjectNode json2 = Json.newObject();
//    json1.put(ConstantsManager.EVENT_ID_FIELD_NAME, eventId1);
//    json1.put(ConstantsManager.CATEGORY_ID_FIELD_NAME, categoryId1);
//    json1.put(ConstantsManager.QUANTITY_FIELD_NAME, quantity1);
//    jsonMaster.put("1", json1);
//    doNothing().when(mockedEventDao).decrementEventCategoryNumberOfTickets(eventId1, categoryId1, quantity1);
//    json2.put(ConstantsManager.EVENT_ID_FIELD_NAME, eventId2);
//    json2.put(ConstantsManager.CATEGORY_ID_FIELD_NAME, categoryId2);
//    json2.put(ConstantsManager.QUANTITY_FIELD_NAME, quantity2);
//    jsonMaster.put("2", json2);
//    doNothing().when(mockedEventDao).decrementEventCategoryNumberOfTickets(eventId2, categoryId2, quantity2);
//
//    when(mockedBody.asJson()).thenReturn(jsonMaster);
//
//    Result result = events.decrementCategoryCounter();
//
//    assertEquals(Helpers.OK, Helpers.status(result));
//    verify(mockedEventDao, times(2)).decrementEventCategoryNumberOfTickets(anyLong(), anyLong(), anyInt());
//    verify(mockedEventDao).decrementEventCategoryNumberOfTickets(eventId1, categoryId1, quantity1);
//    verify(mockedEventDao).decrementEventCategoryNumberOfTickets(eventId2, categoryId2, quantity2);
//}
//
//    @Test
//    public void decrementCategoryCounterWhenMaximumIsExceeded(EventDao mockedEventDao) throws RecordNotFoundException, MaximumExceededException {
//        long eventId1 = 1;
//        long categoryId1 = 1;
//        int quantity1 = 10;
//
//        ObjectNode jsonMaster = Json.newObject();
//        ObjectNode json1 = Json.newObject();
//        json1.put(ConstantsManager.EVENT_ID_FIELD_NAME, eventId1);
//        json1.put(ConstantsManager.CATEGORY_ID_FIELD_NAME, categoryId1);
//        json1.put(ConstantsManager.QUANTITY_FIELD_NAME, quantity1);
//        jsonMaster.put("1", json1);
//        doThrow(new MaximumExceededException()).when(mockedEventDao).decrementEventCategoryNumberOfTickets(eventId1, categoryId1, quantity1);
//
//        when(mockedBody.asJson()).thenReturn(jsonMaster);
//
//        Result result = events.decrementCategoryCounter();
//
//        assertEquals(Helpers.BAD_REQUEST, Helpers.status(result));
//        verify(mockedEventDao).decrementEventCategoryNumberOfTickets(anyLong(), anyLong(), anyInt());
//        verify(mockedEventDao).decrementEventCategoryNumberOfTickets(eventId1, categoryId1, quantity1);
//    }
//
//    @Test
//    public void decrementCategoryCounterWhenRecordNotFoundExceptionIsThrown(EventDao mockedEventDao) throws RecordNotFoundException, MaximumExceededException {
//        reset(mockedEventDao);
//        long eventId1 = 1;
//        long categoryId1 = 1;
//        int quantity1 = 10;
//
//        ObjectNode jsonMaster = Json.newObject();
//        ObjectNode json1 = Json.newObject();
//        json1.put(ConstantsManager.EVENT_ID_FIELD_NAME, eventId1);
//        json1.put(ConstantsManager.CATEGORY_ID_FIELD_NAME, categoryId1);
//        json1.put(ConstantsManager.QUANTITY_FIELD_NAME, quantity1);
//        jsonMaster.put("1", json1);
//        doThrow(new RecordNotFoundException()).when(mockedEventDao).decrementEventCategoryNumberOfTickets(eventId1, categoryId1, quantity1);
//
//        when(mockedBody.asJson()).thenReturn(jsonMaster);
//
//        Result result = events.decrementCategoryCounter();
//
//        assertEquals(Helpers.NOT_FOUND, Helpers.status(result));
//        verify(mockedEventDao).decrementEventCategoryNumberOfTickets(anyLong(), anyLong(), anyInt());
//        verify(mockedEventDao).decrementEventCategoryNumberOfTickets(eventId1, categoryId1, quantity1);
//    }
}
