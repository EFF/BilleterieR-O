package ca.ulaval.glo4003.unittests.controllers;

import ca.ulaval.glo4003.ConstantsManager;
import ca.ulaval.glo4003.controllers.Tickets;
import ca.ulaval.glo4003.dataaccessobjects.EventDao;
import ca.ulaval.glo4003.dataaccessobjects.TicketDao;
import ca.ulaval.glo4003.exceptions.RecordNotFoundException;
import ca.ulaval.glo4003.models.*;
import com.google.inject.Inject;
import org.codehaus.jackson.JsonNode;
import org.jukito.JukitoModule;
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
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JukitoRunner.class)
public class TicketsTest extends BaseControllerTest {
    private static final String A_SECTION = "section 100";
    private static final long A_SEAT = 55;
    private static final long AN_EVENT_ID = 1;
    private static final long A_CATEGORY_ID = 1;
    private static final double TICKET_PRICE = 11.00;
    private static final int TICKET_NUMBER = 77;
    private Ticket firstTicket;
    private Ticket secondTicket;
    private TicketSearchCriteria ticketSearchCriteria;
    private List<Ticket> tempTicketsList;
    @Inject
    private Tickets tickets;

    @Before
    public void setup(TicketDao mockedTicketDao) {
        firstTicket = new Ticket(AN_EVENT_ID, A_CATEGORY_ID, A_SECTION, A_SEAT);
        secondTicket = new Ticket(AN_EVENT_ID, A_CATEGORY_ID, A_SECTION, A_SEAT + 1);
        tempTicketsList = new ArrayList<>();
        tempTicketsList.add(firstTicket);
        tempTicketsList.add(secondTicket);

        ticketSearchCriteria = new TicketSearchCriteria();
        when(mockedTicketDao.search(refEq(ticketSearchCriteria))).thenReturn(tempTicketsList);
    }

    @Test
    public void indexReturnsOnlyTicketCorrespondingToParameters(TicketDao mockedTicketDao) {
        ticketSearchCriteria.setEventId(AN_EVENT_ID);
        ticketSearchCriteria.setCategoryId(A_CATEGORY_ID);
        ticketSearchCriteria.addState(TicketState.AVAILABLE);
        List<Ticket> tempFilteredTicketList = new ArrayList<>();
        tempFilteredTicketList.add(firstTicket);
        when(mockedTicketDao.search(refEq(ticketSearchCriteria))).thenReturn(tempFilteredTicketList);

        when(mockedRequest.getQueryString(ConstantsManager.EVENT_ID_FIELD_NAME)).thenReturn(String.valueOf(AN_EVENT_ID));
        when(mockedRequest.getQueryString(ConstantsManager.CATEGORY_ID_FIELD_NAME)).thenReturn(String.valueOf(A_CATEGORY_ID));
        when(mockedRequest.getQueryString(ConstantsManager.TICKET_STATE_FIELD_NAME)).thenReturn(String.valueOf(TicketState.AVAILABLE));

        Result result = tickets.index();

        assertEquals(Helpers.OK, Helpers.status(result));
        assertEquals("application/json", Helpers.contentType(result));

        String json = Helpers.contentAsString(result);
        JsonNode jsonNode = Json.parse(json);

        assertTrue(jsonNode.isArray());
        JsonNode ticket1 = jsonNode.get(0);

        assertEquals(AN_EVENT_ID, ticket1.get("eventId").asLong());
        assertEquals(A_CATEGORY_ID, ticket1.get("categoryId").asLong());

        verify(mockedTicketDao).search(refEq(ticketSearchCriteria));
    }

    @Test
    public void indexThrowsExceptionWithInvalidParameters(TicketDao mockedTicketDao) {
        ticketSearchCriteria.setEventId(AN_EVENT_ID);
        ticketSearchCriteria.setCategoryId(A_CATEGORY_ID + 1);
        when(mockedTicketDao.search(refEq(ticketSearchCriteria))).thenThrow(new InvalidParameterException("Test"));

        when(mockedRequest.getQueryString(ConstantsManager.EVENT_ID_FIELD_NAME)).thenReturn(String.valueOf(AN_EVENT_ID));
        when(mockedRequest.getQueryString(ConstantsManager.CATEGORY_ID_FIELD_NAME)).thenReturn(String.valueOf(A_CATEGORY_ID + 1));

        Result result = tickets.index();

        assertEquals(Helpers.INTERNAL_SERVER_ERROR, Helpers.status(result));
        verify(mockedTicketDao).search(refEq(ticketSearchCriteria));
    }

    @Test
    public void showReturnsATicket(TicketDao mockedTicketDao) throws RecordNotFoundException {
        when(mockedTicketDao.read(firstTicket.getId())).thenReturn(firstTicket);

        Result result = tickets.show(firstTicket.getId());
        assertEquals(Helpers.OK, Helpers.status(result));
        assertEquals("application/json", Helpers.contentType(result));

        String json = Helpers.contentAsString(result);
        JsonNode jsonNode = Json.parse(json);

        assertEquals(firstTicket.getId(), jsonNode.get("id").asLong());
        verify(mockedTicketDao).read(firstTicket.getId());
    }

    @Test
    public void showReturnsNotFoundWhenRecordNotFound(TicketDao mockedTicketDao) throws RecordNotFoundException {
        when(mockedTicketDao.read(firstTicket.getId())).thenThrow(new RecordNotFoundException());

        Result result = tickets.show(firstTicket.getId());
        assertEquals(Helpers.NOT_FOUND, Helpers.status(result));
        assertEquals(null, Helpers.contentType(result));

        verify(mockedTicketDao).read(firstTicket.getId());
    }

    @Test
    public void checkoutTicketWhenItExists(TicketDao mockedTicketDao, EventDao mockedEventDao) throws RecordNotFoundException {
        when(mockedTicketDao.read(firstTicket.getId())).thenReturn(firstTicket);
        when(mockedEventDao.findCategory(firstTicket.getEventId(), firstTicket.getCategoryId()))
                .thenReturn(new Category(TICKET_PRICE, TICKET_NUMBER, A_CATEGORY_ID,
                        CategoryType.GENERAL_ADMISSION));
        Result result = tickets.checkout(String.valueOf(firstTicket.getId()));

        assertEquals(Helpers.OK, Helpers.status(result));
    }

    @Test
    public void freeTicketWhenIsCheckedOut(TicketDao mockedTicketDao, EventDao mockedEventDao) throws RecordNotFoundException {
        when(mockedTicketDao.read(firstTicket.getId())).thenReturn(firstTicket);
        when(mockedEventDao.findCategory(firstTicket.getEventId(), firstTicket.getCategoryId()))
                .thenReturn(new Category(TICKET_PRICE, TICKET_NUMBER, A_CATEGORY_ID,
                        CategoryType.GENERAL_ADMISSION));
        Result result = tickets.free(String.valueOf(firstTicket.getId()));
        assertEquals(Helpers.OK, Helpers.status(result));
        //verify(mockedEventDao).decrementEventCategoryNumberOfTickets(firstTicket.getEventId(),firstTicket.getCategoryId(),);
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

    public static class TestModule extends JukitoModule {

        @Override
        protected void configureTest() {
            forceMock(TicketDao.class);
            forceMock(EventDao.class);
        }
    }
}
